package slime.media;

import java.io.File;
import java.io.IOException;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;

public class Song
{
	private String songPath;
	private SongTag metaTag;
	
	public Song(String libraryPathLine)
	{
		try 
		{
			this.songPath = libraryPathLine;
			this.metaTag = new SongTag(new File(libraryPathLine));
		}
		catch ( IOException e) 
		{	
			e.printStackTrace();
		}
		catch ( TagException e) 
		{	
			e.printStackTrace();
		}
		catch( WrongFileTypeException e)
		{ 
			e.printStackTrace();
		}
	}

	public String getSongPath() {
		return songPath;
	}

	public SongTag getMetaTag() {
		return metaTag;
	}

	
}
