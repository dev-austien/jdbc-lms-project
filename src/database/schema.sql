SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


CREATE TABLE orders (
  order_id int(11) NOT NULL,
  user_id int(11) NOT NULL,
  service_id int(11) NOT NULL,
  weight decimal(10,2) NOT NULL,
  total_amount decimal(10,2) NOT NULL,
  status enum('PENDING','WASHING','READY','CLAIMED') DEFAULT 'PENDING',
  order_date timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;



INSERT INTO orders (order_id, user_id, service_id, weight, total_amount, status, order_date) VALUES
(1, 3, 1, 8.00, 80.00, 'READY', '2026-05-17 11:35:11'),
(2, 3, 1, 5.00, 180.00, 'READY', '2026-05-17 11:45:43');


CREATE TABLE services (
  service_id int(11) NOT NULL,
  service_type varchar(100) NOT NULL,
  price decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


INSERT INTO services (service_id, service_type, price) VALUES
(1, 'wash', 180.00),
(2, 'Wash & Fold (Standard 8kg)', 180.00),
(3, 'Wash, Iron & Fold (Premium 8kg)', 320.00),
(4, 'Express Wash & Fold (Same-day 8kg)', 260.00),
(5, 'Comforter / Heavy Blanket (Per Piece)', 150.00),
(6, 'Delicate Wear / Formal Care (Per Piece)', 220.00);


CREATE TABLE users (
  user_id int(11) NOT NULL,
  username varchar(50) NOT NULL,
  password varchar(255) NOT NULL,
  role enum('ADMIN','STAFF','CUSTOMER') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


INSERT INTO users (user_id, username, password, role) VALUES
(1, 'admin', 'admin123', 'ADMIN'),
(2, 'austien', 'austien', 'STAFF'),
(3, 'james', 'james', 'CUSTOMER');


CREATE TABLE user_profile (
  profile_id int(11) NOT NULL,
  user_id int(11) NOT NULL,
  first_name varchar(50) NOT NULL,
  middle_name varchar(50) DEFAULT NULL,
  last_name varchar(50) NOT NULL,
  suffix varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


ALTER TABLE orders
  ADD PRIMARY KEY (order_id),
  ADD KEY user_id (user_id),
  ADD KEY service_id (service_id);


ALTER TABLE services
  ADD PRIMARY KEY (service_id);


ALTER TABLE users
  ADD PRIMARY KEY (user_id),
  ADD UNIQUE KEY username (username);


ALTER TABLE user_profile
  ADD PRIMARY KEY (profile_id),
  ADD KEY user_id (user_id);


ALTER TABLE orders
  MODIFY order_id int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;


ALTER TABLE services
  MODIFY service_id int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;


ALTER TABLE users
  MODIFY user_id int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;


ALTER TABLE user_profile
  MODIFY profile_id int(11) NOT NULL AUTO_INCREMENT;


ALTER TABLE orders
  ADD CONSTRAINT orders_ibfk_1 FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
  ADD CONSTRAINT orders_ibfk_2 FOREIGN KEY (service_id) REFERENCES services (service_id) ON DELETE CASCADE;


ALTER TABLE user_profile
  ADD CONSTRAINT user_profile_ibfk_1 FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE;
COMMIT;