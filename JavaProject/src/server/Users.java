package server;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import exception.AlreadyInUseUsername;
import exception.UsernameNotFoundException;

public class Users extends Observable {

	List<User> list = new ArrayList<User>();

	public void addAll(List<User> read) {

		list.addAll(read);
	}

	public List<User> getList() {
		return list;
	}

	public User get(String username) throws UsernameNotFoundException {
		for (User i : list) {
			if (i.getName().equals(username))
				return i;
		}
		throw new UsernameNotFoundException(username);
	}

	public boolean containsUsername(String username) {
		for (User i : list) {
			if (i.getName().equals(username))
				return true;
		}
		return false;
	}

	public void addUser(User user) throws AlreadyInUseUsername {
		if (containsUsername(user.getName())) {
			throw new AlreadyInUseUsername(user);
		}
		list.add(user);
	}
}
