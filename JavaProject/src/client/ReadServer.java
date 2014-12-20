package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReadServer implements Runnable {
	private BufferedReader pIn;
	private Socket socket;

	public ReadServer(Socket socket) throws IOException {
		pIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.socket = socket;
	}

	@Override
	public void run() {
		try {

			String message = "";
			while (!message.equals("QUIT")) {
				message = pIn.readLine();
				System.out.println(message);
			}

		} catch (IOException e1) {

			e1.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
