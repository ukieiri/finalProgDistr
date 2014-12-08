package server;

public class User {
	private String name;
	private String password;

	public User(String name, String passwordHash) {
		this.name = (name);
		this.password = (passwordHash);
	}

	public User(String name) {
		this.name = name;
	}

	protected String getName() {
		return name;
	}

	protected String getPassword() {
		return password;
	}
}
