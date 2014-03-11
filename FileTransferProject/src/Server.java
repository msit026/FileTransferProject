import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.Statement;


public class Server {
	
	static ServerSocket serverSocket;
	static String fromClient="";
	static String Id="";
	static String password="";
	public static void main(String args[]) throws Exception
	{
		Connection con=connection.getConnection();
		Statement stmt=con.createStatement();
		serverSocket=new  ServerSocket(4444);
		while(true)
		{
			System.out.println("Waiting for the client to connect on port "+serverSocket.getLocalPort());
			Socket server = serverSocket.accept();
			
			DataInputStream in = new DataInputStream(server.getInputStream());	
			fromClient=in.readUTF();
			DataOutputStream out=new DataOutputStream(server.getOutputStream());
			String[] temp=fromClient.split("#");
			Id=temp[0];
			password=temp[1];
			String q="select * from user_details where id='"+Id+"' and password='"+password+"';";	
			boolean str=stmt.execute(q);
			if(str)
			{
			  out.writeUTF("true");	
			  
			  
			  
			  
			}
			else
				out.writeUTF("false");	
		}	
	}
}
