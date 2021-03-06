package client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import link.Message;

public class ClientConsole {

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

			ObjectOutputStream outServer = new ObjectOutputStream(
					socket.getOutputStream());

			Thread t = new Thread(new ReadServer(socket));
			t.start();

			String message = "";

			while (!message.equals("QUIT")) {
				message = "";
				message = scan.nextLine();
				if (message.equals("MESSAGE")) {
					Message m = new Message("Fitz", "Uki", 0L, "HELLO \n!");
					outServer.writeObject(m);
				} else {
					outServer.writeObject(message);

				}
				outServer.flush();
			}

			System.out.println("now dying....");

			socket.close();

		} catch (UnknownHostException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		scan.close();
	}

}