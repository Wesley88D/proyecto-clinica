package com.clinica.dao;

import com.clinica.model.Paciente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;

public class PacienteDAO extends GenericDAO {

    public Paciente guardar(Paciente p) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            em.persist(p);
            em.getTransaction().commit();
            return p;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally { em.close(); }
    }

    public Paciente buscarPorId(Long id) {
        EntityManager em = em();
        try { return em.find(Paciente.class, id); } finally { em.close(); }
    }

    public Paciente buscarPorDpi(String dpi) {
        EntityManager em = em();
        try {
            return em.createQuery("SELECT p FROM Paciente p WHERE p.dpi = :dpi", Paciente.class)
                    .setParameter("dpi", dpi).getSingleResult();
        } catch (NoResultException ex) { return null; } finally { em.close(); }
    }

    public List<Paciente> listarTodos() {
        EntityManager em = em();
        try {
            return em.createQuery("SELECT p FROM Paciente p", Paciente.class).getResultList();
        } finally { em.close(); }
    }

    public boolean eliminarSiSinCitas(Long id) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            Paciente p = em.find(Paciente.class, id);
            if (p == null) { em.getTransaction().rollback(); return false; }
            if (p.getCitas() != null && !p.getCitas().isEmpty()) {
                em.getTransaction().rollback();
                return false;
            }
            em.remove(p);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally { em.close(); }
    }
}
