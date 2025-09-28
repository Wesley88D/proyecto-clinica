package com.clinica.dao;

import com.clinica.model.Medico;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;

public class MedicoDAO extends GenericDAO {
    public Medico guardar(Medico m) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            em.persist(m);
            em.getTransaction().commit();
            return m;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally { em.close(); }
    }

    public Medico buscarPorId(Long id) { EntityManager em = em(); try { return em.find(Medico.class, id);} finally { em.close(); }}

    public Medico buscarPorColegiado(String colegiado) {
        EntityManager em = em();
        try { return em.createQuery("SELECT m FROM Medico m WHERE m.colegiado = :c", Medico.class).setParameter("c", colegiado).getSingleResult(); }
        catch (NoResultException ex) { return null; } finally { em.close(); }
    }

    public List<Medico> listarTodos() { EntityManager em = em(); try { return em.createQuery("SELECT m FROM Medico m", Medico.class).getResultList(); } finally { em.close(); }}
}
