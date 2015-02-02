package slime.media;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SongList
{
	private int size = 0;
	private List<Song> listOfSongs;
	private int totalPlayTime = 0;
	
	public SongList(Song[] songTagsArray) 
	{
		listOfSongs = new ArrayList<Song>();
		listOfSongs = Arrays.asList(songTagsArray);
		
		size = listOfSongs.size();
		
		for(Song nextSong : listOfSongs){
			totalPlayTime += nextSong.getMetaTag().getDurration();
		}
	}

	public int getSize() {
		return size;
	}

	public List<Song> getListOfSongs() {
		return listOfSongs;
	}

	public int getTotalPlayTime() {
		return totalPlayTime;
	}
	
	

}
