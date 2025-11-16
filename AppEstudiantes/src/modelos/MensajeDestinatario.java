package modelos;

import java.time.*;

public class MensajeDestinatario {
    private int id;
    private int mensajeId;
    private int destinatarioId;
    private boolean leido;
    private LocalDateTime fechaLectura;
    
    public MensajeDestinatario(int mensajeId, int destinatarioId) {
        this.mensajeId = mensajeId;
        this.destinatarioId = destinatarioId;
        this.leido = false;
    }
    
    public MensajeDestinatario(int id, int mensajeId, int destinatarioId, 
                               boolean leido, LocalDateTime fechaLectura) {
        this.id = id;
        this.mensajeId = mensajeId;
        this.destinatarioId = destinatarioId;
        this.leido = leido;
        this.fechaLectura = fechaLectura;
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getMensajeId() { return mensajeId; }
    public void setMensajeId(int mensajeId) { this.mensajeId = mensajeId; }
    
    public int getDestinatarioId() { return destinatarioId; }
    public void setDestinatarioId(int destinatarioId) { this.destinatarioId = destinatarioId; }
    
    public boolean isLeido() { return leido; }
    public void setLeido(boolean leido) { this.leido = leido; }
    
    public LocalDateTime getFechaLectura() { return fechaLectura; }
    public void setFechaLectura(LocalDateTime fechaLectura) { this.fechaLectura = fechaLectura; }
}