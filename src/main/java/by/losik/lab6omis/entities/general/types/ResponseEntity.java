package by.losik.lab6omis.entities.general.types;

import by.losik.lab6omis.entities.base.BaseEntity;
import by.losik.lab6omis.entities.general.enums.Language;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Сущность, представляющая ответ в системе.
 * Хранит информацию об ответе, включая язык и сообщение.
 *
 * <p>Особенности сущности:</p>
 * <ul>
 *   <li>Использует кэширование Hibernate второго уровня</li>
 *   <li>Находится в схеме "lab6omis"</li>
 *   <li>Содержит валидационные аннотации для обеспечения целостности данных</li>
 * </ul>
 *
 * <p>Валидация:</p>
 * <ul>
 *   <li>Язык ответа обязателен</li>
 *   <li>Сообщение не может быть пустым</li>
 *   <li>Длина сообщения от 1 до 1000 символов</li>
 * </ul>
 *
 * @see BaseEntity
 * @see Language
 * @author Losik Yaroslav
 * @version 1.0
 */
@Entity
@Table(name = "responses", schema = "lab6omis")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "by.losik.lab6omis.entities.general.types.ResponseEntity")
public class ResponseEntity extends BaseEntity {

    /**
     * Язык ответа.
     * Обязательное поле, хранится как строка в базе данных.
     */
    @NotNull(message = "Язык ответа должен быть указан")
    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false)
    private Language language;

    /**
     * Текст сообщения ответа.
     * Обязательное поле с ограничением длины.
     */
    @NotBlank(message = "Сообщение ответа не может быть пустым")
    @Size(min = 1, max = 1000, message = "Сообщение ответа должно содержать от 1 до 1000 символов")
    @Column(name = "message", nullable = false, length = 1000)
    private String message;

    /**
     * Создает новый пустой экземпляр ответа.
     * Требуется JPA для создания экземпляров через рефлексию.
     */
    public ResponseEntity() {
    }

    /**
     * Создает новый экземпляр ответа с указанными параметрами.
     *
     * @param language язык ответа (не может быть null)
     * @param message текст сообщения ответа (не может быть null или пустым)
     */
    public ResponseEntity(Language language, String message) {
        this.language = language;
        this.message = message;
    }

    /**
     * Возвращает язык ответа.
     *
     * @return язык ответа
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * Устанавливает язык ответа.
     *
     * @param language язык ответа (не может быть null)
     */
    public void setLanguage(Language language) {
        this.language = language;
    }

    /**
     * Возвращает текст сообщения ответа.
     *
     * @return текст сообщения
     */
    public String getMessage() {
        return message;
    }

    /**
     * Устанавливает текст сообщения ответа.
     * Длина сообщения должна быть от 1 до 1000 символов.
     *
     * @param message текст сообщения (не может быть null, пустым или превышать 1000 символов)
     */
    public void setMessage(String message) {
        this.message = message;
    }
}