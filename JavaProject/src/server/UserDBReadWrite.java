package server;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class UserDBReadWrite {

	static public Users read() {
		InputStream fileInput = null;

		try {
			fileInput = new FileInputStream(Parameters.pathUserDB);

			ObjectInputStream o = new ObjectInputStream(fileInput);

			while (true) {
				try {
					return (Users) o.readObject();
				} catch (EOFException e) {
				} finally {
					o.close();
				}
			}
		} catch (FileNotFoundException e) {
			// TODO
			// myLogger.severe("Could not find UserList, creating new one.");
			// System.err.println("Could not find UserList, creating new one.");
		} catch (IOException e) {
			// TODO
			// myLogger.severe("An error occured, while reading the UserList");
			// System.err.println("An error occured, while reading the UserList");
		} catch (ClassNotFoundException e) {
			// TODO myLogger.severe("UserList.class could not be found");
			// System.err.println("UserList.class could not be found");
		}

		// TODO Problem with the Users file / creating a new Users
		return new Users();

	}

	public static void write(Users users) {
		// if (!directory.exists()) {
		// if (!directory.mkdirs()) {
		// myLogger.severe("Error creating Directory");
		// // System.err.println("Error creating Directory");
		// return;
		// }
		// }

		File file = new File(Parameters.pathUserDB);
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
