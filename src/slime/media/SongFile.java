package slime.media;

import java.io.File;
import java.io.IOException;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;

public class SongFile implements MediaFile
{
	private MP3File mp3File;
	private SongTag metaTag;
	
	public SongFile(File fileToPlay)
	{
		try 
		{
			this.mp3File = new MP3File(fileToPlay);
			this.metaTag = new SongTag(fileToPlay);
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

	public SongTag readTag(){
		return metaTag;
	}
	
	@Override
	public void playSong() 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pauseSong() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopSong() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getFileType() {
		// TODO Auto-generated method stub
		
	}
}
