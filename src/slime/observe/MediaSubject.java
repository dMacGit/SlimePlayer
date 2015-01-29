package slime.observe;

public interface MediaSubject
{
	public void registerMediaObserver(MediaObserver observer);
	public void deregisterMediaObserver(MediaObserver observer);
	public void notifyAllMediaObservers();
}
