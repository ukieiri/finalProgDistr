package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

public class AcceptClient implements Runnable {

	protected Socket mySkClient;
	protected BufferedReader inClient;
	protected PrintWriter outClient;
	private User user;

	public AcceptClient(Socket cSocket) {
		this.mySkClient = cSocket;
		try {
			Server.logger.info("Create output stream with"
					+ mySkClient.toString());
			outClient = new PrintWriter(mySkClient.getOutputStream());

			Server.logger.info("Create input stream with"
					+ mySkClient.toString());
			inClient = new BufferedReader(new InputStreamReader(
					mySkClient.getInputStream()));
		} catch (IOException e) {
			Server.logger.warning(e.toString());
			close();
			return;
		}
	}

	private void send(Object text) {
		outClient.println(text.toString());
		outClient.println("END OF TRANSMISSION");
		outClient.flush();
	}

	// overwrite the thread run()

	// Command lines :
	// CONNECT Username Password
	// REGISTER Username Password
	// MESSAGE FROM Username TO Username Timestamp Message
	// RESPONSE Code
	// GETUSERLIST
	// PING
	// PONG

	public void run() {

		// uh.. Perhaps while we still have a PING/PONG Response ? I don't
		// know.. need to find something
		while (true) {
			try {
				String cmdFrClient = inClient.readLine();
				if (cmdFrClient == null)
					continue;

				if (cmdFrClient.startsWith("REGISTER")) {
					cmdRegister(cmdFrClient);
					continue;
				}

				if (cmdFrClient.startsWith("CONNECT")) {
					cmdConnect(cmdFrClient);
					continue;
				}

				if (cmdFrClient.startsWith("GETUSERLIST")) {

					Map<String, Boolean> userList = Server.getUserlist()
							.getListForClient();
					send(userList.toString());
					continue;
				}

				if (cmdFrClient.startsWith("WHOAMI")) {
					if (user != null) {
						send(user.getName());

					} else {
						send("NOBODY");
					}

					continue;
				}

				if (cmdFrClient.startsWith("DISCONNECT")) {
					if (user != null) {
						user.setConnected(false);
						user = null;
						send("RESPONSE 3");

					}
					send("RESPONSE 4000");
				}

				if (cmdFrClient.startsWith("QUIT")) {
					close();
					break;
				}

				if (cmdFrClient.startsWith("SHUTDOWN")) {
					if (user != null && user.getName().equals("ADMIN")) {
						// TODO other way to do the same thing
						System.exit(0);
					}
					send("RESPONSE NO RIGHT");
					continue;
				}

				send("RESPONSE 404");

				// String cmdWord = cmdFrClient.substring(0,
				// cmdFrClient.indexOf(" "));
				// switch (cmdWord) {
				// case "MESSAGE":
				// // TODO MESSAGE method
				//
				// break;
				// case "RESPONSE":
				// // TODO RESPONSE method
				//
				// break;
				// case "PING":
				// // TODO PING method
				//
				// case "PONG":
				// // TODO PONG method
				//
				// break;
				// default:
				// // TODO Send back to client an error message
				// Server.logger.warning("Invalid command by "
				// + mySkClient.toString());
				// }
			} catch (IOException e) {
				Server.logger.warning(e.getMessage());
			}

			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void cmdConnect(String cmdLine) throws Exception {
		// CONNECT Username Password
		String[] attributes = cmdLine.split(" ");
		if (attributes.length != 3) {
			throw new Exception();
		}
		connect(attributes[1], attributes[2]);

	}

	private void connect(String username, String password) {
		if (user != null) {
			send("RESPONSE Already Connected");
			return;
		}

		User temp;
		temp = Server.getUserlist().get(username);

		try {
			if (PasswordHash.validatePassword(password, temp.getPassword())) {
				if (!temp.getConnected()) {
					user = temp;
					user.setConnected(true);
					Server.logger.info(temp.getName()
							+ " has connected successfully.");
					send("RESPONSE SUCCESS");

				} else {
					Server.logger.info(temp.getName()
							+ " was already connected.");
					send("RESPONSE User can not be connected at two clients");
					return;
				}

			} else {
				Server.logger.info(temp.getName()
						+ " tried to connect with a incorrect password");
				send("RESPONSE 1002");
			}
		} catch (NoSuchAlgorithmException e) {
			Server.logger
					.severe("Problem with the password algorithm with user "
							+ temp.getName());
			send("RESPONSE 3000");

		} catch (InvalidKeySpecException e) {
			Server.logger
					.severe("Problem with the validation of the password with user "
							+ temp.getName());
			send("RESPONSE 3001");
		}

	}

	private void cmdRegister(String cmdLine) throws Exception {
		// REGISTER Username Password
		String[] attributes = cmdLine.split(" ");
		if (attributes.length != 3) {
			// TODO throws invalid expression
			throw new Exception();
		}
		register(attributes[1], attributes[2]);
	}

	private void register(String username, String password)
			throws NoSuchAlgorithmException, InvalidKeySpecException {

		if (!username.matches("[a-zA-Z\\s']+")) {
			// TODO throws invalid expression
			send("RESPONSE 2000");
			return;
		}

		if (Server.getUserlist().containsUsername(username)) {
			send("RESPONSE 1010");
			Server.logger.warning(mySkClient.toString()
					+ " tried to register an already registered username.");
			return;
		}
		user = new User(username, PasswordHash.createHash(password));

		Server.getUserlist().addUser(user);
		UserDBReadWrite.register(user);
		send("RESPONSE 1");
	}

	private void close() {
		if (user != null) {
			user.setConnected(false);
		}

		try {
			send("QUIT");
			mySkClient.close();
			Server.logger.info("Connection closed with "
					+ mySkClient.toString());

		} catch (IOException e) {
			Server.logger.severe(e.getMessage());
		}
	}

}
