package slime.media;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import slime.song.Song;
import slime.song.SongList;
import slime.song.SongTag;

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
	private final static String NAME = "[PlayList]";
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
		System.out.println(NAME+" Shuffle has been toggled to: "+Boolean.toString(isShuffled));
	}
	
	public void repeatToggled(boolean isRepeat){
		this.isRepeat = isRepeat;
	}
	
	public boolean chooseNextTrack()
	{
		boolean playerFoundNext = false;
		
		
			
		if (isShuffled && tracksLeft()) 
		{
			System.out.println(NAME + " [SHUFFLE-Mode] Getting next song!");
			boolean validated_Random = false;
			while (!validated_Random)
			{
				int tempIndex = (int) (Math.random() * listOfSongs.size());
				System.out.println(NAME + " Randomly Choosing number: " + tempIndex);

				if (!playListHistory.contains(tempIndex)) 
				{
					currentIndex = tempIndex;
					validated_Random = true;
					updatePlayerHistory(currentIndex);
				}

			}
			System.out.println(NAME + " Next Track has been choosen");

			currentTrack = listOfSongs.get(currentIndex);
			System.out.println(NAME + " <<< Next Track is " + currentIndex + " / " + listOfSongs.size() + " >>>");
			System.out.println(NAME + " [ " + currentTrack.getMetaTag().getArtist() + " | "
					+ currentTrack.getMetaTag().getSongTitle() + " | " + currentTrack.getMetaTag().getYear() + " ]");
			playerFoundNext = true;
		}
		else if(tracksLeft())
		{
			System.out.println(NAME + " [NORMAL-Mode] Getting next song!");
			int nextIndex = ++currentIndex;
			
			if (updatePlayerHistory(nextIndex))
			{
				currentTrack = listOfSongs.get(nextIndex);
				System.out.println(
						NAME + " PlayList selected next song: " + this.currentTrack.getMetaTag().getSongTitle());
				System.out.println(NAME + " Next Track has been choosen");

				currentTrack = listOfSongs.get(nextIndex);
				System.out.println(NAME + " <<< Next Track is " + nextIndex + " / " + listOfSongs.size() + " >>>");
				System.out.println(NAME + " [ " + currentTrack.getMetaTag().getArtist() + " | "
						+ currentTrack.getMetaTag().getSongTitle() + " | " + currentTrack.getMetaTag().getYear()
						+ " ]");
				playerFoundNext = true;
			}
			else 
			{
				System.out.println(NAME + " Reached the end of Playlist!");
				playerFoundNext = false;
				currentTrack = null;
			}
		}
		else 
		{
			System.out.println(NAME + " Reached the end of Playlist!");
			playerFoundNext = false;
			currentTrack = null;
		}

		// Make sure that the previous track has been remembered in the track
		// play history
		
		
			
		
		
		return playerFoundNext;
	}
	
	private boolean updatePlayerHistory(int indexToAdd)
	{
		if ( indexToAdd != -1) 
		{
			if(!playListHistory.contains(indexToAdd))
			{
				System.out.println(NAME + " Adding currently playing, to the Play-list history! [ "
						+ listOfSongs.get(currentIndex).getMetaTag().getSongTitle() + " || @ " + currentIndex + " ]");
				playListHistory.addLast(currentIndex);
				System.out.println(NAME + " Player Track History Updated");
				return true;
			}
			else
			{
				System.out.println(NAME + " Track ignored. Already in History!");
				return false;
			}
		}
		else
		{
			System.out.println(NAME + " Index Not valid!");
			return false;
		}
	}
	
	private boolean tracksLeft()
	{
		System.out.println(NAME+" End of playlist check On Index # "+(currentIndex+1)+" List size: "+listOfSongs.size());
		
		if(playListHistory.size()==listOfSongs.size())
		{
			System.out.println(NAME+" Reached End of Playlist!");
			if(isRepeat)
			{
				playListHistory.clear();
				currentIndex = -1;
				System.out.println(NAME+" Repeat enabled, so clearing history!");
				return true;
			}
			else
			{
				System.out.println(NAME+" Playlist Finished!");
				return false;
			}
		}
		else if((currentIndex+1)==listOfSongs.size())
		{			
			currentIndex = -1;
			System.out.println(NAME+" History not full, but reached end of Playlist. So reseting index!");
			return true;
		}
		else
			return true;
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
