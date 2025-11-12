package guiEstudiante;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class HorariosEstudiantes extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JComboBox<String> comboBox_grupo, comboBox_semestre, comboBox_turno;
	private JScrollPane scrol;
	private JLabel imagenLabel;
	private Map<String, String> mapaHorarios;

	// Rutas CARPETAS IMAGENES
	private final String[] rutasBase = { "resources/1ER-SEMESTRE-MATU/", "resources/1ER-SEMESTRE-VESP/",
			"resources/3ER-SEMESTRE-MATU/", "resources/3ER-SEMESTRE-VESP/", "resources/5TO-SEMESTRE-MATU/",
			"resources/5TO-SEMESTRE-VESP/" };

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

		comboBox_semestre = new JComboBox<>(new String[] { "1°", "3°", "5°" });
		comboBox_semestre.setBounds(30, 50, 95, 30);
		contentPane.add(comboBox_semestre);

		comboBox_grupo = new JComboBox<>(
				new String[] { "AMADMRH", "BMADMRH", "CMADMRH", "PRO", "ELE", "MEC", "MAU", "SMEC", "LOG" });
		comboBox_grupo.setBounds(135, 50, 120, 30);
		contentPane.add(comboBox_grupo);

		comboBox_turno = new JComboBox<>(new String[] { "AM", "AV", "VM" });
		comboBox_turno.setBounds(265, 50, 95, 30);
		contentPane.add(comboBox_turno);

		JButton btnAceptar = new JButton("ACEPTAR");
		btnAceptar.setBounds(370, 50, 120, 30);
		contentPane.add(btnAceptar);

		imagenLabel = new JLabel();
		imagenLabel.setHorizontalAlignment(JLabel.CENTER);
		imagenLabel.setVerticalAlignment(JLabel.TOP);

		scrol = new JScrollPane(imagenLabel);
		scrol.setBounds(20, 100, 660, 450);
		contentPane.add(scrol);

		comboBox_semestre.addActionListener(e -> actualizarHorario());
		comboBox_grupo.addActionListener(e -> actualizarHorario());
		comboBox_turno.addActionListener(e -> actualizarHorario());
		btnAceptar.addActionListener(e -> actualizarHorario());

		inicializarHorarios();
	}

//RUTAS DE IMAGEN HORARIOS
	private void inicializarHorarios() {
		mapaHorarios = new HashMap<>();
		// horarios 1 SEMESTRE MT
		mapaHorarios.put("1°-AMADMRH-AM", "1_AMADMRH_page-0001.jpg");
		mapaHorarios.put("1°-BMADMRH-AM", "1_BMADMRH_page-0001.jpg");
		mapaHorarios.put("1°-CMADMRH-AM", "1-CMADMRH_page-0001.jpg");
		mapaHorarios.put("1°-PRO-AM", "1_AMPRO_page-0001.jpg");
		mapaHorarios.put("1°-ELE-AM", "1_AMELE_page-0001.jpg");
		mapaHorarios.put("1°-MEC-AM", "1_AMMEC_page-0001.jpg");
		mapaHorarios.put("1°-SMEC-AM", "1_AMSMEC_page-0001.jpg");
		mapaHorarios.put("1°-LOG-AM", "1_AMLOG_page-0001.jpg");
		mapaHorarios.put("1°-MAU-AM", "1_AMMAU_page-0001.jpg");
		mapaHorarios.put("1°-LOG-AM", "1-AMLOG_page-0001.jpg");
		// horarios 1 SEMESTRE VP
		mapaHorarios.put("1°-AMADMRH-AV", "1_AVADMRH_page-0001.jpg");
		mapaHorarios.put("1°-BMADMRH-AV", "1_BVADMRH_page-0001.jpg");
		mapaHorarios.put("1°-PRO-AV", "1_AVPRO_page-0001.jpg");
		mapaHorarios.put("1°-ELE-AV", "1-AVELE_page-0001.jpg");
		mapaHorarios.put("1°-MEC-AV", "1_AVMEC_page-0001.jpg");
		mapaHorarios.put("1°-MEC-AV", "1_BVMEC_page-0001.jpg");
		mapaHorarios.put("1°-SMEC-AV", "1-AVSMEC_page-0001.jpg");
		mapaHorarios.put("1°-LOG-AV", "1_AVLOG_page-0001.jpg");
		mapaHorarios.put("1°-MAU-AV", "1_AVMAU_page-0001.jpg");
		mapaHorarios.put("1°-LOG-AV", "1-AVLOG_page-0001.jpg");
		// horarios 3 SEMESTRE MT
		mapaHorarios.put("3°-AMADMRH-AM", "3_AMADMRH_page-0001.jpg");
		mapaHorarios.put("3°-BMADMRH-AM", "3_BMADMRH_page-0001.jpg");
		mapaHorarios.put("3°-CMADMRH-AM", "3_CMADMRH_page-0001.jpg");
		mapaHorarios.put("3°-PRO-AM", "3_AMPRO_page-0001.jpg");
		mapaHorarios.put("3°-ELE-AM", "3-AMELE_page-0001.jpg");
		mapaHorarios.put("3°-MEC-AM", "3_AMMEC_page-0001.jpg");
		mapaHorarios.put("3°-SMEC-AM", "3-AMSMEC_page-0001.jpg");
		mapaHorarios.put("3°-LOG-AM", "3_AMLOG_page-0001.jpg");
		mapaHorarios.put("3°-MAU-AM", "3_AMMAU_page-0001.jpg");
		mapaHorarios.put("3°-LOG-AM", "3-AMLOG_page-0001.jpg");
		// HORARIOS 3 SEMESTRE VP
		mapaHorarios.put("3°-AMADMRH-AV", "3_AVADMRH_page-0001.jpg");
		mapaHorarios.put("3°-BMADMRH-AV", "3_BVADMRH_page-0001.jpg");
		mapaHorarios.put("3°-CMADMRH-AV", "3-CVADMRH_page-0001.jpg");
		mapaHorarios.put("3°-PRO-AV", "3_AVPRO_page-0001.jpg");
		mapaHorarios.put("3°-ELE-AV", "3-AVELE_page-0001.jpg");
		mapaHorarios.put("3°-MEC-AV", "3_AVMEC_page-0001.jpg");
		mapaHorarios.put("3°-SMEC-AV", "3-AVSMEC_page-0001.jpg");
		mapaHorarios.put("3°-LOG-AV", "3_AVLOG_page-0001.jpg");
		mapaHorarios.put("3°-MAU-AV", "3_AVMAU_page-0001.jpg");
		mapaHorarios.put("3°-LOG-AV", "3-AVLOG_page-0001.jpg");
		// HORARIOS 5TO SEMESTRE MT
		mapaHorarios.put("5°-AMADMRH-AM", "5_AMADMRH_page-0001.jpg");
		mapaHorarios.put("5°-BMADMRH-AM", "5_BMADMRH_page-0001.jpg");
		mapaHorarios.put("5°-CMADMRH-AM", "5-CMADMRH_page-0001.jpg");
		mapaHorarios.put("5°-PRO-AM", "5_AMPRO_page-0001.jpg");
		mapaHorarios.put("5°-ELE-AM", "5-AMELE_page-0001.jpg");
		mapaHorarios.put("5°-MEC-AM", "5_AMMEC_page-0001.jpg");
		mapaHorarios.put("5°-SMEC-AM", "5-AMSMEC_page-0001.jpg");
		mapaHorarios.put("5°-LOG-AM", "5_AMLOG_page-0001.jpg");
		mapaHorarios.put("5°-MAU-AM", "5_AMMAU_page-0001.jpg");
		mapaHorarios.put("5°-LOG-AM", "5-AMLOG_page-0001.jpg");
		// HORARIOS 5TO SEMESTRE VP
		mapaHorarios.put("5°-AMADMRH-AV", "5_AVADMHR_page-0001.jpg");
		mapaHorarios.put("5°-BMADMRH-AV", "5_BVADMRH_page-0001.jpg");
		mapaHorarios.put("5°-PRO-AV", "5_AVPRO_page-0001.jpg");
		mapaHorarios.put("5°-ELE-AV", "5_AVELE_page-0001.jpg");
		mapaHorarios.put("5°-MEC-AV", "5_AVMEC_page-0001.jpg");
		mapaHorarios.put("5°-SMEC-AV", "5_AVSMEC_page-0001.jpg");
		mapaHorarios.put("5°-LOG-AV", "5-AVLOG_page-0001.jpg");
		mapaHorarios.put("5°-MAU-AV", "5_AVMAU_page-0001.jpg");
		mapaHorarios.put("5°-LOG-AV", "5-AVLOG_page-0001.jpg");

	}

	private void actualizarHorario() {
		String grupo = (String) comboBox_grupo.getSelectedItem();
		String semestre = (String) comboBox_semestre.getSelectedItem();
		String turno = (String) comboBox_turno.getSelectedItem();

		String clave = semestre + "-" + grupo + "-" + turno;
		String nombreArchivo = mapaHorarios.get(clave);

		if (nombreArchivo != null) {
			boolean imagenCargada = false;

			for (String ruta : rutasBase) {
				String rutaCompleta = ruta + nombreArchivo;
				File archivo = new File(rutaCompleta);

				if (archivo.exists()) {
					ImageIcon originalIcon = new ImageIcon(rutaCompleta);
					Image originalImage = originalIcon.getImage();

					int ancho = imagenLabel.getWidth();
					int alto = imagenLabel.getHeight();

					if (ancho > 0 && alto > 0) {
						Image imagenEscalada = originalImage.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
						imagenLabel.setIcon(new ImageIcon(imagenEscalada));
					} else {
						imagenLabel.setIcon(originalIcon);
					}

					imagenCargada = true;
					break;
				}
			}

			if (!imagenCargada) {
				imagenLabel.setIcon(null);
				JOptionPane.showMessageDialog(this, "No se encontró el horario elegido");
			}
		} else {
			imagenLabel.setIcon(null);
			JOptionPane.showMessageDialog(this, "No se encontró el horario elegido");
		}
	}
}
