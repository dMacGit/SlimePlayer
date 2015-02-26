package slime.media;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.tritonus.share.sampled.file.TAudioFileFormat;

import slime.utills.ActionTimer;
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
	private int Year, Durration = 0;
	
	private File audioFile = null;								//Audiofile variable only used in generation of song length
	private final String filePath;
	private final boolean Use_Alternative_Reader;
	
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
		filePath = songFile.getAbsolutePath();
		Use_Alternative_Reader = false;
		if(LibraryHelper.MP3FileChecker(songFile, fileDirectory, filter))
		{
				this.extractMetaTagInfo(new MP3File(songFile));
		}
		else throw new WrongFileTypeException();
	}
	
	/*
	 * Alternative constructor used in the quick load process during application start
	 * 
	 * Takes as argument the String array of tag data, as well as the alternative tag reader library flag
	 */
	public SongTag(String[] tagDataArray, String path, boolean useJAudioTagger)throws WrongFileTypeException, IOException, TagException
	{
		filePath = path;
		Use_Alternative_Reader = useJAudioTagger;
		quickLoadTag(tagDataArray);
	}
	
	//Alternative Constructor for faster loading
	public SongTag(File songFile, boolean useJAudioTagger)throws WrongFileTypeException, IOException, TagException
	{
		audioFile = songFile;
		filePath = songFile.getAbsolutePath();
		Use_Alternative_Reader = useJAudioTagger;
		if(Use_Alternative_Reader)
		{
			//MP3File newFile = new MP3File(songFile);
			

			try 
			{
				this.extractMetaTagInfo_JAudioTagger(AudioFileIO.read(songFile));
			} 
			catch (CannotReadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (org.jaudiotagger.tag.TagException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (ReadOnlyFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (InvalidAudioFrameException e) 
			{
				System.out.println("InvalidAudioFrameException! "+e.getMessage());
			}
		}
	}
	
	/*
	 * This method is used when Tag is created without first opening the
	 * music file: which is done in order to speed up application loading.
	 * 
	 * For more reliable tag data, the music file can be accessed if required.
	 */
	public void validateTagData()
	{
		//
		if(Use_Alternative_Reader)
		{
			//MP3File newFile = new MP3File(songFile);
			

			try 
			{
				this.extractMetaTagInfo_JAudioTagger(AudioFileIO.read(new File(this.filePath)));
			} 
			catch (CannotReadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (org.jaudiotagger.tag.TagException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (ReadOnlyFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (InvalidAudioFrameException e) 
			{
				System.out.println("InvalidAudioFrameException! "+e.getMessage());
			}
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void quickLoadTag(String[] tagData)
	{
		//Load the data taken from the tag array.
		
		// Song title | Artist | Album | Duration | Year | Popularity | Date added
		
		this.SongTitle = ValidateData(tagData[0]);
        this.Artist = ValidateData(tagData[1]);
        this.RecordingTitle = ValidateData(tagData[2]);
        try
        {
        	this.Year = ValidateNumberData(Integer.parseInt(tagData[4]));
        }
        catch(NumberFormatException e)
        {
        	this.Year = 0000;
        }
        try
        {
        	this.Durration += ValidateNumberData(Integer.parseInt(tagData[3]));
        }
        catch(NumberFormatException e)
        {
        	this.Durration = 0;
        }
        
        
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
	 * Alternative method to find time spent per operation.
	 * - Only used for JUnit tests/Debug purposes.
	 */
	private void extractMetaTagInfo_JAudioTagger(AudioFile songFile)
	{
		long timeStart = ActionTimer.triggerTimedActionStart();
		Tag tagObject = songFile.getTag();
		
		
		this.SongTitle = ValidateData(tagObject.getFirst(FieldKey.TITLE));
        this.Artist = ValidateData(tagObject.getFirst(FieldKey.ARTIST));
        this.RecordingTitle = ValidateData(tagObject.getFirst(FieldKey.ALBUM));
        try
        {
        	this.Year = ValidateNumberData(Integer.parseInt(tagObject.getFirst(FieldKey.YEAR)));
        }
        catch(NumberFormatException e)
        {
        	this.Year = 0000;
        }
        tagObject.getFirst(FieldKey.TRACK_TOTAL);
        AudioHeader audioHeader = songFile.getAudioHeader();
        this.Durration = audioHeader.getTrackLength();
			
		
		System.out.println(ActionTimer.formatLastTimedAction("Extracting the meta data", ActionTimer.measurePreviouseActionTime(timeStart, System.currentTimeMillis())));
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
