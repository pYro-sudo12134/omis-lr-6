package by.losik.lab6omis.entities.base;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Базовый класс для всех сущностей JPA.
 * Предоставляет общее поле идентификатора (ID) с автоматической генерацией.
 *
 * <p>Особенности:</p>
 * <ul>
 *   <li>Использует стратегию генерации IDENTITY</li>
 *   <li>Может быть унаследован любыми сущностями JPA</li>
 *   <li>Не является самостоятельной сущностью (@MappedSuperclass)</li>
 * </ul>
 *
 * <p>Пример использования:</p>
 * <pre>
 * {@code
 * @Entity
 * public class User extends BaseEntity {
 *     // поля и методы
 * }
 * }
 * </pre>
 *
 * @see javax.persistence.MappedSuperclass
 * @see javax.persistence.Id
 * @see javax.persistence.GeneratedValue
 *
 * @author Losik Yaroslav
 * @version 1.0
 */
@MappedSuperclass
public abstract class BaseEntity {

    /**
     * Уникальный идентификатор сущности.
     * Генерируется автоматически базой данных при сохранении новой сущности.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Возвращает уникальный идентификатор сущности.
     *
     * @return идентификатор сущности или null, если сущность еще не сохранена
     */
    public Long getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор сущности.
     * Обычно используется только фреймворками, так как генерация ID автоматическая.
     *
     * @param id идентификатор сущности
     */
    public void setId(Long id) {
        this.id = id;
    }
}