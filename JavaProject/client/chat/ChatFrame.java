package chat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTabbedPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import link.Message;
import model.UserListModel;
import control.Client;

public class ChatFrame extends JFrame implements Observer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Client client;
	private UserListModel userListModel = new UserListModel();
	private JList<String> userList = new JList<String>(userListModel);
	private Map<String, MessagePanel> mapTab = new HashMap<String, MessagePanel>();

	private JTabbedPane tabbedPane = new JTabbedPane();

	public ChatFrame(Client client) {
		super("Chat - " + client.getUsername());
		this.client = client;

		userList.setCellRenderer(userListModel.getUserListCellRenderer());

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		add(userList, BorderLayout.WEST);
		add(tabbedPane, BorderLayout.CENTER);
		userList.setPreferredSize(new Dimension(200, 800));
		userList.addListSelectionListener(new SelectTabListener());

		setSize(800, 800);
		setVisible(true);
	}

	@Override
	public void update(Observable arg0, Object object) {
		if (object instanceof Message) {
			Message message = (Message) object;
			String other;
			if (message.getSender().equals(client.getUsername())) {
				other = message.getReceiver();
			} else {
				other = message.getSender();
			}

			if (!mapTab.containsKey(other)) {
				mapTab.put(other, new MessagePanel(client, other));
				tabbedPane.addTab(other, mapTab.get(other));
			}
			mapTab.get(other).display(message);

		}
		if (object instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Boolean> userMap = (Map<String, Boolean>) object;
			userListModel.putAll(userMap);
		}
	}

	class SelectTabListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent event) {
			@SuppressWarnings("rawtypes")
			String selected = (String) ((JList) event.getSource())
					.getSelectedValue();
			if (!mapTab.containsKey(selected)) {
				mapTab.put(selected, new MessagePanel(client, selected));
				tabbedPane.addTab(selected, mapTab.get(selected));
			}
			tabbedPane.setSelectedComponent(mapTab.get(selected));

		}
	}
}
