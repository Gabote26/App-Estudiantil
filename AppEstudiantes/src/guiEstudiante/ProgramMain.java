package guiEstudiante;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import db.ConexionMysql;
import main.LoginSystem;
import main.Settings;
import utils.RoundedButton;

import net.miginfocom.swing.MigLayout;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ProgramMain extends JFrame {

	private static final long serialVersionUID = 1L;

	private final ConexionMysql connectionDB = new ConexionMysql();
	private final long numControl;
	private final String nombre, apellido;

	// Parametros para la animación de ventana
	private boolean maximizado = false;
	private Rectangle prevBounds;
	private Timer animTimer;

	// Panel principal
	private JPanel content;

	// Barra superior
	private JPanel topBar;

	// Badge de notificación y botón mensajes
	private RoundedButton btnMensajes;
	private JLabel badgeMensajes;

	private Integer usuarioIdCache = null;
	private String grupoCache = null;

	public ProgramMain(long numControl, String nombre, String apellido) {
		this.numControl = numControl;
		this.nombre = nombre;
		this.apellido = apellido;

		setTitle("Estudiante - " + nombre + " " + apellido);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setUndecorated(true);

		setSize(950, 600);
		setLocationRelativeTo(null);

		initUI(); // Inicializar la interfaz de usuario
		setupTopbarDrag();
		iniciarActualizacionAutomatica(); // Actualización del badge de mensajes
	}

	// Interfaz gráfica
	private void initUI() {

		topBar = new JPanel(new BorderLayout());
		topBar.setBackground(new Color(28, 36, 70));
		topBar.setPreferredSize(new Dimension(50, 40));

		JLabel title = new JLabel("  Panel del Estudiante", SwingConstants.LEFT);
		title.setForeground(Color.WHITE);
		title.setFont(new Font("Segoe UI", Font.BOLD, 16));
		topBar.add(title, BorderLayout.WEST);

		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		btnPanel.setOpaque(false);

		JButton btnMin = createTopButton("—", e -> setState(Frame.ICONIFIED));
		JButton btnMax = createTopButton("▢", e -> toggleMaximize());
		JButton btnClose = createTopButton("X", e -> System.exit(0));
		btnClose.setBackground(new Color(170, 60, 60));

		btnPanel.add(btnMin);
		btnPanel.add(btnMax);
		btnPanel.add(btnClose);

		topBar.add(btnPanel, BorderLayout.EAST);

		content = new JPanel(new MigLayout("insets 20, gap 20, wrap 2", "[grow,fill][grow,fill]", ""));
		content.setBackground(new Color(38, 47, 87));

		JScrollPane scroll = new JScrollPane(content);
		scroll.setBorder(null);
		scroll.getVerticalScrollBar().setUnitIncrement(16);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(topBar, BorderLayout.NORTH);
		getContentPane().add(scroll, BorderLayout.CENTER);

		agregarContenido();
	}

	private JButton createTopButton(String txt, ActionListener evt) {
		JButton b = new JButton(txt);
		b.setFocusable(false);
		b.setBackground(new Color(60, 60, 60));
		b.setForeground(Color.WHITE);
		b.setBorder(null);
		b.setPreferredSize(new Dimension(40, 40));
		b.addActionListener(evt);
		return b;
	}

	private void agregarContenido() {

		JLabel title = new JLabel("Panel del Estudiante");
		title.setFont(new Font("Segoe UI", Font.BOLD, 26));
		title.setForeground(Color.WHITE);
		content.add(title, "span 2, align center");

		JPanel botones = new JPanel(new MigLayout("wrap 4, gap 15", "[grow]"));
		botones.setOpaque(false);

		btnMensajes = crearBoton("📬 Mis Mensajes", new Color(52, 152, 219), e -> abrirMensajes());
		botones.add(btnMensajes);

		botones.add(crearBoton("📝 Mis Calificaciones", new Color(155, 89, 182),
				e -> new VerCalificaciones(numControl, nombre, apellido).setVisible(true)));

		botones.add(crearBoton("📊 Mis Asistencias", new Color(46, 204, 113),
				e -> new VerAsistencias(numControl, nombre, apellido).setVisible(true)));

		botones.add(
				crearBoton("🗓️ Horarios", new Color(190, 190, 190), e -> new HorariosEstudiantes().setVisible(true)));

		content.add(botones, "span 2, growx");

		JPanel info = new JPanel(new MigLayout("insets 15, wrap 1", "[grow,fill]"));
		info.setOpaque(true);
		info.setBackground(new Color(42, 46, 60));

		TitledBorder tb = new TitledBorder("Información del Estudiante");
		tb.setTitleColor(Color.WHITE);
		info.setBorder(tb);

		info.add(labelInfo("Nombre", nombre + " " + apellido));
		info.add(labelInfo("No. Control", String.valueOf(numControl)));
		info.add(labelInfo("Grupo", obtenerGrupoEstudiante(numControl)));

		content.add(info);

		JLabel noticias = new JLabel("Noticias Recientes");
		noticias.setForeground(Color.WHITE);
		noticias.setFont(new Font("Segoe UI", Font.BOLD, 22));
		content.add(noticias, "wrap");

		content.add(new JLabel(escalarImagen("/welcome.png", 350, 300)));
		content.add(new JLabel(escalarImagen("/appLogoImg.png", 250, 250)));

		RoundedButton btnConf = new RoundedButton("⚙️ Configuración", 20);
		btnConf.setBackground(new Color(128, 128, 128));
		btnConf.addActionListener(e -> new Settings().setVisible(true));

		RoundedButton btnLogout = new RoundedButton("🔒 Cerrar Sesión", 20);
		btnLogout.setBackground(new Color(247, 79, 79));
		btnLogout.addActionListener(e -> LoginSystem.cerrarSesion(this));

		JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		footer.setOpaque(false);
		footer.add(btnConf);
		footer.add(btnLogout);

		content.add(footer, "span 2, growx, align right");
	}

	private RoundedButton crearBoton(String txt, Color bg, ActionListener evt) {
		RoundedButton b = new RoundedButton(txt, 20);
		b.setBackground(bg);
		b.setForeground(Color.WHITE);
		b.addActionListener(evt);
		b.setLayout(new OverlayLayout(b));
		b.setMinimumSize(new Dimension(150, 50));
		b.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		return b;
	}

	private JLabel labelInfo(String titulo, String valor) {
		JLabel lbl = new JLabel(titulo + ": " + valor);
		lbl.setForeground(Color.WHITE);
		lbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		return lbl;
	}

	private ImageIcon escalarImagen(String path, int w, int h) {
		try {
			ImageIcon ic = new ImageIcon(getClass().getResource(path));
			Image img = ic.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
			return new ImageIcon(img);
		} catch (Exception e) {
			return new ImageIcon();
		}
	}

	// REESCALADO
	private void setupTopbarDrag() {
		topBar.addMouseListener(new MouseAdapter() {
			Point clickPoint = null;

			@Override
			public void mousePressed(MouseEvent e) {
				Component c = topBar.getComponentAt(e.getPoint());
				if (c == topBar) {
					clickPoint = e.getLocationOnScreen();
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				clickPoint = null;
			}
		});

		topBar.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				Component c = topBar.getComponentAt(e.getPoint());
				if (c == topBar) {
					Point now = e.getLocationOnScreen();
					Point loc = getLocation();
					setLocation(loc.x + now.x - topBar.getX(), loc.y + now.y - topBar.getY());
				}
			}
		});
	}

	private void toggleMaximize() {
		Rectangle target;

		if (!maximizado) {
			prevBounds = getBounds();
			target = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		} else {
			target = prevBounds != null ? prevBounds : new Rectangle(100, 100, 950, 600);
		}

		animateBounds(getBounds(), target, 250);
		maximizado = !maximizado;
	}

	private void animateBounds(Rectangle start, Rectangle end, int durationMs) {
		if (animTimer != null)
			animTimer.stop();

		final long startTime = System.currentTimeMillis();
		animTimer = new Timer(15, e -> {
			float t = (System.currentTimeMillis() - startTime) / (float) durationMs;
			if (t > 1f)
				t = 1f;

			float f = (float) (1 - Math.pow(1 - t, 3));

			int nx = start.x + Math.round((end.x - start.x) * f);
			int ny = start.y + Math.round((end.y - start.y) * f);
			int nw = start.width + Math.round((end.width - start.width) * f);
			int nh = start.height + Math.round((end.height - start.height) * f);

			setBounds(nx, ny, nw, nh);

			if (t == 1f)
				animTimer.stop();
		});
		animTimer.start();
	}
	
	// ----------- DATOS ------------

	private int obtenerIdUsuario(long control) {
		if (usuarioIdCache != null)
			return usuarioIdCache;

		String sql = "SELECT id FROM usuarios WHERE no_control = ?";

		try (Connection cn = connectionDB.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {

			ps.setLong(1, control);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				usuarioIdCache = rs.getInt("id");
				return usuarioIdCache;
			}

		} catch (SQLException e) {
			System.out.println("Error obteniendo id usuario: " + e.getMessage());
		}

		return -1;
	}

	private String obtenerGrupoEstudiante(long control) {
		if (grupoCache != null)
			return grupoCache;

		String sql = """
				SELECT g.nombre_grupo
				FROM usuarios u
				LEFT JOIN grupos g ON u.grupo_id = g.id
				WHERE u.no_control = ?
				""";

		try (Connection cn = connectionDB.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {

			ps.setLong(1, control);
			ResultSet rs = ps.executeQuery();

			grupoCache = rs.next() ? rs.getString(1) : "No asignado";
			return grupoCache;

		} catch (SQLException e) {
			System.out.println("Error grupo: " + e.getMessage());
			return "No asignado";
		}
	}

	private void abrirMensajes() {
		int usuarioId = obtenerIdUsuario(numControl);

		if (usuarioId <= 0) {
			JOptionPane.showMessageDialog(this, "Error al cargar mensajes");
			return;
		}

		new BandejaMensajes(usuarioId).setVisible(true);

		actualizarBadgeBackground();
	}

	private int obtenerMensajesNoLeidos(int usuarioId) {

		String sql = """
				SELECT COUNT(*)
				FROM mensajes_destinatarios
				WHERE destinatario_id = ? AND leido = FALSE
				""";

		try (Connection cn = connectionDB.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {

			ps.setInt(1, usuarioId);
			ResultSet rs = ps.executeQuery();
			return rs.next() ? rs.getInt(1) : 0;

		} catch (SQLException e) {
			System.out.println("Error mensajes: " + e.getMessage());
			return 0;
		}
	}

	private void iniciarActualizacionAutomatica() {
		badgeMensajes = crearBadge();
		btnMensajes.add(badgeMensajes);

		new Timer(5000, e -> actualizarBadgeBackground()).start();

		actualizarBadgeBackground();
	}

	private JLabel crearBadge() {
		JLabel b = new JLabel("0", SwingConstants.CENTER);

		b.setOpaque(true);
		b.setBackground(new Color(220, 53, 69));
		b.setForeground(Color.WHITE);
		b.setFont(new Font("Segoe UI", Font.BOLD, 11));
		b.setPreferredSize(new Dimension(20, 20));

		// Aineado arriba a la derecha
		b.setAlignmentX(1.0f);
		b.setAlignmentY(0.0f);

		b.setVisible(false);

		return b;
	}

	private void actualizarBadgeBackground() {
		SwingWorker<Integer, Void> worker = new SwingWorker<>() {

			@Override
			protected Integer doInBackground() {
				int uid = obtenerIdUsuario(numControl);
				return uid > 0 ? obtenerMensajesNoLeidos(uid) : 0;
			}

			@Override
			protected void done() {
				try {
					actualizarBadgeVisual(get());
				} catch (Exception ignored) {
				}
			}
		};
		worker.execute();
	}

	private void actualizarBadgeVisual(int unread) {
		if (unread <= 0) {
			badgeMensajes.setVisible(false);
			return;
		}
		badgeMensajes.setText(String.valueOf(unread));
		badgeMensajes.setVisible(true);
	}

}
