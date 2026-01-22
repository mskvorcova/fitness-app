-- Все пароли: "password123" у админа "admin123"
BEGIN;

DELETE FROM bookings;
DELETE FROM memberships;
DELETE FROM schedule_workouts;
DELETE FROM clients;
DELETE FROM trainers;
DELETE FROM users WHERE role != 'ADMIN';

INSERT INTO users (username, password, email, name, role) VALUES
('admin', '$2a$10$neqQZAyr/8FMlxDbNHWNWu2ZfhBnDP2PVkUAq3AgAXT3DCrfVJoT2', 'admin@pulsar-fitness.com', 'Администратор', 'ADMIN')
ON CONFLICT (username) DO NOTHING;

INSERT INTO users (username, password, email, name, role, created_at) VALUES
('ivan.petrov', '$2a$10$Ca9ZgedMb1RZPGXwtK8OW.2ipQxDicwKpYLsAIUgjN6lfTwlYgzxy', 'ivan.petrov@email.ru', 'Иван Петров', 'CLIENT', NOW()),
('maria.ivanova', '$2a$10$Ca9ZgedMb1RZPGXwtK8OW.2ipQxDicwKpYLsAIUgjN6lfTwlYgzxy', 'maria.ivanova@email.ru', 'Мария Иванова', 'CLIENT', NOW()),
('sergey.sidorov', '$2a$10$Ca9ZgedMb1RZPGXwtK8OW.2ipQxDicwKpYLsAIUgjN6lfTwlYgzxy', 'sergey.sidorov@email.ru', 'Сергей Сидоров', 'CLIENT', NOW()),
('elena.kuznetsova', '$2a$10$Ca9ZgedMb1RZPGXwtK8OW.2ipQxDicwKpYLsAIUgjN6lfTwlYgzxy', 'elena.kuznetsova@email.ru', 'Елена Кузнецова', 'CLIENT', NOW()),
('dmitry.volkov', '$2a$10$Ca9ZgedMb1RZPGXwtK8OW.2ipQxDicwKpYLsAIUgjN6lfTwlYgzxy', 'dmitry.volkov@email.ru', 'Дмитрий Волков', 'CLIENT', NOW()),
('anna.smirnova', '$2a$10$Ca9ZgedMb1RZPGXwtK8OW.2ipQxDicwKpYLsAIUgjN6lfTwlYgzxy', 'anna.smirnova@email.ru', 'Анна Смирнова', 'CLIENT', NOW()),
('pavel.novikov', '$2a$10$Ca9ZgedMb1RZPGXwtK8OW.2ipQxDicwKpYLsAIUgjN6lfTwlYgzxy', 'pavel.novikov@email.ru', 'Павел Новиков', 'CLIENT', NOW()),
('olga.kozлова', '$2a$10$Ca9ZgedMb1RZPGXwtK8OW.2ipQxDicwKpYLsAIUgjN6lfTwlYgzxy', 'olga.kozlova@email.ru', 'Ольга Козлова', 'CLIENT', NOW()),
('alexey.morozov', '$2a$10$Ca9ZgedMb1RZPGXwtK8OW.2ipQxDicwKpYLsAIUgjN6lfTwlYgzxy', 'alexey.morozov@email.ru', 'Алексей Морозов', 'CLIENT', NOW()),
('tatiana.lebedeva', '$2a$10$Ca9ZgedMb1RZPGXwtK8OW.2ipQxDicwKpYLsAIUgjN6lfTwlYgzxy', 'tatiana.lebedeva@email.ru', 'Татьяна Лебедева', 'CLIENT', NOW()),
('nikolay.sokolov', '$2a$10$Ca9ZgedMb1RZPGXwtK8OW.2ipQxDicwKpYLsAIUgjN6lfTwlYgzxy', 'nikolay.sokolov@email.ru', 'Николай Соколов', 'CLIENT', NOW()),
('svetlana.popova', '$2a$10$Ca9ZgedMb1RZPGXwtK8OW.2ipQxDicwKpYLsAIUgjN6lfTwlYgzxy', 'svetlana.popova@email.ru', 'Светлана Попова', 'CLIENT', NOW()),
('mikhail.fedorov', '$2a$10$Ca9ZgedMb1RZPGXwtK8OW.2ipQxDicwKpYLsAIUgjN6lfTwlYgzxy', 'mikhail.fedorov@email.ru', 'Михаил Федоров', 'CLIENT', NOW()),
('yulia.vasilieva', '$2a$10$Ca9ZgedMb1RZPGXwtK8OW.2ipQxDicwKpYLsAIUgjN6lfTwlYgzxy', 'yulia.vasilieva@email.ru', 'Юлия Васильева', 'CLIENT', NOW()),
('andrey.romanov', '$2a$10$Ca9ZgedMb1RZPGXwtK8OW.2ipQxDicwKpYLsAIUgjN6lfTwlYgzxy', 'andrey.romanov@email.ru', 'Андрей Романов', 'CLIENT', NOW()),

('trainer.anna', '$2a$10$Ca9ZgedMb1RZPGXwtK8OW.2ipQxDicwKpYLsAIUgjN6lfTwlYgzxy', 'trainer.anna@email.ru', 'Анна Тренерова', 'TRAINER', NOW()),
('trainer.maxim', '$2a$10$Ca9ZgedMb1RZPGXwtK8OW.2ipQxDicwKpYLsAIUgjN6lfTwlYgzxy', 'trainer.maxim@email.ru', 'Максим Тренеров', 'TRAINER', NOW()),
('trainer.ekaterina', '$2a$10$Ca9ZgedMb1RZPGXwtK8OW.2ipQxDicwKpYLsAIUgjN6lfTwlYgzxy', 'trainer.ekaterina@email.ru', 'Екатерина Тренерова', 'TRAINER', NOW()),
('trainer.artem', '$2a$10$Ca9ZgedMb1RZPGXwtK8OW.2ipQxDicwKpYLsAIUgjN6lfTwlYgzxy', 'trainer.artem@email.ru', 'Артем Тренеров', 'TRAINER', NOW()),
('trainer.victoria', '$2a$10$Ca9ZgedMb1RZPGXwtK8OW.2ipQxDicwKpYLsAIUgjN6lfTwlYgzxy', 'trainer.victoria@email.ru', 'Виктория Тренерова', 'TRAINER', NOW());

INSERT INTO clients (user_id, phone, registration_date) VALUES
((SELECT id FROM users WHERE username = 'ivan.petrov'), '+7 999 111-11-11', '2024-01-15'),
((SELECT id FROM users WHERE username = 'maria.ivanova'), '+7 999 222-22-22', '2024-02-01'),
((SELECT id FROM users WHERE username = 'sergey.sidorov'), '+7 999 333-33-33', '2024-02-10'),
((SELECT id FROM users WHERE username = 'elena.kuznetsova'), '+7 999 444-44-44', '2024-02-20'),
((SELECT id FROM users WHERE username = 'dmitry.volkov'), '+7 999 555-55-55', '2024-03-01'),
((SELECT id FROM users WHERE username = 'anna.smirnova'), '+7 999 666-66-66', '2024-03-05'),
((SELECT id FROM users WHERE username = 'pavel.novikov'), '+7 999 777-77-77', '2024-03-10'),
((SELECT id FROM users WHERE username = 'olga.kozлова'), '+7 999 888-88-88', '2024-03-15'),
((SELECT id FROM users WHERE username = 'alexey.morozov'), '+7 999 999-99-99', '2024-03-20'),
((SELECT id FROM users WHERE username = 'tatiana.lebedeva'), '+7 999 000-00-00', '2024-03-25'),
((SELECT id FROM users WHERE username = 'nikolay.sokolov'), '+7 998 111-11-11', '2024-04-01'),
((SELECT id FROM users WHERE username = 'svetlana.popova'), '+7 998 222-22-22', '2024-04-05'),
((SELECT id FROM users WHERE username = 'mikhail.fedorov'), '+7 998 333-33-33', '2024-04-10'),
((SELECT id FROM users WHERE username = 'yulia.vasilieva'), '+7 998 444-44-44', '2024-04-15'),
((SELECT id FROM users WHERE username = 'andrey.romanov'), '+7 998 555-55-55', '2024-04-20');

INSERT INTO trainers (user_id, description, experience_years) VALUES
((SELECT id FROM users WHERE username = 'trainer.anna'), 'Сертифицированный тренер по йоге и пилатесу. Опыт работы 8 лет. Специализация: восстановительная гимнастика и стретчинг.', 8),
((SELECT id FROM users WHERE username = 'trainer.maxim'), 'Мастер спорта по тяжелой атлетике. Тренер по силовым тренировкам и функциональному тренингу. Опыт 12 лет.', 12),
((SELECT id FROM users WHERE username = 'trainer.ekaterina'), 'Тренер по кардио и HIIT тренировкам. Специалист по снижению веса и улучшению выносливости. Опыт 6 лет.', 6),
((SELECT id FROM users WHERE username = 'trainer.artem'), 'Тренер по кроссфиту и функциональному тренингу. Участник соревнований по функциональному многоборью. Опыт 10 лет.', 10),
((SELECT id FROM users WHERE username = 'trainer.victoria'), 'Тренер по танцам и аэробике. Специалист по групповым программам. Опыт 7 лет.', 7);

INSERT INTO membership_types (name, description, duration_days, price) VALUES
('Базовый', 'Базовый абонемент на месяц. Доступ ко всем групповым тренировкам.', 30, 3000.00),
('Стандарт', 'Стандартный абонемент на 3 месяца. Доступ ко всем групповым тренировкам. Скидка 10%.', 90, 8100.00),
('Премиум', 'Премиум абонемент на 6 месяцев. Доступ ко всем групповым тренировкам и персональным консультациям. Скидка 20%.', 180, 14400.00),
('Годовой', 'Годовой абонемент. Полный доступ ко всем услугам клуба. Скидка 30%.', 365, 25550.00),
('Пробный', 'Пробный абонемент на неделю. Доступ ко всем групповым тренировкам.', 7, 1000.00);

INSERT INTO memberships (client_id, membership_type_id, start_date, end_date, status, price) VALUES
((SELECT id FROM clients WHERE user_id = (SELECT id FROM users WHERE username = 'ivan.petrov')),
 (SELECT id FROM membership_types WHERE name = 'Премиум'), 
 '2025-01-15', '2025-07-15', 'ACTIVE', 14400.00),
((SELECT id FROM clients WHERE user_id = (SELECT id FROM users WHERE username = 'maria.ivanova')), 
 (SELECT id FROM membership_types WHERE name = 'Стандарт'), 
 '2025-02-01', '2025-05-01', 'ACTIVE', 8100.00),
((SELECT id FROM clients WHERE user_id = (SELECT id FROM users WHERE username = 'sergey.sidorov')), 
 (SELECT id FROM membership_types WHERE name = 'Базовый'), 
 '2025-03-01', '2025-03-31', 'ACTIVE', 3000.00),
((SELECT id FROM clients WHERE user_id = (SELECT id FROM users WHERE username = 'elena.kuznetsova')), 
 (SELECT id FROM membership_types WHERE name = 'Годовой'), 
 '2025-01-01', '2025-12-31', 'ACTIVE', 25550.00),
((SELECT id FROM clients WHERE user_id = (SELECT id FROM users WHERE username = 'dmitry.volkov')), 
 (SELECT id FROM membership_types WHERE name = 'Стандарт'), 
 '2025-03-01', '2025-05-30', 'ACTIVE', 8100.00),
((SELECT id FROM clients WHERE user_id = (SELECT id FROM users WHERE username = 'anna.smirnova')), 
 (SELECT id FROM membership_types WHERE name = 'Базовый'), 
 '2025-03-05', '2025-04-04', 'ACTIVE', 3000.00),
((SELECT id FROM clients WHERE user_id = (SELECT id FROM users WHERE username = 'pavel.novikov')), 
 (SELECT id FROM membership_types WHERE name = 'Премиум'), 
 '2025-02-01', '2025-07-31', 'ACTIVE', 14400.00),
((SELECT id FROM clients WHERE user_id = (SELECT id FROM users WHERE username = 'olga.kozлова')), 
 (SELECT id FROM membership_types WHERE name = 'Стандарт'), 
 '2025-03-15', '2025-06-13', 'ACTIVE', 8100.00),
((SELECT id FROM clients WHERE user_id = (SELECT id FROM users WHERE username = 'alexey.morozov')), 
 (SELECT id FROM membership_types WHERE name = 'Базовый'), 
 '2025-03-20', '2025-04-19', 'ACTIVE', 3000.00),
((SELECT id FROM clients WHERE user_id = (SELECT id FROM users WHERE username = 'tatiana.lebedeva')), 
 (SELECT id FROM membership_types WHERE name = 'Пробный'), 
 CURRENT_DATE, CURRENT_DATE + INTERVAL '7 days', 'ACTIVE', 1000.00),
((SELECT id FROM clients WHERE user_id = (SELECT id FROM users WHERE username = 'nikolay.sokolov')),
 (SELECT id FROM membership_types WHERE name = 'Базовый'), 
 '2024-01-01', '2024-01-31', 'EXPIRED', 3000.00),
((SELECT id FROM clients WHERE user_id = (SELECT id FROM users WHERE username = 'svetlana.popova')), 
 (SELECT id FROM membership_types WHERE name = 'Стандарт'), 
 '2023-12-01', '2024-02-29', 'EXPIRED', 8100.00),
((SELECT id FROM clients WHERE user_id = (SELECT id FROM users WHERE username = 'mikhail.fedorov')),
 (SELECT id FROM membership_types WHERE name = 'Базовый'), 
 '2024-02-01', '2024-03-02', 'CANCELLED', 3000.00);

INSERT INTO workout_types (name, duration_minutes, hall, capacity, description) VALUES
('Йога для начинающих', 60, 'Зал 1', 20, 'Мягкая практика йоги для новичков. Изучение базовых асан и дыхательных техник.'),
('Силовая тренировка', 90, 'Зал 2', 15, 'Интенсивная силовая тренировка с использованием свободных весов и тренажеров.'),
('Кардио HIIT', 45, 'Зал 1', 25, 'Высокоинтенсивная интервальная тренировка для сжигания калорий и улучшения выносливости.'),
('Пилатес', 60, 'Зал 3', 18, 'Укрепление мышц кора, улучшение гибкости и осанки.'),
('Стретчинг', 45, 'Зал 3', 20, 'Растяжка всех групп мышц, улучшение гибкости и расслабление.'),
('Кроссфит', 60, 'Зал 2', 12, 'Функциональный тренинг высокой интенсивности.'),
('Танцевальная аэробика', 60, 'Зал 1', 30, 'Энергичная танцевальная тренировка под музыку.'),
('Функциональный тренинг', 60, 'Зал 2', 16, 'Тренировка, имитирующая движения из повседневной жизни.'),
('Йога для продвинутых', 75, 'Зал 3', 15, 'Продвинутая практика йоги с сложными асанами.'),
('Силовая тренировка для женщин', 60, 'Зал 2', 18, 'Специальная программа силовых тренировок для женщин.');

INSERT INTO schedule_workouts (day_of_week, start_time, workout_type_id, trainer_id) VALUES
('MONDAY', '09:00', (SELECT id FROM workout_types WHERE name = 'Йога для начинающих'),
 (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'trainer.anna'))),
('MONDAY', '10:30', (SELECT id FROM workout_types WHERE name = 'Пилатес'), 
 (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'trainer.anna'))),
('MONDAY', '18:00', (SELECT id FROM workout_types WHERE name = 'Силовая тренировка'), 
 (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'trainer.maxim'))),
('MONDAY', '19:30', (SELECT id FROM workout_types WHERE name = 'Кардио HIIT'), 
 (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'trainer.ekaterina'))),

('TUESDAY', '09:00', (SELECT id FROM workout_types WHERE name = 'Стретчинг'),
 (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'trainer.anna'))),
('TUESDAY', '10:30', (SELECT id FROM workout_types WHERE name = 'Танцевальная аэробика'), 
 (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'trainer.victoria'))),
('TUESDAY', '18:00', (SELECT id FROM workout_types WHERE name = 'Кроссфит'), 
 (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'trainer.artem'))),
('TUESDAY', '19:30', (SELECT id FROM workout_types WHERE name = 'Функциональный тренинг'), 
 (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'trainer.maxim'))),

('WEDNESDAY', '09:00', (SELECT id FROM workout_types WHERE name = 'Йога для продвинутых'),
 (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'trainer.anna'))),
('WEDNESDAY', '10:30', (SELECT id FROM workout_types WHERE name = 'Пилатес'), 
 (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'trainer.anna'))),
('WEDNESDAY', '18:00', (SELECT id FROM workout_types WHERE name = 'Силовая тренировка для женщин'), 
 (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'trainer.ekaterina'))),
('WEDNESDAY', '19:30', (SELECT id FROM workout_types WHERE name = 'Кардио HIIT'), 
 (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'trainer.ekaterina'))),

('THURSDAY', '09:00', (SELECT id FROM workout_types WHERE name = 'Стретчинг'),
 (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'trainer.anna'))),
('THURSDAY', '10:30', (SELECT id FROM workout_types WHERE name = 'Танцевальная аэробика'), 
 (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'trainer.victoria'))),
('THURSDAY', '18:00', (SELECT id FROM workout_types WHERE name = 'Кроссфит'), 
 (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'trainer.artem'))),
('THURSDAY', '19:30', (SELECT id FROM workout_types WHERE name = 'Силовая тренировка'), 
 (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'trainer.maxim'))),

('FRIDAY', '09:00', (SELECT id FROM workout_types WHERE name = 'Йога для начинающих'),
 (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'trainer.anna'))),
('FRIDAY', '10:30', (SELECT id FROM workout_types WHERE name = 'Пилатес'), 
 (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'trainer.anna'))),
('FRIDAY', '18:00', (SELECT id FROM workout_types WHERE name = 'Функциональный тренинг'), 
 (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'trainer.artem'))),
('FRIDAY', '19:30', (SELECT id FROM workout_types WHERE name = 'Кардио HIIT'), 
 (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'trainer.ekaterina'))),

('SATURDAY', '10:00', (SELECT id FROM workout_types WHERE name = 'Йога для начинающих'),
 (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'trainer.anna'))),
('SATURDAY', '11:30', (SELECT id FROM workout_types WHERE name = 'Танцевальная аэробика'), 
 (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'trainer.victoria'))),
('SATURDAY', '13:00', (SELECT id FROM workout_types WHERE name = 'Кроссфит'), 
 (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'trainer.artem'))),
('SATURDAY', '14:30', (SELECT id FROM workout_types WHERE name = 'Стретчинг'), 
 (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'trainer.anna'))),

('SUNDAY', '10:00', (SELECT id FROM workout_types WHERE name = 'Йога для продвинутых'),
 (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'trainer.anna'))),
('SUNDAY', '11:30', (SELECT id FROM workout_types WHERE name = 'Пилатес'), 
 (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'trainer.anna'))),
('SUNDAY', '13:00', (SELECT id FROM workout_types WHERE name = 'Функциональный тренинг'), 
 (SELECT id FROM trainers WHERE user_id = (SELECT id FROM users WHERE username = 'trainer.maxim')));

INSERT INTO bookings (client_id, schedule_workout_id, workout_date, status) VALUES
((SELECT id FROM clients WHERE user_id = (SELECT id FROM users WHERE username = 'ivan.petrov')),
 (SELECT id FROM schedule_workouts WHERE day_of_week = 'MONDAY' AND start_time = '18:00' LIMIT 1),
 CURRENT_DATE, 'BOOKED'),
((SELECT id FROM clients WHERE user_id = (SELECT id FROM users WHERE username = 'maria.ivanova')), 
 (SELECT id FROM schedule_workouts WHERE day_of_week = 'MONDAY' AND start_time = '19:30' LIMIT 1),
 CURRENT_DATE, 'BOOKED'),
((SELECT id FROM clients WHERE user_id = (SELECT id FROM users WHERE username = 'sergey.sidorov')), 
 (SELECT id FROM schedule_workouts WHERE day_of_week = 'TUESDAY' AND start_time = '18:00' LIMIT 1),
 CURRENT_DATE + INTERVAL '1 day', 'BOOKED'),
((SELECT id FROM clients WHERE user_id = (SELECT id FROM users WHERE username = 'elena.kuznetsova')), 
 (SELECT id FROM schedule_workouts WHERE day_of_week = 'TUESDAY' AND start_time = '10:30' LIMIT 1),
 CURRENT_DATE + INTERVAL '1 day', 'BOOKED'),
((SELECT id FROM clients WHERE user_id = (SELECT id FROM users WHERE username = 'dmitry.volkov')), 
 (SELECT id FROM schedule_workouts WHERE day_of_week = 'WEDNESDAY' AND start_time = '18:00' LIMIT 1),
 CURRENT_DATE + INTERVAL '2 days', 'BOOKED'),
((SELECT id FROM clients WHERE user_id = (SELECT id FROM users WHERE username = 'anna.smirnova')), 
 (SELECT id FROM schedule_workouts WHERE day_of_week = 'WEDNESDAY' AND start_time = '19:30' LIMIT 1),
 CURRENT_DATE + INTERVAL '2 days', 'BOOKED'),
((SELECT id FROM clients WHERE user_id = (SELECT id FROM users WHERE username = 'pavel.novikov')), 
 (SELECT id FROM schedule_workouts WHERE day_of_week = 'THURSDAY' AND start_time = '18:00' LIMIT 1),
 CURRENT_DATE + INTERVAL '3 days', 'BOOKED'),
((SELECT id FROM clients WHERE user_id = (SELECT id FROM users WHERE username = 'olga.kozлова')), 
 (SELECT id FROM schedule_workouts WHERE day_of_week = 'FRIDAY' AND start_time = '09:00' LIMIT 1),
 CURRENT_DATE + INTERVAL '4 days', 'BOOKED'),
((SELECT id FROM clients WHERE user_id = (SELECT id FROM users WHERE username = 'alexey.morozov')), 
 (SELECT id FROM schedule_workouts WHERE day_of_week = 'SATURDAY' AND start_time = '10:00' LIMIT 1),
 CURRENT_DATE + INTERVAL '5 days', 'BOOKED'),
((SELECT id FROM clients WHERE user_id = (SELECT id FROM users WHERE username = 'tatiana.lebedeva')), 
 (SELECT id FROM schedule_workouts WHERE day_of_week = 'SATURDAY' AND start_time = '11:30' LIMIT 1),
 CURRENT_DATE + INTERVAL '5 days', 'BOOKED'),

((SELECT id FROM clients WHERE user_id = (SELECT id FROM users WHERE username = 'ivan.petrov')),
 (SELECT id FROM schedule_workouts WHERE day_of_week = 'MONDAY' AND start_time = '18:00' LIMIT 1),
 CURRENT_DATE - INTERVAL '7 days', 'ATTENDED'),
((SELECT id FROM clients WHERE user_id = (SELECT id FROM users WHERE username = 'maria.ivanova')), 
 (SELECT id FROM schedule_workouts WHERE day_of_week = 'TUESDAY' AND start_time = '19:30' LIMIT 1),
 CURRENT_DATE - INTERVAL '6 days', 'ATTENDED'),
((SELECT id FROM clients WHERE user_id = (SELECT id FROM users WHERE username = 'sergey.sidorov')), 
 (SELECT id FROM schedule_workouts WHERE day_of_week = 'WEDNESDAY' AND start_time = '18:00' LIMIT 1),
 CURRENT_DATE - INTERVAL '5 days', 'ATTENDED'),
((SELECT id FROM clients WHERE user_id = (SELECT id FROM users WHERE username = 'elena.kuznetsova')), 
 (SELECT id FROM schedule_workouts WHERE day_of_week = 'THURSDAY' AND start_time = '10:30' LIMIT 1),
 CURRENT_DATE - INTERVAL '4 days', 'ATTENDED'),
((SELECT id FROM clients WHERE user_id = (SELECT id FROM users WHERE username = 'dmitry.volkov')), 
 (SELECT id FROM schedule_workouts WHERE day_of_week = 'FRIDAY' AND start_time = '18:00' LIMIT 1),
 CURRENT_DATE - INTERVAL '3 days', 'ATTENDED'),

((SELECT id FROM clients WHERE user_id = (SELECT id FROM users WHERE username = 'anna.smirnova')),
 (SELECT id FROM schedule_workouts WHERE day_of_week = 'MONDAY' AND start_time = '19:30' LIMIT 1),
 CURRENT_DATE - INTERVAL '7 days', 'NOT_ATTENDED'),
((SELECT id FROM clients WHERE user_id = (SELECT id FROM users WHERE username = 'pavel.novikov')), 
 (SELECT id FROM schedule_workouts WHERE day_of_week = 'TUESDAY' AND start_time = '18:00' LIMIT 1),
 CURRENT_DATE - INTERVAL '6 days', 'NOT_ATTENDED'),

((SELECT id FROM clients WHERE user_id = (SELECT id FROM users WHERE username = 'olga.kozлова')),
 (SELECT id FROM schedule_workouts WHERE day_of_week = 'WEDNESDAY' AND start_time = '09:00' LIMIT 1),
 CURRENT_DATE - INTERVAL '2 days', 'CANCELLED'),
((SELECT id FROM clients WHERE user_id = (SELECT id FROM users WHERE username = 'alexey.morozov')), 
 (SELECT id FROM schedule_workouts WHERE day_of_week = 'THURSDAY' AND start_time = '10:30' LIMIT 1),
 CURRENT_DATE - INTERVAL '1 day', 'CANCELLED');

COMMIT;
