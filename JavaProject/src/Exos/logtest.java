package Exos;

import java.util.Date;
import java.util.logging.*;
import java.io.*;

public class logtest {

	/**
	 * @param args
	 */
    private static Logger myLogger;
    
    public static class MyCustomFormatter extends Formatter {

    	public MyCustomFormatter() {
    		super();
    	}

    	public String format(LogRecord record) {
    		
    		// Create a StringBuffer to contain the formatted record
    		// start with the date.
    		StringBuffer sb = new StringBuffer();
    		
    		// Get the date from the LogRecord and add it to the buffer
    		Date date = new Date(record.getMillis());
    		sb.append(date.toString());
    		sb.append(";");
    		
    		sb.append(record.getSourceClassName());
    		sb.append(";");
    		
    		sb.append(record.getSourceMethodName());
    		sb.append(";");
    		
    		
    		// Get the level name and add it to the buffer
    		sb.append(record.getLevel().getName());
    		sb.append(";");
    		 
    		sb.append(formatMessage(record));
    		sb.append("\r\n");

    		return sb.toString();
    	}
    }

    // Define the level of log
    // 0 = config
    // 1 = info
    // 2 = warning
    // 3 = severe
    // by default the log is at info level
	public static void main(String[] args) 
	{
                 // Log a FINE tracing message
		// Create an appending file handler
        boolean append = true;
        Logger myLogger = Logger.getLogger("TestLog");
        FileHandler fh;

        //Define the logger level without parameters
        if (args.length == 0){
            myLogger.setLevel(Level.INFO);
      	}
        else{
        	//Define the logger level with parameters
	        int valIn = Integer.parseInt(args[0]);
	        if ((valIn > 3) || (valIn < 0))
	        {
	            myLogger.setLevel(Level.INFO);
	      	}
	        //We are in range  
	        switch(valIn){
		        case 0 :
		            myLogger.setLevel(Level.CONFIG);
		        	break;
		        case 1 :
		            myLogger.setLevel(Level.INFO);
		        	break;
		        case 2 :
		            myLogger.setLevel(Level.WARNING);
		        	break;
		        case 3 :
		            myLogger.setLevel(Level.SEVERE);
		        	break;
	        }
        }
        try{
        	// define a new file handler and its log
            fh = new FileHandler(".\\my.log", append);
            
            //add the handle to the log            
            myLogger.addHandler(fh);

            //use a simple formatter 
//            SimpleFormatter formatter = new SimpleFormatter().format();
//            fh.setFormatter(formatter);
            MyCustomFormatter formatter = new MyCustomFormatter();
            fh.setFormatter(formatter);
          
            Level tempo = myLogger.getLevel();
            myLogger.setLevel(Level.INFO);
    		myLogger.info("*********** program starts ***********");
            myLogger.setLevel(tempo); 
            
            myLogger.config("this is the config level");            
            myLogger.log(Level.CONFIG,"this is the config level v2");            
            //
            myLogger.info("this is the info level");
            myLogger.log(Level.INFO,"this is the info level v2");            
            //
            myLogger.warning("this is the warning level");
            myLogger.log(Level.WARNING,"this is the warning level v2");            
            //
            myLogger.severe("this is the severe level");
            myLogger.log(Level.SEVERE,"this is the severe level v2");            

        } catch (Error ex){
            // Log the error
            myLogger.log(Level.SEVERE,"exception thrown",ex);
        } catch (RuntimeException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		// end program
        Level tempo = myLogger.getLevel();
        myLogger.setLevel(Level.INFO);
		myLogger.info("=========== program ends =============");
        myLogger.setLevel(tempo); 

	}
}