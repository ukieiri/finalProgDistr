package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Map;

import message.Message;

public class UserRunnable implements Runnable {

	protected Socket mySkClient;
	protected ObjectInputStream inClient;
	protected ObjectOutputStream outClient;
	private User user;
	private Users users;
	private Map<String, UserRunnable> connectedUsers;

	public UserRunnable(Socket cSocket, Map<String, UserRunnable> connectedUsers) {
		this.mySkClient = cSocket;
		this.connectedUsers = connectedUsers;
		try {
			Server.logger.info("Create output stream with"
					+ mySkClient.toString());
			outClient = new ObjectOutputStream(mySkClient.getOutputStream());

			Server.logger.info("Create input stream with"
					+ mySkClient.toString());
			inClient = new ObjectInputStream(mySkClient.getInputStream());
		} catch (IOException e) {
			Server.logger.warning(e.toString());
			close();
			return;
		}
	}

	// overwrite the thread run()

	// Command lines :
	// CONNECT Username Password
	// REGISTER Username Password
	// MESSAGE Username Username Message
	// RESPONSE Code
	// GETUSERLIST
	// PING
	// PONG

	public void run() {
		// TODO Perhaps log that we created a new thread or closed it ?
		if (login())
			listen();
	}

	private boolean login() {

		try {
			String name = (String) inClient.readObject();
			String password;

			users = UserDBReadWrite.read();

			// TODO logger.info("User " + name +
			// " is trying to connect.");
			if (!users.containsKey(name)) {
				outClient.writeObject("REGISTER");
				outClient.flush();

				// TODO logger.info("User " + user.getName() +
				// " does not exist.";

				password = (String) inClient.readObject();
				password = PasswordHash.createHash(password);
				user = new User(name, password);

				// TODO logger.info("User " + user.getName() +
				// " has been created";

				// Reread because of problem if someone is long to accept
				// the registration (not the same Users)

				users = UserDBReadWrite.read();
				users.addUser(user);
				UserDBReadWrite.write(users);

			} else if (connectedUsers.containsKey(name)) {
				// TODO log warning tried to log with an already used login
				outClient.writeObject("ALREADYCONNECTED");
				outClient.flush();
				close();
				return false;
			}

			outClient.writeObject("PASSWORD");
			outClient.flush();

			password = (String) inClient.readObject();

			User u = users.get(name);

			if (!PasswordHash.validatePassword(password, u.getPassword())) {
				// Not good password !
				// TODO log warning ? severe ? tried to log with not a good
				// password !
				outClient.writeObject("FALSE");
				outClient.flush();

				close();
				return false;
			}
			// TODO log info successful log !
			user = u;
			user.setConnected(true);
			connectedUsers.put(name, this);
			outClient.writeObject("CONNECTED");
			outClient.flush();

			sendAllMessages();

			return true;

		} catch (IOException e) {
			// TODO logger ?
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO logger ?
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO logger ? problem in the password creation severe
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO logger ? problem in the password creation severe
			e.printStackTrace();
		}

		close();
		return false;
	}

	private void listen() {
		while (!mySkClient.isBound() || !mySkClient.isConnected()) {
			try {
				Object o = inClient.readObject();

				if (o instanceof Message) {

					cmdMessage((Message) o);
				} else if (o.toString().equals("LOGOUT")) {
					outClient.writeObject("LOGOUT");
					outClient.flush();

					// TODO log info successfully log out
					close();
				} else {
					// TODO log warning cmd send is not know
					outClient.writeObject("CMD NOT KNOW");
					outClient.flush();
				}

			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block LOG severe ?
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block LOG severe ?
				e.printStackTrace();
			}

		}
	}

	private void cmdMessage(Message message) {
		UserRunnable client = connectedUsers.get(message.getSender());
		message.setTimestamp(System.currentTimeMillis());
		if (client != null) {
			client.send(message);
		}
		send(message);

		MessageReadWrite.write(message);
	}

	private void sendAllMessages() {

		List<Message> messages = MessageReadWrite.read(user);
		for (Message m : messages) {
			send(m);
		}
	}

	private void send(Message message) {

	}

	private void close() {
		if (user != null) {
			user.setConnected(false);
			connectedUsers.remove(user.getName());
			// TODO log.info user disconnected ?
		}

		try {
			outClient.writeObject("QUIT");
			outClient.flush();
			mySkClient.close();
			inClient.close();
			outClient.close();
			Server.logger.info("Connection closed with "
					+ mySkClient.toString());

		} catch (IOException e) {
			Server.logger.severe(e.getMessage());
		}
	}

}
