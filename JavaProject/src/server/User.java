package server;

import java.io.Serializable;

public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1001L;
	private String name;
	private String password;
	private boolean connected;

	public User(String name, String passwordHash) {
		connected = false;
		this.name = name;
		this.password = passwordHash;
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

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public boolean getConnected() {
		return connected;
	}
}
