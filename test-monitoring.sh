#!/bin/bash

# Test script for Spring Boot Admin monitoring platform
# This script demonstrates how to test the notification functionality

echo "==================================="
echo "Spring Boot Admin Monitoring Test"
echo "==================================="
echo ""

echo "This test will:"
echo "1. Start the Spring Boot application (triggers UP notification)"
echo "2. Wait for 20 seconds"
echo "3. Stop the application (triggers DOWN notification)"
echo ""

# Check if application.yaml has notification enabled
echo "Checking notification configuration..."
if grep -q "enabled: true" src/main/resources/application.yaml; then
    echo "✅ Notifications are enabled in application.yaml"
else
    echo "⚠️  Notifications are not enabled. To enable:"
    echo "   - Set spring.boot.admin.notify.feishu.enabled=true"
    echo "   - Set spring.boot.admin.notify.dingtalk.enabled=true"
    echo "   - Set spring.boot.admin.notify.mail.enabled=true"
    echo "   - Configure webhook URLs or mail settings"
fi
echo ""

echo "Starting application..."
echo "The application will register itself with Admin Server"
echo "You can access the admin panel at: http://localhost:8080/admin"
echo ""

# Build the application first
echo "Building application..."
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    echo ""
    echo "Starting application (this will trigger UP notification)..."
    echo "Press Ctrl+C to stop the application (this will trigger DOWN notification)"
    echo ""
    
    # Start the application
    java -jar target/sky-ai-0.0.1-SNAPSHOT.jar
else
    echo "❌ Build failed!"
    exit 1
fi
