package server;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Users {

	Map<String, User> map = new LinkedHashMap<String, User>();

	public void addAll(List<User> read) {
		// Add value to the map
		for (User i : read) {
			map.put(i.getName(), i);
		}
	}

	public User get(String username) {
		return map.get(username);
	}

	public boolean containsUsername(String username) {
		return map.containsKey(username);
	}

	public void addUser(User user) {
		map.put(user.getName(), user);
	}

	public Map<String, Boolean> getListForClient() {

		// Only send Username and if the user is connected to the clients
		Map<String, Boolean> forClient = new LinkedHashMap<String, Boolean>();
		for (User i : map.values()) {
			forClient.put(i.getName(), i.getConnected());
		}

		return forClient;
	}
}
