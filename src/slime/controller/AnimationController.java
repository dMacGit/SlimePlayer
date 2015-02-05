package slime.controller;

import java.util.Timer;
import java.util.TimerTask;

import slime.core.PlaySongsFromFolder.secondsUpdating;
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
	
	public AnimationController(SongTag tagToAnimate) 
	{
		currentAnimatedTag = tagToAnimate;
		internalAnimatorThread = new AnimatorThread();
		runnableThread = new Thread(internalAnimatorThread);
		runnableThread.start();
		//secondsUpdating = new secondsUpdating();
        //seconds = new Timer();
		//seconds.scheduleAtFixedRate(secondsUpdating, 100, 1000);
        
	}
	public AnimationController() 
	{
		this(null);
	}
	
	private class AnimatorThread implements Runnable
	{
		@Override
		public void run()
		{
			while(notClossed)
			{
				
                
				if(currentAnimatedTag != null)
				{
					if(playerState == PlayState.STOPPED || playerState == PlayState.PAUSED)
					{
						//Keep text left aligned, and stationary
						if(playerState == PlayState.PAUSED){
							//Pause the timer
						}
						else
						{
							//Reset the timer
						}
							
					}
					else
					{
						//Animate the scrolling text!
						//Animate or increment the timer!
					}
				}
				
			}
		}
	}
	
	public class secondsUpdating extends TimerTask
    {
        public void run()
        {
            if(playerState != PlayState.PAUSED)
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

	@Override
	public String getAnimatorObserverName()
	{
		return this.OBSERVER_NAME;
	}

	@Override
	public void updateAnimatorObserver(PlayState playState, SongTag tagData) 
	{
		this.currentAnimatedTag = tagData;
		this.playerState = playState;
		//System.out.println("<<<< Now animating tag for: "+tagData.getArtist()+">>>>>");
		
	}

	
}
