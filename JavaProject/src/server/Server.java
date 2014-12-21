package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.logging.Logger;

public class Server {
	public final static Logger logger = Logger.getLogger("Server");

	// Command lines :
	// CONNECT Username Password
	// REGISTER Username Password
	// MESSAGE Username Username Message
	// RESPONSE Code
	// PING
	// PONG

	// TODO other version of this if possible.. so bad.Perhaps view the server
	// has an object.. we will see.
	private Users users;

	public Server() throws SecurityException, IOException {
		// Init the logger
		SocketFormatter.initLogger(logger, Parameters.pathLog + initDate()
				+ ".log");
		InetAddress localAddress;
		ServerSocket MySkServer;
		int port = 45000;

		System.out.println();
		logger.info("*** Start of server ***");
		try {
			// Create the server socket and listen to the port
			localAddress = InetAddress.getLocalHost();
			MySkServer = new ServerSocket(port, 10, localAddress);
			// wait for a client connection

			users = new Users(new UserDBReadWrite(Parameters.pathUserDB));

			while (true) {

				Socket clientSocket = MySkServer.accept();

				logger.info("New Connection "
						+ clientSocket.getInetAddress().toString());
				UserRunnable user = new UserRunnable(clientSocket, users);
				Thread t = new Thread(user);

				// starting the thread
				t.start();
			}

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws SecurityException,
			IOException {

		new Server();
	}

	private static String initDate() {
		Calendar calendar = Calendar.getInstance();
		StringBuilder sb = new StringBuilder();
		sb.append(calendar.get(Calendar.YEAR));
		sb.append("-");
		sb.append(calendar.get(Calendar.MONTH));
		return sb.toString();
	}

	public Users getUserlist() {
		return users;
	}
}
