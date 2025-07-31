# Система управления банковскими картами

## Описание проекта
REST-сервис для управления банковскими картами с поддержкой JWT-аутентификации, 
фильтрации, переводов между картами и разграничения доступа по ролям.

## Стек используемых технологий
![Static Badge](https://img.shields.io/badge/Java-17-blue)
![Static Badge](https://img.shields.io/badge/Spring_Boot-3.5-green)
![Static Badge](https://img.shields.io/badge/Spring_Security-grey)
![Static Badge](https://img.shields.io/badge/JWT_(JJWT)-grey)
![Static Badge](https://img.shields.io/badge/Spring_Data_JPA-grey)
![Static Badge](https://img.shields.io/badge/Swagger_(OpenAPI)-grey)
![Static Badge](https://img.shields.io/badge/PostgreSQL-grey)
![Static Badge](https://img.shields.io/badge/Apache_Maven-grey)
![Static Badge](https://img.shields.io/badge/Liquibase-grey)
![Static Badge](https://img.shields.io/badge/Mockito-grey)

## Возможности
### Аутентификация и авторизация

- JWT login
- Роли: USER, ADMIN
- Защищённые эндпоинты на основе ролей

### Работа с пользователями

- Создание пользователя 
- Обновление/удаление по ID 
- Получение всех или одного по ID

### Управление картами

- Создание/удаление/блокировка/активация карты
- Получение карты по ID
- Список всех карт (для админа)
- Фильтрация по номеру, статусу и балансу
- Постраничный вывод

### Операции пользователя
- Список карт пользователя
- Переводы между своими картами
- Просмотр баланса карты
- Запрос на блокировку карты

## Быстрый старт

### Предварительные требования:

- Установленный JDK (рекомендуется JDK 17 и выше)
- Установленный Maven
- Docker
- Git
- 
### Шаги для запуска:

1. *Клонирование репозитория:*


```sh
   git clone https://github.com/DimTiabu/Bank_REST.git
```

2. *Переменные окружения*

Создайте файл .env и укажите в нем свои данные для подключения к БД и JWT-secret:

```
POSTGRES_DB=bank_cards_db
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
JWT_SECRET="v3ry_s3cur3_and_l0ng_s3cr3t_k3y_with_32_chr_min"
```

3. *Запуск приложения:*

- через Docker Compose:
```bash
  docker-compose up --build
```
- для локального запуска:
```bash
    mvn spring-boot:run
```
---

## Тестирование
Для запуска всех тестов:
```sh
    mvn clean test
```

## Swagger UI
Документация API доступна по адресу:

👉 http://localhost:8080/swagger-ui/index.html

