package by.losik.lab6omis.repository.base;

import by.losik.lab6omis.persistence.TransactionManager;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public abstract class BaseRepository<T, ID> {

    @Inject
    protected TransactionManager txManager;

    private final Class<T> entityClass;

    @SuppressWarnings("unchecked")
    public BaseRepository() {
        Type genericSuperclass = getClass().getGenericSuperclass();
        Class<?> currentClass = getClass();

        while (!(genericSuperclass instanceof ParameterizedType) && currentClass != null) {
            currentClass = currentClass.getSuperclass();
            if (currentClass != null) {
                genericSuperclass = currentClass.getGenericSuperclass();
            }
        }

        if (!(genericSuperclass instanceof ParameterizedType)) {
            throw new IllegalStateException("Cannot determine entity type for repository: " + getClass());
        }

        ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
        Type[] typeArgs = parameterizedType.getActualTypeArguments();

        if (typeArgs.length == 0) {
            throw new IllegalStateException("No type arguments found for repository: " + getClass());
        }

        this.entityClass = (Class<T>) typeArgs[0];
    }

    public T save(T entity) {
        return txManager.executeInTransaction((Function<EntityManager, T>) em -> em.merge(entity));
    }

    public T create(T entity) {
        return txManager.executeInTransaction(em -> {
            em.persist(entity);
            return entity;
        });
    }

    public void deleteById(ID id) {
        txManager.executeInTransaction(em -> {
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
            }
        });
    }

    public void delete(T entity) {
        txManager.executeInTransaction(em -> {
            if (em.contains(entity)) {
                em.remove(entity);
            } else {
                T managed = em.merge(entity);
                em.remove(managed);
            }
        });
    }

    public Optional<T> findById(ID id) {
        return txManager.executeQuery(em ->
                Optional.ofNullable(em.find(entityClass, id))
        );
    }

    public List<T> findAll() {
        return txManager.executeQuery(em ->
                em.createQuery("SELECT e FROM " + getEntityName() + " e", entityClass)
                        .getResultList()
        );
    }

    public List<T> findAll(int page, int size) {
        return txManager.executeQuery(em ->
                em.createQuery("SELECT e FROM " + getEntityName() + " e", entityClass)
                        .setFirstResult(page * size)
                        .setMaxResults(size)
                        .getResultList()
        );
    }

    public boolean existsById(ID id) {
        return txManager.exists(entityClass, id);
    }

    public long count() {
        return txManager.executeQuery(em ->
                em.createQuery("SELECT COUNT(e) FROM " + getEntityName() + " e", Long.class)
                        .getSingleResult()
        );
    }

    public boolean isNew(T entity) {
        return txManager.executeQuery(em ->
                em.getEntityManagerFactory()
                        .getPersistenceUnitUtil()
                        .getIdentifier(entity) == null
        );
    }

    protected List<T> executeQuery(String jpql) {
        return executeQuery(jpql, Collections.emptyMap());
    }

    protected List<T> executeQuery(String jpql, Map<String, Object> params) {
        return txManager.executeQuery(em -> {
            TypedQuery<T> query = em.createQuery(jpql, entityClass);
            params.forEach(query::setParameter);
            return query.getResultList();
        });
    }

    protected <R> R executeQuery(Function<EntityManager, R> operation) {
        return txManager.executeQuery(operation);
    }

    protected List<T> executeNamedQuery(String queryName) {
        return executeNamedQuery(queryName, Collections.emptyMap());
    }

    protected List<T> executeNamedQuery(String queryName, Map<String, Object> params) {
        return txManager.executeQuery(em -> {
            TypedQuery<T> query = em.createNamedQuery(queryName, entityClass);
            params.forEach(query::setParameter);
            return query.getResultList();
        });
    }

    protected Optional<T> executeQuerySingle(String jpql) {
        return executeQuerySingle(jpql, Collections.emptyMap());
    }

    protected Optional<T> executeQuerySingle(String jpql, Map<String, Object> params) {
        List<T> results = executeQuery(jpql, params);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    protected Optional<T> executeNamedQuerySingle(String queryName) {
        return executeNamedQuerySingle(queryName, Collections.emptyMap());
    }

    protected Optional<T> executeNamedQuerySingle(String queryName, Map<String, Object> params) {
        List<T> results = executeNamedQuery(queryName, params);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    protected <R> List<R> executeCustomQuery(String jpql, Class<R> resultClass) {
        return executeCustomQuery(jpql, resultClass, Collections.emptyMap());
    }

    protected <R> List<R> executeCustomQuery(String jpql, Class<R> resultClass, Map<String, Object> params) {
        return txManager.executeQuery(em -> {
            TypedQuery<R> query = em.createQuery(jpql, resultClass);
            params.forEach(query::setParameter);
            return query.getResultList();
        });
    }

    protected <R> R executeCustomQuerySingle(String jpql, Class<R> resultClass) {
        return executeCustomQuerySingle(jpql, resultClass, Collections.emptyMap());
    }

    protected <R> R executeCustomQuerySingle(String jpql, Class<R> resultClass, Map<String, Object> params) {
        return txManager.executeQuery(em -> {
            TypedQuery<R> query = em.createQuery(jpql, resultClass);
            params.forEach(query::setParameter);
            return query.getSingleResult();
        });
    }

    protected String getEntityName() {
        return entityClass.getSimpleName();
    }

    protected Class<T> getEntityClass() {
        return entityClass;
    }
}