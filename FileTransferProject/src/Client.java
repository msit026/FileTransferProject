import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
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
		System.out.println("enter server name");                                       //to read the inputs from the user
		Scanner s=new Scanner(System.in);
		servername=s.nextLine();
		System.out.println("enter port number");
		portnumber=s.nextInt();
		
		
		while(state.equals("false"))
		{
			Socket client=new Socket(servername,portnumber);
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
							
							int dataPort = Integer.parseInt(in.readUTF());
							
							Socket dataSocket = new Socket(servername,dataPort);
							String[] breakedcommand=cmd.split(" ");
							   String reqpath=breakedcommand[1]+"\\"+breakedcommand[2];   //source file path
							   byte[] buf=new byte[14*1024];
							   byte[] array;
							   inr=new FileInputStream(reqpath);
							   bis=new BufferedInputStream(inr);
							   OutputStream os = dataSocket.getOutputStream();
							   bos= new BufferedOutputStream(os);
							   int c;
							   int off=0;
							   int count = 0;
//							   bis.read(buf);
//							   bos.write(buf,0,buf.length);
							   while((c=bis.read(buf))>0)
							   {
								   array=new byte[c];
								   System.arraycopy(buf, 0, array, 0,c);
								   System.out.println(array.length + "-- In while of client : " + (++count));
								  // bos.write(buf,0,buf.length);
								   bos.write(array);  
							   }
							   bis.close();
							   bos.flush();
							   bos.close();
							   System.out.println(in.readUTF());
							  
							   //bos.write(buf,0,buf.length);
						   
							
						}
						
						
						else if(commandgiven[0].equalsIgnoreCase("download"))
						{
							
							int dataPort = Integer.parseInt(in.readUTF());
							Socket dataSocket = new Socket(servername,dataPort);
							bis=new BufferedInputStream(dataSocket.getInputStream());
							outr=new FileOutputStream(commandgiven[2]+"/"+ commandgiven[1]);
							bos=new BufferedOutputStream(outr);
							byte[] buf=new byte[14*1024];
							byte[] array;
						   int c;
						   int count = 0;
						   while((c=bis.read(buf))>0)
						   {
							   array = new byte[c];
							   System.arraycopy(buf, 0, array, 0, c);
								  System.out.println(buf.length +"In while of client download: " + (++count));
								   bos.write(array);   
						   }
							bos.close();
							dataSocket.close();
							System.out.println(in.readUTF());
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
					System.out.println("wrong id or password");     //to prompt the wrong password or username
					client.close();
					
				}
		}
	
		
		
		
		
	}
}
