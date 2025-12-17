-- Initial data for Order Service

-- Orders
INSERT INTO orders (id, customer_name, customer_email, customer_phone, status, delivery_address, total_price, created_at, updated_at) VALUES 
(1, 'Іван Петренко', 'ivan.petrenko@gmail.com', '+380501234567', 'CREATED', 'вул. Хрещатик, 1, Київ, 01001', 27500.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Марія Коваленко', 'maria.kovalenko@ukr.net', '+380672223344', 'WAREHOUSE_RESERVED', 'вул. Шевченка, 45, Львів, 79000', 32000.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Олександр Мельник', 'oleksandr.melnyk@example.com', '+380933334455', 'IN_DELIVERY', 'пр. Незалежності, 100, Харків, 61000', 28000.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Order Items
INSERT INTO order_items (id, order_id, product_id, product_name, quantity, price_per_unit, total_price) VALUES 
(1, 1, 101, 'Ноутбук Lenovo ThinkPad', 1, 25000.0, 25000.0),
(2, 1, 102, 'Миша Logitech MX Master', 1, 2500.0, 2500.0),
(3, 2, 201, 'Смартфон Samsung Galaxy S23', 1, 28000.0, 28000.0),
(4, 2, 202, 'Чохол для телефону', 1, 500.0, 500.0),
(5, 2, 203, 'Навушники Samsung Buds', 1, 3500.0, 3500.0),
(6, 3, 301, 'Монітор Dell 27"', 2, 12000.0, 24000.0),
(7, 3, 302, 'Клавіатура механічна', 2, 2000.0, 4000.0);

-- Set sequence start values
ALTER SEQUENCE orders_seq RESTART WITH 4;
ALTER SEQUENCE order_items_seq RESTART WITH 8;
