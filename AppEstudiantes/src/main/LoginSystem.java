package main;

import db.ConexionMysql;
import guiAdmin.MainForAdmin;
import guiEstudiante.ProgramMain;
import guiProfesor.MainForTeachers;
import utils.RoundedButton;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginSystem extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel container;
	private JTextField userInput;
	private JPasswordField passwordInput;
	private final ConexionMysql connectionDB = new ConexionMysql();

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				LoginSystem frame = new LoginSystem();
				frame.setVisible(true);
				frame.setResizable(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public LoginSystem() {
		setTitle("Sistema de Inicio de Sesión");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 478, 432);
		setLocationRelativeTo(null);

		container = new JPanel();
		container.setForeground(Color.WHITE);
		container.setBackground(new Color(42, 34, 71));
		container.setBorder(new EmptyBorder(5, 5, 5, 5));
		container.setLayout(null);
		setContentPane(container);

		// Etiquetas
		JLabel titleLogin = new JLabel("INICIAR SESIÓN");
		titleLogin.setBounds(138, 59, 174, 23);
		titleLogin.setFont(new Font("Roboto", Font.BOLD, 23));
		titleLogin.setForeground(Color.WHITE);
		container.add(titleLogin);

		JLabel loginInformation = new JLabel("Ingresa tu usuario y contraseña para entrar al sistema");
		loginInformation.setFont(new Font("MS Gothic", Font.ITALIC, 12));
		loginInformation.setForeground(Color.WHITE);
		loginInformation.setBounds(52, 92, 360, 23);
		container.add(loginInformation);

		// Placeholders para los inputs
		String placeholderUser = "Usuario (No. de Control)";
		String placeholderPassword = "Contraseña";

		// Campo usuario
		userInput = new JTextField(placeholderUser);
		userInput.setBackground(new Color(218, 242, 245));
		userInput.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 10));
		userInput.setForeground(Color.GRAY);
		userInput.setBounds(125, 140, 198, 23);
		container.add(userInput);

		userInput.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (userInput.getText().equals(placeholderUser)) {
					userInput.setText("");
					userInput.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (userInput.getText().isEmpty()) {
					userInput.setText(placeholderUser);
					userInput.setForeground(Color.GRAY);
				}
			}
		});

		// Campo de la contraseña
		passwordInput = new JPasswordField(placeholderPassword, 20);
		passwordInput.setBackground(new Color(218, 242, 245));
		passwordInput.setForeground(Color.GRAY);
		passwordInput.setBounds(125, 173, 198, 23);
		passwordInput.setEchoChar((char) 0);
		container.add(passwordInput);

		char defaultEchoChar = new JPasswordField().getEchoChar();

		passwordInput.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				String currentText = new String(passwordInput.getPassword());
				if (currentText.equals(placeholderPassword)) {
					passwordInput.setText("");
					passwordInput.setForeground(Color.BLACK);
					passwordInput.setEchoChar(defaultEchoChar);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				String currentText = new String(passwordInput.getPassword());
				if (currentText.isEmpty()) {
					passwordInput.setText(placeholderPassword);
					passwordInput.setForeground(Color.GRAY);
					passwordInput.setEchoChar((char) 0);
				}
			}
		});

		// Botón de inicio de sesión
		JButton loginBtn = new RoundedButton("Iniciar Sesión", 20);
		loginBtn.setForeground(Color.WHITE);
		loginBtn.setFont(new Font("Tahoma", Font.PLAIN, 13));
		loginBtn.setBackground(new Color(173, 19, 74));
		loginBtn.setBounds(169, 228, 115, 33);
		container.add(loginBtn);

		loginBtn.addActionListener(e -> iniciarSesion(placeholderUser, placeholderPassword));

		// Botón de recuperación de contraseña (Implementar)
		RoundedButton changePasswordBtn = new RoundedButton("Recuperar Contraseña", 20);
		changePasswordBtn.setForeground(Color.WHITE);
		changePasswordBtn.setFont(new Font("Tahoma", Font.PLAIN, 13));
		changePasswordBtn.setBackground(new Color(40, 153, 140));
		changePasswordBtn.setBounds(149, 271, 153, 28);
		container.add(changePasswordBtn);

		changePasswordBtn.addActionListener(
				e -> JOptionPane.showMessageDialog(this, "Para resetear la contraseña, contacte al administrador",
						"Información", JOptionPane.INFORMATION_MESSAGE));

		JLabel lblVersion = new JLabel("v1.0.0");
		lblVersion.setForeground(new Color(238, 238, 238));
		lblVersion.setBounds(417, 373, 37, 12);
		container.add(lblVersion);

	}

	/**
	 * Método que maneja el proceso de inicio de sesión.
	 */
	private void iniciarSesion(String placeholderUser, String placeholderPassword) {
		String user = userInput.getText();
		String password = new String(passwordInput.getPassword());

		// Validacion de campos vacíos o placeholders para evitar inconsistencias
		if (user.equals(placeholderUser) || password.equals(placeholderPassword) || user.isBlank()
				|| password.isBlank()) {
			JOptionPane.showMessageDialog(this, "¡DEBE COMPLETAR TODOS LOS CAMPOS!");
			return;
		}

		// Validar conexión con la base de datos
		Connection cn = connectionDB.conectar();
		if (cn == null) {
			JOptionPane.showMessageDialog(this, "No se pudo establecer conexión con la base de datos.",
					"Error de conexión", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Consulta de datos SQL en la base de datos
		String query = "SELECT nombre, apellido, role, no_control FROM usuarios WHERE email = ? AND password = ?";

		try (PreparedStatement ps = cn.prepareStatement(query)) {
			ps.setString(1, user);
			ps.setString(2, password);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					String nombreUsuario = rs.getString("nombre");
					String apellidoUsuario = rs.getString("apellido");
					String assignedRole = rs.getString("role");
					String numControl = rs.getString("no_control");

					dispose();

					// Abrir ventana correspondiente dependiendo del rol del usuario
					if ("PROFESOR".equalsIgnoreCase(assignedRole)) {
						new MainForTeachers(nombreUsuario).setVisible(true); // Ventana del profesor
					} else if ("ESTUDIANTE".equalsIgnoreCase(assignedRole)) {
						new ProgramMain(numControl, nombreUsuario, apellidoUsuario).setVisible(true); // Ventana de usuarios
					} else if ("ADMIN".equalsIgnoreCase(assignedRole)) {
						new MainForAdmin(nombreUsuario).setVisible(true); // Panel de administrador
					} else {
						JOptionPane.showMessageDialog(this, "Rol desconocido: " + assignedRole, "Advertencia",
								JOptionPane.WARNING_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(this, "¡USUARIO O CONTRASEÑA INCORRECTOS!");
				}
			}

		} catch (SQLException err) {
			JOptionPane.showMessageDialog(this, "ERROR AL INICIAR SESIÓN:\n" + err.getMessage(), "Error SQL",
					JOptionPane.ERROR_MESSAGE);
		} finally {
			try {
				cn.close();
			} catch (SQLException ignore) {
			}
		}
	}
}
