package guiEstudiante;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import calificaciones.calificacion;
import db.ConexionMysql;
import utils.RoundedButton;

import java.awt.*;
import java.sql.*;

public class ProgramMain extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private final ConexionMysql connectionDB = new ConexionMysql();
	private final String numControl, nombre, apellido;

	public ProgramMain(String numControl, String nombre, String apellido) {
		this.numControl = numControl;
		this.nombre = nombre;
		this.apellido = apellido;
		
		setTitle("Estudiante - " + nombre + " " + apellido);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 950, 595);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setForeground(new Color(255, 255, 255));
		contentPane.setBackground(new Color(42, 34, 71));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// ========== TÍTULO ==========
		JLabel lblTitulo = new JLabel("Panel del Estudiante");
		lblTitulo.setForeground(new Color(192, 192, 192));
		lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
		lblTitulo.setBounds(350, 10, 250, 30);
		contentPane.add(lblTitulo);

		// ========== BOTONES PRINCIPALES ==========

		RoundedButton btnMensajes = new RoundedButton("📬 Mis Mensajes", 20);
		btnMensajes.setBackground(new Color(52, 152, 219));
		btnMensajes.setForeground(Color.WHITE);
		btnMensajes.setBounds(123, 60, 150, 39);
		btnMensajes.addActionListener(e -> {
			int usuarioId = obtenerIdUsuario(numControl);
			if (usuarioId > 0) {
				new BandejaMensajes(usuarioId).setVisible(true);
			} else {
				JOptionPane.showMessageDialog(this, "Error al cargar mensajes");
			}
		});
		contentPane.add(btnMensajes);

		RoundedButton btnVerCalificaciones = new RoundedButton("📝 Mis Calificaciones", 20);
		btnVerCalificaciones.setBackground(new Color(155, 89, 182));
		btnVerCalificaciones.setForeground(Color.WHITE);
		btnVerCalificaciones.setBounds(283, 60, 170, 39);
		btnVerCalificaciones.addActionListener(e -> {
		    try {
		        long nc = Long.parseLong(numControl);
		        new VerCalificaciones(nc, nombre, apellido).setVisible(true);
		    } catch (NumberFormatException ex) {
		        JOptionPane.showMessageDialog(this, 
		            "Error: Número de control inválido", 
		            "Error", 
		            JOptionPane.ERROR_MESSAGE);
		    }
		});
		contentPane.add(btnVerCalificaciones);

		RoundedButton btnAsistencias = new RoundedButton("📊 Mis Asistencias", 20);
		btnAsistencias.setText("📊 Verificar Asistencias");
		btnAsistencias.setBackground(new Color(46, 204, 113));
		btnAsistencias.setForeground(Color.WHITE);
		btnAsistencias.setBounds(463, 60, 187, 39);
		btnAsistencias.addActionListener(e -> {
			try {
				long nc = Long.parseLong(numControl);
				new VerAsistencias(nc, nombre, apellido).setVisible(true);
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(this, 
					"Error: Número de control inválido", 
					"Error", 
					JOptionPane.ERROR_MESSAGE);
			}
		});
		contentPane.add(btnAsistencias);

		RoundedButton btnHorarios = new RoundedButton("🗓️ HORARIOS", 20);
		btnHorarios.setBounds(660, 60, 150, 39);
		btnHorarios.addActionListener(e -> {
			HorariosEstudiantes ventanaHorarios = new HorariosEstudiantes();
			ventanaHorarios.setVisible(true);
		});
		contentPane.add(btnHorarios);

		// ========== IMAGEN DECORATIVA ==========
		try {
			ImageIcon original = new ImageIcon("resources/calendario_example.png");
			Image imagenEscalada = original.getImage().getScaledInstance(274, 295, Image.SCALE_SMOOTH);
			JLabel lblIcon = new JLabel(new ImageIcon(imagenEscalada));
			lblIcon.setBackground(new Color(0, 0, 160));
			lblIcon.setBounds(565, 193, 250, 276);
			contentPane.add(lblIcon);
		} catch (Exception e) {
			System.err.println("No se pudo cargar la imagen: " + e.getMessage());
		}
		
		try {
			ImageIcon original2 = new ImageIcon("resources/Myaux Logo.jpg");
			Image imagenEscalada2 = original2.getImage().getScaledInstance(274, 295, Image.SCALE_SMOOTH);
			JLabel lblIcon2 = new JLabel(new ImageIcon(imagenEscalada2));
			lblIcon2.setBackground(new Color(0, 0, 160));
			lblIcon2.setBounds(190, 369, 141, 179);
			contentPane.add(lblIcon2);
		} catch (Exception e) {
			System.err.println("No se pudo cargar la imagen: " + e.getMessage());
		}

		// ========== INFORMACIÓN DEL ESTUDIANTE ==========
		JPanel panelInfo = new JPanel();
		panelInfo.setBackground(new Color(240, 240, 240));
		panelInfo.setBounds(123, 200, 350, 100);
		panelInfo.setBorder(BorderFactory.createTitledBorder("Información del Estudiante"));
		panelInfo.setLayout(null);
		contentPane.add(panelInfo);

		JLabel lblNombre = new JLabel("Nombre: " + nombre + " " + apellido);
		lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblNombre.setBounds(10, 20, 330, 25);
		panelInfo.add(lblNombre);

		JLabel lblNumControl = new JLabel("No. Control: " + numControl);
		lblNumControl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblNumControl.setBounds(10, 50, 330, 25);
		panelInfo.add(lblNumControl);

		String grupoNombre = obtenerGrupoEstudiante(numControl);
		JLabel lblGrupo = new JLabel("Grupo: " + (grupoNombre != null ? grupoNombre : "Sin asignar"));
		lblGrupo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblGrupo.setBounds(10, 75, 330, 25);
		panelInfo.add(lblGrupo);
		
	

		// ========== BANDEJA DE MENSAJES NO LEÍDOS ==========
		mostrarBadgeMensajes();
	}

	// ========== MÉTODOS AUXILIARES ==========

	private int obtenerIdUsuario(String noControl) {
		String sql = "SELECT id FROM usuarios WHERE no_control = ?";
		
		try (Connection cn = connectionDB.conectar();
		     PreparedStatement ps = cn.prepareStatement(sql)) {
			
			ps.setString(1, noControl);
			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {
				return rs.getInt("id");
			}
			
		} catch (SQLException e) {
			System.err.println("Error al obtener ID: " + e.getMessage());
		}
		
		return -1;
	}

	private String obtenerGrupoEstudiante(String noControl) {
		String sql = """
			SELECT g.nombre_grupo 
			FROM usuarios u 
			LEFT JOIN grupos g ON u.grupo_id = g.id 
			WHERE u.no_control = ?
		""";
		
		try (Connection cn = connectionDB.conectar();
		     PreparedStatement ps = cn.prepareStatement(sql)) {
			
			ps.setString(1, noControl);
			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {
				return rs.getString("nombre_grupo");
			}
			
		} catch (SQLException e) {
			System.err.println("Error al obtener grupo: " + e.getMessage());
		}
		
		return null;
	}

	private void mostrarBadgeMensajes() {
		int usuarioId = obtenerIdUsuario(numControl);
		if (usuarioId <= 0) return;

		String sql = """
			SELECT COUNT(*) as total
			FROM mensajes_destinatarios
			WHERE destinatario_id = ? AND leido = FALSE
		""";

		try (Connection cn = connectionDB.conectar();
		     PreparedStatement ps = cn.prepareStatement(sql)) {

			ps.setInt(1, usuarioId);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				int noLeidos = rs.getInt("total");
				
				if (noLeidos > 0) {
					JLabel lblBadge = new JLabel(String.valueOf(noLeidos));
					lblBadge.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
					lblBadge.setForeground(Color.WHITE);
					lblBadge.setBackground(Color.RED);
					lblBadge.setOpaque(true);
					lblBadge.setHorizontalAlignment(SwingConstants.CENTER);
					lblBadge.setBounds(260, 55, 25, 20);
					lblBadge.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
					contentPane.add(lblBadge);
				}
			}

		} catch (SQLException e) {
			System.err.println("Error al contar mensajes: " + e.getMessage());
		}
	}
}