package com.weavers.duqhan.dao.jpa;

import com.weavers.duqhan.dao.BaseDao;
import com.weavers.duqhan.domain.BaseDomain;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base domain class for all domain classes
 * @param <T>
 */
public class BaseDaoJpa<T extends BaseDomain> implements BaseDao<T> {

    @PersistenceContext
    private EntityManager entityManager;
    private final Class<T> prototype;
    private final String entityName;

    /**
     * @param prototype
     * @param entityName
     */
    public BaseDaoJpa(Class<T> prototype, String entityName) {
        this.prototype = prototype;
        this.entityName = entityName;
    }

    @Override
    @Transactional
    public T save(T entity) {
        if (entity.getId() != null) {
            entityManager.merge(entity);
        } else {
            entityManager.persist(entity);
        }
        return entity;
    }

    @Override
    public T loadById(Long entityId) {
        T entity = entityManager.find(prototype, entityId);
        if (entity == null) {
        }
        return entity;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> loadAll() {
        Query query = entityManager.createQuery("select obj from " + entityName + " obj order by obj.id desc");
        //noinspection unchecked
//        System.out.println("Entity Name");
        return query.getResultList();
    }

    @Override
    public void delete(final T entity) {
        T loadedEntity = loadById(entity.getId());
        entityManager.remove(loadedEntity);
    }

    /**
     *
     * User:  3Idiots Date: Sept 18, 2013 Time: 1:20:49 PM
     *
     * @param cl
     * @return
     * @throws IllegalArgumentException Return Type: T Description:
     */
    protected final T newPrototype(Class<T> cl) throws IllegalArgumentException {
        try {
            return cl.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
}
