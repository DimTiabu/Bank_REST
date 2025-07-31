-- Таблица запросов на блокировку карт
CREATE TABLE IF NOT EXISTS bankcards.card_block_requests (
                                                             id UUID PRIMARY KEY,
                                                             user_id UUID NOT NULL,
                                                             card_id UUID NOT NULL,
                                                             requested_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                                             status VARCHAR(20) NOT NULL,

                                                             CONSTRAINT fk_card_block_requests_users FOREIGN KEY (user_id)
                                                                 REFERENCES bankcards.users(id) ON DELETE CASCADE,

                                                             CONSTRAINT fk_card_block_requests_cards FOREIGN KEY (card_id)
                                                                 REFERENCES bankcards.cards(id) ON DELETE CASCADE
);

-- Индекс для ускорения поиска по карте и статусу запроса
CREATE INDEX IF NOT EXISTS idx_card_block_requests_card_status
    ON bankcards.card_block_requests (card_id, status);
