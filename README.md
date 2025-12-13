# omis-lr-6

## Технологический стек
- **Java 11** - основной язык
- **JavaEE 8** - стандарты Jakarta EE(ранее Java EE)
- **JAX-RS (Jersey)** - REST API
- **CDI (Weld)** - dependency injection
- **JPA/Hibernate** - ORM и работа с БД
- **Bean Validation** - валидация данных
- **EHCache** - кэширование второго уровня
- **Thymeleaf** - серверный рендеринг
- **PostgreSQL 15** - основная СУБД
- **Apache Tomcat 9** - контейнер сервлетов
- **Catalina** - движок для сервлетов
- **Maven** - сборка и управление зависимостями

## Запуск

### Предварительные требования
- Java 11+ (OpenJDK или Oracle JDK)
- PostgreSQL 15+
- Apache Tomcat 9+
- Maven 3.6+

### Настройка базы данных
```bash
# Создание БД (если не существует, но тогда и в persistence.xml тоже нужно поменять настройки подключения)
createdb -U postgres omis6

# Применение SQL скрипта
psql -U postgres -d omis6 -f src/main/resources/omis6.sql
```

### Сборка WAR файла
```
mvn clean package -DskipTests
```

### Развертка на Tomcat
```
# Копирование WAR файла
cp target/lab6omis.war /path/to/tomcat/webapps/

# Запуск Tomcat
# Linux/Mac
/path/to/tomcat/bin/startup.sh

# Windows
/path/to/tomcat/bin/startup.bat
```

### Проверка доступа
```
curl http://localhost:8080/lab6omis_war_exploded/
```
Можно настроить путь иначе, но это уже на ваш вкус, я стандартно сделал.

### Структура проекта
```
src/main/java/by/losik/lab6omis/
├── entities/          # JPA сущности
├── dto/               # Data Transfer Objects
├── repository/        # Паттерн Repository
├── service/           # Бизнес-логика
├── resource/          # REST API (JAX-RS)
├── persistence/       # Настройка JPA
├── exception/         # Обработка исключений
├── filter/            # Фильтры (Auth, Encoding)
└── servlet/           # Сервлеты (ViewServlet, AuthServlet). Тут же и используется Thymeleaf
```

### Конфигурация
Основные конфигурационные файлы:

- persistence.xml - настройки БД
- web.xml - дескриптор развертывания
- ehcache.xml - конфигурация кэширования
- beans.xml - активация CDI
- context.xml - настройки контекста Tomcat

