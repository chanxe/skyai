# 演示视频录制指南

本指南帮助您录制一个完整的功能演示视频。

## 视频结构建议

### 总时长: 5-8 分钟

---

## 第一部分: 项目介绍 (1分钟)

### 内容要点

1. **任务背景**
   - "这是一个基于 Spring Boot Admin 的监控平台实现"
   - "要求实现服务启动和停止时的多渠道通知功能"

2. **技术选型**
   - Spring Boot 3.5.0
   - Spring Boot Admin 3.3.5
   - 支持飞书、钉钉、邮箱三种通知方式

3. **核心功能**
   - 实时监控服务状态
   - 自动发送启动/停止通知
   - 可视化监控面板

---

## 第二部分: 项目结构展示 (1分钟)

### 展示项目文件

```bash
# 显示项目结构
tree -L 2 src/

# 或者用 ls
ls -la src/main/java/com/sky/skyai/
```

### 重点展示的文件

1. **SkyAiApplication.java**
   ```bash
   cat src/main/java/com/sky/skyai/SkyAiApplication.java
   ```
   - 指出 `@EnableAdminServer` 注解

2. **CustomFeishuNotifier.java**
   ```bash
   cat src/main/java/com/sky/skyai/notification/CustomFeishuNotifier.java
   ```
   - 说明通知器的工作原理

3. **application.yaml**
   ```bash
   cat src/main/resources/application.yaml | grep -A 20 "boot:"
   ```
   - 展示配置结构

---

## 第三部分: 配置演示 (1-2分钟)

### 1. 展示配置文件

打开 `application.yaml`，展示：

```yaml
spring:
  boot:
    admin:
      context-path: /admin
      notify:
        custom-feishu:
          enabled: true  # 指出如何启用
          webhook-url: ${FEISHU_WEBHOOK_URL}
        dingtalk:
          enabled: true
          webhook-url: ${DINGTALK_WEBHOOK_URL}
```

### 2. 设置环境变量（可以打码）

```bash
# 展示如何配置（URL可以部分打码）
export FEISHU_WEBHOOK_URL=https://open.feishu.cn/open-apis/bot/v2/hook/xxxxx
export DINGTALK_WEBHOOK_URL=https://oapi.dingtalk.com/robot/send?access_token=xxxxx
```

### 3. 说明配置方式

- "可以通过环境变量配置"
- "也可以直接在配置文件中配置"
- "每个通知渠道都可以独立启用或禁用"

---

## 第四部分: 构建和启动 (1分钟)

### 1. 构建项目

```bash
# 展示构建过程
mvn clean package -DskipTests

# 展示构建结果
ls -lh target/sky-ai-*.jar
```

说明: "构建生成了一个 56MB 的可执行 JAR 文件"

### 2. 启动应用

```bash
# 启动应用
java -jar target/sky-ai-0.0.1-SNAPSHOT.jar
```

**重点关注**:
- 控制台输出中的 "Spring Boot Admin Server" 启动信息
- "Registered instance" 日志
- 端口 8080 监听信息

---

## 第五部分: 监控面板演示 (2分钟)

### 1. 打开浏览器

访问: `http://localhost:8080/admin`

### 2. 展示主界面

- **应用列表**
  - 指出已注册的 "sky-ai" 应用
  - 状态显示为 "UP"（绿色）

### 3. 点击应用详情

依次展示以下标签页：

1. **Details (详情)**
   - 应用名称
   - 服务地址
   - 状态信息

2. **Health (健康检查)**
   - 显示各个组件的健康状态
   - 数据库、磁盘空间等

3. **Metrics (指标)**
   - JVM 内存使用
   - 系统 CPU 使用率
   - HTTP 请求统计

4. **Environment (环境)**
   - 系统属性
   - 环境变量

5. **Loggers (日志)**
   - 展示可以动态调整日志级别

### 4. 说明监控功能

"通过这个面板，我们可以：
- 实时查看服务状态
- 监控系统资源使用情况
- 查看和管理应用配置
- 动态调整日志级别"

---

## 第六部分: 通知功能演示 (2-3分钟)

### 1. 展示启动通知

**在启动应用后**:

1. **展示飞书群**
   - 切换到飞书应用
   - 展示收到的启动通知消息
   - 指出消息内容：
     ```
     【服务状态变更通知】
     服务名称: sky-ai
     服务地址: http://localhost:8080
     状态变更: UP
     ✅ 服务已启动
     ```

2. **展示钉钉群**
   - 切换到钉钉应用
   - 展示收到的启动通知消息

3. **展示邮箱**（如已配置）
   - 打开邮箱
   - 展示收到的启动通知邮件

### 2. 演示停止通知

```bash
# 在终端中按 Ctrl+C 停止应用
# 或者在新终端执行
pkill -f sky-ai
```

**展示停止通知**:

1. 在监控面板中，观察状态变为 "OFFLINE"（灰色）
2. 切换到飞书，展示停止通知
3. 切换到钉钉，展示停止通知

### 3. 说明通知机制

"当服务状态发生变化时：
1. Spring Boot Admin 检测到状态变更事件
2. 触发配置的通知器
3. 通知器调用相应平台的 Webhook API
4. 消息发送到配置的群聊或邮箱"

---

## 第七部分: 代码走查 (1-2分钟)

### 1. 展示关键代码

**CustomFeishuNotifier.java**:

```bash
# 用编辑器打开
vim src/main/java/com/sky/skyai/notification/CustomFeishuNotifier.java
# 或 code .
```

指出关键部分：

1. **继承 AbstractEventNotifier**
   ```java
   public class CustomFeishuNotifier extends AbstractEventNotifier
   ```

2. **处理状态变更事件**
   ```java
   protected Mono<Void> doNotify(InstanceEvent event, Instance instance)
   ```

3. **发送通知到飞书**
   ```java
   private void sendToFeishu(String message)
   ```

### 2. 解释实现原理

"这个实现利用了 Spring Boot Admin 的事件系统：
- 监听实例状态变更事件
- 解析事件信息（服务名、状态、地址）
- 构建通知消息
- 通过 HTTP POST 发送到 Webhook"

---

## 第八部分: 扩展性说明 (30秒)

### 展示如何扩展

```bash
# 展示文档
cat IMPLEMENTATION_SUMMARY.md | grep -A 10 "扩展性"
```

说明:
- "可以轻松添加其他通知渠道"
- "例如企业微信、Slack、自定义 HTTP 接口等"
- "只需实现 AbstractEventNotifier 接口"

---

## 第九部分: 总结 (30秒)

### 总结要点

1. **完成度**
   - ✅ 实现了所有要求的功能
   - ✅ 支持三种通知渠道
   - ✅ 提供完整的监控界面

2. **技术亮点**
   - 使用企业级监控框架
   - 事件驱动的通知机制
   - 灵活的配置方式
   - 良好的扩展性

3. **生产就绪**
   - 完善的错误处理
   - 详细的日志记录
   - 容器化支持
   - 完整的文档

---

## 录制技巧

### 准备工作

1. **清理桌面**
   - 关闭不相关的应用
   - 清理终端历史

2. **准备窗口布局**
   - 终端窗口
   - 浏览器（监控面板）
   - 飞书/钉钉应用
   - 代码编辑器

3. **测试流程**
   - 在正式录制前完整走一遍流程
   - 确保所有通知都能正常收到

### 录制建议

1. **语速适中**
   - 清晰表达每个步骤
   - 给观众理解的时间

2. **突出重点**
   - 用鼠标指出关键信息
   - 重复重要概念

3. **流畅演示**
   - 提前准备好命令
   - 避免长时间等待

4. **画质清晰**
   - 使用 1080p 或更高分辨率
   - 确保文字清晰可读
   - 字体大小适中

### 推荐录屏工具

**Windows**:
- OBS Studio (免费)
- Camtasia (付费)

**macOS**:
- QuickTime Player (系统自带)
- ScreenFlow (付费)
- OBS Studio (免费)

**Linux**:
- OBS Studio
- SimpleScreenRecorder
- Kazam

---

## 故障排除

### 如果通知没有收到

1. 检查 webhook URL 是否正确
2. 查看应用日志中的错误信息
3. 测试 webhook URL:
   ```bash
   curl -X POST 'YOUR_WEBHOOK_URL' \
     -H 'Content-Type: application/json' \
     -d '{"msg_type":"text","content":{"text":"测试"}}'
   ```

### 如果监控面板无法访问

1. 确认应用已启动
2. 检查端口 8080 是否被占用
3. 查看启动日志

### 如果需要重新演示

```bash
# 停止应用
pkill -f sky-ai

# 等待几秒
sleep 3

# 重新启动
java -jar target/sky-ai-0.0.1-SNAPSHOT.jar
```

---

## 演示脚本示例

### 开场白

"大家好，我将演示一个基于 Spring Boot Admin 的服务监控平台。这个项目实现了服务启动和停止时的自动通知功能，支持飞书、钉钉和邮箱三种通知方式。"

### 项目介绍

"首先，让我展示一下项目结构。可以看到，我们有一个主应用类，一个自定义的飞书通知器，以及完整的配置文件。"

### 配置说明

"配置非常简单，只需要在配置文件中填写 webhook URL，并将相应的通知渠道设置为启用状态。"

### 启动演示

"现在让我启动应用。可以看到，Spring Boot Admin Server 正在启动... 应用已经成功启动了。"

### 监控面板

"打开浏览器，访问 localhost:8080/admin，这就是监控面板。可以看到我们的应用已经注册进来了，状态显示为 UP。"

### 通知展示

"同时，飞书和钉钉群也收到了服务启动的通知。通知中包含了服务名称、地址和状态信息。"

### 停止演示

"现在让我停止应用，看看停止通知。可以看到，监控面板中的状态变为了 OFFLINE，同时飞书和钉钉也收到了服务停止的通知。"

### 代码说明

"让我简单解释一下实现原理。这个通知器继承了 Spring Boot Admin 的 AbstractEventNotifier，监听服务状态变更事件，然后调用飞书的 Webhook API 发送通知。"

### 结束语

"以上就是整个项目的演示。这个实现不仅完成了任务要求，还提供了完整的监控界面、灵活的配置方式和良好的扩展性。谢谢观看！"

---

## 检查清单

录制前确认：

- [ ] 所有 webhook URL 已配置
- [ ] 应用可以正常启动
- [ ] 监控面板可以访问
- [ ] 通知功能已测试
- [ ] 录屏软件已准备好
- [ ] 窗口布局已调整好
- [ ] 字体大小合适
- [ ] 网络连接稳定

录制后检查：

- [ ] 视频画质清晰
- [ ] 声音清楚
- [ ] 演示流畅
- [ ] 所有功能都展示到位
- [ ] 时长在 5-8 分钟内
- [ ] 没有敏感信息泄露

---

祝录制顺利！🎥
