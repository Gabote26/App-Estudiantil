package guiEstudiante;

import calificaciones.CalificacionesBase;
import modelos.Calificacion;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VerCalificaciones extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTable tableCalificaciones;
    private DefaultTableModel model;
    private final long numControl;
    private final CalificacionesBase califDAO = new CalificacionesBase();

    public VerCalificaciones(long numControl, String nombre, String apellido) {
        this.numControl = numControl;

        setTitle("📝 Mis Calificaciones - " + nombre + " " + apellido);
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        // ------- Header -------
        
        JPanel headerPanel = new JPanel(null);
        headerPanel.setBounds(0, 0, 800, 80);
        headerPanel.setBackground(new Color(52, 73, 94));
        getContentPane().add(headerPanel);

        JLabel lblTitulo = new JLabel("📝 Mis Calificaciones");
        lblTitulo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBounds(30, 15, 400, 30);
        headerPanel.add(lblTitulo);

        JLabel lblInfo = new JLabel(nombre + " " + apellido + " - No. Control: " + numControl);
        lblInfo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        lblInfo.setForeground(new Color(200, 200, 200));
        lblInfo.setBounds(30, 50, 500, 20);
        headerPanel.add(lblInfo);

        // ------- Tabla -------

        model = new DefaultTableModel(
            new Object[]{"Materia", "Parcial 1", "Parcial 2", "Parcial 3", "Promedio", "Estado"},
            0
        ) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableCalificaciones = new JTable(model);
        tableCalificaciones.setRowHeight(35);
        tableCalificaciones.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        tableCalificaciones.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableCalificaciones.getColumnModel().getColumn(4).setCellRenderer(new PromedioRenderer());
        tableCalificaciones.getColumnModel().getColumn(5).setCellRenderer(new EstadoRenderer());
        
        JScrollPane scrollPane = new JScrollPane(tableCalificaciones);
        scrollPane.setBounds(30, 100, 730, 350);
        getContentPane().add(scrollPane);

        JButton btnRefrescar = new JButton("🔄 Actualizar");
        btnRefrescar.setBounds(320, 470, 140, 35);
        btnRefrescar.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        btnRefrescar.addActionListener(e -> cargarDatos());
        getContentPane().add(btnRefrescar);

        cargarDatos();
    }

    private void cargarDatos() {
        model.setRowCount(0);
        
        List<Calificacion> calificaciones = califDAO.getCalificacionesEstudiante(numControl);

        if (calificaciones.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No hay calificaciones registradas aún",
                "Sin datos",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        for (Calificacion cal : calificaciones) {
            String p1 = cal.getParcial1() != null ? String.format("%.2f", cal.getParcial1()) : "-";
            String p2 = cal.getParcial2() != null ? String.format("%.2f", cal.getParcial2()) : "-";
            String p3 = cal.getParcial3() != null ? String.format("%.2f", cal.getParcial3()) : "-";
            String promedio = String.format("%.2f", cal.getPromedio());
            String estado = cal.esAprobado() ? "Aprobado" : "Reprobado";

            model.addRow(new Object[]{
                cal.getMateria(),
                p1,
                p2,
                p3,
                promedio,
                estado
            });
        }
    }

    // Obtener el promedio de forma automatica
    class PromedioRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        public PromedioRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            try {
                double promedio = Double.parseDouble(value.toString());
                setFont(getFont().deriveFont(Font.BOLD));
                
                if (promedio >= 9.0) {
                    setForeground(new Color(0, 153, 0));
                } else if (promedio >= 8.0) {
                    setForeground(new Color(51, 153, 51));
                } else if (promedio >= 7.0) {
                    setForeground(new Color(255, 140, 0));
                } else if (promedio >= 6.0) {
                    setForeground(new Color(255, 100, 0));
                } else {
                    setForeground(Color.RED);
                }
            } catch (NumberFormatException e) {
                setForeground(Color.BLACK);
            }
            
            if (!isSelected) {
                setBackground(Color.WHITE);
            }
            
            return this;
        }
    }

    // Mostrar el estado actual en que se encuentra la calificacion
    class EstadoRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        public EstadoRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setFont(getFont().deriveFont(Font.BOLD));
            
            if ("Aprobado".equals(value)) {
                setForeground(new Color(0, 153, 0));
                setText("✅ Aprobado");
            } else {
                setForeground(Color.RED);
                setText("❌ Reprobado");
            }
            
            if (!isSelected) {
                setBackground(Color.WHITE);
            }
            
            return this;
        }
    }
}