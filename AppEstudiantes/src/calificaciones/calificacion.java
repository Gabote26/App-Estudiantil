package calificaciones;

import javax.swing.table.DefaultTableModel;
import java.awt.EventQueue;

import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.Color;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

public class calificacion extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table;
	private JLabel lblNewLabel_1;
	private Point posicionFija;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					calificacion frame = new calificacion();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public calificacion() {
		setBackground(new Color(0, 0, 0));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 300, 450, 260);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(255, 255, 255));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setResizable(false);
		setLocationRelativeTo(null);

		posicionFija = getLocation();

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentMoved(ComponentEvent e) {
				setLocation(posicionFija);
			}
		});

		JLabel lblNewLabel = new JLabel("CALIFICACIONES");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		lblNewLabel.setBounds(10, 11, 414, 14);
		contentPane.add(lblNewLabel);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 36, 414, 155);
		contentPane.add(scrollPane);

		table = new JTable();
		scrollPane.setViewportView(table);
		table.setFillsViewportHeight(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		Double es1, pen1, len1, in1, ec1, hum1, so1, p1, sum1;
		Double es2, pen2, len2, in2, ec2, hum2, so2, p2, sum2;
		Double es3, pen3, len3, in3, ec3, hum3, so3, p3, sum3, pf;

		es1 = 9.0;
		pen1 = 10.0;
		len1 = 9.0;
		in1 = 10.0;
		ec1 = 5.0;
		hum1 = 10.0;
		so1 = 10.0;

		sum1 = es1 + pen1 + len1 + in1 + ec1 + hum1 + so1;
		p1 = sum1 / 7;

		es2 = 9.0;
		pen2 = 10.0;
		len2 = 9.0;
		in2 = 10.0;
		ec2 = 9.0;
		hum2 = 10.0;
		so2 = 10.0;

		sum2 = es2 + pen2 + len2 + in2 + ec2 + hum2 + so2;
		p2 = sum2 / 7;

		es3 = 9.0;
		pen3 = 10.0;
		len3 = 9.0;
		in3 = 10.0;
		ec3 = 8.0;
		hum3 = 10.0;
		so3 = 10.0;

		sum3 = es3 + pen3 + len3 + in3 + ec3 + hum3 + so3;
		p3 = sum3 / 7;

		pf = (p1 + p2 + p3) / 3;

		String sp1 = String.format("%.2f", p1);
		String sp2 = String.format("%.2f", p2);
		String sp3 = String.format("%.2f", p3);

		DefaultTableModel model = new DefaultTableModel(new Object[][] { { "Especialidad", es1, es2, es3 },
				{ "Pensamiento matemáticas", pen1, pen2, pen3 }, { "Lengua y Comunicacion", len1, len2, len3 },
				{ "Ingles", in1, in2, in3 }, { "Ecosisitemas", ec1, ec2, ec3 }, { "Humanidades", hum1, hum2, hum3 },
				{ "Socioemocionales", so1, so2, so3 }, { "Promedio", sp1, sp2, sp3 },

		}, new String[] { "Materia", "Parcial 1", "Parcial 2", "Parcial 3" });

		table.setModel(model);

		lblNewLabel_1 = new JLabel("Promedio final: " + String.format("%.2f", pf));
		lblNewLabel_1.setFont(new Font("Times New Roman", Font.PLAIN, 12));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setForeground(new Color(255, 0, 0));
		lblNewLabel_1.setBackground(new Color(255, 255, 255));
		lblNewLabel_1.setBounds(10, 196, 414, 14);
		contentPane.add(lblNewLabel_1);

	}
}
