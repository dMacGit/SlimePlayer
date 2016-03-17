package slime.media;

import java.util.LinkedList;


/**
 * <b>
 * The LibrarySearch class is the class used for any and all searches needed
 * through the entire library list of songs.
 * </b>
 * <p>
 * It supports searching via the use of a String search term, as well as
 * for a specific {@link SearchType} such as Artists, Albums, and Tracks.
 * It is {@link Runnable}, and returns results as a {@link LinkedList} of {@link SongTag} objects.
 * </p>
 * 
 * @see Runnable
 * @see LinkedList
 * @see SongTag
 * 
 * @author dMacGit
 *
 */
public class LibrarySearch implements Runnable 
{
	
	private String term;
	private SearchType type;
	
	//The Collection of results that match the search term!
	private LinkedList<SongTag> results;
	
	/**
	 * <b>The Main LibrarySearch Constructor</b>
	 * <p>
	 * This is used when the class is created when needed to be used.
	 * It makes use of the {@link SearchType} enumerator, as well as s
	 * search term string.
	 * </p>
	 * 
	 * @param type
	 * @param term
	 */
	public LibrarySearch(SearchType type, String term)
	{
		this.type = type;
		this.term = term;
		//this.run();
	}
	
	/**
	 * <b>Alternative Constructor for use as singleton at first startup</b>
	 * <p>
	 * This is so that at startup, a singleton instance of this class can be
	 * instantiated, regardless of needing to be used. Hence it has no arguments
	 * </p>
	 */
	public LibrarySearch()
	{
		//
	}
	
	
	public void clearSearch()
	{
		this.results.clear();
	}
	
	public void newSearch(SearchType type, String term){
		this.type = type;
		this.term = term;
		//this.run();
	}

	@Override
	public void run() 
	{
		
		
		
	}
}
