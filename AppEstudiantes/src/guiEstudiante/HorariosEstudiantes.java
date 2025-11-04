package guiEstudiante;

import java.awt.EventQueue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;
import java.awt.Color;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicButtonListener;

public class HorariosEstudiantes extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	JTable jT;
	DefaultTableModel mt;
	JScrollPane scrol;
	private JTextField textField;
	private JList list;
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HorariosEstudiantes frame = new HorariosEstudiantes();
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
	public HorariosEstudiantes() 
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 663, 600);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(128, 128, 192));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Dime el grupo del cual quieras saber tu horario");
		lblNewLabel.setFont(new Font("Microsoft Himalaya", Font.PLAIN, 20));
		lblNewLabel.setBounds(20, 0, 271, 61);
		contentPane.add(lblNewLabel);
				
		mt = new DefaultTableModel();
		scrol = new JScrollPane();
		String ids[] = {"Lunes", "martes", "miercoles", "jueves", "viernes"}; 
		mt.setColumnIdentifiers(ids);
		jT = new JTable(mt);
		jT.setBounds(20, 187, 585, 252);
		scrol.setBounds(20, 187, 585, 252);
		scrol.setViewportView(jT);
		
		String ampro[] = {"Mate", "Lengua", "Historia", "Edu fisica", "geografia"};
		String ammec[] = {"Mate", "Lengua", "Historia", "Edu fisicia", "geografia"};
		
		contentPane.add(scrol);
		
		textField = new JTextField();
		textField.setBounds(20, 55, 139, 31);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JButton btnAceptar = new JButton("ACEPTAR");
		btnAceptar.setBounds(182, 55, 148, 31);
		contentPane.add(btnAceptar); 
		
		list = new JList(ammec);
		list.setBounds(372, 20, 117, 107);
		contentPane.add(list);
		list.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		
			
			
			
			
		});
		
	
      
      
    

		
		
		
		btnAceptar.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		       String dato = textField.getText();
		     switch (dato) {
		    	 case "1°AM-PRO":
		    		 System.out.println("xd");
		    		 mt.addRow(ampro);
		    	 case "2°AM-MEC": 
		    		 System.out.println("dx");
		    		
		    	default: 
		    		System.out.println("xddddd");
		       
		       
		     }
		    }
		});
		
		


   }
}