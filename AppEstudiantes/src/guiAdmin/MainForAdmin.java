package guiAdmin;

import guiBase.BaseMainFrame;
import guiProfesor.AgregarEstudiante;
import guiProfesor.EditarEstudiante;
import guiProfesor.EnviarMensaje;
import guiProfesor.GestionarEstudiante;
import utils.Recargable;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MainForAdmin extends BaseMainFrame implements Recargable {

	private final String nombre;

	public MainForAdmin(String nombre) {
		super("🛠️ Panel de Administrador", "Bienvenido " + nombre);
		this.nombre = nombre;

		// Asignar listeners a los botones
		btnRefrescar.addActionListener(e -> cargarEstudiantes());
		btnAgregar.addActionListener(e -> agregarEstudiante());
		btnEditar.addActionListener(e -> editarEstudiante());
		btnEliminar.addActionListener(e -> eliminarEstudiante());
		btnGestionar.addActionListener(e -> gestionarEstudiante());
		btnSendMsg.addActionListener(e -> enviarMensaje());
	}

	// ========== ACCIONES ESPECÍFICAS DE ADMINISTRADOR ==========

	@Override
	protected void eliminarEstudiante() {
		int selectedRow = tableEstudiantes.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Seleccione un estudiante primero.");
			return;
		}

		int confirm = JOptionPane.showConfirmDialog(this, 
				"¿Seguro que desea eliminar este estudiante?",
				"Confirmar eliminación", 
				JOptionPane.YES_NO_OPTION);

		if (confirm != JOptionPane.YES_OPTION)
			return;

		int id = (int) model.getValueAt(selectedRow, 0);
		String query = "DELETE FROM usuarios WHERE id = ?";

		try (Connection cn = connectionDB.conectar(); 
			 PreparedStatement ps = cn.prepareStatement(query)) {
			ps.setInt(1, id);
			ps.executeUpdate();
			JOptionPane.showMessageDialog(this, "✅ Estudiante eliminado correctamente.");
			cargarEstudiantes();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, 
					"Error al eliminar estudiante:\n" + e.getMessage(), 
					"Error SQL",
					JOptionPane.ERROR_MESSAGE);
		}
	}

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

		// Pasa la referencia de esta ventana (Recargable)
		new EditarEstudiante(this, id, nombre, apellido, email, grupoId).setVisible(true);
	}

	@Override
	protected void agregarEstudiante() {
		new AgregarEstudiante(this).setVisible(true);
	}

	@Override
	protected void enviarMensaje() {
		new EnviarMensaje().setVisible(true);
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
				"¿Deseas ver el historial académico?", 
				"Gestión Académica",
				JOptionPane.YES_NO_OPTION);

		if (opcion == JOptionPane.YES_OPTION)
			new GestionarEstudiante(noControl, nombre, apellido).setVisible(true);
	}
}