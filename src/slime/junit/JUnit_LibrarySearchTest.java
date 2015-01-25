package slime.junit;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.farng.mp3.TagException;
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
			SongTag testTag = new SongTag(musicFileOne);	//<--- Incomplete music tag (Has NULL fields!)
			assertEquals(testTag.getSongTitle(),"Dragonballz Theme Song");
			assertEquals(testTag.getArtist(),"Unknown");
			assertEquals(testTag.getRecordingTitle(),"Unknown");
			//System.out.println(testTag.toString());
			
			SongTag testTagTwo = new SongTag(musicFileTwo);
			assertEquals(testTagTwo.getSongTitle(),"Transformers Theme Song");
			assertEquals(testTagTwo.getArtist(),"White Lion");
			assertEquals(testTagTwo.getRecordingTitle(),"soundtrack");
			
		}
		catch(WrongFileTypeException | IOException | TagException e){
			e.printStackTrace();
		}
		
	}

}
