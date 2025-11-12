package guiProfesor;

import db.ConexionMysql;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class GestionarEstudiante extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	DefaultTableModel model, model2;
	JTable tablaEstudiante, tablaFaltasEstudiante;
	private final ConexionMysql connectionDB = new ConexionMysql();
	private final String numControl;
	private JPanel actionPanel;
	private JButton btnRefrescar, btnEditarFaltas, btnEditarCalificaciones;

	/**
	 * Create the frame.
	 */
	public GestionarEstudiante(String numControl, String nombre, String apellido) {
		this.numControl = numControl;
		setTitle("Gestionar Estudiantes");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(1200, 700);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel container = new JPanel();
		container.setBounds(0, 136, 1186, 527);
		container.setLayout(null);
		contentPane.add(container);

		JPanel titleSection = new JPanel();
		titleSection.setBackground(new Color(52, 73, 94));
		titleSection.setBounds(0, 0, 1186, 137);
		contentPane.add(titleSection);
		titleSection.setLayout(null);

		JLabel lblTitulo = new JLabel("📚 HISTORIAL ACADÉMICO");
		lblTitulo.setBounds(393, 27, 306, 28);
		lblTitulo.setForeground(new Color(255, 215, 0));
		lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
		lblTitulo.setAlignmentX(0.5f);
		titleSection.add(lblTitulo);

		JLabel lblAlumno = new JLabel("👤 Alumno: " + nombre + " " + apellido);
		lblAlumno.setBounds(38, 65, 480, 28);
		lblAlumno.setFont(new Font("Arial", Font.PLAIN, 17));
		lblAlumno.setForeground(Color.WHITE);
		titleSection.add(lblAlumno);

		JLabel lblNumDe = new JLabel("🔑 Num. de Control: " + numControl);
		lblNumDe.setForeground(Color.WHITE);
		lblNumDe.setFont(new Font("Arial", Font.PLAIN, 17));
		lblNumDe.setBounds(742, 65, 374, 28);
		titleSection.add(lblNumDe);

		// ---------------- TABLA CALIFICACIONES ----------------
		model = new DefaultTableModel(new Object[] { "calf_lengua", "calf_humanidades", "calf_matematicas",
				"calf_sociales", "calf_ciencias", "Promedio", "Estado" }, 0);
		tablaEstudiante = new JTable(model);
		tablaEstudiante.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(tablaEstudiante);
		scrollPane.setBounds(43, 30, 1094, 196);
		container.add(scrollPane);

		// ---------------- TABLA FALTAS ----------------
		model2 = new DefaultTableModel(new Object[] { "faltas_lengua", "faltas_humanidades", "faltas_matematicas",
				"faltas_sociales", "faltas_ciencias", "Estado" }, 0);
		tablaFaltasEstudiante = new JTable(model2);
		tablaFaltasEstudiante.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPaneFaltas = new JScrollPane(tablaFaltasEstudiante);
		scrollPaneFaltas.setBounds(43, 255, 1094, 196);
		container.add(scrollPaneFaltas);
		cargarDatosEstudiante();

		// ---------------- PANEL DE BOTONES ----------------
		actionPanel = new JPanel();
		actionPanel.setBackground(new Color(245, 245, 245));
		actionPanel.setBounds(43, 480, 1094, 31);
		actionPanel.setVisible(true);
		container.add(actionPanel);

		btnRefrescar = new JButton("🔄 Refrescar Lista");
		btnEditarCalificaciones = new JButton("✏️ Editar Calificaciones");
		btnEditarFaltas = new JButton("✏️ Editar Faltas");

		actionPanel.add(btnRefrescar);
		actionPanel.add(btnEditarCalificaciones);
		actionPanel.add(btnEditarFaltas);
		
		// Button Actions
		btnRefrescar.addActionListener(e -> cargarDatosEstudiante());
		btnEditarCalificaciones.addActionListener(e -> editarCalificaciones());
		btnEditarFaltas.addActionListener(e -> editarFaltas());
		
	}

	public void cargarDatosEstudiante() {
		// Calificaciones
		model.setRowCount(0);
		String queryCalificaciones = "SELECT c.calf_lengua, c.calf_humanidades, c.calf_matematicas, "
				+ "c.calf_sociales, c.calf_ciencias " + "FROM calificaciones c "
				+ "JOIN usuarios u ON u.no_control = c.num_control " + "WHERE c.num_control = ?";

		try (Connection cn = connectionDB.conectar(); PreparedStatement ps = cn.prepareStatement(queryCalificaciones)) {

			ps.setString(1, numControl);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					double lengua = rs.getDouble("calf_lengua");
					double humanidades = rs.getDouble("calf_humanidades");
					double matematicas = rs.getDouble("calf_matematicas");
					double sociales = rs.getDouble("calf_sociales");
					double ciencias = rs.getDouble("calf_ciencias");

					double promedio = (lengua + humanidades + matematicas + sociales + ciencias) / 5.0;
					String estado = (promedio >= 6.0) ? "Aprobado" : "Reprobado";

					Object[] filaCalificaciones = { lengua, humanidades, matematicas, sociales, ciencias,
							String.format("%.2f", promedio), estado };
					model.addRow(filaCalificaciones);
				}
			}

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Error al cargar las calificaciones:\n" + e.getMessage(), "Error SQL",
					JOptionPane.ERROR_MESSAGE);
		}

		// Faltas
		model2.setRowCount(0);
		String queryFaltas = "SELECT f.faltas_lengua, f.faltas_humanidades, f.faltas_matematicas, "
				+ "f.faltas_sociales, f.faltas_ciencias " + "FROM faltas f "
				+ "JOIN usuarios u ON u.no_control = f.num_control " + "WHERE f.num_control = ?";

		try (Connection cn = connectionDB.conectar(); PreparedStatement ps = cn.prepareStatement(queryFaltas)) {

			ps.setString(1, numControl);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					int lengua = rs.getInt("faltas_lengua");
					int humanidades = rs.getInt("faltas_humanidades");
					int matematicas = rs.getInt("faltas_matematicas");
					int sociales = rs.getInt("faltas_sociales");
					int ciencias = rs.getInt("faltas_ciencias");

					// double promedio = (lengua + humanidades + matematicas + sociales + ciencias)
					// / 5.0;
					// String estado = (promedio >= 6.0) ? "Aprobado" : "Reprobado";

					Object[] filaCalificaciones = { lengua, humanidades, matematicas, sociales, ciencias, "Test" };
					model2.addRow(filaCalificaciones);
				}
			}

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Error al cargar las faltas:\n" + e.getMessage(), "Error SQL",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void editarCalificaciones() {
		GestionarCalificaciones ventana = new GestionarCalificaciones(numControl);
		ventana.setVisible(true);
	}
	
	private void editarFaltas() {
		GestionarFaltas ventana = new GestionarFaltas(numControl);
		ventana.setVisible(true);
	}

}
