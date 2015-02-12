package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import link.Message;

public class MessageReadWrite {
	private User user;
	private Parameters parameters;
	private Logging logging = new Logging();
	private Logger logger = logging.getCustomLogger();

	public MessageReadWrite(User user, Parameters parameters) {
		this.user = user;
		this.parameters = parameters;
	}

	public List<Message> read() {
		// Read all the messages of user
		// Get the folder of the user
		File userFolder = new File(parameters.getPathUserMessages(),
				user.getName());

		List<Message> messageList = new LinkedList<Message>();
		// Assure the architecture is how we want it
		if (userFolder.exists() && userFolder.isDirectory()) {
			// List all the files in the folder
			File[] files = userFolder.listFiles();

			// Read all the messages and add it to the list for all the files
			for (File savedMessage : files) {
				messageList.addAll(read(savedMessage));
			}
		}

		return messageList;
	}

	// Function that reads the message for a given file
	private List<Message> read(File savedConversation) {
		List<Message> list = new LinkedList<Message>();
		BufferedReader bReader = null;
		boolean fine = true;

		try {
			// Create the reader
			bReader = new BufferedReader(new FileReader(savedConversation));
			String line = null;

			while (true) { // The loop break inside if the readLine send null
				// Read the next line
				line = bReader.readLine();
				if (line == null) { // If we are at the end of the file
					break;
				}

				// Split the line in 4 at the ;
				// Message are saved like this
				// time stamp;sender;receiver;message
				String[] parts = line.split(";", 4);

				// If the message wasn't how we wanted, skip it.
				if (parts.length < 4) {
					// a line wasn't readable. file in need of rewriting
					fine = false;
					continue;
				}

				// Create and add the new message
				Message message = new Message(parts[1], parts[2],
						Long.parseLong(parts[0]), parts[3]);
				list.add(message);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.warning("File does not exist");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (bReader != null)
					bReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Are all the lines readable ? If not, rewrite the file.
		if (!fine)
			rewrite(savedConversation, list);

		return list;
	}

	// Function that rewrite the content of a file with a message list
	private void rewrite(File file, List<Message> list) {
		PrintWriter outUser = null;
		StringBuilder sb = new StringBuilder();
		// For each message, create the file entry and add it to the builder
		for (Message m : list)
			sb.append(createMessage(m));
		try {
			// Write all the message in the file.
			outUser = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			outUser.write(sb.toString());

		} catch (IOException e) {
			// TODO log severe could not write
			e.printStackTrace();
		} finally {
			// Try to close the file writer
			if (outUser != null)
				outUser.close();
		}

	}

	private String createMessage(Message message) {
		// Create a String that represents a message
		// time stamp;sender;receiver;message
		StringBuilder sb = new StringBuilder();
		sb.append(message.getTimestamp());
		sb.append(";");
		sb.append(message.getSender());
		sb.append(";");
		sb.append(message.getReceiver());
		sb.append(";");
		sb.append(message.getText());
		sb.append(System.lineSeparator());

		return sb.toString();
	}

	public void write(Message message) {
		// Dispatch the message in the sender / receiver folders
		String text = createMessage(message);

		if (!write(message.getSender(), message.getReceiver(), text))
			// LOG failure to write
			;
		if (!write(message.getReceiver(), message.getSender(), text))
			// LOG failure to write
			;

	}

	private boolean write(String base, String file, String text) {
		File folder = new File(parameters.getPathUserMessages(), base);
		// Create a new folder if it doesn't exist
		if (!folder.exists() && !folder.mkdirs()) {
			// Couldn't create the folder
			// TODO log severe couldn't create the folder for the user
			return false; // Couldn't write the message
		}

		PrintWriter outUser = null;
		File messageFile = new File(folder, file);
		try {
			// Open the file in append mode
			outUser = new PrintWriter(new BufferedWriter(new FileWriter(
					messageFile, true)));

			// Put the text after the rest of the file
			outUser.append(text);

		} catch (IOException e) {

			// TODO log severe could not write
			e.printStackTrace();
		} finally {
			// Try to close the writer
			if (outUser != null)
				outUser.close();
		}

		// SUCCESS !
		return true;
	}
}
