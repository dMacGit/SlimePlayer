package slime.media;

public class MusicLibrary implements Playable
{
	private SongList libraryList;
	
	public MusicLibrary(SongList songList) 
	{
		libraryList = songList;
	}

	@Override
	public String getTotalTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SongList getSongList() {
		// TODO Auto-generated method stub
		return null;
	}

}
