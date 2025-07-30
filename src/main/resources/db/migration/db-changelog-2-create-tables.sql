-- Таблица пользователей
CREATE TABLE IF NOT EXISTS bankcards.users (
                                               id UUID PRIMARY KEY,
                                               first_name VARCHAR(100) NOT NULL,
                                               last_name VARCHAR(100) NOT NULL,
                                               email VARCHAR(255) NOT NULL UNIQUE,
                                               phone_number VARCHAR(20),
                                               password VARCHAR(255) NOT NULL,
                                               role VARCHAR(50) NOT NULL,
                                               created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

-- Таблица карт
CREATE TABLE IF NOT EXISTS bankcards.cards (
                                               id UUID PRIMARY KEY,
                                               number VARCHAR(20) NOT NULL UNIQUE,
                                               user_id UUID NOT NULL,
                                               expiration_date DATE NOT NULL,
                                               status VARCHAR(20) NOT NULL,
                                               balance NUMERIC(19,2) NOT NULL,

                                               CONSTRAINT fk_cards_users FOREIGN KEY (user_id)
                                                   REFERENCES bankcards.users(id) ON DELETE CASCADE
);
