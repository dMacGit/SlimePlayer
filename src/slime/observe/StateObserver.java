package slime.observe;

import slime.media.PlayState;
import slime.song.Song;

/**
 * <P><B>
 * This is the interface for each of the classes requiring
 * updates to and from the {@link MusicLibraryManager} class.</B>
 * <p>
 * It contains useful methods for the Subject class to query each Observer; <br> Such as the Observer's Name
 * ( {@link #getStateObserverName()} ), its current PlayeSate ( {@link #getCurrentPlayState()} ), assigning
 * itself as the parentSubject of that observer ( {@link #setParentSubject(StateSubject)} ), as well
 * as the main update method used by the Subject class for PlayState changes and songFile changes 
 * ( {@link #updateStateObserver(Song, PlayState)} ).
 * 
 * @author dMacGit
 */
public interface StateObserver
{	
	/** @return StateObserver Name as a String. */
	public String getStateObserverName();
	
	/** @return StateObserver's current state as PlayState object */
	public PlayState getCurrentPlayState();
	
	/** @Sets StateObserver's parent Subject as StateSubject object 
	 *  @param subject (Required) Used to set the passed Subject as
	 *  the Observers new Subject class */
	public void setParentSubject(StateSubject subject);
	
	/** @Updates StateObserver's current state as PlayState object 
	 *	@param songFile (Optional) Used to notify the Observer of a new song
	 *	@param state (Required) Used to notify the Observer of the current player 
	 *  application state change */
	public void updateStateObserver(Song songFile, PlayState state);
}
