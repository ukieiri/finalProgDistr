package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class AcceptClient implements Runnable {

	protected Socket mySkClient;
	protected BufferedReader inClient;
	protected PrintWriter outCLient;
	private User user;

	public AcceptClient(Socket cSocket) {
		this.mySkClient = cSocket;
		try {
			Server.logger.info("Create output stream with"
					+ mySkClient.toString());
			outCLient = new PrintWriter(mySkClient.getOutputStream());

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
		outCLient.println(text);
		outCLient.flush();
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

				// String cmdWord = cmdFrClient.substring(0,
				// cmdFrClient.indexOf(" "));
				// switch (cmdWord) {
				// case "CONNECT":
				// // TODO Connect method
				// break;
				// case "REGISTER":
				// cmdRegister(cmdFrClient);
				// // TODO REGISTER method
				//
				// break;
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
		// TODO Throws already existing user

		UserDBReadWrite.register(user);

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
