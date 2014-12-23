package link;

import java.io.Serializable;

public class Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2000L;
	private String sender;
	private String receiver;
	private long timestamp;
	private String text;

	public Message(String sender, String receiver, String text) {
		this(sender, receiver, 0L, text);
	}

	public Message(String sender, String receiver, long timestamp, String text) {
		// replace all the lineSeparator by spaces so it won't be multiple lines
		// on the system

		this.sender = sender;
		this.receiver = receiver;
		this.timestamp = timestamp;
		text = text.replaceAll("\n", "");
		text = text.replaceAll(System.lineSeparator(), "");
		text = text.trim();
		this.text = text;
	}

	public String getSender() {
		return sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getText() {
		return text;
	}

}
