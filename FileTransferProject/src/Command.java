

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

public class Command
{
	static FileInputStream in = null;
    static FileOutputStream out = null;
    static FileOutputStream fos;
    static String filename;
    static int flag=0;
    static BufferedOutputStream bos;
    static BufferedInputStream bis;
    static String id;
    static byte[] buf;
    static String destinationpath;
    static int count;
    static String path = "D:/FTP"; // current path
    static String rootDir = "D:/FTP"; // root Directory
    static String filePath;
    static int usercount=0;
   
    public static void setid(String i)
    {
    	id=i;	
    }    
    
    
	public static  String command(String cmd) throws 
IOException
	{
		try
		{
			String[] cmdSplit = cmd.split(" ");
			if (cmdSplit[0].equalsIgnoreCase("list"))
				return displayAllFiles();
			if(cmdSplit[0].equalsIgnoreCase("newdir"))
				return newDir(cmdSplit[1]);
			if(cmdSplit[0].equalsIgnoreCase("changedir"))
			{
				if(cmdSplit.length > 1) // if a path is specified, change directory to that path
					return changeDir(path + "/" + cmdSplit[1]);
				else  // if no path is specified, change directory to root folder
					return changeDir(rootDir);
			}
			
			if(cmdSplit[0].equalsIgnoreCase("help"))
				
				 return help();
			
			
			if(cmdSplit[0].equalsIgnoreCase("delete"))
				return delete(path + "/" + cmdSplit[1]);
			if(cmdSplit[0].equalsIgnoreCase("rename"))
				return rename(path + "/" + cmdSplit[1],path + "/" + cmdSplit[2]);
			if(cmdSplit[0].equalsIgnoreCase("move"))
			{
				if(cmdSplit.length > 2) // if destination path is specified, move the file to destination path(Ex: move abc.txt directory1)
					return rename(path + "/" + cmdSplit[1],rootDir + "/" + cmdSplit[2] + "/" + cmdSplit[1].substring(cmdSplit[1].lastIndexOf('/') + 1));
				else //if no destination is specified, move the file/directory to root folder(Ex: move abc.txt)
					return rename(path + "/" + cmdSplit[1],rootDir + "/" + cmdSplit[1].substring(cmdSplit[1].lastIndexOf('/') + 1));
			}
			if(cmdSplit[0].equalsIgnoreCase("logout"))
				return "Bye!";
					
			if(cmd.contains("upload"))
			{
				//System.out.println("inside upload");
				String destinationpath="D:/FTP";
				String[] k=cmd.split(" ");
				k[1].replace('\\','/');
				String path=k[1]+"/"+k[2];
				//System.out.println(path);
				String kr=destinationpath+"/"+k[2];
				if(k.length==3)
				{
					
					try {
			            in = new FileInputStream(path);
			            out = new FileOutputStream(kr);
			            int c;
	
			            while ((c = in.read()) != -1) {
			                out.write(c);
			            }
			        } finally {
			            if (in != null) {
			                in.close();
			            }
			            if (out != null) {
			                out.close();
			            }
			        }					
				}
				else
				{
					File f=new File(destinationpath+"/"+k[3]);
					//System.out.println(destinationpath+"\\\\"+k[3]);
					FileReader fr = null;
			        FileWriter fw=null;
			        
					try {
						//System.out.println(path);
						fr = new FileReader(path);
						//System.out.println("after fr");
	                   fw=new FileWriter(f);
	                   //System.out.println("after fw");
						int c =fr.read();
						while (c != -1)
						{
							fw.write(c);
							c = fr.read();
						}
						fw.close();
						String aa="Uploaded sucessfully with name "+k[3];
						return aa;
			        }
					catch (IOException e) {
						System.out.println("Error in uploading the file!");
						//e.printStackTrace();
					}
				}
				
			}
			return "No command";
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			return "No command";
		}
		
	}
	
	public static void showreports() {
		System.out.println("------------------------------\nReports:");
		try{
			Connection c=connection.getConnection();                     //to get connected with the database
			Statement smt=c.createStatement();
			String q="select * from files";
			ResultSet r=smt.executeQuery(q);
			int count=0;
			while(r.next())
				count++;
			System.out.println("Total number of files in server is: " + count);
			
			System.out.println("Number of users currently logged in: "+usercount);
			smt=c.createStatement();
			String sr="select * from files where isUnderDownload='1';";
			ResultSet rr=smt.executeQuery(sr);
			int f=0;
		     while(rr.next())
		    	 f++;
			System.out.println("Number of File under download: "+f);
		}
		catch(Exception e)
		{
			System.out.println("Error in reports!");
		}
	}
	public static void incUser()   ////call this function after thread created write statement
	{
		usercount++;
	}
	
	public static void decUser() ////call this when ever the socket is closed
	{
		usercount--;
	}

	static int check = 0;
	public static String uploadintofile(byte[] x) throws 
IOException
	{
		buf=new byte[14*1024];
		buf=x;
		//System.out.println(x.length + "-- In command -- " + (++check));
//		if(buf.length>0)
//		   {
		     bos.write(x);
		     bos.close();
		   //}
		//fos.close();
		     //System.out.println("After write in command");
		return "ready";
	}
	
	public static boolean closeFileAndVerify(long fileSize) throws Exception
	{
		if(fos != null)  // if upload
			fos.close();
		if(bis != null) // if download
			bis.close();
		File f = new File(filePath);
		if(f.length() == fileSize)
		{
			return true;
		}
		else
		{
			if(fos != null && bis == null)// if upload
			{
				f.delete();
				//System.out.println("Here in closefile of command");
			}
			return false;
		}
	}
	
	
	public static String createfile(String cmd) throws FileNotFoundException
	{
		filePath = "";
		//System.out.println("inside create file");
		String[] a=cmd.split(" ");
		//destinationpath="D:/FTP";
		if(a.length==3)
		{
		 filePath=path+"/"+a[2];
		 filename=a[2];
		}
		else
		{
			filePath=path+"/"+a[3];
			filename=a[3];
		}
		//System.out.println("path: "+filePath);
		//File f=new File("path");
		fos=new FileOutputStream(filePath,true);
		bos= new BufferedOutputStream(fos);
		return null;	
	}
	
	
	public static void storeInDB() throws Exception
	{
		Connection con=connection.getConnection();                     //to get connected with the database
		Statement stmt=con.createStatement();
		   File f=new File(filePath);
		   String qry="insert into files values('"+filePath+"','"+id+"','"+f.length()+"','0','"+filename+"');";
		   //System.out.println(qry);
		   int b=stmt.executeUpdate(qry);
		   if(b > 0)
			   System.out.println("sucessfully added");
		   else
			   System.out.println("not uploaded correctly");   
	}
	
	
	
	public static String displayAllFiles() //Displays all the files in the given directory
	{
		//System.out.println("inside displayfiels");
		
		  String files="";
		  File folder = new File(path);
		  File[] listOfFiles = folder.listFiles(); // gives the name of all the files
		 
		  for (int i = 0; i < listOfFiles.length; i++) 
		  {
			  
			   if (listOfFiles[i].isFile() && isFileInDB(listOfFiles[i])) 
			   {
			      files =files+"#"+listOfFiles[i].getName(); // all files in a string with # as delimiter
			   }
			   
		  }
		  if(files.equalsIgnoreCase(""))
			  return "No files to display!";
		 return files;
	}

	public static boolean isFileInDB(File file) {
		Connection con;
		count=0;
		try 
		{
			con = connection.getConnection();
			Statement stmt=con.createStatement();
			String q = "select * from files where file_name = '" + file.getName() + "';";
			ResultSet res = stmt.executeQuery(q);
			
			if(res.next())
			{
				return true;
			}
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("Could not enter into Database!");
		}       
		return false;
	}


	public static void initiateDownLoad(String command) throws Exception
	{
		filePath = "";
		//System.out.println("inside initiate download");
		Connection con;
		count=0;
		
		con = connection.getConnection();
		              //to get connected with the database
		Statement stmt=con.createStatement();
		String[] a=command.split(" ");
		String str="select * from files where file_name='"+a[1]+"';";
		//System.out.println(str);
		ResultSet r=stmt.executeQuery(str);
		if(r.next())
		{
			filePath=r.getString(1);
		}
		in=new FileInputStream(filePath);
		bis=new BufferedInputStream(in);
		//Command.downLoad();
		      
	}
	
	
	
	
	public static byte[] downLoad() throws Exception
	{
		   buf=new byte[14*1024];
		   byte[] array = null;
		   int c;
		   if((c=bis.read(buf))>0)
		   {
			  
			   array=new byte[c];
			   System.arraycopy(buf, 0, array, 0,c);
			   //System.out.println(array.length + "-- In while of command : " +count);
			   count++;
			   return array;  
		   }
		  return array;
		  
	}
	
	public static String newDir(String dirName)
	{
		try
		{
			Connection con=connection.getConnection();                     //to get connected with the database
			Statement stmt=con.createStatement();
			String q="select * from directories where dir_name = '" + path + "';";	//checking in the database if dir with given name exists
			ResultSet res = stmt.executeQuery(q);
			File theDir = new File(path + "/" + dirName);

			  
			if (!(theDir.exists() && res.next())) {
				
				boolean result = theDir.mkdir();  // if the directory does not exist, create it
				q = "insert into directories values('"+ path + "/" + dirName +"', '" + id + "')";  // insert into DB
				int resultQ = stmt.executeUpdate(q);
										
				if(result || (resultQ == 1)) {    
					return "DIR created";
				}
			}

		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
		return "Could not create directory.";
	}

	public static String changeDir(String dirPath)
	{
		
		try
		{
			String newName = dirPath.replace('\\', '/');
			
			Connection con=connection.getConnection();                     //to get connected with the database
			Statement stmt=con.createStatement();
			String q="select * from directories where dir_name = '" + newName + "';";	//checking in the database if dir with given name exists
			ResultSet res = stmt.executeQuery(q);

			  
			if (res.next() || newName.equalsIgnoreCase(rootDir)) {
				path = newName;
				return "Path changed : '" + path + "'!";
			}

		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
		return "Could not find directory specified.";
	}

	public static String delete(String filePath) 
	{
		String newName = filePath.replace('\\', '/');
		int pos = newName.lastIndexOf('/');
		String fileName = newName.substring(pos+1);
		if(fileName.contains("."))
			return deleteFile(filePath);
		else
			return deleteDirectory(filePath);
			
		
	}

	public static String deleteDirectory(String filePath) {
		try
		{
			
			String newName = filePath.replace('\\', '/');
			Connection con=connection.getConnection();                     //to get connected with the database
			Statement stmt=con.createStatement();
			String q="select * from directories where dir_name = '" + newName + "';";	//checking in the database if dir with given name exists
			ResultSet res = stmt.executeQuery(q);
	
			if (res.next()) {
				q = "select * from directories where dir_name = '" + newName + "' and id = '" + id + "';";
				ResultSet res1 = stmt.executeQuery(q);
				if(res1.next())
				{
					q = "delete from directories where dir_name = '" + newName + "' and id = '" + id + "';";
					int r = stmt.executeUpdate(q);
					if(r > 0)
					{
						File f = new File(newName);
						if(f.delete())
							return "Deleted directory succesfully!";
					}
				}
				else
				{
					return "You are not permitted to delete the directory!";
				}
				
			}
			else
				return "No such directory found!";
	
		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
		return "Could not delete the directory.";
	}

	public static String deleteFile(String filePath) 
	{
		String newName = filePath.replace('\\', '/');
		try
		{
			
			
			Connection con=connection.getConnection();                     //to get connected with the database
			Statement stmt=con.createStatement();
			String q="select * from files where file_path = '" + newName + "';";	//checking in the database if dir with given name exists
			ResultSet res = stmt.executeQuery(q);
	
			if (res.next()) {
				q = "select * from files where file_path = '" + newName + "' and id = '" + id + "' and isUnderDownload = 0;";
				ResultSet res1 = stmt.executeQuery(q);
				if(res1.next())
				{
					q = "delete from files where file_path = '" + newName + "' and id = '" + id + "';";
					int r = stmt.executeUpdate(q);
					if(r > 0)
					{
						File f = new File(newName);
						if(f.delete())
							return "Deleted file succesfully!";
					}
				}
				else
				{
					return "You are not permitted to delete the file!";
				}
				
			}
			else
				return "No such file found!";
	
		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
		return "Could not delete the file.";
	}

	public static String rename(String oldFilePath, String newFilePath) 
	{
		String newName = oldFilePath.replace('\\', '/');
		int pos = newName.lastIndexOf('/');
		String fileName = newName.substring(pos+1);
		if(fileName.contains("."))
		{
			return renameFile(oldFilePath,newFilePath);
		}
		else
		{
			return renameDirectory(oldFilePath,newFilePath);
		}
		
			
	}

	public static String renameFile(String oldFilePath, String newFilePath) 
	{
		String oldName = oldFilePath.replace('\\', '/');
		String newName = newFilePath.replace('\\', '/');
		String newFileName = newName.substring(newName.lastIndexOf("/")+1);
		try
		{
			
			
			Connection con=connection.getConnection();                     //to get connected with the database
			Statement stmt=con.createStatement();
			String q="select * from files where file_path = '" + oldName + "';";	//checking in the database if dir with given name exists
			ResultSet res = stmt.executeQuery(q);
	
			if (res.next()) {
				q = "select * from files where file_path = '" + oldName + "' and id = '" + id + "' and isUnderDownload = 0;";
				ResultSet res1 = stmt.executeQuery(q);
				if(res1.next())
				{
					q = "delete from files where file_path = '" + oldName + "' and id = '" + id + "';";
					int r = stmt.executeUpdate(q);
					File f4 = new File(oldName);
					//System.out.println("file size: " + f4.length());
					q = "insert into files values('" + newName + "', '" + id + "', '" + f4.length() + "', 0, '" + newFileName + "');";
					//System.out.println("query : " + q);
					int r1 = stmt.executeUpdate(q);
					if(r > 0 && r1 > 0)
					{
						File f = new File(oldName);
						File f1 = new File(newName);
						if(f.renameTo(f1))
						{
							File f2 = new File(oldName);
							f2.delete();
							return "Renamed/Moved directory succesfully!";
						}
					}
				}
				else
				{
					return "You are not permitted to rename/move the file!";
				}
				
			}
			else
				return "No such file found!";
	
		}
		catch(MySQLIntegrityConstraintViolationException me)
		{
			return "File with the name - '" + newFileName + "' already exists.\n Try a different file name";
		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
		return "Could not rename/move the file.";
	}			
	
	public static String renameDirectory(String oldFilePath, String newFilePath) 
	{
		String oldName = oldFilePath.replace('\\', '/');
		String newName = newFilePath.replace('\\', '/');			
		String newFileName = newName.substring(newName.lastIndexOf("/")+1);
		try
		{
			
			Connection con=connection.getConnection();                     //to get connected with the database
			Statement stmt=con.createStatement();
			String q="select * from directories where dir_name = '" + oldName + "';";	//checking in the database if dir with given name exists
			ResultSet res = stmt.executeQuery(q);
	
			if (res.next()) {
				q = "select * from directories where dir_name = '" + oldName + "' and id = '" + id + "';";
				ResultSet res1 = stmt.executeQuery(q);
				if(res1.next())
				{
					q = "delete from directories where dir_name = '" + oldName + "' and id = '" + id + "';";
					int r = stmt.executeUpdate(q);
					q = "insert into directories values('" + newName + "', '" + id + "');";
					int r1 = stmt.executeUpdate(q);
					
					if(r > 0 && r1 > 0)
					{
						
						File f = new File(oldName);
						File f1 = new File(newName);
						if(f.renameTo(f1))
						{
							File f2 = new File(oldName);
							f2.delete();
							return "Renamed/Moved directory succesfully!";
						}
					}
				}
				else
				{
					return "You are not permitted to rename/move the directory!";
				}
				
			}
			else
				return "No such directory found!";
	
		}
		catch(MySQLIntegrityConstraintViolationException me)
		{
			return "File with the name - '" + newFileName + "' already exists.\n Try a different file name";
		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
		return "Could not rename/move the directory.";
	}


	public static void storeLastLoggedIn() 
	{
		java.sql.Timestamp sqlDate = new java.sql.Timestamp(System.currentTimeMillis());
		
		try
		{
			Connection con=connection.getConnection();                     //to get connected with the database
			Statement stmt=con.createStatement();
			String q="update user_details set last_logged_in = '" + sqlDate + "' where id = '" + id + "';";	//checking in the database if dir with given name exists
			stmt.executeUpdate(q);
		}catch(Exception e)
		{
			
		}
	}
	
	public static String help()
	{
		String mar="\n------------------------------------------------------------------------------------------\n";
		String list="List  -- To see all the files that are present in the root folder \n\t\t Syntax: list\n------------------------------------------------------------------------------------------\n";
		String upload="Upload -- To upload a file to the server \n \t\t Syntax: upload <Path> <File Name>\n\t-- To upload " +
				       "a file to server with desired name \n \t\t Syntax: upload path <File Name> <desired File Name\n------------------------------------------------------------------------------------------\n";
		String download="Download -- To download a file fromt the server \n \t\t Syntax: download <FileName> <Path>\n------------------------------------------------------------------------------------------\n";
		String delete="Delete -- To delete a file from the server\n\t\t Syntax: delete <File Name>\n------------------------------------------------------------------------------------------\n";
		String changedir="Change directory -- To change the directory we are working on\n\t\t Syntax: changedir <Path>\n------------------------------------------------------------------------------------------\n";
		String rename="Rename -- To rename the file in the server\n\t\t Syntax: rename <File Name> <New File Name>\n------------------------------------------------------------------------------------------\n";
		String logout="Logout -- To logout and close the session\n\t\t Syntax: logout\n------------------------------------------------------------------------------------------\n";
		String move="Move -- To move a file in the server from one directory to another\n\t\t Syntax: move <File Name> <path>\n------------------------------------------------------------------------------------------\n";
        String help="Help -- Display all the commands and syntax \n\t\t Syntax: help\n------------------------------------------------------------------------------------------\n";
		String newdir="New directory -- To create a new directory\n\t\t Syntax: newdir <directory>\n------------------------------------------------------------------------------------------\n";
		String cmp=mar+list+newdir+changedir+logout+upload+download+delete+rename+move+help;
		return cmp;
	}


	public static boolean usernameExists(String id) 
	{
		try
		{
			Connection con=connection.getConnection();                     //to get connected with the database
			Statement stmt=con.createStatement();
			String q="select * from user_details where id = '" + id + "';";	//checking in the database if dir with given name exists
			ResultSet res = stmt.executeQuery(q);
			
			if(res.next())
			{
				return true;
			}
			
		}catch(Exception e)
		{
			System.out.println("Error in user name existance check method");
		}
		return false;
	}

	

}






	



