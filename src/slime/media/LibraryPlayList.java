package slime.media;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.farng.mp3.TagException;

import slime.utills.ActionTimer;
import slime.utills.FileIO;

public class LibraryPlayList extends PlayList 
{
	private final static String NAME = "[LibraryPlayList]";
	
	private static String DEFAULT_HOME_MUSIC_DIR;
	private static String PLAYER_ROOT;
	private static String PLAYER_DATA_DIR;
	private static String PATHS_FILE;
	private static String LIBRARY_FILE;
	
	private static final String START_DATE_DELIMITOR = "'['", MID_DATE_DELIMITOR = "','", END_DATE_DELIMITOR = "']'";
    private static final String CHARACTER_SEPERATOR = "&&";
	
    
	public LibraryPlayList()
	{
		super("Library");
		
		Properties config = new Properties();
    	try
    	{
			config.load(new FileInputStream("player.properties"));
			DEFAULT_HOME_MUSIC_DIR = removeQuotes(config.getProperty("DIR"));
			PLAYER_ROOT = /*removeQuotes(config.getProperty("PLAYER_ROOT"));*/System.getProperty("user.dir");
			PLAYER_DATA_DIR = removeQuotes(config.getProperty("PLAYER_DATA_DIR"));
			PATHS_FILE = removeQuotes(config.getProperty("PATHS_FILE"));
			LIBRARY_FILE = removeQuotes(config.getProperty("LIBRARY_FILE"));
			
			System.out.println(NAME+" Properties File READ...\nDefault Music Directory set to: "+DEFAULT_HOME_MUSIC_DIR
				+"\nDefault Player path is: "+PLAYER_ROOT+"\nData Directory is: "+PLAYER_DATA_DIR+"\nPaths file name is ["+PATHS_FILE+
				"]\nLibrary file name is: "+LIBRARY_FILE+"]");
		}
    	catch (FileNotFoundException ex) 
    	{
			
			ex.getMessage();
		}
    	catch (IOException e1) 
    	{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void toggleShuffle(boolean shuffle){
		super.shuffleToggled(shuffle);
	}
	
	private String removeQuotes(String string){
		return string.substring(1,string.lastIndexOf('"'));
	}
	
	public void createLibraryPlaylist()
	{
		long startTime = 0;
    	long addTimeStart = 0;
    	Object[] libraryDataArray = null;
    	
    	String LibraryFilePath = PLAYER_ROOT+"\\"+PLAYER_DATA_DIR+"\\"+LIBRARY_FILE;
    	try 
    	{
    		long startReadingFiles = ActionTimer.triggerTimedActionStart();
    		System.out.println(LibraryFilePath);
    		libraryDataArray = FileIO.ReadData(LibraryFilePath);
    		
    		System.out.println(ActionTimer.formatLastTimedAction("Read Files",ActionTimer.measurePreviouseActionTime(startReadingFiles, System.currentTimeMillis())));
    		
    		//To reduce initialization time set map size.
    		
    		long totalTimeForListCreation = 0;
			for(int index = 0; index < libraryDataArray.length; index++)
	    	{
				//songTags.add(libraryDataArray[index].toString());
				
				//File structure: [ data | data | data | data ],[ data ]
				String[] mainDataArray = libraryDataArray[index].toString().split(MID_DATE_DELIMITOR);
				String filePath = mainDataArray[1].substring(mainDataArray[1].indexOf(START_DATE_DELIMITOR)+START_DATE_DELIMITOR.length(),mainDataArray[1].indexOf(END_DATE_DELIMITOR));
				    				
				final String wholeTagString = mainDataArray[0];
				
				String tempSubstring = wholeTagString.substring(wholeTagString.indexOf(START_DATE_DELIMITOR)+START_DATE_DELIMITOR.length(),wholeTagString.indexOf(END_DATE_DELIMITOR));
				String[] tagDataArray = null;
				tagDataArray = tempSubstring.split(CHARACTER_SEPERATOR);
				try 
    			{
    				long timeTaken = 0;
    				addTimeStart = ActionTimer.triggerTimedActionStart();
					super.addSongToPlaylist(new Song(filePath,tagDataArray,true));
					timeTaken = ActionTimer.measurePreviouseActionTime(addTimeStart, System.currentTimeMillis());
					totalTimeForListCreation += timeTaken;
				} 
    			catch (WrongFileTypeException | TagException e)
    			{
					System.out.println(NAME+" Error trying to build the Tag!");
				}
	    	}
    		System.out.println(ActionTimer.formatLastTimedAction(NAME+" Total time to add ",totalTimeForListCreation));    		
		}
    	catch (FileNotFoundException e) 
    	{
    		System.out.println(NAME+" FileNotFoundException Populating Library! "+e.getMessage());
		}
    	catch (IOException e) 
    	{
    		System.out.println(NAME+" IOException Populating Library! "+e.getMessage());
		}
	}	

}
