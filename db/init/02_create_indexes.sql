CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

CREATE INDEX IF NOT EXISTS idx_memberships_client_status ON memberships(client_id, status);

CREATE INDEX IF NOT EXISTS idx_schedule_workouts_day_of_week ON schedule_workouts(day_of_week);
CREATE INDEX IF NOT EXISTS idx_schedule_workouts_trainer_id ON schedule_workouts(trainer_id);

CREATE INDEX IF NOT EXISTS idx_bookings_client_id ON bookings(client_id);
CREATE INDEX IF NOT EXISTS idx_bookings_schedule_date_status ON bookings(schedule_workout_id, workout_date, status);
CREATE INDEX IF NOT EXISTS idx_bookings_status_workout_date ON bookings(status, workout_date);