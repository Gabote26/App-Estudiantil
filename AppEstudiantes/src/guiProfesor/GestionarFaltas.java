package guiProfesor;

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
