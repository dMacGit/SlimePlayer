package slime.utills;

public class ActionTimer 
{
	private static long timeOfLastAction;
	
	public ActionTimer()
	{
		timeOfLastAction = System.currentTimeMillis();
	}
	
	/*
     * This method simply measures the time between the current time and the time
     * of the last action or event that was captured!
     */
    private static long measurePreviouseActionTime(long currentTimeInMilliseconds)
    {
    	return (currentTimeInMilliseconds - timeOfLastAction);
    }
    
    /*
     * This method just updates the previous action trigger time
     */
    public static void triggerTimedActionStart()
    {
    	timeOfLastAction = System.currentTimeMillis();
    }
    
    /*
     * Simple toString that returns our formated string
     */
    public static String formatLastTimedAction(String actionNameText)
    {
    	return new String("Action =={ "+actionNameText + " }== has taken: "+(measurePreviouseActionTime(System.currentTimeMillis())/1000+" Seconds!"));
    }
}
