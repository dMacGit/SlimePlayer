package slime.observe;

import javax.swing.JLabel;

import slime.media.PlayState;
import slime.media.Song;

public interface StateSubject
{
	public void registerStateObserver(StateObserver observer);
	public void deregisterStateObserver(StateObserver observer);
	public void notifyAllStateObservers(Song song, PlayState state);
	public void stateSubjectCallback(String observerName, PlayState state, JLabel label);
}
