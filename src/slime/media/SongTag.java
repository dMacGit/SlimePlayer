package slime.media;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.tritonus.share.sampled.file.TAudioFileFormat;

import slime.utills.LibraryHelper;
import slime.utills.MusicFileFilter;

/*
 * The SongTag class maintains access to the Tag information off the
 * piece of music as well as error checking and null checking upon
 * Tag creation.
 */

public class SongTag 
{
	private TagVersion tagVersion = null;						//Important if expanded or replaced in future 
	private String SongTitle, Artist, RecordingTitle  = null;	//Critical to set to null
	private int Year, Durration = -1;
	
	private File audioFile = null;								//Audiofile variable only used in generation of song length
	
	/*
	 * Main constructor of the class.
	 * 
	 * Once the Inputed .mp3 file has been validated, calls the extractMetaTagInfo method.
	 * Extract method does Tag version checks, then correctly extracts and individually
	 * checks the tag data by using the appropriate VadidateData(String) OR ValidateData(Int)
	 * methods. 
	 * 
	 * [WrongFileTypeInfo] Error Thrown if not of .mp3 file type.
	 * [IOException] Error Thrown if general Error when reading the file.
	 * [TagException] Error Thrown if error when reading the getName tag field.
	 * 
	 */
	public SongTag(File songFile)throws WrongFileTypeException, IOException, TagException
	{
		this(songFile,new MusicFileFilter(),new File(songFile.getParent()));
	}
	public SongTag(File songFile, MusicFileFilter filter, File fileDirectory)throws WrongFileTypeException, IOException, TagException
	{
		audioFile = songFile;
		if(LibraryHelper.MP3FileChecker(songFile, fileDirectory, filter))
		{
				this.extractMetaTagInfo(new MP3File(songFile));
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
			System.out.println("Cannot read tag data! Incorrect tag version! ");
		}
		
		if(tagVersion == TagVersion.ID3v1)
		{

			this.SongTitle = ValidateData(songFile.getID3v1Tag().getSongTitle());
            this.Artist = ValidateData(songFile.getID3v1Tag().getArtist());
            this.RecordingTitle = ValidateData(songFile.getID3v1Tag().getAlbum());
            try
            {
            	this.Year = ValidateNumberData(Integer.parseInt(songFile.getID3v1Tag().getYear()));
            }
            catch(NumberFormatException e)
            {
            	this.Year = 0000;
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
            try
            {
	            this.Year = ValidateNumberData(Integer.parseInt(songFile.getID3v2Tag().getYearReleased()));
            }
            catch(NumberFormatException e)
            {
            	this.Year = 0000;
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
	}
	
	/*
	 * Validate method for String literals
	 * 
	 * Checks for Null data value, and returns "Unknown" String if Null.
	 */
	private String ValidateData(String data)
	{
		if(data != null && !data.isEmpty()){
			return data;
		}
		else
		{
			return new String("Unknown");
		}
	}
	
	/*
	 * Validate method for Integer Primitives
	 * 
	 * Checks data within valid range of values.
	 * [NumberFormatException] Error Thrown if data not within valid range.
	 */
	private int ValidateNumberData(int data) throws NumberFormatException
	{
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
