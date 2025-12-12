package com.sky.skyai.notification;

import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.events.InstanceEvent;
import de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent;
import de.codecentric.boot.admin.server.notify.AbstractEventNotifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Email notifier for Spring Boot Admin
 * Sends email notifications when service status changes
 */
@Slf4j
@Component
public class EmailNotifier extends AbstractEventNotifier {

    @Value("${spring.boot.admin.notify.mail.to:}")
    private String toEmail;

    @Value("${spring.boot.admin.notify.mail.from:}")
    private String fromEmail;

    @Value("${spring.boot.admin.notify.mail.enabled:false}")
    private boolean enabled;

    private final JavaMailSender mailSender;

    public EmailNotifier(InstanceRepository repository, JavaMailSender mailSender) {
        super(repository);
        this.mailSender = mailSender;
    }

    @Override
    protected Mono<Void> doNotify(InstanceEvent event, Instance instance) {
        return Mono.fromRunnable(() -> {
            if (!enabled || toEmail == null || toEmail.isEmpty()) {
                log.debug("Email notification is disabled or recipient email is not configured");
                return;
            }

            if (event instanceof InstanceStatusChangedEvent) {
                InstanceStatusChangedEvent statusEvent = (InstanceStatusChangedEvent) event;
                String status = statusEvent.getStatusInfo().getStatus();
                String serviceName = instance.getRegistration().getName();
                String serviceUrl = instance.getRegistration().getServiceUrl();

                sendEmail(serviceName, status, serviceUrl);
            }
        });
    }

    private void sendEmail(String serviceName, String status, String serviceUrl) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            
            String subject = String.format("【服务监控】%s - 状态: %s", serviceName, status);
            message.setSubject(subject);
            
            StringBuilder content = new StringBuilder();
            content.append("服务状态变更通知\n\n");
            content.append("服务名称: ").append(serviceName).append("\n");
            content.append("服务地址: ").append(serviceUrl).append("\n");
            content.append("状态变更: ").append(status).append("\n\n");
            
            if ("UP".equals(status)) {
                content.append("✅ 服务已启动\n");
            } else if ("DOWN".equals(status) || "OFFLINE".equals(status)) {
                content.append("❌ 服务已停止\n");
            }
            
            content.append("\n此邮件由Spring Boot Admin监控平台自动发送。");
            message.setText(content.toString());
            
            mailSender.send(message);
            log.info("Email notification sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send email notification", e);
        }
    }
}
