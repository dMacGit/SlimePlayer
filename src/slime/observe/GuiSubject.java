package slime.observe;

import slime.media.PlayState;
import slime.song.Song;

/**
 * <P><B>
 * This is the interface for the Subject class.</B>
 * <p>
 * It contains the required methods for the Subject class to maintain its Observers.
 * Such as registering ( {@link #registerGuiObserver(GuiObserver) } ) and de-registering Observers
 * ( {@link #deregisterGuiObserver(GuiObserver) } ), notifying all its observers 
 * ( {@link #notifyAllObservers(PlayState) } ), and lastly its call-back method 
 * ( {@link #guiCallback(PlayState, Song) } ) which is used by the Observer itself
 * to update the Subject of any of its changes of state.
 * 
 * @author dMacGit
 */
public interface GuiSubject 
{
	/** @void Adds the Observer to the Subjects list of registered Observers 
	 *	@param observer (Required) The Observer Object to be registered
	 */
	public void registerGuiObserver(GuiObserver observer);
	
	/** @void Removes the Observer from the Subjects list of registered Observers 
	 *	@param observer (Required) The Observer Object to be de-registered
	 */
	public void deregisterGuiObserver(GuiObserver observer);
	
	/** @void Sends updates to all the Observers registered with this Subject 
	 *  @param newState (Required) The updated state of the player application 
	 */
	public void notifyAllObservers(PlayState newState);
	
	/** @void Used by Observers to notify the Subject of a state change
	 *  @param state (Required) The current PlayState of the calling Observer
	 *  @param song (Optional) The new Song to be played
	 */
	public void guiCallback(PlayState state, Song song);
}
