package guiProfesor;

import javax.swing.*;
import calificaciones.CalificacionesBase;
import modelos.Calificacion;
import java.awt.*;

public class GestionarCalificaciones extends JFrame {

	private JTextField txtLengua, txtHumanidades, txtMatematicas, txtSociales, txtCiencias;
	private final CalificacionesBase califDAO = new CalificacionesBase();
	private final String numControl;

	public GestionarCalificaciones(String numControl) {
		this.numControl = numControl;

		setTitle("Gestión de Calificaciones - Alumno: " + numControl);
		setSize(450, 400);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);

		JLabel label = new JLabel("Lengua:");
		label.setBounds(22, 21, 107, 36);
		getContentPane().add(label);
		txtLengua = new JTextField();
		txtLengua.setBounds(192, 21, 213, 36);
		getContentPane().add(txtLengua);

		JLabel label_1 = new JLabel("Humanidades:");
		label_1.setBounds(22, 67, 107, 36);
		getContentPane().add(label_1);
		txtHumanidades = new JTextField();
		txtHumanidades.setBounds(192, 67, 213, 36);
		getContentPane().add(txtHumanidades);

		JLabel label_2 = new JLabel("Matemáticas:");
		label_2.setBounds(22, 113, 107, 36);
		getContentPane().add(label_2);
		txtMatematicas = new JTextField();
		txtMatematicas.setBounds(192, 113, 213, 36);
		getContentPane().add(txtMatematicas);

		JLabel label_3 = new JLabel("Sociales:");
		label_3.setBounds(22, 159, 107, 36);
		getContentPane().add(label_3);
		txtSociales = new JTextField();
		txtSociales.setBounds(192, 159, 213, 36);
		getContentPane().add(txtSociales);

		JLabel label_4 = new JLabel("Ciencias:");
		label_4.setBounds(22, 205, 107, 36);
		getContentPane().add(label_4);
		txtCiencias = new JTextField();
		txtCiencias.setBounds(192, 205, 213, 36);
		getContentPane().add(txtCiencias);

		JButton btnGuardar = new JButton("💾 Guardar Cambios");
		btnGuardar.setBounds(230, 276, 175, 36);
		JButton btnCargar = new JButton("📥 Cargar Existentes");
		btnCargar.setBounds(33, 276, 175, 36);
		getContentPane().add(btnCargar);
		getContentPane().add(btnGuardar);

		btnCargar.addActionListener(e -> cargarCalificaciones());
		btnGuardar.addActionListener(e -> guardarCalificaciones());
	}

	private void cargarCalificaciones() {
		Calificacion c = califDAO.getCalificaciones(numControl);
		if (c == null) {
			JOptionPane.showMessageDialog(this, "No hay calificaciones registradas. Puedes subir nuevas.");
			limpiarCampos();
		} else {
			txtLengua.setText(String.valueOf(c.getCalfLengua()));
			txtHumanidades.setText(String.valueOf(c.getCalfHumanidades()));
			txtMatematicas.setText(String.valueOf(c.getCalfMatematicas()));
			txtSociales.setText(String.valueOf(c.getCalfSociales()));
			txtCiencias.setText(String.valueOf(c.getCalfCiencias()));
		}
	}

	private void guardarCalificaciones() {
		try {
			double lengua = Double.parseDouble(txtLengua.getText());
			double humanidades = Double.parseDouble(txtHumanidades.getText());
			double matematicas = Double.parseDouble(txtMatematicas.getText());
			double sociales = Double.parseDouble(txtSociales.getText());
			double ciencias = Double.parseDouble(txtCiencias.getText());

			if (!esValido(lengua) || !esValido(humanidades) || !esValido(matematicas) || !esValido(sociales)
					|| !esValido(ciencias)) {
				JOptionPane.showMessageDialog(this, "Las calificaciones deben estar entre 0 y 100.");
				return;
			}

			Calificacion existente = califDAO.getCalificaciones(numControl);
			Calificacion nueva = new Calificacion(numControl, lengua, humanidades, matematicas, sociales, ciencias);

			boolean exito;
			if (existente == null)
				exito = califDAO.insertarCalificaciones(nueva);
			else
				exito = califDAO.actualizarCalificaciones(nueva);

			if (exito)
				JOptionPane.showMessageDialog(this, "✅ Calificaciones guardadas correctamente.");
			else
				JOptionPane.showMessageDialog(this, "❌ No se pudieron guardar las calificaciones.");

		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Introduce valores numéricos válidos.");
		}
	}

	private boolean esValido(double v) {
		return v >= 0 && v <= 100;
	}

	private void limpiarCampos() {
		txtLengua.setText("");
		txtHumanidades.setText("");
		txtMatematicas.setText("");
		txtSociales.setText("");
		txtCiencias.setText("");
	}
}
