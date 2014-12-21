package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import link.Message;

public class UserRunnable implements Runnable, Observer {

	protected Socket mySkClient;
	protected ObjectInputStream inClient;
	protected ObjectOutputStream outClient;
	private Users users;
	private User user;

	public UserRunnable(Socket cSocket, Users users) {
		this.mySkClient = cSocket;
		this.users = users;
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

	public void run() {
		// TODO Perhaps log that we created a new thread or closed it ?
		if (login())
			listen();
		close();
	}

	private boolean login() {
		try {
			String name = (String) inClient.readObject();
			String password;

			users.read();
			// TODO logger.info("User " + name +
			// " is trying to connect.");
			if (!users.containsKey(name)) {
				outClient.writeObject("REGISTER");
				outClient.flush();

				// TODO logger.info("User " + user.getName() +
				// " does not exist.";

				password = (String) inClient.readObject();
				password = PasswordHash.createHash(password);

				// TODO logger.info("User " + user.getName() +
				// " has been created";

				users.addUser(new User(name, password));
				users.write();
			} else if (users.isConnected(name)) {
				// TODO log warning tried to log with an already used login
				outClient.writeObject("ALREADYCONNECTED");
				outClient.flush();
				close();
				return false;
			}

			outClient.writeObject("PASSWORD");
			outClient.flush();

			password = (String) inClient.readObject();

			if (!users.validatePassword(name, password)) {
				// Not good password !
				// TODO log warning ? severe ? tried to log with not a good
				// password !
				outClient.writeObject("FALSE");
				outClient.flush();

				close();
				return false;
			}

			// TODO log info successful log !
			user = users.get(name);
			users.addConnection(user, this);
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
		Server.logger.info("Start listening");

		while (mySkClient.isBound() && mySkClient.isConnected()) {
			try {
				Object o = inClient.readObject();
				Server.logger.info("Receiving information");

				if (o instanceof Message) {

					cmdMessage((Message) o);
				} else if (o.toString().equals("LOGOUT")) {
					outClient.writeObject("LOGOUT");
					outClient.flush();

					// TODO log info successfully log out
					close();
					return;
				} else {
					// TODO log warning cmd send is not know
					outClient.writeObject("CMD NOT KNOW");
					outClient.flush();
				}

			} catch (ClassNotFoundException e) {
				Server.logger.info("Stop listening CNFE");
				e.printStackTrace();
			} catch (IOException e) {
				Server.logger.info("Stop listening IOE");
				e.printStackTrace();
			}

		}
		Server.logger.info("Stop listening !bound ! connected");

	}

	private void cmdMessage(Message message) throws IOException {
		Server.logger.info("Receiving message");
		if (!user.getName().equals(message.getSender())) {
			Server.logger.severe(this.toString()
					+ " tried to send a message with an incorrect sender !");
			outClient.writeObject("INCORRECTSENDER");
			outClient.flush();

			// Disconnecting because of trying to write as someone else
			close();
			return;
		}
		UserRunnable client = users.getConnection(message.getReceiver());
		message.setTimestamp(System.currentTimeMillis());
		if (client != null) {
			Server.logger.info("Sending message to " + client.user.getName());

			client.send(message);
		}
		Server.logger.info("Sending message to " + user.getName());

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
		try {
			outClient.writeObject(message);
			outClient.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block

			Server.logger.severe("Couldn't send message IOE");

			e.printStackTrace();
		}
	}

	private void close() {
		if (user != null) {
			users.removeConnection(user);
			user = null;
			// TODO log.info user disconnected ?
		}

		try {
			if (outClient != null) {
				outClient.writeObject("QUIT");
				outClient.flush();
				outClient.close();
			}
			if (inClient != null) {
				inClient.close();
			}
			if (mySkClient != null) {
				mySkClient.close();
			}
			Server.logger.info("Connection closed with "
					+ mySkClient.toString());

		} catch (IOException e) {
			Server.logger.severe(e.getMessage());
		}
	}

	@Override
	public void update(Observable from, Object object) {

		if (from instanceof Users) {
			Users liste = (Users) from;
			try {
				outClient.writeObject(object);
				outClient.flush();

			} catch (IOException e) {
				// TODO log warning / severe can't send update userlist
			}

		}
	}

}
