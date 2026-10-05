SET NAMES 'utf8mb4';
CREATE DATABASE IF NOT EXISTS pharmacy CHARACTER SET utf8mb4;
USE pharmacy;

CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    phone VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE bookings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    medicines TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    CHECK (status IN ('ACCEPTED','ASSEMBLING','ASSEMBLED','PURCHASED','CANCELLED'))
);

INSERT INTO users (phone, name) VALUES
('+79001112233', 'Иванов Иван'),
('+79002223344', 'Петрова Мария'),
('+79003334455', 'Сидоров Пётр'),
('+79004445566', 'Кузнецова Анна'),
('+79005556677', 'Смирнов Олег');

INSERT INTO bookings (user_id, medicines, status, created_at) VALUES
(1, 'Парацетамол 500мг x2, Аспирин x1', 'ACCEPTED',   '2026-09-01 10:00:00'),
(2, 'Ибупрофен 200мг x1',                'ASSEMBLING', '2026-09-02 11:00:00'),
(3, 'Амоксициллин 500мг x3',             'ASSEMBLED',  '2026-09-03 12:00:00'),
(4, 'Витамин C 500мг x2',                'PURCHASED',  '2026-09-04 13:00:00'),
(5, 'Аспирин x1, Нурофен x2',            'CANCELLED',  '2026-09-05 14:00:00'),
(1, 'Лоратадин 10мг x1',                 'ACCEPTED',   '2026-09-06 15:00:00'),
(2, 'Омепразол 20мг x2',                 'ASSEMBLING', '2026-09-07 16:00:00'),
(3, 'Магне B6 x1',                       'ASSEMBLED',  '2026-09-08 17:00:00'),
(4, 'Парацетамол x3, Аспирин x2',        'PURCHASED',  '2026-09-09 18:00:00'),
(5, 'Нафтизин x1',                       'CANCELLED',  '2026-09-10 19:00:00'),
(1, 'Корвалол x1',                       'ACCEPTED',   '2026-09-11 20:00:00'),
(2, 'Валерьянка x2',                     'ASSEMBLING', '2026-09-12 21:00:00');