-- Create Users Table (Authentication)
CREATE TABLE Users (
                       user_id INT PRIMARY KEY AUTO_INCREMENT,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       role ENUM('Admin', 'Worker', 'Customer') NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Employees Table (Staff Info)
CREATE TABLE Employees (
                           employee_id INT PRIMARY KEY AUTO_INCREMENT,
                           user_id INT,
                           first_name VARCHAR(50),
                           last_name VARCHAR(50),
                           position VARCHAR(50),
                           phone VARCHAR(15),
                           FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

-- Create Customers Table (Client Info)
CREATE TABLE Customers (
                           customer_id INT PRIMARY KEY AUTO_INCREMENT,
                           user_id INT, -- Links to their login
                           first_name VARCHAR(50),
                           last_name VARCHAR(50),
                           phone_number VARCHAR(15) UNIQUE,
                           email VARCHAR(100),
                           address TEXT,
                           FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE SET NULL
);

-- Create Services Table (Price List)
CREATE TABLE Services (
                          service_id INT PRIMARY KEY AUTO_INCREMENT,
                          service_name VARCHAR(100),
                          unit_type ENUM('KG', 'Piece'),
                          price_per_unit DECIMAL(10, 2)
);

-- Create Orders Table
CREATE TABLE Orders (
                        order_id INT PRIMARY KEY AUTO_INCREMENT,
                        customer_id INT,
                        worker_id INT, -- Tracks which employee is handling the order
                        order_date DATETIME DEFAULT CURRENT_TIMESTAMP,
                        pickup_date DATETIME,
                        delivery_date DATETIME,
                        total_amount DECIMAL(10, 2) DEFAULT 0.00,
                        status ENUM('Pending', 'In-Progress', 'Ready', 'Delivered', 'Cancelled') DEFAULT 'Pending',
                        FOREIGN KEY (customer_id) REFERENCES Customers(customer_id),
                        FOREIGN KEY (worker_id) REFERENCES Employees(employee_id)
);

-- Create Order Items (Breakdown of an order)
CREATE TABLE Order_Items (
                             item_id INT PRIMARY KEY AUTO_INCREMENT,
                             order_id INT,
                             service_id INT,
                             quantity DECIMAL(10, 2),
                             subtotal DECIMAL(10, 2),
                             FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON DELETE CASCADE,
                             FOREIGN KEY (service_id) REFERENCES Services(service_id)
);

-- Create Payments Table
CREATE TABLE Payments (
                          payment_id INT PRIMARY KEY AUTO_INCREMENT,
                          order_id INT,
                          payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          amount_paid DECIMAL(10, 2),
                          payment_method ENUM('Cash', 'Card', 'Online'),
                          FOREIGN KEY (order_id) REFERENCES Orders(order_id)
);