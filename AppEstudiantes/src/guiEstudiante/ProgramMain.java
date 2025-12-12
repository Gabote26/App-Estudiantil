package guiEstudiante;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import db.ConexionMysql;
import main.LoginSystem;
import main.Settings;
import utils.RoundedButton;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ProgramMain extends JFrame {

	private static final long serialVersionUID = 1L;

	private final ConexionMysql connectionDB = new ConexionMysql();
	private final long numControl;
	private final String nombre, apellido;

	// Parámetros para la animación de ventana
	private boolean maximizado = false;
	private Rectangle prevBounds;
	private Timer animTimer;

	// Componentes principales
	private JPanel topBar;
	private JPanel mainPanel;
	private JPanel headerRight;
	private JPanel botonesPanel;
	private JPanel infoPanel;
	private JLabel lblWelcome;
	private JLabel lblLogo;
	
	// Botones principales
	private RoundedButton btnMensajes;
	private RoundedButton btnCalificaciones;
	private RoundedButton btnAsistencias;
	private RoundedButton btnHorarios;
	
	// Botones laterales
	private RoundedButton btnSettings;
	private RoundedButton btnLogout;

	// Badge de notificación
	private JLabel badgeMensajes;

	private Integer usuarioIdCache = null;
	private String grupoCache = null;
	
	// Variables para el redimensionamiento
	private static final int RESIZE_MARGIN = 5;
	private int resizeDirection = 0;
	private Point initialClick;
	
	// Constantes para direcciones de redimensionamiento
	private static final int RESIZE_NONE = 0;
	private static final int RESIZE_N = 1;
	private static final int RESIZE_S = 2;
	private static final int RESIZE_W = 4;
	private static final int RESIZE_E = 8;
	private static final int RESIZE_NW = RESIZE_N | RESIZE_W;
	private static final int RESIZE_NE = RESIZE_N | RESIZE_E;
	private static final int RESIZE_SW = RESIZE_S | RESIZE_W;
	private static final int RESIZE_SE = RESIZE_S | RESIZE_E;
	
	// Constantes para dimensiones
	private static final int TOP_BAR_HEIGHT = 40;
	private static final int HEADER_RIGHT_WIDTH = 180;
	private static final int MARGIN = 20;
	private static final int BUTTON_HEIGHT = 60;
	
	// Imágenes originales
	private ImageIcon welcomeOriginal;
	private ImageIcon logoOriginal;

	public ProgramMain(long numControl, String nombre, String apellido) {
		this.numControl = numControl;
		this.nombre = nombre;
		this.apellido = apellido;

		setTitle("Estudiante - " + nombre + " " + apellido);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setUndecorated(true);

		setSize(950, 600);
		setLocationRelativeTo(null);
		
		// Cargar imágenes originales
		cargarImagenesOriginales();

		initComponents();
		setupListeners();
		iniciarActualizacionAutomatica();
		
		// Ajustar layout inicial
		SwingUtilities.invokeLater(this::adjustLayout);
	}
	
	private ImageIcon scaleImageProportionally(ImageIcon original, int maxW, int maxH) {
	    if (original == null) return null;

	    int ow = original.getIconWidth();
	    int oh = original.getIconHeight();

	    if (ow <= 0 || oh <= 0 || maxW <= 0 || maxH <= 0) return null;

	    double scale = Math.min((double) maxW / ow, (double) maxH / oh);

	    int newW = (int) (ow * scale);
	    int newH = (int) (oh * scale);

	    Image scaled = original.getImage().getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
	    return new ImageIcon(scaled);
	}
	
	private void cargarImagenesOriginales() {
		try {
			welcomeOriginal = new ImageIcon(getClass().getResource("/welcome.png"));
		} catch (Exception e) {
			welcomeOriginal = null;
		}
		
		try {
			logoOriginal = new ImageIcon(getClass().getResource("/appLogoImg.png"));
		} catch (Exception e) {
			logoOriginal = null;
		}
	}

	// Inicializar los componentes de la aplicación
	private void initComponents() {
		getContentPane().setLayout(null);
		getContentPane().setBackground(new Color(38, 47, 87));
		
		// ------- Top Bar -------
		topBar = new JPanel(new BorderLayout());
		topBar.setBackground(new Color(28, 36, 70));
		
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
		getContentPane().add(topBar);
		
		// ------- Panel Principal -------
		mainPanel = new JPanel(null);
		mainPanel.setBackground(new Color(38, 47, 87));
		getContentPane().add(mainPanel);
		
		// ------- Panel de Botones -------
		botonesPanel = new JPanel(null);
		botonesPanel.setOpaque(false);
		mainPanel.add(botonesPanel);
		
		// Crear botones principales
		btnMensajes = crearBoton("📬 Mis Mensajes", new Color(52, 152, 219));
		btnMensajes.addActionListener(e -> abrirMensajes());
		
		btnCalificaciones = crearBoton("📝 Mis Calificaciones", new Color(155, 89, 182));
		btnCalificaciones.addActionListener(e -> new VerCalificaciones(numControl, nombre, apellido).setVisible(true));
		
		btnAsistencias = crearBoton("📊 Mis Asistencias", new Color(46, 204, 113));
		btnAsistencias.addActionListener(e -> new VerAsistencias(numControl, nombre, apellido).setVisible(true));
		
		btnHorarios = crearBoton("🗓️ Horarios", new Color(190, 190, 190));
		btnHorarios.addActionListener(e -> new HorariosEstudiantes().setVisible(true));
		
		botonesPanel.add(btnMensajes);
		botonesPanel.add(btnCalificaciones);
		botonesPanel.add(btnAsistencias);
		botonesPanel.add(btnHorarios);
		
		// ------- Panel de informacion del usuario -------
		infoPanel = new JPanel(null);
		infoPanel.setOpaque(true);
		infoPanel.setBackground(new Color(42, 46, 60));
		
		TitledBorder tb = new TitledBorder("Información del Estudiante");
		tb.setTitleColor(Color.WHITE);
		tb.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));
		infoPanel.setBorder(tb);
		
		JLabel lblNombre = crearLabelInfo("Nombre: " + nombre + " " + apellido);
		lblNombre.setBounds(15, 25, 300, 25);
		infoPanel.add(lblNombre);
		
		JLabel lblControl = crearLabelInfo("No. Control: " + numControl);
		lblControl.setBounds(15, 55, 300, 25);
		infoPanel.add(lblControl);
		
		JLabel lblGrupo = crearLabelInfo("Grupo: " + obtenerGrupoEstudiante(numControl));
		lblGrupo.setBounds(15, 85, 300, 25);
		infoPanel.add(lblGrupo);
		
		mainPanel.add(infoPanel);
		
		// ------- Imagenes -------
		lblWelcome = new JLabel();
		lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
		mainPanel.add(lblWelcome);
		
		lblLogo = new JLabel();
		lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
		mainPanel.add(lblLogo);
		
		// ------- Panel secundario -------
		headerRight = new JPanel(null);
		headerRight.setBackground(new Color(42, 46, 60));
		
		btnSettings = new RoundedButton("⚙️ Configuración", 18);
		btnSettings.setBackground(new Color(60, 65, 78));
		btnSettings.setForeground(Color.WHITE);
		btnSettings.setToolTipText("Abrir configuración");
		btnSettings.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
		btnSettings.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnSettings.setFocusPainted(false);
		btnSettings.setBorderPainted(false);
		btnSettings.setOpaque(false);
		btnSettings.setContentAreaFilled(false);
		btnSettings.setBounds(15, 30, 150, 38);
		btnSettings.addActionListener(e -> new Settings().setVisible(true));
		
		btnLogout = new RoundedButton("🔒 Cerrar Sesión", 20);
		btnLogout.setBackground(new Color(247, 79, 79));
		btnLogout.setForeground(Color.WHITE);
		btnLogout.setBounds(25, 400, 132, 39);
		btnSettings.setFocusPainted(false);
		btnLogout.setBorderPainted(false);
		btnLogout.setOpaque(false);
		btnLogout.setContentAreaFilled(false);
		btnLogout.addActionListener(e -> LoginSystem.cerrarSesion(this));
		
		headerRight.add(btnSettings);
		headerRight.add(btnLogout);
		getContentPane().add(headerRight);
	}
	
	private void setupListeners() {
		// Drag de ventana
		addDragListener(topBar);
		
		// Redimensionamiento
		setupResizeListeners();
		
		// Listener para adaptación responsive
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				adjustLayout();
			}
		});
	}
	
	// ------- Ajuste de layout responsivo -------
	private void adjustLayout() {
		int frameWidth = getWidth();
		int frameHeight = getHeight();
		
		// ------- Top Bar -------
		topBar.setBounds(0, 0, frameWidth, TOP_BAR_HEIGHT);
		
		// ------- Panel derecho (secundario) -------
		int headerRightX = frameWidth - HEADER_RIGHT_WIDTH;
		int headerRightHeight = frameHeight - TOP_BAR_HEIGHT;
		headerRight.setBounds(headerRightX, TOP_BAR_HEIGHT, HEADER_RIGHT_WIDTH, headerRightHeight);
		
		// Reposicionar botón de logout
		btnLogout.setBounds(25, headerRightHeight - 70, 132, 39);
		
		// ------- Panel Principal -------
		int mainPanelWidth = headerRightX - MARGIN * 2;
		int mainPanelHeight = headerRightHeight - MARGIN * 2;
		mainPanel.setBounds(MARGIN, TOP_BAR_HEIGHT + MARGIN, mainPanelWidth, mainPanelHeight);
		
		// ------- Panel de Botones -------
		int buttonWidth = 200;
		int buttonGap = 15;
		int totalWidthFor4 = (buttonWidth * 4) + (buttonGap * 3);
		boolean use2x2 = mainPanelWidth < totalWidthFor4;
		
		if (use2x2) {
			// Distribución 2x2
			int totalWidth2x2 = (buttonWidth * 2) + buttonGap;
			int startX = (mainPanelWidth - totalWidth2x2) / 2;
			int totalHeight2x2 = (BUTTON_HEIGHT * 2) + buttonGap;
			
			btnMensajes.setBounds(startX, 10, buttonWidth, BUTTON_HEIGHT);
			btnCalificaciones.setBounds(startX + buttonWidth + buttonGap, 10, buttonWidth, BUTTON_HEIGHT);
			btnAsistencias.setBounds(startX, 10 + BUTTON_HEIGHT + buttonGap, buttonWidth, BUTTON_HEIGHT);
			btnHorarios.setBounds(startX + buttonWidth + buttonGap, 10 + BUTTON_HEIGHT + buttonGap, buttonWidth, BUTTON_HEIGHT);
			
			botonesPanel.setBounds(0, 0, mainPanelWidth, totalHeight2x2 + 20);
		} else {
			// Distribución 1x4
			int startX = (mainPanelWidth - totalWidthFor4) / 2;
			
			btnMensajes.setBounds(startX, 10, buttonWidth, BUTTON_HEIGHT);
			btnCalificaciones.setBounds(startX + buttonWidth + buttonGap, 10, buttonWidth, BUTTON_HEIGHT);
			btnAsistencias.setBounds(startX + (buttonWidth + buttonGap) * 2, 10, buttonWidth, BUTTON_HEIGHT);
			btnHorarios.setBounds(startX + (buttonWidth + buttonGap) * 3, 10, buttonWidth, BUTTON_HEIGHT);
			
			botonesPanel.setBounds(0, 0, mainPanelWidth, BUTTON_HEIGHT + 20);
		}
		
		// ------- Panel de información -------
		int infoPanelY = botonesPanel.getHeight() + 20;
		int infoPanelWidth = Math.min(350, mainPanelWidth / 2 - 20);
		int infoPanelHeight = 140;
		infoPanel.setBounds(20, infoPanelY, infoPanelWidth, infoPanelHeight);
		
		// ------- Imagen de bienvenida -------
		int welcomeX = infoPanelWidth + 40;
		int welcomeY = infoPanelY;
		int welcomeWidth = mainPanelWidth - welcomeX - 20;
		int welcomeHeight = Math.min(300, mainPanelHeight - infoPanelY - 20);
		
		// Escalar imagen welcome proporcional
		if (welcomeOriginal != null) {
		    ImageIcon scaled = scaleImageProportionally(
		        welcomeOriginal,
		        welcomeWidth,
		        welcomeHeight
		    );

		    lblWelcome.setIcon(scaled);

		    int centeredX = welcomeX + (welcomeWidth - scaled.getIconWidth()) / 2;
		    int centeredY = welcomeY + (welcomeHeight - scaled.getIconHeight()) / 2;
		    
		    lblWelcome.setBounds(centeredX, centeredY, scaled.getIconWidth(), scaled.getIconHeight());
		} else {
		    lblWelcome.setBounds(welcomeX, welcomeY, welcomeWidth, welcomeHeight);
		}
		
		// ------- Logo -------
		int logoY = infoPanelY + infoPanelHeight + 20;
		int logoWidth = infoPanelWidth;
		int logoHeight = Math.min(250, mainPanelHeight - logoY - 20);

		// Escalar imagen logo proporcional
		if (logoOriginal != null && logoHeight > 50) {
		    ImageIcon scaledLogo = scaleImageProportionally(
		        logoOriginal,
		        logoWidth,
		        logoHeight
		    );

		    lblLogo.setIcon(scaledLogo);

		    int centeredX = 20 + (logoWidth - scaledLogo.getIconWidth()) / 2;

		    lblLogo.setBounds(
		        centeredX,
		        logoY,
		        scaledLogo.getIconWidth(),
		        scaledLogo.getIconHeight()
		    );
		} else {
		    lblLogo.setBounds(20, logoY, logoWidth, 0);
		}
		
		revalidate();
		repaint();
	}

	// Crear boton del topBar
	private JButton createTopButton(String txt, ActionListener evt) {
		JButton b = new JButton(txt);
		b.setFocusable(false);
		b.setBackground(new Color(60, 60, 60));
		b.setForeground(Color.WHITE);
		b.setBorder(null);
		b.setPreferredSize(new Dimension(40, 40));
		b.addActionListener(evt);
		b.setCursor(new Cursor(Cursor.HAND_CURSOR));
		return b;
	}

	// Crear boton
	private RoundedButton crearBoton(String txt, Color bg) {
	    RoundedButton b = new RoundedButton(txt, 20);
	    b.setBackground(bg);
	    b.setForeground(Color.WHITE);
	    b.setLayout(null);
	    b.setMinimumSize(new Dimension(150, 50));
	    b.setCursor(new Cursor(Cursor.HAND_CURSOR));
	    b.setFocusPainted(false);
	    b.setOpaque(false);
	    b.setContentAreaFilled(false);
	    b.setBorderPainted(false);

	    return b;
	}

	private JLabel crearLabelInfo(String texto) {
		JLabel lbl = new JLabel(texto);
		lbl.setForeground(Color.WHITE);
		lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		return lbl;
	}

	// ------- Sistema para arrastrar la ventana -------
	private void addDragListener(JPanel panel) {
		final Point[] p = new Point[1];
		
		panel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				Component c = panel.getComponentAt(e.getPoint());
				if (c == panel || c.getParent() == panel) {
					p[0] = e.getPoint();
				}
			}
			
			public void mouseReleased(MouseEvent e) {
				p[0] = null;
			}
		});
		
		panel.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				Component c = panel.getComponentAt(e.getPoint());
				if ((c == panel || c.getParent() == panel) && p[0] != null) {
					Point now = e.getLocationOnScreen();
					Point loc = getLocation();
					setLocation(loc.x + now.x - p[0].x - loc.x, loc.y + now.y - p[0].y - loc.y);
				}
			}
		});
	}

	// ------- Maximizar o restaurar el tamaño de la ventana con animacion -------
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
	
	// ------- Permitir redimensionar la ventana arrastrando bordes -------
	private void setupResizeListeners() {
		MouseAdapter resizeAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				initialClick = e.getPoint();
				resizeDirection = getResizeDirection(e.getPoint());
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				resizeDirection = RESIZE_NONE;
				setCursor(Cursor.getDefaultCursor());
			}
			
			@Override
			public void mouseMoved(MouseEvent e) {
				updateCursor(e.getPoint());
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				if (resizeDirection != RESIZE_NONE) {
					resizeWindow(e.getPoint());
				}
			}
		};
		
		addMouseListener(resizeAdapter);
		addMouseMotionListener(resizeAdapter);
	}
	
	private int getResizeDirection(Point p) {
		int dir = RESIZE_NONE;
		
		if (p.x < RESIZE_MARGIN) dir |= RESIZE_W;
		else if (p.x > getWidth() - RESIZE_MARGIN) dir |= RESIZE_E;
		
		if (p.y < RESIZE_MARGIN) dir |= RESIZE_N;
		else if (p.y > getHeight() - RESIZE_MARGIN) dir |= RESIZE_S;
		
		return dir;
	}
	
	private void updateCursor(Point p) {
		int dir = getResizeDirection(p);
		
		switch (dir) {
			case RESIZE_N:
			case RESIZE_S:
				setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
				break;
			case RESIZE_W:
			case RESIZE_E:
				setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
				break;
			case RESIZE_NW:
			case RESIZE_SE:
				setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
				break;
			case RESIZE_NE:
			case RESIZE_SW:
				setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
				break;
			default:
				setCursor(Cursor.getDefaultCursor());
				break;
		}
	}
	
	private void resizeWindow(Point currentPoint) {
		Rectangle bounds = getBounds();
		int dx = currentPoint.x - initialClick.x;
		int dy = currentPoint.y - initialClick.y;
		
		int minWidth = 700;
		int minHeight = 450;
		
		// Redimensionar según la dirección
		if ((resizeDirection & RESIZE_W) != 0) {
			int newWidth = bounds.width - dx;
			if (newWidth >= minWidth) {
				bounds.x += dx;
				bounds.width = newWidth;
				initialClick.x = currentPoint.x;
			}
		}
		
		if ((resizeDirection & RESIZE_E) != 0) {
			int newWidth = bounds.width + dx;
			if (newWidth >= minWidth) {
				bounds.width = newWidth;
				initialClick.x = currentPoint.x;
			}
		}
		
		if ((resizeDirection & RESIZE_N) != 0) {
			int newHeight = bounds.height - dy;
			if (newHeight >= minHeight) {
				bounds.y += dy;
				bounds.height = newHeight;
				initialClick.y = currentPoint.y;
			}
		}
		
		if ((resizeDirection & RESIZE_S) != 0) {
			int newHeight = bounds.height + dy;
			if (newHeight >= minHeight) {
				bounds.height = newHeight;
				initialClick.y = currentPoint.y;
			}
		}
		
		setBounds(bounds);
	}

	// ----------- Datos de usuario ------------

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
		btnMensajes.setLayout(new OverlayLayout(btnMensajes));
		btnMensajes.add(badgeMensajes);

		new Timer(5000, e -> actualizarBadgeBackground()).start();

		actualizarBadgeBackground();
	}

	// Crear el badge de notificaciones
	private JLabel crearBadge() {
		JLabel b = new JLabel("0", SwingConstants.CENTER);

		b.setOpaque(true);
		b.setBackground(new Color(220, 53, 69));
		b.setForeground(Color.WHITE);
		b.setFont(new Font("Segoe UI", Font.BOLD, 11));
		b.setPreferredSize(new Dimension(20, 20));
		b.setMaximumSize(new Dimension(20, 20));

		// Alineado arriba a la derecha
		b.setAlignmentX(1.0f);
		b.setAlignmentY(0.0f);

		b.setVisible(false);

		return b;
	}

	// Actualizar el fondo de badge
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

	// Actualizar elemento visual del badge para coincidir con el numero de mensajes sin leer
	private void actualizarBadgeVisual(int unread) {
		if (unread <= 0) {
			badgeMensajes.setVisible(false);
			return;
		}
		badgeMensajes.setText(String.valueOf(unread));
		badgeMensajes.setVisible(true);
	}

}