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

	@SuppressWarnings({ "unused", "unchecked" })
	// The check is done by the unused part
	public Map<String, User> read() {
		InputStream fileInput = null;
		Map<String, User> map = null;
		ObjectInputStream objectInput = null;
		try {
			fileInput = new FileInputStream(userDBPath);
			objectInput = new ObjectInputStream(fileInput);
			map = (Map<String, User>) objectInput.readObject();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (fileInput != null)
					fileInput.close();
				if (objectInput != null)
					objectInput.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// Check that we only have String and users
		if (map != null) {
			try {
				for (String s : map.keySet())
					;
				for (User user : map.values()) {
				}
			}

			catch (ClassCastException e) {
				// TODO Log severe problem with the userList We found a non
				// String/User map !
				return null;
			}
		}
		return map;
	}

	public void write(Map<String, User> users) {
		// if (!directory.exists()) {
		// if (!directory.mkdirs()) {
		// myLogger.severe("Error creating Directory");
		// // System.err.println("Error creating Directory");
		// return;
		// }
		// }

		File file = new File(userDBPath);
		OutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(file);
			ObjectOutputStream outStream = null;

			try {
				outStream = new ObjectOutputStream(fileOut);

				outStream.writeObject(users);
			} catch (IOException e) {
				// TODO perhaps a log for the error ?
				e.printStackTrace();
			} finally {
				try {
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
				if (fileOut != null)
					fileOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}