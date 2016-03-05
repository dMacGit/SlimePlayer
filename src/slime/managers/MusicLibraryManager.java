package slime.managers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;

import org.farng.mp3.TagException;

import slime.controller.AnimationController;
import slime.controller.MediaController;
import slime.controller.ScrollingTextController;
import slime.media.PlayState;
import slime.media.Song;
import slime.media.SongList;
import slime.media.SongTag;
import slime.media.WrongFileTypeException;
import slime.observe.GuiObserver;
import slime.observe.GuiSubject;
import slime.observe.StateObserver;
import slime.observe.StateSubject;
import slime.utills.ActionTimer;
import slime.utills.FileIO;

public class MusicLibraryManager implements StateSubject, GuiObserver
{
	private GuiSubject parentSubject;
	
	private AnimationController animationController;
	private MediaController mediaController;
	
	private List<StateObserver> stateObserverList = new ArrayList<StateObserver>();
	//private List<AnimatorObserver> animatorObserverList = new ArrayList<AnimatorObserver>();
	
    private ArrayList<String> songTags;
    private ArrayList<Song> defaultPlaylist;
    private LinkedList<Integer> playListHistory;
    
    private boolean currentlyPlaying = false, STOP_MANAGER = false, isPaused;
    private boolean shuffle_Is_On = false, repeat_Is_On = false;
    private boolean PLAY_STATE_CHANGED = false, SYNC_CHANGED;
    
    /*
     * Observer call-backs will sync up all observers and modify these values
     * at each stage of the Manager State change
     */
    private boolean observersSyncReady = false;
    private boolean observersSyncInit = false;
    private boolean observersSyncPlay = false;
    private boolean observersSyncStop = false;
    private boolean observersSyncFin = false;
    private boolean observersSyncClose = false;
    
    //These are values changed by the guiObserver: User Pressed a button
    private boolean userPressedButton = false;
    
    //What user button was pressed
    private boolean userPressedPlay = false;
    private boolean userPressedPause = false;
    private boolean userPressedSkip_next = false;
    private boolean userPressedSkip_back = false;
    private boolean userPressedStop = false;
    private boolean userToggledShuffle = false;
    private boolean userToggledRepeat = false;
    private boolean userSkippedForwards = false;
    private boolean userSkippedBack = false;
    private boolean userPressedClose = false;
    
    private Thread songThread;
    private playlistManagerThread playListThread;
    
    private ScrollingTextController label;
    private JLabel scrollingTitleLabel;
    private int labelWidth;
    private final String HOLDINGS_FILE_NAME = "Lib_MP3player.txt", SONG_PATHS_FILE_NAME = "SongPaths.txt";
    private String HOLDINGS_FILE_PATH, SONG_PATHS_FILE_PATH, Durration, theCurrentSongTitle = null, FILE_DIR;
    private String MEDIA_OBSERVER_NAME = "PlaySongsFormFolder";
    private SongTag currentPlayingSongTag = null;
    private SongList listOfSongs;
    private PlayState currentPlayState;
    private Song currentSong;
    private String startDataDelimitor = "'['", midDataDelimitor = "','", endDataDelimitor = "']'";
    private String characterSeperator = "&&";

    public MusicLibraryManager(String dir)
    {
        FILE_DIR = dir+"/";
        System.out.println("The library folder dir!"+FILE_DIR);
        HOLDINGS_FILE_PATH = FILE_DIR+HOLDINGS_FILE_NAME;
        SONG_PATHS_FILE_PATH = FILE_DIR+SONG_PATHS_FILE_NAME;
        
        mediaController = new MediaController();
        animationController = new AnimationController();
        
        songTags = new ArrayList<String>();
        listOfSongs = new SongList();
        /*
        scrollingTitleLabel = new JLabel("");
        scrollingTitleLabel.setPreferredSize(new Dimension(225,25));
        scrollingTitleLabel.setMinimumSize(new Dimension(225,25));
        scrollingTitleLabel.setMaximumSize(new Dimension(225,25));
        scrollingTitleLabel.setForeground(Color.WHITE);*/
        populateLibrary();
        
        //Create the default playlist, in order, no shuffle.
        defaultPlaylist = (ArrayList<Song>) listOfSongs.getListOfSongs();
        //Initialize the playlist history stack
        playListHistory = new LinkedList<Integer>();
        System.out.println("The list of songs is this large: "+listOfSongs.getSize()+" Songs!");
        playListThread = new playlistManagerThread(defaultPlaylist,playListHistory);
        playListThread.start();
        
        this.registerStateObserver(animationController);
        animationController.setParentSubject(this);
        this.registerStateObserver(mediaController);
        mediaController.setParentSubject(this);
        
        System.out.println("Player has been Started!");
        
    }
    
    public MusicLibraryManager(SongList playList)
    {
        HOLDINGS_FILE_PATH = FILE_DIR+HOLDINGS_FILE_NAME;
        SONG_PATHS_FILE_PATH = FILE_DIR+SONG_PATHS_FILE_NAME;
        mediaController = new MediaController();
        animationController = new AnimationController();
        songTags = new ArrayList<String>();
        listOfSongs = playList;
        /*scrollingTitleLabel = new JLabel("");
        scrollingTitleLabel.setPreferredSize(new Dimension(225,25));
        scrollingTitleLabel.setMinimumSize(new Dimension(225,25));
        scrollingTitleLabel.setMaximumSize(new Dimension(225,25));
        scrollingTitleLabel.setForeground(Color.WHITE);*/
        populateLibrary();
        
        
        //Create the default playlist, in order, no shuffle.
        defaultPlaylist = (ArrayList<Song>) listOfSongs.getListOfSongs();
        System.out.println("The list of songs is this large: "+listOfSongs.getSize()+" Songs!");
        //Initialize the playlist history stack
        playListHistory = new LinkedList<Integer>();
        
        playListThread = new playlistManagerThread(defaultPlaylist,playListHistory);
        playListThread.start();
        
        this.registerStateObserver(animationController);
        animationController.setParentSubject(this);
        this.registerStateObserver(mediaController);
        mediaController.setParentSubject(this);
        
        System.out.println("Player has been Started!");
        
    }
    public ArrayList<String> getMapOfSong()
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
    		System.out.println(HOLDINGS_FILE_PATH);
    		System.out.println(ActionTimer.formatLastTimedAction("Read Files",ActionTimer.measurePreviouseActionTime(startReadingFiles, System.currentTimeMillis())));
    		
    		//To reduce initialization time set map size.
    		
    		long totalTimeForListCreation = 0;
			for(int index = 0; index < libraryDataArray.length; index++)
	    	{
				songTags.add(libraryDataArray[index].toString());
				
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
    
    public class playlistManagerThread extends Thread
    {
    	public ArrayList<Song> defaultPlaylist;
    	public LinkedList<Integer> playListHistory;
    	
    	//public Song currentSong;
    	public int currentIndex = -1, nextSongIndex = 0;
    	
    	
        public playlistManagerThread(ArrayList<Song> defaultPlaylist, LinkedList<Integer> playListHistory)
        {
        	this.defaultPlaylist = defaultPlaylist;
        	this.playListHistory = playListHistory;
        	currentPlayState = PlayState.INITIALIZED;
        	currentSong = null;
        	System.out.println("The Playlist Manager Thread is created!");
        }

        @Override
        public void run()
        {
            while(!STOP_MANAGER)
            {
            	
            	if(currentPlayState == PlayState.INITIALIZED)
                { 
        			System.out.println("The Manager thread is initialized!");
        			PLAY_STATE_CHANGED = !PLAY_STATE_CHANGED;
        			//if(observersSyncInit)
        			//
        				currentPlayState = PlayState.IDLE;
        				notifyAllStateObservers(null,currentPlayState);
        				
        			//}
                }
            	
            	if( userToggledShuffle )
                {
        			shuffle_Is_On = !shuffle_Is_On;
					PLAY_STATE_CHANGED = !PLAY_STATE_CHANGED;
					resetUserButtons();
                }
            	
            	if( userToggledRepeat )
                {
        			repeat_Is_On = !repeat_Is_On;
					PLAY_STATE_CHANGED = !PLAY_STATE_CHANGED;
					resetUserButtons();
                }
            	
            	if(PLAY_STATE_CHANGED || SYNC_CHANGED)
            	{
            		/*	The initial state of the manager and the Animator and MediaController
            		 * 	It is assumed that the Animator and MediaController classes are Initiated
            		 * 	on creation.
            		 */
            		
            		//The Idle state of the Manager. Not worrying about the observer idle states
            		if(currentPlayState == PlayState.IDLE && userPressedPlay)
                    {
            			System.out.println("[Manager] Changing from IDLE to READY");
            			if(shuffle_Is_On)
                    	{
            				System.out.println("[Manager] SHUFFLE Active, Choosing random song");
                			currentIndex = (int)(Math.random()*defaultPlaylist.size());
                    		currentSong = defaultPlaylist.get(currentIndex);
                    		System.out.println("[Manager] Randomly selected Index: "+currentIndex+" which holds: "+currentSong.getMetaTag().getRecordingTitle());
                    	}
                    	else
                    	{
                    		System.out.println("[Manager] SHUFFLE Inactive, Choosing next song");
                    		currentSong = defaultPlaylist.get(++currentIndex);
                    		
                    	}
            			currentPlayState = PlayState.READY;
                		notifyAllStateObservers(currentSong,currentPlayState);
            			PLAY_STATE_CHANGED = false;
            			resetUserButtons();
                    }
            		else if(currentPlayState == PlayState.READY)
                    {
            			System.out.println("[Manager] READY for Observer SYNC-READY");
            			if(observersSyncReady)
                		{
            				System.out.println("[Manager] Observer SYNC-READY Manager Changing to PLAYING");
                			currentPlayState = PlayState.PLAYING;
                			notifyAllStateObservers(null,currentPlayState);
                			observersSyncReady = false;
                			PLAY_STATE_CHANGED = false;
                			SYNC_CHANGED = false;
                		}
            			
                    }
            		else if(currentPlayState == PlayState.FINISHED)
                    {
                		//First Check for end of playlist
                		if( playListHistory.size()!=defaultPlaylist.size())
                		{
                			if(shuffle_Is_On)
                        	{
                    			//Add last played song to history
                    			playListHistory.addLast(currentIndex);
                    			
                    			//currentIndex = nextSongIndex;
                    			//Randomly choose next index, and check for already played
                    			
                    			boolean randomized = false;
                    			
                    			while(!randomized)
                    			{
                    				int tempIndex = (int)(Math.random()*defaultPlaylist.size());
                        			System.out.println("Randomly Choosing number: "+tempIndex);
                        			
                    				if(!playListHistory.contains(tempIndex))
	                    			{
	                    				currentIndex = tempIndex;
	                    				randomized = true;
	                    			}
                    			}
                    			
                        		currentSong = defaultPlaylist.get(currentIndex);
                        		currentPlayState = PlayState.READY;
                        		PLAY_STATE_CHANGED = false;
                        		observersSyncFin = false;
                        		
                        		notifyAllStateObservers(currentSong,currentPlayState);              		
                        	}
                        	else
                        	{
                        		//Add last played song to history
                        		playListHistory.addLast(currentIndex);
                        		
                        		currentIndex++;
                        		currentSong = defaultPlaylist.get(currentIndex);
                        		currentPlayState = PlayState.READY;
                        		PLAY_STATE_CHANGED = false;
                        		notifyAllStateObservers(currentSong,currentPlayState);
                        	}
                        	
                        	//Need to change so mediaController has no playlist, and Manager has Playlist
                        	/*if(!mediaController.hasPlaylist())
                        	{
                        		mediaController.setSongList(listOfSongs);
                        	}
                        	                    
                            currentSong = mediaController.getCurrentSong();
        					currentPlayingSongTag = currentSong.getMetaTag();*/
                			currentPlayingSongTag = currentSong.getMetaTag();
        					System.out.println("Current Song / Next Song to play: "+currentPlayingSongTag.getSongTitle());

                            int checkTime = (currentPlayingSongTag.getDurration())/2;
                            int realTime = (checkTime%60);
                            System.out.println(currentPlayingSongTag.getSongTitle()+" <=["+Durration+"]=> "+checkTime+" ---> "+(int)(checkTime/60)+":"+realTime);
                            
                            //mediaController.play();
                            //currentlyPlaying = true;
                        }
                		else
                		{
                			//Reached end of Playlist
                		}
                		//PLAY_STATE_CHANGED = !PLAY_STATE_CHANGED;
                    }
            		else if(currentPlayState == PlayState.PLAYING && observersSyncPlay)
                    {
            			observersSyncPlay = false;
						PLAY_STATE_CHANGED = !PLAY_STATE_CHANGED;
						
                    }
            		else if(currentPlayState == PlayState.PLAYING && userPressedPause)
                    {
						currentPlayState = PlayState.PAUSED;
						notifyAllStateObservers(null, currentPlayState);
						PLAY_STATE_CHANGED = !PLAY_STATE_CHANGED;
						resetUserButtons();
						
                    }
            		else if(currentPlayState == PlayState.PAUSED && userPressedPlay)
                    {
						currentPlayState = PlayState.PLAYING;
						notifyAllStateObservers(null, currentPlayState);
						PLAY_STATE_CHANGED = !PLAY_STATE_CHANGED;
						resetUserButtons();
                    }
            		else if( (currentPlayState == PlayState.PAUSED || currentPlayState == PlayState.PLAYING) && userSkippedForwards)
                    {
            			currentPlayState = PlayState.READY;
            			PLAY_STATE_CHANGED = !PLAY_STATE_CHANGED;
            			notifyAllStateObservers(null, PlayState.SKIPPED_FORWARDS);
						
						resetUserButtons();
                    }
            		else if(userPressedStop)
            		{
            			currentPlayState = PlayState.STOPPED;
            			SYNC_CHANGED = false;
            			System.out.println("[ Manager ] Is Now "+currentPlayState.toString());
            			PLAY_STATE_CHANGED = false;
            			notifyAllStateObservers(null, currentPlayState);
            			resetUserButtons();
            		}
            		else if(userPressedClose)
            		{
            			currentPlayState = PlayState.SHUTDOWN;
            			SYNC_CHANGED = false;
            			System.out.println("[ Manager ] Is Now in "+currentPlayState.toString());
            			PLAY_STATE_CHANGED = false;
            			notifyAllStateObservers(null, currentPlayState);
            			resetUserButtons();
            		}
            		else if(currentPlayState == PlayState.SHUTDOWN)
            		{
            			if(observersSyncClose)
            			{
            				//Fully shut down the program!
            				System.out.println("[ Manager ] Observers Have Stopped! Deregistering...");
            				deregisterStateObserver(animationController);
            				deregisterStateObserver(mediaController);
            				System.out.println("[ Manager ] Observers Deregistered");
            				animationController = null;
            				mediaController = null;
            				System.out.println("[ Manager ] Closing Manager Thread!");
            				STOP_MANAGER = true;
            				parentSubject.guiCallback(currentPlayState, null);
            			}
            		}
            		else if(currentPlayState == PlayState.STOPPED)
            		{
            			if(observersSyncStop)
            			{
            				//Fully shut down the program!
            				System.out.println("[ Manager ] Observers Have Stopped! Deregistering...");
            				deregisterStateObserver(animationController);
            				deregisterStateObserver(mediaController);
            				System.out.println("[ Manager ] Observers Deregistered");
            				animationController = null;
            				mediaController = null;
            				System.out.println("[ Manager ] Closing Manager Thread!");
            				STOP_MANAGER = true;
            				parentSubject.guiCallback(currentPlayState, null);
            			}
            		}
            	}
            	else
            	{
            		try
                    {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException ex)
                    {
                        System.out.println("InterruptedException sleeping song player! "+ex);;
                    }
            	}
                
                
                
            }
        }
    }
    
    private void resetUserButtons()
    {
    	userPressedPlay = false;
        userPressedPause = false;
        userPressedSkip_next = false;
        userPressedSkip_back = false;
        userPressedStop = false;
        userToggledShuffle = false;
        userToggledRepeat = false;
        userSkippedForwards = false;
        userSkippedBack = false;
        userPressedClose = false;
    }
    /*public void pauseTheSong()
    {
        mediaController.pause();
    }
    public void playTheSong()
    {
        mediaController.play();
        this.getPartentSubject().notifyAllMediaObservers();
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
    }*/

    private void checkObserverSync(String observerName, PlayState state)
    {
    	System.out.println("Checking observer sync for state: "+state.toString());
    	if(state == PlayState.READY)
    	{
    		int totalObserverReady = 0;
    		for(StateObserver observer :stateObserverList)
    		{
    			if(observer.getCurrentPlayState() == PlayState.READY){
    				totalObserverReady++;
    			}
    		}
    		System.out.println("Sync: "+totalObserverReady+" of "+stateObserverList.size());
    		
    		if(totalObserverReady == stateObserverList.size())
    		{
    			observersSyncReady = true;
    			PLAY_STATE_CHANGED = true;
    			SYNC_CHANGED = true;
    			System.out.println(observerName+" Synced! ");
    		}
    	}
    	else if(state == PlayState.PLAYING)
    	{
    		int totalObserversPlay = 0;
    		for(StateObserver observer :stateObserverList)
    		{
    			if(observer.getCurrentPlayState() == PlayState.FINISHED){
    				totalObserversPlay++;
    			}
    		}
    		if(totalObserversPlay == stateObserverList.size())
    		{
    			observersSyncPlay = true;
    			PLAY_STATE_CHANGED = true;
    			SYNC_CHANGED = true;
    		}
    	}
    	else if(state == PlayState.INITIALIZED)
    	{
    		int totalObserversInit = 0;
    		for(StateObserver observer :stateObserverList)
    		{
    			if(observer.getCurrentPlayState() == PlayState.FINISHED){
    				totalObserversInit++;
    			}
    		}
    		if(totalObserversInit == stateObserverList.size())
    		{
    			observersSyncInit = true;
    			PLAY_STATE_CHANGED = true;
    			SYNC_CHANGED = true;
    		}
    	}
    	else if(state == PlayState.STOPPED)
    	{
    		int totalObserversStop = 0;
    		for(StateObserver observer :stateObserverList)
    		{
    			if(observer.getCurrentPlayState() == PlayState.STOPPED){
    				totalObserversStop++;
    			}
    		}
    		System.out.println("Sync: "+totalObserversStop+" of "+stateObserverList.size());
    		if(totalObserversStop == stateObserverList.size())
    		{
    			observersSyncStop = true;
    			PLAY_STATE_CHANGED = true;
    			SYNC_CHANGED = true;
    			System.out.println("--> Should now have sync stop");
    		}
    	}
    	else if(state == PlayState.FINISHED)
    	{
    		int totalObserversFin = 0;
    		for(StateObserver observer :stateObserverList)
    		{
    			if(observer.getCurrentPlayState() == PlayState.FINISHED){
    				totalObserversFin++;
    			}
    		}
    		System.out.println("Sync: "+totalObserversFin+" of "+stateObserverList.size());
    		if(totalObserversFin == stateObserverList.size())
    		{
    			observersSyncFin = true;
    			PLAY_STATE_CHANGED = true;
    			SYNC_CHANGED = true;
    		}
    	}
    	else if(state == PlayState.SHUTDOWN)
    	{
    		int totalObserversClosed = 0;
    		for(StateObserver observer :stateObserverList)
    		{
    			if(observer.getCurrentPlayState() == PlayState.SHUTDOWN){
    				totalObserversClosed++;
    			}
    		}
    		if(totalObserversClosed == stateObserverList.size())
    		{
    			observersSyncClose = true;
    			PLAY_STATE_CHANGED = true;
    			SYNC_CHANGED = true;
    		}
    	}
    	
    	
    }
	/*@Override
	public void updateMediaObserver(MediaSubject mediaSubject, PlayState stateOfPlayer) 
	{
		if(stateOfPlayer == PlayState.INITIATION)
		{
			//Assigns parentSubject at Initialization
			parentSubject = mediaSubject;
		}
		this.playerCurrentState = stateOfPlayer;
		System.out.println("Player is currently: "+playerCurrentState);
		if(mediaController != null)
		{
			mediaController.changeState(stateOfPlayer);
		}
		//
	}*/
	public SongTag getTheCurrentSongTag()
	{
		return this.currentSong.getMetaTag();
	}

	/*@Override
	public MediaSubject getPartentSubject() 
	{
		return parentSubject;
	}*/

	@Override
	public void stateSubjectCallback(String observerName, PlayState state) 
	{
		
		//Called by the MediaController when the song file has reached the end of play.
		if(state == PlayState.FINISHED)
		{
			
			currentPlayState = PlayState.FINISHED;
			PLAY_STATE_CHANGED = true;
		}
		else if(state == PlayState.READY)
		{
			checkObserverSync(observerName,PlayState.READY);
		}
		else if(state == PlayState.STOPPED)
		{
			checkObserverSync(observerName,PlayState.STOPPED);
		}
		else if(state == PlayState.SHUTDOWN)
		{
			checkObserverSync(observerName,PlayState.SHUTDOWN);
		}
	}

	@Override
	public void registerStateObserver(StateObserver observer) 
	{
		stateObserverList.add(observer);
		System.out.println("<<<< "+observer.getStateObserverName()+" Added! >>>>");
		
	}

	@Override
	public void deregisterStateObserver(StateObserver observer) 
	{
		stateObserverList.remove(observer);
		System.out.println("<<<< "+observer.getStateObserverName()+" Removed! >>>>");
		
	}

	@Override
	public void notifyAllStateObservers(Song currentSong, PlayState state) 
	{
		for(StateObserver observer : stateObserverList)
		{
			observer.updateStateObserver(currentSong, state);
		}
		
	}

	@Override
	public String getGuiObserverName() 
	{
		return this.getClass().getName();
	}

	@Override
	public void setParentSubject(GuiSubject subject) 
	{
		this.parentSubject = subject;
	}

	@Override
	public void updateGuiObserver(PlayState newState) 
	{		
		System.out.println(this.getGuiObserverName()+" [ Gui State Changed! "+newState.toString()+" ]");
		PLAY_STATE_CHANGED = true;
		if(newState == PlayState.PLAYING)
		{
			userPressedPlay = true;
		}
		else if(newState == PlayState.PAUSED)
		{
			userPressedPause = true;
		}
		else if(newState == PlayState.STOPPED)
		{
			userPressedStop = true;
		}
		else if(newState == PlayState.SHUTDOWN)
		{
			userPressedClose = true;
		}
		else if(newState == PlayState.SHUFFLE_TOGGLED)
		{
			userToggledShuffle = true;
		}
		else if(newState == PlayState.REPEAT_TOGGLED)
		{
			userToggledRepeat = true;
		}
		else if(newState == PlayState.SKIPPED_FORWARDS)
		{
			userSkippedForwards = true;
		}
		else if(newState == PlayState.SKIPPED_BACK)
		{
			userSkippedBack = true;
		}
	}

	/*@Override
	public void registerAnimatorObserver(AnimatorObserver observer) 
	{
		animatorObserverList.add(observer);
		System.out.println("<<<< "+observer.getAnimatorObserverName()+" Added! >>>>");
		
	}

	@Override
	public void deregisterAnimatorObserver(AnimatorObserver observer) 
	{
		animatorObserverList.remove(observer);
		System.out.println("<<<< "+observer.getAnimatorObserverName()+" Removed! >>>>");
		
	}

	@Override
	public void notifyAllAnimatorObservers() 
	{
		for(AnimatorObserver observer : animatorObserverList){
			observer.updateAnimatorObserver(currentPlayState, getTheCurrentSongTag());
		}
		
	}*/
}
