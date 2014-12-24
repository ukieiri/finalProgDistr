package control;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.Observable;

import link.Message;

public class ReadServer extends Observable implements Runnable {
	protected ObjectInputStream inServer;
	private volatile Thread reader;
	private Client client;

	public ReadServer(Client client, ObjectInputStream inServer)
			throws IOException {
		this.client = client;
		this.inServer = inServer;
	}

	@Override
	public void run() {
		try {
			reader = Thread.currentThread();
			Thread thisThread = Thread.currentThread();
			Object o = "";
			while (reader == thisThread) {
				o = inServer.readObject();
				if (o.equals("QUIT")) {
					client.displayString("Server is shutting down. Shut down of the client.");
					System.exit(0);
				}
				if (o instanceof Message || o instanceof Map) {
					setChanged();
					notifyObservers(o);
					continue;
				}
				client.displayString(o.toString());
			}

		} catch (IOException | ClassNotFoundException e) {
			client.displayException(e);
		}

	}

	public void stop() {
		reader = null;
	}
}
