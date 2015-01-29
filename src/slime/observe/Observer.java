package slime.observe;

import slime.media.PlayState;

public interface Observer 
{
	public String getObserverName();
	public void update(PlayState stateOfPlayer);
}
