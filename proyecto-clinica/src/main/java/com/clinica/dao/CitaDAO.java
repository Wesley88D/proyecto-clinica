package com.clinica.dao;

import com.clinica.model.Cita;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.time.LocalDateTime;
import java.util.List;

public class CitaDAO extends GenericDAO {

    public Cita guardar(Cita c) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            em.persist(c);
            em.getTransaction().commit();
            return c;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally { em.close(); }
    }

    public List<Cita> citasPorPaciente(Long pacienteId) {
        EntityManager em = em();
        try {
            return em.createQuery("SELECT c FROM Cita c WHERE c.paciente.id = :pid", Cita.class)
                    .setParameter("pid", pacienteId).getResultList();
        } finally { em.close(); }
    }

    public List<Cita> proximasPorMedico(Long medicoId) {
        EntityManager em = em();
        try {
            return em.createQuery("SELECT c FROM Cita c WHERE c.medico.id = :mid AND c.fechaHora >= :now ORDER BY c.fechaHora", Cita.class)
                    .setParameter("mid", medicoId).setParameter("now", LocalDateTime.now()).getResultList();
        } finally { em.close(); }
    }

    public List<Cita> buscarPorRango(LocalDateTime inicio, LocalDateTime fin) {
        EntityManager em = em();
        try {
            return em.createQuery("SELECT c FROM Cita c WHERE c.fechaHora BETWEEN :ini AND :fin ORDER BY c.fechaHora", Cita.class)
                    .setParameter("ini", inicio).setParameter("fin", fin).getResultList();
        } finally { em.close(); }
    }

    public Cita buscarPorId(Long id) { EntityManager em = em(); try { return em.find(Cita.class, id);} finally { em.close(); }}

    public boolean eliminar(Long id) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            Cita c = em.find(Cita.class, id);
            if (c == null) { em.getTransaction().rollback(); return false; }
            em.remove(c);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally { em.close(); }
    }

    public boolean existeChoqueAgenda(Long medicoId, LocalDateTime fechaHora) {
        EntityManager em = em();
        try {
            Long cnt = em.createQuery("SELECT COUNT(c) FROM Cita c WHERE c.medico.id = :mid AND c.fechaHora = :fh", Long.class)
                    .setParameter("mid", medicoId).setParameter("fh", fechaHora).getSingleResult();
            return cnt != null && cnt > 0;
        } finally { em.close(); }
    }
}
