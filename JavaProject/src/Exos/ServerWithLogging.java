package Exos;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;
import java.io.PrintWriter;
import java.lang.String;
import java.util.Date;
import java.util.logging.*;
import java.io.*;

public class ServerWithLogging {

	/**
	 * @param args
	 */
	//Create a logger
    private final static Logger ServerLogger = Logger.getLogger("ServerLog");;
   
    
    //extend the current Formatter
    public static class SocketFormatter extends Formatter {

    	public SocketFormatter() {
    		super();
    	}

    	public String format(LogRecord record) {
    		
    		// Create a StringBuffer to contain the formatted record
    		StringBuffer sb = new StringBuffer();
    				
    		// Get the date from the LogRecord and add it to the buffer
    		Date date = new Date(record.getMillis());
    		sb.append(date.getTime());
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
    }
	
	
	public static void main(String[] args) {
		
		ServerSocket MySkServer;
		Socket srvSocket = null ;
		InetAddress LocalAddress;
		PrintWriter Pout;
		BufferedReader Buffin = null;
	
		//get the current date
		Calendar currentDate = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-H-mm-ss");
		String dateNow = formatter.format(currentDate.getTime());
		
		try {
  	        
	        //define a new file handler and its log,
			//we give a new name every time the program starts,
			//and we do not append new information
            FileHandler fh = new FileHandler(".\\Server" + dateNow + ".log",false);
          
            //use our formatter 
            SocketFormatter SktFormatter = new SocketFormatter();
            fh.setFormatter(SktFormatter);
            
            //add the handle to the log            
            ServerLogger.addHandler(fh);
            ServerLogger.setLevel(Level.INFO);
    		ServerLogger.info("*********** program starts ***********");
            
			LocalAddress = InetAddress.getLocalHost();
			
			//Warning : the backlog value (2nd parameter is handled by the implementation
			MySkServer = new ServerSocket(45000,10,LocalAddress);
			
			//set 3min timeout
			MySkServer.setSoTimeout(180000);
			
			System.out.println("Default Timeout :" + MySkServer.getSoTimeout());
			ServerLogger.info("Default Timeout :" + MySkServer.getSoTimeout());
			
			System.out.println("Used IpAddress :" + MySkServer.getInetAddress());
			ServerLogger.info("Used IpAddress :" + MySkServer.getInetAddress());
			
			System.out.println("Listening to Port :" + MySkServer.getLocalPort());
			ServerLogger.info("Listening to Port :" + MySkServer.getLocalPort());
			
			//wait for client connection
			srvSocket = MySkServer.accept();
			
  			System.out.println("A client is connected :");
  			ServerLogger.info("A client is connected :");
  			
  			//open the output data stream
  			Pout = new PrintWriter(srvSocket.getOutputStream());
  			
  			//open the input data stream
  			Buffin = new BufferedReader (new InputStreamReader (srvSocket.getInputStream()));
  			
  		    //create the scanner to accept data from the console
  			Scanner sc = new Scanner(System.in);

  			String message_distant = "";
  			
  			//loop on the client connection in/out
  			while(true)
			{
  				//Send a message to the client
  				System.out.println("Send message to client: ");
  				ServerLogger.info("Send message to client: ");
  				String message = sc.nextLine();
  				Pout.println(message);
  				ServerLogger.info(message);
  				Pout.flush();		
  				
  				//Read a line from the input buffer, remove the last cr
		        message_distant = Buffin.readLine().trim();
		        
		        //Display the message sent by the client 
		        System.out.println("\nReceive message from client:\n"+message_distant);
		        ServerLogger.info("Receive message from client:"+message_distant);
		        
		        //if the order is quit then exit from the loop
		        if (message_distant.equals("quit"))
		        {
			     System.out.println("\nReceived the quit message....");
			     ServerLogger.info("Received the quit message....");
		         break;
		        }
			}
			//end server
	        System.out.println("\nNow dying....");
	        ServerLogger.setLevel(Level.INFO);
			ServerLogger.info("=========== program ends =============");
	        
	        
	        Pout.close();
			Buffin.close();
  			srvSocket.close();
  			MySkServer.close();
			
		}catch (SocketException e) {		
		  System.out.println("Connection Timed out");
   	      ServerLogger.severe("Connection Timed out");
		}
		catch (IOException e) {
			e.printStackTrace();
    	    ServerLogger.severe("IO exception"+e.toString());
		}
	}
}