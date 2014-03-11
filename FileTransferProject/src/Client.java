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
		System.out.println("enter server name");
		Scanner s=new Scanner(System.in);
		servername=s.nextLine();
		System.out.println("enter port number");
		portnumber=s.nextInt();
		Socket client=new Socket(servername,portnumber);
		
		while(state.equals("false"))
		{
			System.out.println("Enter your ID");
			s=new Scanner(System.in);
			Id=s.nextLine();
			s=new Scanner(System.in);
			System.out.println("Enter password");
			password=s.nextLine();
			
			String temp=Id+"#"+password;
			
			OutputStream outToServer = client.getOutputStream();
			DataOutputStream out=new DataOutputStream(outToServer);
			out.writeUTF(temp);
			
			InputStream inFromServer=client.getInputStream();
			DataInputStream in=new DataInputStream(inFromServer);
			state=in.readUTF();
			//System.out.println(state);
				if(state.equals("true"))
				{
					System.out.println("logged in succesfully");
					break;
				}
				else
					System.out.println("wrong id or password");
		}
	
		
		
		
		
	}
}
