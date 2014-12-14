package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
							if (attributes.length != 3) {
								Server.logger.severe("ERROR IN USER DATABASE");
								continue;
							}
							userList.add(new User(attributes[1], attributes[2]));
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

		try (PrintWriter out = new PrintWriter(new BufferedWriter(
				new FileWriter(dbUser, true)))) {
			out.println(user.getId() + ";" + user.getName() + ";"
					+ user.getPassword());
		} catch (IOException e) {
			System.err.println(e);
		}
	}
}
