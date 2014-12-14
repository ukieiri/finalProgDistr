package exception;

import server.Server;
import server.User;

public class AlreadyInUseUsername extends Exception {
	private static final long serialVersionUID = 4549563183436820300L;

	public AlreadyInUseUsername(User user) {
		Server.logger.warning(user.getName() + " is already in use.");
	}

}