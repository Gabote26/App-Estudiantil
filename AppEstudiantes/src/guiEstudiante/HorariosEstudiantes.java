package guiEstudiante;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class HorariosEstudiantes extends JFrame {

    private JPanel contentPane;
    private JComboBox<String> comboBox_grupo, comboBox_semestre, comboBox_turno;
    private JScrollPane scrol;
    private JLabel imagenLabel;
    private Map<String, String> mapaHorarios;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                HorariosEstudiantes frame = new HorariosEstudiantes();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public HorariosEstudiantes() {
        setTitle("Consulta de Horarios");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 720, 600);
        contentPane = new JPanel();
        contentPane.setBackground(new Color(128, 128, 192));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblTitulo = new JLabel("Selecciona tu semestre, grupo y turno:");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setBounds(20, 10, 400, 30);
        contentPane.add(lblTitulo);

        comboBox_semestre = new JComboBox<>(new String[]{"1°", "3°", "5°"});
        comboBox_semestre.setBounds(30, 50, 95, 30);
        contentPane.add(comboBox_semestre);

        comboBox_grupo = new JComboBox<>(new String[]{"AMADMRH", "BMADMRH", "CMADMRH", "PRO", "ELE", "MEC", "MAU", "SMEC", "LOG"});
        comboBox_grupo.setBounds(135, 50, 120, 30);
        contentPane.add(comboBox_grupo);

        comboBox_turno = new JComboBox<>(new String[]{"AM", "VM"});
        comboBox_turno.setBounds(265, 50, 95, 30);
        contentPane.add(comboBox_turno);

        JButton btnAceptar = new JButton("ACEPTAR");
        btnAceptar.setBounds(370, 50, 120, 30);
        contentPane.add(btnAceptar);

        // Crear JLabel para imagen y colocarlo dentro del JScrollPane
        imagenLabel = new JLabel();
        imagenLabel.setHorizontalAlignment(JLabel.CENTER);
        imagenLabel.setVerticalAlignment(JLabel.TOP);

        scrol = new JScrollPane(imagenLabel);
        scrol.setBounds(20, 100, 660, 450);
        contentPane.add(scrol);

        btnAceptar.addActionListener(e -> actualizarHorario());

        inicializarHorarios();
    }

    private void inicializarHorarios() {
        mapaHorarios = new HashMap<>();
        mapaHorarios.put("1°-AMADMRH-AM", "1_AMADMRH_page-0001.jpg");
        imagenLabel.setIcon(new ImageIcon("resources/1ER-SEMESTRE-MATU/1_AMADMRH_page-0001.jpg"));
        mapaHorarios.put("1°-BMADMRH-AM", "1_BMADMRH_page-0001.jpg");
        imagenLabel.setIcon(new ImageIcon("resources/1ER-SEMESTRE-MATU/1_BMADMRH_page-0001.jpg"));
        mapaHorarios.put("1°-CMADMRH-AM", "1_CMADMRH_page-0001.jpg");
        imagenLabel.setIcon(new ImageIcon("resources/1ER-SEMESTRE-MATU/1-CMADMRH_page-0001.jpg"));
        mapaHorarios.put("1°-PRO-AM", "1_AMPRO_page-0001.jpg");
        mapaHorarios.put("1°-ELE-AM", "1_AMELE_page-0001.jpg");
        mapaHorarios.put("1°-MEC-AM", "1_AMMEC_page-0001.jpg");
        mapaHorarios.put("1°-SMEC-AM", "1_SMEC_page-0001.jpg");
        mapaHorarios.put("1°-LOG-AM", "1_AMLOG_page-0001.jpg");
        mapaHorarios.put("1°-MAU-AM", "1_AMMAU_page-0001.jpg");
    }

    private void actualizarHorario() {
        String grupo = (String) comboBox_grupo.getSelectedItem();
        String semestre = (String) comboBox_semestre.getSelectedItem();
        String turno = (String) comboBox_turno.getSelectedItem();

        String clave = semestre + "-" + grupo + "-" + turno;
        String rutaImagen = mapaHorarios.get(clave);

        if (rutaImagen != null) {
            ImageIcon icon = new ImageIcon(rutaImagen);
            imagenLabel.setIcon(icon);
        } else {
            imagenLabel.setIcon(null);
            JOptionPane.showMessageDialog(this, "No se encontró imagen para esa combinación.");
        }
    }
}
