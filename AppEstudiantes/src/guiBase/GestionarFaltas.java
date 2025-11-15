package guiBase;

import javax.swing.*;
import faltas.FaltasBase;
import modelos.Faltas;
import java.awt.*;

public class GestionarFaltas extends JFrame {

	private JTextField txtLengua, txtHumanidades, txtMatematicas, txtSociales, txtCiencias;
	private final FaltasBase faltasDAO = new FaltasBase();
	private final String numControl;

	public GestionarFaltas(String numControl) {
		this.numControl = numControl;

		setTitle("Gestión de Faltas - Alumno: " + numControl);
		setSize(450, 400);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);

		JLabel label = new JLabel("Lengua:");
		label.setBounds(27, 22, 104, 36);
		getContentPane().add(label);
		txtLengua = new JTextField();
		txtLengua.setBounds(197, 22, 213, 36);
		getContentPane().add(txtLengua);

		JLabel label_1 = new JLabel("Humanidades:");
		label_1.setBounds(27, 68, 104, 36);
		getContentPane().add(label_1);
		txtHumanidades = new JTextField();
		txtHumanidades.setBounds(197, 68, 213, 36);
		getContentPane().add(txtHumanidades);

		JLabel label_2 = new JLabel("Matemáticas:");
		label_2.setBounds(27, 114, 104, 36);
		getContentPane().add(label_2);
		txtMatematicas = new JTextField();
		txtMatematicas.setBounds(197, 114, 213, 36);
		getContentPane().add(txtMatematicas);

		JLabel label_3 = new JLabel("Sociales:");
		label_3.setBounds(27, 160, 104, 36);
		getContentPane().add(label_3);
		txtSociales = new JTextField();
		txtSociales.setBounds(197, 160, 213, 36);
		getContentPane().add(txtSociales);

		JLabel label_4 = new JLabel("Ciencias:");
		label_4.setBounds(27, 206, 104, 36);
		getContentPane().add(label_4);
		txtCiencias = new JTextField();
		txtCiencias.setBounds(197, 206, 213, 36);
		getContentPane().add(txtCiencias);

		JButton btnGuardar = new JButton("💾 Guardar Cambios");
		btnGuardar.setBounds(232, 275, 175, 36);
		JButton btnCargar = new JButton("📥 Cargar Existentes");
		btnCargar.setBounds(32, 275, 175, 36);
		getContentPane().add(btnCargar);
		getContentPane().add(btnGuardar);

		btnCargar.addActionListener(e -> cargarFaltas());
		btnGuardar.addActionListener(e -> guardarFaltas());
	}

	private void cargarFaltas() {
		Faltas c = faltasDAO.getFaltas(numControl);
		if (c == null) {
			JOptionPane.showMessageDialog(this, "No hay faltas registradas. Puedes subir nuevas si es necesario.");
			limpiarCampos();
		} else {
			txtLengua.setText(String.valueOf(c.getFaltasLengua()));
			txtHumanidades.setText(String.valueOf(c.getFaltasHumanidades()));
			txtMatematicas.setText(String.valueOf(c.getFaltasMatematicas()));
			txtSociales.setText(String.valueOf(c.getFaltasSociales()));
			txtCiencias.setText(String.valueOf(c.getFaltasCiencias()));
		}
	}

	private void guardarFaltas() {
		try {
			int lengua = Integer.parseInt(txtLengua.getText());
			int humanidades = Integer.parseInt(txtHumanidades.getText());
			int matematicas = Integer.parseInt(txtMatematicas.getText());
			int sociales = Integer.parseInt(txtSociales.getText());
			int ciencias = Integer.parseInt(txtCiencias.getText());

			Faltas existente = faltasDAO.getFaltas(numControl);
			Faltas nueva = new Faltas(numControl, lengua, humanidades, matematicas, sociales, ciencias);

			boolean exito;
			if (existente == null)
				exito = faltasDAO.insertarFaltas(nueva);
			else
				exito = faltasDAO.actualizarFaltas(nueva);

			if (exito)
				JOptionPane.showMessageDialog(this, "✅ Faltas guardadas correctamente.");
			else
				JOptionPane.showMessageDialog(this, "❌ No se pudieron guardar las faltas.");

		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Introduce valores numéricos válidos.");
		}
	}

	private void limpiarCampos() {
		txtLengua.setText("");
		txtHumanidades.setText("");
		txtMatematicas.setText("");
		txtSociales.setText("");
		txtCiencias.setText("");
	}
}
