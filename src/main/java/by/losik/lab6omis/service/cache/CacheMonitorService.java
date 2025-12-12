package by.losik.lab6omis.service.cache;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import java.util.Map;

/**
 * Сервис для мониторинга и управления кэшами Hibernate.
 * Предоставляет методы для получения статистики использования кэшей
 * и очистки всех кэшей второго уровня и кэшей запросов.
 *
 * @author Losik Yaroslav
 * @version 1.0
 */
@ApplicationScoped
public class CacheMonitorService {

    @Inject
    private EntityManagerFactory emf;

    /**
     * Возвращает статистику использования кэшей Hibernate.
     * Собирает данные о количестве попаданий, промахов и операций записи
     * для кэша второго уровня и кэша запросов, а также общую статистику
     * по загрузкам сущностей и коллекций.
     *
     * @return Карта (Map) с ключами и значениями статистики, содержащая:
     *         <ul>
     *           <li><b>secondLevelCacheHitCount</b> - количество попаданий в кэш второго уровня</li>
     *           <li><b>secondLevelCacheMissCount</b> - количество промахов в кэш второго уровня</li>
     *           <li><b>secondLevelCachePutCount</b> - количество операций записи в кэш второго уровня</li>
     *           <li><b>queryCacheHitCount</b> - количество попаданий в кэш запросов</li>
     *           <li><b>queryCacheMissCount</b> - количество промахов в кэш запросов</li>
     *           <li><b>queryCachePutCount</b> - количество операций записи в кэш запросов</li>
     *           <li><b>entityFetchCount</b> - общее количество загрузок сущностей</li>
     *           <li><b>collectionFetchCount</b> - общее количество загрузок коллекций</li>
     *           <li><b>isStatisticsEnabled</b> - флаг, указывающий, включена ли статистика</li>
     *         </ul>
     */
    public Map<String, Object> getCacheStatistics() {
        SessionFactory sessionFactory = emf.unwrap(SessionFactory.class);
        Statistics stats = sessionFactory.getStatistics();

        return Map.of(
                "secondLevelCacheHitCount", stats.getSecondLevelCacheHitCount(),
                "secondLevelCacheMissCount", stats.getSecondLevelCacheMissCount(),
                "secondLevelCachePutCount", stats.getSecondLevelCachePutCount(),
                "queryCacheHitCount", stats.getQueryCacheHitCount(),
                "queryCacheMissCount", stats.getQueryCacheMissCount(),
                "queryCachePutCount", stats.getQueryCachePutCount(),
                "entityFetchCount", stats.getEntityFetchCount(),
                "collectionFetchCount", stats.getCollectionFetchCount(),
                "isStatisticsEnabled", stats.isStatisticsEnabled()
        );
    }

    /**
     * Очищает все кэши Hibernate второго уровня и кэши запросов.
     * Используется для сброса кэшированных данных, например, при изменении
     * данных вручную или для освобождения памяти.
     *
     * @throws javax.persistence.PersistenceException если возникает ошибка при работе с EntityManagerFactory
     */
    public void clearAllCaches() {
        SessionFactory sessionFactory = emf.unwrap(SessionFactory.class);
        sessionFactory.getCache().evictAll();
    }
}