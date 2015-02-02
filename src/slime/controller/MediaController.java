package slime.controller;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import slime.media.Song;
import slime.media.SongList;
import slime.media.SongTag;

public class MediaController 
{
	private boolean shuffle;
	private boolean repeat;
	private SongList songList;
	private Song currentSong;
	private int[] finishedSongs;
	
	// ----- separation of file variables
	
	private final Object lock;
	private PlayerThread playSongControls;
	private Thread wrapperThread;
	private int startOrder = 0;
	private boolean threadStarted = false;
	
	public MediaController(SongList listOfSongs, boolean shuffle, boolean repeat, boolean initialized) 
	{
		songList = listOfSongs;
		
		this.repeat = repeat;
		this.shuffle = shuffle;
		
		lock = new Object();
		if(initialized)
		{
			findNextSong();
			//finishedSongs = new int[listOfSongs.getSize()];
			playSongControls = new PlayerThread(new File(currentSong.getSongPath()));
			wrapperThread = new Thread(playSongControls);
		}
	}
	public MediaController() 
	{
		this(null, false,false,false);
	}
	
	public void setSongList(SongList newListOfSongs){
		this.songList = newListOfSongs;
		this.findNextSong();
	}
	
	public boolean hasPlaylist()
	{
		if(this.songList != null && !this.songList.isEmpty())
		{
			return true;
		} 
		else return false;
			
		
	}
	
	private void findNextSong()
	{
		System.out.println("Choosing Next song!!");
		if(shuffle)
		{
			int randomIndex = ((int)(Math.random()*songList.getSize()));
			currentSong = songList.getListOfSongs().get(randomIndex);
		}
		else
		{
			currentSong = songList.getListOfSongs().get(startOrder++);
		}
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
	            System.out.println("There has been an error! "+e);
	        }
	        finally
	        {
	            if (din != null)
	            {

	                try
	                {
	                    din.close();

	                }
	                catch (IOException e)
	                {
	                    System.out.println("This stream is closed! "+e);
	                }
	            }
	        }
	        songFinished = true;
	    }
		
		public void playSong()
	    {
	        synchronized (lock)
	        {
	            paused = false;
	            lock.notifyAll();
	        }
	    }
		
		public void pauseSong(){
	        paused = true;
		}
		
		public boolean isSongPaused()
	    {
	        return paused;
	    }
		
		public void stopSong()
	    {
	        try
	        {
	            line.stop();
	            line.close();
	            din.close();
	        }
	        catch (IOException | NullPointerException ex)
	        {
	            System.out.println("Error closing [din] "+ex);
	        }
	        songFinished = true;
	    }
	}
	public Song getCurrentSong(){
		return this.currentSong;
	}
	public void play()
	{
		if(threadStarted)
		{
			wrapperThread.start();
			playSongControls.playSong();
		}
		else
		{
			findNextSong();
			playSongControls = new PlayerThread(new File(currentSong.getSongPath()));
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
	public void skip()
	{
		this.stop();
		findNextSong();
		playSongControls = new PlayerThread(new File(currentSong.getSongPath()));
		wrapperThread = new Thread(playSongControls);
		wrapperThread.start();
		
	}
	public void stop(){
		
		playSongControls.stopSong();
	}
	
	public void toggleShuffle(){
		this.shuffle = !shuffle;
	}
	public void toggleRepeat(){
		this.repeat = !repeat;
	}
}
