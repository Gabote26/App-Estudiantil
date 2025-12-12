package main;

import utils.ConfigManager;
import utils.ThemeManager;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Settings extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    private JComboBox<String> configTema;
    private JComboBox<String> configIdioma;
    private JCheckBox configNotificaciones;
    private JButton btnGuardar, btnCancelar, btnRestaurar;
    
    public Settings() {
        setType(Type.UTILITY);
        setTitle("⚙️ Configuración");
        setSize(480, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // ------- Título -------
        JLabel lblTitulo = new JLabel("Configuración de la Aplicación");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBorder(new EmptyBorder(0, 0, 15, 0));
        mainPanel.add(lblTitulo, BorderLayout.NORTH);
        
        // ------- Panel de configuracion -------
        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBackground(Color.WHITE);
        
        // Tema de la aplicacion
        GridBagConstraints gbc_lblTema = new GridBagConstraints();
        gbc_lblTema.insets = new Insets(8, 10, 8, 10);
        gbc_lblTema.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblTema.gridx = 0;
        gbc_lblTema.gridy = 0;
        gbc_lblTema.weightx = 0.4;
        JLabel lblTema = new JLabel("🎨 Tema de la aplicación:");
        lblTema.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        configPanel.add(lblTema, gbc_lblTema);
        
        GridBagConstraints gbc_configTema = new GridBagConstraints();
        gbc_configTema.insets = new Insets(8, 10, 8, 10);
        gbc_configTema.fill = GridBagConstraints.HORIZONTAL;
        gbc_configTema.gridx = 1;
        gbc_configTema.gridy = 0;
        gbc_configTema.weightx = 0.6;
        configTema = new JComboBox<>(new String[]{"Claro", "Oscuro", "Automático"});
        configTema.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        configTema.setSelectedItem(ConfigManager.getTema());
        configPanel.add(configTema, gbc_configTema);
        
        // Descripción del tema
        GridBagConstraints gbc_lblTemaDesc = new GridBagConstraints();
        gbc_lblTemaDesc.insets = new Insets(8, 10, 8, 10);
        gbc_lblTemaDesc.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblTemaDesc.gridx = 0;
        gbc_lblTemaDesc.gridy = 1;
        gbc_lblTemaDesc.gridwidth = 2;
        JLabel lblTemaDesc = new JLabel(
            "<html><i>· Automático: Sigue el tema de tu sistema operativo</i></html>"
        );
        lblTemaDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTemaDesc.setForeground(new Color(100, 100, 100));
        configPanel.add(lblTemaDesc, gbc_lblTemaDesc);
        
        // Separador 1
        GridBagConstraints gbc_sep1 = new GridBagConstraints();
        gbc_sep1.insets = new Insets(15, 10, 15, 10);
        gbc_sep1.fill = GridBagConstraints.HORIZONTAL;
        gbc_sep1.gridx = 0;
        gbc_sep1.gridy = 2;
        gbc_sep1.gridwidth = 2;
        JSeparator sep1 = new JSeparator();
        configPanel.add(sep1, gbc_sep1);
        
        // --- Idioma ---
        GridBagConstraints gbc_lblIdioma = new GridBagConstraints();
        gbc_lblIdioma.insets = new Insets(8, 10, 8, 10);
        gbc_lblIdioma.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblIdioma.gridx = 0;
        gbc_lblIdioma.gridy = 3;
        gbc_lblIdioma.weightx = 0.4;
        gbc_lblIdioma.gridwidth = 1;
        JLabel lblIdioma = new JLabel("🌐 Idioma:");
        lblIdioma.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        configPanel.add(lblIdioma, gbc_lblIdioma);
        
        GridBagConstraints gbc_configIdioma = new GridBagConstraints();
        gbc_configIdioma.insets = new Insets(8, 10, 8, 10);
        gbc_configIdioma.fill = GridBagConstraints.HORIZONTAL;
        gbc_configIdioma.gridx = 1;
        gbc_configIdioma.gridy = 3;
        gbc_configIdioma.weightx = 0.6;
        configIdioma = new JComboBox<>(new String[]{"Español", "Inglés"});
        configIdioma.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        configIdioma.setSelectedItem(ConfigManager.getIdioma());
        configIdioma.setEnabled(false); // Deshabilitado por falta de funcion
        configPanel.add(configIdioma, gbc_configIdioma);
        
        GridBagConstraints gbc_lblIdiomaDesc = new GridBagConstraints();
        gbc_lblIdiomaDesc.insets = new Insets(8, 10, 8, 10);
        gbc_lblIdiomaDesc.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblIdiomaDesc.gridx = 0;
        gbc_lblIdiomaDesc.gridy = 4;
        gbc_lblIdiomaDesc.gridwidth = 2;
        JLabel lblIdiomaDesc = new JLabel(
            "<html><i>· Funcionalidad en desarrollo</i></html>"
        );
        lblIdiomaDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblIdiomaDesc.setForeground(new Color(180, 100, 100));
        configPanel.add(lblIdiomaDesc, gbc_lblIdiomaDesc);
        
        // Separador 2
        GridBagConstraints gbc_sep2 = new GridBagConstraints();
        gbc_sep2.insets = new Insets(15, 10, 15, 10);
        gbc_sep2.fill = GridBagConstraints.HORIZONTAL;
        gbc_sep2.gridx = 0;
        gbc_sep2.gridy = 5;
        gbc_sep2.gridwidth = 2;
        JSeparator sep2 = new JSeparator();
        configPanel.add(sep2, gbc_sep2);
        
        // --- Notificaciones ---
        GridBagConstraints gbc_configNotificaciones = new GridBagConstraints();
        gbc_configNotificaciones.insets = new Insets(8, 10, 8, 10);
        gbc_configNotificaciones.fill = GridBagConstraints.HORIZONTAL;
        gbc_configNotificaciones.gridx = 0;
        gbc_configNotificaciones.gridy = 6;
        gbc_configNotificaciones.gridwidth = 2;
        configNotificaciones = new JCheckBox("🔔 Habilitar notificaciones");
        configNotificaciones.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        configNotificaciones.setBackground(Color.WHITE);
        configNotificaciones.setSelected(ConfigManager.getNotificaciones());
        configNotificaciones.setEnabled(false); // Deshabilitado por ahora
        configPanel.add(configNotificaciones, gbc_configNotificaciones);
        
        GridBagConstraints gbc_lblNotifDesc = new GridBagConstraints();
        gbc_lblNotifDesc.insets = new Insets(8, 10, 8, 10);
        gbc_lblNotifDesc.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblNotifDesc.gridx = 0;
        gbc_lblNotifDesc.gridy = 7;
        gbc_lblNotifDesc.gridwidth = 2;
        JLabel lblNotifDesc = new JLabel(
            "<html><i>· Funcionalidad en desarrollo</i></html>"
        );
        lblNotifDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblNotifDesc.setForeground(new Color(180, 100, 100));
        configPanel.add(lblNotifDesc, gbc_lblNotifDesc);
        
        // Agregar scroll pane para el panel de configuración y que el contenido entero pueda verse
        JScrollPane scrollPane = new JScrollPane(configPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // ------- Panel de botones -------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        btnGuardar = new JButton("💾 Guardar");
        btnGuardar.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
        btnGuardar.setPreferredSize(new Dimension(120, 35));
        btnGuardar.setBackground(new Color(70, 130, 180));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnCancelar = new JButton("❌ Cancelar");
        btnCancelar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        btnCancelar.setPreferredSize(new Dimension(120, 35));
        btnCancelar.setBackground(new Color(220, 220, 220));
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnRestaurar = new JButton("🔄 Restaurar");
        btnRestaurar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        btnRestaurar.setPreferredSize(new Dimension(120, 35));
        btnRestaurar.setBackground(new Color(255, 200, 100));
        btnRestaurar.setFocusPainted(false);
        btnRestaurar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRestaurar.setToolTipText("Restaurar valores por defecto");
        
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnRestaurar);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // ------- Listeners -------
        
        btnGuardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                guardarConfiguracion();
            }
        });
        
        btnCancelar.addActionListener(e -> dispose());
        
        btnRestaurar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de restaurar la configuración por defecto?",
                "Confirmar restauración",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                ConfigManager.resetConfig();
                cargarConfiguracion();
                JOptionPane.showMessageDialog(
                    this,
                    "✅ Configuración restaurada a valores por defecto",
                    "Restauración exitosa",
                    JOptionPane.INFORMATION_MESSAGE
                );
                // Aplicar tema a todas las ventanas
                ThemeManager.applyThemeToAll();
            }
        });
        
        addHoverEffect(btnGuardar, new Color(70, 130, 180), new Color(50, 110, 160));
        addHoverEffect(btnCancelar, new Color(220, 220, 220), new Color(200, 200, 200));
        addHoverEffect(btnRestaurar, new Color(255, 200, 100), new Color(235, 180, 80));
        
        getContentPane().add(mainPanel);
        setVisible(true);
    }

    // Cargar la configuracion existente
    private void cargarConfiguracion() {
        configTema.setSelectedItem(ConfigManager.getTema());
        configIdioma.setSelectedItem(ConfigManager.getIdioma());
        configNotificaciones.setSelected(ConfigManager.getNotificaciones());
    }

    // Guardar la configuracion ingresada
    private void guardarConfiguracion() {
        String tema = (String) configTema.getSelectedItem();
        String idioma = (String) configIdioma.getSelectedItem();
        boolean notificaciones = configNotificaciones.isSelected();

        ConfigManager.setTema(tema);
        ConfigManager.setIdioma(idioma);
        ConfigManager.setNotificaciones(notificaciones);
        
        // Aplicar tema a todas las ventanas abiertas
        ThemeManager.applyThemeToAll();

        String temaActual = ConfigManager.isDarkMode() ? "Oscuro" : "Claro";
        if (tema.equals("Automático")) {
            temaActual = "Automático (" + temaActual + ")";
        }
        
        // Comprobacion de los datos ingresados
        JOptionPane.showMessageDialog(
            this,
            "✅ Configuración guardada correctamente\n\n" +
            "Tema aplicado: " + temaActual + "\n" +
            "Idioma: " + idioma + " (en desarrollo)\n" +
            "Notificaciones: " + (notificaciones ? "Habilitadas" : "Deshabilitadas") + " (en desarrollo)",
            "Configuración guardada",
            JOptionPane.INFORMATION_MESSAGE
        );
        
        dispose();
    }
    
    private void addHoverEffect(JButton btn, Color normal, Color hover) {
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hover);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(normal);
            }
        });
    }
    
}