import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Client
{
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
					s=new Scanner(System.in);                      //to read the command
					String commandgiven=s.nextLine();
					if(commandgiven.equalsIgnoreCase("list"))        //if list is given as the command
					{
						out.writeUTF(commandgiven);
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
