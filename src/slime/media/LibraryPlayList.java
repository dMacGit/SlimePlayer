package slime.media;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.farng.mp3.TagException;

import slime.exceptions.WrongFileTypeException;
import slime.io.FileIO;
import slime.song.Song;
import slime.utills.ActionTimer;

public class LibraryPlayList extends PlayList 
{
	private final static String NAME = "[LibraryPlayList]";
	
	
	private final static String DEFAULT_PROPERTIES_FILE = "slimeplayer.properties";
	
	private final static String DEFAULT_MUSIC_HOME = "%USERPROFILE%\\My Documents\\My Music";
	private final static String DEFAULT_ROOT = "\\";
	private final static String DEFAULT_DATA_DIR_NAME = "Data_Files";
	private final static String DEFAULT_LIBRARY_FILE_NAME = "Library.txt";
	//private final static String DEFAULT_PATHS_FILE_NAME = "Song_Paths.txt";
	
	private static String Music_Home, Root, Data_Dir, Library_File, /*Song_Paths_File, */Library_File_Path;
	
	private static final String START_DATE_DELIMITOR = "'['", MID_DATE_DELIMITOR = "','", END_DATE_DELIMITOR = "']'";
    private static final String CHARACTER_SEPERATOR = "&&";
	
    
	public LibraryPlayList()
	{
		super("Library");
		Root = System.getProperty("user.home");
		String logFileName = "slime_log.txt"; 
		File logFile = new File(Root,logFileName);
		//LibraryFile_Path = ;
		
    	try
    	{
    		Properties config = new Properties();
    		FileInputStream fin = new FileInputStream(Root+"\\"+DEFAULT_PROPERTIES_FILE);
    		config.load(fin);
    		
    		Music_Home = removeQuotes(config.getProperty("DIR"));
    		Data_Dir = removeQuotes(config.getProperty("PLAYER_DATA_DIR"));
    		Library_File = removeQuotes(config.getProperty("LIBRARY_FILE"));
    		//Song_Paths_File = removeQuotes(config.getProperty("LIBRARY_FILE"));
    		
    		System.out.println(NAME+" Properties File READ...\nDefault Music Directory set to: "+Music_Home
    			+"\nDefault Player path is: "+Root+"\nData Directory is: "+Data_Dir+"\nLibrary file name is: "+Library_File+"]");
    		fin.close();
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
    	
    	
    	
    	checkDirectories();
		try 
		{
			logFile.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(logFile));
			bw.write(logFile.getPath());
			//bw.newLine();
			bw.flush();
			bw.close();
			
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void toggleShuffle(boolean shuffle){
		super.shuffleToggled(shuffle);
	}
	
	private String removeQuotes(String string){
		return string.substring(1,string.lastIndexOf('"'));
	}
	
	private void checkDirectories()
	{
		String log = "";
		try
		{
			
		/*File rootDir = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toString());
        File realRoot = new File(rootDir.getParent());
        String validatedPath = new String( realRoot.getPath().substring(realRoot.getPath().indexOf("\\")+1).replace("%20", " "));
        //File rDir = new File(rootDir);
        File validatedRoot = new File(validatedPath);
        File dataDir = new File(validatedRoot.getPath()+"\\"+PLAYER_DATA_DIR);
        System.out.println("PLAYER_DATA_DIR: "+validatedPath+" + \\ + "+PLAYER_DATA_DIR);
        PLAYER_DATA_DIR = dataDir.getPath();
        log += "PLAYER_DATA_DIR: "+validatedPath+" + \\ + "+PLAYER_DATA_DIR+"\n";
        //currentDirectoryToSearch.setText(validatedPath.toString());*/
		File dataDirFile = new File(Root+"\\"+Data_Dir);
        if(dataDirFile.exists())
        {
        	//currentDirectoryToSearch.setText("DATA_DIR Doesn't Exist!");
        	System.out.println("DATA_DIR Found!");
        	log += "DATA_DIR Found!\n";
        	Library_File_Path = dataDirFile.getPath()+"\\"+this.Library_File;
        	log += "Library_File_Path is "+Library_File_Path+"\n";
        }
        else
        	System.out.println("DATA_DIR Doesn't Exist!");
        	log += "DATA_DIR Doesn't Exist!\n";
		}
		catch(Exception ex)
		{
			log += "Exception!";
		}
		File writeFile = new File(Root+"\\log.txt");
		try 
		{
			writeFile.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(writeFile));
			bw.write(log);
			//bw.newLine();
			bw.flush();
			bw.close();
			
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void createLibraryPlaylist()
	{
		long startTime = 0;
    	long addTimeStart = 0;
    	Object[] libraryDataArray = null;
    	
    	String LibraryFilePath = Library_File_Path;
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
