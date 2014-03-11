import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class connection {
	private static Connection con = null;
	public static Connection getConnection() throws Exception {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/filetransfer","root","root");
			System.out.println("Connection Established....");
		} catch(SQLException sqlEx) {
			sqlEx.printStackTrace();
		}
		return con;
	}
}
		
		