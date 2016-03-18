package slime.media;

import java.io.IOException;

import org.farng.mp3.TagException;

/**
 * 
 * <b>
 * This Song Object is a simple wrapper class, for holding and allowing access to the Music file {@link #getSongPath()} as a String,
 * and the Music file meta-data in the {@link SongTag} object through {@link #getMetaTag()}.
 * </b>
 * 
 * @see SongTag
 * 
 * @author dMacGit
 *
 */

public class Song
{
	private String songPath;
	private SongTag metaTag;
	
	/*
	 * Song Object holds the path to the file as well as the tag data held in its own SongTag object.
	 * 
	 * When constructor called, the path is given as argument as well as the tag data in String array form,
	 * it then passes the data array directly to the song tag object on creation, to be initialized internally.
	 */
	public Song(String songPath, String[] tagDataArray, boolean libraryFlag) throws WrongFileTypeException, IOException, TagException
	{
			this.songPath = songPath;
			this.metaTag = new SongTag(tagDataArray, songPath, libraryFlag);
	}
		
	public String getSongPath() {
		return songPath;
	}

	public SongTag getMetaTag() {
		return metaTag;
	}

	
}
