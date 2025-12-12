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
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Сущность, представляющая запрос в системе.
 * Хранит информацию о запросе, включая язык, цель и точность распознавания.
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
 *   <li>Язык обязателен</li>
 *   <li>Цель запроса не может быть пустой (5-500 символов)</li>
 *   <li>Точность распознавания обязательна (0.0-100.0, формат XXX.XX)</li>
 * </ul>
 *
 * @see BaseEntity
 * @see Language
 * @author Losik Yaroslav
 * @version 1.0
 */
@Entity
@Table(name = "requests", schema = "lab6omis")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "by.losik.lab6omis.entities.general.types.Request")
public class Request extends BaseEntity {

    /**
     * Язык запроса.
     * Обязательное поле, хранится как строка в базе данных.
     */
    @NotNull(message = "Язык должен быть указан")
    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false)
    private Language language;

    /**
     * Цель запроса.
     * Обязательное поле с ограничением длины от 5 до 500 символов.
     */
    @NotBlank(message = "Цель запроса не может быть пустой")
    @Size(min = 5, max = 500, message = "Цель запроса должна содержать от 5 до 500 символов")
    @Column(name = "goal", nullable = false)
    private String goal;

    /**
     * Точность распознавания запроса.
     * Должна быть в диапазоне от 0.0 до 100.0 включительно.
     * Формат: до 3 целых цифр и до 2 дробных.
     */
    @NotNull(message = "Точность распознавания должна быть указана")
    @DecimalMin(value = "0.0", message = "Точность распознавания не может быть меньше 0")
    @DecimalMax(value = "100.0", message = "Точность распознавания не может превышать 100")
    @Digits(integer = 3, fraction = 2, message = "Точность распознавания должна быть в формате XXX.XX")
    @Column(name = "recognition_accuracy", nullable = false)
    private Double recognitionAccuracy;

    /**
     * Создает новый пустой экземпляр запроса.
     * Требуется JPA для создания экземпляров через рефлексию.
     */
    public Request() {
    }

    /**
     * Создает новый экземпляр запроса с указанными параметрами.
     *
     * @param language язык запроса (не может быть null)
     * @param goal цель запроса (не может быть null, пустым, длина 5-500 символов)
     * @param recognitionAccuracy точность распознавания (не может быть null, 0.0-100.0)
     */
    public Request(Language language, String goal, Double recognitionAccuracy) {
        this.language = language;
        this.goal = goal;
        this.recognitionAccuracy = recognitionAccuracy;
    }

    /**
     * Возвращает язык запроса.
     *
     * @return язык запроса
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * Устанавливает язык запроса.
     *
     * @param language язык запроса (не может быть null)
     */
    public void setLanguage(Language language) {
        this.language = language;
    }

    /**
     * Возвращает цель запроса.
     *
     * @return цель запроса
     */
    public String getGoal() {
        return goal;
    }

    /**
     * Устанавливает цель запроса.
     * Длина должна быть от 5 до 500 символов.
     *
     * @param goal цель запроса (не может быть null, пустой или выходить за пределы длины)
     */
    public void setGoal(String goal) {
        this.goal = goal;
    }

    /**
     * Возвращает точность распознавания запроса.
     *
     * @return точность распознавания (0.0-100.0)
     */
    public Double getRecognitionAccuracy() {
        return recognitionAccuracy;
    }

    /**
     * Устанавливает точность распознавания запроса.
     * Должна быть в диапазоне от 0.0 до 100.0 включительно.
     *
     * @param recognitionAccuracy точность распознавания (не может быть null, 0.0-100.0)
     */
    public void setRecognitionAccuracy(Double recognitionAccuracy) {
        this.recognitionAccuracy = recognitionAccuracy;
    }
}