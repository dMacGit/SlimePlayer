package slime.controller;

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
	
	public AnimationController(SongTag tagToAnimate) 
	{
		currentAnimatedTag = tagToAnimate;
		internalAnimatorThread = new AnimatorThread();
		runnableThread = new Thread(internalAnimatorThread);
		runnableThread.start();
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
						else{
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
