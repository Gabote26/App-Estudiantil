package guiBase;

import db.ConexionMysql;
import main.LoginSystem;
import main.Settings;
import utils.ConfigManager;
import utils.Recargable;
import utils.RoundedButton;
import utils.SessionManager;
import utils.ThemeManager;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public abstract class BaseMainFrame extends JFrame implements Recargable {

	private static final long serialVersionUID = 1L;

	protected final ConexionMysql connectionDB = new ConexionMysql();

    protected JTable tableEstudiantes;
    protected DefaultTableModel model;
    protected JComboBox<String> cbGrupos;
    protected int grupoSeleccionadoId = -1;

    protected RoundedButton btnGestionar, btnRefrescar, btnAgregar, btnEliminar, btnEditar, btnSendMsg;
    protected JPanel actionPanel;

    protected boolean darkMode = false;
    private RoundedButton btnSettings;
    protected RoundedButton btnCalificaciones;

    // Componentes para el tema
    private JPanel headerPanel;
    private JLabel lblTitulo;
    private JPanel headerRight;
    private JPanel searchPanel;
    private JLabel lblBuscar;
    private JComboBox<String> cbFiltro;
    private JTextField txtBuscar;
    private JScrollPane scrollPane;
    private JPanel grupoPanel;
    private JLabel lblGrupo;

    public BaseMainFrame(String tituloVentana, String tituloHeader) {
        setTitle(tituloVentana);
        setSize(1350, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);

        setLocationRelativeTo(null);
        getContentPane().setLayout(null);
        getContentPane().setBackground(new Color(250, 250, 252));

        // Se registra la ventana en el ThemeManager para que pueda establecerle algun tema
        ThemeManager.registerFrame(this);
        
        // Cargar el tema guardado
        darkMode = ConfigManager.isDarkMode();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ThemeManager.unregisterFrame(BaseMainFrame.this);
            }
        });

        // ======= HEADER =======
        headerPanel = new JPanel(null);
        headerPanel.setBounds(0, 0, 1350, 72);
        headerPanel.setBackground(new Color(80, 90, 140));

        lblTitulo = new JLabel(tituloHeader);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBounds(18, 20, 400, 30);
        headerPanel.add(lblTitulo);
        getContentPane().add(headerPanel);

        // ======= PANEL DERECHO =======
        headerRight = new JPanel(null);
        headerRight.setBounds(1170, 72, 180, 468);
        headerRight.setBackground(Color.WHITE);

        btnSettings = new RoundedButton("⚙️ Configuración", 18);
        btnSettings.setBackground(new Color(247, 248, 250));
        btnSettings.setForeground(new Color(45, 45, 45));
        btnSettings.setToolTipText("Abrir configuración");
        btnSettings.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        btnSettings.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSettings.setFocusPainted(false);
        btnSettings.setBorderPainted(false);
        btnSettings.setOpaque(true);
        btnSettings.setBounds(15, 30, 150, 38);
        btnSettings.addActionListener(e -> {
            new Settings();
        });
        
     // Cerrar Sesión
     		RoundedButton logOutBtn = new RoundedButton("🔒 Cerrar Sesion", 20);
     		logOutBtn.setBackground(new Color(247, 79, 79));
     		logOutBtn.setForeground(Color.WHITE);
     		logOutBtn.setBounds(25, 400, 132, 39);
     		logOutBtn.addActionListener(e -> {
    		    LoginSystem.cerrarSesion(this);
    		});
        
        headerRight.add(btnSettings);
        headerRight.add(logOutBtn);
        getContentPane().add(headerRight);

        // ======= PANEL DE BÚSQUEDA =======
        searchPanel = new JPanel(null);
        searchPanel.setBounds(0, 72, 1170, 60);
        searchPanel.setBackground(Color.WHITE);

        lblBuscar = new JLabel("Buscar por:");
        lblBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblBuscar.setForeground(new Color(45, 45, 45));
        lblBuscar.setBounds(20, 15, 90, 30);
        searchPanel.add(lblBuscar);

        String[] criterios = {"Todos", "Nombre", "Apellido", "Email", "No-Control"};
        cbFiltro = new JComboBox<>(criterios);
        cbFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbFiltro.setBackground(Color.WHITE);
        cbFiltro.setForeground(new Color(45, 45, 45));
        cbFiltro.setBounds(110, 15, 140, 30);
        searchPanel.add(cbFiltro);

        txtBuscar = new JTextField();
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBuscar.setBackground(Color.WHITE);
        txtBuscar.setForeground(new Color(45, 45, 45));
        txtBuscar.setBounds(260, 15, 400, 30);
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        searchPanel.add(txtBuscar);
        getContentPane().add(searchPanel);

        // ======= TABLA =======
        model = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Apellido", "Email", "Rol", "No-Control", "Grupo"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tableEstudiantes = new JTable(model);
        tableEstudiantes.setRowHeight(34);
        tableEstudiantes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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

        scrollPane = new JScrollPane(tableEstudiantes);
        scrollPane.setBounds(20, 140, 1130, 280);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        getContentPane().add(scrollPane);

        // ======= FILTRO DINÁMICO =======
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        tableEstudiantes.setRowSorter(sorter);
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            private void filtrar() {
                String texto = txtBuscar.getText().trim();
                String filtro = cbFiltro.getSelectedItem().toString();
                if (texto.isEmpty()) {
                    sorter.setRowFilter(null);
                    return;
                }
                int columna = switch (filtro) {
                    case "Nombre" -> 1;
                    case "Apellido" -> 2;
                    case "Email" -> 3;
                    case "No-Control" -> 5;
                    default -> -1;
                };
                if (columna == -1)
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
                else
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto, columna));
            }

            @Override
            public void insertUpdate(DocumentEvent e) { filtrar(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filtrar(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filtrar(); }
        });
        
     // ---------- CERRAR VENTANA ----------
        JButton closeBtn = new JButton("X");
        closeBtn.setBounds(1300, 10, 40, 30);
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setBackground(new Color(153, 61, 61));
        closeBtn.setBorder(null);
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> System.exit(0));
        headerPanel.add(closeBtn);

        addDragListener(headerPanel);

        // ======= PANEL DE GRUPOS =======
        grupoPanel = new JPanel(null);
        grupoPanel.setBounds(20, 430, 1130, 50);
        grupoPanel.setBackground(Color.WHITE);

        lblGrupo = new JLabel("Seleccionar Grupo:");
        lblGrupo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblGrupo.setForeground(new Color(51, 51, 51));
        lblGrupo.setBounds(10, 10, 140, 30);
        grupoPanel.add(lblGrupo);

        cbGrupos = new JComboBox<>();
        cbGrupos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbGrupos.setBackground(Color.WHITE);
        cbGrupos.setForeground(new Color(45, 45, 45));
        cbGrupos.setBounds(150, 10, 220, 30);
        grupoPanel.add(cbGrupos);
        getContentPane().add(grupoPanel);

        // ======= PANEL DE ACCIONES =======
        actionPanel = new JPanel(null);
        actionPanel.setBounds(20, 480, 1130, 50);
        actionPanel.setBackground(Color.WHITE);
        getContentPane().add(actionPanel);

        btnRefrescar = new RoundedButton("🔄 Refrescar Lista", 20);
        btnEliminar = new RoundedButton("🗑️ Eliminar Estudiante", 20);
        btnEditar = new RoundedButton("✏️ Editar Estudiante", 20);
        btnAgregar = new RoundedButton("➕ Agregar Estudiante", 20);
        btnSendMsg = new RoundedButton("📣 Enviar Mensaje", 20);
        btnGestionar = new RoundedButton("📋 Gestionar Estudiante", 20);
        
        btnCalificaciones = new RoundedButton("📝 Gestionar Calificaciones", 20);
        btnCalificaciones.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        btnCalificaciones.setBackground(new Color(245, 245, 245));
        btnCalificaciones.setForeground(new Color(48, 48, 48));
        btnCalificaciones.setFocusPainted(false);
        btnCalificaciones.setBorderPainted(false);
        btnCalificaciones.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCalificaciones.setOpaque(true);
        
        // Estilo de botones
        styleActionButton(btnGestionar, new Color(245, 245, 245), new Color(230, 230, 230));
        styleActionButton(btnRefrescar, new Color(245, 245, 245), new Color(230, 230, 230));
        styleActionButton(btnAgregar, new Color(245, 245, 245), new Color(230, 230, 230));
        styleActionButton(btnEditar, new Color(245, 245, 245), new Color(230, 230, 230));
        styleActionButton(btnEliminar, new Color(245, 245, 245), new Color(230, 230, 230));
        styleActionButton(btnSendMsg, new Color(245, 245, 245), new Color(230, 230, 230));
        styleActionButton(btnCalificaciones, new Color(245, 245, 245), new Color(230, 230, 230));

        //actionPanel.add(btnGestionar);
        actionPanel.add(btnRefrescar);
        actionPanel.add(btnAgregar);
        actionPanel.add(btnEditar);
        actionPanel.add(btnEliminar);
        actionPanel.add(btnSendMsg);
        actionPanel.add(btnCalificaciones);

        btnRefrescar.setBounds(10, 10, 160, 30);
        btnAgregar.setBounds(180, 10, 180, 30);
        btnEditar.setBounds(370, 10, 160, 30);
        btnEliminar.setBounds(540, 10, 180, 30);
        btnSendMsg.setBounds(730, 10, 160, 30);
        btnGestionar.setBounds(900, 10, 180, 30);
        btnCalificaciones.setBounds(210, 50, 190, 30);

        // ======= LISTENERS =======
        cbGrupos.addActionListener(e -> {
            grupoSeleccionadoId = (cbGrupos.getSelectedIndex() > 0)
                    ? obtenerIdGrupo(cbGrupos.getSelectedItem().toString())
                    : -1;
            cargarEstudiantes();
        });

        cargarGrupos();
        cargarEstudiantes();
        aplicarTema();
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

    // =================== MÉTODOS COMUNES ===================
    protected void cargarGrupos() {
        cbGrupos.removeAllItems();
        cbGrupos.addItem("Todos los grupos");
        try (Connection cn = connectionDB.conectar();
             PreparedStatement ps = cn.prepareStatement("SELECT id, nombre_grupo FROM grupos");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cbGrupos.addItem(rs.getString("nombre_grupo"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar grupos:\n" + e.getMessage());
        }
    }

    protected int obtenerIdGrupo(String nombreGrupo) {
        String sql = "SELECT id FROM grupos WHERE nombre_grupo = ?";
        try (Connection cn = connectionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombreGrupo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            System.err.println("Error al obtener id del grupo: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public void cargarEstudiantes() {
        model.setRowCount(0);
        String base = "SELECT u.id, u.nombre, u.apellido, u.email, u.role, u.no_control, g.nombre_grupo " +
                "FROM usuarios u LEFT JOIN grupos g ON u.grupo_id = g.id WHERE u.role = 'ESTUDIANTE'";
        boolean filtroGrupo = grupoSeleccionadoId != -1;
        String query = filtroGrupo ? base + " AND u.grupo_id = ?" : base;

        try (Connection cn = connectionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(query)) {
            if (filtroGrupo) ps.setInt(1, grupoSeleccionadoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"), rs.getString("nombre"), rs.getString("apellido"),
                        rs.getString("email"), rs.getString("role"),
                        rs.getString("no_control"), rs.getString("nombre_grupo")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar estudiantes:\n" + e.getMessage());
        }
    }

    // ======= Métodos abstractos =======
    protected abstract void eliminarEstudiante();
    protected abstract void editarEstudiante();
    protected abstract void agregarEstudiante();
    protected abstract void enviarMensaje();

    // ======= ESTILO DE BOTONES =======
    private void styleActionButton(RoundedButton b, Color bg, Color bgHoverBase) {
        b.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
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
                b.setBackground(hover);
                if (isDark(hover))
                    b.setForeground(Color.WHITE);
                else
                    b.setForeground(new Color(30, 30, 30));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                b.setBackground(bg);
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

    // ======= MÉTODOS PARA QUE THEMEMANAGER FUNCIONE =======
    
    /**
     * Establece el modo oscuro
     * @param dark true para modo oscuro, false para modo claro
     */
    public void setDarkMode(boolean dark) {
        this.darkMode = dark;
    }
    
    /**
     * Obtiene el estado actual del modo oscuro
     * @return true si está en modo oscuro
     */
    public boolean isDarkMode() {
        return this.darkMode;
    }

    // ======= Tema claro / oscuro =======
    public void aplicarTema() {
        if (darkMode) {
            // Modo Obscuro
            Color bg = new Color(34, 38, 48);
            Color panel = new Color(42, 46, 60);
            Color header = new Color(60, 70, 110);
            Color text = new Color(230, 230, 235);
            Color inputBg = new Color(60, 65, 78);
            Color border = new Color(70, 75, 90);
            Color tableHeader = new Color(48, 52, 66);

            getContentPane().setBackground(bg);
            
            // Header
            headerPanel.setBackground(header);
            lblTitulo.setForeground(text);
            
            // Panel derecho
            headerRight.setBackground(panel);
            btnSettings.setBackground(new Color(60, 65, 78));
            btnSettings.setForeground(Color.WHITE);
            
            // Panel de búsqueda
            searchPanel.setBackground(panel);
            lblBuscar.setForeground(text);
            cbFiltro.setBackground(inputBg);
            cbFiltro.setForeground(text);
            txtBuscar.setBackground(inputBg);
            txtBuscar.setForeground(text);
            txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(border),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)
            ));
            
            // Tabla
            scrollPane.getViewport().setBackground(panel);
            JTableHeader th = tableEstudiantes.getTableHeader();
            th.setBackground(tableHeader);
            th.setForeground(text);
            tableEstudiantes.setBackground(new Color(44, 48, 62));
            tableEstudiantes.setForeground(text);
            tableEstudiantes.setSelectionBackground(new Color(70, 80, 110));
            tableEstudiantes.setGridColor(new Color(50, 55, 70));
            
            // Panel de grupos
            grupoPanel.setBackground(panel);
            lblGrupo.setForeground(text);
            cbGrupos.setBackground(inputBg);
            cbGrupos.setForeground(text);
            
            // Panel de acciones
            actionPanel.setBackground(panel);
            
            // Actualizar estilo de botones
            updateButtonStyle(btnGestionar, new Color(60, 65, 78), new Color(50, 55, 68), Color.WHITE);
            updateButtonStyle(btnRefrescar, new Color(60, 65, 78), new Color(50, 55, 68), Color.WHITE);
            updateButtonStyle(btnAgregar, new Color(60, 65, 78), new Color(50, 55, 68), Color.WHITE);
            updateButtonStyle(btnEditar, new Color(60, 65, 78), new Color(50, 55, 68), Color.WHITE);
            updateButtonStyle(btnEliminar, new Color(60, 65, 78), new Color(50, 55, 68), Color.WHITE);
            updateButtonStyle(btnSendMsg, new Color(60, 65, 78), new Color(50, 55, 68), Color.WHITE);
            
        } else {
            // Modo claro
            Color bg = new Color(250, 250, 252);
            Color panel = new Color(255, 255, 255);
            Color header = new Color(80, 90, 140);
            Color text = new Color(45, 45, 45);
            Color inputBg = new Color(255, 255, 255);
            Color border = new Color(220, 220, 220);
            Color tableHeader = new Color(245, 245, 250);

            getContentPane().setBackground(bg);
            
            // Header
            headerPanel.setBackground(header);
            lblTitulo.setForeground(Color.WHITE);
            
            // Panel derecho
            headerRight.setBackground(panel);
            btnSettings.setBackground(new Color(247, 248, 250));
            btnSettings.setForeground(new Color(45, 45, 45));
            
            // Panel de búsqueda
            searchPanel.setBackground(panel);
            lblBuscar.setForeground(text);
            cbFiltro.setBackground(inputBg);
            cbFiltro.setForeground(text);
            txtBuscar.setBackground(inputBg);
            txtBuscar.setForeground(text);
            txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(border),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)
            ));
            
            // Tabla
            scrollPane.getViewport().setBackground(panel);
            JTableHeader th = tableEstudiantes.getTableHeader();
            th.setBackground(tableHeader);
            th.setForeground(new Color(70, 70, 70));
            tableEstudiantes.setBackground(Color.WHITE);
            tableEstudiantes.setForeground(new Color(40, 40, 40));
            tableEstudiantes.setSelectionBackground(new Color(200, 220, 255));
            tableEstudiantes.setGridColor(new Color(240, 240, 240));
            
            // Panel de grupos
            grupoPanel.setBackground(panel);
            lblGrupo.setForeground(text);
            cbGrupos.setBackground(inputBg);
            cbGrupos.setForeground(text);
            
            // Panel de acciones
            actionPanel.setBackground(panel);
            
            // Actualizar estilo de botones
            updateButtonStyle(btnGestionar, new Color(245, 245, 245), new Color(230, 230, 230), new Color(48, 48, 48));
            updateButtonStyle(btnRefrescar, new Color(245, 245, 245), new Color(230, 230, 230), new Color(48, 48, 48));
            updateButtonStyle(btnAgregar, new Color(245, 245, 245), new Color(230, 230, 230), new Color(48, 48, 48));
            updateButtonStyle(btnEditar, new Color(245, 245, 245), new Color(230, 230, 230), new Color(48, 48, 48));
            updateButtonStyle(btnEliminar, new Color(245, 245, 245), new Color(230, 230, 230), new Color(48, 48, 48));
            updateButtonStyle(btnSendMsg, new Color(245, 245, 245), new Color(230, 230, 230), new Color(48, 48, 48));
        }
        
        repaint();
    }

    private void updateButtonStyle(RoundedButton btn, Color bg, Color hoverBg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
       
        for (MouseListener ml : btn.getMouseListeners()) {
            if (ml instanceof MouseAdapter) {
                btn.removeMouseListener(ml);
            }
        }
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverBg);
                if (isDark(hoverBg))
                    btn.setForeground(Color.WHITE);
                else
                    btn.setForeground(new Color(30, 30, 30));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
                btn.setForeground(fg);
            }
        });
    }
}