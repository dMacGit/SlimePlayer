package slime.core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;


import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;


import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import slime.controller.SongTagAnimator;
import slime.controller.SongTimeController;
import slime.managers.MusicLibraryManager;
import slime.media.PlayState;
import slime.media.Song;
import slime.media.SongTag;
import slime.observe.GuiObserver;
import slime.observe.GuiSubject;
import slime.utills.ShrinkImageToSize;

/**
 * 
 * <b>
 * The main player GUI class.
 * </b>
 * <p>
 * As this maintains all the user interface functionality, it contains an inner {@link MouseListener}
 * class for handling GUI button presses as well as implements the {@link GuiSubject} interface as it
 * is a Subject class for Observers.  
 * <p>
 * Sets up the main GUI: Icon file directories, JPanel creation and configuration, Instantiates the
 * {@link MusicLibraryManager} Object and registers it as an {@link GuiObserver} as well as its parent.
 * 
 * @author dMacGit
 * 
 * @see MouseListener
 * @see MusicLibraryManager
 * @see GuiSubject
 * @see GuiObserver
 * 
 */

public class PlayerGUI extends JPanel implements GuiSubject
{
    
	private final static String NAME = "[GUI] ";
	private MusicLibraryManager musicLibraryManager;
	
	private static final long serialVersionUID = -4125262661558412319L;
	
    private PlaylistGUI playListWindow;
    
    private JLabel defaultStringLabel,playPause,skip,menu,playList,exit,shuffle,repeat;
    private final short H_Size = 20, SONG_NAME_W = 225, DEFAULT_STRING_LABEL_W = 47, DEFAULT_STRING_LABEL_H = 22;
    private JPanel panelBar;
    
    private boolean notStarted = true;
    
    private final String defaultString = "Playing: ", FILE_DIR = "Data_Files";
    
    private final String defaultUserMusicDirectory = "%USERPROFILE%\\My Documents\\My Music";
    
    private List<GuiObserver> guiObserverList = new ArrayList<GuiObserver>();

    
    private PlayState currentStateOfPlayer = PlayState.STOPPED;
    private boolean observersStopped, observersShutdown = false;
    
    private SongTag currentSongTag = null;
    private final long TimeStarted;
    private long timeOfLastAction;
    private SongTagAnimator scrollingLabel;
    private SongTimeController songTimeUpdater;
    
    private JLabel scrollingTitleLabel;
    
    //This is the dir path to the images folder		---> Change if necessary!
    
    public static String THE_FOLDER_DIR = "images/";
    
    private mouseListener listenerOne;
    
    private ImageIcon PLAY_ICON = ShrinkImageToSize.shrinkImageToSize(new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource(THE_FOLDER_DIR+"playButtonGlossy.png"))),H_Size,H_Size),
            PAUSE_ICON = ShrinkImageToSize.shrinkImageToSize(new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource(THE_FOLDER_DIR+"pauseButtonGlossy.png"))),H_Size,H_Size),
            SKIP_ICON = ShrinkImageToSize.shrinkImageToSize(new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource(THE_FOLDER_DIR+"skipButtonGlossy.png"))),H_Size,H_Size),
            MENU_ICON = ShrinkImageToSize.shrinkImageToSize(new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource(THE_FOLDER_DIR+"menuButtonGlossy.png"))),H_Size,H_Size),
            LIST_ICON = ShrinkImageToSize.shrinkImageToSize(new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource(THE_FOLDER_DIR+"playListButtonGlossy.png"))),H_Size,H_Size),
            EXIT_ICON = ShrinkImageToSize.shrinkImageToSize(new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource(THE_FOLDER_DIR+"smlCloseIcon.png"))),H_Size,H_Size),
            REPEAT_ICON_DE_SELECT = ShrinkImageToSize.shrinkImageToSize(new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource(THE_FOLDER_DIR+"repeatButton.png"))),H_Size,H_Size),
            SHUFFLE_ICON_DE_SELECT = ShrinkImageToSize.shrinkImageToSize(new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource(THE_FOLDER_DIR+"shuffleButton.png"))),H_Size,H_Size),
            REPEAT_ICON_SELECTED = ShrinkImageToSize.shrinkImageToSize(new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource(THE_FOLDER_DIR+"repeatButtonSelected.png"))),H_Size,H_Size),
            SHUFFLE_ICON_SELECTED = ShrinkImageToSize.shrinkImageToSize(new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource(THE_FOLDER_DIR+"shuffleButtonSelected.png"))),H_Size,H_Size);
    
    private boolean shuffle_Select = false, repeat_Select = false;

    public PlayerGUI()
    {   
    	TimeStarted = System.currentTimeMillis();
    	
        this.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
        
        listenerOne = new mouseListener();
        playPause = new JLabel(PLAY_ICON);
        playPause.setName("playPause");
        skip = new JLabel(SKIP_ICON);
        skip.setName("skip");
        exit = new JLabel(EXIT_ICON);
        shuffle = new JLabel(SHUFFLE_ICON_DE_SELECT);
        shuffle.setName("shuffle");
        repeat = new JLabel(REPEAT_ICON_DE_SELECT);
        repeat.setName("repeat");
        playList = new JLabel(LIST_ICON);
        playList.setName("playList");
        menu = new JLabel(MENU_ICON);
        defaultStringLabel = new JLabel(defaultString);
        
        defaultStringLabel.setForeground(Color.WHITE);
        defaultStringLabel.setPreferredSize(new Dimension(DEFAULT_STRING_LABEL_W,DEFAULT_STRING_LABEL_H));
        
        final String StartingMessage = "Ready to go! Click Play...";
        scrollingLabel = new SongTagAnimator(StartingMessage, 90);
        
        songTimeUpdater = new SongTimeController(0);
        
        scrollingLabel.setPreferredSize(new Dimension(SONG_NAME_W,DEFAULT_STRING_LABEL_H));
        scrollingLabel.setMinimumSize(new Dimension(SONG_NAME_W,DEFAULT_STRING_LABEL_H));
        scrollingLabel.setMaximumSize(new Dimension(SONG_NAME_W,DEFAULT_STRING_LABEL_H));
        
        scrollingLabel.setForeground(Color.WHITE);
        scrollingLabel.setBackground(Color.BLACK);
        
        panelBar = new JPanel();
        panelBar.setBackground(Color.BLACK); 
        
        panelBar.add(playPause);
        panelBar.add(skip);
        panelBar.add(defaultStringLabel);
        panelBar.add(scrollingLabel);
        panelBar.add(songTimeUpdater);
        panelBar.add(shuffle);
        panelBar.add(repeat);
        panelBar.add(playList);
        panelBar.add(menu);
        panelBar.add(exit);
        
        this.setBackground(Color.red);
        this.add(panelBar);
        
        playPause.addMouseListener(listenerOne);
        skip.addMouseListener(listenerOne);
        playList.addMouseListener(listenerOne);
        menu.addMouseListener(listenerOne);
        exit.addMouseListener(listenerOne);
        shuffle.addMouseListener(listenerOne);
        repeat.addMouseListener(listenerOne);

        musicLibraryManager = new MusicLibraryManager(FILE_DIR);
        System.out.println(NAME+MusicLibraryManager.class.getName()+"  created!");
        
        registerGuiObserver(musicLibraryManager);
        musicLibraryManager.setParentSubject(this);
        
    }
    
    /*
     * These listeners handle the buttons that make up the playerGUI.
     * 
     * Shuffle, Repeat, Skip, Pause/Play, Exit.
     */
    private class mouseListener implements MouseListener
    {
        public void mouseClicked(MouseEvent e){}
        public void mousePressed(MouseEvent e)
        {
            Object source = e.getSource();
            System.out.println(NAME+" {MouseListener} Source is "+((JLabel)source).getName());
            if(source == shuffle)
            {
                if(shuffle_Select)
                {
                    shuffle.setIcon(SHUFFLE_ICON_DE_SELECT);
                    shuffle_Select = false;
                    notifyAllObservers(PlayState.SHUFFLE_TOGGLED);
                    revalidate();
                }
                else
                {
                    shuffle.setIcon(SHUFFLE_ICON_SELECTED);
                    shuffle_Select = true;
                    notifyAllObservers(PlayState.SHUFFLE_TOGGLED);
                    revalidate();
                }
                
            }
            if(source == repeat)
            {
                if(repeat_Select)
                {
                    repeat.setIcon(REPEAT_ICON_DE_SELECT);
                    repeat_Select = false;
                    notifyAllObservers(PlayState.REPEAT_TOGGLED);
                    revalidate();
                }
                else
                {
                    repeat.setIcon(REPEAT_ICON_SELECTED);
                    repeat_Select = true;
                    notifyAllObservers(PlayState.REPEAT_TOGGLED);
                    revalidate();
                }
                
            }
            
            if (notStarted)
            {
                if (source == playPause) 
                {
                	currentStateOfPlayer = PlayState.PLAYING;
                    notStarted = false;
                    playPause.setIcon(PAUSE_ICON);
                    revalidate();
                    notifyAllObservers(currentStateOfPlayer);                    
                    System.out.println(NAME+"Starting up Player!!");                   
                }
            }
            else
            {
            	System.out.println(NAME+" User Skipped/Paused/Played: Current State: "+currentStateOfPlayer);
                if (source == playPause && currentStateOfPlayer == PlayState.PAUSED)
                {
                	currentStateOfPlayer = PlayState.PLAYING;
                    playPause.setIcon(PAUSE_ICON);
                    songTimeUpdater.startTimer();
                    revalidate();
                    notifyAllObservers(currentStateOfPlayer);
                    System.out.println(NAME+"User pressed Play!!");
                }
                else if (source == playPause && currentStateOfPlayer == PlayState.PLAYING)
                {
                	currentStateOfPlayer = PlayState.PAUSED;
                    playPause.setIcon(PLAY_ICON);
                    songTimeUpdater.stopTimer();
                    revalidate();
                    notifyAllObservers(currentStateOfPlayer);
                    System.out.println(NAME+"User pressed Pause!!");
                }
                if (source == skip) {
                	currentStateOfPlayer = PlayState.SKIPPED_FORWARDS;
                	notifyAllObservers(currentStateOfPlayer);
                	System.out.println(NAME+"User pressed skip!!");
                	currentStateOfPlayer = PlayState.PLAYING;
                }
                System.out.println(NAME+" New State: "+currentStateOfPlayer);
            }
            if(source == playList)
            {
                if(playListWindow == null)
                {
                    playListWindow = new PlaylistGUI(musicLibraryManager);
                    playListWindow.setVisible(true);
                }
                else
                {
                    if(playListWindow.isOpen())
                    {
                        playListWindow.open();
                    }
                }
            }
            if(source == exit)
            {
            	currentStateOfPlayer = PlayState.SHUTDOWN;
            	notifyAllObservers(currentStateOfPlayer);
            	
            }
            if(currentStateOfPlayer == PlayState.STOPPED && observersStopped)
            {
            	deregisterGuiObserver(musicLibraryManager);
            }
            
        }
        public void mouseReleased(MouseEvent e){}
        public void mouseEntered(MouseEvent e){}
        public void mouseExited(MouseEvent e){}

    }
    
    private void shutdownPlayer()
    {
    	System.out.println(NAME+"Deregistering MusicLibraryManager...");
		deregisterGuiObserver(musicLibraryManager);
		System.out.println(NAME+"Deregistered MusicLibraryManager...");
		this.setVisible( false );
        System.exit(0);
        System.gc();
    }

	@Override
	public void registerGuiObserver(GuiObserver guiObserver)
	{
		guiObserverList.add(guiObserver);
		System.out.println(NAME+"<<<< "+guiObserver.getGuiObserverName()+" Added! >>>>");
		
	}

	@Override
	public void deregisterGuiObserver(GuiObserver guiObserver) {
		guiObserverList.remove(guiObserver);
		System.out.println(NAME+"<<<< "+guiObserver.getGuiObserverName()+" Removed! >>>>");
		
	}

	@Override
	public void notifyAllObservers(PlayState newState) {
		for(GuiObserver observer : guiObserverList){
			observer.updateGuiObserver(newState);
		}
		
	}

	/*
	 * Callback method used by observers for sending updated values
	 * and objects required for animation on the GUI display.
	 * 
	 */
	
	@Override
	public void guiCallback(PlayState state, Song song) 
	{
		if(state == PlayState.STOPPED){
			observersStopped = true;
		}
		else if(state == PlayState.SHUTDOWN){
			observersShutdown = true;
			shutdownPlayer();
		}
		else if(state == PlayState.READY)
		{
			System.out.println(NAME+"Recieved READY Callback!");
			final String temp = craetedPrintedSongString(song.getMetaTag());
			System.out.println(NAME+"New song: "+temp);
			scrollingLabel.updateText(craetedPrintedSongString(song.getMetaTag()),temp.length());
			songTimeUpdater.newDuration(song.getMetaTag().getDurration());
			songTimeUpdater.startTimer();
		}
	}
	
	/* 
	 * This method simply takes the SongTag info as argument, and proceeds to format that
	 * in a more readable way in preparation for animation in the UI display.
	 */
	private String craetedPrintedSongString(SongTag song)
	{
		String Album = song.getRecordingTitle();
		String Title = song.getSongTitle();
		String Artist = song.getArtist();
		int Year = song.getYear();
		
		String preparedYear = "";
		
		//Checks if particular fields are not empty.
        if(Album.compareTo("") == 0)
        {
            Album = "";
        }
        
        if(Title.compareTo("") == 0)
        {
        	Title = "Unknown Song";
        }
        
        if(Artist.compareTo("") == 0)
        {
        	Artist = "";
        }
        
        if(Year <= 1890)
        {
        	preparedYear = "";
        }
        else
        	preparedYear = Integer.toString(Year);
        
        return new String(Artist+"   :   "+Title+"     "+Album+"     "+preparedYear);
	    
	}
	
	
	
	
	
}

