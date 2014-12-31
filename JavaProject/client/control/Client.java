package control;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import link.Message;
import chat.ChatFrame;
import connection.ConnectionFrame;

public class Client {

	private JFrame frame;
	private ReadServer reader;
	private InetAddress serverAddress;
	private Socket socket;
	private ObjectOutputStream outServer;
	private ObjectInputStream inServer;
	private String username;

	public Client() {
		Thread tShutdown = new ShutdownHook();
		Runtime.getRuntime().addShutdownHook(tShutdown);
		frame = new ConnectionFrame(this);
	}

	public static void main(String[] args) {
		System.out.println();
		new Client();
	}

	private void startChatFrame() {
		frame.dispose();
		frame = new ChatFrame(this);
		Thread t;
		try {
			reader = new ReadServer(this, inServer);
			reader.addObserver((Observer) frame);
			t = new Thread(reader);
			t.start();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String getUsername() {
		return username;
	}

	public void connect(String address, String port, String username,
			String password) {
		try {
			serverAddress = InetAddress.getByName(address);
			socket = new Socket(serverAddress, Integer.parseInt(port));
			outServer = new ObjectOutputStream(socket.getOutputStream());
			inServer = new ObjectInputStream(socket.getInputStream());

			if (!inServer.readObject().equals("CONNECTED")) {
				JOptionPane.showMessageDialog(frame,
						"Couldn't connect to the server");
				return;
			}
			outServer.writeObject(username);
			String response = inServer.readObject().toString();
			if (response.equals("ONLYALPHABET")) {
				JOptionPane.showMessageDialog(frame,
						"Only alphabet characters authorized a to Z");
				return;
			}
			if (response.equals("ALREADYCONNECTED")) {
				JOptionPane.showMessageDialog(frame,
						"This user is already connected.");
				return;

			}
			if (response.equals("REGISTER")) {
				JOptionPane.showMessageDialog(frame,
						"User does not exist yet. Please register.");
				return;
			}
			outServer.writeObject(password);
			response = inServer.readObject().toString();

			if (response.equals("FALSE")) {
				JOptionPane.showMessageDialog(frame,
						"Username or password is incorrect. Disconnecting");
				return;
			}

			this.username = username;
			startChatFrame();

		} catch (UnknownHostException e) {
			displayException(e);
		} catch (IOException e) {
			displayException(e);
		} catch (ClassNotFoundException e) {
			displayException(e);
		}

	}
	
	public void registerUser(String username, String password, String repassword, String port, String address) throws IOException, ClassNotFoundException{
		serverAddress = InetAddress.getByName(address);
		socket = new Socket(serverAddress, Integer.parseInt(port));
		outServer = new ObjectOutputStream(this.socket.getOutputStream());
		inServer = new ObjectInputStream(this.socket.getInputStream());
		
		outServer.writeObject(username);
		String response = inServer.readObject().toString();	
		outServer.writeObject(password);
		response = inServer.readObject().toString();

		if (response.equals("FALSE")) {
			JOptionPane.showMessageDialog(frame,
					"False username/password. Disconnecting");
			return;
		}	
		JOptionPane.showMessageDialog(frame, "User " + username + " registered successfully");
	}
	
	public void displayException(Exception e) {
		JOptionPane.showMessageDialog(frame, e.getMessage());

	}

	public void displayString(String e) {
		JOptionPane.showMessageDialog(frame, e);

	}

	public void send(Message message) {
		try {
			outServer.writeObject(message);
			outServer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			displayException(e);
		}
	}
	
	class ShutdownHook extends Thread {

		// assure that we shut down the connection with the server
		public void run() {

			if (reader != null)
				reader.stop();
			try {
				if (socket != null && !socket.isClosed()) {
					if (outServer != null) {
						outServer.writeObject("LOGOUT");
						outServer.flush();
						outServer.close();
					}
					if (inServer != null)
						inServer.close();

					socket.close();

				}

			} catch (IOException e) {
				// The server was already shutdown
			}
		}
	}

}
