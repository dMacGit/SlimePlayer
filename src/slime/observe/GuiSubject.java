package slime.observe;

import javax.swing.JLabel;

import slime.media.PlayState;
import slime.song.Song;
import slime.song.SongTag;

public interface GuiSubject 
{
	public void registerGuiObserver(GuiObserver obj);
	public void deregisterGuiObserver(GuiObserver obj);
	public void notifyAllObservers(PlayState newState);
	public void guiCallback(PlayState state, Song song);
}
