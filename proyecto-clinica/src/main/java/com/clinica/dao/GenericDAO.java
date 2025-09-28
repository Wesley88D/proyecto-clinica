package com.clinica.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class GenericDAO {
    protected static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("clinicPU");

    public EntityManager em() {
        return emf.createEntityManager();
    }
}
