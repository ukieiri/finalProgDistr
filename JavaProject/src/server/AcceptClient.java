package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import exception.AlreadyInUseUsername;
import exception.UsernameNotFoundException;

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

	private void send(String text) throws InterruptedException {
		outClient.println(text);
		outClient.flush();
	}

	// overwrite the thread run()

	// Command lines :
	// CONNECT Username Password
	// REGISTER Username Password
	// MESSAGE FROM Username TO Username Timestamp Message
	// RESPONSE Code
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
		User temp;
		try {
			temp = Server.getUserlist().get(username);
		} catch (UsernameNotFoundException e1) {
			outClient.println("RESPONSE 1003");
			return;
		}

		try {
			if (PasswordHash.validatePassword(password, temp.getPassword())) {
				if (!temp.getConnected()) {
					user = temp;
					user.setConnected(true);
					Server.logger.info(temp.getName()
							+ " has connected successfully.");
					outClient.println("RESPONSE 1");

				} else {
					Server.logger.info(temp.getName()
							+ " was already connected.");
					outClient.println("RESPONSE 1001");
					return;
				}

			} else {
				Server.logger.info(temp.getName()
						+ " tried to connect with a incorrect password");
				outClient.println("RESPONSE 1002");
			}
		} catch (NoSuchAlgorithmException e) {
			Server.logger
					.severe("Problem with the password algorithm with user "
							+ temp.getName());
			outClient.println("RESPONSE 3000");

		} catch (InvalidKeySpecException e) {
			Server.logger
					.severe("Problem with the validation of the password with user "
							+ temp.getName());
			outClient.println("RESPONSE 3001");
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
		user = new User(username, PasswordHash.createHash(password));

		try {
			Server.getUserlist().addUser(user);
			UserDBReadWrite.register(user);

		} catch (AlreadyInUseUsername e) {

			outClient.println("RESPONSE 1000");
			// TODO Message back the Client because there is a problem
		}
	}

	private void close() {
		try {
			mySkClient.close();
			Server.logger.info("Connection closed with "
					+ mySkClient.toString());

		} catch (IOException e) {
			Server.logger.severe(e.getMessage());
		}
	}
}
