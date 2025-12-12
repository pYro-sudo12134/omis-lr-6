package by.losik.lab6omis.persistence;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Менеджер транзакций для управления операциями с базой данных.
 * Предоставляет удобные методы для выполнения запросов и транзакций
 * с автоматическим управлением жизненным циклом {@link EntityManager}.
 *
 * <p>Основные функции:</p>
 * <ul>
 *   <li>Выполнение запросов к базе данных</li>
 *   <li>Управление транзакциями с автоматическим commit/rollback</li>
 *   <li>Проверка существования сущностей</li>
 *   <li>Автоматическое управление ресурсами EntityManager</li>
 * </ul>
 *
 * @see EntityManager
 * @see EntityTransaction
 * @see ApplicationScoped
 */
@ApplicationScoped
public class TransactionManager {

    @Inject
    private EntityManagerFactory emf;

    /**
     * Выполняет операцию чтения (запрос) без транзакции.
     * Создает новый EntityManager, выполняет операцию и автоматически закрывает его.
     * Подходит для операций чтения, которые не требуют изменения данных.
     *
     * @param <R> тип возвращаемого значения
     * @param operation функция, содержащая логику запроса к базе данных
     * @return результат выполнения операции
     * @throws PersistenceException если операция завершилась с ошибкой
     */
    public <R> R executeQuery(Function<EntityManager, R> operation) {
        EntityManager em = emf.createEntityManager();
        try {
            return operation.apply(em);
        } catch (Exception e) {
            throw new PersistenceException("Query failed", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Выполняет операцию с транзакцией, возвращающую результат.
     * Создает новый EntityManager, начинает транзакцию, выполняет операцию,
     * фиксирует изменения и автоматически закрывает EntityManager.
     * В случае ошибки выполняет откат транзакции.
     *
     * @param <R> тип возвращаемого значения
     * @param operation функция, содержащая логику операции с базой данных
     * @return результат выполнения операции
     * @throws PersistenceException если операция завершилась с ошибкой
     */
    public <R> R executeInTransaction(Function<EntityManager, R> operation) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;

        try {
            tx = em.getTransaction();
            tx.begin();

            R result = operation.apply(em);

            tx.commit();
            return result;

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new PersistenceException("Transaction failed", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Выполняет операцию с транзакцией без возвращаемого значения.
     * Создает новый EntityManager, начинает транзакцию, выполняет операцию,
     * фиксирует изменения и автоматически закрывает EntityManager.
     * В случае ошибки выполняет откат транзакции.
     *
     * @param operation функция, содержащая логику операции с базой данных
     * @throws PersistenceException если операция завершилась с ошибкой
     */
    public void executeInTransaction(Consumer<EntityManager> operation) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;

        try {
            tx = em.getTransaction();
            tx.begin();

            operation.accept(em);

            tx.commit();

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new PersistenceException("Transaction failed", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Проверяет существование сущности в базе данных по её идентификатору.
     * Выполняет запрос без транзакции для проверки наличия сущности.
     *
     * @param entityClass класс сущности
     * @param id идентификатор сущности
     * @return true если сущность существует, false в противном случае
     * @throws PersistenceException если запрос завершился с ошибкой
     */
    public boolean exists(Class<?> entityClass, Object id) {
        return executeQuery(em -> em.find(entityClass, id) != null);
    }
}