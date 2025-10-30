package guiProfesor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase Materia
 */
class Materia {
    private String codigo;
    private String nombre;
    private double calificacion;
    private int creditos;
    private String profesor;
    private String periodo;
    private LocalDateTime fechaCalificacion;
    private List<Double> calificacionesParciales;
    private boolean asistenciaSuficiente;
    
    public Materia(String codigo, String nombre, double calificacion, int creditos, 
                   String profesor, String periodo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.calificacion = calificacion;
        this.creditos = creditos;
        this.profesor = profesor;
        this.periodo = periodo;
        this.setFechaCalificacion(LocalDateTime.now());
        this.calificacionesParciales = new ArrayList<>();
        this.asistenciaSuficiente = true;
    }
    
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public double getCalificacion() { return calificacion; }
    public int getCreditos() { return creditos; }
    public String getProfesor() { return profesor; }
    public String getPeriodo() { return periodo; }
    public List<Double> getCalificacionesParciales() { return calificacionesParciales; }
    public boolean isAsistenciaSuficiente() { return asistenciaSuficiente; }
    
    public void setCalificacion(double calificacion) { this.calificacion = calificacion; }
    public void setAsistenciaSuficiente(boolean asistenciaSuficiente) { 
        this.asistenciaSuficiente = asistenciaSuficiente; 
    }
    
    public void agregarCalificacionParcial(double calificacion) {
        this.calificacionesParciales.add(calificacion);
    }
    
    public boolean estaAprobada() {
        return calificacion >= 6.0 && asistenciaSuficiente;
    }
    
    public String getEstado() {
        if (!asistenciaSuficiente) {
            return "REPROBADA POR ASISTENCIA";
        }
        if (calificacion >= 9.0) return "EXCELENTE";
        else if (calificacion >= 8.0) return "MUY BIEN";
        else if (calificacion >= 7.0) return "BIEN";
        else if (calificacion >= 6.0) return "APROBADA";
        else return "REPROBADA";
    }

	public LocalDateTime getFechaCalificacion() {
		return fechaCalificacion;
	}

	public void setFechaCalificacion(LocalDateTime fechaCalificacion) {
		this.fechaCalificacion = fechaCalificacion;
	}
}

/**
 * Clase Alumno
 */
class Alumno {
    private String matricula;
    private String nombre;
    private String carrera;
    private int semestre;
    private List<Materia> materias;
    
    public Alumno(String matricula, String nombre, String carrera, int semestre) {
        this.matricula = matricula;
        this.nombre = nombre;
        this.carrera = carrera;
        this.semestre = semestre;
        this.materias = new ArrayList<>();
    }
    
    public String getMatricula() { return matricula; }
    public String getNombre() { return nombre; }
    public String getCarrera() { return carrera; }
    public int getSemestre() { return semestre; }
    public List<Materia> getMaterias() { return materias; }
    
    public void agregarMateria(Materia materia) {
        this.materias.add(materia);
    }
    
    public void eliminarMateria(String codigo) {
        materias.removeIf(m -> m.getCodigo().equals(codigo));
    }
    
    public double getPromedioGeneral() {
        if (materias.isEmpty()) return 0.0;
        return materias.stream()
                      .mapToDouble(Materia::getCalificacion)
                      .average()
                      .orElse(0.0);
    }
    
    public double getPromedioPonderado() {
        if (materias.isEmpty()) return 0.0;
        
        double sumaCalificacionesPonderadas = materias.stream()
            .mapToDouble(m -> m.getCalificacion() * m.getCreditos())
            .sum();
        
        int totalCreditos = materias.stream()
            .mapToInt(Materia::getCreditos)
            .sum();
        
        return totalCreditos > 0 ? sumaCalificacionesPonderadas / totalCreditos : 0.0;
    }
    
    public int getCreditosAprobados() {
        return materias.stream()
                      .filter(Materia::estaAprobada)
                      .mapToInt(Materia::getCreditos)
                      .sum();
    }
    
    public double getIndiceAprobacion() {
        if (materias.isEmpty()) return 0.0;
        long aprobadas = materias.stream().filter(Materia::estaAprobada).count();
        return (aprobadas * 100.0) / materias.size();
    }
}

/**
 * Ventana principal del sistema
 */
public class GestionarMateriasDeAlumno extends JFrame {
    private Alumno alumno;
    private JTable tablaMaterias;
    private DefaultTableModel modeloTabla;
    private JLabel lblPromedio, lblPonderado, lblCreditos, lblAprobacion;
    
    public GestionarMateriasDeAlumno() {
        // Mostrar diálogo de configuración inicial
        mostrarDialogoInicial();
        
        setTitle("Sistema de Gestión Académica - " + alumno.getNombre());
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        inicializarComponentes();
    }
    
    private void mostrarDialogoInicial() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField txtMatricula = new JTextField("2024001234");
        JTextField txtNombre = new JTextField("Juan Pérez García");
        JTextField txtCarrera = new JTextField("Ingeniería en Sistemas");
        JSpinner spinSemestre = new JSpinner(new SpinnerNumberModel(5, 1, 12, 1));
        
        panel.add(new JLabel("Matrícula:"));
        panel.add(txtMatricula);
        panel.add(new JLabel("Nombre Completo:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Carrera:"));
        panel.add(txtCarrera);
        panel.add(new JLabel("Semestre:"));
        panel.add(spinSemestre);
        
        int result = JOptionPane.showConfirmDialog(null, panel, 
                    "Datos del Alumno", JOptionPane.OK_CANCEL_OPTION, 
                    JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            alumno = new Alumno(
                txtMatricula.getText(),
                txtNombre.getText(),
                txtCarrera.getText(),
                (int) spinSemestre.getValue()
            );
        } else {
            System.exit(0);
        }
    }
    
    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        
        // Panel superior - Información del alumno
        JPanel panelSuperior = crearPanelInformacion();
        add(panelSuperior, BorderLayout.NORTH);
        
        // Panel central - Tabla de materias
        JPanel panelCentral = crearPanelTabla();
        add(panelCentral, BorderLayout.CENTER);
        
        // Panel inferior - Botones
        JPanel panelInferior = crearPanelBotones();
        add(panelInferior, BorderLayout.SOUTH);
        
        actualizarEstadisticas();
    }
    
    private JPanel crearPanelInformacion() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(52, 73, 94));
        
        JLabel lblTitulo = new JLabel("📚 HISTORIAL ACADÉMICO");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblTitulo);
        
        panel.add(Box.createVerticalStrut(15));
        
        JPanel infoPrincipal = new JPanel(new GridLayout(2, 2, 15, 5));
        infoPrincipal.setOpaque(false);
        
        infoPrincipal.add(crearEtiquetaInfo("👤 Alumno: " + alumno.getNombre()));
        infoPrincipal.add(crearEtiquetaInfo("📋 Matrícula: " + alumno.getMatricula()));
        infoPrincipal.add(crearEtiquetaInfo("🎓 Carrera: " + alumno.getCarrera()));
        infoPrincipal.add(crearEtiquetaInfo("📖 Semestre: " + alumno.getSemestre()));
        
        panel.add(infoPrincipal);
        
        panel.add(Box.createVerticalStrut(15));
        
        JPanel estadisticas = new JPanel(new GridLayout(1, 4, 15, 0));
        estadisticas.setOpaque(false);
        
        lblPromedio = crearEtiquetaEstadistica("Promedio General: 0.00");
        lblPonderado = crearEtiquetaEstadistica("Promedio Ponderado: 0.00");
        lblCreditos = crearEtiquetaEstadistica("Créditos Aprobados: 0");
        lblAprobacion = crearEtiquetaEstadistica("Aprobación: 0%");
        
        estadisticas.add(lblPromedio);
        estadisticas.add(lblPonderado);
        estadisticas.add(lblCreditos);
        estadisticas.add(lblAprobacion);
        
        panel.add(estadisticas);
        
        return panel;
    }
    
    private JLabel crearEtiquetaInfo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Arial", Font.PLAIN, 14));
        lbl.setForeground(Color.WHITE);
        return lbl;
    }
    
    private JLabel crearEtiquetaEstadistica(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        lbl.setForeground(new Color(255, 215, 0));
        lbl.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return lbl;
    }
    
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] columnas = {"Código", "Materia", "Calificación", "Créditos", 
                            "Profesor", "Periodo", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaMaterias = new JTable(modeloTabla);
        tablaMaterias.setRowHeight(30);
        tablaMaterias.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaMaterias.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tablaMaterias.getTableHeader().setBackground(new Color(52, 73, 94));
        tablaMaterias.getTableHeader().setForeground(Color.WHITE);
        tablaMaterias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Renderizador personalizado para colorear filas
        tablaMaterias.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, 
                                                                 isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String estado = (String) table.getValueAt(row, 6);
                    if (estado.contains("REPROBADA")) {
                        c.setBackground(new Color(255, 200, 200));
                        c.setForeground(Color.RED.darker());
                    } else if (estado.equals("EXCELENTE")) {
                        c.setBackground(new Color(200, 255, 200));
                        c.setForeground(new Color(0, 128, 0));
                    } else {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                }
                
                if (column == 2) { // Columna de calificación
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setFont(getFont().deriveFont(Font.BOLD));
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaMaterias);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(new Color(236, 240, 241));
        
        JButton btnAgregar = crearBoton("➕ Agregar Materia", new Color(46, 204, 113));
        JButton btnEditar = crearBoton("✏️ Editar Materia", new Color(52, 152, 219));
        JButton btnEliminar = crearBoton("🗑️ Eliminar Materia", new Color(231, 76, 60));
        JButton btnReporte = crearBoton("📄 Generar Reporte", new Color(155, 89, 182));
        
        btnAgregar.addActionListener(e -> agregarMateria());
        btnEditar.addActionListener(e -> editarMateria());
        btnEliminar.addActionListener(e -> eliminarMateria());
        btnReporte.addActionListener(e -> generarReporte());
        
        panel.add(btnAgregar);
        panel.add(btnEditar);
        panel.add(btnEliminar);
        panel.add(btnReporte);
        
        return panel;
    }
    
    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(180, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(color.darker());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(color);
            }
        });
        
        return btn;
    }
    
    private void agregarMateria() {
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JTextField txtCodigo = new JTextField();
        JTextField txtNombre = new JTextField();
        JSpinner spinCalificacion = new JSpinner(new SpinnerNumberModel(10.0, 0.0, 10.0, 0.1));
        JSpinner spinCreditos = new JSpinner(new SpinnerNumberModel(6, 1, 12, 1));
        JTextField txtProfesor = new JTextField();
        JTextField txtPeriodo = new JTextField("2024-2");
        JCheckBox chkAsistencia = new JCheckBox("Asistencia Suficiente", true);
        
        panel.add(new JLabel("Código:"));
        panel.add(txtCodigo);
        panel.add(new JLabel("Nombre de la Materia:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Calificación:"));
        panel.add(spinCalificacion);
        panel.add(new JLabel("Créditos:"));
        panel.add(spinCreditos);
        panel.add(new JLabel("Profesor:"));
        panel.add(txtProfesor);
        panel.add(new JLabel("Periodo:"));
        panel.add(txtPeriodo);
        panel.add(new JLabel(""));
        panel.add(chkAsistencia);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
                    "Agregar Nueva Materia", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                Materia materia = new Materia(
                    txtCodigo.getText(),
                    txtNombre.getText(),
                    (Double) spinCalificacion.getValue(),
                    (Integer) spinCreditos.getValue(),
                    txtProfesor.getText(),
                    txtPeriodo.getText()
                );
                materia.setAsistenciaSuficiente(chkAsistencia.isSelected());
                
                alumno.agregarMateria(materia);
                actualizarTabla();
                actualizarEstadisticas();
                
                JOptionPane.showMessageDialog(this, "Materia agregada exitosamente", 
                                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al agregar materia: " + ex.getMessage(), 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void editarMateria() {
        int selectedRow = tablaMaterias.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una materia para editar", 
                                        "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String codigo = (String) modeloTabla.getValueAt(selectedRow, 0);
        Materia materia = alumno.getMaterias().stream()
            .filter(m -> m.getCodigo().equals(codigo))
            .findFirst()
            .orElse(null);
        
        if (materia != null) {
            JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            JSpinner spinCalificacion = new JSpinner(
                new SpinnerNumberModel(materia.getCalificacion(), 0.0, 10.0, 0.1));
            JCheckBox chkAsistencia = new JCheckBox("Asistencia Suficiente", 
                                                    materia.isAsistenciaSuficiente());
            
            panel.add(new JLabel("Nueva Calificación:"));
            panel.add(spinCalificacion);
            panel.add(new JLabel(""));
            panel.add(chkAsistencia);
            
            int result = JOptionPane.showConfirmDialog(this, panel, 
                        "Editar Materia", JOptionPane.OK_CANCEL_OPTION);
            
            if (result == JOptionPane.OK_OPTION) {
                materia.setCalificacion((Double) spinCalificacion.getValue());
                materia.setAsistenciaSuficiente(chkAsistencia.isSelected());
                actualizarTabla();
                actualizarEstadisticas();
            }
        }
    }
    
    private void eliminarMateria() {
        int selectedRow = tablaMaterias.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una materia para eliminar", 
                                        "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar esta materia?", 
            "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String codigo = (String) modeloTabla.getValueAt(selectedRow, 0);
            alumno.eliminarMateria(codigo);
            actualizarTabla();
            actualizarEstadisticas();
        }
    }
    
    private void generarReporte() {
        JTextArea txtReporte = new JTextArea(25, 60);
        txtReporte.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtReporte.setEditable(false);
        
        StringBuilder reporte = new StringBuilder();
        reporte.append("═══════════════════════════════════════════════════════════════\n");
        reporte.append("              REPORTE ACADÉMICO COMPLETO\n");
        reporte.append("═══════════════════════════════════════════════════════════════\n\n");
        reporte.append("Fecha: ").append(LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n\n");
        reporte.append("DATOS DEL ALUMNO:\n");
        reporte.append("─────────────────────────────────────────────────────────────\n");
        reporte.append("Nombre: ").append(alumno.getNombre()).append("\n");
        reporte.append("Matrícula: ").append(alumno.getMatricula()).append("\n");
        reporte.append("Carrera: ").append(alumno.getCarrera()).append("\n");
        reporte.append("Semestre: ").append(alumno.getSemestre()).append("\n\n");
        
        reporte.append("ESTADÍSTICAS:\n");
        reporte.append("─────────────────────────────────────────────────────────────\n");
        reporte.append(String.format("Promedio General: %.2f\n", alumno.getPromedioGeneral()));
        reporte.append(String.format("Promedio Ponderado: %.2f\n", alumno.getPromedioPonderado()));
        reporte.append(String.format("Créditos Aprobados: %d\n", alumno.getCreditosAprobados()));
        reporte.append(String.format("Índice de Aprobación: %.1f%%\n\n", alumno.getIndiceAprobacion()));
        
        reporte.append("MATERIAS CURSADAS:\n");
        reporte.append("═══════════════════════════════════════════════════════════════\n");
        
        for (Materia m : alumno.getMaterias()) {
            reporte.append(String.format("\n[%s] %s\n", m.getCodigo(), m.getNombre()));
            reporte.append(String.format("  Calificación: %.2f | Créditos: %d | Estado: %s\n", 
                m.getCalificacion(), m.getCreditos(), m.getEstado()));
            reporte.append(String.format("  Profesor: %s | Periodo: %s\n", 
                m.getProfesor(), m.getPeriodo()));
            reporte.append("─────────────────────────────────────────────────────────────\n");
        }
        
        txtReporte.setText(reporte.toString());
        
        JScrollPane scrollPane = new JScrollPane(txtReporte);
        JOptionPane.showMessageDialog(this, scrollPane, 
            "Reporte Académico", JOptionPane.PLAIN_MESSAGE);
    }
    
    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        
        for (Materia m : alumno.getMaterias()) {
            modeloTabla.addRow(new Object[]{
                m.getCodigo(),
                m.getNombre(),
                String.format("%.2f", m.getCalificacion()),
                m.getCreditos(),
                m.getProfesor(),
                m.getPeriodo(),
                m.getEstado()
            });
        }
    }
    
    private void actualizarEstadisticas() {
        lblPromedio.setText(String.format("Promedio General: %.2f", 
                                         alumno.getPromedioGeneral()));
        lblPonderado.setText(String.format("Promedio Ponderado: %.2f", 
                                          alumno.getPromedioPonderado()));
        lblCreditos.setText(String.format("Créditos Aprobados: %d", 
                                         alumno.getCreditosAprobados()));
        lblAprobacion.setText(String.format("Aprobación: %.1f%%", 
                                           alumno.getIndiceAprobacion()));
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
        	GestionarMateriasDeAlumno ventana = new GestionarMateriasDeAlumno();
            ventana.setVisible(true);
        });
    }
}