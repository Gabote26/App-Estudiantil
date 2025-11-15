package guiProfesor;

import guiBase.BaseMainFrame;
import guiBase.EditarEstudiante;
import guiBase.EnviarMensaje;
import guiBase.GestionarEstudiante;
import utils.Recargable;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MainForTeachers extends BaseMainFrame implements Recargable {

	private final String nombre;

	public MainForTeachers(String nombre) {
		super("🎓 Panel del Profesor", "Bienvenido " + nombre);
		this.nombre = nombre;

		// Ocultar botones no disponibles para profesores
		btnAgregar.setVisible(false);
		btnAgregar.setEnabled(false);
		btnEliminar.setVisible(false);
		btnEliminar.setEnabled(false);

		// Asignacion de listeners a los botones
		btnRefrescar.addActionListener(e -> cargarEstudiantes());
		btnEditar.addActionListener(e -> editarEstudiante());
		btnGestionar.addActionListener(e -> gestionarEstudiante());
		btnSendMsg.addActionListener(e -> enviarMensaje());
	}

	// ========= ACCIONES ESPECÍFICAS DEL PROFESOR =========

	@Override
	protected void editarEstudiante() {
		int selectedRow = tableEstudiantes.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Seleccione un estudiante primero.");
			return;
		}

		int id = (int) model.getValueAt(selectedRow, 0);
		String nombre = model.getValueAt(selectedRow, 1).toString();
		String apellido = model.getValueAt(selectedRow, 2).toString();
		String email = model.getValueAt(selectedRow, 3).toString();
		String nombreGrupo = (model.getValueAt(selectedRow, 6) != null) 
				? model.getValueAt(selectedRow, 6).toString() 
				: null;
		int grupoId = (nombreGrupo != null) ? obtenerIdGrupo(nombreGrupo) : -1;

		new EditarEstudiante(this, id, nombre, apellido, email, grupoId).setVisible(true);
	}

	@Override
	protected void enviarMensaje() {
		new EnviarMensaje().setVisible(true);
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

	/**
	 * Actualizar el grupo de un estudiante en la base de datos mediante una consulta sql
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
}