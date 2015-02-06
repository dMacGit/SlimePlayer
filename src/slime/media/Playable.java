package slime.media;

public interface Playable 
{
	public String getTotalTime();
	public SongList getSongList();
	public SongTag getNextSong();
	public SongTag getPreviousSong();
	
	
}
