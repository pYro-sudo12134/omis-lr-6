package by.losik.lab6omis.persistence;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Продюсер для создания и управления экземплярами {@link EntityManager} и {@link EntityManagerFactory}.
 * Обеспечивает управление жизненным циклом EntityManager в контексте CDI.
 *
 * <p>Основные функции:</p>
 * <ul>
 *   <li>Создание и закрытие EntityManagerFactory</li>
 *   <li>Производство EntityManager с областью видимости RequestScoped</li>
 *   <li>Автоматическое закрытие EntityManager в конце запроса</li>
 * </ul>
 *
 * @see EntityManager
 * @see EntityManagerFactory
 * @see ApplicationScoped
 * @see RequestScoped
 */
@ApplicationScoped
public class EntityManagerProducer {

    private EntityManagerFactory emf;

    /**
     * Инициализирует EntityManagerFactory при создании бина.
     * Создает фабрику EntityManager с использованием persistence unit "lab6omisPU".
     *
     * @throws javax.persistence.PersistenceException если не удается создать EntityManagerFactory
     */
    @PostConstruct
    public void init() {
        emf = Persistence.createEntityManagerFactory("lab6omisPU");
    }

    /**
     * Закрывает EntityManagerFactory при уничтожении бина.
     * Освобождает все ресурсы, связанные с фабрикой.
     */
    @PreDestroy
    public void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    /**
     * Производит экземпляр {@link EntityManagerFactory}.
     * Фабрика имеет область видимости {@link ApplicationScoped} и используется
     * для создания EntityManager в рамках всего приложения.
     *
     * @return экземпляр EntityManagerFactory
     */
    @Produces
    @ApplicationScoped
    public EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    /**
     * Создает новый экземпляр {@link EntityManager} для каждого HTTP-запроса.
     * EntityManager имеет область видимости {@link RequestScoped}, что означает,
     * что для каждого запроса создается новый экземпляр, который автоматически
     * закрывается в конце запроса.
     *
     * @return новый экземпляр EntityManager
     * @throws javax.persistence.PersistenceException если не удается создать EntityManager
     */
    @Produces
    @RequestScoped
    public EntityManager createEntityManager() {
        return emf.createEntityManager();
    }

    /**
     * Закрывает {@link EntityManager} в конце запроса.
     * Метод вызывается автоматически контейнером CDI при уничтожении бина EntityManager.
     *
     * @param em экземпляр EntityManager для закрытия
     */
    public void closeEntityManager(@Disposes EntityManager em) {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }
}