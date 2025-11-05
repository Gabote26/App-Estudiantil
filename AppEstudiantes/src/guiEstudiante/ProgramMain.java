package guiEstudiante;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import utils.RoundedButton;

import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class ProgramMain extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ProgramMain frame = new ProgramMain();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ProgramMain() {
		setTitle("Gestion de Estudiante ${user}");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 795, 595);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setForeground(new Color(255, 255, 255));
		contentPane.setBackground(new Color(255, 255, 255));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel title = new JLabel("ESTUDIANTE");
		title.setForeground(new Color(0, 0, 0));
		title.setBounds(10, 1, 115, 52);
		title.setFont(new Font("Tahoma", Font.BOLD, 16));
		contentPane.add(title);

		RoundedButton btnNewButton = new RoundedButton("New button", 20);
		btnNewButton.setBackground(new Color(128, 255, 128));
		btnNewButton.setText("NOTICIAS");
		btnNewButton.setBounds(123, 10, 115, 39);
		contentPane.add(btnNewButton);

		JButton btnNewButton_1 = new RoundedButton("New button", 20);
		btnNewButton_1.setText("CALIFICACIONES");
		btnNewButton_1.setBounds(248, 10, 115, 39);
		contentPane.add(btnNewButton_1);

		JButton btnNewButton_2 = new RoundedButton("New button", 20);
		btnNewButton_2.setText("FALTAS");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnNewButton_2.setBounds(373, 10, 125, 39);
		contentPane.add(btnNewButton_2);

		JButton btnNewButton_3 = new RoundedButton("New button", 20);
		btnNewButton_3.setText("MATERIAS REPROBADAS");
		btnNewButton_3.setBounds(508, 10, 137, 39);
		contentPane.add(btnNewButton_3);

		JButton btnNewButton_4 = new RoundedButton("New button", 20);
		btnNewButton_4.setText("HORARIOS");
		btnNewButton_4.setBounds(655, 10, 116, 39);
		contentPane.add(btnNewButton_4);

		// Horarios(switch case)

		// NO BORRAR
		ImageIcon original = new ImageIcon("resources/calendario_example.png");
		Image imagenEscalada = original.getImage().getScaledInstance(274, 295, Image.SCALE_SMOOTH);

		JLabel lblIcon = new JLabel(new ImageIcon(imagenEscalada));
		lblIcon.setBounds(495, 90, 274, 295);
		contentPane.add(lblIcon);

		// .....................................................................................

	}
}
