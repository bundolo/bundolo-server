package org.bundolo.dao;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.hibernate.Session;

public abstract class JpaDAO<K, E> {

    @PersistenceContext(unitName = "BundoloPostgresPersistenceUnit", type = PersistenceContextType.EXTENDED)
    protected EntityManager entityManager;

    protected Class<E> entityClass;

    @SuppressWarnings("unchecked")
    public JpaDAO() {
	ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
	this.entityClass = (Class<E>) genericSuperclass.getActualTypeArguments()[1];
    }

    public void persist(E entity) {
	entityManager.persist(entity);
    }

    public void remove(E entity) {
	entityManager.remove(entity);
    }

    public E merge(E entity) {
	return entityManager.merge(entity);
    }

    public void refresh(E entity) {
	entityManager.refresh(entity);
    }

    public E findById(K id) {
	return entityManager.find(entityClass, id);
    }

    public E flush(E entity) {
	entityManager.flush();
	return entity;
    }

    @SuppressWarnings("unchecked")
    public List<E> findAll() {
	Query q = entityManager.createQuery("SELECT h FROM " + entityClass.getName() + " h");
	return q.getResultList();
    }

    public Integer removeAll() {
	Query q = entityManager.createQuery("DELETE FROM " + entityClass.getName() + " h");
	return q.executeUpdate();
    }

    public void clear() {
	// clear() forces reading from database instead of cache
	// it can be optimised to clear only when needed
	Session session = entityManager.unwrap(Session.class);
	session.clear();
    }

}
