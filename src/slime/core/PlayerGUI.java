package slime.core;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.UIManager;

import slime.controller.SongTagAnimator;
import slime.controller.SongTimeController;
import slime.io.ImageLoader;
import slime.io.LibraryHelper;
import slime.managers.MusicLibraryManager;
import slime.media.PlayState;
import slime.observe.GuiObserver;
import slime.observe.GuiSubject;
import slime.song.Song;
import slime.song.SongTag;
import slime.utills.ComponentMover;
import slime.utills.LibraryCreater;
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
    private static PlayerGUI gui;
    private static InnerJMenuBar menuBar;
    private static TrayIcon trayIcon;
    private static JFrame frame;
    
	private final static String NAME = "[GUI] ";
	private MusicLibraryManager musicLibraryManager;
	
	private JPanel mainWindowPanel;
	
	private static final long serialVersionUID = -4125262661558412319L;
	
    private PlaylistGUI playListWindow;
    
    private JLabel defaultStringLabel,playPause,skip,menu,playList,exit,shuffle,repeat;
    private final short H_Size = 20, SONG_NAME_W = 225, DEFAULT_STRING_LABEL_W = 47, DEFAULT_STRING_LABEL_H = 22;
    private JPanel panelBar;
    
    private boolean notStarted = true;
    
    private static String defaultString = "Playing: ", DATA_DIR, ROOT;
    
    private static String defaultUserMusicDirectory = "%USERPROFILE%\\My Documents\\My Music";
    
    private List<GuiObserver> guiObserverList = new ArrayList<GuiObserver>();

    
    private PlayState currentStateOfPlayer = PlayState.STOPPED;
    private boolean observersStopped, observersShutdown = false;
    
    private SongTag currentSongTag = null;
    private final long TimeStarted;
    private long timeOfLastAction;
    private SongTagAnimator scrollingLabel;
    private SongTimeController songTimeUpdater;
    
    private final static String PROPERTIES_FILE = "slimeplayer.properties";
    private final static String DEFAULT_ROOT = "Slimeplayer";
	private final static String DEFAULT_MUSIC_HOME = "%USERPROFILE%\\My Documents\\My Music";
	private final static String DEFAULT_DATA_DIR_NAME = "Data_Files";
	private final static String DEFAULT_LIBRARY_FILE_NAME = "Library.txt";
	
	private static String Music_Home, Root, Data_Dir, Library_File;
	
	private HashMap<JPanel, Boolean> mapOfPanels = new HashMap<JPanel, Boolean>();  
    
    private JLabel scrollingTitleLabel;
    
    private int MAX_PLAYLIST_HEIGHT;
    
    private static LibraryCreater libCreater;
    
    private boolean isLibCreaterOpen = false;
    
    //This is the dir path to the images folder		---> Change if necessary!
    
    public static String THE_FOLDER_DIR = "images/";
    
    private mouseListener listenerOne;
    private URL PLAY_ICON_URL = ClassLoader.getSystemResource(THE_FOLDER_DIR+"playButtonGlossy.png"),
    		PAUSE_ICON_URL = ClassLoader.getSystemResource(THE_FOLDER_DIR+"pauseButtonGlossy.png"),
    		SKIP_ICON_URL = ClassLoader.getSystemResource(THE_FOLDER_DIR+"skipButtonGlossy.png"),
    		MENU_ICON_URL = ClassLoader.getSystemResource(THE_FOLDER_DIR+"menuButtonGlossy.png"),
    		LIST_ICON_URL = ClassLoader.getSystemResource(THE_FOLDER_DIR+"playListButtonGlossy.png"),
    		EXIT_ICON_URL = ClassLoader.getSystemResource(THE_FOLDER_DIR+"smlCloseIcon.png"),
    		SELECT_REPEAT_ICON_URL = ClassLoader.getSystemResource(THE_FOLDER_DIR+"repeatButton.png"),
    		SELECT_SHUFFLE_ICON_URL = ClassLoader.getSystemResource(THE_FOLDER_DIR+"shuffleButton.png"),
    		SELECT_REPEAT_DE_SELECT_ICON_URL = ClassLoader.getSystemResource(THE_FOLDER_DIR+"repeatButtonSelected.png"),
    		SELECT_SHUFFLE_DE_SELECT_ICON_URL = ClassLoader.getSystemResource(THE_FOLDER_DIR+"shuffleButtonSelected.png");
    
    
    private ImageIcon PLAY_ICON = ShrinkImageToSize.shrinkImageToSize(new ImageIcon(Toolkit.getDefaultToolkit().getImage(PLAY_ICON_URL)),H_Size,H_Size),
            PAUSE_ICON = ShrinkImageToSize.shrinkImageToSize(new ImageIcon(Toolkit.getDefaultToolkit().getImage(PAUSE_ICON_URL)),H_Size,H_Size),
            SKIP_ICON = ShrinkImageToSize.shrinkImageToSize(new ImageIcon(Toolkit.getDefaultToolkit().getImage(SKIP_ICON_URL)),H_Size,H_Size),
            MENU_ICON = ShrinkImageToSize.shrinkImageToSize(new ImageIcon(Toolkit.getDefaultToolkit().getImage(MENU_ICON_URL)),H_Size,H_Size),
            LIST_ICON = ShrinkImageToSize.shrinkImageToSize(new ImageIcon(Toolkit.getDefaultToolkit().getImage(LIST_ICON_URL)),H_Size,H_Size),
            EXIT_ICON = ShrinkImageToSize.shrinkImageToSize(new ImageIcon(Toolkit.getDefaultToolkit().getImage(EXIT_ICON_URL)),H_Size,H_Size),
            REPEAT_ICON_DE_SELECT = ShrinkImageToSize.shrinkImageToSize(new ImageIcon(Toolkit.getDefaultToolkit().getImage(SELECT_REPEAT_ICON_URL)),H_Size,H_Size),
            SHUFFLE_ICON_DE_SELECT = ShrinkImageToSize.shrinkImageToSize(new ImageIcon(Toolkit.getDefaultToolkit().getImage(SELECT_SHUFFLE_ICON_URL)),H_Size,H_Size),
            REPEAT_ICON_SELECTED = ShrinkImageToSize.shrinkImageToSize(new ImageIcon(Toolkit.getDefaultToolkit().getImage(SELECT_REPEAT_DE_SELECT_ICON_URL)),H_Size,H_Size),
            SHUFFLE_ICON_SELECTED = ShrinkImageToSize.shrinkImageToSize(new ImageIcon(Toolkit.getDefaultToolkit().getImage(SELECT_SHUFFLE_DE_SELECT_ICON_URL)),H_Size,H_Size);
    
    private boolean shuffle_Select = false, repeat_Select = false;
	private boolean isNewLibrary = false, emptyLibrary = false;

    public PlayerGUI()
    {   
    	super();
    	/*
    	 * Setting the Background & Foreground of the menus
    	 */
    	//UIManager.put("Frame.background", Color.BLACK);
    	UIManager.put("MenuBar.background", Color.BLACK);
    	UIManager.put("MenuBar.foreground", Color.WHITE);
    	UIManager.put("MenuBar.opaque", true);
    	UIManager.put("Menu.background", Color.BLACK);
    	UIManager.put("Menu.foreground", Color.WHITE);
    	UIManager.put("Menu.opaque", true);
    	UIManager.put("MenuItem.background", Color.BLACK);
    	UIManager.put("MenuItem.foreground", Color.WHITE);
    	UIManager.put("MenuItem.opaque", true);
    	
    	this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
    	
    	try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Error setting native LAF: " + e);
        }
    	
    	mainWindowPanel = new JPanel();
    	mainWindowPanel.setSize(this.getWidth(), 500);
    	//mainWindowPanel.setBackground(Color.WHITE);
    	mainWindowPanel.setVisible(true);
    	
    	
    	
		//this.add(mainWindowPanel);
    	
    	mapOfPanels.put(mainWindowPanel, false);
    	
    	/*
    	 * Make sure that the properties file is created.
    	 * Then proceed to load the properties into the player 
    	 */
    	check_PropertiesFile();
    	
    	/*
    	 * Check that there is are the required files and directories
    	 * before loading the library into the player
    	 */
    	if(check_Created_LibraryDirectory())
    	{
    		
    			
    	}
    	
    	if(is_Fresh_LibraryDirectory())
		{
				isNewLibrary = true;
				
		}
		emptyLibrary = true;
    	
		libCreater = new LibraryCreater();
		mainWindowPanel.add(libCreater);
    	
    	if(isNewLibrary || emptyLibrary)
    	{
	    	musicLibraryManager = new MusicLibraryManager(emptyLibrary);
	        System.out.println(NAME+MusicLibraryManager.class.getName()+"  created!");
	        
	        registerGuiObserver(musicLibraryManager);
	        musicLibraryManager.setParentSubject(this);
	    	
	    	try 
	        {	
	    		if(musicLibraryManager.getMapOfSong()!=null)
	    		{
					playListWindow = new PlaylistGUI(musicLibraryManager.getMapOfSong());
					MAX_PLAYLIST_HEIGHT = playListWindow.getHeight();
					//frame.add(playListWindow);
					playListWindow.setVisible(false);
					//mapOfPanels.put(playListWindow, false);
	    		}
				
			} 
	        catch (Exception e1)
	        {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	}
    	
    	
    	
    	TimeStarted = System.currentTimeMillis();
    	
    	
        //this.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
        
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
        menu.setName("menu");
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
        
        //this.setBackground(Color.red);
        add(panelBar);
        
        
        
        mapOfPanels.put(panelBar, true);
        
        playPause.addMouseListener(listenerOne);
        skip.addMouseListener(listenerOne);
        playList.addMouseListener(listenerOne);
        menu.addMouseListener(listenerOne);
        exit.addMouseListener(listenerOne);
        shuffle.addMouseListener(listenerOne);
        repeat.addMouseListener(listenerOne);
 
    }
    
    public JPanel getCreaterPanel(){
    	return libCreater;
    }
    
    public void closeCreaterPanel()
    {   	
    	remove(mainWindowPanel);
    	isLibCreaterOpen = false;
    	frame.pack();
    	if(libCreater.isLibraryUpdated())
		{
			//Call the update method!
    		musicLibraryManager.forceLibraryPlaylistUpdate();
    		
    		try 
	        {	
	    		if(musicLibraryManager.getMapOfSong()!=null)
	    		{
					playListWindow = new PlaylistGUI(musicLibraryManager.getMapOfSong());
					MAX_PLAYLIST_HEIGHT = playListWindow.getHeight();
					//frame.add(playListWindow);
					playListWindow.setVisible(false);
					//mapOfPanels.put(playListWindow, false);
	    		}
				
			} 
	        catch (Exception e1)
	        {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
    }
            
    public void openCreaterPanel()
    {          	
		isLibCreaterOpen = true;
		remove(panelBar);
		add(mainWindowPanel);
		add(panelBar);
		frame.pack();
		
		
		
    }
    
    public boolean isCreaterPanelOpen()
    {
    	return isLibCreaterOpen;
    }
    
    
    private boolean check_Created_LibraryDirectory()
    {
    	boolean fresh_Directory = false;

    	File dataDirFile = new File(Root+"\\"+Data_Dir);
        if(!dataDirFile.exists())
        {
        	System.out.println("DATA_DIR Doesn't Exist!");
        	
        	File tempLibFile = new File(dataDirFile.getPath()+"\\"+Library_File);
        	
        	try 
        	{
        		dataDirFile.mkdir();
        		System.out.println(dataDirFile.getPath()+"\n"+dataDirFile.getAbsolutePath());
        		System.out.println(tempLibFile.getPath()+"\n"+tempLibFile.getAbsolutePath());
				if(tempLibFile.createNewFile())
				{
					System.out.println("The Library File was created!");
				}
				fresh_Directory = true;
			}
        	catch (IOException e)
        	{
				e.printStackTrace();
				System.out.println("DATA_DIR Doesn't Exist!");
				return fresh_Directory;
			}
        }
        else
        {
        	System.out.println("DATA_DIR Does Exist!");
        }
    
    	return fresh_Directory;
    }
    
    private boolean is_Fresh_LibraryDirectory()
    {
    	boolean fresh_Directory = false;

    	//File dataDirFile = new File(Root+"\\"+Data_Dir);
    	File tempLibFile = new File(Root+"\\"+Data_Dir+"\\"+Library_File);
    	long sizeInBytes;
        if(tempLibFile.exists())
        {
        	System.out.println("Library_File Exist!");
        	
        	//FileReader fr = new FileReader(tempLibFile);
        	//BufferedReader br = new BufferedReader(fr);
        	if(tempLibFile.isFile())
        	{
        		if(tempLibFile.length() > 0)
        		{
        			System.out.println("Library_File contains data!");
        			
        		}
        		else
        		{
        			fresh_Directory = true;
        			System.out.println("Library_File contains no data! File is Fresh!");
        		}
        	}

        }
        else
        {
        	System.out.println("Library_File Doesn't Exist!");
        }
    
    	return fresh_Directory;
    }
    
    private boolean check_PropertiesFile()
    {
    	Properties config = new Properties();
    	String dirValue = null;
    	FileWriter fw;
		BufferedWriter bw;
		
		boolean created_Properties = false;
    	try
    	{
    		if( !(new File(System.getProperty("user.home")+"\\"+PROPERTIES_FILE).exists()) )
    		{
    			created_Properties = true;
    			//Then create a new properties file!
    			File newProperties = new File(System.getProperty("user.home"),PROPERTIES_FILE);
    			System.out.println("Creating the properties file @ "+System.getProperty("user.home"));
    			fw = new FileWriter(newProperties);
    			bw = new BufferedWriter(fw);
    			
    			Music_Home = DEFAULT_MUSIC_HOME;
    			Root = System.getProperty("user.home");
    			Data_Dir = DEFAULT_DATA_DIR_NAME;
    			Library_File = DEFAULT_LIBRARY_FILE_NAME;
    			
    			/*
				 * DIR:"%USERPROFILE%\\My Documents\\My Music"
				 * PLAYER_ROOT:"\\"
				 * PLAYER_DATA_DIR:"Data_Files"
				 * PATHS_FILE:"SongPaths.txt"
				 * LIBRARY_FILE:"Lib_MP3player.txt"
				 */
    			
    			bw.write("DIR:"+'"'+DEFAULT_MUSIC_HOME+'"');
    			bw.newLine();
    			bw.write("PLAYER_ROOT:"+'"'+"\\"+'"');
    			bw.newLine();
    			bw.write("PLAYER_DATA_DIR:"+'"'+DEFAULT_DATA_DIR_NAME+'"');
    			bw.newLine();
    			bw.write("LIBRARY_FILE:"+'"'+DEFAULT_LIBRARY_FILE_NAME+'"');
    			bw.flush();
    			bw.close();
    			fw.close();
    		}
    		else
    		{
    			//Then read from the file
    			System.out.println("Found the properties file @ "+System.getProperty("user.home"));
    			
    			try
            	{
            		FileInputStream fin = new FileInputStream(new File(System.getProperty("user.home")+"\\"+PROPERTIES_FILE));
        			config.load(fin);
        			
        			dirValue = config.getProperty("DIR");
        			System.out.println("Found the users Music Home dir location @ "+dirValue);
        			Music_Home = LibraryHelper.removeQuotes(config.getProperty("DIR"));
        			Root = System.getProperty("user.home");
        			Data_Dir = LibraryHelper.removeQuotes(config.getProperty("PLAYER_DATA_DIR"));
        			Library_File = LibraryHelper.removeQuotes(config.getProperty("LIBRARY_FILE"));
        			fin.close();
        		}
            	catch (FileNotFoundException e1) 
            	{
        			// TODO Auto-generated catch block
        			e1.printStackTrace();
        		}
            	catch (IOException e1) 
            	{
        			// TODO Auto-generated catch block
        			e1.printStackTrace();
        		}
    		}
    		
    		
    	}
    	catch(IOException io_ex)
    	{
    		io_ex.printStackTrace();
    	}
    	
    	return created_Properties;
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
            else if(source == repeat)
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
            
            if(source == menu)
            {
            	int offset = 0;
                if(menuBar.isVisible())
                {
                	/*if(mainWindowPanel.isVisible())
                	{
                		
                	}
                	//Disable the menubar and adjust the player
                	frame.setBounds(frame.getX(), frame.getY()+(menuBar.getHeight()), frame.getWidth(), frame.getHeight()-menu.getHeight());*/
                	
                	menuBar.setVisible(false);
                	if(mainWindowPanel.isVisible())
                	{
                		remove(mainWindowPanel);
                	}
                	frame.pack();
                	//updateAllPanels(menuBar.isVisible());
                	
                }
                else
                {
                	//Enable the menubar and adjust the player
                	//frame.setBounds(frame.getX(), frame.getY()-(menuBar.getHeight()), frame.getWidth(), frame.getHeight()+menu.getHeight());
                	menuBar.setVisible(true);
                	if(isCreaterPanelOpen()){
                		remove(panelBar);
                    	add(mainWindowPanel);
                    	add(panelBar);
                	}
                	//updateAllPanels(menuBar.isVisible());
                	frame.pack();
                }
            }
            else if(source == playList)
            {
            	//System.out.println("<<< The GUI Panel >>>\nSize: "+getWidth()+" X "+getHeight());
            	//System.out.println("<<< The playListWindow Panel >>>\nSize: "+playListWindow.getWidth()+" X "+playListWindow.getHeight());
                
            	if(playListWindow != null)
            	{
	            	if(playListWindow.isVisible())
	                {
	                	//frame.setLocation(frame.getX(), frame.getY()+playListWindow.getHeight());
	                	//frame.setSize(frame.getWidth(), frame.getHeight()-playListWindow.getHeight());
	            		
	                	playListWindow.setVisible(false);
	                	remove(playListWindow);
	                	//updateAllPanels(menuBar.isVisible());
	                	
	                	frame.revalidate();
	                }
	                else
	                {
	                	//frame.setLocation(frame.getX(), frame.getY()-playListWindow.getHeight());
	                	//frame.setSize(new Dimension(frame.getWidth(), frame.getHeight()+playListWindow.getHeight()));
	                	
	                	playListWindow.setVisible(true);   
	                	//updateAllPanels(menuBar.isVisible());
	                	add(playListWindow);
	                	frame.revalidate();
	                	
	                	//frame.revalidate();
	                }
            	}
            }
            if(source == exit)
            {
            	if(!guiObserverList.isEmpty())
            	{
            		currentStateOfPlayer = PlayState.SHUTDOWN;
            		notifyAllObservers(currentStateOfPlayer);
            	}
            	else
            	{
            		lightShutdown();
            	}
            	
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
    
    private void lightShutdown()
    {
    	this.setVisible( false );
        System.exit(0);
        System.gc();
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
		else if(state == PlayState.END_OF_PLAYLIST)
		{
			//Stop the Animator class
			currentStateOfPlayer = PlayState.END_OF_PLAYLIST;
			songTimeUpdater.stopTimer();
			songTimeUpdater.reset();
			String text = "... Reached End Of Playlist!";
			scrollingLabel.updateText(text,text.length());
			scrollingLabel.stop();
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

            frame = new JFrame("SlimePlayer");
            menuBar = new InnerJMenuBar(gui);
            frame.setJMenuBar(menuBar);
            //menuBar.setEnabled(false);
            //menuBar.setBorderPainted(false);
            menuBar.setVisible(false);
            //gui.setPreferredSize(new Dimension(500,32));
            if (SystemTray.isSupported())
            {
                SystemTray tray = SystemTray.getSystemTray();
                trayIcon = new TrayIcon(smallIcon.getImage(), "SlimePlayer",null);
                trayIcon.setImageAutoSize(true);
                try
                {
                    tray.add(trayIcon);
                }
                catch (AWTException e)
                {
                    System.err.println(NAME+"TrayIcon could not be added.");
                }
            }
            else
            {
                System.out.println(NAME+"System tray icon not supported!!");
            }
            frame.setIconImage(smallIcon.getImage());
            frame.setUndecorated( true );
            ComponentMover cm = new ComponentMover();
            cm.registerComponent(frame);
            frame.getContentPane().add(gui);
            frame.setSize(width, height);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            frame.pack();
            System.out.println(NAME+"Dimensions are: "+frame.getWidth()+" W "+frame.getHeight()+" H");
            
            //Now register all of the observers and subjects.
            
            
            
    	} 
    	else 
    	{
    		System.out.println(NAME+"There was an error finding the icon directory folder [ "+location+""+fileName+" ]");
    	}
    	
	}
}

/**
 * 
 * @author dMacGit
 * <b>
 * All though not being used or fully coded, I am leaving this
 * MenuBar class in the project.
 * </b>
 * <p>
 * This is mainly due to the possibility of being used in the future
 * for an alternative to navigating the player or even to provide 
 * more advanced settings and options to the player, that is normally
 * hidden in the normal view.
 * </p>
 *  
 *
 */

class InnerJMenuBar  extends JMenuBar
{
   
   private Color BACKGROUND_COLOR = Color.BLACK;
   private Color FOREGROUND_COLOR = Color.white;
   public PlayerGUI gui;

   public InnerJMenuBar(PlayerGUI gui)
   {
      super();
      this.gui = gui;
      super.setBackground(BACKGROUND_COLOR);
      Action playList = new AbstractAction("View Playlist")
      {
         public void actionPerformed(ActionEvent e)
         {
            //PlayList playList = new PlayList(playerGui);
         }
      };
      Action addFolder = new AbstractAction("Add Folder")
      {
         public void actionPerformed(ActionEvent e)
         {
        	 if(isPanelOpen())
        	 {
        		 closePanel();
        	 }
        	 else
        		 openPanel();
         }
      };
      JMenu fileDrop = new JMenu("Menu");
      fileDrop.add(playList);
      fileDrop.add(addFolder);
      add(fileDrop);
   }
   
   
   
   private void openPanel(){
	   gui.openCreaterPanel();
   }
   
   private void closePanel(){
	   gui.closeCreaterPanel();
   }
   
   private boolean isPanelOpen(){
	   return gui.isCreaterPanelOpen();
   }
}

