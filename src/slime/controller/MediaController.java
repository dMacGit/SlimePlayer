package slime.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import slime.media.PlayState;
import slime.media.Song;
import slime.media.SongList;
import slime.media.SongTag;
import slime.observe.StateObserver;
import slime.observe.StateSubject;

/**
 * <p><b>
 * This MediaController Class handles interfacing the user input
 * with the playing of the Song file. It currently only supports playing .mp3 files.</b>
 * <p>
 * It implements the {@link StateObserver } interface so that it can be notified
 * of player control updates by the user. This observer has (as its Subject), the
 * {@link MusicLibraryManager } Class, and makes use of the Subjects callback method
 * for sending any state changes for sync purposes, as well as when the song
 * has finished playing.
 * 
 * <p>
 * Main constructor is {@link #MediaController(boolean shuffle, boolean repeat, boolean initialized)}<BR>
 * Default constructor is {@link #MediaController()}
 * 
 * @author dMacGit
 * 
 * @see StateObserver
 * @see MusicLibraryManager
 */

public class MediaController implements StateObserver
{	
	private final static String NAME = "[MediaController]";
	private StateSubject subject;
	private boolean shuffle;
	private boolean repeat;
	private Song currentSong;
	
	private final Object lock;
	private PlayerThread playSongControls;
	private Thread wrapperThread;
	private boolean threadStarted = false;
	private PlayState playState = PlayState.INITIALIZED;
	
	/**
	 * 
	 * This is the main Constructor for the MediaController class.
	 * 
	 * @param shuffle (Required) Boolean value that specifies if shuffle is active.
	 * @param repeat (Required) Boolean value that specifies if repeat is active.
	 * @param initialized (Required) Used to make sure the Controller starts
	 * in the initialized play-state.
	 */
	public MediaController(boolean shuffle, boolean repeat, boolean initialized) 
	{
		this.repeat = repeat;
		this.shuffle = shuffle;
		
		lock = new Object();
		if(initialized)
		{
			playState = PlayState.INITIALIZED;
			
		}
	}
	
	/**
	 * This is the default no parameter constructor.
	 * 
	 * @return new MediaController(false, false, true)
	 * @see {@link #MediaController(Boolean shuffle, Boolean repeat, PlayState initialized) }
	 */
	public MediaController() 
	{
		this(false,false,true);
	}
	
	
	private class PlayerThread implements Runnable
	{
		private AudioInputStream in,din;
	    private AudioFormat baseFormat;
	    private AudioFormat decodedFormat;
	    private SourceDataLine line;
	    private volatile boolean paused = false;
	    private boolean songFinished;
	    private File theSong;
	    
		public PlayerThread(File songFile)
		{
			//init any local variables!
			theSong = songFile;
			songFinished = false;
			paused = false;
			threadStarted = true;
		}
		@Override
		public void run()
	    {
	        din = null;

	        try
	        {
	            //File file = mp3file;
	            in = AudioSystem.getAudioInputStream(theSong);
	            baseFormat = in.getFormat();
	            decodedFormat = new AudioFormat(
	                    AudioFormat.Encoding.PCM_SIGNED,
	                    baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
	                    baseFormat.getChannels() * 2, baseFormat.getSampleRate(),
	                    false);
	            din = AudioSystem.getAudioInputStream(decodedFormat, in);
	            DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
	            line = (SourceDataLine) AudioSystem.getLine(info);
	            if (line != null)
	            {
	                line.open(decodedFormat);
	                byte[] data = new byte[4096];
	                // Start
	                line.start();

	                int nBytesRead;
	                synchronized (lock)
	                {
	                    while ((nBytesRead = din.read(data, 0, data.length)) != -1)
	                    {
	                        while (paused)
	                        {
	                            if (line.isRunning())
	                            {
	                                line.stop();
	                            }
	                            try
	                            {
	                                lock.wait();
	                            }
	                            catch (InterruptedException e)
	                            {
	                                System.out.println(NAME+" Play Interrupted!! "+e);
	                            }
	                        }

	                        if (!line.isRunning()) {
	                            line.start();
	                        }
	                        line.write(data, 0, nBytesRead);
	                    }
	                }

	                // Stop
	                line.drain();
	                line.stop();
	                line.close();
	                din.close();
	            }

	        }
	        catch (Exception e)
	        {
	            //e.printStackTrace();
	            System.out.println(NAME+" Exception in Player Thread "+e.getMessage());
	        }
	        finally
	        {
	            if (din != null)
	            {

	                try
	                {
	                    din.close();
	                    threadStarted = false;
	                }
	                catch (IOException e)
	                {
	                	System.out.println(NAME+" IOException in Player Thread "+e.getMessage());
	                }
	            }
	        }
	        songFinished = true;
	        if(playState != PlayState.SHUTDOWN)
	        {
	        	playState = PlayState.FINISHED;
				subject.stateSubjectCallback(getStateObserverName(), playState, null);
	        }
	        
	    }
		
		private void playSong()
	    {
	        synchronized (lock)
	        {
	            paused = false;
	            lock.notifyAll();
	        }
	    }
		
		private void pauseSong(){
	        paused = true;
		}
		
		private boolean isSongPaused()
	    {
	        return paused;
	    }
		
		private void stopSong()
	    {
	        try
	        {
	            line.stop();
	            line.close();
	            din.close();
	        }
	        catch (IOException | NullPointerException ex)
	        {
	            System.out.println(NAME+" Exception closing streams "+ex.getMessage());
	        }
	        songFinished = true;
	        playState = PlayState.FINISHED;
			subject.stateSubjectCallback(getStateObserverName(), playState, null);
	    }
		private void close()
	    {
	        try
	        {
	            line.stop();
	            line.close();
	            din.close();
	        }
	        catch (IOException | NullPointerException ex)
	        {
	            System.out.println(NAME+" Exception closing streams "+ex.getMessage());
	        }
	        songFinished = true;
	        //playState = PlayState.SHUTDOWN;
			//subject.stateSubjectCallback(getStateObserverName(), playState, null);
	    }
	}
	public Song getCurrentSong()
	{
		return this.currentSong;
	}
	public void play()
	{
		if(threadStarted)
		{
			playSongControls.playSong();
		}
		else
		{
			playSongControls = new PlayerThread(new File(getCurrentSong().getSongPath()));
			wrapperThread = new Thread(playSongControls);
			wrapperThread.start();
		}
	}
	public boolean isPaused(){
		return playSongControls.isSongPaused();
	}
	public void pause(){
		playSongControls.pauseSong();
	}
	
	public void stop(){
		
		playSongControls.stopSong();
	}
	public void close(){
		
		playSongControls.close();
	}
	
	@Override
	public String getStateObserverName() 
	{
		return this.getClass().getName();
	}
	@Override
	public void setParentSubject(StateSubject subject) {
		this.subject = subject;
	}
	@Override
	public void updateStateObserver(Song song, PlayState newState) 
	{
		System.out.println(NAME+" : ["+this.getStateObserverName()+"] Observer has recieved state: "+newState.toString());
		if(newState == PlayState.IDLE)
		{
			//IDLE STATE: DO NOTHING
		}
		else if(newState == PlayState.READY)
		{
			currentSong = song;
			playSongControls = new PlayerThread(new File(currentSong.getSongPath()));
			wrapperThread = new Thread(playSongControls);
			playState = PlayState.READY;
			System.out.println(NAME+" : ["+this.getStateObserverName()+"] Song to play: "+currentSong.getMetaTag().toString());
			System.out.println(NAME+" : ["+this.getStateObserverName()+"] State has changed to: "+newState.toString());
			
			subject.stateSubjectCallback(getStateObserverName(), playState, null);
			
		}
		else if(newState == PlayState.PLAYING)
		{
			if(playState == PlayState.PAUSED)
			{
				if(wrapperThread != null && wrapperThread.isAlive() && playSongControls.isSongPaused())
				{
					play();
					playState = PlayState.PLAYING;
					subject.stateSubjectCallback(getStateObserverName(), playState, null);
				}
			}
			else if(playState == PlayState.READY && playSongControls != null && wrapperThread != null && currentSong != null)
			{
				wrapperThread.start();
				playState = PlayState.PLAYING;
				subject.stateSubjectCallback(getStateObserverName(), playState, null);
			}
			else{
				System.out.println(NAME+" Exception: Play requested when no PlaySongControls Thread! ");
			}
			
		}
		else if(newState == PlayState.SKIPPED_FORWARDS)
		{
			if(wrapperThread != null && wrapperThread.isAlive())
			{
				stop();
				playState = PlayState.READY;
				subject.stateSubjectCallback(getStateObserverName(), playState, null);
			}
			else{
				System.out.println(NAME+" Exception: Play requested when no PlaySongControls Thread! ");
			}
		}
		else if(newState == PlayState.PAUSED)
		{
			if(wrapperThread != null && wrapperThread.isAlive())
			{
				pause();
				playState = PlayState.PAUSED;
				subject.stateSubjectCallback(getStateObserverName(), playState, null);
			}
			else{
				System.out.println(NAME+" Exception: Play requested when no PlaySongControls Thread! ");
			}
		}
		else if(newState == PlayState.SHUTDOWN)
		{
			
			if(wrapperThread != null)
			{
				playState = newState;
				if(wrapperThread.isAlive())
				{
					close();
					playSongControls = null;
					wrapperThread = null;
				}
				
			}
			wrapperThread = null;
			playState = newState;
			System.out.println(NAME+" ["+this.getStateObserverName()+"] Is Now STOPPED: "+playState.toString());
			subject.stateSubjectCallback(getStateObserverName(), playState, null);
		}
		else if(newState == PlayState.STOPPED)
		{
			
			if(wrapperThread != null)
			{
				if(wrapperThread.isAlive())
				{
					stop();
					playSongControls = null;
					wrapperThread = null;
				}
				
			}
			wrapperThread = null;
			playState = newState;
			System.out.println(NAME+" ["+this.getStateObserverName()+"] Is Now STOPPED: "+playState.toString());
			subject.stateSubjectCallback(getStateObserverName(), playState, null);
		}
		System.out.println(NAME+" ["+this.getStateObserverName()+"] --> State has changed state to: "+playState.toString());
	}
	@Override
	public PlayState getCurrentPlayState() 
	{
		return this.playState;
	}
}
