package guiEstudiante;

import asistencias.AsistenciasBase;
import asistencias.AsistenciasBase.ResumenAsistencia;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VerAsistencias extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTable tableResumen;
    private DefaultTableModel model;
    private final long numControl;
    private final AsistenciasBase asistenciasDAO = new AsistenciasBase();

    public VerAsistencias(long numControl, String nombre, String apellido) {
        this.numControl = numControl;

        setTitle("📊 Mis Asistencias - " + nombre + " " + apellido);
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        // Título
        JLabel lblTitulo = new JLabel("📊 Resumen de Asistencias");
        lblTitulo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 22));
        lblTitulo.setBounds(200, 20, 300, 30);
        getContentPane().add(lblTitulo);

        // Tabla
        model = new DefaultTableModel(
            new Object[]{"Materia", "Asistencias", "Faltas", "Justificantes", "% Asistencia"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableResumen = new JTable(model);
        tableResumen.setRowHeight(30);
        tableResumen.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(tableResumen);
        scrollPane.setBounds(30, 80, 630, 300);
        getContentPane().add(scrollPane);

        // Botón refrescar
        JButton btnRefrescar = new JButton("🔄 Actualizar");
        btnRefrescar.setBounds(280, 400, 140, 35);
        btnRefrescar.addActionListener(e -> cargarDatos());
        getContentPane().add(btnRefrescar);

        cargarDatos();
    }

    private void cargarDatos() {
        model.setRowCount(0);
        
        List<ResumenAsistencia> resumen = asistenciasDAO.obtenerResumenAlumno(numControl);

        if (resumen.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No hay registros de asistencia aún",
                "Sin datos",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        for (ResumenAsistencia r : resumen) {
            model.addRow(new Object[]{
                r.materia,
                r.asistencias,
                r.faltas,
                r.justificantes,
                String.format("%.1f%%", r.getPorcentajeAsistencia())
            });
        }
    }
}