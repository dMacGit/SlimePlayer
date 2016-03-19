package slime.observe;

import javax.swing.JLabel;

import slime.media.PlayState;
import slime.media.Song;
import slime.media.SongTag;

public interface GuiSubject 
{
	public void registerGuiObserver(GuiObserver obj);
	public void deregisterGuiObserver(GuiObserver obj);
	public void notifyAllObservers(PlayState newState);
	public void guiCallback(PlayState state, Song song);
}
