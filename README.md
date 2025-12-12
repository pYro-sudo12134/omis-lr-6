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
- **Maven** - сборка и управление зависимостями

## Быстрый запуск

### 1. Предварительные требования
- Java 11+ (OpenJDK или Oracle JDK)
- PostgreSQL 15+
- Apache Tomcat 9+
- Maven 3.6+

### 2. Настройка базы данных
```bash
# Создание БД (если не существует)
createdb -U postgres omis6

# Применение SQL скрипта
psql -U postgres -d omis6 -f src/main/resources/omis6.sql
```

### Сборка приложения
```bash
# Сборка WAR файла
mvn clean package -DskipTests
```

### Развертывание на Tomcat
```bash
# Копирование WAR файла
cp target/lab6omis.war /path/to/tomcat/webapps/

# Запуск Tomcat
# Linux/Mac
/path/to/tomcat/bin/startup.sh

# Windows
/path/to/tomcat/bin/startup.bat
```

### 5. Доступ к приложению
Откройте в браузере: [http://localhost:8080/lab6omis](http://localhost:8080/lab6omis)

## Структура проекта
```
src/main/java/by/losik/lab6omis/
├── entities/           # JPA сущности
├── dto/               # Data Transfer Objects
├── repository/        # Паттерн Repository
├── service/           # Бизнес-логика
├── resource/          # REST API (JAX-RS)
├── controller/        # Thymeleaf контроллеры
├── persistence/       # Настройка JPA
└── exception/         # Обработка исключений
```

## Конфигурация
Основные конфигурационные файлы:
- `src/main/webapp/WEB-INF/persistence.xml` - настройки БД
- `src/main/webapp/WEB-INF/web.xml` - дескриптор развертывания
- `src/main/resources/ehcache.xml` - конфигурация кэширования
- `src/main/webapp/WEB-INF/beans.xml` - активация CDI

Для подключения к своей БД отредактируйте `persistence.xml`:
```xml
<property name="javax.persistence.jdbc.url" 
          value="jdbc:postgresql://localhost:5432/omis6"/>
<property name="javax.persistence.jdbc.user" value="your_username"/>
<property name="javax.persistence.jdbc.password" value="your_password"/>
```
