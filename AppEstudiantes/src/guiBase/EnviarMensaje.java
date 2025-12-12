package guiBase;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import db.ConexionMysql;
import mensajes.MensajesBase;
import modelos.Mensaje;
import java.sql.*;

public class EnviarMensaje extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtSearch;
    private JTextArea msgContent;
    private JTextField txtAsunto;
    private JComboBox<String> selectType;
    private JComboBox<String> selectGroups;
    private JRadioButton destOptUsers, destOptGroups;
    private JList<String> listDestinatarios;
    private DefaultListModel<String> modelDestinatarios;
    private JTextPane resultMsg, resultMsgType;
    private JList<String> listResultDest;
    private DefaultListModel<String> modelResultDest;
    
    private final ConexionMysql connectionDB = new ConexionMysql();
    private final MensajesBase mensajesDAO = new MensajesBase();
    private final int remitenteId; // ID del profesor/admin que envía el mensaje
    private List<Integer> destinatariosSeleccionados = new ArrayList<>();

    public EnviarMensaje(int remitenteId) {
        this.remitenteId = remitenteId;
        initComponents();
        cargarGrupos();
    }

    private void initComponents() {
        setType(Type.UTILITY);
        setTitle("📣 Enviar Mensaje");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        contentPane.setBackground(Color.WHITE);

        // -------- Formulario de las caracteristicas del mensaje --------
        
        JLabel titleSendMsg = new JLabel("📣 Enviar Mensaje");
        titleSendMsg.setBounds(50, 10, 250, 35);
        titleSendMsg.setFont(new Font("Segoe UI Emoji", Font.BOLD, 24));
        contentPane.add(titleSendMsg);

        // Tipo de mensaje
        JLabel lblMsg = new JLabel("Tipo de mensaje:");
        lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblMsg.setBounds(30, 60, 150, 25);
        contentPane.add(lblMsg);

        selectType = new JComboBox<>(new String[]{"Anuncio General", "Notificación de Calificación", "Aviso de Asistencia"});
        selectType.setBounds(180, 60, 230, 30);
        selectType.addActionListener(e -> actualizarVistaPrevia());
        contentPane.add(selectType);

        // Asunto
        JLabel lblAsunto = new JLabel("Asunto:");
        lblAsunto.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblAsunto.setBounds(30, 105, 150, 25);
        contentPane.add(lblAsunto);

        txtAsunto = new JTextField();
        txtAsunto.setBounds(180, 105, 280, 30);
        txtAsunto.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { actualizarVistaPrevia(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { actualizarVistaPrevia(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { actualizarVistaPrevia(); }
        });
        contentPane.add(txtAsunto);

        // Destinatarios
        JLabel lblDest = new JLabel("Destinatarios:");
        lblDest.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDest.setBounds(30, 150, 150, 25);
        contentPane.add(lblDest);

        destOptGroups = new JRadioButton("Por grupos");
        destOptGroups.setBounds(40, 180, 120, 25);
        destOptGroups.setBackground(Color.WHITE);
        destOptGroups.setSelected(true);
        contentPane.add(destOptGroups);

        selectGroups = new JComboBox<>();
        selectGroups.setBounds(160, 180, 200, 30);
        selectGroups.addActionListener(e -> cargarEstudiantesGrupo());
        contentPane.add(selectGroups);

        JButton btnAgregarGrupo = new JButton("➕");
        btnAgregarGrupo.setBounds(370, 180, 50, 30);
        btnAgregarGrupo.addActionListener(e -> agregarGrupoCompleto());
        contentPane.add(btnAgregarGrupo);

        destOptUsers = new JRadioButton("Usuarios específicos");
        destOptUsers.setBounds(40, 220, 160, 25);
        destOptUsers.setBackground(Color.WHITE);
        contentPane.add(destOptUsers);

        ButtonGroup bgDest = new ButtonGroup();
        bgDest.add(destOptGroups);
        bgDest.add(destOptUsers);

        txtSearch = new JTextField();
        txtSearch.setToolTipText("Buscar por nombre o correo");
        txtSearch.setBounds(210, 220, 150, 30);
        contentPane.add(txtSearch);

        JButton btnBuscar = new JButton("🔍");
        btnBuscar.setBounds(370, 220, 50, 30);
        btnBuscar.addActionListener(e -> buscarEstudiantes());
        contentPane.add(btnBuscar);

        // Lista de destinatarios que han sido seleccionados
        JLabel lblSeleccionados = new JLabel("Seleccionados:");
        lblSeleccionados.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSeleccionados.setBounds(30, 260, 150, 20);
        contentPane.add(lblSeleccionados);

        modelDestinatarios = new DefaultListModel<>();
        listDestinatarios = new JList<>(modelDestinatarios);
        JScrollPane scrollDest = new JScrollPane(listDestinatarios);
        scrollDest.setBounds(30, 285, 390, 100);
        contentPane.add(scrollDest);

        JButton btnLimpiarDest = new JButton("🗑️ Limpiar");
        btnLimpiarDest.setBounds(270, 390, 150, 25);
        btnLimpiarDest.addActionListener(e -> limpiarDestinatarios());
        contentPane.add(btnLimpiarDest);

        // Contenido del mensaje
        JLabel lblContent = new JLabel("Contenido del mensaje:");
        lblContent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblContent.setBounds(30, 425, 200, 25);
        contentPane.add(lblContent);

        msgContent = new JTextArea();
        msgContent.setFont(new Font("Calibri", Font.PLAIN, 13));
        msgContent.setLineWrap(true);
        msgContent.setWrapStyleWord(true);
        msgContent.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        msgContent.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { actualizarVistaPrevia(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { actualizarVistaPrevia(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { actualizarVistaPrevia(); }
        });
        JScrollPane scrollMsg = new JScrollPane(msgContent);
        scrollMsg.setBounds(30, 455, 390, 80);
        contentPane.add(scrollMsg);

        // Botón enviar
        JButton btnEnviar = new JButton("📤 Enviar Mensaje");
        btnEnviar.setBounds(210, 545, 210, 35);
        btnEnviar.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        btnEnviar.setBackground(new Color(40, 180, 99));
        btnEnviar.setForeground(Color.WHITE);
        btnEnviar.setFocusPainted(false);
        btnEnviar.addActionListener(e -> enviarMensaje());
        contentPane.add(btnEnviar);

        // ------------ Vista previa del mensaje -----------
        
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setBounds(480, 10, 2, 570);
        separator.setBackground(Color.GRAY);
        contentPane.add(separator);

        JLabel titlePreview = new JLabel("👀 Vista Previa");
        titlePreview.setFont(new Font("Segoe UI Emoji", Font.BOLD, 22));
        titlePreview.setBounds(550, 10, 200, 35);
        contentPane.add(titlePreview);

        JLabel lblPrevTipo = new JLabel("Tipo:");
        lblPrevTipo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPrevTipo.setBounds(510, 60, 80, 25);
        contentPane.add(lblPrevTipo);

        resultMsgType = new JTextPane();
        resultMsgType.setEditable(false);
        resultMsgType.setBounds(510, 90, 450, 30);
        resultMsgType.setBackground(new Color(245, 245, 245));
        contentPane.add(resultMsgType);

        JLabel lblPrevDest = new JLabel("Destinatarios:");
        lblPrevDest.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPrevDest.setBounds(510, 135, 100, 25);
        contentPane.add(lblPrevDest);

        modelResultDest = new DefaultListModel<>();
        listResultDest = new JList<>(modelResultDest);
        JScrollPane scrollResultDest = new JScrollPane(listResultDest);
        scrollResultDest.setBounds(510, 165, 450, 100);
        contentPane.add(scrollResultDest);

        JLabel lblPrevMsg = new JLabel("Mensaje:");
        lblPrevMsg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPrevMsg.setBounds(510, 280, 100, 25);
        contentPane.add(lblPrevMsg);

        resultMsg = new JTextPane();
        resultMsg.setEditable(false);
        resultMsg.setBackground(new Color(245, 245, 245));
        JScrollPane scrollResultMsg = new JScrollPane(resultMsg);
        scrollResultMsg.setBounds(510, 310, 450, 225);
        contentPane.add(scrollResultMsg);
    }

    // Cargar los grupos disponibles
    private void cargarGrupos() {
        selectGroups.removeAllItems();
        String sql = "SELECT id, nombre_grupo FROM grupos ORDER BY nombre_grupo";
        
        try (Connection cn = connectionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                selectGroups.addItem(rs.getInt("id") + " - " + rs.getString("nombre_grupo"));
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar grupos: " + e.getMessage());
        }
    }

    // Cargar a los estudiantes del grupo seleccionado
    private void cargarEstudiantesGrupo() {
        actualizarVistaPrevia();
    }

    // Agregar un grupo entero 
    private void agregarGrupoCompleto() {
        if (selectGroups.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un grupo primero");
            return;
        }

        String seleccion = selectGroups.getSelectedItem().toString();
        int grupoId = Integer.parseInt(seleccion.split(" - ")[0]);
        
        List<Integer> estudiantesIds = mensajesDAO.obtenerEstudiantesDeGrupo(grupoId);
        
        if (estudiantesIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay estudiantes en este grupo");
            return;
        }

        // Agregar a la lista
        for (int id : estudiantesIds) {
            if (!destinatariosSeleccionados.contains(id)) {
                destinatariosSeleccionados.add(id);
            }
        }

        actualizarListaDestinatarios();
        JOptionPane.showMessageDialog(this, "✅ Grupo agregado: " + estudiantesIds.size() + " estudiantes");
    }

    private void buscarEstudiantes() {
        String texto = txtSearch.getText().trim();
        
        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Escribe un nombre o correo para buscar");
            return;
        }

        List<Integer> ids = mensajesDAO.buscarEstudiantes(texto);
        
        if (ids.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se encontraron estudiantes");
            return;
        }

        // Mostrar resultados para la selección
        String[] opciones = new String[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            opciones[i] = obtenerNombreEstudiante(ids.get(i));
        }

        JList<String> list = new JList<>(opciones);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        int result = JOptionPane.showConfirmDialog(
            this,
            new JScrollPane(list),
            "Selecciona estudiantes",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            int[] indices = list.getSelectedIndices();
            for (int index : indices) {
                int id = ids.get(index);
                if (!destinatariosSeleccionados.contains(id)) {
                    destinatariosSeleccionados.add(id);
                }
            }
            actualizarListaDestinatarios();
        }
    }

    // Obtener el nombre del usuario al que se enviara el mensaje
    private String obtenerNombreEstudiante(int id) {
        String sql = "SELECT nombre, apellido, email FROM usuarios WHERE id = ?";
        
        try (Connection cn = connectionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getString("nombre") + " " + rs.getString("apellido") + 
                       " (" + rs.getString("email") + ")";
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener nombre: " + e.getMessage());
        }
        
        return "ID: " + id;
    }

    // Actualizar la lista de destinatarios
    private void actualizarListaDestinatarios() {
        modelDestinatarios.clear();
        modelResultDest.clear();
        
        for (int id : destinatariosSeleccionados) {
            String nombre = obtenerNombreEstudiante(id);
            modelDestinatarios.addElement(nombre);
            modelResultDest.addElement(nombre);
        }
        
        actualizarVistaPrevia();
    }

    // Borrar la lista de destinatarios
    private void limpiarDestinatarios() {
        destinatariosSeleccionados.clear();
        modelDestinatarios.clear();
        modelResultDest.clear();
        actualizarVistaPrevia();
    }

    // Actualizar la vista previa del mensaje
    private void actualizarVistaPrevia() {
        // Tipo de mensaje
        String tipo = selectType.getSelectedItem().toString();
        resultMsgType.setText(tipo);

        // Contenido
        String asunto = txtAsunto.getText();
        String contenido = msgContent.getText();
        resultMsg.setText("Asunto: " + asunto + "\n\n" + contenido);
    }

    // Metodo para enviar el mensaje con todas las caracteristicas
    private void enviarMensaje() {
        // Validaciones para evitar datos sin rellenar
        if (txtAsunto.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠️ Escribe un asunto");
            return;
        }

        if (msgContent.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠️ Escribe el contenido del mensaje");
            return;
        }

        if (destinatariosSeleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠️ Selecciona al menos un destinatario");
            return;
        }

        // Establecer el tipo de mensaje
        String tipoSeleccionado = selectType.getSelectedItem().toString();
        String tipoMensaje;
        
        if (tipoSeleccionado.contains("Calificación")) {
            tipoMensaje = "calificacion";
        } else if (tipoSeleccionado.contains("Asistencia")) {
            tipoMensaje = "asistencia";
        } else {
            tipoMensaje = "anuncio";
        }

        // Crear el mensaje
        Mensaje mensaje = new Mensaje(
            remitenteId,
            tipoMensaje,
            txtAsunto.getText().trim(),
            msgContent.getText().trim()
        );

        // Confirmar que el mesnsaje haya sido enviado
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "¿Enviar mensaje a " + destinatariosSeleccionados.size() + " estudiante(s)?",
            "Confirmar envío",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Enviar el mensaje
        int mensajeId = mensajesDAO.enviarMensaje(mensaje, destinatariosSeleccionados);

        if (mensajeId > 0) {
            JOptionPane.showMessageDialog(
                this,
                "✅ Mensaje enviado correctamente a " + destinatariosSeleccionados.size() + " estudiante(s)",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE
            );
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(
                this,
                "❌ Error al enviar el mensaje. Intenta de nuevo.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void limpiarFormulario() {
        txtAsunto.setText("");
        msgContent.setText("");
        txtSearch.setText("");
        limpiarDestinatarios();
        selectType.setSelectedIndex(0);
    }
}