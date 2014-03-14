import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
public class newUser {
	public static void main(String args[]) throws Exception
	{
		newUser n=new newUser();
		n.registration();
	}
	
	public int registration() throws Exception
	{
		Connection c=connection.getConnection();                     //to get connected with the database
		Statement smt=c.createStatement();
		String name="";
		int f=0;
		int p=0;
		String password="";
		String rpassword="";
		Scanner s=new Scanner(System.in);
		do{
		f=0;
		System.out.println("Enter your user name");
		name=s.nextLine();
		String q="select * from user_details where id='"+name+"';";
		//System.out.println(q);
		ResultSet r=smt.executeQuery(q);
		if(r.next())
		{
			System.out.println("User name already exit choose another user name");
			f=1;
		}
		else
		{
			System.out.println("user name available");
		}
		}while(f==1);
		do{
			p=0;
		System.out.println("Enter the passowrd");
        s=new Scanner(System.in);
        password=s.nextLine();
        System.out.println("ReEnter the password");
        s=new Scanner(System.in);
        rpassword=s.nextLine();
         
        if(!rpassword.equals(password))
        {
        	System.out.println("password not matched");
        	p=1;
        }
        else
        {
        	System.out.println("password matched");
        }
		}while(p==1);
		java.sql.Timestamp sqlDate = new java.sql.Timestamp(System.currentTimeMillis());
		String a="insert into user_details  values('"+name+"','"+password+"','"+sqlDate+"');";
		//System.out.println(a);
		int b=smt.executeUpdate(a);
		if(b!=0)
		{
			System.out.println("Sucessfully registered");
		}
		else
		{
			System.out.println("Error in registration please try again");
			registration();
		}
		
		
		
		
		return 0;
		
	}
	
	
	
	

}
