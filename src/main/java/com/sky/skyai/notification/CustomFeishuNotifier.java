package com.sky.skyai.notification;

import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.events.InstanceEvent;
import de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent;
import de.codecentric.boot.admin.server.notify.AbstractEventNotifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom Feishu (飞书) notifier for Spring Boot Admin
 * Sends notifications to Feishu webhook when service status changes
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "spring.boot.admin.notify.custom-feishu", name = "enabled", havingValue = "true")
public class CustomFeishuNotifier extends AbstractEventNotifier {

    @Value("${spring.boot.admin.notify.custom-feishu.webhook-url:}")
    private String webhookUrl;

    private final RestTemplate restTemplate;

    public CustomFeishuNotifier(InstanceRepository repository, RestTemplate restTemplate) {
        super(repository);
        this.restTemplate = restTemplate;
    }

    @Override
    protected Mono<Void> doNotify(InstanceEvent event, Instance instance) {
        return Mono.fromRunnable(() -> {
            if (webhookUrl == null || webhookUrl.isEmpty()) {
                log.warn("Feishu webhook URL is not configured");
                return;
            }

            if (event instanceof InstanceStatusChangedEvent) {
                InstanceStatusChangedEvent statusEvent = (InstanceStatusChangedEvent) event;
                String status = statusEvent.getStatusInfo().getStatus();
                String serviceName = instance.getRegistration().getName();
                String serviceUrl = instance.getRegistration().getServiceUrl();

                String message = buildMessage(serviceName, status, serviceUrl);
                sendToFeishu(message);
            }
        });
    }

    private String buildMessage(String serviceName, String status, String serviceUrl) {
        StringBuilder message = new StringBuilder();
        message.append("【服务状态变更通知】\n\n");
        message.append("服务名称: ").append(serviceName).append("\n");
        message.append("服务地址: ").append(serviceUrl).append("\n");
        message.append("状态变更: ").append(status).append("\n");
        
        if ("UP".equals(status)) {
            message.append("\n✅ 服务已启动");
        } else if ("DOWN".equals(status) || "OFFLINE".equals(status)) {
            message.append("\n❌ 服务已停止");
        }
        
        return message.toString();
    }

    private void sendToFeishu(String message) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("msg_type", "text");
            
            Map<String, String> content = new HashMap<>();
            content.put("text", message);
            body.put("content", content);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            var response = restTemplate.postForEntity(webhookUrl, entity, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Feishu notification sent successfully: {}", message);
            } else {
                log.warn("Feishu notification may have failed. Status: {}, Response: {}", 
                        response.getStatusCode(), response.getBody());
            }
        } catch (Exception e) {
            log.error("Failed to send Feishu notification", e);
        }
    }
}
