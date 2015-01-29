package slime.observe;

import slime.media.PlayState;
import slime.media.SongTag;

public interface AnimatorObserver 
{
	public String getAnimatorObserverName();
	public void updateAnimatorObserver(PlayState playState, SongTag tagData);
}
