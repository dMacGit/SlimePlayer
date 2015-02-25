package slime.utills;

public class ActionTimer 
{	
	/*
     * This method simply measures the time between the current time and the time
     * of the last action or event that was captured!
     */
    public static long measurePreviouseActionTime(Long pastTimeIsMilliseconds, long currentTimeInMilliseconds)
    {
    	return (currentTimeInMilliseconds - pastTimeIsMilliseconds);
    }
    
    /*
     * This method just updates the previous action trigger time
     */
    public static long triggerTimedActionStart()
    {
    	return System.currentTimeMillis();
    }
    
    /*
     * Simple toString that returns our formated string
     */
    public static String formatLastTimedAction(String actionNameText, long timeResult)
    {
    	return new String("Action =={ "+actionNameText + " }== has taken: "+(timeResult)+" Milliseconds!");
    }
}
