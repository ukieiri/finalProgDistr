package server;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Users extends LinkedHashMap<String, User> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1000L;

	public void addAll(List<User> read) {
		// Add value to the map
		for (User i : read) {
			put(i.getName(), i);
		}
	}

	public User get(String username) {
		return get(username);
	}

	public boolean containsUsername(String username) {
		return containsKey(username);
	}

	public void addUser(User user) {
		put(user.getName(), user);
	}

	public Map<String, Boolean> getListForClient() {

		// Only send Username and if the user is connected to the clients
		Map<String, Boolean> forClient = new LinkedHashMap<String, Boolean>();
		for (User i : values()) {
			forClient.put(i.getName(), i.getConnected());
		}

		return forClient;
	}
}
