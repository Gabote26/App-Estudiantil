package guiProfesor;

import javax.swing.*;
import db.ConexionMysql;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AgregarEstudiante extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextField txtNombre;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private final ConexionMysql connectionDB = new ConexionMysql();
    private MainForTeachers parentFrame;
    private JTextField txtApellido;
    private JTextField txtNumControl;

    public AgregarEstudiante(MainForTeachers parent) {
        this.parentFrame = parent;

        setTitle("Agregar Nuevo Estudiante");
        setSize(400, 410);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);

        JLabel lblTitulo = new JLabel("Registrar Nuevo Estudiante");
        lblTitulo.setFont(new Font("Roboto", Font.BOLD, 18));
        lblTitulo.setBounds(80, 20, 260, 25);
        getContentPane().add(lblTitulo);

        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(50, 70, 100, 20);
        getContentPane().add(lblNombre);

        txtNombre = new JTextField();
        txtNombre.setBounds(150, 70, 180, 25);
        getContentPane().add(txtNombre);
        
        JLabel lblApellido = new JLabel("Apellido:");
        lblApellido.setBounds(50, 120, 100, 20);
        getContentPane().add(lblApellido);
        
        txtApellido = new JTextField();
        txtApellido.setBounds(150, 120, 180, 25);
        getContentPane().add(txtApellido);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(50, 170, 100, 20);
        getContentPane().add(lblEmail);

        txtEmail = new JTextField();
        txtEmail.setBounds(150, 170, 180, 25);
        getContentPane().add(txtEmail);

        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setBounds(50, 220, 100, 20);
        getContentPane().add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(150, 220, 180, 25);
        getContentPane().add(txtPassword);

        JButton btnGuardar = new JButton("💾 Guardar Estudiante");
        btnGuardar.setBounds(110, 320, 180, 30);
        getContentPane().add(btnGuardar);
        
        JLabel lblNumControl = new JLabel("Num. de Control");
        lblNumControl.setBounds(50, 270, 100, 20);
        getContentPane().add(lblNumControl);
        
        txtNumControl = new JTextField();
        txtNumControl.setBounds(150, 270, 180, 25);
        getContentPane().add(txtNumControl);
        btnGuardar.addActionListener(e -> guardarNuevoEstudiante());
    }

    /**
     * Inserta un nuevo estudiante en la base de datos
     */
    private void guardarNuevoEstudiante() {
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String numControl = txtNumControl.getText().trim();

        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || password.isEmpty() || numControl.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos.");
            return;
        }

        String query = "INSERT INTO usuarios (nombre, apellido, email, password, role, no_control) VALUES (?, ?, ?, ?, 'ESTUDIANTE', ?)";

        try (Connection cn = connectionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(query)) {
        	

            ps.setString(1, nombre);
            ps.setString(2, apellido);
            ps.setString(3, email);
            ps.setString(4, password);
            ps.setString(5, numControl);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "✅ Estudiante agregado correctamente.");
                parentFrame.cargarEstudiantes();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo registrar el estudiante.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al agregar estudiante:\n" + e.getMessage(),
                    "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }
}
