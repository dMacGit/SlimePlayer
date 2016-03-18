package slime.media;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * <B>
 * Currently this PlayList class is just a placeholder for being used in the future to hold and maintain
 * the playlist's state and other player specific information.
 * </B>
 * <p>
 * Currently the SlimePlayer just makes use of a large library of music, which by
 * default is treated the same way as a playlist. In future this should be moved into
 * a PlayList object instead, as realistically there can be many playlist saved in the
 * player by the user.
 * </p>
 * 
 * @see SongList
 * @see SongTag
 * 
 * @author dMacGit
 *
 */

public class PlayList
{
	//The Song-list DataStructure
	private SongList listOfSongs;
	
	//PlayListHistory keeps track of played or skipped songs
    private LinkedList<Integer> playListHistory;
	
	//The name of the play-list
	private final String playListName;
	
	private int playCount;	
	
	//The total number of tracks
	//private int total_Tracks = 0;
	
	//Play-list Duration
	private int totalPlayTime;
	
	//Play-list state
	private Song currentTrack = null;
	private int currentIndex = -1;
	private SongTag nextTrack;
	
	private SongTag previousTrack;
	
	private boolean isShuffled = false;
	private boolean isRepeat = false;
	
	//Set a max name length for the PlayList Name
	private final static int MAX_CHARACTERS = 30;
	
	/*
	 * Other optional variables for future features
	 */
	
	//Play-list Rating
	private final static int MAX_STARS = 5;
	private final static int MIN_STARS = 1;
	//Setting playlist rating to zero as not rated yet!
	private int playListRating = 0;
	
	//Play-list Genre
	private final static String UNSPECIFIED_GENRE = "None";

	
	private String genre = UNSPECIFIED_GENRE;
	
	
	
	public PlayList(SongList songs)
	{
		listOfSongs = songs;
		this.playListName = "New Playlist";
		playListHistory = new LinkedList<Integer>();
	}
	
	public PlayList(String playListName)
	{
		playListHistory = new LinkedList<Integer>();
		if(playListName != null && playListName.compareToIgnoreCase("")==0 && playListName.length() <= MAX_CHARACTERS)
		{
			this.playListName = playListName;
		}
		else 
			this.playListName = "New Playlist";
		
		//Creating an Empty SongList
		listOfSongs = new SongList();
		
	}
	
	public boolean addSongToPlaylist(Song songToAdd)
	{
		if(!listOfSongs.contains(songToAdd))
		{
			return listOfSongs.add(songToAdd);
		}
		else 
			return false;
	}
	
	public boolean removeSongFromPlaylist(Song songToAdd)
	{
		if(listOfSongs.contains(songToAdd))
		{
			return listOfSongs.remove(songToAdd);
		}
		else 
			return false;
	}
	
	public void shuffleToggled(boolean shuffled)
	{
		isShuffled = shuffled;
		System.out.println("[PlayList] Shuffle has been toggled to: "+Boolean.toString(isShuffled));
	}
	
	public void repeatToggled(boolean isRepeat){
		this.isRepeat = isRepeat;
	}
	
	public boolean chooseNextTrack()
	{
		boolean playerFoundNext = false;
		//Make sure that the previous track has been remembered in the track play history
		if(currentTrack != null && currentIndex != -1)
		{
			System.out.println("Adding last played to the Play-list history! [ "+listOfSongs.get(currentIndex).getMetaTag().getSongTitle()+" || @ "+currentIndex+" ]");
			playListHistory.addLast(currentIndex);
		}
		System.out.println("--- Player Track History Updated ---");
		//First Check for end of playlist
		if(playListHistory.size()== listOfSongs.size())
		{
			if(isRepeat)
			{
				playListHistory.clear();
			}
			System.out.println("--- End of playlist check ---");
		}
		else
		{
			System.out.println("--- Getting next song! ---");
			if(isShuffled)
			{
				System.out.println("--- In Shuffle Mode ---");
				boolean validated_Random = false;
				while(!validated_Random)
				{
					int tempIndex = (int)(Math.random()*listOfSongs.size());
					System.out.println("Randomly Choosing number: "+tempIndex);
		
					if(!playListHistory.contains(tempIndex))
					{
						currentIndex = tempIndex;
						validated_Random = true;
					}
					
				}
			}
			else
			{
				currentTrack = listOfSongs.get(++currentIndex);
				System.out.println("PlayList selected next song: "+this.currentTrack.getMetaTag().getSongTitle());
			}
			System.out.println("--- Next Track has been choosen ---");
			
			currentTrack = listOfSongs.get(currentIndex);
			System.out.println("<<< Next Track is "+currentIndex+" / "+listOfSongs.size()+" >>>");
			System.out.println("[ "+currentTrack.getMetaTag().getArtist()+" | "+currentTrack.getMetaTag().getSongTitle()+" | "+currentTrack.getMetaTag().getYear()+" ]");
			playerFoundNext = true;
		}
		
		return playerFoundNext;
	}
	
	public Song getCurrentTrack(){
		return this.currentTrack;
	}
	
	public SongTag getCurrentTrack_MetaData(){
		return this.getCurrentTrack().getMetaTag();
	}
	
	public String getCurrentTrack_File_Path(){
		return this.getCurrentTrack().getSongPath();
	}
	
	public int getTotalNumberTracks(){
		return this.listOfSongs.size();
	}
	
	public int getTotalDuration(){
		return this.totalPlayTime;
	}
	
	public int getPlayCount(){
		return this.currentIndex;
	}
	
	public List<SongTag> getSongTags() throws Exception
	{
		return listOfSongs.getMapOfTags();
	}

}
