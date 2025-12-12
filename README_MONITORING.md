# Spring Boot Admin 监控平台实现

本项目实现了基于 Spring Boot Admin 的服务监控平台，支持服务启动和停止时自动通知到飞书、钉钉和邮箱。

## 目录

- [功能特性](#功能特性)
- [技术架构](#技术架构)
- [快速开始](#快速开始)
- [配置说明](#配置说明)
- [测试指南](#测试指南)
- [演示视频](#演示视频)
- [常见问题](#常见问题)

## 功能特性

### ✅ 已实现功能

1. **Spring Boot Admin Server**: 提供可视化监控面板
2. **服务注册与监控**: 自动注册服务实例并实时监控状态
3. **飞书通知**: 服务启动/停止时发送通知到飞书群
4. **钉钉通知**: 服务启动/停止时发送通知到钉钉群
5. **邮件通知**: 服务启动/停止时发送邮件通知
6. **自定义通知器**: 可扩展的通知机制，支持添加更多通知渠道

### 📊 监控面板功能

访问 `http://localhost:8080/admin` 可查看:

- 应用列表和状态
- 详细的健康检查信息
- JVM 内存使用情况
- 线程信息
- 日志级别管理
- 环境属性
- HTTP 请求跟踪
- 指标监控

## 技术架构

### 核心组件

```
┌─────────────────────────────────────────────────────┐
│                  Spring Boot Application            │
│                                                      │
│  ┌────────────────────────────────────────────┐   │
│  │      Spring Boot Admin Server              │   │
│  │  - 监控面板                                 │   │
│  │  - 实例管理                                 │   │
│  │  - 事件系统                                 │   │
│  └────────────────────────────────────────────┘   │
│                        │                            │
│  ┌────────────────────────────────────────────┐   │
│  │      Spring Boot Admin Client              │   │
│  │  - 自动注册                                 │   │
│  │  - 状态报告                                 │   │
│  └────────────────────────────────────────────┘   │
│                        │                            │
│  ┌────────────────────────────────────────────┐   │
│  │         Notification System                 │   │
│  │                                              │   │
│  │  ┌──────────────────────────────────────┐ │   │
│  │  │  FeishuNotifier (飞书)               │ │   │
│  │  │  - 监听状态变更事件                  │ │   │
│  │  │  - 构建通知消息                       │ │   │
│  │  │  - 调用飞书 Webhook API              │ │   │
│  │  └──────────────────────────────────────┘ │   │
│  │                                              │   │
│  │  ┌──────────────────────────────────────┐ │   │
│  │  │  DingTalkNotifier (钉钉)             │ │   │
│  │  │  - 监听状态变更事件                  │ │   │
│  │  │  - 构建通知消息                       │ │   │
│  │  │  - 调用钉钉 Webhook API              │ │   │
│  │  └──────────────────────────────────────┘ │   │
│  │                                              │   │
│  │  ┌──────────────────────────────────────┐ │   │
│  │  │  EmailNotifier (邮件)                │ │   │
│  │  │  - 监听状态变更事件                  │ │   │
│  │  │  - 构建通知消息                       │ │   │
│  │  │  - 发送邮件                           │ │   │
│  │  └──────────────────────────────────────┘ │   │
│  └────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────┘
```

### 依赖组件

- **Spring Boot 3.5.0**: 应用框架
- **Spring Boot Admin 3.3.5**: 监控服务器和客户端
- **Spring Boot Actuator**: 端点暴露和监控指标
- **Spring Boot WebFlux**: 响应式 Web 客户端
- **Spring Boot Mail**: 邮件发送支持

## 快速开始

### 前置条件

- JDK 17+
- Maven 3.6+
- MySQL 8.0+ (可选，用于其他业务功能)

### 1. 克隆并构建项目

```bash
git clone <repository-url>
cd skyai
mvn clean package -DskipTests
```

### 2. 配置通知渠道

选择一个或多个通知渠道进行配置：

#### 方式 A: 使用环境变量（推荐）

```bash
# 飞书通知
export FEISHU_WEBHOOK_URL=https://open.feishu.cn/open-apis/bot/v2/hook/YOUR_TOKEN

# 钉钉通知
export DINGTALK_WEBHOOK_URL=https://oapi.dingtalk.com/robot/send?access_token=YOUR_TOKEN

# 邮件通知
export NOTIFICATION_EMAIL_TO=recipient@example.com
export NOTIFICATION_EMAIL_FROM=noreply@example.com
export MAIL_HOST=smtp.gmail.com
export MAIL_PORT=587
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password
```

#### 方式 B: 修改配置文件

编辑 `src/main/resources/application.yaml`:

```yaml
spring:
  boot:
    admin:
      notify:
        feishu:
          enabled: true
          webhook-url: https://open.feishu.cn/open-apis/bot/v2/hook/YOUR_TOKEN
        dingtalk:
          enabled: true
          webhook-url: https://oapi.dingtalk.com/robot/send?access_token=YOUR_TOKEN
        mail:
          enabled: true
          to: recipient@example.com
          from: noreply@example.com
```

### 3. 启动应用

```bash
java -jar target/sky-ai-0.0.1-SNAPSHOT.jar
```

或使用 Maven:

```bash
mvn spring-boot:run
```

### 4. 访问监控面板

打开浏览器访问: http://localhost:8080/admin

## 配置说明

### 飞书 Webhook 配置

1. 在飞书群中点击 **设置** → **群机器人** → **添加机器人**
2. 选择 **自定义机器人**
3. 配置机器人名称和描述
4. 复制生成的 Webhook URL
5. 将 URL 配置到应用中

**消息格式示例:**

```
【服务状态变更通知】

服务名称: sky-ai
服务地址: http://localhost:8080
状态变更: UP

✅ 服务已启动
```

### 钉钉 Webhook 配置

1. 在钉钉群中点击 **群设置** → **智能群助手** → **添加机器人**
2. 选择 **自定义机器人**
3. 配置机器人名称和安全设置（建议使用关键词）
4. 复制生成的 Webhook URL
5. 将 URL 配置到应用中

**消息格式示例:**

```markdown
### 服务状态变更通知

**服务名称**: sky-ai

**服务地址**: http://localhost:8080

**状态变更**: UP

✅ **服务已启动**
```

### 邮件通知配置

支持主流邮件服务商：

#### Gmail 配置示例

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password  # 使用应用专用密码
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

#### QQ 邮箱配置示例

```yaml
spring:
  mail:
    host: smtp.qq.com
    port: 587
    username: your-email@qq.com
    password: your-authorization-code  # 使用授权码
```

#### 163 邮箱配置示例

```yaml
spring:
  mail:
    host: smtp.163.com
    port: 465
    username: your-email@163.com
    password: your-authorization-code
    properties:
      mail:
        smtp:
          auth: true
          ssl:
            enable: true
```

## 测试指南

### 方式 1: 使用提供的测试脚本

```bash
./test-monitoring.sh
```

脚本会：
1. 构建应用
2. 启动应用（触发 UP 通知）
3. 按 Ctrl+C 停止应用（触发 DOWN 通知）

### 方式 2: 手动测试

1. **启动服务测试**:
   ```bash
   mvn spring-boot:run
   ```
   - 观察应用启动日志
   - 检查飞书/钉钉/邮箱是否收到"服务已启动"通知
   - 访问 http://localhost:8080/admin 查看服务状态

2. **停止服务测试**:
   - 按 Ctrl+C 停止应用
   - 检查飞书/钉钉/邮箱是否收到"服务已停止"通知

3. **多实例测试**:
   ```bash
   # 终端 1: 启动 Admin Server
   mvn spring-boot:run
   
   # 终端 2: 启动另一个实例（修改端口）
   mvn spring-boot:run -Dserver.port=8081
   ```

### 方式 3: 使用 Docker（可选）

如果想测试容器化场景：

```bash
# 构建 Docker 镜像
docker build -t skyai-monitor .

# 启动容器（传递环境变量）
docker run -p 8080:8080 \
  -e FEISHU_WEBHOOK_URL=your-webhook-url \
  -e DINGTALK_WEBHOOK_URL=your-webhook-url \
  skyai-monitor
```

### 验证清单

- [ ] 服务启动后，Admin 面板显示服务为 UP 状态
- [ ] 飞书群收到服务启动通知（如已配置）
- [ ] 钉钉群收到服务启动通知（如已配置）
- [ ] 邮箱收到服务启动通知（如已配置）
- [ ] 服务停止后，通知渠道收到服务停止通知
- [ ] Admin 面板可以查看服务详细信息

## 演示视频

建议录制以下内容的演示视频：

1. **项目介绍** (30秒)
   - 项目背景和目标
   - 技术架构简介

2. **配置展示** (1分钟)
   - 展示 application.yaml 配置
   - 展示通知渠道配置（webhook URL 可打码）

3. **功能演示** (3-4分钟)
   - 启动应用
   - 展示 Admin 监控面板
   - 展示服务列表和详细信息
   - 展示飞书/钉钉收到的启动通知
   - 停止应用
   - 展示飞书/钉钉收到的停止通知

4. **代码走查** (2-3分钟)
   - 展示 Notifier 核心代码
   - 解释通知实现原理

## 常见问题

### Q1: 为什么没有收到通知？

**A1**: 检查以下几点：
1. 确认 `enabled` 设置为 `true`
2. 确认 webhook URL 配置正确
3. 查看应用日志，搜索 "notification" 关键字
4. 测试 webhook URL 是否可访问

```bash
# 测试飞书 webhook
curl -X POST 'YOUR_FEISHU_WEBHOOK_URL' \
  -H 'Content-Type: application/json' \
  -d '{"msg_type":"text","content":{"text":"测试消息"}}'
```

### Q2: Admin 面板无法访问？

**A2**: 
- 确认应用已启动
- 检查端口 8080 是否被占用
- 确认访问地址: http://localhost:8080/admin （注意 /admin 路径）

### Q3: 服务未注册到 Admin Server？

**A3**:
- 检查 `spring.boot.admin.client.url` 配置
- 确认 Actuator 端点已暴露
- 查看启动日志中的注册信息

### Q4: 邮件发送失败？

**A4**:
- 检查邮件服务器配置
- 确认使用应用专用密码/授权码
- 检查防火墙和网络连接
- 查看详细错误日志

### Q5: 如何添加更多通知渠道？

**A5**: 继承 `AbstractEventNotifier` 类：

```java
@Component
public class WeChatNotifier extends AbstractEventNotifier {
    
    public WeChatNotifier(InstanceRepository repository) {
        super(repository);
    }
    
    @Override
    protected Mono<Void> doNotify(InstanceEvent event, Instance instance) {
        // 实现企业微信通知逻辑
        return Mono.empty();
    }
}
```

## 项目文件说明

```
skyai/
├── src/main/java/com/sky/skyai/
│   ├── SkyAiApplication.java              # 主应用类，启用 Admin Server
│   ├── notification/
│   │   ├── FeishuNotifier.java            # 飞书通知器
│   │   ├── DingTalkNotifier.java          # 钉钉通知器
│   │   └── EmailNotifier.java             # 邮件通知器
│   └── config/
│       └── NotificationConfiguration.java  # 通知配置类
├── src/main/resources/
│   └── application.yaml                    # 应用配置文件
├── MONITORING_SETUP.md                     # 详细配置文档
├── README_MONITORING.md                    # 本文件
├── demo-config.yaml                        # 演示配置模板
└── test-monitoring.sh                      # 测试脚本
```

## 技术亮点

1. **完整的监控方案**: 集成 Spring Boot Admin 提供完整的服务监控
2. **多通道通知**: 支持飞书、钉钉、邮件三种主流通知方式
3. **可扩展架构**: 基于事件驱动，易于添加新的通知渠道
4. **配置灵活**: 支持环境变量和配置文件两种配置方式
5. **生产就绪**: 包含完整的错误处理和日志记录

## 参考资料

- [Spring Boot Admin 官方文档](https://codecentric.github.io/spring-boot-admin/current/)
- [Spring Boot Actuator 文档](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [飞书开放平台 - 自定义机器人](https://open.feishu.cn/document/ukTMukTMukTM/ucTM5YjL3ETO24yNxkjN)
- [钉钉开放平台 - 自定义机器人](https://open.dingtalk.com/document/robots/custom-robot-access)

## 联系方式

如有问题或建议，请提交 Issue 或 Pull Request。
