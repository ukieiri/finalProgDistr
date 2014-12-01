package Exos;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.*;

import Exos.ServerWithLogging.SocketFormatter;

public class ClientConnexionLogging {	
	//Create a logger
    private final static Logger ClientLogger = Logger.getLogger("ClientLog");;
    
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
			
			InetAddress ServerAddress;
			String message_distant ="";
			
			byte[] MyIp = new byte[4];
	        MyIp[0] = (byte) 192;
	        MyIp[1] = (byte) 168;
	        MyIp[2] = (byte) 108;
	        MyIp[3] = (byte) 10;
	        
			//get the current date
			Calendar currentDate = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-H-mm-ss");
			String dateNow = formatter.format(currentDate.getTime());        
	        
			try {
				
		        //define a new file handler and its log,
				//we give a new name every time the program starts,
				//and we do not append new information
	            FileHandler fh = new FileHandler(".\\Client" + dateNow + ".log",false);
	            
	            //add the handle to the log            
	            ClientLogger.addHandler(fh);
	            ClientLogger.setLevel(Level.INFO);
	    		ClientLogger.info("*********** program starts ***********");

	            //use our formatter 
	            SocketFormatter SktFormatter = new SocketFormatter();
	            fh.setFormatter(SktFormatter);			
	            
	        	//get the server address
	            ServerAddress = InetAddress.getByAddress("",MyIp);
				System.out.println("Get the address of the server : "+ ServerAddress);
				ClientLogger.info("Get the address of the server : "+ ServerAddress);

				//try to connect to the server
				Socket MySocket = new Socket(ServerAddress,45000);

				System.out.println("We got the connexion to  "+ ServerAddress);
				ClientLogger.info("We got the connexion to  "+ ServerAddress);
				
				//get an input stream from the socket to read data from the server
				BufferedReader Buffin = new BufferedReader (new InputStreamReader (MySocket.getInputStream()));
				
				//get an output stream to send data to the server
				System.out.println("send message to the server...");
				PrintWriter Pout = new PrintWriter(MySocket.getOutputStream());

				Pout.println("I got the connexion, thanks");

				//tell the server that we have finished to talk
				Pout.println(""); 
				Pout.flush();		
				
				//listen to the input from the socket
				//exit when the order quit is given			
				while(true)
				{	
					//Read a line in the buffer, wait until something arrive remove the last cr
					System.out.println("wait message from server...");
					ClientLogger.info("wait message from server...");
					
					message_distant = Buffin.readLine().trim();

					//display message received by the server
					System.out.println("\nMessage received from server:\n"+message_distant);
					ClientLogger.info("\nMessage received from server:\n"+message_distant);
					
					//if quit then exit the loop
					if (message_distant.equals("quit"))
					{
						System.out.println("\nquit sent from server...");
						ClientLogger.info("\nquit sent from server...");
						break;
					}			        
				}								
	          
				//send back the message to the server to kill it
		        Pout.close();
		        Buffin.close();
		        
				System.out.println("\nTerminate program...");
  		        ClientLogger.info("\nTerminate program...");
		        MySocket.close();


			}catch (UnknownHostException e) {
				
				e.printStackTrace();
			}catch (IOException e) {
				System.out.println("server connection error, dying.....");
  		        ClientLogger.severe("server connection error, dying.....");
			}
		}
}