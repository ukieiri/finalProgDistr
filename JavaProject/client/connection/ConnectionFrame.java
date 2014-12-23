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

public class ConnectionFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField address = new JTextField("192.168.1.103");
	private JTextField port = new JTextField("45000");
	private JTextField username = new JTextField("Fitz");
	private JTextField password = new JPasswordField("password");

	private JButton connect = new JButton("Connect");

	private Client client;

	public ConnectionFrame(Client client) {
		super("Connection to server :");
		this.client = client;

		setDefaultCloseOperation(EXIT_ON_CLOSE);

		setLocationRelativeTo(null);
		setResizable(false);
		setLayout(new GridLayout(5, 2, 5, 5));

		add(new JLabel("Hostname : "));
		add(address);

		add(new JLabel("Port : "));
		add(port);

		add(new JLabel("Username : "));
		add(username);

		add(new JLabel("Password : "));
		add(password);

		add(new JLabel());
		add(connect);

		connect.addActionListener(new ConnectListener());
		setSize(410, 256);
		setVisible(true);
	}

	class ConnectListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			client.connect(address.getText(), port.getText(),
					username.getText(), password.getText());
		}
	}
}
