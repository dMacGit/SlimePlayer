package slime.utills;

import java.io.File;
import java.io.FilenameFilter;

public class MusicFileFilter implements FilenameFilter {	
	
	@Override
	public boolean accept(File dir, String name) 
	{
		if(name.endsWith(".mp3")){
			return true;
		}
		return false;
	}

}
