package slime.core;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import slime.controller.AnimationController;
import slime.controller.MediaController;
import slime.managers.MusicLibraryManager;
import slime.media.PlayState;
import slime.media.SongTag;
import slime.observe.AnimatorObserver;
import slime.observe.AnimatorSubject;
import slime.observe.GuiObserver;
import slime.observe.GuiSubject;
import slime.observe.StateObserver;
import slime.observe.StateSubject;
import slime.utills.ActionTimer;
import slime.utills.ComponentMover;
import slime.utills.ImageLoader;
import slime.utills.ShrinkImageToSize;

/*
 * Main player class.
 * 
 * Sets up main GUI, Icon file directories, as well as user Media location.
 * Handles all button events and manages all Observers to the media player and 
 * animation events.
 */
public class PlayerGUI extends JPanel implements ActionListener, GuiSubject
{
    /**
	 * 
	 */
	
	private MusicLibraryManager musicLibraryManager;
	
	private static final long serialVersionUID = -4125262661558412319L;
	private static TrayIcon trayIcon;
    private PlaylistGUI playListWindow;
    private static PlayerGUI gui;
    private JLabel defaultStringLabel,songName,songTime,playPause,skip,menu,playList,exit,shuffle,repeat;
    private final short H_Size = 20, SONG_TIME_W = 38, SONG_NAME_W = 225, DEFAULT_STRING_LABEL_W = 47, DEFAULT_STRING_LABEL_H = 22;
    private JPanel panelBar;
    //public MusicLibraryManager musicLibraryManager;
    private boolean notStarted = true;
    private final String defaultString = "Playing: ", FILE_DIR = "Data_Files";
    private final String defaultUserMusicDirectory = "%USERPROFILE%\\My Documents\\My Music";
    private List<GuiObserver> guiObserverList = new ArrayList<GuiObserver>();
    //private List<AnimatorObserver> animatorObserverList = new ArrayList<AnimatorObserver>();
    
    private PlayState currentStateOfPlayer = PlayState.STOPPED;
    private boolean observersStopped, observersShutdown = false;
    
    private SongTag currentSongTag = null;
    private final long TimeStarted;
    private long timeOfLastAction;
    //private AnimationController animator;
    
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
    private Timer infoUpdater_Scroller;

    public PlayerGUI()
    {   
    	TimeStarted = System.currentTimeMillis();
    	//readADirectory = new PlaySongsFromFolder(FILE_DIR);
        this.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
        playPause = new JLabel(PLAY_ICON);
        skip = new JLabel(SKIP_ICON);
        exit = new JLabel(EXIT_ICON);
        shuffle = new JLabel(SHUFFLE_ICON_DE_SELECT);
        repeat = new JLabel(REPEAT_ICON_DE_SELECT);
        listenerOne = new mouseListener();
        playList = new JLabel(LIST_ICON);
        menu = new JLabel(MENU_ICON);
        defaultStringLabel = new JLabel(defaultString);
        defaultStringLabel.setForeground(Color.WHITE);
        defaultStringLabel.setPreferredSize(new Dimension(DEFAULT_STRING_LABEL_W,DEFAULT_STRING_LABEL_H));
        songName = new JLabel("");
        songTime = new JLabel("00:00");
        songTime.setPreferredSize(new Dimension(SONG_TIME_W,DEFAULT_STRING_LABEL_H));
        songTime.setForeground(Color.WHITE);
        songName.setPreferredSize(new Dimension(SONG_NAME_W,DEFAULT_STRING_LABEL_H));
        songName.setMinimumSize(new Dimension(SONG_NAME_W,DEFAULT_STRING_LABEL_H));
        songName.setMaximumSize(new Dimension(SONG_NAME_W,DEFAULT_STRING_LABEL_H));
        songName.setForeground(Color.WHITE);        
        panelBar = new JPanel();
        panelBar.setBackground(Color.BLACK);       
        panelBar.add(playPause);
        panelBar.add(skip);
        panelBar.add(defaultStringLabel);
        panelBar.add(songName);
        panelBar.add(songTime);
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

        //animator = new AnimationController();
        musicLibraryManager = new MusicLibraryManager(FILE_DIR);
        
        registerGuiObserver(musicLibraryManager);
        musicLibraryManager.setParentSubject(this);
        //MediaController
        //LibraryManager
        //PlaylistManager
        
        //registerAnimatorObserver(animator);
        
        infoUpdater_Scroller = new Timer(50,this);
        infoUpdater_Scroller.start();
        
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
            if(source == shuffle)
            {
                if(shuffle_Select)
                {
                	currentStateOfPlayer = PlayState.SHUFFLE_TOGGLED;
                    shuffle.setIcon(SHUFFLE_ICON_DE_SELECT);
                    shuffle_Select = false;
                }
                else
                {
                	currentStateOfPlayer = PlayState.SHUFFLE_TOGGLED;
                    shuffle.setIcon(SHUFFLE_ICON_SELECTED);
                    shuffle_Select = true;
                }
                notifyAllObservers();
                revalidate();
            }
            if(source == repeat)
            {
                if(repeat_Select)
                {
                    repeat.setIcon(REPEAT_ICON_DE_SELECT);
                    currentStateOfPlayer = PlayState.REPEAT_TOGGLED;
                    repeat_Select = false;
                }
                else
                {
                    repeat.setIcon(REPEAT_ICON_SELECTED);
                    currentStateOfPlayer = PlayState.REPEAT_TOGGLED;
                    repeat_Select = true;
                }
                notifyAllObservers();
                revalidate();
            }
            if (notStarted)
            {
            	
                if (source == playPause) {
                		
                	currentStateOfPlayer = PlayState.PLAYING;
                    notStarted = false;
                    playPause.setIcon(PAUSE_ICON);
                    revalidate();
                    notifyAllObservers();
                    //musicLibraryManager = new MusicLibraryManager(FILE_DIR);
                    
                    //registerMediaObserver(musicLibraryManager);
                    
                    System.out.println("Starting up Player!!");
                    /*animator.setJLabelBounds(songName.getX(), songName.getWidth());
                    currentSongTag = musicLibraryManager.getTheCurrentSong();
                    animator.startTimer();*/
                    
                }
                
            }
            else
            {
                /*if (animator.getLabel() != null) {
                    songName.setIcon(animator.getLabel().getIcon());
                    songTime.setText(animator.getSongTime());
                    revalidate();
                }*/
                if (source == playPause && currentStateOfPlayer == PlayState.PAUSED) {
                	currentStateOfPlayer = PlayState.PLAYING;
                	//currentSongTag = musicLibraryManager.getTheCurrentSong();
                    //musicLibraryManager.playTheSong();
                    playPause.setIcon(PAUSE_ICON);
                    revalidate();
                    notifyAllObservers();
                    System.out.println("User pressed Play!!");
                }
                else if (source == playPause && currentStateOfPlayer == PlayState.PLAYING) {
                	currentStateOfPlayer = PlayState.PAUSED;
                	//currentSongTag = musicLibraryManager.getTheCurrentSong();
                    playPause.setIcon(PLAY_ICON);
                    revalidate();
                    notifyAllObservers();
                    System.out.println("User pressed Pause!!");
                    //musicLibraryManager.pauseTheSong();
                }
                if (source == skip) {
                	currentStateOfPlayer = PlayState.SKIPPED_FORWARDS;
                	notifyAllObservers();
                	System.out.println("User pressed skip!!");
                	currentStateOfPlayer = PlayState.PLAYING;
                    //musicLibraryManager.skipTheSong();
                    //currentSongTag = musicLibraryManager.getTheCurrentSong();
                    
                }
            }
            if(source == playList)
            {
                if(playListWindow == null)
                {
                    //playListWindow = new PlaylistGUI(musicLibraryManager);
                    //playListWindow.setVisible(true);
                }
                else
                {
                    if(playListWindow.isOpen())
                    {
                        System.out.println("Playlist is invisible!");
                        playListWindow.open();
                        System.out.println("Playlist is set to visible!");
                    }
                }
            }
            if(source == exit)
            {
            	currentStateOfPlayer = PlayState.SHUTDOWN;
            	notifyAllObservers();
            	
            }
            if(currentStateOfPlayer == PlayState.STOPPED && observersStopped)
            {
            	deregisterGuiObserver(musicLibraryManager);
                
            }
            //notifyAllAnimatorObservers();
            //notifyAllMediaObservers();
            //currentSongTag = readADirectory.getTheCurrentSong();
            
        }
        public void mouseReleased(MouseEvent e){}
        public void mouseEntered(MouseEvent e){}
        public void mouseExited(MouseEvent e){}

    }

    public void actionPerformed(ActionEvent e)
    {
    	
    	if(observersStopped || observersShutdown)
    	{
    		deregisterGuiObserver(musicLibraryManager);
    		this.setVisible( false );
            System.exit(0);
            System.gc();
    	}
        if(!notStarted)
        {
            /*if(animator.getLabel() != null)
            {
                songName.setIcon(animator.getLabel().getIcon());
                songTime.setText(animator.getSongTime());
                revalidate();
            }*/
        }
    }

	@Override
	public void registerGuiObserver(GuiObserver guiObserver)
	{
		guiObserverList.add(guiObserver);
		System.out.println("<<<< "+guiObserver.getGuiObserverName()+" Added! >>>>");
		
	}

	@Override
	public void deregisterGuiObserver(GuiObserver guiObserver) {
		guiObserverList.remove(guiObserver);
		System.out.println("<<<< "+guiObserver.getGuiObserverName()+" Removed! >>>>");
		
	}

	@Override
	public void notifyAllObservers() {
		for(GuiObserver observer : guiObserverList){
			observer.updateGuiObserver(currentStateOfPlayer);
		}
		
	}

	@Override
	public void guiCallback(PlayState state, SongTag tagInfo) 
	{
		if(state == PlayState.STOPPED){
			observersStopped = true;
		}
		if(state == PlayState.SHUTDOWN){
			observersShutdown = true;
		}
	}

}
