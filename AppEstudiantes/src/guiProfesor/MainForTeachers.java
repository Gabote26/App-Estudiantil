package guiProfesor;

import guiBase.BaseMainFrame;
import guiBase.EditarEstudiante;
import guiBase.EnviarMensaje;
import guiBase.GestionarEstudiante;
import utils.Recargable;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainForTeachers extends BaseMainFrame implements Recargable {

	private static final long serialVersionUID = 1L;
	private final String nombre;

	public MainForTeachers(String nombre) {
		super("🎓 Panel del Profesor", "Bienvenido " + nombre);
		this.nombre = nombre;

		// Ocultar botones no disponibles para profesores
		btnAgregar.setVisible(false);
		btnAgregar.setEnabled(false);
		btnEliminar.setVisible(false);
		btnEliminar.setEnabled(false);
		btnEditar.setEnabled(false);
		btnEditar.setVisible(false);
		
		btnRefrescar.addActionListener(e -> cargarEstudiantes());
		btnGestionar.addActionListener(e -> gestionarEstudiante());
		btnSendMsg.addActionListener(e -> enviarMensaje());
		agregarBotonAsistencias();
	}

	// ========= ACCIONES ESPECÍFICAS DEL PROFESOR =========

	@Override
	protected void enviarMensaje() {
		int usuarioId = obtenerIdUsuario(this.nombre);
		if (usuarioId > 0) {
			new EnviarMensaje(usuarioId).setVisible(true);
		} else {
			JOptionPane.showMessageDialog(this, "Error al obtener ID de usuario");
		}
	}

	@Override
	protected void agregarEstudiante() {
		JOptionPane.showMessageDialog(this,
				"❌ Los profesores no pueden agregar estudiantes.\nEsta opción está reservada al administrador.",
				"Acción no permitida", JOptionPane.WARNING_MESSAGE);
	}

	@Override
	protected void eliminarEstudiante() {
		JOptionPane.showMessageDialog(this, 
				"❌ Los profesores no pueden eliminar estudiantes.", 
				"Acción no permitida",
				JOptionPane.WARNING_MESSAGE);
	}

	// ========= GESTIÓN DE ESTUDIANTES =========

	private void gestionarEstudiante() {
		int selectedRow = tableEstudiantes.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Seleccione un estudiante primero.");
			return;
		}

		String nombre = model.getValueAt(selectedRow, 1).toString();
		String apellido = model.getValueAt(selectedRow, 2).toString();
		String email = model.getValueAt(selectedRow, 3).toString();
		String noControl = model.getValueAt(selectedRow, 5).toString();

		JOptionPane.showMessageDialog(this,
				"📋 Detalles del Estudiante:\n\n" 
				+ "Nombre: " + nombre + " " + apellido + "\n" 
				+ "Email: " + email + "\n" 
				+ "No. Control: " + noControl,
				"Información del Estudiante", 
				JOptionPane.INFORMATION_MESSAGE);

		int opcion = JOptionPane.showConfirmDialog(this, 
				"¿Deseas gestionar el historial académico?",
				"Gestión Académica", 
				JOptionPane.YES_NO_OPTION);

		if (opcion == JOptionPane.YES_OPTION)
			new GestionarEstudiante(noControl, nombre, apellido).setVisible(true);
	}

	// ========= GESTIÓN DE ASISTENCIAS =========

	private void agregarBotonAsistencias() {
		String materiaAsignada = obtenerMateriaProfesor();
		
		utils.RoundedButton btnAsistencias = new utils.RoundedButton("📋 Gestionar Asistencias", 20);
		btnAsistencias.setBounds(10, 50, 190, 30);
		btnAsistencias.setBackground(new java.awt.Color(245, 245, 245));
		btnAsistencias.setForeground(new java.awt.Color(48, 48, 48));
		btnAsistencias.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.PLAIN, 13));
		btnAsistencias.addActionListener(e -> {
			new GestionarAsistencias(materiaAsignada).setVisible(true);
		});
		
		actionPanel.add(btnAsistencias);
		
		// Ajustar las posiciones para evitar sobreposiciones o mal diseño
		btnRefrescar.setBounds(210, 10, 160, 30);
		btnEditar.setBounds(380, 10, 160, 30);
		btnSendMsg.setBounds(550, 10, 160, 30);
		btnGestionar.setBounds(720, 10, 180, 30);
	}

	private String obtenerMateriaProfesor() {
		String sql = "SELECT materia_asignada FROM usuarios WHERE nombre = ? AND role = 'PROFESOR'";
		
		try (Connection cn = connectionDB.conectar();
		     PreparedStatement ps = cn.prepareStatement(sql)) {
			
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
		
		try (Connection cn = connectionDB.conectar();
		     PreparedStatement ps = cn.prepareStatement(sql)) {
			
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

	/**
	 * Actualizar el grupo de un estudiante en la base de datos
	 * @param idEstudiante ID del estudiante a actualizar
	 * @param nuevoGrupoId ID del nuevo grupo
	 */
	public void actualizarGrupoEstudiante(int idEstudiante, int nuevoGrupoId) {
		String query = "UPDATE usuarios SET grupo_id = ? WHERE id = ?";
		try (Connection cn = connectionDB.conectar(); 
		     PreparedStatement ps = cn.prepareStatement(query)) {
			ps.setInt(1, nuevoGrupoId);
			ps.setInt(2, idEstudiante);
			ps.executeUpdate();
			JOptionPane.showMessageDialog(this, "✅ Grupo actualizado correctamente.");
			cargarEstudiantes();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, 
					"Error al actualizar grupo:\n" + e.getMessage(), 
					"Error SQL",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	protected void editarEstudiante() {} // Disabled for teachers
}