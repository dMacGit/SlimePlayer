package slime.controller;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import slime.core.ScrollingText;
import slime.media.PlayState;
import slime.media.SongTag;
import slime.observe.AnimatorObserver;

public class AnimationController implements AnimatorObserver
{
	private AnimatorThread internalAnimatorThread;
	private boolean notClossed = true;
	private PlayState playerState = PlayState.STOPPED;
	private String OBSERVER_NAME = "AnimationController";
	private SongTag currentAnimatedTag = null;
	private Thread runnableThread = null;
	private Timer seconds;
	private TimerTask secondsUpdating;
	private byte songMinutes, songSeconds, pausedSeconds, pausedMinutes;
	private String songTime;
	private boolean isPaused = true;
    private ScrollingText label;
    private JLabel scrollingTitleLabel;
    private int labelWidth;
	
	public AnimationController(SongTag tagToAnimate) 
	{
		scrollingTitleLabel = new JLabel("");
        scrollingTitleLabel.setPreferredSize(new Dimension(225,25));
        scrollingTitleLabel.setMinimumSize(new Dimension(225,25));
        scrollingTitleLabel.setMaximumSize(new Dimension(225,25));
        scrollingTitleLabel.setForeground(Color.WHITE);
		
		currentAnimatedTag = tagToAnimate;
		internalAnimatorThread = new AnimatorThread(currentAnimatedTag);
		runnableThread = new Thread(internalAnimatorThread);
		runnableThread.start();
		secondsUpdating = new secondsUpdating();
        seconds = new Timer();
		seconds.scheduleAtFixedRate(secondsUpdating, 100, 1000);
        
	}
	public AnimationController() 
	{
		this(null);
	}
	public void startTimer(){
		secondsUpdating.run();
	}
	public void stopTimer(){
		secondsUpdating.cancel();
	}
	public void pauseTimer(){
		
	}
	
	private class AnimatorThread implements Runnable
	{
		public AnimatorThread(SongTag songTag)
		{
			if(songTag != null)
			{
				label = new ScrollingText( songTag ,labelWidth);
				if(label.getImage() != null)
	            {
	                scrollingTitleLabel.setIcon(new ImageIcon(label.getImage()));
	            }
			}
		}
		
		@Override
		public void run()
		{
			while(notClossed)
			{
				
                
				if(currentAnimatedTag != null)
				{
					if(playerState == PlayState.PLAYING)
					{
						scrollingTitleLabel.setIcon(new ImageIcon(label.getImage()));
					}
					
					
					/*if(playerState == PlayState.STOPPED || playerState == PlayState.PAUSED)
					{
						//Keep text left aligned, and stationary
						//scrollingTitleLabel.setIcon(new ImageIcon(label.getImage()));
						if(playerState == PlayState.PAUSED)
						{

						}
						else
						{

						}
							
					}
					else
					{
						scrollingTitleLabel.setIcon(new ImageIcon(label.getImage()));
						//Animate the scrolling text!
						//Animate or increment the timer!
					}*/
				}
				
			}
		}
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
	
	
	public void setJLabelBounds(int xPosition, int width)
    {
        labelWidth = width;
    }
    public JLabel getLabel()
    {
        return scrollingTitleLabel;
    }
    
	public String getSongTime()
    {
        return songTime;
    }

	@Override
	public String getAnimatorObserverName()
	{
		return this.OBSERVER_NAME;
	}

	@Override
	public void updateAnimatorObserver(PlayState playState, SongTag tagData) 
	{
		this.stateHandler(playState, tagData);
		//System.out.println("<<<< Now animating tag for: "+tagData.getArtist()+">>>>>");
		
	}
	
	private void stateHandler(PlayState newState, SongTag tagData)
	{
		if(tagData != this.currentAnimatedTag)
		{
			this.currentAnimatedTag = tagData;
			
			System.out.println("==========> Songtag changed to -> "+tagData.getSongTitle());
			
			if(label == null)
			{
				label = new ScrollingText( this.currentAnimatedTag ,labelWidth);
				System.out.println("Label has been initialized!");
				if(label.getImage() != null)
	            {
	                scrollingTitleLabel.setIcon(new ImageIcon(label.getImage()));
	                System.out.println("Icon has also been set for the first time!");
	            }
			}
			else
			{
				label.changeTag(currentAnimatedTag);
				if(label.getImage() != null)
	            {
	                scrollingTitleLabel.setIcon(new ImageIcon(label.getImage()));
	                System.out.println("The icon has been reset!");
	            }
			}
			//Reload the JLabel with the updated SongInfo
		}
		
		if(newState == PlayState.REPEAT_TOGGLED || newState == PlayState.SHUFFLE_TOGGLED)
		{
			//Do nothing!
		}
		else if(newState == PlayState.PAUSED || newState == PlayState.PLAYING || newState == PlayState.STOPPED)
		{ 
			if(newState == PlayState.PAUSED && !this.isPaused)
			{
				isPaused = true;
				pausedSeconds = songSeconds;
		        pausedMinutes = songMinutes;
		        label.pauseAnimation();
		        while(!label.pauseAnimation())
		        {
		        	
		        }
		        scrollingTitleLabel.setIcon(new ImageIcon(label.getImage()));
		        
			}
			else if(newState == PlayState.PLAYING && this.isPaused)
			{
				songSeconds = pausedSeconds;
		        songMinutes = pausedMinutes;
		        pausedSeconds = 0;
		        pausedMinutes = 0;
		        isPaused = false;
		        label.startAnimation();
			}
			playerState = newState; 
		}
		
		if(newState == PlayState.SKIPPED_FORWARDS || newState == PlayState.SKIPPED_BACK)
        {
			if(this.isPaused)
			{
				songMinutes = 0;
	            songSeconds = 0;
	            pausedSeconds = 0;
	            pausedMinutes = 0;
	            songTime = "00:00";
				isPaused = false;
				
			}
			else
			{
				//Reset the time!
	        	songMinutes = 0;
	            songSeconds = 0;
	            pausedSeconds = 0;
	            pausedMinutes = 0;
	            songTime = "00:00";
	            label.resetAnimation();
	        	label.resetTimer();
	        	label.changeTag(this.currentAnimatedTag);
	        	label.startAnimation();
			}
        	
            //playerState = newState;
        }
		
		
	}
	
}
