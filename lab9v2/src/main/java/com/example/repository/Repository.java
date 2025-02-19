package com.example.repository;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.util.List;

public class Repository<T> {
    private EntityManagerFactory emf;
    private Class<T> entityClass;

    public Repository(EntityManagerFactory emf, Class<T> entityClass) {
        this.emf = emf;
        this.entityClass = entityClass;
    }

    public void create(T entity) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public T findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(entityClass, id);
        } finally {
            em.close();
        }
    }

    public List<T> findByName(String namePattern) {
        EntityManager em = emf.createEntityManager();
        try {
            Query query = em.createNamedQuery(entityClass.getSimpleName() + ".findByName");
            query.setParameter("namePattern", namePattern );
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
