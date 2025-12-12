CREATE SCHEMA IF NOT EXISTS lab6omis;
SET search_path TO lab6omis;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE SEQUENCE IF NOT EXISTS lab6omis.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS lab6omis.requests (
     id BIGINT PRIMARY KEY,
     created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
     modified_date TIMESTAMP,
     language VARCHAR(20) NOT NULL,
     goal TEXT NOT NULL,
     recognition_accuracy DECIMAL(5,2) NOT NULL,

     CONSTRAINT requests_recognition_accuracy_check
         CHECK (recognition_accuracy >= 0.0 AND recognition_accuracy <= 100.0),
     CONSTRAINT requests_language_check
         CHECK (language IN ('RU', 'EN', 'DE', 'FR', 'ES', 'ZH')),
     CONSTRAINT requests_goal_length_check
         CHECK (LENGTH(goal) >= 5 AND LENGTH(goal) <= 500)
);

CREATE INDEX IF NOT EXISTS idx_requests_language ON lab6omis.requests(language);
CREATE INDEX IF NOT EXISTS idx_requests_accuracy ON lab6omis.requests(recognition_accuracy);
CREATE INDEX IF NOT EXISTS idx_requests_created_date ON lab6omis.requests(created_date);

COMMENT ON TABLE lab6omis.requests IS 'Таблица запросов пользователей';
COMMENT ON COLUMN lab6omis.requests.language IS 'Язык запроса';
COMMENT ON COLUMN lab6omis.requests.goal IS 'Цель запроса';
COMMENT ON COLUMN lab6omis.requests.recognition_accuracy IS 'Точность распознавания (0-100%)';

CREATE TABLE IF NOT EXISTS lab6omis.response (
    id BIGINT PRIMARY KEY,
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP,
    language VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,

    CONSTRAINT responses_language_check
      CHECK (language IN ('RU', 'EN', 'DE', 'FR', 'ES', 'ZH')),
    CONSTRAINT responses_message_length_check
      CHECK (LENGTH(message) >= 1 AND LENGTH(message) <= 1000)
);

CREATE INDEX IF NOT EXISTS idx_responses_language ON lab6omis.response(language);
CREATE INDEX IF NOT EXISTS idx_responses_created_date ON lab6omis.response(created_date);
CREATE INDEX IF NOT EXISTS idx_responses_message_length ON lab6omis.response(LENGTH(message));

COMMENT ON TABLE lab6omis.response IS 'Таблица ответов системы';
COMMENT ON COLUMN lab6omis.response.message IS 'Сообщение ответа';

CREATE TABLE IF NOT EXISTS lab6omis.solutions (
      id BIGINT PRIMARY KEY,
      created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
      modified_date TIMESTAMP,
      language VARCHAR(20) NOT NULL,
      message TEXT NOT NULL,

      CONSTRAINT solutions_language_check
          CHECK (language IN ('RU', 'EN', 'DE', 'FR', 'ES', 'ZH')),
      CONSTRAINT solutions_message_length_check
          CHECK (LENGTH(message) >= 10 AND LENGTH(message) <= 2000)
);

CREATE INDEX IF NOT EXISTS idx_solutions_language ON lab6omis.solutions(language);
CREATE INDEX IF NOT EXISTS idx_solutions_created_date ON lab6omis.solutions(created_date);

COMMENT ON TABLE lab6omis.solutions IS 'Таблица решений';
COMMENT ON COLUMN lab6omis.solutions.message IS 'Сообщение решения';

CREATE TABLE IF NOT EXISTS lab6omis.sounds (
   id BIGINT PRIMARY KEY,
   created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   modified_date TIMESTAMP,
   noise VARCHAR(100) NOT NULL,
   frequency INTEGER NOT NULL,

   CONSTRAINT sounds_frequency_check
       CHECK (frequency >= 1 AND frequency <= 200000),
   CONSTRAINT sounds_noise_length_check
       CHECK (LENGTH(noise) >= 2 AND LENGTH(noise) <= 100)
);

CREATE INDEX IF NOT EXISTS idx_sounds_noise ON lab6omis.sounds(noise);
CREATE INDEX IF NOT EXISTS idx_sounds_frequency ON lab6omis.sounds(frequency);
CREATE INDEX IF NOT EXISTS idx_sounds_created_date ON lab6omis.sounds(created_date);

COMMENT ON TABLE lab6omis.sounds IS 'Таблица звуков/шумов';
COMMENT ON COLUMN lab6omis.sounds.noise IS 'Тип шума';
COMMENT ON COLUMN lab6omis.sounds.frequency IS 'Частота в Гц (1-200000)';

CREATE TABLE IF NOT EXISTS lab6omis.sensors (
    id BIGINT PRIMARY KEY,
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    location VARCHAR(200),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT sensors_name_length_check
        CHECK (LENGTH(name) >= 2 AND LENGTH(name) <= 100),
    CONSTRAINT sensors_type_length_check
        CHECK (LENGTH(type) >= 2 AND LENGTH(type) <= 50),
    CONSTRAINT sensors_location_length_check
        CHECK (location IS NULL OR LENGTH(location) <= 200),

    CONSTRAINT sensors_name_unique UNIQUE (name)
);

CREATE INDEX IF NOT EXISTS idx_sensors_name ON lab6omis.sensors(name);
CREATE INDEX IF NOT EXISTS idx_sensors_type ON lab6omis.sensors(type);
CREATE INDEX IF NOT EXISTS idx_sensors_location ON lab6omis.sensors(location);
CREATE INDEX IF NOT EXISTS idx_sensors_active ON lab6omis.sensors(is_active);
CREATE INDEX IF NOT EXISTS idx_sensors_created_date ON lab6omis.sensors(created_date);

COMMENT ON TABLE lab6omis.sensors IS 'Таблица сенсоров';
COMMENT ON COLUMN lab6omis.sensors.name IS 'Имя сенсора';
COMMENT ON COLUMN lab6omis.sensors.type IS 'Тип сенсора';
COMMENT ON COLUMN lab6omis.sensors.location IS 'Локация сенсора';
COMMENT ON COLUMN lab6omis.sensors.is_active IS 'Статус активности';

CREATE TABLE IF NOT EXISTS lab6omis.sensor_data (
    id BIGINT PRIMARY KEY,
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP,
    timestamp TIMESTAMP NOT NULL,
    purpose TEXT NOT NULL,
    sensor_id BIGINT NOT NULL,

    CONSTRAINT sensor_data_purpose_length_check
        CHECK (LENGTH(purpose) >= 3 AND LENGTH(purpose) <= 500),
    CONSTRAINT sensor_data_timestamp_check
        CHECK (timestamp <= CURRENT_TIMESTAMP),

    CONSTRAINT fk_sensor_data_sensor
        FOREIGN KEY (sensor_id)
            REFERENCES lab6omis.sensors(id)
            ON DELETE CASCADE
            ON UPDATE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_sensor_data_sensor_id ON lab6omis.sensor_data(sensor_id);
CREATE INDEX IF NOT EXISTS idx_sensor_data_timestamp ON lab6omis.sensor_data(timestamp);
CREATE INDEX IF NOT EXISTS idx_sensor_data_purpose ON lab6omis.sensor_data(purpose);
CREATE INDEX IF NOT EXISTS idx_sensor_data_created_date ON lab6omis.sensor_data(created_date);
CREATE INDEX IF NOT EXISTS idx_sensor_data_timestamp_sensor
    ON lab6omis.sensor_data(timestamp, sensor_id);

COMMENT ON TABLE lab6omis.sensor_data IS 'Таблица данных сенсоров';
COMMENT ON COLUMN lab6omis.sensor_data.timestamp IS 'Временная метка данных';
COMMENT ON COLUMN lab6omis.sensor_data.purpose IS 'Назначение данных';
COMMENT ON COLUMN lab6omis.sensor_data.sensor_id IS 'Ссылка на сенсор';

CREATE OR REPLACE FUNCTION lab6omis.update_modified_date()
    RETURNS TRIGGER AS $$
BEGIN
        NEW.modified_date = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_requests_modified_date
    BEFORE UPDATE ON lab6omis.requests
    FOR EACH ROW
EXECUTE FUNCTION lab6omis.update_modified_date();

CREATE TRIGGER trg_update_responses_modified_date
    BEFORE UPDATE ON lab6omis.response
    FOR EACH ROW
EXECUTE FUNCTION lab6omis.update_modified_date();

CREATE TRIGGER trg_update_solutions_modified_date
    BEFORE UPDATE ON lab6omis.solutions
    FOR EACH ROW
EXECUTE FUNCTION lab6omis.update_modified_date();

CREATE TRIGGER trg_update_sounds_modified_date
    BEFORE UPDATE ON lab6omis.sounds
    FOR EACH ROW
EXECUTE FUNCTION lab6omis.update_modified_date();

CREATE TRIGGER trg_update_sensors_modified_date
    BEFORE UPDATE ON lab6omis.sensors
    FOR EACH ROW
EXECUTE FUNCTION lab6omis.update_modified_date();

CREATE TRIGGER trg_update_sensor_data_modified_date
    BEFORE UPDATE ON lab6omis.sensor_data
    FOR EACH ROW
EXECUTE FUNCTION lab6omis.update_modified_date();

CREATE OR REPLACE VIEW lab6omis.active_sensors_with_data AS
SELECT
    s.id,
    s.name,
    s.type,
    s.location,
    s.is_active,
    s.created_date,
    COUNT(sd.id) as data_count,
    MAX(sd.timestamp) as last_data_timestamp
FROM lab6omis.sensors s
         LEFT JOIN lab6omis.sensor_data sd ON s.id = sd.sensor_id
WHERE s.is_active = TRUE
GROUP BY s.id, s.name, s.type, s.location, s.is_active, s.created_date;

CREATE OR REPLACE VIEW lab6omis.language_statistics AS
SELECT
    'requests' as source,
    language,
    COUNT(*) as count,
    AVG(recognition_accuracy) as avg_accuracy
FROM lab6omis.requests
GROUP BY language
UNION ALL
SELECT
    'response' as source,
    language,
    COUNT(*) as count,
    NULL as avg_accuracy
FROM lab6omis.response
GROUP BY language
UNION ALL
SELECT
    'solutions' as source,
    language,
    COUNT(*) as count,
    NULL as avg_accuracy
FROM lab6omis.solutions
GROUP BY language;

CREATE OR REPLACE VIEW lab6omis.sensor_data_detailed AS
SELECT
    sd.*,
    s.name as sensor_name,
    s.type as sensor_type,
    s.location as sensor_location,
    s.is_active as sensor_active
FROM lab6omis.sensor_data sd
         JOIN lab6omis.sensors s ON sd.sensor_id = s.id;