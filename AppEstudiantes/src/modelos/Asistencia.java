package modelos;

import java.time.LocalDate;

public class Asistencia {
    private int id;
    private long numControl;
    private String materia;
    private LocalDate fecha;
    private String estado; // "A": Asistencia, "F": Falta, "P": Permiso
    
    // Constructor para insercciones
    public Asistencia(long numControl, String materia, LocalDate fecha, String estado) {
        this.numControl = numControl;
        this.materia = materia;
        this.fecha = fecha;
        this.estado = estado;
    }
    
    // Constructor para consultas
    public Asistencia(int id, long numControl, String materia, LocalDate fecha, String estado) {
        this.id = id;
        this.numControl = numControl;
        this.materia = materia;
        this.fecha = fecha;
        this.estado = estado;
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public long getNumControl() { return numControl; }
    public void setNumControl(long numControl) { this.numControl = numControl; }
    
    public String getMateria() { return materia; }
    public void setMateria(String materia) { this.materia = materia; }
    
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    // Método de validación de estado
    public boolean esEstadoValido() {
        return estado.equals("A") || estado.equals("F") || estado.equals("P");
    }
}