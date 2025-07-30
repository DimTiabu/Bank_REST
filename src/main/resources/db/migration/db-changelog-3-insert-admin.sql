INSERT INTO bankcards.users (
    id, first_name, last_name, email, phone_number, password, role, created_at
) VALUES (
             gen_random_uuid(),
             'Admin',
             'Adminov',
             'admin@mail.com',
             '89999999999',
             '$2a$10$5H9qjldWZkaByoFX6npTnu9Y6DWK/B0VXs5RvabRC2SO4UeJHrsV.', -- bcrypt от "12345"
             'ROLE_ADMIN',
             now()
         );
