package guiEstudiante;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class HorariosEstudiantes extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JComboBox<String> comboBox_grupo, comboBox_semestre, comboBox_turno;
	private JLabel imagenLabel;
	private JScrollPane scrol;

	private Map<HorarioKey, String> mapaHorarios;

	// Carpetas de las imagenes de los horarios
	private final String[] rutasBase = { "resources/1ER-SEMESTRE-MATU/", "resources/1ER-SEMESTRE-VESP/",
			"resources/3ER-SEMESTRE-MATU/", "resources/3ER-SEMESTRE-VESP/", "resources/5TO-SEMESTRE-MATU/",
			"resources/5TO-SEMESTRE-VESP/" };

	// -------- MAIN ----------
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			HorariosEstudiantes frame = new HorariosEstudiantes();
			frame.setVisible(true);
		});
	}

	public HorariosEstudiantes() {

		setTitle("Consulta de Horarios - Estudiantes");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(850, 750);
		setLocationRelativeTo(null);

		// Contenedor
		contentPane = new JPanel(new BorderLayout());
		contentPane.setBackground(new Color(38, 47, 87));
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		setContentPane(contentPane);

		// Encabezado
		JPanel headerPanel = new JPanel();
		headerPanel.setBackground(new Color(60, 70, 110));
		headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
		headerPanel.setLayout(new BorderLayout(10, 10));
		contentPane.add(headerPanel, BorderLayout.NORTH);

		// Título
		JLabel lblTitulo = new JLabel("Selecciona tu semestre, grupo y turno:");
		lblTitulo.setForeground(Color.WHITE);
		lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
		headerPanel.add(lblTitulo, BorderLayout.NORTH);

		// Panel de filtros (Seleccion de combobox)
		JPanel filtrosPanel = new JPanel(new GridLayout(1, 3, 15, 0));
		filtrosPanel.setBackground(new Color(60, 70, 110));
		headerPanel.add(filtrosPanel, BorderLayout.SOUTH);

		// -------- OPCIONES --------
		comboBox_semestre = crearCombo(new String[] { "Seleccione una opción...", "1°", "3°", "5°" });
		comboBox_grupo = crearCombo(
				new String[] { "Seleccione una opción...", "ADMRH", "PRO", "ELE", "MEC", "MAU", "SMEC", "LOG" });
		comboBox_turno = crearCombo(new String[] { "Seleccione una opción...", "AM", "BM", "CM", "AV", "BV", "CV" });

		filtrosPanel.add(comboBox_semestre);
		filtrosPanel.add(comboBox_grupo);
		filtrosPanel.add(comboBox_turno);

		imagenLabel = new JLabel();
		imagenLabel.setHorizontalAlignment(JLabel.CENTER);
		imagenLabel.setVerticalAlignment(JLabel.TOP);
		imagenLabel.setBackground(Color.WHITE);

		scrol = new JScrollPane(imagenLabel);
		scrol.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
		contentPane.add(scrol, BorderLayout.CENTER);

		// Listeners
		comboBox_semestre.addActionListener(e -> actualizarHorario());
		comboBox_grupo.addActionListener(e -> actualizarHorario());
		comboBox_turno.addActionListener(e -> actualizarHorario());

		inicializarHorarios();
	}

	private JComboBox<String> crearCombo(String[] items) {
		JComboBox<String> combo = new JComboBox<>(items);
		combo.setBackground(Color.WHITE);
		combo.setForeground(Color.BLACK);
		combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		combo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		return combo;
	}

	// -------- ESTRUCTURA DEL MAPA --------
	private static class HorarioKey {
		String semestre, grupo, turno;

		HorarioKey(String s, String g, String t) {
			semestre = s;
			grupo = g;
			turno = t;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (!(o instanceof HorarioKey))
				return false;
			HorarioKey k = (HorarioKey) o;
			return semestre.equals(k.semestre) && grupo.equals(k.grupo) && turno.equals(k.turno);
		}

		@Override
		public int hashCode() {
			return (semestre + grupo + turno).hashCode();
		}
	}

	// -------- AGREGAR HORARIOS --------
	private void agregar(String semestre, String grupo, String turno, String archivo) {
		mapaHorarios.put(new HorarioKey(semestre, grupo, turno), archivo);
	}

	// -------- LISTA DE HORARIOS --------
	private void inicializarHorarios() {
		mapaHorarios = new HashMap<>();

		// Horarios 1ro turno matutino
		agregar("1°", "ADMRH", "AM", "1_AMADMRH_page-0001.jpg");
		agregar("1°", "ADMRH", "BM", "1_BMADMRH_page-0001.jpg");
		agregar("1°", "ADMRH", "CM", "1-CMADMRH_page-0001.jpg");
		agregar("1°", "ELE", "AM", "1_AMELE_page-0001.jpg");
		agregar("1°", "MAU", "AM", "1_AMMAU_page-0001.jpg");
		agregar("1°", "MEC", "AM", "1_AMMEC_page-0001.jpg");
		agregar("1°", "PRO", "AM", "1_AMPRO_page-0001.jpg");
		agregar("1°", "SMEC", "AM", "1_AMSMEC_page-0001.jpg");
		agregar("1°", "LOG", "AM", "1-AMLOG_page-0001.jpg");

		// Horarios 1ro turno vespertino
		agregar("1°", "ADMRH", "AV", "1_AVADMRH_page-0001.jpg");
		agregar("1°", "ADMRH", "BV", "1_BMADMRH_page-0001.jpg");
		agregar("1°", "ELE", "AV", "1-AVELE_page-0001.jpg");
		agregar("1°", "MAU", "AV", "1_AVMAU_page-0001.jpg");
		agregar("1°", "MEC", "BV", "1_BVMEC_page-0001.jpg");
		agregar("1°", "PRO", "AV", "1_AVPRO_page-0001.jpg");
		agregar("1°", "SMEC", "AV", "1-AVSMEC_page-0001.jpg");
		agregar("1°", "LOG", "AV", "1_AVLOG_page-0001.jpg");
		
		// Horarios 3ro turno matutino
				agregar("1°", "ADMRH", "AM", "1_AMADMRH_page-0001.jpg");
				agregar("1°", "ADMRH", "BM", "1_BMADMRH_page-0001.jpg");
				agregar("1°", "ADMRH", "CM", "1-CMADMRH_page-0001.jpg");
				agregar("1°", "ELE", "AM", "1_AMELE_page-0001.jpg");
				agregar("1°", "MAU", "AM", "1_AMMAU_page-0001.jpg");
				agregar("1°", "MEC", "AM", "1_AMMEC_page-0001.jpg");
				agregar("1°", "PRO", "AM", "1_AMPRO_page-0001.jpg");
				agregar("1°", "SMEC", "AM", "1_AMSMEC_page-0001.jpg");
				agregar("1°", "LOG", "AM", "1-AMLOG_page-0001.jpg");

				// Horarios 3ro turno vespertino
				agregar("1°", "ADMRH", "AV", "1_AVADMRH_page-0001.jpg");
				agregar("1°", "ADMRH", "BV", "1_BMADMRH_page-0001.jpg");
				agregar("1°", "ELE", "AV", "1-AVELE_page-0001.jpg");
				agregar("1°", "MAU", "AV", "1_AVMAU_page-0001.jpg");
				agregar("1°", "MEC", "BV", "1_BVMEC_page-0001.jpg");
				agregar("1°", "PRO", "AV", "1_AVPRO_page-0001.jpg");
				agregar("1°", "SMEC", "AV", "1-AVSMEC_page-0001.jpg");
				agregar("1°", "LOG", "AV", "1_AVLOG_page-0001.jpg");

	}

	// -------- ACTUALIZAR HORARIO --------
	private void actualizarHorario() {

		String semestre = (String) comboBox_semestre.getSelectedItem();
		String grupo = (String) comboBox_grupo.getSelectedItem();
		String turno = (String) comboBox_turno.getSelectedItem();

		if (semestre.contains("Seleccione") || grupo.contains("Seleccione") || turno.contains("Seleccione")) {

			imagenLabel.setIcon(null);
			return;
		}

		String archivo = mapaHorarios.get(new HorarioKey(semestre, grupo, turno));

		if (archivo == null) {
			imagenLabel.setIcon(null);
			JOptionPane.showMessageDialog(this, "No se encontró el horario seleccionado.");
			return;
		}

		cargarImagen(archivo);
	}

	// -------- CARGAR IMAGEN --------
	private void cargarImagen(String archivo) {

		for (String base : rutasBase) {
			File f = new File(base + archivo);

			if (f.exists()) {
				ImageIcon original = new ImageIcon(f.getAbsolutePath());

				Image img = original.getImage().getScaledInstance(imagenLabel.getWidth(), imagenLabel.getHeight(),
						Image.SCALE_SMOOTH);

				imagenLabel.setIcon(new ImageIcon(img));
				return;
			}
		}

		JOptionPane.showMessageDialog(this, "Imagen no encontrada en las rutas disponibles.");
	}

}
