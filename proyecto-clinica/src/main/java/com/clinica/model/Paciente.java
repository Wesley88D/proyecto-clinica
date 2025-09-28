package com.clinica.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "paciente",
       uniqueConstraints = {@UniqueConstraint(name = "uk_paciente_dpi", columnNames = {"dpi"})})
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(nullable = false, unique = true)
    private String dpi;

    private LocalDate fechaNacimiento;

    private String telefono;

    private String email;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.PERSIST, orphanRemoval = false)
    private List<Cita> citas = new ArrayList<>();

    @OneToOne(mappedBy = "paciente")
    private HistorialMedico historial;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDpi() { return dpi; }
    public void setDpi(String dpi) { this.dpi = dpi; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<Cita> getCitas() { return citas; }
    public void setCitas(List<Cita> citas) { this.citas = citas; }
    public HistorialMedico getHistorial() { return historial; }
    public void setHistorial(HistorialMedico historial) { this.historial = historial; }

    @Override
    public String toString() {
        return "Paciente{" + "id=" + id + ", nombre='" + nombre + '\'' + ", dpi='" + dpi + '\'' + '}';
    }
}
