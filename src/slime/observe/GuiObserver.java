package slime.observe;

import slime.media.PlayState;
import slime.song.Song;

/**
 * <P><B>
 * This is the interface for each of the classes requiring
 * updates to and from the {@link slime.core.PlayerGUI } class.</B>
 * <p>
 * It contains useful methods for the Subject class to query each Observer; <br> Such as the Observer's Name
 * ( {@link #getGuiObserverName()} ), assigning itself as the parentSubject of that observer
 * ( {@link #setParentSubject(GuiSubject)} ), as well as the main update method used by the Subject class
 * for PlayState changes and songFile changes ( {@link #updateGuiObserver(PlayState)} ).
 * 
 * @author dMacGit
 */
public interface GuiObserver 
{
	/** @return GuiObserver Name as a String. */
	public String getGuiObserverName();
	
	/** @Sets GuiObserver's parent Subject as GuiSubject object 
	 *  @param subject (Required) Used to set the passed Subject as
	 *  the Observers new Subject class */
	public void setParentSubject(GuiSubject subject);
	
	/** @Updates GuiObserver's current state as PlayState object 
	 *	@param state (Required) Used to notify the Observer of the current player 
	 *  application state change */
	public void updateGuiObserver(PlayState state);
}
