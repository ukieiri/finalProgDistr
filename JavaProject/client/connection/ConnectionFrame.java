package connection;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import control.Client;
import connection.RegisterFrame;

public class ConnectionFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private JTextField address = new JTextField("192.168.108.12");
	private JTextField port = new JTextField("45000");
	private JTextField username = new JTextField("Uki");
	private JTextField password = new JPasswordField("7014085");

	private JButton connect = new JButton("Connect");
	private JButton register = new JButton("Register");
	private Client client;

	public ConnectionFrame(Client client) {
		super("Connection to server :");
		this.client = client;

		setDefaultCloseOperation(EXIT_ON_CLOSE);

		setLocationRelativeTo(null);
		setResizable(false);
		setLayout(new GridLayout(6, 2, 5, 5));

		add(new JLabel("Hostname : "));
		add(address);

		add(new JLabel("Port : "));
		add(port);

		add(new JLabel("Username : "));
		add(username);

		add(new JLabel("Password : "));
		add(password);

		add(new JLabel());
		add(register);
		
		add(new JLabel());
		add(connect);

		connect.addActionListener(new ConnectListener());
		register.addActionListener(new RegisterListener());
		setSize(410, 306);
		setVisible(true);
	}

	class ConnectListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			client.connect(address.getText(), port.getText(),
					username.getText(), password.getText());
		}
	}
	class RegisterListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			RegisterFrame frame = new RegisterFrame(client, port.getText(), address.getText());
			frame.pack();
			frame.setVisible(true);
		}
	}
}
