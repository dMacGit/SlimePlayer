package slime.utills;

import java.io.IOException;
import java.net.URL;

public class ImageLoader 
{
	public static boolean imageValidator(String location, String fileName)
	{
		
		try {
			URL validated = ClassLoader.getSystemResource(location+""+fileName); 
			if (validated != null)
			{
				System.out.println("The file was validated!");
				return true;
			}
		}
		catch(NullPointerException ex)
		{
			ex.printStackTrace();
		}
		return false;
	}
}
