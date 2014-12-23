package model;

import java.awt.Color;
import java.awt.Component;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class UserListModel extends DefaultListModel<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 594286980172711460L;

	private Map<String, Boolean> userMap = new LinkedHashMap<String, Boolean>();

	public void putAll(Map<String, Boolean> userMap) {

		this.userMap.putAll(userMap);
		removeAllElements();
		String[] keys = (String[]) this.userMap.keySet().toArray(new String[0]);
		Arrays.sort(keys, new SortUserList(this.userMap));

		for (String username : keys)
			addElement(username);

	}

	public UserListCellRenderer getUserListCellRenderer() {
		return new UserListCellRenderer();
	}

	class UserListCellRenderer extends JLabel implements
			ListCellRenderer<String> {

		private static final long serialVersionUID = -3304500395711404453L;

		public UserListCellRenderer() {
			setOpaque(true);
		}

		@Override
		public Component getListCellRendererComponent(
				JList<? extends String> list, String value, int index,
				boolean isSelected, boolean cellHasFocus) {
			Color background;
			Color foreground;

			setText(value);
			if (isSelected) {
				foreground = Color.WHITE;
				background = Color.BLACK;
			} else if (userMap.get(value)) {
				foreground = Color.BLACK;
				background = Color.white;

			} else {
				foreground = Color.GRAY;
				background = Color.white;

			}

			setBackground(background);
			setForeground(foreground);
			return this;
		}
	}

	static private class SortUserList implements Comparator<String> {

		private Map<String, Boolean> userMap;

		public SortUserList(Map<String, Boolean> userMap) {
			this.userMap = userMap;
		}

		@Override
		public int compare(String arg0, String arg1) {
			if (userMap.get(arg0).compareTo(userMap.get(arg1)) != 0)
				return userMap.get(arg0).compareTo(userMap.get(arg1)) * -1;
			return arg0.compareTo(arg1);
		}
	}

}
