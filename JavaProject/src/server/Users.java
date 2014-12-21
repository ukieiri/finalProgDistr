package server;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

public class Users extends Observable {

	private Map<String, User> userMap = new HashMap<String, User>();
	private Map<String, UserRunnable> connectionMap = new HashMap<String, UserRunnable>();
	private UserDBReadWrite userDB;

	public Users(UserDBReadWrite userDBReadWrite) {
		userDB = userDBReadWrite;
	}

	public boolean containsKey(String name) {
		return userMap.containsKey(name);
	}

	public User get(String name) {
		return userMap.get(name);
	}

	public void read() {
		userMap = userDB.read();
		if (userMap == null) {
			userMap = new HashMap<String, User>();
		}
	}

	public void write() {
		userDB.write(userMap);
	}

	public boolean isConnected(String name) {
		return connectionMap.containsKey(userMap.get(name));
	}

	public boolean validatePassword(String user, String password)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		return userMap.get(user).validatePassword(password);
	}

	public void addUser(User user) {
		userMap.put(user.getName(), user);
		notifyRegisterMap(user, false);
	}

	public void addConnection(User user, UserRunnable userRunnable) {
		connectionMap.put(user.getName(), userRunnable);

		notifyRegisterMap(user, true);
		addObserver(userRunnable);
		userRunnable.update(this, getRegisterMap());
	}

	public UserRunnable getConnection(String user) {
		return connectionMap.get(user);
	}

	public UserRunnable removeConnection(User user) {
		UserRunnable removed = connectionMap.remove(user.getName());
		deleteObserver(removed);
		notifyRegisterMap(user, false);
		return removed;
	}

	private void notifyRegisterMap(User user, boolean connected) {
		Map<String, Boolean> registerMap = new HashMap<String, Boolean>();
		registerMap.put(user.getName(), connected);
		setChanged();
		notifyObservers(registerMap);

	}

	public Map<String, Boolean> getRegisterMap() {
		Map<String, Boolean> registerMap = new HashMap<String, Boolean>();
		for (String name : userMap.keySet()) {
			registerMap.put(name, false);
		}

		for (String name : connectionMap.keySet()) {
			registerMap.put(name, true);
		}
		return registerMap;
	}
}
