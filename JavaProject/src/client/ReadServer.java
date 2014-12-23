package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Date;

import link.Message;

public class ReadServer implements Runnable {

	protected ObjectInputStream inServer;
	private Socket socket;

	public ReadServer(Socket socket) throws IOException {
		inServer = new ObjectInputStream(socket.getInputStream());
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			System.out.println("Thread running");

			Object o = "";
			while (!o.equals("QUIT")) {
				o = inServer.readObject();
				if (o instanceof Message) {
					Message message = (Message) o;
					StringBuilder sb = new StringBuilder();
					Date date = new Date(message.getTimestamp());
					sb.append(date);
					sb.append(" - ");
					sb.append(message.getSender());
					sb.append(" to ");
					sb.append(message.getReceiver());
					sb.append(" : ");
					sb.append(message.getText());
					System.out.println(sb.toString());
				} else {
					System.out.println(o.toString());

				}
			}

		} catch (IOException | ClassNotFoundException e1) {

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
