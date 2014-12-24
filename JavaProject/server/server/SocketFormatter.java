package server;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class SocketFormatter extends Formatter {

	public SocketFormatter() {
		super();
	}

	public String format(LogRecord record) {

		// Create a StringBuffer to contain the formatted record
		StringBuffer sb = new StringBuffer();

		// Get the date from the LogRecord and add it to the buffer
		Date date = new Date(record.getMillis());
		sb.append(date.toString());
		sb.append(";");

		sb.append(record.getSourceClassName());
		sb.append(";");

		// Get the level name and add it to the buffer
		sb.append(record.getLevel().getName());
		sb.append(";");

		sb.append(formatMessage(record));
		sb.append("\r\n");

		return sb.toString();
	}

	public static void initLogger(Logger logger, String folderPath)
			throws SecurityException, IOException {

		File folder = new File(folderPath);
		folder.mkdirs();
		FileHandler fh;
		// define a new file handler and its log
		fh = new FileHandler(folderPath + "log.log", true);

		// add the handle to the log
		logger.addHandler(fh);

		// use our formatter
		SocketFormatter sktFormatter = new SocketFormatter();
		fh.setFormatter(sktFormatter);

	}
}
