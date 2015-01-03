package connection;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import control.Client;

public class RegisterFrame extends JDialog {
	private static final long serialVersionUID = -3745949287660843218L;
	JTextField password = new JPasswordField();
	JTextField repassword = new JPasswordField();
	JTextField username = new JTextField();
	JTextField portHidden;
	JTextField addressHidden;
	private Client client;
	RegisterFrame frame;

	public RegisterFrame(Client client, String port, String address) {		
		this.client = client;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		//to keep values of port and address but hide them
				portHidden = new JTextField(port);
				addressHidden = new JTextField(address);
				portHidden.setVisible(false);
				addressHidden.setVisible(false);		
		
		setTitle("Register new User : ");
		setLocationRelativeTo(null);
		setResizable(false);		
		setLayout(new GridLayout(6, 2, 5, 5));
		add(portHidden);
		add(new JLabel());
		JButton register = new JButton("Register");
		add(new JLabel("Username : "));
		add(username);
		add(new JLabel("Password : "));
		add(password);
		add(new JLabel("Retype Password : "));
		add(repassword);
		add(new JLabel());
		add(register, BorderLayout.WEST);
		register.addActionListener(new Button_Click());
		add(addressHidden);		
		
		setSize(410, 220);
		setVisible(true);
	}

	class Button_Click implements ActionListener {
	
		@Override
		public void actionPerformed(ActionEvent e) {
			//check if passwords match
			if(!password.getText().equals(repassword.getText())){
				password.setText("");
				repassword.setText("");
				JOptionPane.showMessageDialog(frame, "Password does not match");
			}			
			else{
			try {
			 client.registerUser(username.getText(), password.getText(), repassword.getText(), portHidden.getText(), addressHidden.getText());
			} catch (ClassNotFoundException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}		
			setVisible(false);
			}
		}
	}
}
