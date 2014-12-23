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
			System.out.println("Thread running");
			reader = Thread.currentThread();
			Thread thisThread = Thread.currentThread();
			Object o = "";
			while (!o.equals("QUIT") && reader == thisThread) {
				o = inServer.readObject();
				if (o instanceof Message || o instanceof Map) {
					setChanged();
					notifyObservers(o);

				}
				System.out.println(o.toString());
			}

		} catch (IOException | ClassNotFoundException e) {
			client.displayError(e);
		}
	}

	public void stop() {
		reader = null;
	}
}
