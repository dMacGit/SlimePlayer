package slime.media;

import java.io.File;
import java.io.IOException;
import java.util.IllegalFormatException;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.tritonus.share.sampled.file.TAudioFileFormat;

public class SongTag 
{
	private TagVersion tagVersion = null;
	private String SongTitle, Artist, RecordingTitle  = null;
	private int Year, Durration = -1;
	
	private File audioFile = null;
	
	
	public SongTag(File songFile)throws WrongFileTypeException
	{
		audioFile = songFile;
		if(songFile.getName().contains(".mp3"))
		{
			try 
			{
				this.extractMetaTagInfo(new MP3File(songFile));
			}
			catch (IOException e)	{	e.printStackTrace();	}
			catch (TagException e)	{	e.printStackTrace();	}
		}
		else throw new WrongFileTypeException();
		
	}
	
	private void extractMetaTagInfo(MP3File songFile)
	{
		if(songFile.hasID3v1Tag())
		{
			tagVersion = TagVersion.ID3v1;
		} else if(songFile.hasID3v2Tag()){
			tagVersion = TagVersion.ID3v2;
		} else {
			System.out.println("Cannot read tag data! Incorrect tag version!");
		}
		
		if(tagVersion == TagVersion.ID3v1)
		{

			this.SongTitle = ValidateData(songFile.getID3v1Tag().getSongTitle());
            this.Artist = ValidateData(songFile.getID3v1Tag().getArtist());
            this.RecordingTitle = ValidateData(songFile.getID3v1Tag().getAlbum());
            try
            {
            	System.out.println("{"+songFile.getID3v1Tag().getYear()+"}");
            	this.Year = ValidateNumberData(Integer.parseInt(songFile.getID3v1Tag().getYear()));
            }
            catch(NumberFormatException e)
            {
            	e.printStackTrace();
            }
            int val = 0;
            AudioFileFormat baseFileFormat = null;
            try
            {
                baseFileFormat = AudioSystem.getAudioFileFormat(audioFile);
            }
            catch (UnsupportedAudioFileException ex)
            {
                System.out.println("Unsupported Audio File!! "+ex);
            } 
            catch (IOException e) {	e.printStackTrace();	}

            if (baseFileFormat instanceof TAudioFileFormat)
            {
                Map properties = ((TAudioFileFormat) baseFileFormat).properties();
                String key = "duration";
                val = Integer.parseInt(properties.get(key).toString())/1000000;
            }
            this.Durration = val;
			
		} 
		else if(tagVersion == TagVersion.ID3v2)
		{

			this.SongTitle = ValidateData(songFile.getID3v2Tag().getSongTitle());
            this.Artist = ValidateData(songFile.getID3v2Tag().getAuthorComposer());
            this.RecordingTitle = ValidateData(songFile.getID3v2Tag().getAlbumTitle());
            this.Year = ValidateNumberData(Integer.parseInt(songFile.getID3v2Tag().getYearReleased()));
            int val = 0;
            AudioFileFormat baseFileFormat = null;
            try
            {
                baseFileFormat = AudioSystem.getAudioFileFormat(audioFile);
            }
            catch (UnsupportedAudioFileException ex)
            {
                System.out.println("Unsupported Audio File!! "+ex);
            } 
            catch (IOException e) {	e.printStackTrace();	}

            if (baseFileFormat instanceof TAudioFileFormat)
            {
                Map properties = ((TAudioFileFormat) baseFileFormat).properties();
                String key = "duration";
                val = Integer.parseInt(properties.get(key).toString())/1000000;
            }
            this.Durration = val;
			
		}
	}
	private String ValidateData(String data)
	{
		System.out.println(data);
		if(data != null && !data.isEmpty()){
			return data;
		}
		else
		{
			return new String("NONE");
		}
	}
	private int ValidateNumberData(int data) throws NumberFormatException
	{
		System.out.println(data);
		if(data != 0 && data > 1000){
			return data;
		}
		else throw new NumberFormatException();
	}

	public TagVersion getTagVersion() {
		return tagVersion;
	}

	public String getSongTitle() {
		return SongTitle;
	}

	public String getArtist() {
		return Artist;
	}

	public String getRecordingTitle() {
		return RecordingTitle;
	}

	public int getYear() {
		return Year;
	}

	public int getDurration() {
		return Durration;
	}
	
	public String toString(){
		return new String("[ "+getArtist()+" ] "+getSongTitle()+" { "+getRecordingTitle()+" } "+getDurration());
	}
	
}
