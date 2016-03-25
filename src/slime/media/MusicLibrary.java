package slime.media;

import java.util.ArrayList;
import java.util.Collection;

import slime.search.LibrarySearch;
import slime.search.SearchType;
import slime.song.SongList;
import slime.song.SongTag;

public class MusicLibrary
{
	private SongList libraryList;
	
	//The runnable class to search the library for search terms
	private LibrarySearch runnableSearcher;
	
	//Search results collection
	
	private final static String DEFAULT_MUSIC_DIRECTORY = "%USERPROFILE%\\My Documents\\My Music";
	
    private String defaultUserMusicDirectory = DEFAULT_MUSIC_DIRECTORY;
    private final String FILE_DIR = "Data_Files";
    
    private final String LIBRARY_FILE_NAME = "Lib_MP3player.txt", SONG_PATHS_FILE_NAME = "SongPaths.txt";
    
    private String HOLDINGS_FILE_PATH, SONG_PATHS_FILE_PATH, Durration, theCurrentSongTitle = null;
	
	public MusicLibrary(String homeUserMusicDir) 
	{
		this.defaultUserMusicDirectory = homeUserMusicDir;
	}
	
	public MusicLibrary()
	{
		/* ---- Operations to perform ----
		 * load library from files (Maybe in future try and optimize loading! )
		 * Library held in SongList
		 * 
		 * +++++++++++++++++++++++++++++++++++
		 * 
		 * Allow adding / Removing of files
		 * Allow creating and deleting PlayList objects
		 * Allow selecting / un-selecting of PlayList objects
		 * Allow stating / stopping PlayList objects. 
		 */
		
	}
	
	private ArrayList<SongTag> itemSearch(SearchType type, String term) throws IllegalArgumentException
	{
		if(type == null || (term == null || term.compareToIgnoreCase("")==0) ){
			throw new IllegalArgumentException();
		}
		
		if(type == SearchType.Artist && (term != null || term.compareToIgnoreCase("")==0) )
		{
			if(runnableSearcher == null)
			{
				runnableSearcher = new LibrarySearch(type, term);
			}
			else
			{
				runnableSearcher.clearSearch();
				runnableSearcher.newSearch(type, term);
			}
				
			
		}
		return new ArrayList<SongTag>();
	}
	
	
	
	
	private void generateMusicLibrary()
	{
		/*
		 * This will take a long time so make sure to use some sort of proxy-notification
		 * as well as making sure to use a separate runnable thread in order to not lock 
		 * the main thread.
		 */
		
		//This will populate a SongList (libraryList) object
	}
	
	/*
	 * Generic updateMusicLibrary method
	 * 
	 * Is called when there is song(s) added or removed from the library
	 * 
	 */
	private void updateMusicLibrary()
	{
		//Not sure what will happen in this method exactly yet!
	}
	
	/*
	 * AddMusicToLibrary Method specifically for when there is 
	 * a song or group of songs added to the library.
	 */
	private void addMusicToLibrary()
	{
		//Skeleton code...
	}
	
	/*
	 * RemoveMusicFromLibrary Method for when there is songs
	 * or group of songs to remove from the library.
	 */
	private void removeMusicFromLibray()
	{
		//Skeleton code..
	}

}
