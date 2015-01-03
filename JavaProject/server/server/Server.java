package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Handler;
import java.util.logging.Logger;

import server.Logging;

public class Server {
	private Logging logging = new Logging();
	private Logger logger;
	private Users users;
	private ScannerRunnable tScanner;
	private Parameters parameters;

	public Server() {
		InetAddress localAddress;
		ServerSocket MySkServer;
		
		logger = logging.getCustomLogger();

		// Init the shutdown hook to assure the closure of connection/thread/...
		Thread tShutdown = new ShutdownHook();
		Runtime.getRuntime().addShutdownHook(tShutdown);

		// Load settings of the server
		parameters = new Parameters("settings.cfg");

		logger.info("*** Start of server ***");
		try {
			// Create the server socket and listen to the port
			localAddress = InetAddress.getLocalHost();
			logger.info("Server: " + localAddress.toString());

			// Start listening to the port
			MySkServer = new ServerSocket(parameters.getPort(),
					parameters.getBacklog(), localAddress);

			// Create the user list
			users = new Users(new UserDBReadWrite(parameters.getPathUserDB(),logger));

			// Create and start the scanner listener (admin command)
			ScannerRunnable tScanner = new ScannerRunnable(this, logger);
			Thread t = new Thread(tScanner);
			t.start();

			// While there is no IOException
			while (true) {
				// Accept a new client
				Socket clientSocket = MySkServer.accept();

				logger.info("New Connection "
						+ clientSocket.getInetAddress().toString());

				// Create and start a new thread for a new client
				UserRunnable user = new UserRunnable(clientSocket, this, logger);
				t = new Thread(user);
				t.start();
			}

		} catch (IOException e) {
			// The server is shutting down
			// TODO log
			e.printStackTrace();
		}
	}

	public Users getUserlist() {
		return users;
	}

	public Parameters getParameters() {
		return parameters;
	}

	public static void main(String[] args) {
		new Server();
	}

	class ShutdownHook extends Thread {
		// assure that we shutdown the connection with the server and the
		// threads
		public void run() {
			// Stop all current connections
			if (users != null)
			{
				users.stop();
			}
			// Stop scanner listener thread
			if (tScanner != null)
			{
				tScanner.stop();
			}
			// Close all the log files
			if (logger != null) {
				for (Handler handler : logger.getHandlers()) {
					handler.close();
				}
			}
		}
	}

}
