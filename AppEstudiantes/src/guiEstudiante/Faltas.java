package guiEstudiante;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import db.ConexionMysql;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JTextField;

public class Faltas extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtFecha, txtTipo, txtObs;
	private JTable tabla;
	private DefaultTableModel modelo;
	private final String archivo = "registros.csv";
	private JTextField txtBuscar;
	private final String numControl;
	private final ConexionMysql connectionDB = new ConexionMysql();

	public Faltas(String numControl) {
		this.numControl = numControl;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 513);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(0, 0, 0));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("Registro de Inasistencias");
		lblNewLabel.setFont(new Font("Times New Roman", Font.BOLD, 23));
		lblNewLabel.setForeground(new Color(255, 255, 255));
		lblNewLabel.setBounds(216, 49, 297, 48);
		contentPane.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Ingrese el nombre completo del alumno");
		lblNewLabel_1.setFont(new Font("Times New Roman", Font.PLAIN, 18));
		lblNewLabel_1.setForeground(new Color(255, 255, 255));
		lblNewLabel_1.setBounds(66, 123, 312, 21);
		contentPane.add(lblNewLabel_1);

		txtBuscar = new JTextField();
		txtBuscar.setBounds(58, 161, 568, 25);
		contentPane.add(txtBuscar);

		JButton btnBuscar = new JButton("Buscar");
		btnBuscar.setBounds(278, 209, 100, 25);
		contentPane.add(btnBuscar);

		modelo = new DefaultTableModel(new String[] { "Fecha", "Lengua", "Humanidades", "Matematicas", "Sociales", "Ciencias" }, 0);
		tabla = new JTable(modelo);
		JScrollPane scrollPane = new JScrollPane(tabla);
		scrollPane.setBounds(33, 267, 620, 170);
		contentPane.add(scrollPane);
		btnBuscar.addActionListener(e -> buscarRegistros());
	}

	private void buscarRegistros() {
		modelo.setRowCount(0);
		try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
			String linea;
			while ((linea = br.readLine()) != null) {
				String[] datos = linea.split(",");
				if (datos[0].equalsIgnoreCase(txtBuscar.getText())) {
					modelo.addRow(datos);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void cargarDatosEstudiante() {
		// Faltas
		modelo.setRowCount(0);
		String queryFaltas = "SELECT f.faltas_lengua, f.faltas_humanidades, f.faltas_matematicas, "
				+ "f.faltas_sociales, f.faltas_ciencias " + "FROM faltas f "
				+ "JOIN usuarios u ON u.no_control = f.num_control " + "WHERE f.num_control = ?";

		try (Connection cn = connectionDB.conectar(); PreparedStatement ps = cn.prepareStatement(queryFaltas)) {

			ps.setString(1, numControl);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					int lengua = rs.getInt("faltas_lengua");
					int humanidades = rs.getInt("faltas_humanidades");
					int matematicas = rs.getInt("faltas_matematicas");
					int sociales = rs.getInt("faltas_sociales");
					int ciencias = rs.getInt("faltas_ciencias");

					// double promedio = (lengua + humanidades + matematicas + sociales + ciencias)
					// / 5.0;
					// String estado = (promedio >= 6.0) ? "Aprobado" : "Reprobado";

					Object[] filaCalificaciones = { lengua, humanidades, matematicas, sociales, ciencias, "Test" };
					modelo.addRow(filaCalificaciones);
				}
			}

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Error al cargar las faltas:\n" + e.getMessage(), "Error SQL",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
}
