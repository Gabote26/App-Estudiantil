package guiEstudiante;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import db.ConexionMysql;
import main.Settings;
import utils.RoundedButton;

import java.awt.*;
import java.sql.*;
import java.awt.event.*;

public class ProgramMain extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private final ConexionMysql connectionDB = new ConexionMysql();
	private final long numControl;
	@SuppressWarnings("unused")
	private final String nombre, apellido;

	public ProgramMain(long numControl, String nombre, String apellido) {
	    this.numControl = numControl;
	    this.nombre = nombre;
	    this.apellido = apellido;

	    setTitle("Estudiante - " + nombre + " " + apellido);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setUndecorated(true); // esencial para fade-in
	    setBounds(100, 100, 950, 595);
	    setLocationRelativeTo(null);

	    contentPane = new JPanel();
	    contentPane.setForeground(Color.WHITE);
	    contentPane.setBackground(new Color(38, 47, 87));
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
			new VerCalificaciones(numControl, nombre, apellido).setVisible(true);
		});
		contentPane.add(btnVerCalificaciones);

		RoundedButton btnAsistencias = new RoundedButton("📊 Mis Asistencias", 20);
		btnAsistencias.setBackground(new Color(46, 204, 113));
		btnAsistencias.setForeground(Color.WHITE);
		btnAsistencias.setBounds(463, 60, 187, 39);
		btnAsistencias.addActionListener(e -> {
			new VerAsistencias(numControl, nombre, apellido).setVisible(true);
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
			ImageIcon original = new ImageIcon("resources/welcome.png");
			Image imagenEscalada = original.getImage().getScaledInstance(255, 255, Image.SCALE_SMOOTH);
			JLabel lblIcon = new JLabel(new ImageIcon(imagenEscalada));
			lblIcon.setBackground(new Color(0, 0, 160));
			lblIcon.setBounds(489, 237, 255, 255);
			contentPane.add(lblIcon);
		} catch (Exception e) {
			System.err.println("No se pudo cargar la imagen: " + e.getMessage());
		}
		
		try {
			ImageIcon original2 = new ImageIcon("resources/appLogoImg.png");
			Image imagenEscalada2 = original2.getImage().getScaledInstance(255, 255, Image.SCALE_SMOOTH);
			JLabel lblIcon2 = new JLabel(new ImageIcon(imagenEscalada2));
			lblIcon2.setBackground(new Color(0, 0, 160));
			lblIcon2.setBounds(10, 360, 190, 188);
			contentPane.add(lblIcon2);
		} catch (Exception e) {
			System.err.println("No se pudo cargar la imagen: " + e.getMessage());
		}

		// ========== INFORMACIÓN DEL ESTUDIANTE ==========
		JPanel panelInfo = new JPanel();
		panelInfo.setForeground(new Color(255, 255, 255));
		panelInfo.setBackground(new Color(42, 46, 60));
		panelInfo.setBounds(23, 132, 350, 100);
		panelInfo.setBorder(
			    new TitledBorder("Información del Estudiante") {{
			        setTitleColor(Color.WHITE);
			    }}
			);
		panelInfo.setLayout(null);
		contentPane.add(panelInfo);

		JLabel lblNombre = new JLabel("Nombre: " + nombre + " " + apellido);
		lblNombre.setForeground(new Color(255, 255, 255));
		lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblNombre.setBounds(10, 20, 330, 25);
		panelInfo.add(lblNombre);

		JLabel lblNumControl = new JLabel("No. Control: " + numControl);
		lblNumControl.setForeground(new Color(255, 255, 255));
		lblNumControl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblNumControl.setBounds(10, 50, 330, 25);
		panelInfo.add(lblNumControl);

		String grupoNombre = obtenerGrupoEstudiante(numControl);
		JLabel lblGrupo = new JLabel("Grupo: " + (grupoNombre != null ? grupoNombre : "Sin asignar"));
		lblGrupo.setForeground(new Color(255, 255, 255));
		lblGrupo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblGrupo.setBounds(10, 75, 330, 25);
		panelInfo.add(lblGrupo);
		
		JLabel lblNewLabel = new JLabel("NOTICIAS RECIENTES");
		lblNewLabel.setFont(new Font("Segoe UI", Font.BOLD, 23));
		lblNewLabel.setForeground(new Color(192, 192, 192));
		lblNewLabel.setBounds(494, 163, 250, 39);
		contentPane.add(lblNewLabel);
		
		JButton btnSettings = new RoundedButton("⚙️", 20);
		btnSettings.setText("    ⚙️");
		btnSettings.setBackground(new Color(128, 128, 128));
		btnSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Settings stgFrame = new Settings();
				stgFrame.setVisible(true);
			}
		});
		btnSettings.setToolTipText("Configuración");
		btnSettings.setForeground(new Color(0, 0, 0));
		btnSettings.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 17));
		btnSettings.setBounds(844, 59, 79, 39);
		contentPane.add(btnSettings);
		
		// ---------- CERRAR VENTANA ----------
        JButton closeBtn = new JButton("X");
        closeBtn.setBounds(883, 10, 40, 30);
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setBackground(new Color(153, 61, 61));
        closeBtn.setBorder(null);
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> System.exit(0));
        contentPane.add(closeBtn);

        addDragListener(contentPane);

		// ========== BANDEJA DE MENSAJES NO LEÍDOS ==========
		mostrarBadgeMensajes();
	}
	
	// Permitir que la ventana pueda cambiarse de posición
    private void addDragListener(JPanel panel) {
        final int[] p = new int[2];

        panel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                p[0] = e.getX();
                p[1] = e.getY();
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                setLocation(getX() + e.getX() - p[0], getY() + e.getY() - p[1]);
            }
        });
    }

	// ========== MÉTODOS AUXILIARES ==========

	private int obtenerIdUsuario(long noControl) {
		String sql = "SELECT id FROM usuarios WHERE no_control = ?";
		
		try (Connection cn = connectionDB.conectar();
		     PreparedStatement ps = cn.prepareStatement(sql)) {
			
			ps.setLong(1, noControl);
			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {
				return rs.getInt("id");
			}
			
		} catch (SQLException e) {
			System.err.println("Error al obtener ID: " + e.getMessage());
		}
		
		return -1;
	}

	private String obtenerGrupoEstudiante(long noControl) {
		String sql = """
			SELECT g.nombre_grupo 
			FROM usuarios u 
			LEFT JOIN grupos g ON u.grupo_id = g.id 
			WHERE u.no_control = ?
		""";
		
		try (Connection cn = connectionDB.conectar();
		     PreparedStatement ps = cn.prepareStatement(sql)) {
			
			ps.setLong(1, noControl);
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