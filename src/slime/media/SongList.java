package slime.media;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SongList
{
	private int size = 0;
	private ArrayList<Song> listOfSongs;
	private int totalPlayTime = 0;
	
	public SongList(Song[] songTagsArray) 
	{
		listOfSongs = new ArrayList<Song>();
		listOfSongs = (ArrayList<Song>) Arrays.asList(songTagsArray);
		
		size = listOfSongs.size();
		
		for(Song nextSong : listOfSongs){
			totalPlayTime += nextSong.getMetaTag().getDurration();
		}
	}
	public SongList()
	{
		listOfSongs = new ArrayList<Song>();
	}
	
	public void setCappacity(int size)
	{
		if(isEmpty())
		{
			this.size = size;
		}
	}
	
	public boolean isEmpty(){
		if(this.size > 0){
			return false;
		}
		else return true;
	}
	
	public void addSong(Song songToAdd)
	{
		listOfSongs.add(songToAdd);
		size++;
		totalPlayTime += songToAdd.getMetaTag().getDurration();
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
