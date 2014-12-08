package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

	public static void main(String[] args) {

		Socket socket = null;

		byte[] myIp = new byte[4];
		myIp[0] = (byte) 192;
		myIp[1] = (byte) 168;
		myIp[2] = (byte) 1;
		myIp[3] = (byte) 103;

		try {
			// get the server address
			InetAddress ServerAddress = InetAddress.getByAddress("", myIp);
			System.out.println("Get the address of the server : "
					+ ServerAddress);

			// Try to connect to the server
			socket = new Socket(ServerAddress, 45000);
			System.out.println("We got the connexion to  " + ServerAddress);

			// wait for 3 seconds before dying
			PrintWriter Pout = new PrintWriter(socket.getOutputStream());

			Pout.println("REGISTER Test password");

			// tell the server that we have finished to talk
			Pout.println("");
			Pout.flush();

			// Then die
			System.out.println("now dying....");
			socket.close();

		} catch (UnknownHostException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

}