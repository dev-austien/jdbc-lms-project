CREATE DATABASE lsms_db;
USE lsms_db;

-- 1. Users Table --
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM ('ADMIN', 'STAFF', 'CUSTOMER') NOT NULL
);

-- 2. User Profile (Enforced 1-to-1 Relationship) --
CREATE TABLE user_profile (
    profile_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    middle_name VARCHAR(50),
    last_name VARCHAR(50) NOT NULL,
    suffix VARCHAR(10),
    phone_number VARCHAR(15),

    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 3. Services Table --
CREATE TABLE services (
    service_id INT PRIMARY KEY AUTO_INCREMENT,
    service_type VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    pricing_unit ENUM('PER_KG', 'PER_PIECE') NOT NULL
);

-- 4. Orders Table (receipt header) --
CREATE TABLE orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status ENUM('PENDING', 'WASHING', 'READY', 'CLAIMED') DEFAULT 'PENDING',
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (customer_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 5. Order Items (quantity = kg for wash services, or piece count for specialty items) --
CREATE TABLE order_items (
    item_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    service_id INT NOT NULL,
    quantity DECIMAL(10,2) NOT NULL,
    price_at_purchase DECIMAL(10,2) NOT NULL,

    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES services(service_id) ON DELETE RESTRICT
);

-- Default admin --
INSERT INTO users (username, password, role)
VALUES ('admin', 'admin123', 'ADMIN');

INSERT INTO user_profile (user_id, first_name, last_name)
SELECT user_id, 'System', 'Administrator'
FROM users
WHERE username = 'admin';

-- Catalog --
INSERT INTO services (service_type, description, unit_price, pricing_unit) VALUES
(
    'Wash, Dry, and Iron (Premium Care)',
    'Clothes are washed, dried, professionally ironed, and placed on hangers. Perfect for office uniforms, formal wear, and customers who hate ironing.',
    280.00,
    'PER_KG'
),
(
    'Comforters & Heavy Blankets (Specialty Item)',
    'A dedicated cycle for bulky items like thick comforters, duvets, and heavy blankets that won''t fit or dry well in standard home machines.',
    250.00,
    'PER_PIECE'
),
(
    'Express Wash and Fold (Speed Service)',
    'The exact same high-quality care as your standard Wash and Fold, but guaranteed to be finished and ready for pickup within 2 to 3 hours.',
    300.00,
    'PER_KG'
),
(
    'Dry Cleaning / Delicate Care (Delicates)',
    'Specialized solvent-based or ultra-gentle cleaning for sensitive fabrics like silk, wool, leather jackets, or delicate gowns that cannot undergo standard machine washing.',
    150.00,
    'PER_PIECE'
);

