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
 * Сущность, представляющая решение в системе.
 * Хранит информацию о решении, включая язык и сообщение решения.
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
 *   <li>Язык решения обязателен</li>
 *   <li>Сообщение решения не может быть пустым (10-2000 символов)</li>
 * </ul>
 *
 * @see BaseEntity
 * @see Language
 * @author Losik Yaroslav
 * @version 1.0
 */
@Entity
@Table(name = "solutions", schema = "lab6omis")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "by.losik.lab6omis.entities.general.types.Solution")
public class Solution extends BaseEntity {

    /**
     * Язык решения.
     * Обязательное поле, хранится как строка в базе данных.
     */
    @NotNull(message = "Язык решения должен быть указан")
    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false)
    private Language language;

    /**
     * Текст сообщения решения.
     * Обязательное поле с ограничением длины от 10 до 2000 символов.
     */
    @NotBlank(message = "Сообщение решения не может быть пустым")
    @Size(min = 10, max = 2000, message = "Сообщение решения должно содержать от 10 до 2000 символов")
    @Column(name = "message", nullable = false, length = 2000)
    private String message;

    /**
     * Создает новый пустой экземпляр решения.
     * Требуется JPA для создания экземпляров через рефлексию.
     */
    public Solution() {
    }

    /**
     * Создает новый экземпляр решения с указанными параметрами.
     *
     * @param language язык решения (не может быть null)
     * @param message текст сообщения решения (не может быть null, пустым, длина 10-2000 символов)
     */
    public Solution(Language language, String message) {
        this.language = language;
        this.message = message;
    }

    /**
     * Возвращает язык решения.
     *
     * @return язык решения
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * Устанавливает язык решения.
     *
     * @param language язык решения (не может быть null)
     */
    public void setLanguage(Language language) {
        this.language = language;
    }

    /**
     * Возвращает текст сообщения решения.
     *
     * @return текст сообщения
     */
    public String getMessage() {
        return message;
    }

    /**
     * Устанавливает текст сообщения решения.
     * Длина сообщения должна быть от 10 до 2000 символов.
     *
     * @param message текст сообщения (не может быть null, пустым или превышать 2000 символов)
     */
    public void setMessage(String message) {
        this.message = message;
    }
}