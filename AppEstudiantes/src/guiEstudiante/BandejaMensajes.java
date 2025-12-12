package guiEstudiante;

import mensajes.MensajesBase;
import modelos.Mensaje;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BandejaMensajes extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTable tableMensajes;
    private DefaultTableModel model;
    private final int usuarioId;
    private final MensajesBase mensajesDAO = new MensajesBase();
    private JLabel lblNoLeidos;
    private List<Mensaje> mensajesActuales;

    public BandejaMensajes(int usuarioId) {
        this.usuarioId = usuarioId;

        setTitle("📬 Mi Bandeja de Entrada");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        // -------- Header ---------
        
        JPanel headerPanel = new JPanel(null);
        headerPanel.setBounds(0, 0, 900, 80);
        headerPanel.setBackground(new Color(52, 73, 94));
        getContentPane().add(headerPanel);

        JLabel lblTitulo = new JLabel("📬 Mis Mensajes");
        lblTitulo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 26));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBounds(30, 20, 300, 40);
        headerPanel.add(lblTitulo);

        lblNoLeidos = new JLabel();
        lblNoLeidos.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        lblNoLeidos.setForeground(new Color(255, 215, 0));
        lblNoLeidos.setBounds(30, 60, 300, 20);
        headerPanel.add(lblNoLeidos);

        JButton btnRefrescar = new JButton("🔄 Actualizar");
        btnRefrescar.setBounds(750, 25, 120, 35);
        btnRefrescar.setFocusPainted(false);
        btnRefrescar.addActionListener(e -> cargarMensajes());
        headerPanel.add(btnRefrescar);

        // ------- Filtros --------
        
        JPanel panelFiltros = new JPanel(null);
        panelFiltros.setBounds(20, 100, 840, 50);
        panelFiltros.setBackground(new Color(245, 245, 250));
        getContentPane().add(panelFiltros);

        JLabel lblFiltro = new JLabel("Filtrar por tipo:");
        lblFiltro.setBounds(20, 10, 100, 30);
        panelFiltros.add(lblFiltro);

        JComboBox<String> cbFiltro = new JComboBox<>(new String[]{
            "Todos", "Anuncios", "Calificaciones", "Asistencias"
        });
        cbFiltro.setBounds(130, 10, 150, 30);
        cbFiltro.addActionListener(e -> aplicarFiltro(cbFiltro.getSelectedItem().toString()));
        panelFiltros.add(cbFiltro);

        // ------- Tabla con todos los mensajes disponibles --------

        model = new DefaultTableModel(
            new Object[]{"ID", "Tipo", "Asunto", "Fecha", "Estado"}, 
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableMensajes = new JTable(model);
        tableMensajes.setRowHeight(35);
        tableMensajes.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        tableMensajes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableMensajes.getColumnModel().getColumn(0).setMinWidth(0);
        tableMensajes.getColumnModel().getColumn(0).setMaxWidth(0);
        tableMensajes.getColumnModel().getColumn(0).setWidth(0);

        // Doble clic para abrir el mensaje
        tableMensajes.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    abrirMensaje();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tableMensajes);
        scrollPane.setBounds(20, 170, 840, 330);
        getContentPane().add(scrollPane);

        // Abrir el mensaje seleccionado
        JButton btnAbrir = new JButton("📖 Abrir Mensaje");
        btnAbrir.setBounds(350, 520, 180, 35);
        btnAbrir.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        btnAbrir.setBackground(new Color(52, 152, 219));
        btnAbrir.setForeground(Color.WHITE);
        btnAbrir.setFocusPainted(false);
        btnAbrir.addActionListener(e -> abrirMensaje());
        getContentPane().add(btnAbrir);

        cargarMensajes();
    }

    // Cargar todos los mensajes disponibles a los que tiene acceso un usuario
    private void cargarMensajes() {
        model.setRowCount(0);
        mensajesActuales = mensajesDAO.obtenerMensajesUsuario(usuarioId);

        if (mensajesActuales.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No tienes mensajes aún",
                "Bandeja vacía",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Mensaje m : mensajesActuales) {
            String tipoDisplay = formatearTipo(m.getTipoMensaje());
            String fechaDisplay = m.getFechaEnvio().format(formatter);
            String estadoDisplay = "📧 Nuevo"; // Por defecto (Hacer que se guarde el estado en los getters y setters para actualizarlo cuando se haya abierto ya un mensaje

            model.addRow(new Object[]{
                m.getId(),
                tipoDisplay,
                m.getAsunto(),
                fechaDisplay,
                estadoDisplay
            });
        }

        actualizarContadorNoLeidos();
    }

    private void aplicarFiltro(String filtro) {
        if (mensajesActuales == null) return;

        model.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Mensaje m : mensajesActuales) {
            boolean incluir = false;

            switch (filtro) {
                case "Todos":
                    incluir = true;
                    break;
                case "Anuncios":
                    incluir = m.getTipoMensaje().equals("anuncio");
                    break;
                case "Calificaciones":
                    incluir = m.getTipoMensaje().equals("calificacion");
                    break;
                case "Asistencias":
                    incluir = m.getTipoMensaje().equals("asistencia");
                    break;
            }

            if (incluir) {
                String tipoDisplay = formatearTipo(m.getTipoMensaje());
                String fechaDisplay = m.getFechaEnvio().format(formatter);

                model.addRow(new Object[]{
                    m.getId(),
                    tipoDisplay,
                    m.getAsunto(),
                    fechaDisplay,
                    "📧 Nuevo"
                });
            }
        }
    }

    private void abrirMensaje() {
        int selectedRow = tableMensajes.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un mensaje primero");
            return;
        }

        int mensajeId = (int) model.getValueAt(selectedRow, 0);
        Mensaje mensaje = mensajesActuales.stream()
            .filter(m -> m.getId() == mensajeId)
            .findFirst()
            .orElse(null);

        if (mensaje == null) return;

        // Marcar como leído
        mensajesDAO.marcarComoLeido(mensajeId, usuarioId);

        // Mostrar ventana con el contenido
        new VentanaMensaje(mensaje).setVisible(true);

        // Actualizar estado en la tabla
        model.setValueAt("✅ Leído", selectedRow, 4);
        actualizarContadorNoLeidos();
    }

    private void actualizarContadorNoLeidos() {
        int noLeidos = mensajesDAO.contarMensajesNoLeidos(usuarioId);
        lblNoLeidos.setText("📬 Tienes " + noLeidos + " mensaje(s) sin leer");
    }

    private String formatearTipo(String tipo) {
        return switch (tipo) {
            case "anuncio" -> "📣 Anuncio";
            case "calificacion" -> "📝 Calificación";
            case "asistencia" -> "📋 Asistencia";
            default -> tipo;
        };
    }

    // ---------- Ventana externa para mostrar el contenido entero del mensaje -----------
    
    class VentanaMensaje extends JFrame {

		private static final long serialVersionUID = 1L;

		public VentanaMensaje(Mensaje mensaje) {
            setTitle(mensaje.getAsunto());
            setSize(600, 500);
            setLocationRelativeTo(BandejaMensajes.this);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            getContentPane().setLayout(null);
            getContentPane().setBackground(Color.WHITE);

            // Tipo de mensaje
            JLabel lblTipo = new JLabel(formatearTipo(mensaje.getTipoMensaje()));
            lblTipo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
            lblTipo.setBounds(30, 20, 300, 30);
            getContentPane().add(lblTipo);

            // Fecha
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            JLabel lblFecha = new JLabel("📅 " + mensaje.getFechaEnvio().format(formatter));
            lblFecha.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
            lblFecha.setForeground(Color.GRAY);
            lblFecha.setBounds(30, 55, 300, 20);
            getContentPane().add(lblFecha);

            // Asunto
            JLabel lblAsunto = new JLabel("Asunto: " + mensaje.getAsunto());
            lblAsunto.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
            lblAsunto.setBounds(30, 90, 520, 25);
            getContentPane().add(lblAsunto);

            // Separador
            JSeparator sep = new JSeparator();
            sep.setBounds(30, 125, 520, 2);
            getContentPane().add(sep);

            // Contenido del mensaje
            JTextArea txtContenido = new JTextArea(mensaje.getContenido());
            txtContenido.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
            txtContenido.setLineWrap(true);
            txtContenido.setWrapStyleWord(true);
            txtContenido.setEditable(false);
            txtContenido.setBackground(Color.WHITE);
            
            JScrollPane scroll = new JScrollPane(txtContenido);
            scroll.setBounds(30, 140, 520, 260);
            scroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            getContentPane().add(scroll);

            // Botón para cerrar la ventana
            JButton btnCerrar = new JButton("🔓 Cerrar");
            btnCerrar.setBounds(240, 420, 100, 30);
            btnCerrar.addActionListener(e -> dispose());
            getContentPane().add(btnCerrar);
        }
    }
}