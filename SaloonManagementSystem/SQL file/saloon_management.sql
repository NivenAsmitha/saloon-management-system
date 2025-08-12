-- Create Database
CREATE DATABASE IF NOT EXISTS saloon_management;
USE saloon_management;

-- ==========================
-- Users Table
-- ==========================
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'EMPLOYEE') NOT NULL,
    must_change_password TINYINT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==========================
-- Employees Table
-- ==========================
CREATE TABLE IF NOT EXISTS employees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    email VARCHAR(100),
    address TEXT,
    salary DECIMAL(10,2),
    hire_date DATE,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ==========================
-- Products Table
-- ==========================
CREATE TABLE IF NOT EXISTS products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==========================
-- Services Table
-- ==========================
CREATE TABLE IF NOT EXISTS services (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    duration_minutes INT DEFAULT 30,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==========================
-- Bookings Table
-- ==========================
CREATE TABLE IF NOT EXISTS bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL,
    customer_age INT NULL,
    customer_phone VARCHAR(15),
    booking_date DATE NOT NULL,
    booking_time TIME NOT NULL,
    employee_id INT,
    status ENUM('PENDING', 'CONFIRMED', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
    total_amount DECIMAL(10,2) DEFAULT 0,
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id),
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- ==========================
-- Booking Services Table
-- ==========================
CREATE TABLE IF NOT EXISTS booking_services (
    id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT,
    service_id INT,
    quantity INT DEFAULT 1,
    price DECIMAL(10,2),
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES services(id)
);

-- ==========================
-- Booking Products Table
-- ==========================
CREATE TABLE IF NOT EXISTS booking_products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT,
    product_id INT,
    quantity INT DEFAULT 1,
    price DECIMAL(10,2),
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- ==========================
-- Bills Table
-- ==========================
CREATE TABLE IF NOT EXISTS bills (
    id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

-- ==========================
-- Seed Data
-- ==========================
INSERT INTO users (username, password, role, must_change_password)
VALUES ('admin', 'admin123', 'ADMIN', 0)
ON DUPLICATE KEY UPDATE username = username;

INSERT INTO employees (name, phone, email, address, salary, hire_date, user_id)
VALUES ('John Doe', '123-456-7890', 'john@salon.com', '123 Main St', 2500.00, '2024-01-15', 1)
ON DUPLICATE KEY UPDATE name = name;

INSERT INTO services (name, description, price, duration_minutes) VALUES
('Haircut', 'Professional haircut and styling', 25.00, 30),
('Hair Wash', 'Shampoo and conditioning treatment', 15.00, 20),
('Facial', 'Deep cleansing facial treatment', 45.00, 60),
('Massage', 'Relaxing full body massage', 80.00, 90);

INSERT INTO users (username, password, role, must_change_password)
VALUES ('emma', 'pass123', 'EMPLOYEE', 1)
ON DUPLICATE KEY UPDATE username = username;
