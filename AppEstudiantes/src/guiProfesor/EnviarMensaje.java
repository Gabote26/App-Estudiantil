package guiProfesor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

import db.ConexionMysql;
import utils.RoundedButton;

public class EnviarMensaje extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JComboBox<String> cbGrupos;
	private int grupoSeleccionadoId = -1;
	private JTable tableEstudiantes;
	private DefaultTableModel model;
	private final ConexionMysql connectionDB = new ConexionMysql();

	private JPanel actionPanel;
	private RoundedButton btnRefrescar, btnAgregar, btnEliminar, btnEditar, btnSendMsg;
	private RoundedButton btnSeleccionarEstudiante;

	public EnviarMensaje() {
		setTitle("Enviar anuncio a alumnos");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1350, 540);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		// =================== HEADER ===================
				JPanel headerPanel = new JPanel(new BorderLayout());
				headerPanel.setPreferredSize(new Dimension(700, 72));
				headerPanel.setBorder(new EmptyBorder(12, 18, 12, 18));
				headerPanel.setBackground(new Color(80, 90, 140));

				JLabel title = new JLabel("Gestión de Estudiantes");
				title.setFont(new Font("Segoe UI", Font.BOLD, 22));
				title.setForeground(Color.WHITE);
				headerPanel.add(title, BorderLayout.WEST);

				// =================== PANEL LATERAL ===================
				JPanel headerRight = new JPanel();
				headerRight.setLayout(new BorderLayout());
				headerRight.setPreferredSize(new Dimension(180, getHeight()));
				headerRight.setBorder(new EmptyBorder(20, 12, 20, 12));
				headerRight.setBackground(Color.white);

				JLabel subtitle = new JLabel(
						"<html><div style='text-align:center;'>Panel administrativo<br>Gestión de estudiantes</div></html>");
				subtitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
				subtitle.setForeground(new Color(40, 0, 81));
				subtitle.setHorizontalAlignment(SwingConstants.CENTER);
				subtitle.setBorder(new EmptyBorder(10, 0, 0, 0));

				headerRight.add(subtitle, BorderLayout.CENTER);

				getContentPane().add(headerPanel, BorderLayout.NORTH);
				getContentPane().add(headerRight, BorderLayout.EAST);

				// =================== FILTRO DE BÚSQUEDA ===================
				JPanel searchPanel = new JPanel(new GridBagLayout());
				searchPanel.setBorder(new EmptyBorder(12, 18, 12, 18));

				GridBagConstraints gbc = new GridBagConstraints();
				gbc.insets = new Insets(6, 8, 6, 8);
				gbc.anchor = GridBagConstraints.WEST;
				gbc.gridy = 0;

				JLabel lblBuscar = new JLabel("Buscar por:");
				lblBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
				gbc.gridx = 0;
				searchPanel.add(lblBuscar, gbc);

				GridBagConstraints gbcCb = new GridBagConstraints();
				gbcCb.insets = new Insets(6, 8, 6, 8);
				gbcCb.anchor = GridBagConstraints.WEST;
				gbcCb.gridy = 0;
				gbcCb.gridx = 1;

				String[] criterios = { "Todos", "Nombre", "Apellido", "Email", "No-Control" };
				JComboBox<String> cbFiltro = new JComboBox<>(criterios);
				cbFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 13));
				cbFiltro.setPreferredSize(new Dimension(140, 30));
				searchPanel.add(cbFiltro, gbcCb);

				GridBagConstraints gbcTxt = new GridBagConstraints();
				gbcTxt.insets = new Insets(6, 8, 6, 8);
				gbcTxt.anchor = GridBagConstraints.WEST;
				gbcTxt.gridy = 0;
				gbcTxt.gridx = 2;
				gbcTxt.weightx = 1;
				gbcTxt.fill = GridBagConstraints.HORIZONTAL;

				JTextField txtBuscar = new JTextField(30);
				txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
				txtBuscar.setPreferredSize(new Dimension(360, 34));
				txtBuscar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)),
						BorderFactory.createEmptyBorder(6, 8, 6, 8)));
				searchPanel.add(txtBuscar, gbcTxt);

				getContentPane().add(searchPanel, BorderLayout.BEFORE_FIRST_LINE);

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
				scrollPane.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
				getContentPane().add(scrollPane, BorderLayout.CENTER);

				// Filtro dinámico
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
				JPanel grupoPanel = new JPanel(new GridBagLayout());
				grupoPanel.setBorder(new EmptyBorder(12, 18, 12, 18));

				GridBagConstraints g2Lbl = new GridBagConstraints();
				g2Lbl.insets = new Insets(6, 8, 6, 8);
				g2Lbl.anchor = GridBagConstraints.WEST;
				g2Lbl.gridy = 0;
				g2Lbl.gridx = 0;

				JLabel lblGrupo = new JLabel("Seleccionar Grupo:");
				lblGrupo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
				lblGrupo.setForeground(new Color(51, 51, 51));
				grupoPanel.add(lblGrupo, g2Lbl);

				GridBagConstraints g2Cb = new GridBagConstraints();
				g2Cb.insets = new Insets(6, 8, 6, 8);
				g2Cb.anchor = GridBagConstraints.WEST;
				g2Cb.gridy = 0;
				g2Cb.gridx = 1;

				cbGrupos = new JComboBox<>();
				cbGrupos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
				cbGrupos.setPreferredSize(new Dimension(220, 34));
				cbGrupos.addItem("Todos los grupos");
				grupoPanel.add(cbGrupos, g2Cb);

				GridBagConstraints g2Btn = new GridBagConstraints();
				g2Btn.insets = new Insets(6, 8, 6, 8);
				g2Btn.anchor = GridBagConstraints.WEST;
				g2Btn.gridy = 0;
				g2Btn.gridx = 2;

				btnSeleccionarEstudiante = new RoundedButton("🎯 Seleccionar Estudiante", 20);
				styleActionButton(btnSeleccionarEstudiante, new Color(245, 245, 245), new Color(220, 220, 220));
				btnSeleccionarEstudiante.setPreferredSize(new Dimension(200, 36));
				grupoPanel.add(btnSeleccionarEstudiante, g2Btn);

				// =================== PANEL DE BOTONES ===================
				actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
				actionPanel.setVisible(false);
				actionPanel.setBorder(new EmptyBorder(6, 0, 6, 0));

				btnRefrescar = new RoundedButton("🔄 Refrescar Lista", 20);
				btnSendMsg = new RoundedButton("📣 Enviar Anuncio", 20);
				btnAgregar = new RoundedButton("➕ Agregar Estudiante", 20);
				btnEliminar = new RoundedButton("🗑️ Eliminar Estudiante", 20);
				btnEditar = new RoundedButton("✏️ Editar Estudiante", 20);
				RoundedButton btnGestionar = new RoundedButton("🛠️ Gestionar", 20);
				btnGestionar.setEnabled(false);

				styleActionButton(btnRefrescar, new Color(245, 245, 245), new Color(230, 230, 230));
				styleActionButton(btnSendMsg, new Color(245, 245, 245), new Color(230, 230, 230));
				styleActionButton(btnAgregar, new Color(240, 248, 255), new Color(220, 235, 255));
				styleActionButton(btnEliminar, new Color(255, 240, 240), new Color(255, 220, 220));
				styleActionButton(btnEditar, new Color(245, 245, 245), new Color(230, 230, 230));
				styleActionButton(btnGestionar, new Color(245, 245, 245), new Color(230, 230, 230));
				btnEliminar.setEnabled(false);
				btnEditar.setEnabled(false);

				actionPanel.add(btnGestionar);
				actionPanel.add(btnRefrescar);
				actionPanel.add(btnSendMsg);
				actionPanel.add(btnAgregar);
				actionPanel.add(btnEliminar);
				actionPanel.add(btnEditar);

				JPanel bottomContainer = new JPanel(new BorderLayout());
				bottomContainer.add(grupoPanel, BorderLayout.NORTH);
				bottomContainer.add(actionPanel, BorderLayout.SOUTH);
				bottomContainer.setBorder(new EmptyBorder(6, 0, 12, 0));
				getContentPane().add(bottomContainer, BorderLayout.SOUTH);

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

	}
	
	private void styleActionButton(RoundedButton b, Color bg, Color bgHoverBase) {
		b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		b.setBackground(bg);
		b.setForeground(new Color(48, 48, 48));
		b.setFocusPainted(false);
		b.setBorderPainted(false);
		b.setCursor(new Cursor(Cursor.HAND_CURSOR));
		b.setPreferredSize(new Dimension(170, 36));
		b.setOpaque(true);
		b.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				Color hover = deriveHoverColor(bgHoverBase);
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
	
	// =================== MÉTODOS ===================
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
			}
		}

}
