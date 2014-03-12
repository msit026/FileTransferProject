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
				if(state.equals("true"))                      //if correct login credentials given
				{
					System.out.println("logged in succesfully");
					System.out.println("Enter the command: ");
					s=new Scanner(System.in); //to read the command
					String cmd=s.nextLine();
					String[] commandgiven=cmd.split(" ");
					
					out.writeUTF(cmd);                   //sending the command into the server
					if(commandgiven[0].equalsIgnoreCase("list"))        //if list is given as the command
					{
						String k=in.readUTF();
						System.out.println("k val: "+k);
						String[] r=k.split("#");                    //to display all the files in the given directory
						for(int i=0;i<r.length;i++)
						{
							System.out.println(r[i]);          //to print on console
						}
					}
					
					if(commandgiven[0].equalsIgnoreCase("upload"))
					{
					   String[] breakedcommand=cmd.split(" ");
					   String reqpath=breakedcommand[1]+"\\"+breakedcommand[2];   //source file path
					   byte[] buf=new byte[14*1024];
					   byte[] array;
					   inr=new FileInputStream(reqpath);
					   bis=new BufferedInputStream(inr);
					   bos= new BufferedOutputStream(client.getOutputStream());
					   int c;
					   int off=0;
					   int count = 0;
//					   bis.read(buf);
//					   bos.write(buf,0,buf.length);
					   while((c=bis.read(buf))>0)
					   {
						   array=new byte[c];
						   System.arraycopy(buf, 0, array, 0,c);
						   //System.out.println(buf.length + "-- In while of client : " + (++count));
						  // bos.write(buf,0,buf.length);
						   bos.write(array);  
					   }
					   bos.flush();
					   bos.close();
					   //bos.write(buf,0,buf.length);
						
					}
					
					
					if(commandgiven[0].equalsIgnoreCase("download"))
					{
						
						bis=new BufferedInputStream(client.getInputStream());
						outr=new FileOutputStream(commandgiven[2]+"/"+ commandgiven[1]);
						bos=new BufferedOutputStream(outr);
						byte[] buf=new byte[14*1024];
						   int c;
						   int off=0;
						   int count = 0;
						   boolean alive=true;
						   while((c=bis.read(buf))>0)
						   {
								  // System.out.println("In while of client download: " + (++count));
								   bos.write(buf);   
						   }
						bos.close();
						System.out.println("------------ Downloaded sucessfully--------------");
					}
					
					if(commandgiven[0].equalsIgnoreCase("help"))
					{
						String k=in.readUTF();
						System.out.println("k val: "+k);
						String[] r=k.split("#");                    //to display all the files in the given directory
						for(int i=0;i<r.length;i++)
						{
							System.out.println(r[i]);          //to print on console
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
