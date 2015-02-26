package slime.managers;

import java.awt.Color;
import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.farng.mp3.TagException;

import slime.controller.MediaController;
import slime.controller.ScrollingTextController;
import slime.core.PlaySongControls;
import slime.media.PlayState;
import slime.media.Song;
import slime.media.SongList;
import slime.media.SongTag;
import slime.media.WrongFileTypeException;
import slime.observe.MediaObserver;
import slime.utills.ActionTimer;
import slime.utills.FileIO;

public class MusicLibraryManager implements MediaObserver
{
    private LinkedList<String> songTags;
    private boolean currentlyPlaying = false, stop = false, isPaused;
    private Thread songThread;
    private ScrollingTextController label;
    private JLabel scrollingTitleLabel;
    private int labelWidth;
    private final String HOLDINGS_FILE_NAME = "Lib_MP3player.txt", SONG_PATHS_FILE_NAME = "SongPaths.txt";
    private String HOLDINGS_FILE_PATH, SONG_PATHS_FILE_PATH, Durration, theCurrentSongTitle = null, FILE_DIR;
    private String MEDIA_OBSERVER_NAME = "PlaySongsFormFolder";
    private PlayState playerCurrentState = PlayState.STOPPED;
    private SongTag currentPlayingSongTag = null;
    private MediaController mediaController;
    private SongList listOfSongs;
    private Song currentSong;
    private String startDataDelimitor = "'['", midDataDelimitor = "','", endDataDelimitor = "']'";
    private String characterSeperator = "&&";

    public MusicLibraryManager(String dir)
    {
        FILE_DIR = dir+"/";
        HOLDINGS_FILE_PATH = FILE_DIR+HOLDINGS_FILE_NAME;
        SONG_PATHS_FILE_PATH = FILE_DIR+SONG_PATHS_FILE_NAME;
        mediaController = new MediaController();
        songTags = new LinkedList<String>();
        listOfSongs = new SongList();
        scrollingTitleLabel = new JLabel("");
        scrollingTitleLabel.setPreferredSize(new Dimension(225,25));
        scrollingTitleLabel.setMinimumSize(new Dimension(225,25));
        scrollingTitleLabel.setMaximumSize(new Dimension(225,25));
        scrollingTitleLabel.setForeground(Color.WHITE);
        populateLibrary();
        playRandomSong play = new playRandomSong();
        play.start();
        System.out.println("Player has been Started!");
    }
    
    public MusicLibraryManager(SongList playList)
    {
        HOLDINGS_FILE_PATH = FILE_DIR+HOLDINGS_FILE_NAME;
        SONG_PATHS_FILE_PATH = FILE_DIR+SONG_PATHS_FILE_NAME;
        mediaController = new MediaController();
        songTags = new LinkedList<String>();
        listOfSongs = playList;
        scrollingTitleLabel = new JLabel("");
        scrollingTitleLabel.setPreferredSize(new Dimension(225,25));
        scrollingTitleLabel.setMinimumSize(new Dimension(225,25));
        scrollingTitleLabel.setMaximumSize(new Dimension(225,25));
        scrollingTitleLabel.setForeground(Color.WHITE);
        populateLibrary();
        playRandomSong play = new playRandomSong();
        play.start();
        System.out.println("Player has been Started!");
    }
    public LinkedList<String> getMapOfSong()
    {
        return songTags;
    }
    
    public void populateLibrary()
    {
    	long startTime = 0;
    	long addTimeStart = 0;
    	Object[] libraryDataArray = null;
    	try 
    	{
    		long startReadingFiles = ActionTimer.triggerTimedActionStart();
    		libraryDataArray = FileIO.ReadData(this.HOLDINGS_FILE_PATH);
    		System.out.println(ActionTimer.formatLastTimedAction("Read Files",ActionTimer.measurePreviouseActionTime(startReadingFiles, System.currentTimeMillis())));
    		
    		//To reduce initialization time set map size.
    		
    		long totalTimeForListCreation = 0;
			listOfSongs.setCappacity(libraryDataArray.length);
			for(int index = 0; index < libraryDataArray.length; index++)
	    	{
				songTags.addLast(libraryDataArray[index].toString());
				
				//File structure: [ data | data | data | data ],[ data ]
				String[] mainDataArray = libraryDataArray[index].toString().split(midDataDelimitor);
				String filePath = mainDataArray[1].substring(mainDataArray[1].indexOf(startDataDelimitor)+startDataDelimitor.length(),mainDataArray[1].indexOf(endDataDelimitor));
				    				
				final String wholeTagString = mainDataArray[0];
				
				String tempSubstring = wholeTagString.substring(wholeTagString.indexOf(startDataDelimitor)+startDataDelimitor.length(),wholeTagString.indexOf(endDataDelimitor));
				String[] tagDataArray = null;
				tagDataArray = tempSubstring.split(characterSeperator);
				try 
    			{
    				long timeTaken = 0;
    				addTimeStart = ActionTimer.triggerTimedActionStart();
					listOfSongs.addSong(new Song(filePath,tagDataArray,true));
					timeTaken = ActionTimer.measurePreviouseActionTime(addTimeStart, System.currentTimeMillis());
					totalTimeForListCreation += timeTaken;
				} 
    			catch (WrongFileTypeException | TagException e)
    			{
					System.out.println("Error trying to build the Tag!");
				}
	    	}
    		System.out.println(ActionTimer.formatLastTimedAction("Total time to add ",totalTimeForListCreation));    		
		}
    	catch (FileNotFoundException e) 
    	{
    		System.out.println("FileNotFoundException Populating Library! "+e.getMessage());
		}
    	catch (IOException e) 
    	{
    		System.out.println("IOException Populating Library! "+e.getMessage());
		}
    	
    }
    
    private void arrayToString(Object[] arrayOfData){
    	
    	System.out.println("Starting To test the array! ");
		
		for(int stringIndex = 0; stringIndex < arrayOfData.length; stringIndex++)
		{
			//print out the array of data for debug purposes!
			System.out.println("Index: [ "+stringIndex+" ] "+arrayOfData[stringIndex].toString()+",");
		}
    }

    public class playRandomSong extends Thread
    {
        public playRandomSong()
        {
        }

        @Override
        public void run()
        {
            while(!stop)
            {
                if(!currentlyPlaying)
                {
                	if(!mediaController.hasPlaylist())
                	{
                		mediaController.setSongList(listOfSongs);
                	}
                	                    
                    currentSong = mediaController.getCurrentSong();
					currentPlayingSongTag = currentSong.getMetaTag();
					
					System.out.println("Current Song / Next Song to play: "+currentPlayingSongTag.getSongTitle());

                    int checkTime = (currentPlayingSongTag.getDurration())/2;
                    int realTime = (checkTime%60);
                    System.out.println(currentPlayingSongTag.getSongTitle()+" <=["+Durration+"]=> "+checkTime+" ---> "+(int)(checkTime/60)+":"+realTime);
                    
                    mediaController.play();
                    currentlyPlaying = true;
                }

                try
                {
                    Thread.sleep(45);
                }
                catch (InterruptedException ex)
                {
                    System.out.println("InterruptedException sleeping song player! "+ex);;
                }
            }
        }
    }
    public void pauseTheSong()
    {
        mediaController.pause();
    }
    public void playTheSong()
    {
        mediaController.play();
    }
    public void skipTheSong()
    {
        if(isPaused)
        {
            playTheSong();
        }
        else
        {
            mediaController.skip();
            currentPlayingSongTag = mediaController.getCurrentSong().getMetaTag();
        	System.out.println("Skipped song to -> "+currentPlayingSongTag.getSongTitle());
        	
        }
    }
    public String getTheSongName()
    {
        return theCurrentSongTitle;
    }
    public boolean getPausedState()
    {
        return mediaController.isPaused();
    }
    public void stopPlayer()
    {
    	this.stop = true;
    }
	@Override
	public String getMediaObserverName() 
	{
		return MEDIA_OBSERVER_NAME;
	}
	@Override
	public void updateMediaObserver(PlayState stateOfPlayer) 
	{
		this.playerCurrentState = stateOfPlayer;
		System.out.println("Player is currently: "+playerCurrentState);
		if(mediaController != null)
		{
			mediaController.changeState(stateOfPlayer);
		}
	}
	public SongTag getTheCurrentSong()
	{
		return this.currentPlayingSongTag;
	}
}
