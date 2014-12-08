package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserDBReadWrite {

	static public List<User> read() {
		List<User> userList = new ArrayList<User>();
		File dbUser = new File("C:/temp/user.db");

		if (dbUser.isFile()) {
			BufferedReader bufferIn;
			try {
				bufferIn = new BufferedReader(new FileReader(dbUser));
				while (true)
					try {
						String textRead = bufferIn.readLine();

						// Reached end of file
						if (textRead == null) {

							bufferIn.close();
							Server.logger.info("END of USER DATABASE");
							break;
						} else {
							String[] attributes = textRead.split(";");
							if (attributes.length != 2) {
								Server.logger.severe("ERROR IN USER DATABASE");
								continue;
							}
							userList.add(new User(attributes[0], attributes[1]));
							Server.logger.info(attributes[0]);
						}

					} catch (IOException e) {
						e.printStackTrace();
					}
			} catch (FileNotFoundException e1) {
				Server.logger.severe(e1.getMessage());

			}
		}
		return userList;

	}

	static public void register(User user) {
		File dbUser = new File("C:/temp/user.db");
		if (dbUser.isFile()) {
			BufferedWriter bufferOut = null;
			try {
				bufferOut = new BufferedWriter(new FileWriter(dbUser));
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			try {
				bufferOut.write(user.getName() + ";" + user.getPassword()
						+ System.getProperty("line.separator"));

				bufferOut.flush();
				bufferOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}
