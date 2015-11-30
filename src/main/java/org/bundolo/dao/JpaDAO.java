package org.bundolo.dao;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.hibernate.Session;

public abstract class JpaDAO<K, E> {

    @PersistenceContext(unitName = "BundoloPostgresPersistenceUnit", type = PersistenceContextType.TRANSACTION)
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

    //TODO this is not generic
    @SuppressWarnings("unchecked")
    public List<E> findAllPaged(int start, int maxResults) {
	// Query q = entityManager.createQuery("SELECT h FROM " + entityClass.getName() + " h");
	Query q = entityManager.createQuery("SELECT h FROM " + entityClass.getName() + " h order by contentId");
	// Query q = entityManager
	// .createQuery("SELECT h FROM "
	// + entityClass.getName()
	// +
	// " h where contentId in (2412, 2361, 2311, 2439, 2316, 2319, 2410, 2330, 2326, 2331, 2377, 2333, 2335, 2348, 2336, 2337, 2339, 2366, 2340, 2341, 2342, 2343, 2344, 2345, 2349, 2350, 2354, 2355, 2356, 2357, 2358, 2359, 2360, 2363, 2435, 2364, 2367, 2370, 2375, 2380, 2383, 2384, 2386, 2388, 2387, 2389, 2390, 2393, 2394, 2489, 2479, 2362, 2450, 2436, 2369, 2474, 2429, 2409, 2476, 2465, 2382, 2418, 2365, 2413, 2329, 2313, 2470, 2490, 2372, 2403, 2351, 2338, 2396, 2408, 2368, 2461, 2346, 2376, 2504, 324002, 324098)");
	//
	q.setFirstResult(start);
	q.setMaxResults(maxResults);
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
