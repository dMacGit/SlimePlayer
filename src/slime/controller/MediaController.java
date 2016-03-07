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

public class MediaController implements StateObserver
{	
	private StateSubject subject;
	private boolean shuffle;
	private boolean repeat;
	private Song currentSong;
	
	private final Object lock;
	private PlayerThread playSongControls;
	private Thread wrapperThread;
	private boolean threadStarted = false;
	private PlayState playState = PlayState.INITIALIZED;
	
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
	public MediaController() 
	{
		this(false,false,true);
	}
	
	/*public void setSongList(SongList newListOfSongs){
		this.songList = newListOfSongs;
		System.out.println(":: Created Song list of "+newListOfSongs.getSize()+" Songs ::");
		this.findNextSong();
	}*/
	
	//Remove this method! Breaks Architecture.
	/*private void findNextSong()
	{
		System.out.println("Choosing Next song!!");
		int index;
		if(shuffle)
		{
			System.out.println("Shuffle is activated!");
			index = ((int)(Math.random()*songList.getSize()));
			System.out.println(":: Choosing "+index+" of "+songList.getSize()+" Songs ::");
			currentSong = songList.getListOfSongs().get(index);
			if(index < songList.getSize())
			{
				startOrder = ++index;
			}
			else startOrder = 0;
		}
		else
		{
			currentSong = songList.getListOfSongs().get(startOrder);
			index = ++startOrder;
		}
	}*/
	
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
	                                System.out.println("Play Interrupted!! "+e);
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
	            System.out.println("Exception in Player Thread "+e.getMessage());
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
	                	System.out.println("IOException in Player Thread "+e.getMessage());
	                }
	            }
	        }
	        songFinished = true;
	        if(playState != PlayState.SHUTDOWN){
	        	playState = PlayState.FINISHED;
				subject.stateSubjectCallback(getStateObserverName(), playState);
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
	            System.out.println("Exception closing streams "+ex.getMessage());
	        }
	        songFinished = true;
	        playState = PlayState.FINISHED;
			subject.stateSubjectCallback(getStateObserverName(), playState);
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
	            System.out.println("Exception closing streams "+ex.getMessage());
	        }
	        songFinished = true;
	        playState = PlayState.SHUTDOWN;
			subject.stateSubjectCallback(getStateObserverName(), playState);
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
	/*public void skip()
	{
		this.stop();
		findNextSong();
		playSongControls = new PlayerThread(new File(currentSong.getSongPath()));
		wrapperThread = new Thread(playSongControls);
		wrapperThread.start();
		
	}*/
	public void stop(){
		
		playSongControls.stopSong();
	}
	public void close(){
		
		playSongControls.close();
	}
	
	/*public void toggleShuffle()
	{
		this.shuffle = !shuffle;
		System.out.println("Shuffle has been toggled to: "+ shuffle);
	}
	public void toggleRepeat()
	{
		this.repeat = !repeat;
		System.out.println("Repeat has been toggled to: "+ repeat);
	}*/
	/*public void changeState(PlayState state)
	{
		this.playState = state;
		stateHandler();
	}
	private void stateHandler(){
		if(playState == PlayState.SHUFFLE_TOGGLED)
		{
			this.toggleShuffle();
		}
		else if(playState == PlayState.REPEAT_TOGGLED)
		{
			this.toggleRepeat();
		}
	}*/
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
		System.out.println("["+this.getStateObserverName()+"] Observer has recieved state: "+newState.toString());
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
			System.out.println("["+this.getStateObserverName()+"] Song to play: "+currentSong.getMetaTag().toString());
			System.out.println("["+this.getStateObserverName()+"] State has changed to: "+newState.toString());
			
			subject.stateSubjectCallback(getStateObserverName(), playState);
			
		}
		else if(newState == PlayState.PLAYING)
		{
			if(playState == PlayState.PAUSED)
			{
				if(wrapperThread != null && wrapperThread.isAlive() && playSongControls.isSongPaused())
				{
					play();
					playState = PlayState.PLAYING;
					subject.stateSubjectCallback(getStateObserverName(), playState);
				}
			}
			else if(playState == PlayState.READY && playSongControls != null && wrapperThread != null && currentSong != null)
			{
				wrapperThread.start();
				playState = PlayState.PLAYING;
				subject.stateSubjectCallback(getStateObserverName(), playState);
			}
			else{
				System.out.println("Exception: Play requested when no PlaySongControls Thread! ");
			}
			
		}
		else if(newState == PlayState.SKIPPED_FORWARDS)
		{
			if(wrapperThread != null && wrapperThread.isAlive())
			{
				stop();
				playState = PlayState.READY;
				subject.stateSubjectCallback(getStateObserverName(), playState);
			}
			else{
				System.out.println("Exception: Play requested when no PlaySongControls Thread! ");
			}
		}
		else if(newState == PlayState.PAUSED)
		{
			if(wrapperThread != null && wrapperThread.isAlive())
			{
				pause();
				playState = PlayState.PAUSED;
				subject.stateSubjectCallback(getStateObserverName(), playState);
			}
			else{
				System.out.println("Exception: Play requested when no PlaySongControls Thread! ");
			}
		}
		else if(newState == PlayState.SHUTDOWN)
		{
			
			if(wrapperThread != null)
			{
				if(wrapperThread.isAlive())
				{
					close();
					playSongControls = null;
					wrapperThread = null;
				}
				
			}
			wrapperThread = null;
			playState = newState;
			System.out.println("["+this.getStateObserverName()+"] Is Now STOPPED: "+playState.toString());
			subject.stateSubjectCallback(getStateObserverName(), playState);
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
			System.out.println("["+this.getStateObserverName()+"] Is Now STOPPED: "+playState.toString());
			subject.stateSubjectCallback(getStateObserverName(), playState);
		}
		System.out.println("["+this.getStateObserverName()+"] --> State has changed state to: "+playState.toString());
	}
	@Override
	public PlayState getCurrentPlayState() 
	{
		return this.playState;
	}
}
