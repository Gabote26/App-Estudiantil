package guiBase;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import db.ConexionMysql;
import utils.Recargable;

public class AgregarEstudiante extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextField txtNombre, txtApellido, txtEmail, txtNumControl;
    private JPasswordField txtPassword;
    private JComboBox<String> cbGrupo;
    private final ConexionMysql connectionDB = new ConexionMysql();
    private final Recargable parentFrame;

    public AgregarEstudiante(Recargable parentFrame) {
        this.parentFrame = parentFrame;

        setTitle("Agregar Nuevo Estudiante");
        setSize(400, 454);
        setResizable(false);
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

        JLabel lblNumControl = new JLabel("Num. de Control:");
        lblNumControl.setBounds(50, 270, 120, 20);
        getContentPane().add(lblNumControl);

        txtNumControl = new JTextField();
        txtNumControl.setBounds(150, 270, 180, 25);
        getContentPane().add(txtNumControl);

        JLabel lblGrupo = new JLabel("Grupo:");
        lblGrupo.setBounds(50, 310, 100, 20);
        getContentPane().add(lblGrupo);

        cbGrupo = new JComboBox<>();
        cbGrupo.setBounds(150, 310, 180, 25);
        getContentPane().add(cbGrupo);
        cargarGrupos();

        JButton btnGuardar = new JButton("💾 Guardar Estudiante");
        btnGuardar.setBounds(110, 370, 180, 30);
        getContentPane().add(btnGuardar);
        btnGuardar.addActionListener(e -> guardarNuevoEstudiante());
    }

    private void cargarGrupos() {
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
        try (Connection cn = connectionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setString(1, nombreGrupo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            System.err.println("Error al obtener id del grupo: " + e.getMessage());
        }
        return -1;
    }

    private void guardarNuevoEstudiante() {
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String numControl = txtNumControl.getText().trim();
        String nombreGrupo = (String) cbGrupo.getSelectedItem();
        int grupoId = obtenerIdGrupo(nombreGrupo);

        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty()
                || password.isEmpty() || numControl.isEmpty() || nombreGrupo == null) {
            JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos.");
            return;
        }

        String query = """
                INSERT INTO usuarios (nombre, apellido, email, password, role, no_control, grupo_id)
                VALUES (?, ?, ?, ?, 'ESTUDIANTE', ?, ?)
                """;

        try (Connection cn = connectionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(query)) {

            ps.setString(1, nombre);
            ps.setString(2, apellido);
            ps.setString(3, email);
            ps.setString(4, password);
            ps.setString(5, numControl);
            ps.setInt(6, grupoId);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "✅ Estudiante agregado correctamente.");

                // Refrescar la tabla
                if (parentFrame != null)
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
