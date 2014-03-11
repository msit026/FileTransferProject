import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


public class Server {
	
	static ServerSocket serverSocket;
	static String fromClient="";
	static String Id="";
	static String password="";
	public static void main(String args[]) throws Exception
	{
		Connection con=connection.getConnection();                     //to get connected with the database
		Statement stmt=con.createStatement();
		serverSocket=new  ServerSocket(4444);                       //server socket
		Socket server;
		while(true)
		{
			System.out.println("Waiting for the client to connect on port "+serverSocket.getLocalPort());
			server = serverSocket.accept();                                           //connecting to the client
			
			DataInputStream in = new DataInputStream(server.getInputStream());	 //to read from the client
			fromClient=in.readUTF();
			
			DataOutputStream out=new DataOutputStream(server.getOutputStream());//creating data stream to write to client
			
			String[] temp=fromClient.split("#");          //to split the id and passwrd
			
			Id=temp[0];
			password=temp[1];
			
			String q="select * from user_details where id='"+Id+"' and password='"+password+"';";	//checking in the database
			
			ResultSet str=stmt.executeQuery(q);
			//System.out.println("str values"+str);
			if(str.next())
			{
				//System.out.println("inside if of server");
			    out.writeUTF("true");	                                        //sending the authentication status to the client
			    String cmd=in.readUTF();                                       //to read the command
				 String x= Command.command(cmd);
				 out.writeUTF(x);                                     //to write the result of given command to the client
			}
			else
			{
				//System.out.println("insdie else of server");
				out.writeUTF("false");	                               //sending false if wrong credentials given
				////logout..................
			}
			
			
	
		}	
	
	}
}
