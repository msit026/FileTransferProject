

import java.io.File;



public class Command
{
	
	
	public static  String command(String cmd)       //command method to call the respective methods 
	{
		if(cmd.equalsIgnoreCase("list"))
			return  displayAllFiles();
		return null;
		
		
	}
	
	public static String displayAllFiles() //Displays all the files in the given directory
	{
		String path = "C:/Users/shalini/Contacts/Desktop/googleglass";   //path
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
	}
	



