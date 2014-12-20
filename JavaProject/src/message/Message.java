package message;

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

	public Message(String sender, String receiver, long timestamp, String text) {
		this.sender = sender;
		this.receiver = receiver;
		this.timestamp = timestamp;
		text.replaceAll(System.lineSeparator(), " ");
		text.trim();
		text += System.lineSeparator();
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
