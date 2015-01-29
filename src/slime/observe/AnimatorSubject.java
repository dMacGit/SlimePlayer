package slime.observe;

public interface AnimatorSubject 
{
	public void registerAnimatorObserver(AnimatorObserver observer);
	public void deregisterAnimatorObserver(AnimatorObserver observer);
	public void notifyAllAnimatorObservers();
}
