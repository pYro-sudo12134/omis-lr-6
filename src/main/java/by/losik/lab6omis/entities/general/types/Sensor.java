package by.losik.lab6omis.entities.general.types;

import by.losik.lab6omis.entities.base.BaseEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность, представляющая сенсор в системе.
 * Хранит информацию о сенсоре, включая его имя, тип, локацию и статус активности.
 * Содержит связь "один ко многим" с данными сенсора.
 *
 * <p>Особенности сущности:</p>
 * <ul>
 *   <li>Использует кэширование Hibernate второго уровня</li>
 *   <li>Находится в схеме "lab6omis"</li>
 *   <li>Содержит валидационные аннотации</li>
 *   <li>Управляет коллекцией данных сенсора через каскадные операции</li>
 * </ul>
 *
 * <p>Валидация:</p>
 * <ul>
 *   <li>Имя сенсора обязательно (2-100 символов)</li>
 *   <li>Тип сенсора обязателен (2-50 символов)</li>
 *   <li>Локация опциональна (до 200 символов)</li>
 *   <li>Статус активности обязателен</li>
 * </ul>
 *
 * @see BaseEntity
 * @see SensorData
 * @author Losik Yaroslav
 * @version 1.0
 */
@Entity
@Table(name = "sensors", schema = "lab6omis")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "by.losik.lab6omis.entities.general.types.Sensor")
public class Sensor extends BaseEntity {

    /**
     * Название сенсора.
     * Обязательное поле с ограничением длины от 2 до 100 символов.
     */
    @NotBlank(message = "Имя сенсора не может быть пустым")
    @Size(min = 2, max = 100, message = "Имя сенсора должно содержать от 2 до 100 символов")
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Тип сенсора (например: "Температурный", "Давления", "Влажности").
     * Обязательное поле с ограничением длины от 2 до 50 символов.
     */
    @NotBlank(message = "Тип сенсора не может быть пустым")
    @Size(min = 2, max = 50, message = "Тип сенсора должен содержать от 2 до 50 символов")
    @Column(name = "type", nullable = false)
    private String type;

    /**
     * Локация установки сенсора.
     * Опциональное поле с ограничением длины до 200 символов.
     */
    @Size(max = 200, message = "Локация не должна превышать 200 символов")
    @Column(name = "location")
    private String location;

    /**
     * Статус активности сенсора.
     * Обязательное поле, по умолчанию true (активен).
     */
    @NotNull(message = "Статус активности должен быть указан")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * Коллекция данных, собранных сенсором.
     * Связь "один ко многим" с каскадными операциями и автоматическим удалением orphans.
     */
    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL, orphanRemoval = true)
    @Valid
    private List<SensorData> sensorDataList = new ArrayList<>();

    /**
     * Создает новый пустой экземпляр сенсора.
     * Требуется JPA для создания экземпляров через рефлексию.
     */
    public Sensor() {
    }

    /**
     * Создает новый экземпляр сенсора с указанными параметрами.
     *
     * @param name имя сенсора (не может быть null, пустым, длина 2-100 символов)
     * @param type тип сенсора (не может быть null, пустым, длина 2-50 символов)
     * @param location локация сенсора (может быть null, до 200 символов)
     */
    public Sensor(String name, String type, String location) {
        this.name = name;
        this.type = type;
        this.location = location;
    }

    /**
     * Добавляет данные сенсора в коллекцию и устанавливает обратную ссылку.
     * Выполняет управление двунаправленной связью.
     *
     * @param sensorData данные сенсора для добавления (не может быть null)
     */
    public void addSensorData(SensorData sensorData) {
        sensorData.setSensor(this);
        sensorDataList.add(sensorData);
    }

    /**
     * Удаляет данные сенсора из коллекции и очищает обратную ссылку.
     * Выполняет управление двунаправленной связью.
     *
     * @param sensorData данные сенсора для удаления (не может быть null)
     */
    public void removeSensorData(SensorData sensorData) {
        sensorDataList.remove(sensorData);
        sensorData.setSensor(null);
    }

    /**
     * Возвращает имя сенсора.
     *
     * @return имя сенсора
     */
    public String getName() {
        return name;
    }

    /**
     * Устанавливает имя сенсора.
     * Длина должна быть от 2 до 100 символов.
     *
     * @param name имя сенсора (не может быть null, пустым или выходить за пределы длины)
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Возвращает тип сенсора.
     *
     * @return тип сенсора
     */
    public String getType() {
        return type;
    }

    /**
     * Устанавливает тип сенсора.
     * Длина должна быть от 2 до 50 символов.
     *
     * @param type тип сенсора (не может быть null, пустым или выходить за пределы длины)
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Возвращает локацию сенсора.
     *
     * @return локация сенсора или null, если не установлена
     */
    public String getLocation() {
        return location;
    }

    /**
     * Устанавливает локацию сенсора.
     * Длина не должна превышать 200 символов.
     *
     * @param location локация сенсора (может быть null, до 200 символов)
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Возвращает статус активности сенсора.
     *
     * @return true если сенсор активен, false в противном случае
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Устанавливает статус активности сенсора.
     *
     * @param isActive статус активности (не может быть null)
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Возвращает список данных сенсора.
     *
     * @return неизменяемый список данных сенсора (может быть пустым, но не null)
     */
    public List<SensorData> getSensorDataList() {
        return sensorDataList;
    }

    /**
     * Устанавливает список данных сенсора.
     * Заменяет существующую коллекцию. Для постепенного добавления используйте {@link #addSensorData(SensorData)}.
     *
     * @param sensorDataList список данных сенсора (не может быть null)
     */
    public void setSensorDataList(List<SensorData> sensorDataList) {
        this.sensorDataList = sensorDataList;
    }
}