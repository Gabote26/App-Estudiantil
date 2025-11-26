package main;

import db.ConexionMysql;
import guiAdmin.MainForAdmin;
import guiEstudiante.ProgramMain;
import guiProfesor.MainForTeachers;
import utils.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;

public class LoginSystem extends JFrame {

    private JPanel container;

    private MaterialTextField userField;
    private MaterialPasswordField passField;

    private final ConexionMysql connectionDB = new ConexionMysql();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                MaterialSplash splash = new MaterialSplash();
                splash.setVisible(true);
                Thread.sleep(1200);
                splash.dispose();

                LoginSystem frame = new LoginSystem();
                frame.setUndecorated(true);
                frame.setLocationRelativeTo(null);
                FadeUtils.fadeIn(frame, 300);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public LoginSystem() {
        setTitle("Iniciar Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setSize(480, 460);
        setLocationRelativeTo(null);

        try {
            setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 25, 25));
        } catch (Exception ex) {
        	JOptionPane.showMessageDialog(null, "ADVERTENCIA: La forma de la ventana no es compatible: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	        return;
        }

        container = new JPanel();
        container.setBackground(new Color(30, 30, 35));
        container.setLayout(null);
        container.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(container);

        // ---------- TITULO ----------
        JLabel title = new JLabel("INICIAR SESIÓN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBounds(140, 35, 250, 35);
        container.add(title);

        JLabel subtitle = new JLabel("Ingresa tu usuario y contraseña");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(new Color(180, 180, 180));
        subtitle.setBounds(130, 70, 280, 25);
        container.add(subtitle);

        // ---------- ICONOS ----------
        Icon userIcon = new ImageIcon("resources/icons/user.png");
        Icon lockIcon = new ImageIcon("resources/icons/lock.png");
        Icon eyeOn  = new ImageIcon("resources/icons/eye.png");
        Icon eyeOff = new ImageIcon("resources/icons/eye_off.png");

        // ---------- CAMPOS ----------
        userField = new MaterialTextField("Usuario (No. de Control)", userIcon);
        userField.setBounds(100, 120, 280, 60);
        container.add(userField);

        passField = new MaterialPasswordField("Contraseña", lockIcon, eyeOn, eyeOff);
        passField.setBounds(100, 200, 280, 60);
        container.add(passField);

        // ---------- BOTÓN LOGIN ----------
        RoundedButton loginBtn = new RoundedButton("Ingresar", 22);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setBackground(new Color(25, 118, 210));
        loginBtn.setBounds(160, 285, 160, 45);
        container.add(loginBtn);

        loginBtn.addActionListener(e -> iniciarSesion());

        // ---------- CERRAR VENTANA ----------
        JButton closeBtn = new JButton("X");
        closeBtn.setBounds(430, 8, 40, 30);
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setBackground(new Color(153, 61, 61));
        closeBtn.setBorder(null);
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> System.exit(0));
        container.add(closeBtn);

        addDragListener(container);
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

    private void iniciarSesion() {

        String user = userField.getText();
        String password = passField.getText();

        if (user.isBlank() || password.isBlank()) {
            JOptionPane.showMessageDialog(this, "Debe completar todos los campos");
            return;
        }

        MaterialLoader loader = new MaterialLoader(this);

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {

            String nombre, apellido, role;
            long numControl;

            @Override
            protected Boolean doInBackground() throws Exception {
                Connection cn = connectionDB.conectar();
                if (cn == null) return false;

                String query = """
                    SELECT nombre, apellido, role, no_control
                    FROM usuarios
                    WHERE email = ? AND password = ?
                """;

                try (PreparedStatement ps = cn.prepareStatement(query)) {
                    ps.setString(1, user);
                    ps.setString(2, password);

                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        nombre = rs.getString("nombre");
                        apellido = rs.getString("apellido");
                        role = rs.getString("role");
                        numControl = rs.getLong("no_control");
                        return true;
                    }
                }
                return false;
            }

            @Override
            protected void done() {
                loader.dispose();

                boolean ok = false;
                try { ok = get(); } catch (Exception ignored) {}

                if (!ok) {
                    JOptionPane.showMessageDialog(LoginSystem.this, "Usuario o contraseña incorrectos");
                    return;
                }

                FadeUtils.fadeOut(LoginSystem.this, 300, () -> {
                    dispose();
                    JFrame next = switch (role.toUpperCase()) {
                        case "ADMIN" -> new MainForAdmin(nombre);
                        case "PROFESOR" -> new MainForTeachers(nombre);
                        case "ESTUDIANTE" -> new ProgramMain(numControl, nombre, apellido);
                        default -> null;
                    };

                    if (next != null) {
                        next.setUndecorated(true);
                        next.setLocationRelativeTo(null);
                        FadeUtils.fadeIn(next, 300);
                    }
                });
            }
        };

        worker.execute();
        loader.setVisible(true);
    }
}
