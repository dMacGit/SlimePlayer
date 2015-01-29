package slime.observe;

import slime.media.PlayState;

public interface MediaObserver
{
	public String getMediaObserverName();
	public void updateMediaObserver(PlayState stateOfPlayer);
}
