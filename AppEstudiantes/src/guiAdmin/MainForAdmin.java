package guiAdmin;

import guiBase.*;
import utils.Recargable;

import javax.swing.*;
import java.sql.*;

public class MainForAdmin extends BaseMainFrame implements Recargable {

	private static final long serialVersionUID = 1L;
	private final String nombre;

	public MainForAdmin(String nombre) {
		super("🛠️ Panel de Administrador", "Bienvenido " + nombre);
		this.nombre = nombre;

		// Acciones de los botones
		btnRefrescar.addActionListener(e -> cargarEstudiantes());
		btnAgregar.addActionListener(e -> agregarEstudiante());
		btnEditar.addActionListener(e -> editarEstudiante());
		btnEliminar.addActionListener(e -> eliminarEstudiante());
		btnSendMsg.addActionListener(e -> enviarMensaje());
		btnCalificaciones.addActionListener(e -> gestionarCalificaciones());
		
		btnCalificaciones.setEnabled(false);
		btnCalificaciones.setVisible(false);
	}

	// Acciones que solo puede realizar el administrador
	
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

		new EditarEstudiante(this, id, nombre, apellido, email, grupoId).setVisible(true);
	}

	@Override
	protected void agregarEstudiante() {
		new AgregarEstudiante(this).setVisible(true);
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

	private void gestionarCalificaciones() {
		int usuarioId = obtenerIdUsuario(this.nombre);
		
		if (usuarioId <= 0) {
			JOptionPane.showMessageDialog(this, "Error al obtener datos del administrador");
			return;
		}
		
		// El administrador puede calificar cualquier materia (null = todas)
		new GestionarCalificacionesDeGrupo(null, usuarioId).setVisible(true);
	}

	// ------------ Método Auxiliar ----------------

	private int obtenerIdUsuario(String nombre) {
		String sql = "SELECT id FROM usuarios WHERE nombre = ? AND role = 'ADMIN'";
		
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
}