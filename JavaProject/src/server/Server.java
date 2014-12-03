package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.logging.Logger;

public class Server {
	private static Logger logger = Logger.getLogger("Server");

	public static void main(String[] args) throws SecurityException, IOException
	{
		// Init the logger
		SocketFormatter.initLogger(logger, initDate() + ".log");
		InetAddress localAddress;
		ServerSocket MySkServer;
		int port = 45000;

		System.out.println();
		logger.info("*** Start of server ***");
		try
		{
			// Create the server socket and listen to the port
			localAddress = InetAddress.getLocalHost();
			MySkServer = new ServerSocket(port, 10, localAddress);

			// wait for a client connection
			while (true)
			{

				Socket clientSocket = MySkServer.accept();

				logger.info("New Connection " + clientSocket.getInetAddress().toString());
				Thread t = new Thread(new AcceptClient(clientSocket));
				// starting the thread
				t.start();
			}

		} catch (IOException e)
		{

			e.printStackTrace();
		}
	}

	private static String initDate()
	{
		Calendar calendar = Calendar.getInstance();
		StringBuilder sb = new StringBuilder();
		sb.append(calendar.get(Calendar.YEAR));
		sb.append("-");
		sb.append(calendar.get(Calendar.MONTH));
		return sb.toString();
	}
}
