package slime.core;

import java.io.*;
import javax.sound.sampled.*;

/**
 *
 * @author Phantom
 */
public class PlaySongControls extends Thread
{
    private AudioInputStream in,din;
    private AudioFormat baseFormat;
    private AudioFormat decodedFormat;
    // some lock somewhere...
    private final Object lock;
    // some paused variable
    private volatile boolean paused = false;
    private File theSong;
    private boolean songFinished = false;
    private boolean pausedOrNot = false;
    private SourceDataLine line;


    public PlaySongControls(File mp3file)
    {
        lock = new Object();
        theSong = mp3file;
        System.out.println(theSong+" :: Currently Playing!!");
    }
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
    public void userPressedPause()
    {
        pausedOrNot = true;
        paused = true;
    }
    public void userPressedPlay()
    {
        synchronized (lock)
        {
            paused = false;
            pausedOrNot = false;
            lock.notifyAll();
        }
    }
    public boolean pausedOrNot()
    {
        return pausedOrNot;
    }
    public void stopPlaying()
    {
        try
        {
            line.stop();
            line.close();
            din.close();
        }
        catch (IOException ex)
        {
            System.out.println("Error closing [din] "+ex);
        }
        songFinished = true;
    }
}
