package com.sky.skyai.notification;

import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.events.InstanceEvent;
import de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent;
import de.codecentric.boot.admin.server.notify.AbstractEventNotifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * DingTalk (钉钉) notifier for Spring Boot Admin
 * Sends notifications to DingTalk webhook when service status changes
 */
@Slf4j
@Component
public class DingTalkNotifier extends AbstractEventNotifier {

    @Value("${spring.boot.admin.notify.dingtalk.webhook-url:}")
    private String webhookUrl;

    @Value("${spring.boot.admin.notify.dingtalk.enabled:false}")
    private boolean enabled;

    private final RestTemplate restTemplate;

    public DingTalkNotifier(InstanceRepository repository, RestTemplate restTemplate) {
        super(repository);
        this.restTemplate = restTemplate;
    }

    @Override
    protected Mono<Void> doNotify(InstanceEvent event, Instance instance) {
        return Mono.fromRunnable(() -> {
            if (!enabled || webhookUrl == null || webhookUrl.isEmpty()) {
                log.debug("DingTalk notification is disabled or webhook URL is not configured");
                return;
            }

            if (event instanceof InstanceStatusChangedEvent) {
                InstanceStatusChangedEvent statusEvent = (InstanceStatusChangedEvent) event;
                String status = statusEvent.getStatusInfo().getStatus();
                String serviceName = instance.getRegistration().getName();
                String serviceUrl = instance.getRegistration().getServiceUrl();

                String message = buildMessage(serviceName, status, serviceUrl);
                sendToDingTalk(message);
            }
        });
    }

    private String buildMessage(String serviceName, String status, String serviceUrl) {
        StringBuilder message = new StringBuilder();
        message.append("### 服务状态变更通知\n\n");
        message.append("**服务名称**: ").append(serviceName).append("\n\n");
        message.append("**服务地址**: ").append(serviceUrl).append("\n\n");
        message.append("**状态变更**: ").append(status).append("\n\n");
        
        if ("UP".equals(status)) {
            message.append("✅ **服务已启动**");
        } else if ("DOWN".equals(status) || "OFFLINE".equals(status)) {
            message.append("❌ **服务已停止**");
        }
        
        return message.toString();
    }

    private void sendToDingTalk(String message) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("msgtype", "markdown");
            
            Map<String, String> markdown = new HashMap<>();
            markdown.put("title", "服务状态变更");
            markdown.put("text", message);
            body.put("markdown", markdown);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(webhookUrl, entity, String.class);
            
            log.info("DingTalk notification sent successfully: {}", message);
        } catch (Exception e) {
            log.error("Failed to send DingTalk notification", e);
        }
    }
}
