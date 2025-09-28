package com.clinica;

import com.clinica.dao.CitaDAO;
import com.clinica.dao.MedicoDAO;
import com.clinica.dao.PacienteDAO;
import com.clinica.model.*;
import jakarta.persistence.PersistenceException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class App {
    private static final Scanner sc = new Scanner(System.in);
    private static final PacienteDAO pacienteDAO = new PacienteDAO();
    private static final MedicoDAO medicoDAO = new MedicoDAO();
    private static final CitaDAO citaDAO = new CitaDAO();
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            printMenu();
            String opt = sc.nextLine().trim();
            switch (opt) {
                case "1": registrarPaciente(); break;
                case "2": crearEditarHistorial(); break;
                case "3": registrarMedico(); break;
                case "4": agendarCita(); break;
                case "5": cambiarEstadoCita(); break;
                case "6": consultas(); break;
                case "7": eliminarMenu(); break;
                case "8": semilla(); break;
                case "9": running = false; break;
                default: System.out.println("Opción inválida"); break;
            }
        }
        System.out.println("Saliendo...");
        System.exit(0);
    }

    private static void printMenu() {
        System.out.println("\n=== Clínica - Menú ==="); 
        System.out.println("1. Registrar paciente"); 
        System.out.println("2. Crear/editar historial médico de un paciente"); 
        System.out.println("3. Registrar médico"); 
        System.out.println("4. Agendar cita"); 
        System.out.println("5. Cambiar estado de una cita"); 
        System.out.println("6. Consultas"); 
        System.out.println("7. Eliminar"); 
        System.out.println("8. Semilla de datos (opcional)"); 
        System.out.println("9. Salir"); 
        System.out.print("Elija una opción: ");
    }

    private static void registrarPaciente() {
        try {
            Paciente p = new Paciente();
            System.out.print("Nombre: "); p.setNombre(sc.nextLine().trim());
            System.out.print("DPI (único): "); p.setDpi(sc.nextLine().trim());
            System.out.print("Fecha nacimiento (YYYY-MM-DD): "); p.setFechaNacimiento(LocalDate.parse(sc.nextLine().trim()));
            System.out.print("Teléfono: "); p.setTelefono(sc.nextLine().trim());
            System.out.print("Email: "); p.setEmail(sc.nextLine().trim());
            pacienteDAO.guardar(p);
            System.out.println("Paciente registrado: " + p);
        } catch (PersistenceException pe) {
            System.out.println("Error de persistencia (posible DPI duplicado): " + pe.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void crearEditarHistorial() {
        System.out.print("DPI del paciente: ");
        String dpi = sc.nextLine().trim();
        Paciente p = pacienteDAO.buscarPorDpi(dpi);
        if (p == null) { System.out.println("Paciente no encontrado"); return; }
        try {
            // buscar si tiene historial
            if (p.getHistorial() == null) {
                HistorialMedico h = new HistorialMedico();
                h.setPaciente(p);
                System.out.print("Alergias: "); h.setAlergias(sc.nextLine().trim());
                System.out.print("Antecedentes: "); h.setAntecedentes(sc.nextLine().trim());
                System.out.print("Observaciones: "); h.setObservaciones(sc.nextLine().trim());
                // persistir historial junto con paciente reference
                // cargar entity manager directly via DAO pattern simple:
                jakarta.persistence.EntityManager em = pacienteDAO.em();
                em.getTransaction().begin();
                Paciente managed = em.find(Paciente.class, p.getId());
                HistorialMedico managedH = new HistorialMedico();
                managedH.setPaciente(managed);
                managedH.setAlergias(h.getAlergias());
                managedH.setAntecedentes(h.getAntecedentes());
                managedH.setObservaciones(h.getObservaciones());
                em.persist(managedH);
                em.getTransaction().commit();
                em.close();
                System.out.println("Historial creado.");
            } else {
                jakarta.persistence.EntityManager em = pacienteDAO.em();
                em.getTransaction().begin();
                HistorialMedico managed = em.find(HistorialMedico.class, p.getId());
                System.out.print("Alergias (actual: " + (managed.getAlergias()==null?"":managed.getAlergias()) + "): "); managed.setAlergias(sc.nextLine());
                System.out.print("Antecedentes (actual): "); managed.setAntecedentes(sc.nextLine());
                System.out.print("Observaciones (actual): "); managed.setObservaciones(sc.nextLine());
                em.getTransaction().commit();
                em.close();
                System.out.println("Historial actualizado.");
            }
        } catch (Exception e) {
            System.out.println("Error al crear/editar historial: " + e.getMessage());
        }
    }

    private static void registrarMedico() {
        try {
            Medico m = new Medico();
            System.out.print("Nombre: "); m.setNombre(sc.nextLine().trim());
            System.out.print("Colegiado (único): "); m.setColegiado(sc.nextLine().trim());
            System.out.println("Especialidades: "); for (Especialidad e : Especialidad.values()) System.out.println(" - " + e);
            System.out.print("Elija especialidad (exacto): "); m.setEspecialidad(Especialidad.valueOf(sc.nextLine().trim()));
            System.out.print("Email: "); m.setEmail(sc.nextLine().trim());
            medicoDAO.guardar(m);
            System.out.println("Medico registrado: " + m);
        } catch (PersistenceException pe) {
            System.out.println("Error de persistencia (posible colegiado duplicado): " + pe.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void agendarCita() {
        try {
            System.out.print("DPI del paciente: "); String dpi = sc.nextLine().trim();
            Paciente p = pacienteDAO.buscarPorDpi(dpi);
            if (p == null) { System.out.println("Paciente no encontrado"); return; }
            System.out.print("Colegiado del médico: "); String col = sc.nextLine().trim();
            Medico m = medicoDAO.buscarPorColegiado(col);
            if (m == null) { System.out.println("Médico no encontrado"); return; }
            System.out.print("Fecha y hora (YYYY-MM-DD HH:mm): "); LocalDateTime fh = LocalDateTime.parse(sc.nextLine().trim(), dtf);
            if (fh.isBefore(LocalDateTime.now())) { System.out.println("No se puede agendar en el pasado"); return; }
            if (citaDAO.existeChoqueAgenda(m.getId(), fh)) { System.out.println("Choque de agenda: el médico ya tiene una cita a esa hora"); return; }
            System.out.print("Motivo: "); String motivo = sc.nextLine().trim();
            Cita c = new Cita();
            c.setPaciente(p);
            c.setMedico(m);
            c.setFechaHora(fh);
            c.setEstado(EstadoCita.PROGRAMADA);
            c.setMotivo(motivo);
            citaDAO.guardar(c);
            System.out.println("Cita agendada: " + c);
        } catch (Exception e) {
            System.out.println("Error al agendar: " + e.getMessage());
        }
    }

    private static void cambiarEstadoCita() {
        try {
            System.out.print("ID de la cita: "); Long id = Long.parseLong(sc.nextLine().trim());
            Cita c = citaDAO.buscarPorId(id);
            if (c == null) { System.out.println("Cita no encontrada"); return; }
            System.out.println("Estado actual: " + c.getEstado());
            System.out.print("Nuevo estado (PROGRAMADA/ATENDIDA/CANCELADA): "); c.setEstado(EstadoCita.valueOf(sc.nextLine().trim()));
            jakarta.persistence.EntityManager em = citaDAO.em();
            em.getTransaction().begin();
            Cita managed = em.find(Cita.class, id);
            managed.setEstado(c.getEstado());
            em.getTransaction().commit();
            em.close();
            System.out.println("Estado actualizado.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void consultas() {
        System.out.println("6.1 Listar pacientes con sus citas"); 
        System.out.println("6.2 Listar médicos con próximas citas"); 
        System.out.println("6.3 Buscar citas por rango de fechas"); 
        System.out.println("6.4 Ver historial médico de un paciente");
        System.out.print("Elija: ");
        String o = sc.nextLine().trim();
        switch (o) {
            case "6.1": listarPacientesConCitas(); break;
            case "6.2": listarMedicosConProximasCitas(); break;
            case "6.3": buscarCitasPorRango(); break;
            case "6.4": verHistorialPaciente(); break;
            default: System.out.println("Opción inválida"); break;
        }
    }

    private static void listarPacientesConCitas() {
        List<Paciente> lista = pacienteDAO.listarTodos();
        for (Paciente p : lista) {
            System.out.println(p);
            List<Cita> citas = citaDAO.citasPorPaciente(p.getId());
            for (Cita c : citas) {
                System.out.println("  - " + c.getFechaHora() + " | Medico: " + c.getMedico().getNombre());
            }
        }
    }

    private static void listarMedicosConProximasCitas() {
        List<Medico> medicos = medicoDAO.listarTodos();
        for (Medico m : medicos) {
            System.out.println(m);
            List<Cita> citas = citaDAO.proximasPorMedico(m.getId());
            for (Cita c : citas) {
                System.out.println("  - " + c.getFechaHora() + " | Paciente: " + c.getPaciente().getNombre());
            }
        }
    }

    private static void buscarCitasPorRango() {
        try {
            System.out.print("Fecha inicio (YYYY-MM-DD HH:mm): "); LocalDateTime ini = LocalDateTime.parse(sc.nextLine().trim(), dtf);
            System.out.print("Fecha fin (YYYY-MM-DD HH:mm): "); LocalDateTime fin = LocalDateTime.parse(sc.nextLine().trim(), dtf);
            List<Cita> res = citaDAO.buscarPorRango(ini, fin);
            for (Cita c : res) System.out.println(c + " | Paciente: " + c.getPaciente().getNombre() + " | Medico: " + c.getMedico().getNombre());
        } catch (Exception e) { System.out.println("Error en fechas: " + e.getMessage()); }
    }

    private static void verHistorialPaciente() {
        System.out.print("DPI del paciente: ");
        Paciente p = pacienteDAO.buscarPorDpi(sc.nextLine().trim());
        if (p == null) { System.out.println("Paciente no encontrado"); return; }
        if (p.getHistorial() == null) { System.out.println("Paciente no tiene historial"); return; }
        System.out.println(p.getHistorial());
        System.out.println("Alergias: " + p.getHistorial().getAlergias());
        System.out.println("Antecedentes: " + p.getHistorial().getAntecedentes());
        System.out.println("Observaciones: " + p.getHistorial().getObservaciones());
    }

    private static void eliminarMenu() {
        System.out.println("7.1 Eliminar cita"); System.out.println("7.2 Eliminar paciente (solo si no tiene citas)");
        System.out.print("Elija: ");
        String o = sc.nextLine().trim();
        switch (o) {
            case "7.1": eliminarCita(); break;
            case "7.2": eliminarPaciente(); break;
            default: System.out.println("Opción inválida"); break;
        }
    }

    private static void eliminarCita() {
        System.out.print("ID de la cita: ");
        Long id = Long.parseLong(sc.nextLine().trim());
        if (citaDAO.eliminar(id)) System.out.println("Cita eliminada"); else System.out.println("No se pudo eliminar");
    }

    private static void eliminarPaciente() {
        System.out.print("ID del paciente: ");
        Long id = Long.parseLong(sc.nextLine().trim());
        boolean ok = pacienteDAO.eliminarSiSinCitas(id);
        if (ok) System.out.println("Paciente eliminado"); else System.out.println("No se puede eliminar: tiene citas o no existe");
    }

    private static void semilla() {
        try {
            Medico m1 = new Medico(); m1.setNombre("Dr. Ana Morales"); m1.setColegiado("C-100"); m1.setEspecialidad(Especialidad.MEDICINA_GENERAL); m1.setEmail("ana@example.com"); medicoDAO.guardar(m1);
            Medico m2 = new Medico(); m2.setNombre("Dr. Carlos Ruiz"); m2.setColegiado("C-200"); m2.setEspecialidad(Especialidad.CARDIOLOGIA); m2.setEmail("carlos@example.com"); medicoDAO.guardar(m2);
            Paciente p1 = new Paciente(); p1.setNombre("Juan Pérez"); p1.setDpi("1234567890101"); p1.setFechaNacimiento(LocalDate.of(1990,1,1)); p1.setTelefono("5555"); p1.setEmail("juan@example.com"); pacienteDAO.guardar(p1);
            Paciente p2 = new Paciente(); p2.setNombre("María López"); p2.setDpi("1098765432101"); p2.setFechaNacimiento(LocalDate.of(1985,5,5)); p2.setTelefono("6666"); p2.setEmail("maria@example.com"); pacienteDAO.guardar(p2);
            System.out.println("Semilla creada.");
        } catch (Exception e) { System.out.println("Error semilla: " + e.getMessage()); }
    }
}
