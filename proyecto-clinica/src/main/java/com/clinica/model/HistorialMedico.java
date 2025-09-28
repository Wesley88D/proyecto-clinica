package com.clinica.model;

import jakarta.persistence.*;

@Entity
@Table(name = "historial_medico")
public class HistorialMedico {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "paciente_id", foreignKey = @ForeignKey(name = "fk_historial_paciente"))
    private Paciente paciente;

    @Column(length = 1000)
    private String alergias;

    @Column(length = 2000)
    private String antecedentes;

    @Column(length = 2000)
    private String observaciones;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }
    public String getAlergias() { return alergias; }
    public void setAlergias(String alergias) { this.alergias = alergias; }
    public String getAntecedentes() { return antecedentes; }
    public void setAntecedentes(String antecedentes) { this.antecedentes = antecedentes; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    @Override
    public String toString() {
        return "HistorialMedico{" + "id=" + id + '}';
    }
}
