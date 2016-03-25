package slime.io;

import java.io.File;
import java.io.FilenameFilter;

public class LibraryHelper 
{
	public static boolean DirectoryChecker(String directory)
    {
    	File directoryToCheck = new File(directory);
    	if(directoryToCheck.isDirectory() && directoryToCheck.listFiles().length > 0)
        {
        	return true;
        }
        else
        	return true;
    }
    
    public static boolean FileChecker(File fileToCheck)
    {
    	if( fileToCheck.isFile() )
        {
        	return true;
        }
        else
        	return true;
    }
    
    public static boolean MP3FileChecker(File fileToCheck, File fileDirectory, FilenameFilter fileFilter)
    {
    	if( fileToCheck.isFile())
        {
    		
    		if (fileFilter == null || fileFilter.accept(fileDirectory, fileToCheck.getName())) 
    		{
                return true;
            }
    		else
    			return false;
        }
        else
        	return false;
    }
    
    /*
    *
    * File path grabber
    * 
    * Grabs the path to the validated music file and adds it to the String Array!
    * 
    */
   public static String[] MusicFilePathGrabber(File[] fileArray)
   {
	   	//Iterate over the list and extract the paths!
	   	String[] dirs = new String[fileArray.length];
	   	
	   	for(int index = 0; index < fileArray.length; index++)
	   	{
	   		dirs[index] = fileArray[index].getPath();
	   	}
	   	return dirs;
   }
   
   public static String removeQuotes(String string){
		return string.substring(1,string.lastIndexOf('"'));
	}
}
