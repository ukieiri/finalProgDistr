package server;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1001L;
	private String name;
	private String password;

	public User(String name, String password) {
		this.name = name;
		this.password = password;
	}

	public User(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public boolean validatePassword(String triedPassword)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		return PasswordHash.validatePassword(triedPassword, password);
	}
}
