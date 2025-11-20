package modelos;

public class Calificacion {
    private int id;
    private long numControl;
    private String materia;
    private Double parcial1;
    private Double parcial2;
    private Double parcial3;
    private Double promedio;

    public Calificacion(int id, long numControl, String materia, 
                        Double parcial1, Double parcial2, Double parcial3) {
        this.id = id;
        this.numControl = numControl;
        this.materia = materia;
        this.parcial1 = parcial1;
        this.parcial2 = parcial2;
        this.parcial3 = parcial3;
        calcularPromedio();
    }

    public Calificacion(long numControl, String materia, 
                        Double parcial1, Double parcial2, Double parcial3) {
        this.numControl = numControl;
        this.materia = materia;
        this.parcial1 = parcial1;
        this.parcial2 = parcial2;
        this.parcial3 = parcial3;
        calcularPromedio();
    }
    
    private void calcularPromedio() {
        int count = 0;
        double sum = 0;
        
        if (parcial1 != null) { sum += parcial1; count++; }
        if (parcial2 != null) { sum += parcial2; count++; }
        if (parcial3 != null) { sum += parcial3; count++; }
        
        this.promedio = count > 0 ? sum / count : 0.0;
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public long getNumControl() { return numControl; }
    public void setNumControl(long numControl) { this.numControl = numControl; }
    
    public String getMateria() { return materia; }
    public void setMateria(String materia) { this.materia = materia; }
    
    public Double getParcial1() { return parcial1; }
    public void setParcial1(Double parcial1) { 
        this.parcial1 = parcial1; 
        calcularPromedio();
    }
    
    public Double getParcial2() { return parcial2; }
    public void setParcial2(Double parcial2) { 
        this.parcial2 = parcial2; 
        calcularPromedio();
    }
    
    public Double getParcial3() { return parcial3; }
    public void setParcial3(Double parcial3) { 
        this.parcial3 = parcial3; 
        calcularPromedio();
    }
    
    public Double getPromedio() { return promedio; }
    
    public boolean esAprobado() {
        return promedio != null && promedio >= 6.0;
    }
}