package exception;

import server.Server;

public class UsernameNotFoundException extends Exception {

	public UsernameNotFoundException(String username) {
		Server.logger.info(username + " was not found.");
	}

}
