package chat;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import server.LinePainter;
import link.Message;
import control.Client;

public class MessagePanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3291759760244248689L;
	private String user;
	private JTextPane messagesList = new JTextPane();
	LinePainter painter = new LinePainter(messagesList);
	private JTextField writing = new JTextField();
	private JButton send = new JButton("Send");
	private Client client;

	public MessagePanel(Client client, String username) {
		this.user = username;
		this.client = client;
		setLayout(new BorderLayout());
		add(messagesList, BorderLayout.CENTER);
		JPanel south = new JPanel(new BorderLayout());
		south.add(writing);
		south.add(send, BorderLayout.EAST);
		send.addActionListener(new SendListener());
		add(south, BorderLayout.SOUTH);
		writing.setFocusable(true);
		writing.requestFocusInWindow();
		writing.addKeyListener(new KeySendListener());				
	}

	class SendListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!writing.getText().isEmpty())
				client.send(new Message(client.getUsername(), user, writing
						.getText()));
			writing.setText("");
		}
	}
	class KeySendListener implements KeyListener{
		public void keyReleased( KeyEvent e )
		{
		if( e.getKeyCode() == KeyEvent.VK_ENTER )
		{
			if (!writing.getText().isEmpty())
				client.send(new Message(client.getUsername(), user, writing
						.getText()));
			writing.setText("");
		}
		}

		@Override
		public void keyPressed(KeyEvent arg0) {
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
		}
	}
	public void display(Message message) {
		StringBuilder sb = new StringBuilder();
		Date date = new Date(message.getTimestamp());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");		
	    String formattedDate = dateFormat.format(date);
		sb.append(formattedDate);
		sb.append("/ ");
		sb.append(message.getSender());
		sb.append(" : ");
		sb.append(message.getText());
		sb.append(System.lineSeparator());
		messagesList.setText(messagesList.getText() + sb.toString());

	}

}
