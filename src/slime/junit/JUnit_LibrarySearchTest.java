package slime.junit;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import slime.media.SongTag;
import slime.media.WrongFileTypeException;
import slime.utills.MusicFileFilter;
import slime.utills.RecursiveSearch;
import slime.utills.manualLibraryCreation;

public class JUnit_LibrarySearchTest 
{
	protected static String TEST_DIRECTORY = "";
	protected static String PROJECT_DIRECTORY = "D:/Users/Phantom/Documents/D Programing/Eclipse Projetcs/Java/SlimePlayer/bin/slime"; // <-- Change to represent the project DIR!
	protected static File parentDirectory;
	protected static manualLibraryCreation libraryClass;
	protected static File musicFileOne, musicFileTwo;
	
	@BeforeClass
	public static void setUp() throws Exception 
	{
		//Add Code for any variables and any parameters for tests!
		TEST_DIRECTORY = System.getProperty("user.dir")+"/bin/slime/utills/TestFiles";
		parentDirectory = new File(TEST_DIRECTORY);
		libraryClass = new manualLibraryCreation();
		musicFileOne = new File(TEST_DIRECTORY+"/Dragonballz Theme Song.mp3");
		musicFileTwo = new File(TEST_DIRECTORY+"/music/White Lion - Transformers Theme Song.mp3");
		
	}

	@Test
	public void SearchTest() 
	{
		File[] filePathsArray = RecursiveSearch.listFilesAsArray(parentDirectory, new MusicFileFilter(), true);
		String[] musicPaths = libraryClass.MusicFilePathGrabber(filePathsArray);
		//libraryClass.searchTheDirectory(musicPaths);
	}
	
	@Test
	public void MusicTagTest()
	{
		//Validate critical tag information
		
		//First Validate the first music file!
		try{
			SongTag testTag = new SongTag(musicFileOne);
			System.out.println(testTag.toString());
			
		}
		catch(WrongFileTypeException e){
			e.printStackTrace();
		}
		
	}

}
