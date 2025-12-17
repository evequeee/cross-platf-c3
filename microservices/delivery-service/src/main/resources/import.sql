-- Initial data for Delivery Service

INSERT INTO deliveries (id, order_id, driver_name, driver_phone, vehicle_number, pickup_address, delivery_address, status, created_at, estimated_delivery_time, actual_delivery_time, tracking_number, current_location) VALUES
(1, 3, 'Петро Сидоренко', '+380671112233', 'АА 1234 ВВ', 'Склад №1, вул. Промислова, 10', 'пр. Незалежності, 100, Харків, 61000', 'IN_TRANSIT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '2 hours', NULL, 'TRK1734456001', 'Полтава, траса М03'),
(2, 2, 'Олена Іваненко', '+380502223344', 'АВ 5678 СС', 'Склад №2, вул. Складська, 25', 'вул. Шевченка, 45, Львів, 79000', 'ASSIGNED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '4 hours', NULL, 'TRK1734456002', 'Склад №2, очікує завантаження'),
(3, 4, 'Андрій Коваль', '+380933334455', 'АС 9876 ММ', 'Склад №1, вул. Промислова, 10', 'вул. Грушевського, 5, Київ, 01001', 'DELIVERED', CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '30 minutes', 'TRK1734456003', 'Доставлено'),
(4, 5, 'Марина Бойко', '+380674445566', 'ВВ 1111 КК', 'Склад №3, вул. Логістична, 50', 'вул. Сагайдачного, 30, Дніпро, 49000', 'OUT_FOR_DELIVERY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 minutes', NULL, 'TRK1734456004', 'Дніпро, вул. Набережна');

ALTER SEQUENCE deliveries_seq RESTART WITH 5;
