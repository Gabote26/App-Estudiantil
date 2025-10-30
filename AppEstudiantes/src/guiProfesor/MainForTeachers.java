package guiProfesor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import db.ConexionMysql;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MainForTeachers extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTable tableEstudiantes;
    private DefaultTableModel model;
    private final ConexionMysql connectionDB = new ConexionMysql();

    public MainForTeachers() {
        setTitle("Panel del Profesor - Gestión de Estudiantes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1250, 450);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(42, 34, 71));
        headerPanel.setPreferredSize(new Dimension(700, 60));
        JLabel title = new JLabel("Gestión de Estudiantes");
        title.setFont(new Font("Roboto", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        headerPanel.add(title);
        getContentPane().add(headerPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"ID", "Nombre", "Apellido", "Email", "Rol", "No-Control"}, 0);
        tableEstudiantes = new JTable(model);
        tableEstudiantes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tableEstudiantes);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel();
        actionPanel.setBackground(new Color(245, 245, 245));
        JButton btnRefrescar = new JButton("🔄 Refrescar Lista");
        JButton btnAgregar = new JButton("➕ Agregar Estudiante");
        JButton btnEliminar = new JButton("🗑️ Eliminar Estudiante");
        JButton btnVer = new JButton("👁️ Ver Detalles");
        JButton btnEditar = new JButton("✏️ Editar Estudiante");

        actionPanel.add(btnRefrescar);
        actionPanel.add(btnVer);
        actionPanel.add(btnAgregar);
        actionPanel.add(btnEliminar);
        actionPanel.add(btnEditar);

        getContentPane().add(actionPanel, BorderLayout.SOUTH);

        // Cargar datos desde la base de datos
        cargarEstudiantes();

        // Acciones de los botones
        btnRefrescar.addActionListener(e -> cargarEstudiantes());

        btnVer.addActionListener(e -> verDetallesEstudiante());

        btnEliminar.addActionListener(e -> eliminarEstudiante());
        
        btnEditar.addActionListener(e -> editarEstudiante());
        
        btnAgregar.addActionListener(e -> agregarEstudiante());

    }

    /**
     * Se cargan todos los estudiantes desde la base de datos
     */
    public void cargarEstudiantes() {
        model.setRowCount(0); // Limpiar tabla antes de recargar para evitar errores y desbordamientos
        String query = "SELECT id, nombre, apellido, email, role, no_control FROM usuarios WHERE role = 'ESTUDIANTE'";

        try (Connection cn = connectionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Object[] fila = {
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("email"),
                        rs.getString("role"),
                        rs.getString("no_control")
                };
                model.addRow(fila);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar estudiantes:\n" + e.getMessage(),
                    "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Muestra los detalles del estudiante seleccionado
     */
    private void verDetallesEstudiante() {
        int selectedRow = tableEstudiantes.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un estudiante primero.");
            return;
        }

        String nombre = model.getValueAt(selectedRow, 1).toString();
        String email = model.getValueAt(selectedRow, 2).toString();
        JOptionPane.showMessageDialog(this,
                "Detalles del Estudiante:\n\nNombre: " + nombre + "\nEmail: " + email,
                "Información del Estudiante", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Abre el formulario para agregar un nuevo estudiante
     */
    private void agregarEstudiante() {
        AgregarEstudiante agregarFrame = new AgregarEstudiante(this);
        agregarFrame.setVisible(true);
    }


    /**
     * Elimina el estudiante seleccionado de la base de datos
     */
    private void eliminarEstudiante() {
        int selectedRow = tableEstudiantes.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un estudiante primero.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Seguro que desea eliminar este estudiante?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) model.getValueAt(selectedRow, 0);
            String query = "DELETE FROM usuarios WHERE id = ?";

            try (Connection cn = connectionDB.conectar();
                 PreparedStatement ps = cn.prepareStatement(query)) {

                ps.setInt(1, id);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Estudiante eliminado correctamente.");
                cargarEstudiantes();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar estudiante:\n" + e.getMessage(),
                        "Error SQL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Abre una ventana para editar los datos del estudiante seleccionado
     */
    private void editarEstudiante() {
        int selectedRow = tableEstudiantes.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un estudiante primero.");
            return;
        }

        // Obtener datos seleccionados
        int id = (int) model.getValueAt(selectedRow, 0);
        String nombre = model.getValueAt(selectedRow, 1).toString();
        String apellido = model.getValueAt(selectedRow, 2).toString();
        String email = model.getValueAt(selectedRow, 3).toString();

        // Abrir nueva ventana con datos cargados
        EditarEstudiante editarFrame = new EditarEstudiante(this, id, nombre, apellido, email);
        editarFrame.setVisible(true);
    }

}
