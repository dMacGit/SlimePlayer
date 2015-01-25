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

import slime.utills.ShuffleArray;

public class PlaySongsFromFolder
{
    private HashMap<Integer,String> listOfMP3, songTags;
    private boolean currentlyPlaying = false, stop = false, startPlayer = false, isPaused;
    public PlaySongControls playSong;
    private Thread songThread;
    private ScrollingText label;
    private JLabel scrollingTitle;
    private int labelXpos, labelWidth;
    private long HOLDINGS_FILE_LAST_MODIFIED;
    private final String HOLDINGS_FILE_NAME = "Lib_MP3player.txt", SONG_PATHS_FILE_NAME = "SongPaths.txt";
    private String HOLDINGS_FILE_PATH, SONG_PATHS_FILE_PATH, holdingInfoForCurrentSong,
            ID, Title, Artist, Album, Durration, Year, songTime, theCurrentSongTitle = null,
            FOLDER, FILE_DIR/* = "C:/Users/Phantom/Documents/D Programing/Software Engineering A/Assignments/RadioStation/RadioStation"*/;
    private TimerTask secondsUpdating;
    private Timer timerObject,seconds;
    private updateHolingsInfo updater;
    private byte songMinutes, songSeconds, pausedSeconds, pausedMinutes;

    public PlaySongsFromFolder(String dir)
    {
        FILE_DIR = dir+"/";
        HOLDINGS_FILE_PATH = FILE_DIR+HOLDINGS_FILE_NAME;
        SONG_PATHS_FILE_PATH = FILE_DIR+SONG_PATHS_FILE_NAME;
        timerObject = new Timer();
        seconds = new Timer();
        listOfMP3 = new HashMap<Integer,String>();
        songTags = new HashMap<Integer,String>();
        scrollingTitle = new JLabel("");
        scrollingTitle.setPreferredSize(new Dimension(225,25));
        scrollingTitle.setMinimumSize(new Dimension(225,25));
        scrollingTitle.setMaximumSize(new Dimension(225,25));
        scrollingTitle.setForeground(Color.WHITE);
        readHoldingsFile();
        playRandomSong play = new playRandomSong();
        play.start();
        System.out.println("Player has been Started!");
    }
    public HashMap<Integer,String> getMapOfSong()
    {
        return songTags;
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
    public class secondsUpdating extends TimerTask
    {

        public void run()
        {
            if(!isPaused)
            {
                boolean updated = false;
                if(songMinutes < 10 && songSeconds < 10)
                {
                    songTime = ("0"+songMinutes+":0"+songSeconds);
                }
                else if(songMinutes >= 10 && songSeconds >= 10)
                {
                    songTime = (songMinutes+":"+songSeconds);
                }
                else if(songMinutes < 10 && songSeconds >= 10)
                {
                    songTime = ("0"+songMinutes+":"+songSeconds);
                }
               
                if(songSeconds < 59)
                {
                    songSeconds++;
                }
                else
                {
                    songSeconds = 0;
                    songMinutes++;
                }
            }
        }
    }
    public String getSongTime()
    {
        return songTime;
    }
    public String getCurrentSongInfo()
    {
        if(Album.compareTo("") == 0)
        {
            Album = Title;
        }
        return new String(Artist+"   :   "+Title+"     "+Album+"     "+Year);
    }
    public class playRandomSong extends Thread
    {
        private int[] shuffledList = new int[getTheNum()];
        private int[] orderedList = new int[getTheNum()];

        public playRandomSong()
        {
            for(int xy = 0; xy < getTheNum(); xy++)
            {
                shuffledList[xy] = xy+1;
                orderedList[xy] = xy+1;
            }
            ShuffleArray.shuffleArray(shuffledList);
            System.out.println("Ordeded list["+orderedList.length+"]: ");
            for(int indexA = 0; indexA < orderedList.length; indexA++)
            {
                System.out.print(orderedList[indexA]+",");
            }
            System.out.println("Shuffleded list["+shuffledList.length+"]: ");
            for(int indexA = 0; indexA < shuffledList.length; indexA++)
            {
                System.out.print(shuffledList[indexA]+",");
            }
        }

        @Override
        public void run()
        {
            int currentPlayNum = 0;
            while(!stop)
            {
                if(!currentlyPlaying)
                {
                    System.out.println("Choosing Next song!!");
                    System.out.println("Number chosen: "+shuffledList[currentPlayNum]+" out of "+(getTheNum()));
                    System.out.println("ID was for: "+songTags.get(shuffledList[currentPlayNum]));
                    File songFile = new File(getTheSong(shuffledList[currentPlayNum]));
                    /*if(new File(HOLDINGS_FILE).lastModified() != HOLDINGS_FILE_LAST_MODIFIED)
                    {
                        System.out.println("Holdings file was modified durring play: ["+HOLDINGS_FILE_LAST_MODIFIED+"] <!=> ["+new File(HOLDINGS_FILE).lastModified()+"]");
                        setLastChecked(new File(HOLDINGS_FILE).lastModified());
                        getUpdatedSongHoldingFileInfo(x);
                    }*/
                    setHoldingInfoForCurrentSong(songTags.get(shuffledList[currentPlayNum]));
                    updater = new updateHolingsInfo();
                    timerObject.schedule(updater, (Integer.parseInt(Durration)/2)*1000);
                    theCurrentSongTitle = getCurrentSongInfo();
                    int checkTime = (Integer.parseInt(Durration))/2;
                    int realTime = (checkTime%60);
                    System.out.println(theCurrentSongTitle+" <=["+Durration+"]=> "+checkTime+" ---> "+(int)(checkTime/60)+":"+realTime);
                    playSong = new PlaySongControls(songFile);
                    label = new ScrollingText( theCurrentSongTitle, labelXpos,labelWidth);
                    songThread = new Thread(playSong);
                    secondsUpdating = new secondsUpdating();
                    seconds = new Timer();
                    seconds.scheduleAtFixedRate(secondsUpdating, 100, 1000);
                    songThread.start();
                    currentlyPlaying = true;
                    currentPlayNum++;
                }

                if(!songThread.isAlive())
                {
                    currentlyPlaying = false;
                    System.out.println("Song has finished!!");
                    songThread = null;
                    songMinutes = 0;
                    songSeconds = 0;
                    pausedSeconds = 0;
                    pausedMinutes = 0;
                    seconds.cancel();
                }
                if(label.getImage() != null)
                {
                    scrollingTitle.setIcon(new ImageIcon(label.getImage()));
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
        playSong.userPressedPause();
        pausedSeconds = songSeconds;
        pausedMinutes = songMinutes;
        isPaused = true;
    }
    public void playTheSong()
    {
        label.startAnimation();
        isPaused = false;
        songSeconds = pausedSeconds;
        songMinutes = pausedMinutes;
        pausedSeconds = 0;
        pausedMinutes = 0;
        playSong.userPressedPlay();
    }
    public void skipTheSong()
    {
        if(isPaused)
        {
            playTheSong();
        }
        songMinutes = 0;
        songSeconds = 0;
        pausedSeconds = 0;
        pausedMinutes = 0;
        songTime = "00:00";
        playSong.stopPlaying();
        seconds.cancel();
        updater.cancel();
        songThread.stop();
    }
    public void setJLabelBounds(int xPosition, int width)
    {
        labelXpos = xPosition;
        labelWidth = width;
    }
    public JLabel getLabel()
    {
        return scrollingTitle;
    }
    public String getTheSongName()
    {
        return theCurrentSongTitle;
    }
    public boolean getPlayState()
    {
        return playSong.pausedOrNot();
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
}
