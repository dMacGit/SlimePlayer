package slime.observe;

import slime.media.PlayState;

public interface GuiObserver 
{
	public String getGuiObserverName();
	public void setParentSubject(GuiSubject subject);
	public void updateGuiObserver(PlayState state);
}
