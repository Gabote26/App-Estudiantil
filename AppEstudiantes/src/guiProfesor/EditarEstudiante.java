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
    private JComboBox<String> cbGrupo;
    private int grupoActualId;
    private final ConexionMysql connectionDB = new ConexionMysql();

    private int idEstudiante;
    private MainForTeachers parentFrame;
    private JTextField txtApellido;

    public EditarEstudiante(MainForTeachers parent, int id, String nombre, String apellido, String email) {
        this.parentFrame = parent;
        this.idEstudiante = id;

        setTitle("Editar Estudiante");
        setSize(400, 404);
        setResizable(false);
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
        
        JLabel lblGrupo = new JLabel("Grupo:");
        lblGrupo.setBounds(50, 270, 100, 20);
        getContentPane().add(lblGrupo);

        cbGrupo = new JComboBox<>();
        cbGrupo.setBounds(150, 270, 180, 25);
        getContentPane().add(cbGrupo);
        cargarGrupos();

        JButton btnGuardar = new JButton("💾 Guardar Cambios");
        btnGuardar.setBounds(110, 320, 180, 30);
        getContentPane().add(btnGuardar);
        btnGuardar.addActionListener(e -> guardarCambios());
    }
    
    private void cargarGrupos() {
        cbGrupo.removeAllItems();
        cbGrupo.addItem("");
        String query = "SELECT id, nombre_grupo FROM grupos";

        try (Connection cn = connectionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                cbGrupo.addItem(rs.getString("nombre_grupo"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar grupos:\n" + e.getMessage(),
                    "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private int obtenerIdGrupo(String nombreGrupo) {
		String query = "SELECT id FROM grupos WHERE nombre_grupo = ?";
		try (Connection cn = connectionDB.conectar(); PreparedStatement ps = cn.prepareStatement(query)) {
			ps.setString(1, nombreGrupo);
			ResultSet rs = ps.executeQuery();
			if (rs.next())
				return rs.getInt("id");
		} catch (SQLException e) {
			System.err.println("Error al obtener id del grupo: " + e.getMessage());
		}
		return -1;
	}

    /**
     * Actualizar los datos del estudiante en la base de datos segun los datos ingresados
     */
    private void guardarCambios() {
        String nuevoNombre = txtNombre.getText().trim();
        String nuevoApellido = txtApellido.getText().trim();
        String nuevoEmail = txtEmail.getText().trim();
        String nuevaPassword = new String(txtPassword.getPassword()).trim();
        String nombreGrupo = (String) cbGrupo.getSelectedItem();
        int grupoId = obtenerIdGrupo(nombreGrupo);

        if (nuevoNombre.isEmpty() || nuevoApellido.isEmpty() || nuevoEmail.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos obligatorios.");
            return;
        }

        String query;
        if (!nuevaPassword.isEmpty()) {
            query = "UPDATE usuarios SET nombre = ?, apellido = ?, email = ?, password = ?, grupo_id = ? WHERE id = ?";
        } else {
            query = "UPDATE usuarios SET nombre = ?, apellido = ?, email = ?, grupo_id = ? WHERE id = ?";
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
                parentFrame.cargarEstudiantes();
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
