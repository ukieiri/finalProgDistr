package server;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Logging {
	private Logger logger;
	//get year and month for logger
	SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM" );  
	String dateNow = formatter.format( new java.util.Date() );  

	public Logging() {		
		logger = Logger.getLogger("./"+dateNow+"_server.csv");	
		init_Logger();
	//	}
	}

	public void close() {

	}

	public void init_Logger() {
		// define and add handler into log
		FileHandler fh;
		try {
			fh = new FileHandler("./"+dateNow+"_server.csv", true);
			logger.addHandler(fh);

			// apply a custom formatter
			SocketFormatter myFormatter = new SocketFormatter();
			fh.setFormatter(myFormatter);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static class SocketFormatter extends Formatter {

		public SocketFormatter() {
			super();
		}

		@Override
		public String format(LogRecord record) {			
			StringBuffer sb = new StringBuffer();

			// Get the date now
			DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
			Date date = new Date();
			sb.append(dateFormat.format(date));
			sb.append(",");
			
			// Get the level (error, info, etc)
			sb.append(record.getLevel().getName());
			sb.append(",");

			// Get the sender class name
			sb.append(record.getSourceClassName());
			sb.append(",");			
			
			//an actual record
			sb.append(formatMessage(record));
			sb.append("\r\n");

			return sb.toString();
		}

	}

	public Logger getCustomLogger() {
		return logger;
	}
}
