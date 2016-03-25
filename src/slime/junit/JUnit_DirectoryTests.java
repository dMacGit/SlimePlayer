package slime.junit;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import slime.io.LibraryHelper;
import slime.io.MusicFileFilter;
import slime.io.RecursiveSearch;

public class JUnit_DirectoryTests
{
	protected static String TEST_DIRECTORY = "";
	//protected static manualLibraryCreation newLibrary;
	protected static MusicFileFilter testFilter;
	protected static File testParentFileDirectory;
	protected static File[] testFileList;
	protected static File musicFileOne, musicFileTwo;
	
	@BeforeClass
	public static void setUp() throws Exception
	{
		 //newLibrary = new manualLibraryCreation();
		TEST_DIRECTORY = System.getProperty("user.dir")+"/bin/slime/utills/TestFiles";
		testFilter = new MusicFileFilter();
		testParentFileDirectory = new File(TEST_DIRECTORY);
		musicFileOne = new File(TEST_DIRECTORY+"/Dragonballz Theme Song.mp3");
		musicFileTwo = new File(TEST_DIRECTORY+"/music/White Lion - Transformers Theme Song.mp3");
		testFileList = new File[] { musicFileOne, musicFileTwo};
	}
	
	@Test
	public void TestDirectory()
	{
		System.out.println("[ JUnit Directory Test ] (1) Directory Check ==> "+TEST_DIRECTORY);
		assertTrue(LibraryHelper.DirectoryChecker(TEST_DIRECTORY));
	}
	
	
	/*
	 * Unit Test to validate each of the test files.
	 * 
	 *  Note for the returned list from the call to ListFiles()
	 *  "There is no guarantee that the name strings in the resulting array will appear in any specific order!"
	 *  
	 */
	@Test
	public void TestFiles()
	{
		System.out.println("[ JUnit Directory Test ] (2) Is File Check ==> "+TEST_DIRECTORY);
		File[] tempList = new File(TEST_DIRECTORY).listFiles(); 
		File[] tempListTwo = new File(tempList[1].getAbsolutePath()).listFiles();	// <==== ListFiles() { Adjust [index] until returns correct value }.
		assertTrue(LibraryHelper.FileChecker(tempList[0]));
		assertTrue(LibraryHelper.FileChecker(tempListTwo[0]));
		assertEquals(tempList[0],testFileList[0]);
		assertEquals(tempListTwo[0],testFileList[1]);
	}
	
	/*
	 * Unit Test to validate each of the test files as .mp3 file types.
	 * 
	 *  Note for the returned list from the call to ListFiles()
	 *  "There is no guarantee that the name strings in the resulting array will appear in any specific order!"
	 *  
	 */
	@Test
	public void TestMusicFiles()
	{
		System.out.println("[ JUnit Directory Test ] (3) MP3 <.mp3> File Check ==> "+TEST_DIRECTORY);
		File tempParentFile = new File(TEST_DIRECTORY);
		File[] tempList = new File(TEST_DIRECTORY).listFiles(); 
		File[] tempListTwo = new File(tempList[1].getAbsolutePath()).listFiles();	// <==== ListFiles() { Adjust [index] until returns correct value }.
		assertTrue(LibraryHelper.MP3FileChecker(tempList[0], tempParentFile, testFilter ));
		assertTrue(LibraryHelper.MP3FileChecker(tempListTwo[0], new File(tempList[1].getAbsolutePath()), testFilter ));
		assertEquals(tempList[0],testFileList[0]);
		assertEquals(tempListTwo[0],testFileList[1]);
	}
	
	@Test
	public void TestDirectory_Recursively()
	{
		System.out.println("[ JUnit Directory Test ] (4) Directory Check <Recursive> ==> "+TEST_DIRECTORY);
		File[] tempList = RecursiveSearch.listFilesAsArray(testParentFileDirectory,testFilter,true);
		assertEquals(tempList[0],testFileList[0]);
		assertEquals(tempList[1],testFileList[1]);
	}
	
	@Test
	public void TestForNullFiles()
	{
		System.out.println("[ JUnit Directory Test ] (5) Null File Test ==> "+TEST_DIRECTORY);
		File[] tempList = RecursiveSearch.listFilesAsArray(testParentFileDirectory,testFilter,true);
		assertNotNull(tempList[0]);
		assertNotNull(tempList[1]);
	}	
}
