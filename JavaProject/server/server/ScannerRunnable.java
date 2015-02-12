package server;

import java.util.Scanner;
import java.util.logging.Logger;

public class ScannerRunnable implements Runnable {
	private Scanner scan = new Scanner(System.in);
	private Logging logging = new Logging();
	private Logger logger = logging.getCustomLogger();
	// Stop system
	private volatile Thread listener;

	// Parent
	private Server server;

	public ScannerRunnable(Server server) {
		this.server = server;
	}

	public void run() {
		logger.info("Reader launched");
		listener = Thread.currentThread();
		Thread thisThread = listener;
		String cmdLine;
		while (thisThread == listener) {
			logger.info("Reader Ready");
			cmdLine = scan.nextLine(); // Read the next line sent to the console

			logger.info("Reader : " + cmdLine);
			Users users = server.getUserlist();
			Parameters parameters = server.getParameters();

			if (cmdLine.equals("SHUTDOWN")) { // Shut the server down
				// SHUTDOWN
				System.exit(0);
			}
			if (cmdLine.startsWith("REGISTER")) { // Register a new user
				// REGISTER USERNAME PASSWORD

				String[] cmdSplit = cmdLine.split(" ", 3);

				if (cmdSplit.length == 3
						&& cmdSplit[1].matches(parameters.getUserMatch())) {
					// Create a new user
					users.addUser(cmdSplit[1], cmdSplit[2]);
					logger.info("Reader : " + cmdSplit[1]
							+ " has been registered");

				} else { // If we didn't send 3 words or the username didn't
							// match the REGEX
					logger.warning("Reader : " + cmdLine
							+ " does not match user requirement");

				}
				continue;
			}

			if (cmdLine.startsWith("DISCONNECT")) {
				// DISCONNECT USERNAME
				String[] cmdSplit = cmdLine.split(" ", 2);
				if (cmdSplit.length == 2 && users.containsKey(cmdSplit[1])) {
					// IF we send at least 2 words and an existing username
					users.removeConnection(users.get(cmdSplit[1]));
				} else {
					logger.info(cmdSplit[1] + " is not connected");
				}
			}

			// Does not change the current run. You need to restart the server
			// to change parameters
			if (cmdLine.startsWith("SAVE")) {
				// SAVE PARAMETERS VALUE
				String[] cmdSplit = cmdLine.split(" ", 3);
				if (cmdSplit.length == 3)
					parameters.saveProperty(cmdSplit[1], cmdSplit[2]);
			}

		}
	}

	public void stop() {
		listener = null;
		scan.close();
	}

}
