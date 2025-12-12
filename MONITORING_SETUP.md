# Spring Boot Admin 监控平台使用说明

本项目已集成 Spring Boot Admin 监控平台，支持服务启动和停止通知到飞书、钉钉和邮箱。

## 功能特性

1. **服务监控**: 实时监控服务的运行状态
2. **多渠道通知**: 支持飞书、钉钉、邮箱三种通知方式
3. **状态变更通知**: 服务启动或停止时自动发送通知

## 快速开始

### 1. 启动应用

```bash
mvn spring-boot:run
```

### 2. 访问监控面板

启动后访问: http://localhost:8080/admin

在监控面板中可以看到:
- 应用列表
- 应用详细信息
- 健康状态
- 日志
- JVM 信息
- 等等

## 配置通知

### 飞书 (Feishu) 通知配置

1. 在飞书群中创建自定义机器人，获取 Webhook URL
2. 在 `application.yaml` 中配置或设置环境变量:

```yaml
spring:
  boot:
    admin:
      notify:
        feishu:
          enabled: true
          webhook-url: https://open.feishu.cn/open-apis/bot/v2/hook/your-webhook-token
```

或者设置环境变量:
```bash
export FEISHU_WEBHOOK_URL=https://open.feishu.cn/open-apis/bot/v2/hook/your-webhook-token
```

然后在 `application.yaml` 中启用:
```yaml
spring:
  boot:
    admin:
      notify:
        feishu:
          enabled: true
```

### 钉钉 (DingTalk) 通知配置

1. 在钉钉群中创建自定义机器人，获取 Webhook URL
2. 在 `application.yaml` 中配置或设置环境变量:

```yaml
spring:
  boot:
    admin:
      notify:
        dingtalk:
          enabled: true
          webhook-url: https://oapi.dingtalk.com/robot/send?access_token=your-token
```

或者设置环境变量:
```bash
export DINGTALK_WEBHOOK_URL=https://oapi.dingtalk.com/robot/send?access_token=your-token
```

然后在 `application.yaml` 中启用:
```yaml
spring:
  boot:
    admin:
      notify:
        dingtalk:
          enabled: true
```

### 邮箱 (Email) 通知配置

在 `application.yaml` 中配置邮件服务器和收件人:

```yaml
spring:
  boot:
    admin:
      notify:
        mail:
          enabled: true
          to: recipient@example.com
          from: noreply@example.com
  mail:
    host: smtp.example.com
    port: 587
    username: your-email@example.com
    password: your-password
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

或者使用环境变量:
```bash
export NOTIFICATION_EMAIL_TO=recipient@example.com
export NOTIFICATION_EMAIL_FROM=noreply@example.com
export MAIL_HOST=smtp.example.com
export MAIL_PORT=587
export MAIL_USERNAME=your-email@example.com
export MAIL_PASSWORD=your-password
```

## 测试通知

### 方法 1: 重启应用

1. 启动应用
2. 停止应用
3. 再次启动应用

每次状态变更都会触发通知。

### 方法 2: 使用多个应用实例

1. 启动主应用（作为 Admin Server）
2. 启动另一个应用实例并配置为 Admin Client
3. 停止客户端应用

这样可以更真实地模拟服务监控场景。

## 通知消息格式

### 服务启动通知
```
【服务状态变更通知】

服务名称: sky-ai
服务地址: http://localhost:8080
状态变更: UP

✅ 服务已启动
```

### 服务停止通知
```
【服务状态变更通知】

服务名称: sky-ai
服务地址: http://localhost:8080
状态变更: DOWN

❌ 服务已停止
```

## 架构说明

### 组件介绍

1. **Spring Boot Admin Server**: 提供监控面板和管理功能
2. **Spring Boot Admin Client**: 注册到 Server 并报告状态
3. **Notifiers**: 通知器（混合使用自定义和内置）
   - `CustomFeishuNotifier`: 自定义飞书通知器
   - Built-in `DingTalkNotifier`: 内置钉钉通知器
   - Built-in `MailNotifier`: 内置邮件通知器

### 工作流程

1. 应用启动时，Admin Client 向 Admin Server 注册
2. Admin Server 检测到新的实例或状态变更
3. 触发相应的 Notifier
4. Notifier 根据配置发送通知到对应平台

## 故障排查

### 1. 通知未发送

- 检查 `enabled` 是否设置为 `true`
- 检查 webhook URL 或邮件配置是否正确
- 查看应用日志中的错误信息

### 2. 监控面板无法访问

- 确认应用已启动
- 检查端口 8080 是否被占用
- 访问 http://localhost:8080/admin

### 3. 服务未注册到监控面板

- 确认 `spring.boot.admin.client.url` 配置正确
- 检查 Actuator 端点是否暴露
- 查看日志中的注册信息

## 扩展功能

可以通过继承 `AbstractEventNotifier` 类来实现更多通知渠道:

```java
@Component
public class CustomNotifier extends AbstractEventNotifier {
    
    public CustomNotifier(InstanceRepository repository) {
        super(repository);
    }
    
    @Override
    protected Mono<Void> doNotify(InstanceEvent event, Instance instance) {
        // 实现自定义通知逻辑
        return Mono.empty();
    }
}
```

## 相关链接

- [Spring Boot Admin 官方文档](https://github.com/codecentric/spring-boot-admin)
- [飞书机器人文档](https://open.feishu.cn/document/ukTMukTMukTM/ucTM5YjL3ETO24yNxkjN)
- [钉钉机器人文档](https://open.dingtalk.com/document/robots/custom-robot-access)
