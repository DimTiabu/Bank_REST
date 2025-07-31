INSERT INTO bankcards.users (
    id, first_name, last_name, email, phone_number, password, role, created_at
) VALUES (
             gen_random_uuid(),
             'Admin',
             'Adminov',
             'admin@mail.com',
             '89999999999',
             '$2a$10$RsAl5ilGctxuHtu0sJPRi.2ZV3yafzFHIRSW9kDorVJs.UiWy8e3i', -- bcrypt от "12345"
             'ROLE_ADMIN',
             now()
         );
