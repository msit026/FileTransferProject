import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Scanner;




public class Server extends Thread{
	
	static ServerSocket serverSocket;
	
	
	public static void main(String args[]) throws Exception
	{
		
		
		try
		{
			serverSocket=new ServerSocket(4444);
			while(true)
			{
				Socket sock=serverSocket.accept();
				MyThread mt=new MyThread(sock);
				System.out.println("Thread Created");
				mt.start();
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		
		
		
	}
}
		
		
		
		
		
		
		
		
		class MyThread extends Thread
		{
			String fromClient="";
			Socket s;
			String Id="";
			String password="";
			int portNumbers[];
			HashMap<Integer,Boolean> ports = new HashMap<Integer,Boolean>();
			
			
			
			public MyThread(Socket sock) {
				// TODO Auto-generated constructor stub
				s=new Socket();
				s=sock;
			}
			
			public void run()
			{
				
				
				
				
				try
				{
				
				
				Connection con=connection.getConnection();                     //to get connected with the database
				Statement stmt=con.createStatement();
		
		while(true)
		{
			                                          //connecting to the client
			fillPorts();
			DataInputStream in = new DataInputStream(s.getInputStream());	 //to read from the client
			fromClient=in.readUTF();
			
			DataOutputStream out=new DataOutputStream(s.getOutputStream());//creating data stream to write to client
			
			String[] temp=fromClient.split("#");          //to split the id and passwrd
			
			Id=temp[0];
			password=temp[1];
			
			String q="select * from user_details where id='"+Id+"' and password='"+password+"';";	//checking in the database
			
			ResultSet str=stmt.executeQuery(q);
			System.out.println("str values"+str);
			if(str.next())
			{
				//System.out.println("inside if of server");
				 Command.setid(Id);
				 Command.storeLastLoggedIn();
			     out.writeUTF("true");	                                        //sending the authentication status to the client
			     String cmd;//=in.readUTF();                                       //to read the command
			     do
			     {
 				    cmd=in.readUTF();                                       //to read the command
 				                                        //to write the result of given command to the client
			     
				     System.out.println("after cmd");
				     
				     String[] cmdSplit = cmd.split(" ");
				     if(cmdSplit[0].equalsIgnoreCase("upload"))
				     {
				    	
				    	 	
//				    	 	String portNumberString = "";
//				    	 	for(int i = 0; i < portNumbers.length; i++)
//				    	 	{
//				    	 		if(ports.get(portNumbers[i]))
//				    	 			portNumberString += (i+1) + ")" + portNumbers[i] + "\n\t";
//				    	 	}
//				    	 	out.writeUTF(portNumberString);
//				    	 
//				    	 	int selectedPort = Integer.parseInt(in.readUTF());
				    	 
				    	 	int selectedPort;
				    	 	do
			    	 		{
			    	 			
				    	 		int ran = (int)(Math.random() * portNumbers.length-1);
			    	 			selectedPort = portNumbers[ran];
			    	 			
			    	 		}while(!ports.get(selectedPort));
				    	 	
				    	 	
				    	 	ports.put(selectedPort,false);
				    	 	ServerSocket dataServerSocket = new ServerSocket(selectedPort);
				    	 	out.writeUTF(selectedPort + "");
				    	 	Socket dataSocket = dataServerSocket.accept();
				    	 	
				    	 	//Command.createfile(cmd);
					    	
				    	 	byte[] buf=new byte[14*1024];
				    	 	byte[] array;
						   BufferedInputStream bis=new BufferedInputStream(dataSocket.getInputStream());
						   int c;
						   int off=0;
						   int count = 0;
						   boolean alive=true;
						   while((c=bis.read(buf))>0)
						   {
							   
							   array=new byte[c];
							   System.arraycopy(buf, 0, array, 0,c);
							   System.out.println(array.length + "--In while of server: " + (++count));
							   Command.createfile(cmd);
							   Command.uploadintofile(array);	
							  
						   }
						   System.out.println("After while in server");
						   Command.closeFile();
						   Command.storeInDB();
						   //should write into database
						   //bis.read(buf);
						   ports.put(selectedPort, true);
						   dataSocket.close();
						   dataServerSocket.close();
						   //System.out.println(buf);  
						out.writeUTF("Upload Succesful!");
				     }
				     else if(cmdSplit[0].equalsIgnoreCase("download"))
			    	 {
				    	 int selectedPort;
			    	 	do
		    	 		{
		    	 			
			    	 		int ran = (int)(Math.random() * portNumbers.length-1);
		    	 			selectedPort = portNumbers[ran];
		    	 			
		    	 		}while(!ports.get(selectedPort));
			    	 	
			    	 	
			    	 	ports.put(selectedPort,false);
			    	 	ServerSocket dataServerSocket = new ServerSocket(selectedPort);
			    	 	out.writeUTF(selectedPort + "");
			    	 	Socket dataSocket = dataServerSocket.accept();
				    	 	
			    		 Command.initiateDownLoad(cmd);
			    		 byte[] buf=new byte[14*1024];
			    		 byte[] array;// = Command.downLoad();
						   BufferedOutputStream bos= new BufferedOutputStream(dataSocket.getOutputStream()); 
						   int c;
						   int off=0;
						   int count = 0;
						   boolean alive=true;
						   
						  do {
							  
							   array=Command.downLoad();
							  
							   if(array!=null)
							   {
								   System.out.println("array lenght+"+array.length);
								   bos.write(array);
							   }
							   System.out.println("inside while of server "+(++count));
						   }while( array!=null && array.length>0);
						  
						  
						  
						   System.out.println("after while in server---");
						   ports.put(selectedPort, true);
						   dataSocket.close();
						   bos.close();    
						   out.writeUTF("Downloaded the file successfully!");
			    	 }
			    	 else
			    	 {
				    	 String x = Command.command(cmd);
				    	 out.writeUTF(x);  
			    	 }
				     
			     }while(!cmd.equalsIgnoreCase("logout"));
				                                    //to write the result of given command to the client
			}
			else
			{
				//System.out.println("insdie else of server");
				out.writeUTF("false");	                               //sending false if wrong credentials given
				////logout..................
			}
			
			
	
		}	//end of while
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
	
	}
			
			
			private void fillPorts() {
				ports.put(234, true);
				ports.put(921, true);
				ports.put(232, true);
				ports.put(345, true);
				ports.put(4354, true);
				ports.put(5674, true);
				ports.put(2343, true);
				ports.put(7862, true);
				ports.put(2831, true);
				ports.put(288, true);
				
				portNumbers = new int[10];
				portNumbers[0] = 234;
				portNumbers[1] = 921;
				portNumbers[2] = 232;
				portNumbers[3] = 345;
				portNumbers[4] = 4354;
				portNumbers[5] = 5674;
				portNumbers[6] = 2343;
				portNumbers[7] = 7862;
				portNumbers[8] = 2831;
				portNumbers[9] = 288;
				
			}
	
}

