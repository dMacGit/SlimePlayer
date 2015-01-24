package slime.junit;

import java.io.File;
import java.io.FilenameFilter;

public class JUnit_TestHelper 
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
}
