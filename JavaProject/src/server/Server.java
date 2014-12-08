package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

public class Server {
	public final static Logger logger = Logger.getLogger("Server");

	// Command lines :
	// CONNECT Username Password
	// REGISTER Username Password
	// MESSAGE FROM Username TO Username Timestamp Message
	// RESPONSE Code
	// PING
	// PONG

	// TODO other version of this if possible.. so bad.Perhaps view the server
	// has an object.. we will see.
	private final static List<User> userList = new ArrayList<User>();

	public static void main(String[] args) throws SecurityException,
			IOException {
		// Init the logger
		SocketFormatter.initLogger(logger, "C:/temp/" + initDate() + ".log");
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
			while (true) {

				Socket clientSocket = MySkServer.accept();

				logger.info("New Connection "
						+ clientSocket.getInetAddress().toString());
				Thread t = new Thread(new AcceptClient(clientSocket));
				// starting the thread
				t.start();
			}

		} catch (IOException e) {

			e.printStackTrace();
		}

		userList.addAll(UserDBReadWrite.read());
	}

	private static String initDate() {
		Calendar calendar = Calendar.getInstance();
		StringBuilder sb = new StringBuilder();
		sb.append(calendar.get(Calendar.YEAR));
		sb.append("-");
		sb.append(calendar.get(Calendar.MONTH));
		return sb.toString();
	}

	public static List<User> getUserlist() {
		return userList;
	}
}
