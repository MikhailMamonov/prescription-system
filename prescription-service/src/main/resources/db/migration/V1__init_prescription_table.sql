CREATE TABLE prescriptions (
    id VARCHAR(50) PRIMARY KEY,          -- UUID рецепта
    patient_id VARCHAR(50) NOT NULL,     -- UUID пациента
    doctor_id VARCHAR(50) NOT NULL,      -- UUID врача
    medication VARCHAR(255) NOT NULL,    -- Название или код лекарства
    status VARCHAR(30) NOT NULL,         -- Статус (ISSUED, CONFIRMED, CANCELLED)
    created_at TIMESTAMP NOT NULL,       -- Дата выписки
    expires_at TIMESTAMP NOT NULL        -- Срок действия рецепта
);