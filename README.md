# Sky AI - Spring Boot AI Application

Sky AI is a Spring Boot application that integrates with AI models for chat, PDF processing, and other AI-powered features. It supports both Ollama (local) and OpenAI-compatible APIs (Aliyun DashScope).

## Features

- **Chat Interface**: Interactive chat with AI models
- **PDF Processing**: Chat with PDF documents using vector search
- **Multi-Modal Chat**: Support for text and file uploads  
- **Customer Service**: AI-powered customer service with function calling
- **Game Assistant**: Specialized game-related AI interactions
- **Chat History**: Persistent chat conversation management

## Prerequisites

- **Java 17** or higher
- **Maven 3.8+** (or use the included Maven wrapper)
- **MySQL 8.0+** (for production)
- **Ollama** (optional, for local AI models)
- **OpenAI API Key** or **Aliyun DashScope API Key**

## Quick Start

### 1. Clone and Build

```bash
git clone <repository-url>
cd skyai
chmod +x mvnw
./mvnw clean compile
```

### 2. Run Tests

```bash
./mvnw test
```

Tests use an in-memory H2 database and don't require external services.

### 3. Set up Environment

Choose one of the AI providers:

#### Option A: Using Aliyun DashScope (Default Configuration)

1. Get your API key from [Aliyun DashScope](https://dashscope.aliyun.com/)
2. Set the environment variable:
   ```bash
   export OPENAI_API_KEY=your_dashscope_api_key
   ```

#### Option B: Using Ollama (Local AI)

1. Install [Ollama](https://ollama.ai/)
2. Start Ollama service:
   ```bash
   ollama serve
   ```
3. Pull the required model:
   ```bash
   ollama pull deepseek-r1:1.5b
   ```

### 4. Set up Database

#### For Development (H2 Database)

Create a development profile by adding `application-dev.yaml`:

```yaml
spring:
  application:
    name: sky-ai
  ai:
    # Your AI configuration here
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:devdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    username: sa
    password: 
  h2:
    console:
      enabled: true
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
```

Then add H2 dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

#### For Production (MySQL)

1. Install and start MySQL
2. Create database:
   ```sql
   CREATE DATABASE itheima;
   CREATE USER 'skyai'@'localhost' IDENTIFIED BY 'your_password';
   GRANT ALL PRIVILEGES ON itheima.* TO 'skyai'@'localhost';
   ```
3. Update `application.yaml` with your database credentials

### 5. Run the Application

#### With Aliyun DashScope:
```bash
export OPENAI_API_KEY=your_dashscope_api_key
./mvnw spring-boot:run
```

#### With Development Profile (H2):
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

#### With Custom Configuration:
```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

## Docker Setup (Recommended)

### 1. Create Docker Compose File

```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    container_name: skyai-mysql
    environment:
      MYSQL_ROOT_PASSWORD: 123456
      MYSQL_DATABASE: itheima
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

  ollama:
    image: ollama/ollama
    container_name: skyai-ollama
    ports:
      - "11434:11434"
    volumes:
      - ollama_data:/root/.ollama

volumes:
  mysql_data:
  ollama_data:
```

### 2. Start Services

```bash
docker-compose up -d

# Pull the AI model for Ollama
docker exec skyai-ollama ollama pull deepseek-r1:1.5b
```

### 3. Run Application

```bash
export OPENAI_API_KEY=your_api_key  # Only if using DashScope
./mvnw spring-boot:run
```

## API Endpoints

Once running (default port 8080):

- **Chat**: `POST /ai/chat?prompt=Hello&chatId=session1`
- **PDF Chat**: `POST /ai/pdf/chat?prompt=Question&chatId=session1` 
- **Game Chat**: `POST /ai/game/chat?prompt=Game question&chatId=session1`
- **Customer Service**: `POST /ai/service/chat?prompt=Help request&chatId=session1`
- **Chat History**: `GET /ai/history/list`

### Example Chat Request

```bash
curl -X POST "http://localhost:8080/ai/chat?prompt=Hello%20World&chatId=test123"
```

## Configuration

### Environment Variables

- `OPENAI_API_KEY`: Your Aliyun DashScope API key (required for DashScope)

### Application Profiles

- `default`: Production configuration with MySQL and DashScope
- `test`: Test configuration with H2 database
- `dev`: Development configuration (you need to create this)

### Customizing AI Models

Edit `application.yaml` to change models:

```yaml
spring:
  ai:
    ollama:
      chat:
        model: your-preferred-model
    openai:
      chat:
        options:
          model: qwen-max-latest  # or other supported models
```

## Troubleshooting

### Common Issues

1. **Tests fail with network errors**: Tests should use the `test` profile automatically
2. **Database connection errors**: Ensure MySQL is running and credentials are correct
3. **AI API errors**: Check your API key and network connectivity
4. **Port conflicts**: Change the server port in `application.yaml`:
   ```yaml
   server:
     port: 8081
   ```

### Development Mode

For easier development without external dependencies:

1. Add H2 dependency to runtime scope
2. Create `application-dev.yaml` with H2 configuration  
3. Run with: `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev`

### Logs

Check application logs for detailed error information:
```bash
./mvnw spring-boot:run --debug
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Run tests: `./mvnw test`
5. Submit a pull request

## License

[Add your license information here]