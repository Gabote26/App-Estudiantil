package guiEstudiante;

import db.ConexionMysql;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class VerAsistencias extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tableAsistencias;
	private DefaultTableModel model;
	private JComboBox<String> cbMateria;
	private JLabel lblEstadisticas;
	private JPanel panelEstadisticas;

	private final ConexionMysql connectionDB = new ConexionMysql();
	private final long numControl;
	private final String nombreCompleto;

	public VerAsistencias(long numControl, String nombre, String apellido) {
		this.numControl = numControl;
		this.nombreCompleto = apellido + " " + nombre;

		setTitle("📊 Mis Asistencias");
		setSize(1000, 700);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		contentPane = new JPanel();
		contentPane.setBackground(new Color(250, 250, 252));
		contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// ------- Header -------
		JPanel headerPanel = new JPanel(null);
		headerPanel.setBounds(0, 0, 984, 90);
		headerPanel.setBackground(new Color(52, 73, 94));
		contentPane.add(headerPanel);

		JLabel lblTitulo = new JLabel("📊 Mi Registro de Asistencias");
		lblTitulo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 26));
		lblTitulo.setForeground(new Color(255, 215, 0));
		lblTitulo.setBounds(25, 15, 450, 35);
		headerPanel.add(lblTitulo);

		JLabel lblAlumno = new JLabel("👤 " + nombreCompleto);
		lblAlumno.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
		lblAlumno.setForeground(Color.WHITE);
		lblAlumno.setBounds(25, 55, 400, 25);
		headerPanel.add(lblAlumno);

		JLabel lblControl = new JLabel("🔑 No. Control: " + numControl);
		lblControl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
		lblControl.setForeground(Color.WHITE);
		lblControl.setBounds(650, 55, 300, 25);
		headerPanel.add(lblControl);

		// ------- Panel de estadisticas -------
		panelEstadisticas = new JPanel();
		panelEstadisticas.setLayout(new GridLayout(1, 4, 15, 0));
		panelEstadisticas.setBounds(15, 105, 954, 100);
		panelEstadisticas.setBackground(new Color(250, 250, 252));
		contentPane.add(panelEstadisticas);

		// ------- Panel de filtros -------
		JPanel filterPanel = new JPanel(null);
		filterPanel.setBounds(15, 220, 954, 60);
		filterPanel.setBackground(Color.WHITE);
		filterPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(220, 220, 220)), new EmptyBorder(10, 15, 10, 15)));
		contentPane.add(filterPanel);

		JLabel lblFiltrar = new JLabel("📚 Filtrar por materia:");
		lblFiltrar.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
		lblFiltrar.setBounds(15, 15, 150, 25);
		filterPanel.add(lblFiltrar);

		cbMateria = new JComboBox<>(
				new String[] { "Todas las materias", "Lengua", "Humanidades", "Matematicas", "Sociales", "Ciencias" });
		cbMateria.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		cbMateria.setBounds(175, 15, 200, 30);
		cbMateria.addActionListener(e -> cargarAsistencias());
		filterPanel.add(cbMateria);

		JButton btnRefrescar = new JButton("🔄 Actualizar");
		btnRefrescar.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
		btnRefrescar.setBounds(400, 15, 130, 30);
		btnRefrescar.setBackground(new Color(52, 152, 219));
		btnRefrescar.setForeground(Color.WHITE);
		btnRefrescar.setFocusPainted(false);
		btnRefrescar.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnRefrescar.addActionListener(e -> {
			cargarEstadisticasGenerales();
			cargarAsistencias();
		});
		filterPanel.add(btnRefrescar);

		lblEstadisticas = new JLabel("");
		lblEstadisticas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblEstadisticas.setForeground(new Color(100, 100, 100));
		lblEstadisticas.setBounds(560, 15, 380, 30);
		filterPanel.add(lblEstadisticas);

		// ------- Tabla de asistencias -------
		String[] columnas = { "Fecha", "Materia", "Estado" };
		model = new DefaultTableModel(columnas, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		tableAsistencias = new JTable(model);
		tableAsistencias.setRowHeight(38);
		tableAsistencias.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		tableAsistencias.setSelectionBackground(new Color(220, 240, 255));
		tableAsistencias.setGridColor(new Color(240, 240, 240));
		tableAsistencias.setShowGrid(true);

		// Header de la tabla
		JTableHeader header = tableAsistencias.getTableHeader();
		header.setBackground(new Color(245, 245, 250));
		header.setFont(new Font("Segoe UI", Font.BOLD, 14));
		header.setForeground(new Color(70, 70, 70));
		header.setPreferredSize(new Dimension(header.getWidth(), 40));

		// Renderer personalizado para la columna Estado
		DefaultTableCellRenderer estadoRenderer = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				if (column == 2 && value != null) {
					String estado = value.toString();
					setHorizontalAlignment(SwingConstants.CENTER);
					setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));

					if (!isSelected) {
						if (estado.contains("Presente")) {
							setBackground(new Color(230, 255, 230));
							setForeground(new Color(0, 128, 0));
						} else if (estado.contains("Faltó")) {
							setBackground(new Color(255, 230, 230));
							setForeground(new Color(200, 0, 0));
						} else if (estado.contains("Permiso")) {
							setBackground(new Color(255, 250, 220));
							setForeground(new Color(200, 140, 0));
						}
					} else {
						setBackground(table.getSelectionBackground());
						setForeground(table.getSelectionForeground());
					}
				} else {
					if (!isSelected) {
						setBackground(Color.WHITE);
						setForeground(Color.BLACK);
					}
					if (column == 0) {
						setHorizontalAlignment(SwingConstants.CENTER);
					} else if (column == 1) {
						setHorizontalAlignment(SwingConstants.LEFT);
					}
				}

				return c;
			}
		};

		tableAsistencias.setDefaultRenderer(Object.class, estadoRenderer);

		JScrollPane scrollPane = new JScrollPane(tableAsistencias);
		scrollPane.setBounds(15, 295, 954, 345);
		scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
		contentPane.add(scrollPane);

		// Cargar datos iniciales
		cargarEstadisticasGenerales();
		cargarAsistencias();
	}

	private void cargarEstadisticasGenerales() {
		panelEstadisticas.removeAll();

		String sql = """
				    SELECT
				        COUNT(*) as total,
				        SUM(CASE WHEN estado = 'A' THEN 1 ELSE 0 END) as presentes,
				        SUM(CASE WHEN estado = 'F' THEN 1 ELSE 0 END) as faltas,
				        SUM(CASE WHEN estado = 'P' THEN 1 ELSE 0 END) as permisos
				    FROM asistencias
				    WHERE num_control = ?
				""";

		try (Connection cn = connectionDB.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {

			ps.setLong(1, numControl);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				int total = rs.getInt("total");
				int presentes = rs.getInt("presentes");
				int faltas = rs.getInt("faltas");
				int permisos = rs.getInt("permisos");

				double porcentaje = total > 0 ? (presentes * 100.0) / total : 0;

				panelEstadisticas.add(
						crearTarjetaEstadistica("✅ Asistencias", String.valueOf(presentes), new Color(46, 204, 113)));

				panelEstadisticas
						.add(crearTarjetaEstadistica("❌ Faltas", String.valueOf(faltas), new Color(231, 76, 60)));

				panelEstadisticas
						.add(crearTarjetaEstadistica("📝 Permisos", String.valueOf(permisos), new Color(241, 196, 15)));

				panelEstadisticas.add(crearTarjetaEstadistica("📊 % Asistencia", String.format("%.1f%%", porcentaje),
						new Color(155, 89, 182)));
			}

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Error al cargar estadísticas:\n" + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}

		panelEstadisticas.revalidate();
		panelEstadisticas.repaint();
	}

	private JPanel crearTarjetaEstadistica(String titulo, String valor, Color color) {
		JPanel tarjeta = new JPanel(new BorderLayout(5, 5));
		tarjeta.setBackground(Color.WHITE);
		tarjeta.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(220, 220, 220), 2), new EmptyBorder(12, 12, 12, 12)));

		JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
		lblTitulo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
		lblTitulo.setForeground(new Color(100, 100, 100));

		JLabel lblValor = new JLabel(valor, SwingConstants.CENTER);
		lblValor.setFont(new Font("Segoe UI Emoji", Font.BOLD, 32));
		lblValor.setForeground(color);

		tarjeta.add(lblTitulo, BorderLayout.NORTH);
		tarjeta.add(lblValor, BorderLayout.CENTER);

		return tarjeta;
	}

	private void cargarAsistencias() {
		model.setRowCount(0);

		String materiaSeleccionada = cbMateria.getSelectedItem().toString();
		boolean todasMaterias = "Todas las materias".equals(materiaSeleccionada);

		String sql = """
				    SELECT fecha, materia, estado
				    FROM asistencias
				    WHERE num_control = ?
				""" + (todasMaterias ? "" : " AND materia = ?") + """
				    ORDER BY fecha DESC
				""";

		try (Connection cn = connectionDB.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {

			ps.setLong(1, numControl);
			if (!todasMaterias) {
				ps.setString(2, materiaSeleccionada);
			}

			ResultSet rs = ps.executeQuery();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			int contador = 0;

			while (rs.next()) {
				Date fecha = rs.getDate("fecha");
				String materia = rs.getString("materia");
				String estado = rs.getString("estado");

				LocalDate localDate = fecha.toLocalDate();
				String fechaFormateada = localDate.format(formatter);
				String estadoTexto = obtenerEstadoConEmoji(estado);

				model.addRow(new Object[] { fechaFormateada, materia, estadoTexto });

				contador++;
			}

			// Actualizar label de estadísticas
			if (contador == 0) {
				lblEstadisticas.setText("ℹ️ No hay registros de asistencias");
			} else {
				String textoMateria = todasMaterias ? "todas las materias" : materiaSeleccionada;
				lblEstadisticas.setText(String.format("Mostrando %d registro(s) de %s", contador, textoMateria));
			}

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Error al cargar asistencias:\n" + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private String obtenerEstadoConEmoji(String estado) {
		return switch (estado) {
		case "A" -> "✅ Presente";
		case "F" -> "❌ Faltó";
		case "P" -> "📝 Permiso";
		default -> estado;
		};
	}
}