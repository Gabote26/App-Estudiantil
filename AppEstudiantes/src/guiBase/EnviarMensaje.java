package guiBase;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class EnviarMensaje extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtSearch;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EnviarMensaje frame = new EnviarMensaje();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public EnviarMensaje() {
		setType(Type.UTILITY);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(1000, 530);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel titleSendMsg = new JLabel("📣 Enviar Mensaje");
		titleSendMsg.setBounds(137, 10, 182, 43);
		titleSendMsg.setFont(new Font("Segoe UI Emoji", Font.BOLD, 21));
		contentPane.add(titleSendMsg);
		
		JLabel lblMsg = new JLabel("Escoje el tipo de mensaje:");
		lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblMsg.setBounds(36, 100, 170, 25);
		contentPane.add(lblMsg);
		
		JLabel lblDest = new JLabel("¿A quienes va dirijido el mensaje?");
		lblDest.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblDest.setBounds(36, 186, 226, 25);
		contentPane.add(lblDest);
		
		JComboBox selectType = new JComboBox();
		selectType.setBounds(324, 104, 119, 20);
		contentPane.add(selectType);
		
		JLabel lblContent = new JLabel("Contenido del mensaje:");
		lblContent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblContent.setBounds(36, 352, 160, 25);
		contentPane.add(lblContent);
		
		JTextArea msgContent = new JTextArea();
		msgContent.setFont(new Font("Calibri", Font.PLAIN, 13));
		msgContent.setToolTipText("Escibe el mensaje que deseas enviar en forma de anuncio");
		msgContent.setWrapStyleWord(true);
		msgContent.setLineWrap(true);
		msgContent.setBounds(234, 323, 219, 97);
		contentPane.add(msgContent);
		
		JRadioButton destOptUsers = new JRadioButton("Usuarios especificos");
		destOptUsers.setBounds(49, 264, 157, 20);
		contentPane.add(destOptUsers);
		
		JRadioButton destOptGroups = new JRadioButton("Grupos");
		destOptGroups.setBounds(49, 230, 89, 20);
		contentPane.add(destOptGroups);
		
		txtSearch = new JTextField();
		txtSearch.setToolTipText("Escribe un nombre o correo para buscar en el sistema");
		txtSearch.setBounds(234, 265, 219, 18);
		contentPane.add(txtSearch);
		txtSearch.setColumns(10);
		
		JComboBox selectGroups = new JComboBox();
		selectGroups.setToolTipText("Seleccione uno o más grupos");
		selectGroups.setBounds(302, 230, 141, 20);
		contentPane.add(selectGroups);
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setBackground(new Color(0, 0, 0));
		separator.setBounds(502, 10, 10, 473);
		contentPane.add(separator);
		
		JLabel titlePreview = new JLabel("👀 Vista Previa");
		titlePreview.setFont(new Font("Segoe UI Emoji", Font.BOLD, 21));
		titlePreview.setBounds(652, 10, 182, 43);
		contentPane.add(titlePreview);
		
		JLabel msgType = new JLabel("Tipo de mensaje:");
		msgType.setEnabled(false);
		msgType.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		msgType.setBounds(540, 100, 119, 25);
		contentPane.add(msgType);
		
		JLabel listDest = new JLabel("Destinatarios");
		listDest.setEnabled(false);
		listDest.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		listDest.setBounds(540, 186, 89, 25);
		contentPane.add(listDest);
		
		JLabel resultContent = new JLabel("Contenido del mensaje:");
		resultContent.setEnabled(false);
		resultContent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		resultContent.setBounds(540, 306, 160, 25);
		contentPane.add(resultContent);
		
		JTextPane resultMsgType = new JTextPane();
		resultMsgType.setEnabled(false);
		resultMsgType.setEditable(false);
		resultMsgType.setBounds(540, 131, 182, 18);
		contentPane.add(resultMsgType);
		
		JList list = new JList();
		list.setEnabled(false);
		list.setBounds(540, 221, 182, 75);
		contentPane.add(list);
		
		JTextPane resultMsg = new JTextPane();
		resultMsg.setEnabled(false);
		resultMsg.setEditable(false);
		resultMsg.setBounds(538, 352, 402, 131);
		contentPane.add(resultMsg);

	}
}
