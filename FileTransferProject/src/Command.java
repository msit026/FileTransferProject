

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



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
    static String path;
    static int count;
    public static void setid(String i)
    {
    	id=i;	
    }    
    
    
	public static  String command(String cmd) throws 
IOException
	{
		System.out.println("inside cmd");
		if(cmd.equalsIgnoreCase("list"))
			return  displayAllFiles();
		
		if(cmd.contains("upload"))
		{
			System.out.println("inside upload");
			String destinationpath="D:/FTP";
			String[] k=cmd.split(" ");
			k[1].replace('\\','/');
			String path=k[1]+"/"+k[2];
			System.out.println(path);
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
				
				
				
				
				
//				File f=new File(kr);
//				System.out.println("x="+destinationpath+"/"+k[2]);
//		         FileReader fr = null;
//		         FileWriter fw=null;
//		         String temp="";
//					try {
//						System.out.println(path);
//						fr = new FileReader(path);
//						//System.out.println("after fr");
//                        fw=new FileWriter(f);
//                       // System.out.println("after fw");
//						int c =fr.read();
//						while (c != -1)
//						{
//							fw.write(c);
//							c = fr.read();
//						}
//						fw.close();
//						String aa="Uploaded sucessfully";
//						return aa;
//			        }
//					catch (IOException e) {
//						e.printStackTrace();
//					}
					
			}
			else
			{
				File f=new File(destinationpath+"/"+k[3]);
				//System.out.println(destinationpath+"\\\\"+k[3]);
				FileReader fr = null;
		         FileWriter fw=null;
		         String temp="";
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
						String aa="uploaded sucessfully with name "+k[3];
						return aa;
			        }
					catch (IOException e) {
						e.printStackTrace();
					}
					
				
			}
			
		}
		return null;
		
		
	}
	
	public static void uploadintofile(byte[] x) throws 
IOException
	{
		buf=new byte[14*1024];
		buf=x;
		
//		if(buf.length>0)
//		   {
		     bos.write(buf);
		   //}
		//fos.close();
		
	}
	
	public static void closeFile() throws Exception
	{
		fos.close();
	}
	
	
	public static String createfile(String cmd) throws FileNotFoundException
	{
		System.out.println("inside create file");
		String[] a=cmd.split(" ");
		destinationpath="D:/FTP";
		if(a.length==3)
		{
		 path=destinationpath+"/"+a[2];
		 filename=a[2];
		}
		else
		{
			path=destinationpath+"/"+a[3];
			filename=a[3];
		}
		System.out.println("path: "+path);
		//File f=new File("path");
		fos=new FileOutputStream(path);
		bos= new BufferedOutputStream(fos);
		return null;	
	}
	
	
	public static void storeInDB() throws Exception
	{
		Connection con=connection.getConnection();                     //to get connected with the database
		Statement stmt=con.createStatement();
		   File f=new File(path);
		   String qry="insert into files values('"+path+"','"+id+"','"+f.length()+"','0','"+filename+"');";
		   System.out.println(qry);
		   boolean b=stmt.execute(qry);
		   if(b)
			   System.out.println("sucessfully added");
		   else
			   System.out.println("not uploaded correctly");   
	}
	
	
	
	public static String displayAllFiles() //Displays all the files in the given directory
	{
		System.out.println("inside displayfiels");
		String path = "D:/FTP";   //path
		  String files="";
		  File folder = new File(path);
		  File[] listOfFiles = folder.listFiles(); // gives the name of all the files
		 
		  for (int i = 0; i < listOfFiles.length; i++) 
		  {
		 
		   if (listOfFiles[i].isFile()) 
		   {
		      files =files+"#"+listOfFiles[i].getName(); // all files in a string with # as delimiter
		   }
		  }
		return files;
		}
	
	
	public static void initiateDownLoad(String command) 
	{
		System.out.println("inside initiate download");
		Connection con;
		count=0;
		try {
			con = connection.getConnection();
		              //to get connected with the database
		Statement stmt=con.createStatement();
		String[] a=command.split(" ");
		String str="select * from files where file_name='"+a[1]+"';";
		//System.out.println(str);
		ResultSet r=stmt.executeQuery(str);
		if(r.next())
		{
			path=r.getString(1);
		}
		in=new FileInputStream(path);
		bis=new BufferedInputStream(in);
		//Command.downLoad();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}       
	}
	
	
	public static byte[] downLoad() throws Exception
	{
		   buf=new byte[14*1024];
		   byte[] array = null;
		   int c;
		   int off=0;
//		   bis.read(buf);
//		   bos.write(buf,0,buf.length);
		   if((c=bis.read(buf))>0)
		   {
			  
			   array=new byte[c];
			   System.arraycopy(buf, 0, array, 0,c);
			  // System.out.println(buf.length + "-- In while of command : " +count);
			   count++;
			   return array;  
		   }
		  return array;
		  
	}
	
	
	
	}
	



