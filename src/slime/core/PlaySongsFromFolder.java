package slime.core;

import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import slime.utills.ShuffleArray;

public class PlaySongsFromFolder implements MediaObserver
{
    private HashMap<Integer,String> listOfMP3, songTags;
    private SongList librarySongList;
    private boolean currentlyPlaying = false, stop = false, startPlayer = false, isPaused;
    public PlaySongControls playSong;
    private Thread songThread;
    private ScrollingText label;
    private JLabel scrollingTitleLabel;
    private int labelXpos, labelWidth;
    private long HOLDINGS_FILE_LAST_MODIFIED;
    private final String HOLDINGS_FILE_NAME = "Lib_MP3player.txt", SONG_PATHS_FILE_NAME = "SongPaths.txt";
    private String HOLDINGS_FILE_PATH, SONG_PATHS_FILE_PATH, holdingInfoForCurrentSong,
            ID, Title, Artist, Album, Durration, Year, songTime, theCurrentSongTitle = null,
            FOLDER, FILE_DIR;
    private TimerTask secondsUpdating;
    private Timer timerObject,seconds;
    private updateHolingsInfo updater;
    private byte songMinutes, songSeconds, pausedSeconds, pausedMinutes;
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
        timerObject = new Timer();
        seconds = new Timer();
        mediaController = new MediaController();
        
        listOfMP3 = new HashMap<Integer,String>();
        songTags = new HashMap<Integer,String>();
        
        listOfSongs = new SongList();
        
        scrollingTitleLabel = new JLabel("");
        scrollingTitleLabel.setPreferredSize(new Dimension(225,25));
        scrollingTitleLabel.setMinimumSize(new Dimension(225,25));
        scrollingTitleLabel.setMaximumSize(new Dimension(225,25));
        scrollingTitleLabel.setForeground(Color.WHITE);
        readHoldingsFile();
        //populateLibrary();
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
    	HOLDINGS_FILE_LAST_MODIFIED = new File(HOLDINGS_FILE_PATH).lastModified();
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
	    		//System.out.println(someRandomData);
	    		
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

    /*
     * The "readHoldingsFile" method obtains all the song information from the holdings file
     * in order for the player to play the songs in a playlist.
     *
     */

    public void readHoldingsFile()
    {
        BufferedReader holdingsBufferedReader = null;
        BufferedReader songBufferedReader = null;
        HOLDINGS_FILE_LAST_MODIFIED = new File(HOLDINGS_FILE_PATH).lastModified();
        
        try
        {
            System.out.println(HOLDINGS_FILE_PATH.toString());
            holdingsBufferedReader = new BufferedReader(new FileReader(HOLDINGS_FILE_PATH));
            songBufferedReader = new BufferedReader(new FileReader(SONG_PATHS_FILE_PATH));
        }
        catch (FileNotFoundException ex)
        {
        	System.out.println("::: Error opening holdings file! :::");           
        }
        
        try
        {
            int count = 1;
            while (songBufferedReader.ready() && holdingsBufferedReader.ready())
            {
                String tagInfo = holdingsBufferedReader.readLine();
                String line = songBufferedReader.readLine();
                line = line.substring(line.indexOf(' ')+1);
                int value = line.lastIndexOf('\\');
                String songPath = line.substring(line.indexOf(' ')+1);
                int check = line.indexOf('\\');
                int begining = 0;
                String validated = "";
                String updatedString = line;
                while(check < value)
                {
                    String subString = line.substring(begining, check);
                    updatedString = updatedString.substring(subString.length()+1);
                    validated += subString+'/';
                    begining = (check)+1;
                    songPath = updatedString;
                    check = updatedString.indexOf('\\')+validated.length();
                }
                String first = songPath.substring(0, songPath.indexOf('\\'));
                String last = updatedString.substring(songPath.indexOf('\\')+1);
                validated += first+"/"+last;
                listOfMP3.put(count,validated);
                songTags.put(count,tagInfo);
                
                try
                {
					listOfSongs.addSong(new Song(validated));
				}
                catch (WrongFileTypeException | TagException e)
                {
                	System.out.println("Error trying to build the Tag! ~> "+tagInfo);
				}
                
                count++;
            }
            holdingsBufferedReader.close();
            songBufferedReader.close();
        }
        catch (IOException ex)
        {
            Logger.getLogger(mainPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
     * The "updateHolingsInfo" method is polled using a timertask in order to keep an updated list of all
     * songs in the playlist. The timertask is set to check the file durring the current song, about half
     * way through playing it. The time until the method is called is determined by half the length of the
     * current song being played. The timertask is reset with that time after the current song is finished
     * and before the next song is played.
     */

    public class updateHolingsInfo extends TimerTask
    {
        public void run()
        {
            System.out.println(":::: Updating the holdings file @ "+songMinutes+":"+songSeconds+" ::::");
            BufferedReader holdingsBufferedReader = null;
            BufferedReader songBufferedReader = null;
            HOLDINGS_FILE_LAST_MODIFIED = new File(HOLDINGS_FILE_PATH).lastModified();
            try
            {
                holdingsBufferedReader = new BufferedReader(new FileReader(HOLDINGS_FILE_PATH));
                songBufferedReader = new BufferedReader(new FileReader(SONG_PATHS_FILE_PATH));
            }
            catch (FileNotFoundException ex)
            {
                Logger.getLogger(mainPlayer.class.getName()).log(Level.SEVERE, null, ex);
            }
            try
            {
                int count = 1;
                while (songBufferedReader.ready() && holdingsBufferedReader.ready())
                {
                    String tagInfo = holdingsBufferedReader.readLine();
                    String line = songBufferedReader.readLine();
                    line = line.substring(line.indexOf(' ')+1);
                    int value = line.lastIndexOf('\\');
                    String songPath = line.substring(line.indexOf(' ')+1);
                    int check = line.indexOf('\\');
                    int begining = 0;
                    String validated = "";
                    String updatedString = line;
                    while(check < value)
                    {
                        String subString = line.substring(begining, check);
                        updatedString = updatedString.substring(subString.length()+1);
                        validated += subString+'/';
                        begining = (check)+1;
                        songPath = updatedString;
                        check = updatedString.indexOf('\\')+validated.length();
                    }
                    String first = songPath.substring(0, songPath.indexOf('\\'));
                    String last = updatedString.substring(songPath.indexOf('\\')+1);
                    validated += first+"/"+last;
                    listOfMP3.put(count,validated);
                    songTags.put(count,tagInfo);
                    count++;
                }
                holdingsBufferedReader.close();
                songBufferedReader.close();
                updater.cancel();
            }
            catch (IOException ex)
            {
                Logger.getLogger(mainPlayer.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println(":::: Successfully Updated the holdings file @ "+songMinutes+":"+songSeconds+" ::::");
        }
    }
    public void getUpdatedSongHoldingFileInfo(int id)
    {
        BufferedReader holdingsBufferedReader = null;
        boolean notFound = true;
        try
        {
            holdingsBufferedReader = new BufferedReader(new FileReader(HOLDINGS_FILE_PATH));
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(mainPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        try
        {
            int count = 1;
            while (holdingsBufferedReader.ready() && notFound)
            {
                String tagInfo = holdingsBufferedReader.readLine();
                if(count == id)
                {
                    songTags.put(count,tagInfo);
                    System.out.println("Updated song info for: "+tagInfo);
                    notFound = false;
                }
                count++;
            }
            holdingsBufferedReader.close();
        }
        catch (IOException ex)
        {
            Logger.getLogger(mainPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void setLastChecked(long lastModified)
    {
        HOLDINGS_FILE_LAST_MODIFIED = lastModified;
    }
    public void setHoldingInfoForCurrentSong(String holdingInfo)
    {
        holdingInfoForCurrentSong = holdingInfo;
        setCurrentSongInfo();
    }
    public void setCurrentSongInfo()
    {
        ID = holdingInfoForCurrentSong.substring(0, holdingInfoForCurrentSong.indexOf('\t'));
        String first = holdingInfoForCurrentSong.substring(holdingInfoForCurrentSong.indexOf('\t')+1);
        Title = first.substring(0, first.indexOf('\t'));
        String second = first.substring(first.indexOf('\t')+1);
        Artist = second.substring(0, second.indexOf('\t'));
        String third = second.substring(second.indexOf('\t')+1);
        Album = third.substring(0, third.indexOf('\t'));
        String fourth = third.substring(third.indexOf('\t')+1);
        Durration = fourth.substring(0,fourth.indexOf('\t'));
        String fifth = fourth.substring(fourth.indexOf('\t')+1);
        Year = fifth.substring(0,fifth.indexOf('\t'));
    }

    public class playRandomSong extends Thread
    {
        private int[] shuffledList = new int[getTheNum()];
        private int[] orderedList = new int[getTheNum()];

        public playRandomSong()
        {
        }

        @Override
        public void run()
        {
            int currentPlayNum = 0;
            while(!stop)
            {
                if(!currentlyPlaying)
                {
                	if(!mediaController.hasPlaylist())
                	{
                		mediaController.setSongList(listOfSongs);
                	}
                	
                    /*System.out.println("Choosing Next song!!");
                    
                    System.out.println("Number chosen: "+shuffledList[currentPlayNum]+" out of "+(getTheNum()));
                    System.out.println("ID was for: "+songTags.get(shuffledList[currentPlayNum]));*/
                    
                    currentSong = mediaController.getCurrentSong();
					currentPlayingSongTag = currentSong.getMetaTag();
					
					System.out.println("Current Song / Next Song to play: "+currentPlayingSongTag.getSongTitle());
					//currentPlayingSongTag = new SongTag(songFile);
                    /*if(new File(HOLDINGS_FILE).lastModified() != HOLDINGS_FILE_LAST_MODIFIED)
                    {
                        System.out.println("Holdings file was modified durring play: ["+HOLDINGS_FILE_LAST_MODIFIED+"] <!=> ["+new File(HOLDINGS_FILE).lastModified()+"]");
                        setLastChecked(new File(HOLDINGS_FILE).lastModified());
                        getUpdatedSongHoldingFileInfo(x);
                    }*/
                    //setHoldingInfoForCurrentSong(songTags.get(shuffledList[currentPlayNum]));
                    //updater = new updateHolingsInfo();
                    //timerObject.schedule(updater, (Integer.parseInt(Durration)/2)*1000);
                    //theCurrentSongTitle = getCurrentSongInfo();
                    int checkTime = (currentPlayingSongTag.getDurration())/2;
                    int realTime = (checkTime%60);
                    System.out.println(currentPlayingSongTag.getSongTitle()+" <=["+Durration+"]=> "+checkTime+" ---> "+(int)(checkTime/60)+":"+realTime);
                    label = new ScrollingText( currentPlayingSongTag ,labelWidth);
                    
                    //songThread = new Thread(playSong);
                    //secondsUpdating = new secondsUpdating();
                    //seconds = new Timer();
                    //seconds.scheduleAtFixedRate(secondsUpdating, 100, 1000);
                    //songThread.start();
                    mediaController.play();
                    currentlyPlaying = true;
                    currentPlayNum++;
                }

                if(label.getImage() != null)
                {
                    scrollingTitleLabel.setIcon(new ImageIcon(label.getImage()));
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
        label.pauseAnimation();
        mediaController.pause();
        //pauseTime();
    }
    public void playTheSong()
    {
        label.startAnimation();
        //unpauseTime();
        mediaController.play();
    }
    public void skipTheSong()
    {
        if(isPaused)
        {
            playTheSong();
        }
        else{
        	//resetTime();
            mediaController.skip();
            currentPlayingSongTag = mediaController.getCurrentSong().getMetaTag();
        	System.out.println("Skipped song to -> "+currentPlayingSongTag.getSongTitle());
        	label.resetAnimation();
        	label.resetTimer();
        	label.changeTag(this.currentPlayingSongTag);
        	label.startAnimation();
        	
        	
        }
        
        //seconds.cancel();
        //updater.cancel();
    }
    public void setJLabelBounds(int xPosition, int width)
    {
        labelXpos = xPosition;
        labelWidth = width;
    }
    public JLabel getLabel()
    {
        return scrollingTitleLabel;
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
        playSong.stopPlaying();
        //resetTime();
        seconds.cancel();
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
