package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class UserDBReadWrite {

	private String userDBPath;

	public UserDBReadWrite(String dbPath) {
		this.userDBPath = dbPath;
		File userDB = new File(dbPath);

		// Create the folders if they don't exist
		if (!userDB.exists()) {
			try {
				userDB.createNewFile();
				write(new HashMap<String, User>());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, User> read() {
		InputStream fileInput = null;
		Map<String, User> map = null;
		ObjectInputStream objectInput = null;
		try {
			// Read the usersdb file into the map
			fileInput = new FileInputStream(userDBPath);
			objectInput = new ObjectInputStream(fileInput);
			map = (Map<String, User>) objectInput.readObject();

		} catch (FileNotFoundException e) {
			// TODO log ? Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO log ? Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO log ? Auto-generated catch block
			e.printStackTrace();
		} finally { // try to close the streams
			try {
				if (fileInput != null)
					fileInput.close();
				if (objectInput != null)
					objectInput.close();
			} catch (IOException e) {
				// TODO log ? Auto-generated catch block
				e.printStackTrace();
			}

		}

		// Check that we only have String and users
		// If the keyset or values contains something else, will catch a
		// ClassCastException
		// http://stackoverflow.com/a/509288

		if (map != null) {
			try {
				for (@SuppressWarnings("unused")
				String s : map.keySet())
					;
				for (@SuppressWarnings("unused")
				User user : map.values()) {
				}
			}

			catch (ClassCastException e) {
				// TODO Log severe problem with the userList We found a non
				// String/User map ! Not the good file.
				// Return nothing so that the caller can create his own map
				return null;
			}
		}
		return map;
	}

	public void write(Map<String, User> users) {

		File file = new File(userDBPath);
		OutputStream fileOut = null;
		try {
			// Create the output stream
			fileOut = new FileOutputStream(file);
			ObjectOutputStream outStream = null;

			try {
				outStream = new ObjectOutputStream(fileOut);

				// write the users map
				outStream.writeObject(users);
			} catch (IOException e) {
				// TODO perhaps a log for the error ?
				e.printStackTrace();
			} finally {
				try {
					// Try to close the streams
					if (outStream != null) {
						outStream.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				// Try to close the streams
				if (fileOut != null)
					fileOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}