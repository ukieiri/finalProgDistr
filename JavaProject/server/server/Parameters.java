package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class Parameters {

	// All the value after the variables are 'by default'
	// the settings.cfg files overwrite this config.

	// path of the log folder

	private String pathLog = "log/";

	// path of the user data base (no message)
	private String pathUserDB = "user.db";

	// path of the saved messages
	private String pathUserMessages = "messages/";

	// open port of the server
	private int port = 45000;

	// REGEX that restrict what the username can be
	// important to not let ; and SPACE available
	private String userMatch = "[a-zA-Z]+";

	// Number of possible connection in waiting
	private int backlog = 10;

	private Properties mySettings = new Properties();
	private String path;

	public Parameters(String path) {
		this.path = path;
		// Get the parameters back
		// http://stackoverflow.com/a/1040137
		try {
			mySettings.load(new FileInputStream(path));
		} catch (FileNotFoundException e) {
			// TODO Log ? create a new file ?
		} catch (IOException e) {
			// TODO LOG
		}
		String exported;

		// Overwrite the different properties
		exported = mySettings.getProperty("pathLog");
		if (exported != null) {
			pathLog = exported;
		}
		exported = mySettings.getProperty("pathUserDB");
		if (exported != null) {
			pathUserDB = exported;
		}
		exported = mySettings.getProperty("pathUserMessages");
		if (exported != null) {
			pathUserMessages = exported;
		}
		exported = mySettings.getProperty("userMatch");
		if (exported != null) {
			userMatch = exported;
		}
		exported = mySettings.getProperty("port");
		if (exported != null) {
			port = Integer.parseInt(exported);
		}
		exported = mySettings.getProperty("backlog");
		if (exported != null) {
			backlog = Integer.parseInt(exported);
		}

		// Set all the properties in the settings
		mySettings.setProperty("pathLog", pathLog);
		mySettings.setProperty("pathUserDB", pathUserDB);
		mySettings.setProperty("pathUserMessages", pathUserMessages);
		mySettings.setProperty("port", "" + port);
		mySettings.setProperty("userMatch", userMatch);
		mySettings.setProperty("backlog", "" + backlog);

		// write the config file
		write();
	}

	private void write() {
		// Write the current parameters in the path given to the constructor
		File file = new File(path);
		OutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(file);

			mySettings.store(fileOut, "Parameters for the chat server");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String getPathLog() {
		return pathLog;
	}

	public int getPort() {
		return port;
	}

	public int getBacklog() {
		return backlog;
	}

	public String getPathUserDB() {
		return pathUserDB;
	}

	public String getUserMatch() {
		return userMatch;
	}

	public String getPathUserMessages() {
		return pathUserMessages;
	}

	public void saveProperty(String key, String value) {
		mySettings.setProperty(key, value);
		write();
	}
}
