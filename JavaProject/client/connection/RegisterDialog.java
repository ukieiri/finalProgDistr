package connection;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class RegisterDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3745949287660843218L;
	JTextField password = new JPasswordField();

	public RegisterDialog() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Register new User : ");
		setLocationRelativeTo(null);
		setResizable(false);
		setLayout(new GridLayout(1, 2, 5, 5));

		JButton save = new JButton("Send");
		add(new JLabel("Retype Password : "));
		add(password);

		add(save);

		save.addActionListener(new Button_Click());
		setSize(410, 64);
	}

	public String showModal() {

		setModalityType(DEFAULT_MODALITY_TYPE);
		setVisible(true);

		dispose();
		return password.getText();
	}

	class Button_Click implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			setVisible(false);

		}
	}
}
