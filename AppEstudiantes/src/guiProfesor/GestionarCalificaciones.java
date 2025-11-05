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
		setSize(400, 400);
		setLocationRelativeTo(null);
		setLayout(new GridLayout(8, 2, 10, 10));

		add(new JLabel("Lengua:"));
		txtLengua = new JTextField();
		add(txtLengua);

		add(new JLabel("Humanidades:"));
		txtHumanidades = new JTextField();
		add(txtHumanidades);

		add(new JLabel("Matemáticas:"));
		txtMatematicas = new JTextField();
		add(txtMatematicas);

		add(new JLabel("Sociales:"));
		txtSociales = new JTextField();
		add(txtSociales);

		add(new JLabel("Ciencias:"));
		txtCiencias = new JTextField();
		add(txtCiencias);

		JButton btnGuardar = new JButton("💾 Guardar Cambios");
		JButton btnCargar = new JButton("📥 Cargar Existentes");
		add(btnCargar);
		add(btnGuardar);

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
