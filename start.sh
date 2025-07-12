#!/bin/bash

# Sky AI Startup Script

set -e

echo "🚀 Starting Sky AI Application"
echo "==============================="

# Function to display help
show_help() {
    echo "Usage: $0 [OPTION]"
    echo ""
    echo "Options:"
    echo "  dev       Start in development mode with H2 database (no external services required)"
    echo "  prod      Start in production mode with MySQL and external AI services"
    echo "  docker    Start external services using Docker Compose"
    echo "  test      Run tests only"
    echo "  build     Build the application"
    echo "  help      Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 dev           # Quick start for development"
    echo "  $0 docker        # Start MySQL and Ollama with Docker"
    echo "  $0 prod          # Start with external services (requires setup)"
    echo ""
}

# Function to check if required tools are available
check_requirements() {
    if ! command -v java &> /dev/null; then
        echo "❌ Java 17+ is required but not found"
        exit 1
    fi
    
    java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F '.' '{print $1}')
    if [ "$java_version" -lt 17 ]; then
        echo "❌ Java 17+ is required, but found version $java_version"
        exit 1
    fi
    
    echo "✅ Java version: $(java -version 2>&1 | head -n1)"
}

# Function to start development mode
start_dev() {
    echo "🔧 Starting in development mode..."
    echo "   - Using H2 in-memory database"
    echo "   - No external services required"
    echo "   - H2 Console: http://localhost:8080/h2-console"
    echo ""
    
    export OPENAI_API_KEY=${OPENAI_API_KEY:-demo-key}
    ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
}

# Function to start production mode
start_prod() {
    echo "🏭 Starting in production mode..."
    
    if [ -z "$OPENAI_API_KEY" ]; then
        echo "⚠️  Warning: OPENAI_API_KEY not set. AI features may not work."
        echo "   Set it with: export OPENAI_API_KEY=your_key"
    fi
    
    echo "   - Using MySQL database (localhost:3306)"
    echo "   - Using external AI services"
    echo ""
    
    ./mvnw spring-boot:run
}

# Function to start Docker services
start_docker() {
    echo "🐳 Starting external services with Docker..."
    
    if ! command -v docker-compose &> /dev/null && ! command -v docker &> /dev/null; then
        echo "❌ Docker is required but not found"
        exit 1
    fi
    
    echo "   Starting MySQL and Ollama..."
    docker-compose up -d
    
    echo ""
    echo "✅ Docker services started!"
    echo "   - MySQL: localhost:3306 (username: root, password: 123456)"
    echo "   - Ollama: localhost:11434"
    echo ""
    echo "💡 Next steps:"
    echo "   1. Wait for services to be ready (about 30 seconds)"
    echo "   2. Run: $0 prod"
    echo ""
    echo "To stop services: docker-compose down"
}

# Function to run tests
run_tests() {
    echo "🧪 Running tests..."
    ./mvnw test
}

# Function to build application
build_app() {
    echo "🔨 Building application..."
    ./mvnw clean package -DskipTests
    echo "✅ Build complete! JAR file: target/sky-ai-0.0.1-SNAPSHOT.jar"
}

# Check requirements
check_requirements

# Make sure Maven wrapper is executable
chmod +x mvnw

# Main logic
case "${1:-help}" in
    "dev")
        start_dev
        ;;
    "prod")
        start_prod
        ;;
    "docker")
        start_docker
        ;;
    "test")
        run_tests
        ;;
    "build")
        build_app
        ;;
    "help"|*)
        show_help
        ;;
esac