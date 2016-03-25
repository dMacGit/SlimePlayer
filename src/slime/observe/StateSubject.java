package slime.observe;

import javax.swing.JLabel;

import slime.media.PlayState;
import slime.song.Song;

/**
 * <P><B>
 * This is the interface for the Subject class.</B>
 * <p>
 * It contains the required methods for the Subject class to maintain its Observers.
 * Such as registering ( {@link #registerStateObserver(StateObserver) } ) and de-registering Observers
 * ( {@link #deregisterStateObserver(StateObserver) } ), notifying all its observers 
 * ( {@link #notifyAllStateObservers(Song, PlayState) } ), and lastly its call-back method 
 * ( {@link #stateSubjectCallback(String, PlayState, Song) } ) which is used by the Observer itself
 * to update the Subject of any of its changes of state.
 * 
 * @author dMacGit
 */
public interface StateSubject
{
	/** @void Adds the Observer to the Subjects list of registered Observers 
	 *	@param observer (Required) The Observer Object to be registered
	 */
	public void registerStateObserver(StateObserver observer);
	
	/** @void Removes the Observer from the Subjects list of registered Observers 
	 *	@param observer (Required) The Observer Object to be de-registered
	 */
	public void deregisterStateObserver(StateObserver observer);
	
	/** @void Sends updates to all the Observers registered with this Subject 
	 *	@param song (Optional) The new Song to be played
	 *  @param state (Required) The updated state of the player application 
	 */
	public void notifyAllStateObservers(Song song, PlayState state);
	
	/** @void Used by Observers to notify the Subject of a state change
	 * 	@param observerName (Required) The name of the Observer Class
	 *  @param state (Required) The current PlayState of the calling Observer
	 *  @param song (Optional) The new Song to be played
	 */
	public void stateSubjectCallback(String observerName, PlayState state, Song song);
}
