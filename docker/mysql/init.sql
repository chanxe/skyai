-- MySQL initialization script for Sky AI
-- This script sets up the database with proper character encoding

CREATE DATABASE IF NOT EXISTS itheima 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

USE itheima;

-- Create a user specifically for the application (optional)
-- The main application user is already created via environment variables
-- This is just for reference

-- Grant all privileges to the application user
GRANT ALL PRIVILEGES ON itheima.* TO 'skyai'@'%';
FLUSH PRIVILEGES;

-- The application uses MyBatis-Plus with automatic table creation
-- So we don't need to create tables manually
-- They will be created when the application starts