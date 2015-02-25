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
    private LinkedList<String> listOfMP3, songTags;
    private boolean currentlyPlaying = false, stop = false, isPaused;
    //public PlaySongControls playSong;
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

    public MusicLibraryManager(String dir)
    {
        FILE_DIR = dir+"/";
        HOLDINGS_FILE_PATH = FILE_DIR+HOLDINGS_FILE_NAME;
        SONG_PATHS_FILE_PATH = FILE_DIR+SONG_PATHS_FILE_NAME;
        
        
        
        mediaController = new MediaController();
        
        
        listOfMP3 = new LinkedList<String>();
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
        
        listOfMP3 = new LinkedList<String>();
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
    	Object[] libraryDataArray = null, pathsDataArray = null;
    	try 
    	{
    		long startReadingFiles = ActionTimer.triggerTimedActionStart();
    		libraryDataArray = FileIO.ReadData(this.HOLDINGS_FILE_PATH);
    		pathsDataArray = FileIO.ReadData(this.SONG_PATHS_FILE_PATH);
    		System.out.println(ActionTimer.formatLastTimedAction("Read Files",ActionTimer.measurePreviouseActionTime(startReadingFiles, System.currentTimeMillis())));
    		
    		//To reduce initialization time set map size.
    		
    		long totalTimeForListCreation = 0;
    		if(libraryDataArray.length == pathsDataArray.length)
    		{
    			System.out.println("----- Lengths are equal -----");
    			//startTime = ActionTimer.triggerTimedActionStart();
    			listOfSongs.setCappacity(libraryDataArray.length);
    			for(int index = 0; index < pathsDataArray.length; index++)
    	    	{
    				songTags.addLast(libraryDataArray[index].toString());
    	    		//listOfMP3.addLast(pathsDataArray[index].toString());
    				
    				//File structure: [ data | data | data | data ],[ data ]
    				String[] mainDataArray = libraryDataArray[index].toString().split("','");
    			
    				
    				final String wholeTagString = mainDataArray[0];
    				
    				String tempSubstring = wholeTagString.substring(wholeTagString.indexOf("'['")+1,wholeTagString.indexOf("']'"));
    				String[] tagDataArray = tempSubstring.split("'|'");
    				    				
    	    		String someRandomData = (pathsDataArray[index].toString()).substring((pathsDataArray[index].toString()).indexOf(" ")+1, (pathsDataArray[index].toString()).length());
        			try 
        			{
        				long timeTaken = 0;
        				addTimeStart = ActionTimer.triggerTimedActionStart();
    					listOfSongs.addSong(new Song(someRandomData));
    					timeTaken = ActionTimer.measurePreviouseActionTime(addTimeStart, System.currentTimeMillis());
    					totalTimeForListCreation += timeTaken;
    				} 
        			catch (WrongFileTypeException | TagException e)
        			{
    					System.out.println("Error trying to build the Tag! ~> "+someRandomData);
    				}
    	    	}
    			//System.out.println(ActionTimer.formatLastTimedAction("Populating Media Library",ActionTimer.measurePreviouseActionTime(startTime, System.currentTimeMillis())));
    		}
    		System.out.println(ActionTimer.formatLastTimedAction("Total time to add ",totalTimeForListCreation));
    		/*else
    		{
	    		for(int index = 0; index < libraryDataArray.length; index++)
		    	{
		    		songTags.put(index,libraryDataArray[index].toString());
		    	}
		    	for(int index = 0; index < pathsDataArray.length; index++)
		    	{
		    		listOfMP3.put(index,pathsDataArray[index].toString());
		    		String someRandomData = (pathsDataArray[index].toString()).substring((pathsDataArray[index].toString()).indexOf(" ")+1, (pathsDataArray[index].toString()).length());
	    			try 
	    			{
						listOfSongs.addSong(new Song(someRandomData));
					} 
	    			catch (WrongFileTypeException | TagException e)
	    			{
						System.out.println("Error trying to build the Tag! ~> "+someRandomData);
					}
		    	}			
    		}*/
    		
		}
    	catch (FileNotFoundException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	catch (IOException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    private void arrayToString(Object[] arrayOfData){
    	
    	System.out.println("Starting To test the array! ");
		
		for(int stringIndex = 0; stringIndex < arrayOfData.length; stringIndex++)
		{
			//print out the array of data for debug purposes!
			System.out.print(arrayOfData[stringIndex].toString()+",");
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
                    //label = new ScrollingText( currentPlayingSongTag ,labelWidth);
                    
                    mediaController.play();
                    currentlyPlaying = true;
                }

                /*if(label.getImage() != null)
                {
                    scrollingTitleLabel.setIcon(new ImageIcon(label.getImage()));
                }*/
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
        //label.pauseAnimation();
        mediaController.pause();
        //pauseTime();
    }
    public void playTheSong()
    {
        //label.startAnimation();
        //unpauseTime();
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
        	/*label.resetAnimation();
        	label.resetTimer();
        	label.changeTag(this.currentPlayingSongTag);
        	label.startAnimation();*/
        	
        	
        }
    }
    /*
    public void setJLabelBounds(int xPosition, int width)
    {
        labelWidth = width;
    }
    public JLabel getLabel()
    {
        return scrollingTitleLabel;
    }*/
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
        //playSong.stopPlaying();
        songThread.stop();
    }
    /*public int getTheNum()
    {
        return listOfMP3.size();
    }
    public String getTheSong(int index)
    {
        return listOfMP3.get(index);
    }*/

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
