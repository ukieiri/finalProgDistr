package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

	public static void main(String[] args) {

		Scanner scan = new Scanner(System.in);

		Socket socket = null;

		// byte[] myIp = new byte[4];
		// myIp[0] = (byte) 192;
		// myIp[1] = (byte) 168;
		// myIp[2] = (byte) 1;
		// myIp[3] = (byte) 103;

		try {
			// get the server address
			// TODO Change by the normal address or let it like this.
			InetAddress ServerAddress = InetAddress.getLocalHost();
			System.out.println("Get the address of the server : "
					+ ServerAddress);

			// Try to connect to the server
			socket = new Socket(ServerAddress, 45000);
			System.out.println("We got the connexion to  " + ServerAddress);

			// wait for 3 seconds before dying
			PrintWriter Pout = new PrintWriter(socket.getOutputStream());
			BufferedReader Pin = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));

			String message = "";
			String response = "";

			while (!message.equals("QUIT")) {
				message = "";
				response = "";
				message = scan.nextLine();
				Pout.println(message);
				Pout.flush();
				while (!response.equals("END OF TRANSMISSION")) {
					response = Pin.readLine();
					System.out.println(response);
				}
			}

			// Pout.println("REGISTER Xlol password");
			//
			// // tell the server that we have finished to talk
			// Pout.println("");
			// Pout.flush();
			//
			// Pout.println("CONNECT Test password");
			// Pout.println("");
			// Pout.flush();
			//
			// Pout.println("GETUSERLIST");
			// Pout.println("");
			// Pout.flush();
			//
			// Pout.println("WHOAMI");
			// Pout.println("");
			// Pout.flush();
			//
			// while (!message.equals("QUIT")) {
			// message = Pin.readLine();
			// System.out.println(message);
			// }
			//
			//
			// Pout.println("QUIT");
			// Pout.println("");
			// Pout.flush();
			// // Then die

			System.out.println("now dying....");

			socket.close();

		} catch (UnknownHostException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

}