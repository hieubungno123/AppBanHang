-- CREATE DATABASE shopapp;

-- USE shopapp;

-- Khách hàng khi muốn mua hàng => phải đăng ký tài khoản => bảng users

-- CREATE TABLE users(
--     id INT PRIMARY KEY AUTO_INCREMENT,
--     fullname VARCHAR(100) DEFAULT '',
--     phone_number VARCHAR(10) NOT NULL,
--     address VARCHAR(200) DEFAULT '',
--     -- username VARCHAR(100) NOT NULL DEFAULT '',
--     password VARCHAR(100) NOT NULL DEFAULT '',  -- MẬT KHẨU ĐÃ MÃ HÓA
--     create_at datetime,
--     update_at datetime,
--     is_active tinyint(1) DEFAULT 1,
--     date_of_birth DATE,
--     facebook_account_id INT DEFAULT 0,
--     google_account_id INT DEFAULT 0
-- ); 

-- alter table users ADD column role_id int;
-- alter table users ADD foreign key (role_id) references role(id);


-- CREATE TABLE roles (
-- 	id INT primary KEY,
--     name varchar(20) NOT NULL
-- );

-- CREATE TABLE tokens(
--     id int PRIMARY KEY AUTO_INCREMENT,
--     token VARCHAR(255) UNIQUE NOT NULL,
--     token_type VARCHAR(50) NOT NULL,
--     expiration_date datetime,
--     revoked tinyint(1) NOT NULL,
--     expired tinyint(1) NOT NULL,
--     user_id int , 
--     FOREIGN KEY (user_id) REFERENCES users(id)
-- );

-- CREATE TABLE social_accounts(
--     id INT PRIMARY KEY AUTO_INCREMENT,
--     provider VARCHAR(20) NOT NULL COMMENT 'Tên nhà social network',
--     provider_id VARCHAR(50) NOT NULL,
--     email VARCHAR(150) NOT NULL COMMENT 'Email tài khoản',
--     name VARCHAR(100) NOT NULL COMMENT 'Tên người dùng',
--     user_id int,
--     FOREIGN KEY (user_id) REFERENCES users(id)
-- );

-- CREATE TABLE categories(
--     id INT PRIMARY KEY AUTO_INCREMENT,
--     name VARCHAR(100) NOT NULL DEFAULT '' COMMENT 'Tên danh mục, vd: đồ điện tử'
-- );

-- CREATE TABLE products(
--     id INT PRIMARY KEY AUTO_INCREMENT,
--     name VARCHAR(350) COMMENT 'Tên sản phẩm',
--     price FLOAT NOT NULL CHECK (price >= 0),
--     thumbnail VARCHAR(255) DEFAULT '',
--     description TEXT NOT NULL,
--     create_at datetime,
--     update_at datetime,
--     category_id INT,
--     FOREIGN KEY (category_id) REFERENCES categories(id)
-- );

CREATE TABLE product_images(
    id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT,
    FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_product_images_product_id
        FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
        -- Khi xóa product thì product_imaged sẽ bị xóa theo
    image_url VARCHAR(300)

);

-- CREATE TABLE orders(
--     id INT PRIMARY KEY AUTO_INCREMENT,
--     user_id int,
--     FOREIGN KEY (user_id) REFERENCES users(id),
--     fullname VARCHAR(100) DEFAULT '',
--     email VARCHAR(100) DEFAULT '',
--     phone_number VARCHAR (20) NOT NULL,
--     address VARCHAR(200) NOT NULL,
--     note VARCHAR(100) DEFAULT '',
--     order_date datetime DEFAULT CURRENT_TIMESTAMP,
--     status VARCHAR(20),
--     total_money FLOAT CHECK(total_money >= 0),
--     shipping_method VARCHAR(100),
--     shipping_address VARCHAR(200),
--     shipping_date DATE,
--     tracking_number VARCHAR(100),
--     payment_method VARCHAR(100),
-- -- 	--Xóa đơn hàng -> xóa mềm -> thêm trường active

--     active TINYINT(1)
-- );

-- ALTER TABLE orders MODIFY COLUMN status ENUM('pending','processing','shipped','delivered', 'cancelled') COMMENT 'Trạng thái đơn hàng';

-- CREATE TABLE order_details(
--     id INT PRIMARY KEY AUTO_INCREMENT,
--     order_id INT,
--     FOREIGN KEY (order_id) REFERENCES orders(id),
--     product_id INT,
--     FOREIGN KEY (product_id) REFERENCES products(id),
--     price FLOAT CHECK(price >= 0),
--     number_of_products INT CHECK(number_of_products > 0),
--     total_money FLOAT CHECK(total_money >= 0),
--     color VARCHAR(20) DEFAULT ''
-- );
