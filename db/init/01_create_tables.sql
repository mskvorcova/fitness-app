CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN ('CLIENT', 'ADMIN', 'TRAINER')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS clients (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    phone VARCHAR(50) UNIQUE NOT NULL,
    registration_date DATE DEFAULT CURRENT_DATE
);

CREATE TABLE IF NOT EXISTS trainers (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    description VARCHAR(1000),
    experience_years INTEGER
);

CREATE TABLE IF NOT EXISTS membership_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(1000),
    duration_days INTEGER NOT NULL CHECK (duration_days > 0),
    price DOUBLE PRECISION NOT NULL CHECK (price >= 0)
);

CREATE TABLE IF NOT EXISTS memberships (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL REFERENCES clients(id) ON DELETE CASCADE,
    membership_type_id BIGINT NOT NULL REFERENCES membership_types(id) ON DELETE RESTRICT,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL CHECK (end_date >= start_date),
    price DOUBLE PRECISION NOT NULL CHECK (price >= 0),
    status VARCHAR(50) NOT NULL CHECK (status IN ('ACTIVE', 'EXPIRED', 'CANCELLED'))
);

CREATE TABLE IF NOT EXISTS workout_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    duration_minutes INTEGER NOT NULL CHECK (duration_minutes > 0),
    hall VARCHAR(255) NOT NULL,
    capacity INTEGER NOT NULL CHECK (capacity > 0),
    description VARCHAR(1000)
);

CREATE TABLE IF NOT EXISTS schedule_workouts (
    id BIGSERIAL PRIMARY KEY,
    day_of_week VARCHAR(50) NOT NULL CHECK (day_of_week IN ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY')),
    start_time TIME NOT NULL,
    workout_type_id BIGINT NOT NULL REFERENCES workout_types(id) ON DELETE CASCADE,
    trainer_id BIGINT NOT NULL REFERENCES trainers(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL REFERENCES clients(id) ON DELETE CASCADE,
    schedule_workout_id BIGINT NOT NULL REFERENCES schedule_workouts(id) ON DELETE CASCADE,
    workout_date TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'BOOKED' CHECK (status IN ('BOOKED', 'ATTENDED', 'NOT_ATTENDED', 'CANCELLED'))
);