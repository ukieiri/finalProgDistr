package message;


public class Message {
	private String sender;
	private String receiver;
	private long timestamp;
	private String texte;

	public Message(String sender, String receiver, long timestamp, String texte) {
		this.sender = sender;
		this.receiver = receiver;
		this.timestamp = timestamp;
		this.texte = texte;
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

	public String getTexte() {
		return texte;
	}

}
