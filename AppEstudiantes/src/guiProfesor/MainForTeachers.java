package guiProfesor;

import guiBase.*;
import utils.Recargable;

import java.awt.*;
import javax.swing.*;
import java.sql.*;

public class MainForTeachers extends BaseMainFrame implements Recargable {

	private static final long serialVersionUID = 1L;
	private final String nombre;

	public MainForTeachers(String nombre) {
		super("🎓 Panel del Profesor", "Bienvenido " + nombre);
		this.nombre = nombre;
		this.setSize(1200, 650);
		
		actionPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

		// Eliminar los botones que no deben tener los profesores
		actionPanel.remove(btnAgregar);
		actionPanel.remove(btnEliminar);
		actionPanel.remove(btnEditar);
		actionPanel.remove(btnGestionar);

		// Acciones que realizan los botones
		btnRefrescar.addActionListener(e -> cargarEstudiantes());
		btnSendMsg.addActionListener(e -> enviarMensaje());
		btnCalificaciones.addActionListener(e -> gestionarCalificaciones());

		// Añadir botones en el orden correcto
		actionPanel.add(btnRefrescar);
		actionPanel.add(btnCalificaciones);
		agregarBotonAsistencias();
		actionPanel.add(btnSendMsg);

		cargarEstudiantes();
	}

	// Acciones especificas que puede realizar un profesor, las que no se cumplen,
	// los metodos existen para mantener compatibilidad con la clase base

	@Override
	protected void editarEstudiante() {
	}

	@Override
	protected void enviarMensaje() {
		int usuarioId = obtenerIdUsuario(this.nombre);
		if (usuarioId > 0) {
			new EnviarMensaje(usuarioId).setVisible(true);
		} else {
			JOptionPane.showMessageDialog(this, "Error al obtener ID de usuario");
		}
	}

	// ------- Gestion de Calificaciones -------

	private void gestionarCalificaciones() {
		String materiaAsignada = obtenerMateriaProfesor();
		int usuarioId = obtenerIdUsuario(this.nombre);

		if (usuarioId <= 0) {
			JOptionPane.showMessageDialog(this, "Error al obtener datos del profesor");
			return;
		}

		new GestionarCalificacionesDeGrupo(materiaAsignada, usuarioId).setVisible(true);
	}

	// ------- Gestion de Asistencias -------

	private void agregarBotonAsistencias() {
		String materiaAsignada = obtenerMateriaProfesor();

		utils.RoundedButton btnAsistencias = new utils.RoundedButton("📋 Gestionar Asistencias", 20);

		btnAsistencias.addActionListener(e -> {
			new GestionarAsistencias(materiaAsignada).setVisible(true);
		});

		actionPanel.add(btnAsistencias);
	}

	private String obtenerMateriaProfesor() {
		String sql = "SELECT materia_asignada FROM usuarios WHERE nombre = ? AND role = 'PROFESOR'";

		try (Connection cn = connectionDB.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {

			ps.setString(1, this.nombre);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				String materia = rs.getString("materia_asignada");
				return (materia != null && !materia.isEmpty()) ? materia : null;
			}

		} catch (SQLException e) {
			System.err.println("Error al obtener materia: " + e.getMessage());
		}

		return null;
	}

	private int obtenerIdUsuario(String nombre) {
		String sql = "SELECT id FROM usuarios WHERE nombre = ? AND role = 'PROFESOR'";

		try (Connection cn = connectionDB.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {

			ps.setString(1, nombre);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getInt("id");
			}

		} catch (SQLException e) {
			System.err.println("Error al obtener ID: " + e.getMessage());
		}

		return -1;
	}

	public void actualizarGrupoEstudiante(int idEstudiante, int nuevoGrupoId) {
		String query = "UPDATE usuarios SET grupo_id = ? WHERE id = ?";
		try (Connection cn = connectionDB.conectar(); PreparedStatement ps = cn.prepareStatement(query)) {
			ps.setInt(1, nuevoGrupoId);
			ps.setInt(2, idEstudiante);
			ps.executeUpdate();
			JOptionPane.showMessageDialog(this, "✅ Grupo actualizado correctamente.");
			cargarEstudiantes();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Error al actualizar grupo:\n" + e.getMessage(), "Error SQL",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	// Métodos de compatibilidad sin funcion
	@Override
	protected void eliminarEstudiante() {
	}

	@Override
	protected void agregarEstudiante() {
	}
}