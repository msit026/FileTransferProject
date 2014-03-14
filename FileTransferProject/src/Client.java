import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Client
{
	static FileInputStream inr = null;
	static FileOutputStream outr = null;
	static BufferedInputStream bis =null;
	static BufferedOutputStream bos=null;
	static String servername="";
	static int portnumber;
	static String Id="";
	static String password="";
	static String state="false";
	public static void main(String args[]) throws UnknownHostException, IOException
	{
		System.out.println("Enter server name");                                       //to read the inputs from the user
		Scanner s=new Scanner(System.in);
		servername=s.nextLine();
		System.out.println("Enter port number");
		while(true)
		{
			try
			{
				s=new Scanner(System.in);
				portnumber=s.nextInt();
				break;
			}
			catch(InputMismatchException e)
			{
				System.out.println("Invalid port number. Enter numeric!");
			}
		}
		
			
		while(state.equals("false"))
		{
			Socket client;
			try
			{
				client=new Socket(servername,portnumber);
			}
			catch(Exception e)
			{
				System.out.println("Wrong host address/port specified!");
				continue;
			}
			System.out.println("Enter your ID");                         //to read the id and password
			s=new Scanner(System.in);
			Id=s.nextLine();
			s=new Scanner(System.in);
			
			System.out.println("Enter password");
			password=s.nextLine();
			
			String temp=Id+"#"+password;                           //id and password in one string with # as delimiter
			
			OutputStream outToServer = client.getOutputStream();
			DataOutputStream out=new DataOutputStream(outToServer);
			out.writeUTF(temp);                                       //writing to the server
			
			InputStream inFromServer=client.getInputStream();
			DataInputStream in=new DataInputStream(inFromServer);
			state=in.readUTF();                                     //reader from the server
			//System.out.println(state);
			String cmd;//=s.nextLine();
				if(state.equals("true"))                      //if correct login credentials given
				{
					System.out.println("logged in succesfully");
					do
					{
						System.out.println("-------------------------------------");
						System.out.println("Enter the command: ");
						s=new Scanner(System.in);                      //to read the command
						cmd=s.nextLine();
						String[] commandgiven=cmd.split(" ");
						
						out.writeUTF(cmd);                   //sending the command into the server
						
						
						if(commandgiven[0].equalsIgnoreCase("upload"))
						{
//							System.out.println("Choose a port to connect to: ");
//							System.out.println("\t"+in.readUTF());
//							s = new Scanner(System.in);
//							int dataPort = s.nextInt();
//							out.writeUTF(dataPort + "");
							
							if(commandgiven.length < 3)
							{
								System.out.println("No command.");
								continue;
							}
							
							int authPin = Integer.parseInt(in.readUTF());
							System.out.println("Authentication pin: " + authPin);
							
							String[] breakedcommand=cmd.split(" ");
							String reqpath=breakedcommand[1]+"\\"+breakedcommand[2];   //source file path
							File f;
							try
							{
								f = new File(reqpath);
								out.writeUTF(authPin + "");
							}
							catch(NullPointerException ne)
							{
								out.writeUTF("404");
								continue;
							}
							
							
							int dataPort = Integer.parseInt(in.readUTF());
							
							Socket dataSocket = new Socket(servername,dataPort);
							
							out.writeUTF(f.length() + "");
							
							   byte[] buf=new byte[14*1024];
							   byte[] array;
							   inr=new FileInputStream(reqpath);
							   bis=new BufferedInputStream(inr);
							   OutputStream os = dataSocket.getOutputStream();
							   bos= new BufferedOutputStream(os);
							   int c;
							   
							   int count = 0;
//							   bis.read(buf);
//							   bos.write(buf,0,buf.length);
							   while((c=bis.read(buf))>0)
							   {
								   array=new byte[c];
								   System.arraycopy(buf, 0, array, 0,c);
								   //System.out.println(array.length + "-- In while of client : " + (++count));
								  // bos.write(buf,0,buf.length);
								   bos.write(array);  
							   }
							   bis.close();
							   bos.flush();
							   bos.close();
							   System.out.println(in.readUTF());
							   dataSocket.close();
							   //bos.write(buf,0,buf.length);
						   
							
						}
						
						
						else if(commandgiven[0].equalsIgnoreCase("download"))
						{
							if(commandgiven.length < 3)
							{
								System.out.println("No command.");
								continue;
							}
							while(checkFileExistance(commandgiven[2],commandgiven[1]))
							{
								commandgiven[1] = commandgiven[1] + "(1)";
							}
													
							int authPin = Integer.parseInt(in.readUTF());
							System.out.println("Authentication pin: " + authPin);
							
							
							out.writeUTF(authPin + "");
							
							
							int dataPort = Integer.parseInt(in.readUTF());
							Socket dataSocket = new Socket(servername,dataPort);
							bis=new BufferedInputStream(dataSocket.getInputStream());
							outr=new FileOutputStream(commandgiven[2]+"/"+ commandgiven[1],true);
							bos=new BufferedOutputStream(outr);
							byte[] buf=new byte[14*1024];
							byte[] array;
						   int c;
						   int count = 0;
						   while((c=bis.read(buf))>0)//try with >0
						   {
							   array = new byte[c];
							   System.arraycopy(buf, 0, array, 0, c);
								  //System.out.println(array.length +"In while of client download: " + (++count));
								   bos.write(array);
								   bos.flush();
						   }
							bos.close();
							//System.out.println("Client closed!");
							File f = new File(commandgiven[2]+"/"+ commandgiven[1]);
							//System.out.println("Client closed12!");
							out.writeUTF(f.length() + "");
							dataSocket.close();
							String msg = in.readUTF();
							if(msg.equalsIgnoreCase("Download unsuccessful! Try again."))
							{
								f.delete();
							}
							System.out.println(msg);
							bis.close();
						}
						
						else       //if list is given as the command
						{
							
							String k=in.readUTF();
							String[] r=k.split("#");                    //to display all the files in the given directory
							for(int i=0;i<r.length;i++)
							{
								System.out.println(r[i]);          //to print on console
							}
						}
						
					}while(!cmd.equalsIgnoreCase("logout"));
					
				}
				else
				{
					if(!Command.usernameExists(Id))
					{
						System.out.println("New User? Do you want to register?(Yes/No)");
						s = new Scanner(System.in);
						if(s.nextLine().equalsIgnoreCase("yes"))
						{	
							try
							{
								newUser.main(args);
							}
							catch(Exception e)
							{
								System.out.println("Error in new User creation");
							}
						}
					}
					else
					{
						System.out.println("wrong id or password");     //to prompt the wrong password or username
						client.close();
					}
				}
		}
		
	}
	
	public static boolean checkFileExistance(String path, String file){
		
		
		File f = new File(path);
		File[] listOfFiles = f.listFiles(); // gives the name of all the files
		
		for (int i = 0; i < listOfFiles.length; i++) 
		{
			if(listOfFiles[i].getName().equalsIgnoreCase(file))
				return true;
		}
		return false;
	}
	
	public static boolean isDir(String path)
	{
		File f = new File(path);
		if(f.isDirectory())
			return true;
		return false;
	}
}
