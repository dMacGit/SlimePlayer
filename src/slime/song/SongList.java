package slime.song;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * <b>
 * The SongList class is the Data-structure for holding all of the Song objects in a
 * list object. 
 * </b>
 * <p>
 * This Data-structure is used inside a PlayList object for allowing for adding and removing
 * of Song objects from the list of playable songs, all while maintaining a total play duration.
 * </p>
 * 
 * @see Song
 * 
 * @author dMacGit
 *
 */

public class SongList extends ArrayList<Song>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3826767102563303952L;

	private int totalPlayTime = 0;
	
	public SongList(Collection<Song> songTagsCollection) 
	{
		super();
		
		if(songTagsCollection != null && !songTagsCollection.isEmpty())
		{
			super.addAll(songTagsCollection);
			Iterator<Song> CollectionIterator = songTagsCollection.iterator();
			while(CollectionIterator.hasNext())
			{
				totalPlayTime += CollectionIterator.next().getMetaTag().getDurration();
			}
		}
	}
	
	public SongList()
	{
		this(null);
	}
	
	
	public void addTrack(Song songToAdd)
	{
		super.add(songToAdd);
		totalPlayTime += songToAdd.getMetaTag().getDurration();
	}
	
	public void removeTrack(Song songToAdd)
	{
		super.remove(songToAdd);
		totalPlayTime -= songToAdd.getMetaTag().getDurration();
	}

	public int getNumberOfTracks() {
		return super.size();
	}

	public int getTotalPlayTime() {
		return totalPlayTime;
	}
	
	public LinkedList<SongTag> getMapOfTags() throws Exception
	{
		final LinkedList<SongTag> tempLinkedList = new LinkedList<SongTag>();
		if(!super.isEmpty())
		{
			Iterator<Song> CollectionIterator = super.iterator();
			while(CollectionIterator.hasNext())
			{
				tempLinkedList.addLast(CollectionIterator.next().getMetaTag());
			}
		}
		else throw new Exception();
		
		return tempLinkedList;
	}

}
