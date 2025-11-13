package guiEstudiante;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import calificaciones.calificacion;
import db.ConexionMysql;
import utils.RoundedButton;

import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;
import java.awt.event.ActionEvent;

public class ProgramMain extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private final ConexionMysql connectionDB = new ConexionMysql();
	private final String numControl, nombre, apellido;

	public ProgramMain(String numControl, String nombre, String apellido) {
		this.numControl = numControl;
		this.nombre = nombre;
		this.apellido = apellido;
		setTitle("Estudiante - " + nombre + " " + apellido);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 950, 595);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setForeground(new Color(255, 255, 255));
		contentPane.setBackground(new Color(255, 255, 255));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		RoundedButton btnNewButton = new RoundedButton("New button", 20);
		btnNewButton.setBackground(new Color(128, 255, 128));
		btnNewButton.setText("NOTICIAS");
		btnNewButton.setBounds(123, 10, 115, 39);
		contentPane.add(btnNewButton);

		JButton btnNewButton_1 = new RoundedButton("New button", 20);
		btnNewButton_1.setText("CALIFICACIONES");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				calificacion winCalif = new calificacion();
				winCalif.setVisible(true);
			}
		});
		btnNewButton_1.setBounds(248, 10, 115, 39);
		contentPane.add(btnNewButton_1);

		JButton btnNewButton_2 = new RoundedButton("New button", 20);
		btnNewButton_2.setText("FALTAS");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Faltas ventanaFaltas = new Faltas(numControl);
				ventanaFaltas.setVisible(true);			}
		});
		btnNewButton_2.setBounds(373, 10, 125, 39);
		contentPane.add(btnNewButton_2);

		JButton btnNewButton_3 = new RoundedButton("New button", 20);
		btnNewButton_3.setText("MATERIAS REPROBADAS");
		btnNewButton_3.setBounds(508, 10, 185, 39);
		contentPane.add(btnNewButton_3);

		JButton btnNewButton_4 = new RoundedButton("🗓️ HORARIOS", 20);
		btnNewButton_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				HorariosEstudiantes ventanaHorarios = new HorariosEstudiantes();
				ventanaHorarios.setVisible(true);
			}
		});
		btnNewButton_4.setBounds(700, 10, 116, 39);
		contentPane.add(btnNewButton_4);

		ImageIcon original = new ImageIcon("resources/calendario_example.png");
		Image imagenEscalada = original.getImage().getScaledInstance(274, 295, Image.SCALE_SMOOTH);

		JLabel lblIcon = new JLabel(new ImageIcon(imagenEscalada));
		lblIcon.setBounds(495, 90, 274, 295);
		contentPane.add(lblIcon);

		// .....................................................................................

	}
}
