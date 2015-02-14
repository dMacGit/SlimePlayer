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
import slime.observe.MediaObserver;
import slime.observe.MediaSubject;
import slime.utills.ComponentMover;
import slime.utills.ImageLoader;
import slime.utills.ShrinkImageToSize;

//Main entry point into the player program!
//
// Set up the player gui as well as library checking
// as well as the player controls.

public class PlayerGUI extends JPanel implements ActionListener, MediaSubject, AnimatorSubject
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -4125262661558412319L;
	private static TrayIcon trayIcon;
    private PlaylistGUI playListWindow;
    private static PlayerGUI gui;
    private JLabel defaultStringLabel,songName,songTime,playPause,skip,menu,playList,exit,shuffle,repeat;
    private final short H_Size = 20, SONG_TIME_W = 38, SONG_NAME_W = 225, DEFAULT_STRING_LABEL_W = 47, DEFAULT_STRING_LABEL_H = 22;
    private JPanel panelBar;
    public MusicLibraryManager readADirectory;
    private boolean notStarted = true;
    private final String defaultString = "Playing: ", FILE_DIR = "Data_Files";
    private final String defaultUserMusicDirectory = "%USERPROFILE%\\My Documents\\My Music";
    private List<MediaObserver> mediaObserverList = new ArrayList<MediaObserver>();
    private List<AnimatorObserver> animatorObserverList = new ArrayList<AnimatorObserver>();
    private PlayState currentStateOfPlayer = PlayState.STOPPED;
    private SongTag currentSongTag = null;
    
    private AnimationController animator;
    
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

        animator = new AnimationController();
        
        //MediaController
        //LibraryManager
        //PlaylistManager
        
        registerAnimatorObserver(animator);
        
        infoUpdater_Scroller = new Timer(50,this);
        infoUpdater_Scroller.start();
        
        
        
    }
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
                revalidate();
            }
            if (notStarted)
            {
            		
                if (source == playPause) {
                	currentStateOfPlayer = PlayState.PLAYING;
                    notStarted = false;
                    playPause.setIcon(PAUSE_ICON);
                    revalidate();
                    readADirectory = new MusicLibraryManager(FILE_DIR);
                    registerMediaObserver(readADirectory);
                    System.out.println("Starting up Player!!");
                    animator.setJLabelBounds(songName.getX(), songName.getWidth());
                    currentSongTag = readADirectory.getTheCurrentSong();
                    animator.startTimer();
                }
            }
            else
            {
                if (animator.getLabel() != null) {
                    songName.setIcon(animator.getLabel().getIcon());
                    songTime.setText(animator.getSongTime());
                    revalidate();
                }
                if (source == playPause && readADirectory.getPausedState()) {
                	currentStateOfPlayer = PlayState.PLAYING;
                	currentSongTag = readADirectory.getTheCurrentSong();
                    readADirectory.playTheSong();
                    playPause.setIcon(PAUSE_ICON);
                    revalidate();
                } else if (source == playPause && !readADirectory.getPausedState()) {
                	currentStateOfPlayer = PlayState.PAUSED;
                	currentSongTag = readADirectory.getTheCurrentSong();
                    playPause.setIcon(PLAY_ICON);
                    revalidate();
                    readADirectory.pauseTheSong();
                    

                }
                if (source == skip) {
                	
                	currentStateOfPlayer = PlayState.SKIPPED_FORWARDS;
                    readADirectory.skipTheSong();
                    currentSongTag = readADirectory.getTheCurrentSong();
                    
                }
            }
            if(source == playList)
            {
                if(playListWindow == null)
                {
                    playListWindow = new PlaylistGUI(readADirectory);
                    playListWindow.setVisible(true);
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
            	currentStateOfPlayer = PlayState.STOPPED;
            	notifyAllMediaObservers();
            	deregisterMediaObserver(readADirectory);
            	deregisterAnimatorObserver(animator);
                gui.setVisible( false );
                System.exit(0);
                System.gc();
            }
            notifyAllMediaObservers();
            //currentSongTag = readADirectory.getTheCurrentSong();
            notifyAllAnimatorObservers();
        }
        public void mouseReleased(MouseEvent e){}
        public void mouseEntered(MouseEvent e){}
        public void mouseExited(MouseEvent e){}

    }
    public static void main(String[] args)
    {
    	ImageIcon smallIcon = null;
    	String location = THE_FOLDER_DIR;
    	String fileName = "PlayerIcon.png";
    	
    	if (ImageLoader.imageValidator(location, fileName))
    	{
    		Image image = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource(THE_FOLDER_DIR+"PlayerIcon.png"));
            Image sourceImage = image;
            smallIcon = new ImageIcon(sourceImage.getScaledInstance(60,60,Image.SCALE_SMOOTH));
            
            Toolkit tools = Toolkit.getDefaultToolkit();
            Dimension dimension = tools.getScreenSize();
            int width = (int)dimension.getWidth() / 2;
            int height = (int)dimension.getHeight() / 2;
            gui = new PlayerGUI();
            //gui.setBackground(Color.BLACK);
            JFrame frame = new JFrame("My MP3 Player");
            //gui.setPreferredSize(new Dimension(500,32));
            if (SystemTray.isSupported())
            {
                SystemTray tray = SystemTray.getSystemTray();
                trayIcon = new TrayIcon(smallIcon.getImage(), "MyPlayer",null);
                trayIcon.setImageAutoSize(true);
                try
                {
                    tray.add(trayIcon);
                }
                catch (AWTException e)
                {
                    System.err.println("TrayIcon could not be added.");
                }
            }
            else
            {
                System.out.println("System tray icon not supported!!");
            }
            frame.setIconImage(smallIcon.getImage());
            frame.setUndecorated( true );
            ComponentMover cm = new ComponentMover();
            cm.registerComponent(frame);
            frame.getContentPane().add(gui);
            frame.setSize(width, height);
            frame.setLocation(width / 2, height / 2);
            frame.setVisible(true);
            frame.pack();
            System.out.println("Dimensions are: "+frame.getWidth()+" W "+frame.getHeight()+" H");
            
    	} else {
    		System.out.println("There was an error finding the icon directory folder [ "+location+""+fileName+" ]");
    	}
    	
       

        
    }

    public void actionPerformed(ActionEvent e)
    {
        if(!notStarted)
        {
            if(animator.getLabel() != null)
            {
                songName.setIcon(animator.getLabel().getIcon());
                songTime.setText(animator.getSongTime());
                revalidate();
            }
        }
    }

	@Override
	public void registerMediaObserver(MediaObserver observer) 
	{
		mediaObserverList.add(observer);
		System.out.println("<<<< "+observer.getMediaObserverName()+" Added! >>>>");
	}

	@Override
	public void deregisterMediaObserver(MediaObserver observer) 
	{
		if(!mediaObserverList.isEmpty())
		{
			mediaObserverList.remove(mediaObserverList.indexOf(observer));
			System.out.println("<<<< "+observer.getMediaObserverName()+" Removed! >>>>");
		}
		
	}

	@Override
	public void notifyAllMediaObservers()
	{
		System.out.println("<<<< State Has Changed >>>>");
		for(MediaObserver observer : mediaObserverList){
			System.out.println("-- "+observer.getMediaObserverName()+" Notified --");
			observer.updateMediaObserver(currentStateOfPlayer);
		}
		
	}

	@Override
	public void registerAnimatorObserver(AnimatorObserver observer) 
	{
		animatorObserverList.add(observer);
		System.out.println("<<<< "+observer.getAnimatorObserverName()+" Added! >>>>");
		
	}

	@Override
	public void deregisterAnimatorObserver(AnimatorObserver observer) 
	{
		if(!animatorObserverList.isEmpty())
		{
			animatorObserverList.remove(animatorObserverList.indexOf(observer));
			System.out.println("<<<< "+observer.getAnimatorObserverName()+" Removed! >>>>");
		}
		
	}

	@Override
	public void notifyAllAnimatorObservers()
	{
		for(AnimatorObserver observer : animatorObserverList){
			observer.updateAnimatorObserver(currentStateOfPlayer,currentSongTag);
		}
		
	}

}
