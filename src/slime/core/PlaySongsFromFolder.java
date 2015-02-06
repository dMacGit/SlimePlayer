package slime.core;

import java.awt.Color;
import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.farng.mp3.TagException;

import slime.controller.MediaController;
import slime.media.PlayState;
import slime.media.Song;
import slime.media.SongList;
import slime.media.SongTag;
import slime.media.WrongFileTypeException;
import slime.observe.MediaObserver;
import slime.utills.FileIO;

public class PlaySongsFromFolder implements MediaObserver
{
    private HashMap<Integer,String> listOfMP3, songTags;
    private boolean currentlyPlaying = false, stop = false, isPaused;
    public PlaySongControls playSong;
    private Thread songThread;
    private ScrollingText label;
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

    public PlaySongsFromFolder(String dir)
    {
        FILE_DIR = dir+"/";
        HOLDINGS_FILE_PATH = FILE_DIR+HOLDINGS_FILE_NAME;
        SONG_PATHS_FILE_PATH = FILE_DIR+SONG_PATHS_FILE_NAME;
        
        mediaController = new MediaController();
        
        listOfMP3 = new HashMap<Integer,String>();
        songTags = new HashMap<Integer,String>();
        
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
    public HashMap<Integer,String> getMapOfSong()
    {
        return songTags;
    }
    
    public void populateLibrary()
    {
    	Object[] libraryDataArray = null, pathsDataArray = null;
    	try 
    	{
    		libraryDataArray = FileIO.ReadData(this.HOLDINGS_FILE_PATH);
    		pathsDataArray = FileIO.ReadData(this.SONG_PATHS_FILE_PATH);

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
        playSong.stopPlaying();
        songThread.stop();
    }
    public int getTheNum()
    {
        return listOfMP3.size();
    }
    public String getTheSong(int index)
    {
        return listOfMP3.get(index);
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
