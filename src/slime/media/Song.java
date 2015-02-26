package slime.media;

import java.io.File;
import java.io.IOException;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;

public class Song
{
	private String songPath;
	private SongTag metaTag;
	
	public Song(String libraryPathLine) throws WrongFileTypeException, IOException, TagException
	{
			this.songPath = libraryPathLine;
			this.metaTag = new SongTag(new File(libraryPathLine));
	}
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
