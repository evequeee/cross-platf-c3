-- Initial data for Notification Service

INSERT INTO notifications (id, recipient, type, subject, message, status, created_at, sent_at, retry_count, error_message) VALUES
(1, 'ivan.petrenko@gmail.com', 'EMAIL', 'Замовлення #1 створено', 'Ваше замовлення успішно прийняте і обробляється. Загальна сума: 27500 грн', 'SENT', CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP - INTERVAL '2 hours', 0, NULL),
(2, '+380671112233', 'SMS', 'Статус доставки', 'Посилка в дорозі. Поточне місцезнаходження: Полтава, траса М03', 'DELIVERED', CURRENT_TIMESTAMP - INTERVAL '30 minutes', CURRENT_TIMESTAMP - INTERVAL '30 minutes', 0, NULL),
(3, 'maria.kovalenko@ukr.net', 'EMAIL', 'Замовлення готове до доставки', 'Ваше замовлення #2 підготовлено та очікує на відправку.', 'PENDING', CURRENT_TIMESTAMP, NULL, 0, NULL),
(4, 'user_12345', 'PUSH', 'Новий статус замовлення', 'Замовлення #3 доставлено. Дякуємо за покупку!', 'SENT', CURRENT_TIMESTAMP - INTERVAL '5 minutes', CURRENT_TIMESTAMP - INTERVAL '5 minutes', 0, NULL),
(5, 'invalid@email', 'EMAIL', 'Тестове повідомлення', 'Це тестове повідомлення', 'FAILED', CURRENT_TIMESTAMP, NULL, 3, 'Неправильна адреса email');

ALTER SEQUENCE notifications_seq RESTART WITH 6;
