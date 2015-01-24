package slime.junit;



import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class JUnit_TestRunner {

	public static void main(String[] args)
	{	
		Result DirectoryTestResults = JUnitCore.runClasses(JUnit_DirectoryTests.class);
		
		for(Failure failure : DirectoryTestResults.getFailures()) 
		{
			System.out.println(" <<< Directory Tests Completed {"+failure.toString()+"} and FAILED! >>> ");
		}
		System.out.println(" <<< Directory Tests Completed {"+DirectoryTestResults.wasSuccessful()+"} and PASSED! >>> ");
		
		Result LibrarySearchTestResults = JUnitCore.runClasses(JUnit_LibrarySearchTest.class);
		for(Failure failure : LibrarySearchTestResults.getFailures()) 
		{
			System.out.println(" <<< Directory Tests Completed {"+failure.toString()+"} and FAILED! >>> ");
		}
		System.out.println(" <<< Directory Tests Completed {"+LibrarySearchTestResults.wasSuccessful()+"} and PASSED! >>> ");
	}

}
