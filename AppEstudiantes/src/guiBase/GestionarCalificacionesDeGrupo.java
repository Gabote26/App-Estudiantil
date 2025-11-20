package guiBase;

import calificaciones.CalificacionesBase;
import calificaciones.CalificacionesBase.CalificacionConAlumno;
import db.ConexionMysql;
import mensajes.MensajesBase;
import modelos.Calificacion;
import modelos.Mensaje;
import utils.RoundedButton;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GestionarCalificacionesDeGrupo extends JFrame {

    private static final long serialVersionUID = 1L;
    private final ConexionMysql connectionDB = new ConexionMysql();
    private final CalificacionesBase califDAO = new CalificacionesBase();
    private final MensajesBase mensajesDAO = new MensajesBase();
    private final String materiaProfesor;
    private final int remitenteId;
    
    private JComboBox<String> cbGrupo;
    private JComboBox<String> cbMateria;
    private JTable tableCalificaciones;
    private DefaultTableModel model;
    private RoundedButton btnCargar, btnGuardar;
    private List<CalificacionConAlumno> datosActuales = new ArrayList<>();

    public GestionarCalificacionesDeGrupo(String materiaProfesor, int remitenteId) {
        this.materiaProfesor = materiaProfesor;
        this.remitenteId = remitenteId;
        
        setTitle("📝 Gestión de Calificaciones" + 
                 (materiaProfesor != null ? " - " + materiaProfesor : ""));
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        JPanel panelFiltros = new JPanel(null);
        panelFiltros.setBounds(20, 20, 940, 80);
        panelFiltros.setBackground(new Color(245, 245, 250));
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Seleccionar grupo y materia"));
        getContentPane().add(panelFiltros);

        JLabel lblGrupo = new JLabel("Grupo:");
        lblGrupo.setBounds(20, 30, 60, 25);
        panelFiltros.add(lblGrupo);

        cbGrupo = new JComboBox<>();
        cbGrupo.setBounds(90, 30, 250, 30);
        panelFiltros.add(cbGrupo);

        JLabel lblMateria = new JLabel("Materia:");
        lblMateria.setBounds(370, 30, 60, 25);
        panelFiltros.add(lblMateria);

        cbMateria = new JComboBox<>(new String[]{
            "Lengua", "Humanidades", "Matematicas", "Sociales", "Ciencias"
        });
        cbMateria.setBounds(440, 30, 200, 30);
        
        // Si el profesor tiene materia asignada, solo mostrar esa para evitar problemas
        if (materiaProfesor != null && !materiaProfesor.isEmpty()) {
            cbMateria.setSelectedItem(materiaProfesor);
            cbMateria.setEnabled(false);
        }
        panelFiltros.add(cbMateria);

        btnCargar = new RoundedButton("📥 Cargar Estudiantes", 20);
        btnCargar.setBounds(670, 30, 180, 30);
        btnCargar.addActionListener(e -> cargarCalificaciones());
        panelFiltros.add(btnCargar);

        // ========== TABLA DE CALIFICACIONES ==========

        model = new DefaultTableModel(
            new Object[]{"No. Control", "Nombre Completo", "Parcial 1", "Parcial 2", "Parcial 3", "Promedio"}, 
            0
        ) {
            private static final long serialVersionUID = 1L;

            @Override
            public Class<?> getColumnClass(int column) {
                if (column >= 2 && column <= 4) return Double.class;
                if (column == 5) return String.class;
                return Object.class;
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 2 && column <= 4; // Solo parciales editables
            }
        };

        tableCalificaciones = new JTable(model);
        tableCalificaciones.setRowHeight(35);
        tableCalificaciones.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableCalificaciones.getColumnModel().getColumn(5).setCellRenderer(new PromedioRenderer());
        
        // Recalcular el promedio de forma automatica
        model.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                
                if (column >= 2 && column <= 4) {
                    actualizarPromedio(row);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tableCalificaciones);
        scrollPane.setBounds(20, 120, 940, 430);
        getContentPane().add(scrollPane);

        // ========== INSTRUCCIONES ==========
        
        JLabel lblInstrucciones = new JLabel(
            "<html><b>Instrucciones:</b> Haz doble clic en las celdas de Parcial 1, 2 o 3 para editar. " +
            "El promedio se calcula automáticamente. Calificaciones: 0-100</html>"
        );
        lblInstrucciones.setBounds(20, 560, 700, 30);
        lblInstrucciones.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblInstrucciones.setForeground(new Color(100, 100, 100));
        getContentPane().add(lblInstrucciones);

        // ========== BOTONES DE ACCIÓN ==========

        btnGuardar = new RoundedButton("💾 Guardar Calificaciones", 20);
        btnGuardar.setBounds(730, 565, 230, 40);
        btnGuardar.setBackground(new Color(52, 152, 219));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.addActionListener(e -> guardarCalificaciones());
        getContentPane().add(btnGuardar);

        // Cargar grupos
        cargarGrupos();
    }

    private void cargarGrupos() {
        cbGrupo.removeAllItems();
        String sql = "SELECT id, nombre_grupo FROM grupos ORDER BY nombre_grupo";
        
        try (Connection cn = connectionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                cbGrupo.addItem(rs.getInt("id") + " - " + rs.getString("nombre_grupo"));
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar grupos: " + e.getMessage());
        }
    }

    private void cargarCalificaciones() {
        if (cbGrupo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un grupo");
            return;
        }

        String seleccion = cbGrupo.getSelectedItem().toString();
        int grupoId = Integer.parseInt(seleccion.split(" - ")[0]);
        String materia = cbMateria.getSelectedItem().toString();

        // Obtener datos de las calificaciones
        datosActuales = califDAO.getCalificacionesGrupo(grupoId, materia);

        if (datosActuales.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay estudiantes en este grupo");
            return;
        }

        model.setRowCount(0);
        
        for (CalificacionConAlumno alumno : datosActuales) {
            String promedio = alumno.promedio != null ? 
                              String.format("%.2f", alumno.promedio) : "-";
            
            model.addRow(new Object[]{
                alumno.numControl,
                alumno.getNombreCompleto(),
                alumno.parcial1,
                alumno.parcial2,
                alumno.parcial3,
                promedio
            });
        }

        JOptionPane.showMessageDialog(this, 
            "✅ " + datosActuales.size() + " estudiantes cargados",
            "Datos cargados",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void actualizarPromedio(int row) {
        try {
            Object p1Obj = model.getValueAt(row, 2);
            Object p2Obj = model.getValueAt(row, 3);
            Object p3Obj = model.getValueAt(row, 4);
            
            int count = 0;
            double sum = 0;
            
            if (p1Obj != null && !p1Obj.toString().trim().isEmpty()) {
                sum += Double.parseDouble(p1Obj.toString());
                count++;
            }
            if (p2Obj != null && !p2Obj.toString().trim().isEmpty()) {
                sum += Double.parseDouble(p2Obj.toString());
                count++;
            }
            if (p3Obj != null && !p3Obj.toString().trim().isEmpty()) {
                sum += Double.parseDouble(p3Obj.toString());
                count++;
            }
            
            String promedio = count > 0 ? String.format("%.2f", sum / count) : "-";
            model.setValueAt(promedio, row, 5);
            
        } catch (NumberFormatException e) {
            model.setValueAt("-", row, 5);
        }
    }

    private void guardarCalificaciones() {
        if (datosActuales.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primero carga la lista de estudiantes");
            return;
        }

        String materia = cbMateria.getSelectedItem().toString();
        List<Calificacion> calificaciones = new ArrayList<>();

        for (int i = 0; i < model.getRowCount(); i++) {
            long numControl = (Long) model.getValueAt(i, 0);
            
            Double p1 = parseCalificacion(model.getValueAt(i, 2));
            Double p2 = parseCalificacion(model.getValueAt(i, 3));
            Double p3 = parseCalificacion(model.getValueAt(i, 4));

            // Solo guardar si hay al menos una calificación
            if (p1 != null || p2 != null || p3 != null) {
                calificaciones.add(new Calificacion(numControl, materia, p1, p2, p3));
            }
        }

        if (calificaciones.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠️ No hay calificaciones para guardar");
            return;
        }

        // Confirmacion
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "¿Guardar " + calificaciones.size() + " calificaciones en " + materia + "?",
            "Confirmar",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        // Guardar si el resultado es exitoso
        boolean exito = califDAO.guardarCalificacionesLote(calificaciones);

        if (exito) {
            enviarNotificaciones(calificaciones, materia);
            JOptionPane.showMessageDialog(this, 
                "✅ Calificaciones guardadas correctamente\n" +
                "📬 Notificaciones enviadas a los estudiantes",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(this, 
                "❌ Error al guardar calificaciones",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private Double parseCalificacion(Object obj) {
        if (obj == null) return null;
        
        String str = obj.toString().trim();
        if (str.isEmpty()) return null;
        
        try {
            double val = Double.parseDouble(str);
            if (val < 0 || val > 100) {
                JOptionPane.showMessageDialog(this, 
                    "⚠️ Calificación fuera de rango: " + val + "\nDebe estar entre 0 y 100");
                return null;
            }
            return val;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void enviarNotificaciones(List<Calificacion> calificaciones, String materia) {
        for (Calificacion cal : calificaciones) {
            int estudianteId = obtenerIdPorNumControl(cal.getNumControl());
            if (estudianteId <= 0) continue;

            String detalles = construirDetallesCalificacion(cal, materia);
            
            Mensaje notificacion = new Mensaje(
                remitenteId,
                "calificacion",
                "📝 Nuevas calificaciones en " + materia,
                detalles
            );
            
            List<Integer> dest = new ArrayList<>();
            dest.add(estudianteId);
            
            mensajesDAO.enviarMensaje(notificacion, dest);
        }
    }

    // Detalles de las calificaciones
    private String construirDetallesCalificacion(Calificacion cal, String materia) {
        StringBuilder sb = new StringBuilder();
        sb.append("Se han registrado nuevas calificaciones en ").append(materia).append(":\n\n");
        
        if (cal.getParcial1() != null) {
            sb.append("📌 Parcial 1: ").append(String.format("%.2f", cal.getParcial1())).append("\n");
        }
        if (cal.getParcial2() != null) {
            sb.append("📌 Parcial 2: ").append(String.format("%.2f", cal.getParcial2())).append("\n");
        }
        if (cal.getParcial3() != null) {
            sb.append("📌 Parcial 3: ").append(String.format("%.2f", cal.getParcial3())).append("\n");
        }
        
        sb.append("\n🎯 Promedio: ").append(String.format("%.2f", cal.getPromedio()));
        sb.append("\n📊 Estado: ").append(cal.esAprobado() ? "✅ Aprobado" : "❌ Reprobado");
        
        return sb.toString();
    }

    private int obtenerIdPorNumControl(long numControl) {
        String sql = "SELECT id FROM usuarios WHERE no_control = ?";
        
        try (Connection cn = connectionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setLong(1, numControl);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id");
            }
            
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        return -1;
    }

    // Promedio renderer
    class PromedioRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        public PromedioRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value != null && !value.toString().equals("-")) {
                try {
                    double promedio = Double.parseDouble(value.toString());
                    
                    if (promedio >= 8.0) {
                        setForeground(new Color(0, 153, 0)); // Verde
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if (promedio >= 6.0) {
                        setForeground(new Color(255, 140, 0)); // Naranja
                    } else {
                        setForeground(Color.RED);
                        setFont(getFont().deriveFont(Font.BOLD));
                    }
                } catch (NumberFormatException e) {
                    setForeground(Color.BLACK);
                }
            } else {
                setForeground(Color.GRAY);
            }
            
            if (!isSelected) {
                setBackground(Color.WHITE);
            }
            
            return this;
        }
    }
}