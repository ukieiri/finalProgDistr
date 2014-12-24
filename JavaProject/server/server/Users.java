package server;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

public class Users extends Observable {

	// Map of all the User
	private Map<String, User> userMap = new HashMap<String, User>();

	// Map of all the connection to the Users
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

	// Read the database and create a new Map if needed
	public void read() {
		userMap = userDB.read();
		if (userMap == null) {
			userMap = new HashMap<String, User>();
		}
	}

	// Write the database
	private void write() {
		userDB.write(userMap);
	}

	// return true if the connectionMap contains the name
	public boolean isConnected(String name) {
		return connectionMap.containsKey(userMap.get(name));
	}

	// Ask the user to validate the password
	public boolean validatePassword(String user, String password)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		return userMap.get(user).validatePassword(password);
	}

	// add a user in the userMap then rewrite the database
	// Can rewrite an existing user
	public void addUser(User user) {
		userMap.put(user.getName(), user);
		notifyRegisterMap(user, false);
		write();
	}

	// add a connection in the connectionMap
	public void addConnection(User user, UserRunnable userRunnable) {
		connectionMap.put(user.getName(), userRunnable);

		notifyRegisterMap(user, true);
		addObserver(userRunnable);

		// send the new connection all the userList
		userRunnable.update(this, getRegisterMap());
	}

	public UserRunnable getConnection(String user) {
		return connectionMap.get(user);
	}

	// Disconnect an user
	public UserRunnable removeConnection(User user) {
		UserRunnable removed = connectionMap.remove(user.getName());
		if (removed != null) {
			// remove it from the observer list
			deleteObserver(removed);
			// stop the user runnable
			removed.stop();

			// notify the disconnected
			notifyRegisterMap(user, false);
		}
		return removed;
	}

	// send a notification about an user and its state
	private void notifyRegisterMap(User user, boolean connected) {
		Map<String, Boolean> registerMap = new HashMap<String, Boolean>();
		registerMap.put(user.getName(), connected);
		setChanged();
		notifyObservers(registerMap);

	}

	// get a map of user and if they are connected
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

	// Stop all the users runnable
	public void stop() {
		for (UserRunnable user : connectionMap.values()) {
			user.stop();
		}
	}

	// Add an user in the userList (not connected)
	public void addUser(String name, String password) {
		try {
			password = PasswordHash.createHash(password);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		User user = new User(name, password);
		userMap.put(name, user);
		notifyRegisterMap(user, false);
		write();

	}
}
