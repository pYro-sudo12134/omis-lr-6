package by.losik.lab6omis.entities.general.types;

import by.losik.lab6omis.entities.base.BaseEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Сущность, представляющая звук в системе.
 * Хранит информацию о типе шума и его частоте.
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
 *   <li>Тип шума обязателен (2-100 символов)</li>
 *   <li>Частота обязательна (1-200000 Гц)</li>
 * </ul>
 *
 * <p>Пример использования:</p>
 * <pre>
 * Sound sound = new Sound("Шум ветра", 1500);
 * sound.setNoise("Шум моря");
 * sound.setFrequency(2000);
 * </pre>
 *
 * @see BaseEntity
 * @author Losik Yaroslav
 * @version 1.0
 */
@Entity
@Table(name = "sounds", schema = "lab6omis")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "by.losik.lab6omis.entities.general.types.Sound")
public class Sound extends BaseEntity {

    /**
     * Тип шума (например: "Шум ветра", "Шум моря", "Промышленный шум").
     * Обязательное поле с ограничением длины от 2 до 100 символов.
     */
    @NotBlank(message = "Тип шума не может быть пустым")
    @Size(min = 2, max = 100, message = "Тип шума должен содержать от 2 до 100 символов")
    @Column(name = "noise", nullable = false)
    private String noise;

    /**
     * Частота звука в герцах (Гц).
     * Обязательное поле с ограничением диапазона от 1 до 200000 Гц.
     * Охватывает диапазон от инфразвука до ультразвука.
     */
    @NotNull(message = "Частота не может быть null")
    @Min(value = 1, message = "Частота должна быть не менее 1 Гц")
    @Max(value = 200000, message = "Частота не должна превышать 200000 Гц")
    @Column(name = "frequency", nullable = false)
    private Integer frequency;

    /**
     * Создает новый пустой экземпляр звука.
     * Требуется JPA для создания экземпляров через рефлексию.
     */
    public Sound() {
    }

    /**
     * Создает новый экземпляр звука с указанными параметрами.
     *
     * @param noise тип шума (не может быть null, пустым, длина 2-100 символов)
     * @param frequency частота звука (не может быть null, 1-200000 Гц)
     */
    public Sound(String noise, Integer frequency) {
        this.noise = noise;
        this.frequency = frequency;
    }

    /**
     * Возвращает тип шума.
     *
     * @return тип шума
     */
    public String getNoise() {
        return noise;
    }

    /**
     * Устанавливает тип шума.
     * Длина должна быть от 2 до 100 символов.
     *
     * @param noise тип шума (не может быть null, пустым или выходить за пределы длины)
     */
    public void setNoise(String noise) {
        this.noise = noise;
    }

    /**
     * Возвращает частоту звука в герцах.
     *
     * @return частота звука в Гц
     */
    public Integer getFrequency() {
        return frequency;
    }

    /**
     * Устанавливает частоту звука.
     * Должна быть в диапазоне от 1 до 200000 Гц включительно.
     *
     * @param frequency частота звука (не может быть null, 1-200000 Гц)
     */
    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }
}