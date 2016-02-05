package slime.observe;

import slime.media.PlayState;
import slime.media.SongTag;

public interface GuiSubject 
{
	public void registerGuiObserver(GuiObserver obj);
	public void deregisterGuiObserver(GuiObserver obj);
	public void notifyAllObservers();
	public void guiCallback(PlayState state, SongTag tagInfo);
}
