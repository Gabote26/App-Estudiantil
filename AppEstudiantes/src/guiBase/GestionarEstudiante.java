package guiBase;

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
		lblTitulo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 24));
		lblTitulo.setAlignmentX(0.5f);
		titleSection.add(lblTitulo);

		JLabel lblAlumno = new JLabel("👤 Alumno: " + nombre + " " + apellido);
		lblAlumno.setBounds(38, 65, 480, 28);
		lblAlumno.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 17));
		lblAlumno.setForeground(Color.WHITE);
		titleSection.add(lblAlumno);

		JLabel lblNumDe = new JLabel("🔑 Num. de Control: " + numControl);
		lblNumDe.setForeground(Color.WHITE);
		lblNumDe.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 17));
		lblNumDe.setBounds(742, 65, 374, 28);
		titleSection.add(lblNumDe);

		// ---------------- TABLA CALIFICACIONES ----------------
		model = new DefaultTableModel(new Object[] { "Materia", "Parcial 1", "Parcial 2", "Parcial 3", 
				"Promedio", "Estado" }, 0);
		tablaEstudiante = new JTable(model);
		tablaEstudiante.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(tablaEstudiante);
		scrollPane.setBounds(43, 30, 1094, 196);
		container.add(scrollPane);

		// ---------------- TABLA FALTAS ----------------
		model2 = new DefaultTableModel(new Object[] { "Materia", "Asistencias", "Faltas", 
				"Justificantes", "% Asistencia" }, 0);
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
		// Cargar calificaciones
		model.setRowCount(0);
		String queryCalificaciones = """
			SELECT materia, parcial_1, parcial_2, parcial_3, promedio
			FROM calificaciones
			WHERE num_control = ?
			ORDER BY materia
			""";

		try (Connection cn = connectionDB.conectar(); 
		     PreparedStatement ps = cn.prepareStatement(queryCalificaciones)) {

			ps.setString(1, numControl);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					String materia = rs.getString("materia");
					Double p1 = (Double) rs.getObject("parcial_1");
					Double p2 = (Double) rs.getObject("parcial_2");
					Double p3 = (Double) rs.getObject("parcial_3");
					Double promedio = (Double) rs.getObject("promedio");

					String p1Str = (p1 != null) ? String.format("%.2f", p1) : "-";
					String p2Str = (p2 != null) ? String.format("%.2f", p2) : "-";
					String p3Str = (p3 != null) ? String.format("%.2f", p3) : "-";
					String promedioStr = (promedio != null) ? String.format("%.2f", promedio) : "-";
					String estado = (promedio != null && promedio >= 6.0) ? "Aprobado" : "Reprobado";

					Object[] filaCalificaciones = { materia, p1Str, p2Str, p3Str, promedioStr, estado };
					model.addRow(filaCalificaciones);
				}
			}

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Error al cargar las calificaciones:\n" + e.getMessage(), 
					"Error SQL", JOptionPane.ERROR_MESSAGE);
		}

		// Cargar resumen de asistencias
		model2.setRowCount(0);
		String queryAsistencias = """
			SELECT 
				materia,
				SUM(CASE WHEN estado = 'A' THEN 1 ELSE 0 END) as asistencias,
				SUM(CASE WHEN estado = 'F' THEN 1 ELSE 0 END) as faltas,
				SUM(CASE WHEN estado = 'P' THEN 1 ELSE 0 END) as justificantes,
				COUNT(*) as total
			FROM asistencias
			WHERE num_control = ?
			GROUP BY materia
			""";

		try (Connection cn = connectionDB.conectar(); 
		     PreparedStatement ps = cn.prepareStatement(queryAsistencias)) {

			ps.setString(1, numControl);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					String materia = rs.getString("materia");
					int asistencias = rs.getInt("asistencias");
					int faltas = rs.getInt("faltas");
					int justificantes = rs.getInt("justificantes");
					int total = rs.getInt("total");

					double porcentaje = total > 0 ? 
							((double)(asistencias + justificantes) / total) * 100 : 0;

					Object[] filaAsistencias = { 
						materia, 
						asistencias, 
						faltas, 
						justificantes, 
						String.format("%.1f%%", porcentaje) 
					};
					model2.addRow(filaAsistencias);
				}
			}

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Error al cargar las asistencias:\n" + e.getMessage(), 
					"Error SQL", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void editarCalificaciones() {
		JOptionPane.showMessageDialog(this, 
			"Usa el botón 'Gestionar Calificaciones' desde el panel principal del profesor/admin",
			"Información",
			JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void editarFaltas() {
		JOptionPane.showMessageDialog(this, 
			"Usa el botón 'Gestionar Asistencias' desde el panel principal del profesor",
			"Información",
			JOptionPane.INFORMATION_MESSAGE);
	}
}