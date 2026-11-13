package mx.edu.cesba;

import java.time.LocalDate;
import java.time.LocalTime;

public class Cita {

    // Estados posibles de una cita.
    // OJO: la restricción de horarios en la base de datos libera el horario
    // cuando el estado es exactamente "CANCELADA".
    public static final String[] ESTADOS = {
        "PROGRAMADA", "CONFIRMADA", "ATENDIDA", "NO_ASISTIO", "CANCELADA"
    };

    private int id;
    private int pacienteId;
    private int medicoId;
    private LocalDate fecha;
    private LocalTime hora;
    private String motivo;
    private String estado = "PROGRAMADA";
    private String observaciones;

    // Campos auxiliares que vienen del JOIN
    private String paciente;
    private String medico;

    public Cita() {
    }

    // Texto amigable para mostrar el estado en pantalla
    public static String etiquetaEstado(String estado) {
        if (estado == null) {
            return "";
        }
        switch (estado) {
            case "PROGRAMADA":
                return "Programada";
            case "CONFIRMADA":
                return "Confirmada";
            case "ATENDIDA":
                return "Atendida";
            case "NO_ASISTIO":
                return "no asistio";
            case "CANCELADA":
                return "Cancelada";
            default:
                return estado;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(int pacienteId) {
        this.pacienteId = pacienteId;
    }

    public int getMedicoId() {
        return medicoId;
    }

    public void setMedicoId(int medicoId) {
        this.medicoId = medicoId;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getPaciente() {
        return paciente;
    }

    public void setPaciente(String paciente) {
        this.paciente = paciente;
    }

    public String getMedico() {
        return medico;
    }

    public void setMedico(String medico) {
        this.medico = medico;
    }

}
    

