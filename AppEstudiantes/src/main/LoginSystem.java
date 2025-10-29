package main;

import utils.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class LoginSystem extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel container;
	private JTextField userInput;
	private JPasswordField passwordInput;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginSystem frame = new LoginSystem();
					frame.setVisible(true);
					frame.setResizable(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public LoginSystem() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 478, 432);
		container = new JPanel();
		container.setForeground(new Color(255, 255, 255));
		container.setBackground(new Color(42, 34, 71));
		container.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(container);
		container.setLayout(null);
		
		
		// Elementos de la interfáz.
		JLabel titleLogin = new JLabel("INICIAR SESIÓN");
		titleLogin.setBounds(138, 59, 174, 23);
		titleLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
		titleLogin.setFont(new Font("Roboto", Font.BOLD, 23));
		titleLogin.setForeground(new Color(255, 255, 255));
		container.add(titleLogin);
		
		JLabel loginInformation = new JLabel("Ingresa tu usuario y contraseña para entrar al sistema");
		loginInformation.setFont(new Font("MS Gothic", Font.ITALIC, 12));
		loginInformation.setForeground(new Color(255, 255, 255));
		loginInformation.setBounds(52, 92, 360, 23);
		container.add(loginInformation);
		
		// Inputs de datos recibidos
		String placeholderUser = "Usuario (No. de Control)";
		String placeholderPassword = "Contraseña";
		
		userInput = new JTextField();
		userInput.setBackground(new Color(218, 242, 245));
		userInput.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 10));
		userInput.setText(placeholderUser);
		userInput.setBounds(125, 140, 198, 23);
		container.add(userInput);
		userInput.setColumns(10);
		userInput.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                // Si el texto es el placeholder, lo borra
                if (userInput.getText().equals(placeholderUser)) {
                	userInput.setText("");
                	userInput.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                // Si está vacío al perder foco, se restaura el placeholder
                if (userInput.getText().isEmpty()) {
                	userInput.setText(placeholderUser);
                	userInput.setForeground(Color.GRAY);
                }
            }
        });
		
		passwordInput = new JPasswordField(20);
		char echoChar = passwordInput.getEchoChar();
		passwordInput.setBackground(new Color(218, 242, 245));
		passwordInput.setBounds(125, 173, 198, 23);
		passwordInput.setText(placeholderPassword);
		passwordInput.setEchoChar((char) 0);
		container.add(passwordInput);
		passwordInput.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                // Si tiene el placeholder, limpiar y volver a activar el echo char
                String currentText = new String(passwordInput.getPassword());
                if (currentText.equals(placeholderPassword)) {
                	passwordInput.setText("");
                	passwordInput.setForeground(Color.BLACK);
                	passwordInput.setEchoChar(echoChar); // activa ocultamiento
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                // Si está vacío al perder foco, restaurar placeholder
                String currentText = new String(passwordInput.getPassword());
                if (currentText.isEmpty()) {
                	passwordInput.setText(placeholderPassword);
                	passwordInput.setForeground(Color.GRAY);
                	passwordInput.setEchoChar((char) 0); // muestra texto plano
                }
            }
        });
		
		// Botones de utilidades
		JButton loginBtn = new RoundedButton("Iniciar Sesión", 20);
		loginBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Login Default Message");
			}
		});
		loginBtn.setForeground(new Color(255, 255, 255));
		loginBtn.setFont(new Font("Tahoma", Font.PLAIN, 13));
		loginBtn.setBackground(new Color(173, 19, 74));
		loginBtn.setBounds(169, 228, 115, 33);
		container.add(loginBtn);
		
		RoundedButton changePasswordBtn = new RoundedButton("Recuperar Contraseña", 20);
		changePasswordBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Para resetear la contraseña, contacte al administrador", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
		changePasswordBtn.setForeground(Color.WHITE);
		changePasswordBtn.setFont(new Font("Tahoma", Font.PLAIN, 13));
		changePasswordBtn.setBackground(new Color(40, 153, 140));
		changePasswordBtn.setBounds(149, 271, 153, 28);
		container.add(changePasswordBtn);

	}
}
