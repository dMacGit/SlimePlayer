package slime.observe;

import javax.swing.JLabel;

import slime.media.PlayState;
import slime.media.SongTag;

public interface GuiSubject 
{
	public void registerGuiObserver(GuiObserver obj);
	public void deregisterGuiObserver(GuiObserver obj);
	public void notifyAllObservers();
	public void guiCallback(PlayState state, JLabel animatedLabel);
}
