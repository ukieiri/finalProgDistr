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

import message.Message;

public class MessageReadWrite {

	public static List<Message> read(User user) {

		File userFile = new File(Parameters.pathUserMessages, user.getName());
		List<Message> messageList = new LinkedList<Message>();
		if (userFile.exists() && userFile.isDirectory()) {
			File[] files = userFile.listFiles();
			for (File savedMessage : files) {
				messageList.addAll(read(savedMessage));
			}
		}

		return messageList;
	}

	private static List<Message> read(File savedConversation) {
		List<Message> list = new LinkedList<Message>();
		BufferedReader bReader = null;
		try {
			bReader = new BufferedReader(new FileReader(savedConversation));
			String line = new String();

			while (true) { // The loop break inside if the readLine send null
				line = bReader.readLine();
				if (line == null) {
					break;
				}
				String[] parts = line.split(";", 4);
				if (parts.length < 4) {
					continue;
				}
				Message message = new Message(parts[1], parts[2],
						Long.parseLong(parts[0]), parts[3]);
				list.add(message);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Server.logger.warning("File does not exist");
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
		return list;
	}

	public static void write(Message message) {
		StringBuilder sb = new StringBuilder();
		sb.append(message.getTimestamp());
		sb.append(";");
		sb.append(message.getSender());
		sb.append(";");
		sb.append(message.getReceiver());
		sb.append(";");

		// replace all the lineSeparator by spaces so it won't be multiple lines
		// on the system
		String text = message.getText();
		sb.append(text);

		text = sb.toString();

		write(message.getSender(), message.getReceiver(), text);
		write(message.getReceiver(), message.getSender(), text);

	}

	private static boolean write(String base, String file, String texte) {
		File folder = new File(Parameters.pathUserMessages, base);
		if (!folder.exists() && !folder.mkdirs()) {
			// TODO log severe couldn't create the folder for the user
			return false;
		}
		PrintWriter outUser = null;
		File messageFile = new File(folder, file);
		try {
			outUser = new PrintWriter(new BufferedWriter(new FileWriter(
					messageFile, true)));
			outUser.append(texte);

		} catch (IOException e) {

			// TODO log severe could not write
			e.printStackTrace();
		} finally {
			if (outUser != null)
				outUser.close();
		}

		return true;
	}
}
