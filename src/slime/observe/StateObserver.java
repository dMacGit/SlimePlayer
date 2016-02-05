package slime.observe;

import slime.media.PlayState;
import slime.media.Song;

public interface StateObserver
{	
	public String getStateObserverName();
	public PlayState getCurrentPlayState();
	public void setParentSubject(StateSubject subject);
	public void updateStateObserver(Song songFile, PlayState state);
}
