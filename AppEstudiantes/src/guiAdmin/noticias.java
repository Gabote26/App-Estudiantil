package guiAdmin;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JButton;

public class noticias extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtNoti1;
	private JTextField txtNoti2;
	private JTextField txtNoti3;
	private JTextField txtNoti4;
	public String Noti1 = "";
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					noticias frame = new noticias();
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
	public noticias() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 858, 565);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		txtNoti1 = new JTextField();
		txtNoti1.setBounds(10, 60, 321, 47);
		contentPane.add(txtNoti1);
		txtNoti1.setColumns(10);
		
		txtNoti2 = new JTextField();
		txtNoti2.setColumns(10);
		txtNoti2.setBounds(10, 136, 321, 47);
		contentPane.add(txtNoti2);
		
		txtNoti3 = new JTextField();
		txtNoti3.setColumns(10);
		txtNoti3.setBounds(10, 206, 321, 47);
		contentPane.add(txtNoti3);
		
		txtNoti4 = new JTextField();
		txtNoti4.setColumns(10);
		txtNoti4.setBounds(10, 278, 321, 47);
		contentPane.add(txtNoti4);
		
		JButton btnNoti = new JButton("SUBIR NOTICIAS");
		btnNoti.setBounds(428, 60, 208, 47);
		contentPane.add(btnNoti);
	/*	btnNoti.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			Noti1 = txtNoti1.getText();
			
			
			
			
			
			}
			
		}); */
	}
	
	public void NOti1() {
		Noti1 = txtNoti1.getText();
		
		
		
	}
	
	
	
	
}
