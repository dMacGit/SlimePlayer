package slime.media;

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
	private String playListName;
	private int playListRating;
	private int playCount;	
	private String genre;
	private int totalPlayTime;
	
	private SongTag currentTrack;
	private SongTag nextTrack;
	private SongTag previousTrack;
	
	public PlayList(SongList songs)
	{
		// DO Write out skeleton of PlayList object class
	}

}
