package guiAdmin;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.border.EmptyBorder;

import db.ConexionMysql;
import guiProfesor.AgregarEstudiante;
import guiProfesor.EditarEstudiante;
import guiProfesor.EnviarMensaje;
import guiProfesor.GestionarEstudiante;
import utils.RoundedButton;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MainForAdmin extends JFrame {

	private static final long serialVersionUID = 1L;

	private JComboBox<String> cbGrupos;
	private final String nombre;
	private int grupoSeleccionadoId = -1;
	private JTable tableEstudiantes;
	private DefaultTableModel model;
	private final ConexionMysql connectionDB = new ConexionMysql();

	private JPanel actionPanel;
	private RoundedButton btnRefrescar, btnAgregar, btnEliminar, btnEditar, btnSendMsg;
	private RoundedButton btnSeleccionarEstudiante;

	private boolean darkMode = false;
	private RoundedButton btnToggleTheme;

	public MainForAdmin(String nombre) {
		this.nombre = nombre;
		setTitle("🛠️ Panel de Administrador");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1350, 600);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);
		getContentPane().setBackground(new Color(250, 250, 252));

		// =================== HEADER ===================
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(null);
		headerPanel.setBounds(0, 0, 1350, 72);
		headerPanel.setBackground(new Color(80, 90, 140));

		JLabel title = new JLabel("Bienvenido " + nombre);
		title.setFont(new Font("Segoe UI", Font.BOLD, 22));
		title.setForeground(Color.WHITE);
		title.setBounds(18, 20, 350, 30);
		headerPanel.add(title);

		getContentPane().add(headerPanel);

		// =================== PANEL LATERAL ===================
		JPanel headerRight = new JPanel();
		headerRight.setLayout(null);
		headerRight.setBounds(1170, 72, 180, 468);
		headerRight.setBackground(Color.WHITE);

		JPanel topRight = new JPanel();
		topRight.setLayout(null);
		topRight.setOpaque(false);
		topRight.setBounds(10, 20, 160, 80);

		btnToggleTheme = new RoundedButton("🌙 Tema", 18);
		btnToggleTheme.setBackground(new Color(245, 245, 245));
		btnToggleTheme.setToolTipText("Cambiar tema (claro/oscuro)");
		btnToggleTheme.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		btnToggleTheme.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnToggleTheme.setBounds(15, 10, 130, 38);
		topRight.add(btnToggleTheme);
		headerRight.add(topRight);

		JLabel subtitle = new JLabel(
				"<html><div style='text-align:center;'>Panel administrativo<br>Gestión de estudiantes</div></html>");
		subtitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
		subtitle.setForeground(new Color(40, 0, 81));
		subtitle.setHorizontalAlignment(SwingConstants.CENTER);
		subtitle.setBounds(10, 120, 160, 40);
		headerRight.add(subtitle);

		getContentPane().add(headerRight);

		// =================== PANEL DE BÚSQUEDA ===================
		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(null);
		searchPanel.setBackground(Color.WHITE);
		searchPanel.setBounds(0, 72, 1170, 60);

		JLabel lblBuscar = new JLabel("Buscar por:");
		lblBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblBuscar.setBounds(20, 15, 90, 30);
		searchPanel.add(lblBuscar);

		String[] criterios = { "Todos", "Nombre", "Apellido", "Email", "No-Control" };
		JComboBox<String> cbFiltro = new JComboBox<>(criterios);
		cbFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		cbFiltro.setBounds(110, 15, 140, 30);
		searchPanel.add(cbFiltro);

		JTextField txtBuscar = new JTextField();
		txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtBuscar.setBounds(260, 15, 400, 30);
		txtBuscar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)),
				BorderFactory.createEmptyBorder(6, 8, 6, 8)));
		searchPanel.add(txtBuscar);

		getContentPane().add(searchPanel);

		// =================== TABLA ===================
		model = new DefaultTableModel(
				new Object[] { "ID", "Nombre", "Apellido", "Email", "Rol", "No-Control", "Grupo" }, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		tableEstudiantes = new JTable(model);
		tableEstudiantes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableEstudiantes.setRowHeight(34);
		tableEstudiantes.setFillsViewportHeight(true);
		tableEstudiantes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		tableEstudiantes.setIntercellSpacing(new Dimension(8, 6));
		tableEstudiantes.setGridColor(new Color(240, 240, 240));
		tableEstudiantes.setShowGrid(false);
		tableEstudiantes.setBackground(Color.WHITE);
		tableEstudiantes.setForeground(new Color(40, 40, 40));
		tableEstudiantes.setSelectionBackground(new Color(200, 220, 255));
		tableEstudiantes.setSelectionForeground(new Color(32, 32, 32));

		JTableHeader th = tableEstudiantes.getTableHeader();
		th.setBackground(new Color(245, 245, 250));
		th.setFont(new Font("Segoe UI", Font.BOLD, 13));
		th.setForeground(new Color(70, 70, 70));
		th.setReorderingAllowed(false);
		th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

		JScrollPane scrollPane = new JScrollPane(tableEstudiantes);
		scrollPane.setBounds(20, 140, 1130, 280);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
		getContentPane().add(scrollPane);

		// =================== FILTRO DINÁMICO ===================
		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
		tableEstudiantes.setRowSorter(sorter);

		txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
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
			public void insertUpdate(DocumentEvent e) {
				filtrar();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				filtrar();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				filtrar();
			}
		});

		// =================== PANEL DE GRUPOS ===================
		JPanel grupoPanel = new JPanel();
		grupoPanel.setLayout(null);
		grupoPanel.setBackground(Color.WHITE);
		grupoPanel.setBounds(20, 430, 1130, 50);

		JLabel lblGrupo = new JLabel("Seleccionar Grupo:");
		lblGrupo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblGrupo.setForeground(new Color(51, 51, 51));
		lblGrupo.setBounds(10, 10, 140, 30);
		grupoPanel.add(lblGrupo);

		cbGrupos = new JComboBox<>();
		cbGrupos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		cbGrupos.setBounds(150, 10, 220, 30);
		cbGrupos.addItem("Todos los grupos");
		grupoPanel.add(cbGrupos);

		btnSeleccionarEstudiante = new RoundedButton("🎯 Seleccionar Estudiante", 20);
		styleActionButton(btnSeleccionarEstudiante, new Color(245, 245, 245), new Color(220, 220, 220));
		btnSeleccionarEstudiante.setBounds(400, 10, 200, 30);
		grupoPanel.add(btnSeleccionarEstudiante);

		getContentPane().add(grupoPanel);

		// =================== PANEL DE ACCIONES ===================
		actionPanel = new JPanel();
		actionPanel.setLayout(null);
		actionPanel.setVisible(false);
		actionPanel.setBackground(Color.WHITE);
		actionPanel.setBounds(20, 480, 1130, 50);

		btnRefrescar = new RoundedButton("🔄 Refrescar Lista", 20);
		btnSendMsg = new RoundedButton("📣 Enviar Anuncio", 20);
		btnAgregar = new RoundedButton("➕ Agregar Estudiante", 20);
		btnEliminar = new RoundedButton("🗑️ Eliminar Estudiante", 20);
		btnEditar = new RoundedButton("✏️ Editar Estudiante", 20);
		RoundedButton btnGestionar = new RoundedButton("🛠️ Gestionar", 20);
		btnGestionar.setEnabled(false);

		styleActionButton(btnRefrescar, new Color(245, 245, 245), new Color(230, 230, 230));
		styleActionButton(btnSendMsg, new Color(245, 245, 245), new Color(230, 230, 230));
		styleActionButton(btnAgregar, new Color(189, 255, 222), new Color(220, 235, 255));
		styleActionButton(btnEliminar, new Color(252, 199, 199), new Color(255, 220, 220));
		styleActionButton(btnEditar, new Color(217, 237, 252), new Color(230, 230, 230));
		styleActionButton(btnGestionar, new Color(245, 245, 245), new Color(230, 230, 230));
		btnEliminar.setEnabled(false);
		btnEditar.setEnabled(false);

		btnGestionar.setBounds(10, 10, 160, 30);
		btnRefrescar.setBounds(180, 10, 160, 30);
		btnSendMsg.setBounds(350, 10, 160, 30);
		btnAgregar.setBounds(520, 10, 180, 30);
		btnEliminar.setBounds(710, 10, 180, 30);
		btnEditar.setBounds(900, 10, 180, 30);

		actionPanel.add(btnGestionar);
		actionPanel.add(btnRefrescar);
		actionPanel.add(btnSendMsg);
		actionPanel.add(btnAgregar);
		actionPanel.add(btnEliminar);
		actionPanel.add(btnEditar);

		getContentPane().add(actionPanel);

		// =================== LISTENERS ===================
		btnSeleccionarEstudiante.addActionListener(e -> {
			int selectedRow = tableEstudiantes.getSelectedRow();

			if (!actionPanel.isVisible()) {
				if (selectedRow == -1) {
					JOptionPane.showMessageDialog(this, "Por favor selecciona un estudiante en la tabla.");
				} else {
					actionPanel.setVisible(true);
					btnEliminar.setEnabled(true);
					btnEditar.setEnabled(true);
					btnGestionar.setEnabled(true);
					btnSeleccionarEstudiante.setBackground(new Color(102, 187, 106));
					btnSeleccionarEstudiante.setForeground(Color.WHITE);
					btnSeleccionarEstudiante.setText("⬆️ Ocultar acciones");
				}
			} else {
				actionPanel.setVisible(false);
				btnEliminar.setEnabled(false);
				btnEditar.setEnabled(false);
				btnGestionar.setEnabled(true);
				btnSeleccionarEstudiante.setBackground(new Color(245, 245, 245));
				btnSeleccionarEstudiante.setForeground(new Color(50, 50, 50));
				btnSeleccionarEstudiante.setText("🎯 Seleccionar Estudiante");
			}
		});

		cbGrupos.addActionListener(e -> {
			if (cbGrupos.getSelectedIndex() > 0) {
				grupoSeleccionadoId = obtenerIdGrupo(cbGrupos.getSelectedItem().toString());
			} else {
				grupoSeleccionadoId = -1;
			}
			cargarEstudiantes();
		});

		btnRefrescar.addActionListener(e -> cargarEstudiantes());
		btnSendMsg.addActionListener(e -> enviarMensaje());
		btnEliminar.addActionListener(e -> eliminarEstudiante());
		btnEditar.addActionListener(e -> editarEstudiante());
		btnAgregar.addActionListener(e -> agregarEstudiante());
		btnGestionar.addActionListener(e -> gestionarEstudiante());

		btnToggleTheme.addActionListener(e -> {
			darkMode = !darkMode;
			applyTheme(darkMode, headerPanel, title, subtitle, searchPanel, lblBuscar, cbFiltro, txtBuscar, scrollPane,
					th, tableEstudiantes, grupoPanel, lblGrupo, cbGrupos, btnSeleccionarEstudiante, actionPanel,
					btnRefrescar, btnAgregar, btnEliminar, btnEditar, btnGestionar, btnToggleTheme);
		});

		applyTheme(darkMode, headerPanel, title, subtitle, searchPanel, lblBuscar, cbFiltro, txtBuscar, scrollPane, th,
				tableEstudiantes, grupoPanel, lblGrupo, cbGrupos, btnSeleccionarEstudiante, actionPanel, btnRefrescar,
				btnAgregar, btnEliminar, btnEditar, btnGestionar, btnToggleTheme);

		cargarGrupos();
		cargarEstudiantes();
	}

	// =================== MÉTODOS AUXILIARES ===================
	private void styleActionButton(RoundedButton b, Color bg, Color bgHoverBase) {
		b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		b.setBackground(bg);
		b.setForeground(new Color(48, 48, 48));
		b.setFocusPainted(false);
		b.setBorderPainted(false);
		b.setCursor(new Cursor(Cursor.HAND_CURSOR));
		b.setOpaque(true);
		b.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				Color hover = deriveHoverColor(bgHoverBase);
				if (isDark(hover))
					b.setForeground(Color.WHITE);
				else
					b.setForeground(new Color(30, 30, 30));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				b.setForeground(new Color(48, 48, 48));
			}
		});
	}

	private Color deriveHoverColor(Color base) {
		int r = clamp(base.getRed() - 6);
		int g = clamp(base.getGreen() - 6);
		int b = clamp(base.getBlue() - 6);
		return new Color(r, g, b);
	}

	private int clamp(int v) {
		return Math.min(255, Math.max(0, v));
	}

	private boolean isDark(Color c) {
		double lum = 0.2126 * c.getRed() + 0.7152 * c.getGreen() + 0.0722 * c.getBlue();
		return lum < 140;
	}

	// =================== TEMA CLARO/OSCURO ===================
	private void applyTheme(boolean dark, JPanel headerPanel, JLabel title, JLabel subtitle, JPanel searchPanel,
			JLabel lblBuscar, JComboBox<String> cbFiltro, JTextField txtBuscar, JScrollPane scrollPane, JTableHeader th,
			JTable tableEstudiantes, JPanel grupoPanel, JLabel lblGrupo, JComboBox<String> cbGrupos,
			RoundedButton btnSeleccionarEstudiante, JPanel actionPanel, RoundedButton btnRefrescar,
			RoundedButton btnAgregar, RoundedButton btnEliminar, RoundedButton btnEditar, RoundedButton btnGestionar,
			RoundedButton btnToggleTheme) {

		if (dark) {
			Color bg = new Color(34, 38, 48);
			Color panel = new Color(42, 46, 60);
			Color header = new Color(60, 70, 110);
			Color text = new Color(230, 230, 235);
			Color subtitleColor = new Color(200, 210, 235);
			Color inputBg = new Color(60, 65, 78);
			Color border = new Color(70, 75, 90);
			Color tableHeader = new Color(48, 52, 66);

			getContentPane().setBackground(bg);
			headerPanel.setBackground(header);
			title.setForeground(text);
			subtitle.setForeground(subtitleColor);
			searchPanel.setBackground(panel);
			lblBuscar.setForeground(text);
			cbFiltro.setBackground(inputBg);
			cbFiltro.setForeground(text);
			txtBuscar.setBackground(inputBg);
			txtBuscar.setForeground(text);
			txtBuscar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(border),
					BorderFactory.createEmptyBorder(6, 8, 6, 8)));
			scrollPane.getViewport().setBackground(panel);
			th.setBackground(tableHeader);
			th.setForeground(text);
			tableEstudiantes.setBackground(new Color(44, 48, 62));
			tableEstudiantes.setForeground(text);
			tableEstudiantes.setSelectionBackground(new Color(70, 80, 110));
			tableEstudiantes.setGridColor(new Color(50, 55, 70));
			grupoPanel.setBackground(panel);
			lblGrupo.setForeground(text);
			cbGrupos.setBackground(inputBg);
			cbGrupos.setForeground(text);
			btnSeleccionarEstudiante.setBackground(new Color(64, 75, 140));
			btnSeleccionarEstudiante.setForeground(Color.WHITE);
			actionPanel.setBackground(panel);
			btnToggleTheme.setText("☀ Tema");
			btnToggleTheme.setBackground(new Color(60, 65, 78));
			btnToggleTheme.setForeground(Color.WHITE);
		} else {
			Color bg = new Color(250, 250, 252);
			Color panel = new Color(255, 255, 255);
			Color header = new Color(80, 90, 140);
			Color text = new Color(45, 45, 45);
			Color subtitleColor = new Color(45, 45, 45);
			Color inputBg = new Color(255, 255, 255);
			Color border = new Color(220, 220, 220);
			Color tableHeader = new Color(245, 245, 250);

			getContentPane().setBackground(bg);
			headerPanel.setBackground(header);
			title.setForeground(Color.WHITE);
			subtitle.setForeground(subtitleColor);
			searchPanel.setBackground(panel);
			lblBuscar.setForeground(text);
			cbFiltro.setBackground(inputBg);
			cbFiltro.setForeground(text);
			txtBuscar.setBackground(inputBg);
			txtBuscar.setForeground(text);
			txtBuscar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(border),
					BorderFactory.createEmptyBorder(6, 8, 6, 8)));
			scrollPane.getViewport().setBackground(panel);
			th.setBackground(tableHeader);
			th.setForeground(new Color(70, 70, 70));
			tableEstudiantes.setBackground(Color.WHITE);
			tableEstudiantes.setForeground(new Color(40, 40, 40));
			tableEstudiantes.setSelectionBackground(new Color(200, 220, 255));
			tableEstudiantes.setGridColor(new Color(240, 240, 240));
			grupoPanel.setBackground(panel);
			lblGrupo.setForeground(text);
			cbGrupos.setBackground(inputBg);
			cbGrupos.setForeground(text);
			btnSeleccionarEstudiante.setBackground(new Color(245, 245, 245));
			btnSeleccionarEstudiante.setForeground(new Color(45, 45, 45));
			actionPanel.setBackground(panel);
			btnToggleTheme.setText("🌙 Tema");
			btnToggleTheme.setBackground(new Color(247, 248, 250));
			btnToggleTheme.setForeground(new Color(45, 45, 45));
		}
	}

	// =================== MÉTODOS DE CARGA ===================
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
		if (grupoSeleccionadoId != -1)
			query += " AND u.grupo_id = " + grupoSeleccionadoId;
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
		}
	}

	// =================== CRUD ===================
	private void enviarMensaje() {
		new EnviarMensaje().setVisible(true);
	}

	private void agregarEstudiante() {
		new AgregarEstudiante(null, this).setVisible(true);
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
		String nombreGrupo = model.getValueAt(selectedRow, 6) != null ? model.getValueAt(selectedRow, 6).toString()
				: null;
		int grupoId = (nombreGrupo != null) ? obtenerIdGrupo(nombreGrupo) : -1;
		new EditarEstudiante(null, this, id, nombre, apellido, email, grupoId).setVisible(true);
	}

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
				"📋 Detalles del Estudiante:\n\n" + "Nombre: " + nombre + " " + apellido + "\n" + "Email: " + email
						+ "\n" + "No. Control: " + noControl,
				"Información del Estudiante", JOptionPane.INFORMATION_MESSAGE);

		int option1 = JOptionPane.showConfirmDialog(this, "¿Deseas ver el historial académico?", "Gestión Académica",
				JOptionPane.YES_NO_OPTION);
		if (option1 == JOptionPane.YES_OPTION)
			new GestionarEstudiante(noControl, nombre, apellido).setVisible(true);
	}

}
