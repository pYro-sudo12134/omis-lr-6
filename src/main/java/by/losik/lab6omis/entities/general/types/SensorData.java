package by.losik.lab6omis.entities.general.types;

import by.losik.lab6omis.entities.base.BaseEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Сущность, представляющая данные, собранные сенсором.
 * Хранит информацию о временной метке, назначении данных и ссылку на родительский сенсор.
 *
 * <p>Особенности сущности:</p>
 * <ul>
 *   <li>Использует кэширование Hibernate второго уровня</li>
 *   <li>Находится в схеме "lab6omis"</li>
 *   <li>Содержит валидационные аннотации для обеспечения целостности данных</li>
 *   <li>Имеет связь "многие к одному" с сущностью Sensor</li>
 * </ul>
 *
 * <p>Валидация:</p>
 * <ul>
 *   <li>Временная метка обязательна и не может быть в будущем</li>
 *   <li>Назначение данных обязательно (3-500 символов)</li>
 *   <li>Сенсор обязателен</li>
 * </ul>
 *
 * @see BaseEntity
 * @see Sensor
 * @author Losik Yaroslav
 * @version 1.0
 */
@Entity
@Table(name = "sensor_data", schema = "lab6omis")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "by.losik.lab6omis.entities.general.types.SensorData")
public class SensorData extends BaseEntity {

    /**
     * Временная метка сбора данных.
     * Обязательное поле, не может быть в будущем времени.
     */
    @NotNull(message = "Временная метка не может быть null")
    @PastOrPresent(message = "Временная метка не может быть в будущем")
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    /**
     * Назначение или описание собранных данных.
     * Обязательное поле с ограничением длины от 3 до 500 символов.
     */
    @NotBlank(message = "Назначение данных не может быть пустым")
    @Size(min = 3, max = 500, message = "Назначение данных должно содержать от 3 до 500 символов")
    @Column(name = "purpose", nullable = false)
    private String purpose;

    /**
     * Сенсор, собравший данные.
     * Обязательное поле, используется ленивая загрузка для оптимизации производительности.
     */
    @NotNull(message = "Сенсор должен быть указан")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;

    /**
     * Создает новый пустой экземпляр данных сенсора.
     * Требуется JPA для создания экземпляров через рефлексию.
     */
    public SensorData() {
    }

    /**
     * Создает новый экземпляр данных сенсора с указанными параметрами.
     *
     * @param timestamp временная метка (не может быть null или в будущем)
     * @param purpose назначение данных (не может быть null, пустым, длина 3-500 символов)
     * @param sensor сенсор (не может быть null)
     */
    public SensorData(LocalDateTime timestamp, String purpose, Sensor sensor) {
        this.timestamp = timestamp;
        this.purpose = purpose;
        this.sensor = sensor;
    }

    /**
     * Возвращает временную метку сбора данных.
     *
     * @return временная метка
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Устанавливает временную метку сбора данных.
     * Не может быть в будущем времени.
     *
     * @param timestamp временная метка (не может быть null или в будущем)
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Возвращает назначение собранных данных.
     *
     * @return назначение данных
     */
    public String getPurpose() {
        return purpose;
    }

    /**
     * Устанавливает назначение собранных данных.
     * Длина должна быть от 3 до 500 символов.
     *
     * @param purpose назначение данных (не может быть null, пустым или выходить за пределы длины)
     */
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    /**
     * Возвращает сенсор, собравший данные.
     *
     * @return сенсор
     */
    public Sensor getSensor() {
        return sensor;
    }

    /**
     * Устанавливает сенсор, собравший данные.
     * Выполняет управление двунаправленной связью с сущностью Sensor.
     *
     * @param sensor сенсор (не может быть null)
     */
    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }
}