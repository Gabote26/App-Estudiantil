package guiProfesor;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import db.ConexionMysql;
import java.awt.*;
import java.sql.*;

public class MainForTeachers extends JFrame {

	private static final long serialVersionUID = 1L;

	// Definiciones de variables principales.
	private JComboBox<String> cbGrupos;
	private int grupoSeleccionadoId = -1;
	private JTable tableEstudiantes;
	private DefaultTableModel model;
	private final ConexionMysql connectionDB = new ConexionMysql();

	// Action panel y botones para gestion
	private JPanel actionPanel;
	private JButton btnRefrescar, btnVer, btnAgregar, btnEliminar, btnEditar;
	private JButton btnSeleccionarEstudiante;

	public MainForTeachers() {
		setTitle("Panel del Profesor - Gestión de Estudiantes");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1250, 500);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		
		// A continuacion compañeros, se definen por secciones los elementos que conforman el programa base.

		// ---------------- PANEL SUPERIOR ----------------
		JPanel headerPanel = new JPanel();
		headerPanel.setBackground(new Color(42, 34, 71));
		headerPanel.setPreferredSize(new Dimension(700, 60));
		JLabel title = new JLabel("Gestión de Estudiantes");
		title.setFont(new Font("Roboto", Font.BOLD, 22));
		title.setForeground(Color.WHITE);
		headerPanel.add(title);
		getContentPane().add(headerPanel, BorderLayout.NORTH);

		// ---------------- PANEL DE BÚSQUEDA ----------------
		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		searchPanel.setBackground(new Color(240, 240, 240));

		JLabel lblBuscar = new JLabel("Buscar por:");
		lblBuscar.setFont(new Font("Roboto", Font.PLAIN, 14));
		searchPanel.add(lblBuscar);

		String[] criterios = { "Todos", "Nombre", "Apellido", "Email", "No-Control" };
		JComboBox<String> cbFiltro = new JComboBox<>(criterios);
		searchPanel.add(cbFiltro);

		JTextField txtBuscar = new JTextField(25);
		searchPanel.add(txtBuscar);

		getContentPane().add(searchPanel, BorderLayout.BEFORE_FIRST_LINE);

		// ---------------- TABLA ----------------
		model = new DefaultTableModel(
				new Object[] { "ID", "Nombre", "Apellido", "Email", "Rol", "No-Control", "Grupo" }, 0);
		tableEstudiantes = new JTable(model);
		tableEstudiantes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(tableEstudiantes);
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		// Filtrado dinamico
		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
		tableEstudiantes.setRowSorter(sorter);

		// Filtro de búsqueda en tiempo real
		txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			private void filtrar() {
				String texto = txtBuscar.getText().trim();
				String filtroSeleccionado = cbFiltro.getSelectedItem().toString();

				if (texto.isEmpty()) {
					sorter.setRowFilter(null);
					return;
				}

				int columna = switch (filtroSeleccionado) {
				case "Nombre" -> 1;
				case "Apellido" -> 2;
				case "Email" -> 3;
				case "No-Control" -> 5;
				default -> -1;
				};

				if (columna == -1) {
					sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
				} else {
					sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto, columna));
				}
			}

			@Override
			public void insertUpdate(javax.swing.event.DocumentEvent e) {
				filtrar();
			}

			@Override
			public void removeUpdate(javax.swing.event.DocumentEvent e) {
				filtrar();
			}

			@Override
			public void changedUpdate(javax.swing.event.DocumentEvent e) {
				filtrar();
			}
		});

		// -------------- PANEL DE GRUPOS Y SELECCIÓN DE ESTUDIANTE --------------
		JPanel grupoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		grupoPanel.setBackground(new Color(240, 240, 240));

		JLabel lblGrupo = new JLabel("Seleccionar Grupo:");
		lblGrupo.setFont(new Font("Roboto", Font.PLAIN, 14));
		grupoPanel.add(lblGrupo);

		cbGrupos = new JComboBox<>();
		cbGrupos.addItem("Todos los grupos");
		grupoPanel.add(cbGrupos);

		// Boton para mostrar/ocultar el actionPanel
		btnSeleccionarEstudiante = new JButton("🎯 Seleccionar Estudiante");
		btnSeleccionarEstudiante.setBackground(new Color(220, 220, 220));
		grupoPanel.add(btnSeleccionarEstudiante);

		// ---------------- PANEL DE BOTONES (actionPanel) ----------------
		actionPanel = new JPanel();
		actionPanel.setBackground(new Color(245, 245, 245));
		actionPanel.setVisible(false);

		btnRefrescar = new JButton("🔄 Refrescar Lista");
		btnVer = new JButton("👁️ Ver Detalles");
		btnAgregar = new JButton("➕ Agregar Estudiante");
		btnEliminar = new JButton("🗑️ Eliminar Estudiante");
		btnEditar = new JButton("✏️ Editar Estudiante");

		btnVer.setEnabled(false);
		btnEliminar.setEnabled(false);
		btnEditar.setEnabled(false);

		actionPanel.add(btnRefrescar);
		actionPanel.add(btnVer);
		actionPanel.add(btnAgregar);
		actionPanel.add(btnEliminar);
		actionPanel.add(btnEditar);

		// Contenedor inferior donde se agrupan grupoPanel y actionPanel para gestionarlos de forma sencilla
		JPanel bottomContainer = new JPanel(new BorderLayout());
		bottomContainer.add(grupoPanel, BorderLayout.NORTH);
		bottomContainer.add(actionPanel, BorderLayout.SOUTH);
		getContentPane().add(bottomContainer, BorderLayout.SOUTH);

		// Evento del botón de selección
		btnSeleccionarEstudiante.addActionListener(e -> {
			int selectedRow = tableEstudiantes.getSelectedRow();

			if (!actionPanel.isVisible()) {
				if (selectedRow == -1) {
					JOptionPane.showMessageDialog(this, "Por favor selecciona un estudiante en la tabla.");
				} else {
					actionPanel.setVisible(true);
					actionPanel.revalidate();
					actionPanel.repaint();
					btnVer.setEnabled(true);
					btnEliminar.setEnabled(true);
					btnEditar.setEnabled(true);
					btnSeleccionarEstudiante.setBackground(new Color(102, 187, 106));
					btnSeleccionarEstudiante.setText("⬆️ Ocultar acciones");
				}
			} else {
				actionPanel.setVisible(false);
				btnVer.setEnabled(false);
				btnEliminar.setEnabled(false);
				btnEditar.setEnabled(false);
				btnSeleccionarEstudiante.setBackground(new Color(220, 220, 220));
				btnSeleccionarEstudiante.setText("🎯 Seleccionar Estudiante");
			}
		});

		// Cambiar grupo segun se seleccione en el comboBox
		cbGrupos.addActionListener(e -> {
			if (cbGrupos.getSelectedIndex() > 0) {
				grupoSeleccionadoId = obtenerIdGrupo(cbGrupos.getSelectedItem().toString());
			} else {
				grupoSeleccionadoId = -1;
			}
			cargarEstudiantes();
		});

		// ---------------- EVENTOS ----------------
		btnRefrescar.addActionListener(e -> cargarEstudiantes());
		btnVer.addActionListener(e -> verDetallesEstudiante());
		btnEliminar.addActionListener(e -> eliminarEstudiante());
		btnEditar.addActionListener(e -> editarEstudiante());
		btnAgregar.addActionListener(e -> agregarEstudiante());

		// Listener de selección en la tabla para detectar cuando un estudiante es seleccionado
		tableEstudiantes.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
			if (!e.getValueIsAdjusting() && actionPanel.isVisible()) {
				boolean haySeleccion = tableEstudiantes.getSelectedRow() != -1;
				btnVer.setEnabled(haySeleccion);
				btnEliminar.setEnabled(haySeleccion);
				btnEditar.setEnabled(haySeleccion);
			}
		});

		// ---------------- CARGAR DATOS ----------------
		cargarGrupos();
		cargarEstudiantes();
	}

	private void cargarGrupos() {
		cbGrupos.removeAllItems();
		cbGrupos.addItem("Todos los grupos");
		String query = "SELECT id, nombre_grupo FROM grupos";

		try (Connection cn = connectionDB.conectar();
				PreparedStatement ps = cn.prepareStatement(query);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				cbGrupos.addItem(rs.getString("nombre_grupo"));
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Error al cargar grupos:\n" + e.getMessage(), "Error SQL",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private int obtenerIdGrupo(String nombreGrupo) {
		String query = "SELECT id FROM grupos WHERE nombre_grupo = ?";
		try (Connection cn = connectionDB.conectar(); PreparedStatement ps = cn.prepareStatement(query)) {
			ps.setString(1, nombreGrupo);
			ResultSet rs = ps.executeQuery();
			if (rs.next())
				return rs.getInt("id");
		} catch (SQLException e) {
			System.err.println("Error al obtener id del grupo: " + e.getMessage());
		}
		return -1;
	}

	public void cargarEstudiantes() {
		model.setRowCount(0);
		String query = "SELECT u.id, u.nombre, u.apellido, u.email, u.role, u.no_control, g.nombre_grupo "
				+ "FROM usuarios u LEFT JOIN grupos g ON u.grupo_id = g.id " + "WHERE u.role = 'ESTUDIANTE'";

		if (grupoSeleccionadoId != -1) {
			query += " AND u.grupo_id = " + grupoSeleccionadoId;
		}

		try (Connection cn = connectionDB.conectar();
				PreparedStatement ps = cn.prepareStatement(query);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				Object[] fila = { rs.getInt("id"), rs.getString("nombre"), rs.getString("apellido"),
						rs.getString("email"), rs.getString("role"), rs.getString("no_control"),
						rs.getString("nombre_grupo") };
				model.addRow(fila);
			}

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Error al cargar estudiantes:\n" + e.getMessage(), "Error SQL",
					JOptionPane.ERROR_MESSAGE);
		} finally {
			actionPanel.setVisible(false);
			btnSeleccionarEstudiante.setBackground(new Color(220, 220, 220));
			btnSeleccionarEstudiante.setText("🎯 Seleccionar Estudiante");
			btnVer.setEnabled(false);
			btnEliminar.setEnabled(false);
			btnEditar.setEnabled(false);
		}
	}

	private void verDetallesEstudiante() {
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
				"📋 Detalles del Estudiante:\n\n" + "Nombre: " + nombre + " " + apellido + "\n" + "Email: " + email
						+ "\n" + "No. Control: " + noControl,
				"Información del Estudiante", JOptionPane.INFORMATION_MESSAGE);
	}

	private void agregarEstudiante() {
		AgregarEstudiante agregarFrame = new AgregarEstudiante(this);
		agregarFrame.setVisible(true);
	}

	private void eliminarEstudiante() {
		int selectedRow = tableEstudiantes.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Seleccione un estudiante primero.");
			return;
		}

		int confirm = JOptionPane.showConfirmDialog(this, "¿Seguro que desea eliminar este estudiante?",
				"Confirmar eliminación", JOptionPane.YES_NO_OPTION);

		if (confirm == JOptionPane.YES_OPTION) {
			int id = (int) model.getValueAt(selectedRow, 0);
			String query = "DELETE FROM usuarios WHERE id = ?";

			try (Connection cn = connectionDB.conectar(); PreparedStatement ps = cn.prepareStatement(query)) {

				ps.setInt(1, id);
				ps.executeUpdate();
				JOptionPane.showMessageDialog(this, "Estudiante eliminado correctamente.");
				cargarEstudiantes();

			} catch (SQLException e) {
				JOptionPane.showMessageDialog(this, "Error al eliminar estudiante:\n" + e.getMessage(), "Error SQL",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void editarEstudiante() {
		int selectedRow = tableEstudiantes.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Seleccione un estudiante primero.");
			return;
		}

		int id = (int) model.getValueAt(selectedRow, 0);
		String nombre = model.getValueAt(selectedRow, 1).toString();
		String apellido = model.getValueAt(selectedRow, 2).toString();
		String email = model.getValueAt(selectedRow, 3).toString();

		EditarEstudiante editarFrame = new EditarEstudiante(this, id, nombre, apellido, email);
		editarFrame.setVisible(true);
	}
}
