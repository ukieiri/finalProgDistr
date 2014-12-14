package server;

public class User {
	private String name;
	private String password;
	static private int cpt = 0;
	private int id;
	private boolean connected;

	public User(String name, String passwordHash) {
		id = cpt;
		cpt++;

		connected = false;
		this.name = (name);
		this.password = (passwordHash);
	}

	public User(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	protected String getPassword() {
		return password;
	}

	public int getId() {
		return id;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public boolean getConnected() {
		return connected;
	}
}
