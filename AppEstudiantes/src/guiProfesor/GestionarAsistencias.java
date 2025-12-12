package guiProfesor;

import asistencias.AsistenciasBase;
import asistencias.AsistenciasBase.AsistenciaConAlumno;
import db.ConexionMysql;
import mensajes.MensajesBase;
import modelos.Asistencia;
import modelos.Mensaje;
import utils.RoundedButton;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class GestionarAsistencias extends JFrame {

    private static final long serialVersionUID = 1L;
    private final ConexionMysql connectionDB = new ConexionMysql();
    private final AsistenciasBase asistenciasDAO = new AsistenciasBase();
    @SuppressWarnings("unused")
	private final String materiaProfesor;
    
    private JComboBox<String> cbGrupo;
    private JComboBox<String> cbMateria;
    private JSpinner dateChooser;
    private JTable tableAsistencias;
    private DefaultTableModel model;
    private RoundedButton btnCargar, btnGuardar;
    private List<AsistenciaConAlumno> datosActuales = new ArrayList<>();

    public GestionarAsistencias(String materiaProfesor) {
        this.materiaProfesor = materiaProfesor;
        
        setTitle("📋 Gestión de Asistencias - " + materiaProfesor);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        // ------- Panel de filtros -------
        
        JPanel panelFiltros = new JPanel(null);
        panelFiltros.setBounds(20, 20, 840, 100);
        panelFiltros.setBackground(new Color(245, 245, 250));
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Seleccionar grupo y fecha"));
        getContentPane().add(panelFiltros);

        JLabel lblGrupo = new JLabel("Grupo:");
        lblGrupo.setBounds(20, 30, 60, 25);
        panelFiltros.add(lblGrupo);

        cbGrupo = new JComboBox<>();
        cbGrupo.setBounds(90, 30, 200, 30);
        panelFiltros.add(cbGrupo);

        JLabel lblMateria = new JLabel("Materia:");
        lblMateria.setBounds(320, 30, 60, 25);
        panelFiltros.add(lblMateria);

        cbMateria = new JComboBox<>(new String[]{
            "Lengua", "Humanidades", "Matematicas", "Sociales", "Ciencias"
        });
        cbMateria.setBounds(390, 30, 180, 30);
        
        // Si el profesor tiene materia asignada, solo mostrar esa para que no modifique las de otros profes
        if (materiaProfesor != null && !materiaProfesor.isEmpty()) {
            cbMateria.setSelectedItem(materiaProfesor);
            cbMateria.setEnabled(false);
        }
        panelFiltros.add(cbMateria);

        JLabel lblFecha = new JLabel("Fecha:");
        lblFecha.setBounds(600, 30, 60, 25);
        panelFiltros.add(lblFecha);

        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateChooser = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateChooser, "dd/MM/yyyy");
        dateChooser.setEditor(dateEditor);
        dateChooser.setValue(new Date());
        dateChooser.setBounds(660, 30, 150, 30);
        panelFiltros.add(dateChooser);

        btnCargar = new RoundedButton("📥 Cargar Lista", 20);
        btnCargar.setBounds(320, 70, 150, 30);
        btnCargar.addActionListener(e -> cargarAsistencias());
        panelFiltros.add(btnCargar);

        // ------- Tabla de Asistencias -------

        model = new DefaultTableModel(
            new Object[]{"No. Control", "Nombre Completo", "Asistió (A)", "Faltó (F)", "Justificante (P)"}, 
            0
        ) {
            private static final long serialVersionUID = 1L;

            @Override
            public Class<?> getColumnClass(int column) {
                if (column >= 2) return Boolean.class;
                return String.class;
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 2;
            }
        };

        tableAsistencias = new JTable(model);
        tableAsistencias.setRowHeight(35);
        tableAsistencias.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        TableColumnModel columnModel = tableAsistencias.getColumnModel();
        for (int i = 2; i <= 4; i++) {
            columnModel.getColumn(i).setCellRenderer(new CheckboxRenderer());
        }
        
        // Listener para que solo se pueda marcar uno por fila
        model.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                
                if (column >= 2 && column <= 4) {
                    boolean valor = (Boolean) model.getValueAt(row, column);
                    
                    if (valor) {
                        for (int i = 2; i <= 4; i++) {
                            if (i != column) {
                                model.setValueAt(false, row, i);
                            }
                        }
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tableAsistencias);
        scrollPane.setBounds(20, 140, 840, 400);
        getContentPane().add(scrollPane);

        // ------- Botones de las acciones -------

        btnGuardar = new RoundedButton("💾 Guardar Asistencias", 20);
        btnGuardar.setBounds(350, 560, 180, 40);
        btnGuardar.setBackground(new Color(52, 152, 219));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.addActionListener(e -> guardarAsistencias());
        getContentPane().add(btnGuardar);

        JButton btnCancelar = new RoundedButton("❌ Cancelar", 20);
        btnCancelar.setBounds(550, 560, 120, 40);
        btnCancelar.addActionListener(e -> dispose());
        getContentPane().add(btnCancelar);

        // Cargar grupos disponibles
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

    private void cargarAsistencias() {
        if (cbGrupo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un grupo");
            return;
        }

        String seleccion = cbGrupo.getSelectedItem().toString();
        int grupoId = Integer.parseInt(seleccion.split(" - ")[0]);
        String materia = cbMateria.getSelectedItem().toString();
        Date fecha = (Date) dateChooser.getValue();
        LocalDate localDate = fecha.toInstant()
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate();

        // Obtener los datos
        datosActuales = asistenciasDAO.obtenerAsistenciasGrupo(grupoId, materia, localDate);

        if (datosActuales.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay estudiantes en este grupo");
            return;
        }

        model.setRowCount(0);
        
        for (AsistenciaConAlumno alumno : datosActuales) {
            boolean a = "A".equals(alumno.estado);
            boolean f = "F".equals(alumno.estado);
            boolean p = "P".equals(alumno.estado);
            
            model.addRow(new Object[]{
                alumno.numControl,
                alumno.getNombreCompleto(),
                a,
                f,
                p
            });
        }

        JOptionPane.showMessageDialog(this, 
            "✅ " + datosActuales.size() + " estudiantes cargados",
            "Datos cargados",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void guardarAsistencias() {
        if (datosActuales.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primero carga la lista de asistencias");
            return;
        }

        Date fecha = (Date) dateChooser.getValue();
        LocalDate localDate = fecha.toInstant()
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate();
        
        String materia = cbMateria.getSelectedItem().toString();
        List<Asistencia> asistencias = new ArrayList<>();

        for (int i = 0; i < model.getRowCount(); i++) {
            long numControl = (Long) model.getValueAt(i, 0);
            boolean a = (Boolean) model.getValueAt(i, 2);
            boolean f = (Boolean) model.getValueAt(i, 3);
            boolean p = (Boolean) model.getValueAt(i, 4);

            String estado = "";
            if (a) estado = "A";
            else if (f) estado = "F";
            else if (p) estado = "P";

            if (!estado.isEmpty()) {
                asistencias.add(new Asistencia(numControl, materia, localDate, estado));
            }
        }

        if (asistencias.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠️ No has marcado ninguna asistencia");
            return;
        }

        // Confirmar
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "¿Guardar " + asistencias.size() + " registros de asistencia?",
            "Confirmar",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        // Guardar
        boolean exito = asistenciasDAO.registrarAsistenciasLote(asistencias);

        if (exito) {
            verificarYNotificarFaltas(asistencias);
            JOptionPane.showMessageDialog(this, 
                "✅ Asistencias guardadas correctamente",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(this, 
                "❌ Error al guardar asistencias",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    private void verificarYNotificarFaltas(List<Asistencia> asistencias) {
        MensajesBase mensajesDAO = new MensajesBase();
        
        for (Asistencia a : asistencias) {
            if ("F".equals(a.getEstado())) {
                // Contar el total de faltas
                int totalFaltas = asistenciasDAO.contarFaltas(a.getNumControl(), a.getMateria());
                
                // Si tiene 3 o más faltas, enviar notificación
                if (totalFaltas >= 3) {
                    int estudianteId = obtenerIdPorNumControl(a.getNumControl());
                    
                    if (estudianteId > 0) {
                        Mensaje alerta = new Mensaje(
                            1, // ID sistema
                            "asistencia",
                            "⚠️ Alerta de Inasistencias",
                            "Has acumulado " + totalFaltas + " faltas en la materia de " + 
                            a.getMateria() + ".\n\n" +
                            "Te recomendamos ponerte al corriente para evitar problemas académicos."
                        );
                        
                        List<Integer> dest = new ArrayList<>();
                        dest.add(estudianteId);
                        
                        mensajesDAO.enviarMensaje(alerta, dest);
                    }
                }
            }
        }
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

    class CheckboxRenderer extends JCheckBox implements TableCellRenderer {
        private static final long serialVersionUID = 1L;

        public CheckboxRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setSelected(value != null && (Boolean) value);
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return this;
        }
    }
}