package server;

import java.util.Date;
import java.util.LinkedList;

import message.Message;

public class MessageReadWrite {

	public static LinkedList<Message> read(User user) {

		return null;
	}

	public static void write(Message message) {
		StringBuilder sb = new StringBuilder();
		Date date = new Date(message.getTimestamp());
		sb.append(date.toString());
		sb.append(" - ");
		sb.append(message.getSender());
		sb.append(" : ");
		sb.append(message.getTexte());

		String text = sb.toString();
		write(text, message.getSender(), message.getReceiver());
		write(text, message.getReceiver(), message.getSender());

	}

	private static boolean write(String base, String file, String texte) {
		// TODO WRITE THIS SHIT MAN;
		return true;
	}

}
