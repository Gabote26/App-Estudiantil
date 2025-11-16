package modelos;

import java.time.*;

public class Mensaje {
	private int id;
	private int remitenteId;
	private String tipoMensaje; // "anuncio", "calificacion", "asistencia"
	private String asunto;
	private String contenido;
	private LocalDateTime fechaEnvio;

	// Crear nuevo mensaje
	public Mensaje(int remitenteId, String tipoMensaje, String asunto, String contenido) {
		this.remitenteId = remitenteId;
		this.tipoMensaje = tipoMensaje;
		this.asunto = asunto;
		this.contenido = contenido;
	}

	// Cargar mensaje desde la base de datos
	public Mensaje(int id, int remitenteId, String tipoMensaje, String asunto, String contenido,
			LocalDateTime fechaEnvio) {
		this.id = id;
		this.remitenteId = remitenteId;
		this.tipoMensaje = tipoMensaje;
		this.asunto = asunto;
		this.contenido = contenido;
		this.fechaEnvio = fechaEnvio;
	}

	// Getters y Setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRemitenteId() {
		return remitenteId;
	}

	public void setRemitenteId(int remitenteId) {
		this.remitenteId = remitenteId;
	}

	public String getTipoMensaje() {
		return tipoMensaje;
	}

	public void setTipoMensaje(String tipoMensaje) {
		this.tipoMensaje = tipoMensaje;
	}

	public String getAsunto() {
		return asunto;
	}

	public void setAsunto(String asunto) {
		this.asunto = asunto;
	}

	public String getContenido() {
		return contenido;
	}

	public void setContenido(String contenido) {
		this.contenido = contenido;
	}

	public LocalDateTime getFechaEnvio() {
		return fechaEnvio;
	}

	public void setFechaEnvio(LocalDateTime fechaEnvio) {
		this.fechaEnvio = fechaEnvio;
	}
}