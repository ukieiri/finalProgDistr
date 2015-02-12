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
import java.util.logging.Logger;

import link.Message;

public class UserRunnable implements Runnable, Observer {
	private volatile Thread listener; // equals currentThread while running
	// null if the thread is stopped
	private Socket mySkClient;
	private ObjectInputStream inClient;
	private ObjectOutputStream outClient;
	private User user;
	private MessageReadWrite messageReadWrite;
	private Server server;
	private Logging logging = new Logging();
	private Logger logger = logging.getCustomLogger();

	public UserRunnable(Socket cSocket, Server server) {
		this.mySkClient = cSocket;
		this.server = server;
		try {
			logger.info("Create output stream with"
					+ mySkClient.toString());

			// Create the output steam
			outClient = new ObjectOutputStream(mySkClient.getOutputStream());

			logger.info("Create input stream with"
					+ mySkClient.toString());

			// Create the input steam
			inClient = new ObjectInputStream(mySkClient.getInputStream());

			// Send a connection verification
			outClient.writeObject("CONNECTED");
			outClient.flush();
		} catch (IOException e) {
			logger.warning(e.toString());
			close();
			return;
		}
	}

	public void run() {
		listener = Thread.currentThread();
		// TODO Perhaps log that we created a new thread or closed it ?
		// if the client successfully log in, then listen to him
		// finally close the connection
		if (login())
			listen();
		close();
	}

	// Used to log in a client as a user
	private boolean login() {
		try {
			// Read the first object send by the client, should be the username
			String name = (String) inClient.readObject();
			String password;
			Users users = server.getUserlist();
			Parameters parameters = server.getParameters();
			// TODO logger.info("User " + name +
			// " is trying to connect.");

			// Make sure the user name only contain legal characters
			if (!name.matches((parameters.getUserMatch()))) {
				outClient.writeObject("ONLYALPHABET");
				outClient.flush();

				return false;
			}

			users.read(); // make sure the users is up to date
			// if we don't have this username, we need to register it
			if (!users.containsKey(name)) {
				register(name);
			} else if (users.isConnected(name)) {
				// Cannot connect if the user is already connected
				// TODO add the possibility to disconnect the currently login
				// client / perhaps a parameter ?

				// TODO log warning tried to log with an already used login
				outClient.writeObject("ALREADYCONNECTED");
				outClient.flush();
				close();
				return false;
			}

			// Send that we now want the password
			outClient.writeObject("PASSWORD");
			outClient.flush();

			password = (String) inClient.readObject();

			try {
				// ask if the password is correct
				if (users.validatePassword(name, password)) {
					// TODO log info successful log !
					user = users.get(name);

					// send a validation of the connection
					outClient.writeObject("ACCEPTED " + name);
					outClient.flush();

					// add the user to the connected list
					users.addConnection(user, this);

					// create the user message reader
					this.messageReadWrite = new MessageReadWrite(user,
							parameters);
					// send the user all his messages
					sendAllMessages();

					return true;

				}
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				// TODO LOG ? Auto-generated catch block
				e.printStackTrace();
			}
			// Not good password or there is a problem with the algo !
			// Either way, refuse the connection
			// TODO log warning ? severe ? tried to log with not a good
			// password !
			outClient.writeObject("FALSE");
			outClient.flush();

			close();
			return false;

		} catch (IOException e) {
			// TODO logger ?
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO logger ?
			e.printStackTrace();
		}
		close();
		return false;
	}

	// Handle the registration of a new user by a client
	private void register(String name) throws IOException,
			ClassNotFoundException {
		Users users = server.getUserlist();

		// Message that we need to register a new user
		outClient.writeObject("REGISTER");
		outClient.flush();

		// TODO logger.info("User " + user.getName() +
		// " does not exist.";

		// Receiving password
		String password = (String) inClient.readObject();

		// TODO logger.info("User " + user.getName() +
		// " has been created";

		// create the new user
		users.addUser(name, password);
	}

	// Listen to the user connection
	private void listen() {
		logger.info(user.getName() + " : Start listening");
		Thread thisThread = Thread.currentThread();

		// while the connection is still bound and connected and the Server
		// didn't ask for the end of this thread (listener will become null)
		while (mySkClient.isBound() && mySkClient.isConnected()
				&& thisThread == listener) {
			try {
				Object o = inClient.readObject();
				// Read what the client is sending

				logger.info(user.getName() + " : Receiving information");

				if (o instanceof Message) {
					// the client sent a message
					cmdMessage((Message) o);
				} else if (o.toString().equals("LOGOUT")) {
					// the client asked to disconnect
					// TODO log info successfully log out
					close();
					return;
				} else {
					// The client send meaningless information
					// TODO log warning cmd send is not know
					outClient.writeObject("CMD NOT KNOW");
					outClient.flush();
				}

			} catch (ClassNotFoundException e) {
				// We don't know what the client just send to us

				// TODO LOG
				e.printStackTrace();
			} catch (IOException e) {
				// TODO LOG
				e.printStackTrace();
			}

		}

	}

	// Handle the reception of a message from the client
	private void cmdMessage(Message message) throws IOException {
		logger.info(user.getName() + " : Receiving message");

		// Control that the client's user and the message's sender are the same
		if (!user.getName().equals(message.getSender())) {
			logger.severe(user.getName()
					+ " : tried to send a message with an incorrect sender !");
			outClient.writeObject("INCORRECTSENDER");
			outClient.flush();

			return;
		}

		// Control that the message's receiver exist
		if (!server.getUserlist().containsKey(message.getReceiver())) {
			logger.severe(user.getName()
					+ " : tried to send a message to an non existant user !");
			outClient.writeObject("INCORRECTSENDER");
			outClient.flush();
			return;
		}

		// get the runnable of the other client
		UserRunnable client = server.getUserlist().getConnection(
				message.getReceiver());

		// set the time stamp to the current ms
		message.setTimestamp(System.currentTimeMillis());

		// if the receiver is connected, send him the message
		if (client != null) {
			logger.info(user.getName() + " : Sending message to "
					+ client.user.getName());

			client.send(message);
		}
		logger.info(user.getName() + " : Sending message to "
				+ user.getName());

		// send the message to the sender
		send(message);

		// finally write the message in the file database
		messageReadWrite.write(message);
	}

	// Send all the message of this user
	private void sendAllMessages() {
		List<Message> messages = messageReadWrite.read();
		for (Message m : messages) {
			send(m);
		}
	}

	// Send a message to this user
	private void send(Message message) {
		try {
			outClient.writeObject(message);
			outClient.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block

			logger.severe("Couldn't send message IOE");

			e.printStackTrace();
		}
	}

	// Close the connection
	private void close() {
		if (user != null) {
			// Remove the connection from the connectedList
			server.getUserlist().removeConnection(user);
			user = null;
			// TODO log.info user disconnected ?
		}

		try { // Close all the stream / socket
			if (outClient != null) {
				outClient.close();
				outClient = null;
			}
			if (inClient != null) {
				inClient.close();
				inClient = null;

			}
			if (mySkClient != null) {
				mySkClient.close();
				mySkClient = null;
			}
			logger.info("Connection closed");

		} catch (IOException e) {
			// if the streams/socket where already closed
			logger.warning(e.getMessage());
		}
	}

	@Override
	public void update(Observable from, Object object) {
		// Update from an observable
		if (from instanceof Users) {
			// Should be only from Users to signal a change in the userList
			try {
				outClient.writeObject(object);
				outClient.flush();

			} catch (IOException e) {
				// TODO log warning / severe can't send update userlist
			}

		}
	}

	// Stop this thread safely
	public void stop() {
		listener = null;
		try {
			if (outClient != null) {
				outClient.writeObject("QUIT");
				outClient.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); // should happen only if the connection was
									// already closed
		}

	}

}
