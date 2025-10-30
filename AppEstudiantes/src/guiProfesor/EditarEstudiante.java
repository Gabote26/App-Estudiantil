package guiProfesor;

import javax.swing.*;
import db.ConexionMysql;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class EditarEstudiante extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextField txtNombre;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private final ConexionMysql connectionDB = new ConexionMysql();

    private int idEstudiante;
    private MainForTeachers parentFrame;
    private JTextField txtApellido;

    public EditarEstudiante(MainForTeachers parent, int id, String nombre, String apellido, String email) {
        this.parentFrame = parent;
        this.idEstudiante = id;

        setTitle("Editar Estudiante");
        setSize(400, 364);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);

        JLabel lblTitulo = new JLabel("Editar Datos del Estudiante");
        lblTitulo.setFont(new Font("Roboto", Font.BOLD, 18));
        lblTitulo.setBounds(80, 20, 260, 25);
        getContentPane().add(lblTitulo);

        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(50, 70, 100, 20);
        getContentPane().add(lblNombre);
        
        JLabel lblApellido = new JLabel("Apellido:");
        lblApellido.setBounds(50, 120, 100, 20);
        getContentPane().add(lblApellido);
        
        txtApellido = new JTextField(apellido);
        txtApellido.setBounds(150, 120, 180, 25);
        getContentPane().add(txtApellido);

        txtNombre = new JTextField(nombre);
        txtNombre.setBounds(150, 70, 180, 25);
        getContentPane().add(txtNombre);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(50, 170, 100, 20);
        getContentPane().add(lblEmail);

        txtEmail = new JTextField(email);
        txtEmail.setBounds(150, 170, 180, 25);
        getContentPane().add(txtEmail);

        JLabel lblPassword = new JLabel("Nueva Contraseña:");
        lblPassword.setBounds(50, 220, 120, 20);
        getContentPane().add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(180, 220, 150, 25);
        getContentPane().add(txtPassword);

        JButton btnGuardar = new JButton("💾 Guardar Cambios");
        btnGuardar.setBounds(110, 270, 180, 30);
        getContentPane().add(btnGuardar);

        // Acción del botón guardar
        btnGuardar.addActionListener(e -> guardarCambios());
    }

    /**
     * Actualiza los datos del estudiante en la base de datos
     */
    private void guardarCambios() {
        String nuevoNombre = txtNombre.getText().trim();
        String nuevoApellido = txtApellido.getText().trim();
        String nuevoEmail = txtEmail.getText().trim();
        String nuevaPassword = new String(txtPassword.getPassword()).trim();

        if (nuevoNombre.isEmpty() || nuevoApellido.isEmpty() || nuevoEmail.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos obligatorios.");
            return;
        }

        String query;
        if (!nuevaPassword.isEmpty()) {
            query = "UPDATE usuarios SET nombre = ?, apellido = ?, email = ?, password = ? WHERE id = ?";
        } else {
            query = "UPDATE usuarios SET nombre = ?, apellido = ?, email = ? WHERE id = ?";
        }

        try (Connection cn = connectionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(query)) {

            ps.setString(1, nuevoNombre);
            ps.setString(2, nuevoApellido);
            ps.setString(3, nuevoEmail);

            if (!nuevaPassword.isEmpty()) {
                ps.setString(4, nuevaPassword);
                ps.setInt(5, idEstudiante);
            } else {
                ps.setInt(4, idEstudiante);
            }

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "✅ Datos actualizados correctamente.");
                parentFrame.cargarEstudiantes(); // refresca la tabla del JFrame principal
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar el estudiante.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar estudiante:\n" + e.getMessage(),
                    "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }
}
