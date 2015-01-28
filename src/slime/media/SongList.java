package slime.media;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SongList
{
	private int size = 0;
	private List<SongTag> listOfSongs;
	private int totalPlayTime = 0;
	
	public SongList(SongTag[] songTagsArray) 
	{
		listOfSongs = new ArrayList<SongTag>();
		listOfSongs = Arrays.asList(songTagsArray);
		
		size = listOfSongs.size();
		
		for(SongTag nextTag : listOfSongs){
			totalPlayTime += nextTag.getDurration();
		}
	}

	public int getSize() {
		return size;
	}

	public List<SongTag> getListOfSongs() {
		return listOfSongs;
	}

	public int getTotalPlayTime() {
		return totalPlayTime;
	}
	
	

}
