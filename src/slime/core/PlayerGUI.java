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
    
    private boolean mainPanel_In_Use = false;
    
    private JPanel lastPanelStillOpen = null;
    
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
	
	private JPanel currentOpenPanel = null;
	
	

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
    	if(!validate_LibraryDirectory())
    	{
    		emptyLibrary = true;
    			
    	}
    	
    	/*if(is_Fresh_LibraryDirectory())
		{
				isNewLibrary = true;
				
		}*/
		
    	
		libCreater = new LibraryCreater();
		libCreater.setName("libCreater");
		//mainWindowPanel.add(libCreater);
    	
    	if(!emptyLibrary)
    	{
	    	
    		initNewLibraryManager(emptyLibrary);
	    	try 
	        {	
	    		if(musicLibraryManager.getMapOfSong()!=null)
	    		{
					playListWindow = new PlaylistGUI(musicLibraryManager.getMapOfSong());
					playListWindow.setName("playList");
					MAX_PLAYLIST_HEIGHT = playListWindow.getHeight();
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
    
    /**
     * <b>
     * This makes sure that the libraryManager is created under the correct
     * conditions. It also adds the class to list of Observers.
     * </b>
     * <p>
     * It handles LibraryManager class creation when there is a new empty library.text file
     * created, and thus the class cannot load any data from the file and hence not build a
     * {@link LibraryPlayList } object.
     * And it handles the normal condition of a validated library.text file, that has data, and
     * thus can create a {@link LibraryPlayList } object.
     * </p>
     * <
     * 
     * @param isEmptyLib The boolean state of the library. True if is empty.
     * 
     */
    private void initNewLibraryManager(boolean isEmptyLib)
    {
    	musicLibraryManager = new MusicLibraryManager(isEmptyLib);
        System.out.println(NAME+MusicLibraryManager.class.getName()+"  created!");
        
        registerGuiObserver(musicLibraryManager);
        musicLibraryManager.setParentSubject(this);
    }
    
    /*
     * The next few methods need to be accessed by the JMenuBar Inner-class
     * and as such, cannot have private access modifiers. As a result I will be
     * giving them the default no-modifier access, restricting access to them to just
     * this class and this package.
     */
    
    /**
     * <B>
     * External call to close the Creator JPanel
     * </B>
     * <p>
     * This is needed so the {@link JMenuBar } inner-class {@link InnerJMenuBar}
     * can notify the panel when the menu-item for the Creator panel is pressed
     * to close it. 
     * </p>
     * <p>
     * It takes care of checking whether the user added a directory, if so then
     * calls to update the implemented {@link MusicLibraryManager } with its
     * {@link MusicLibraryManager#forceLibraryPlaylistUpdate() } method. Also initializing
     * new {@link MusicLibraryManager } if one doesn't exist, and finally creates a new
     * {@link PlaylistGUI } Panel, if none exist.
     * </p>
     * <p>
     * <b><i>Need to shrink this method, and remove Manager calls as it breaks my 
     * observer pattern and also encapsulation</i></b>
     * </p>
     * 
     * @see JMenuBar
     * @see InnerJMenuBar
     * @see MusicLibraryManager
     * @see PlaylistGUI
     */
    
    void closeCreaterPanel()
    {   	
    	setCurrentOpenPanel(null);
    	isLibCreaterOpen = false;
    	
    	if(libCreater.isLibraryUpdated())
		{
			//Call the update method!
    		if(musicLibraryManager != null)
    		{
    			musicLibraryManager.forceLibraryPlaylistUpdate();
    		}
    		else
    		{
    			emptyLibrary = false;
    			initNewLibraryManager(emptyLibrary);
    		}
    		
    		try 
	        {	
	    		if(musicLibraryManager.getMapOfSong()!=null)
	    		{
					playListWindow = new PlaylistGUI(musicLibraryManager.getMapOfSong());
					playListWindow.setName("playList");
					MAX_PLAYLIST_HEIGHT = playListWindow.getHeight();
	    		}
				
			} 
	        catch (Exception e1)
	        {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
    	
    }
    
    /**
     * <B>
     * External call to open the Creator JPanel
     * </B>
     * <p>
     * This is needed so the {@link JMenuBar } inner-class {@link InnerJMenuBar}
     * can notify the panel when the menu-item for the Creator panel is pressed
     * to open it. 
     * </p>
     * 
     * @see JMenuBar
     * @see InnerJMenuBar
     */
    void openCreaterPanel()
    {          	
		isLibCreaterOpen = true;
		setCurrentOpenPanel(libCreater);
    }
    
    /**
     * <b>
     * This is the main method that handles the swapping or adding/removing of panels
     * on the player.
     * </b>
     * <p>
     * All the checking required for adding and removing the correct panel also happens here.
     * </p>
     * <p>
     * There are several states or sequences that need to be checked when adding or removing the
     * {@link currentPanel} from the player, below describes the process for when a call to
     * {@link #isMainPanel_InUse() } returns TRUE as the wrapper panel is open and in use by another panel.
     * <br><br><b>Wrapper Panel in use</b><br>
     * It calls to check if the wrapper panel is open, If so before it removes the correct panel, it sets
     * the {@link #lastPanelStillOpen } variable by assigning it using {@link #getCurrentOpenPanel() }.
     * After the <i>lastPanelStillOpen</i> is set, {@link #closeCurrentOpenPanel } is called. 
     * </p>
     * 
     * @param currentPanel (Required) Is the argument for changing to the new JPanel
     * 
     * @see #getCurrentOpenPanel
     * @see #closeCurrentOpenPanel
     * @see #openNewPanel
     */
    private void setCurrentOpenPanel(JPanel currentPanel)
    {
    	if(currentPanel != null)
    	{
    		System.out.println("Trying to change to panel: "+currentPanel.getName());
	    	if(isMainPanel_InUse())
	    	{
	    		lastPanelStillOpen = getCurrentOpenPanel();
	    		if(lastPanelStillOpen!=null)
	    		{
	    			System.out.println("<setCurrentOpenPanel> Last Panel is Now : "+lastPanelStillOpen.getName());
	    		}
	    		else
	    		{
	    			System.out.println("<setCurrentOpenPanel> Last Panel is Now : NULL");
	    		}
	    		closeCurrentOpenPanel();
	    	}
	    	else
	    	{
	    		/* Reaches this block if the wrapper panel (mainWindowPanel) is not in use or open.
	    		 * Then we need to add the wrapper panel and also add the new Panel.
	    		 * 
	    		 * Order of add is important.
	    		 * 
	    		 * - Need to remove last panel panelBar otherwise mainWindowPanel is added below
	    		 * 
	    		 * - Adding down Y+ of the screen so add main panelBar last.
	    		 */
	    		System.out.println("<setCurrentOpenPanel> MainPanel is NOT currently in use!");
	    		remove(panelBar);
	    		add(mainWindowPanel);
	    		add(panelBar);
	    		
	    	}
	    	/*
	    	 * Finally add the Actual argument JPanel to the mainWindowPanel (wrapper Panel)
	    	 * AND set mainWindowPanel in use to TRUE
	    	 */
	    	openNewPanel(currentPanel);
	    	mainPanel_In_Use = true;
	    	System.out.println("<setCurrentOpenPanel> MainPanel is NOW in use!");
	    	frame.pack();
    	}
    	else
    	{
    		System.out.println("Trying to remove current panel as setting to: Null");
    		if(isMainPanel_InUse())
	    	{
    			System.out.println("MainPanel is in use!");
    			closeCurrentOpenPanel();
    			openNewPanel(lastPanelStillOpen);
    			lastPanelStillOpen = null;
    			System.out.println("<setCurrentOpenPanel> Last Panel is Now NULL!");
	    		//lastPanelStillOpen = getCurrentOpenPanel();
    			mainPanel_In_Use = false;
	    		
	    	}
	    	else
	    	{
	    		System.out.println("MainPanel is NOT in use!");
	    		openNewPanel(currentPanel);		//currentPanel should be null here!		<-----
	    		lastPanelStillOpen = null;
	    		mainPanel_In_Use = false;
	    		
	    	}
	    	frame.pack();
    	}
    }
    
    /**
     * <b>Method handling specifically closing the current Panel added to the mainWindowPanel</b>
     * <p>
     * First checks if current panel is NULL, if not then proceeds to remove the panel returned by the call 
     * to {@link #getCurrentOpenPanel() }. Before it removes it, it does an additional check for the <u><i>libCreator</i></u> panel
     * as there is a boolean <u><i>isLibCreatorOpen</i></u> flag needed to be set (Required for the inner class JMenuBar to monitor
     * its state. 
     * </p>
     * 
     * @see #getCurrentOpenPanel()
     */
    private void closeCurrentOpenPanel()
    {
    	if(getCurrentOpenPanel()!=null)
    	{
	    	System.out.println("<closeCurrentOpenPanel> CLOSING Panel : "+getCurrentOpenPanel().getName());
	    	if(getCurrentOpenPanel().getName().compareToIgnoreCase("libCreator")==0)
	    	{
	    		isLibCreaterOpen = false;
	    	}
	    	mainWindowPanel.remove(getCurrentOpenPanel());
    	}
    }
    
    private void openNewPanel(JPanel currentPanel)
    {
    	currentOpenPanel = currentPanel;
    	if(currentPanel != null)
    	{
    		System.out.println("<openNewPanel> OPENING Panel : "+currentPanel.getName());
    		if(currentPanel.getName().compareToIgnoreCase("libCreator")==0)
    		{
    			isLibCreaterOpen = true;
    		}
    		//currentPanel.setVisible(true);
    		mainWindowPanel.add(currentPanel);
    	}
    	else
    	{
    		System.out.println("<openNewPanel> SETTING Open to NULL! ");
    		remove(mainWindowPanel);
    	}
    }
    
    /**
     * <b>Access method for getting the current panel added to the wrapper mainWindowPanel</b>
     * 
     * @return JPanel, The currentOpenPanel variable 
     */
    private JPanel getCurrentOpenPanel(){
    	return currentOpenPanel;
    }
    
    /**
     * <b>
     * Access method for checking state of
     * the Creator JPanel. 
     * </b>
     * <p>
     * This is accessed by this class and its inner-class
     * {@link InnerJMenuBar }.<br>
     * <i>Maybe need to re-think the use of this method</i>
     * </p> 
     * 
     * @return Boolean, The state of the JPanel. True for open.
     */
    boolean isCreaterPanelOpen()
    {
    	return isLibCreaterOpen;
    }
    
    /**
     * <b>
     * Checks if the Library Directory exists and creates if required, as well as 
     * validating the needed text files and creates them if they don't exist. 
     * </b>
     * <p>
     * Needed in order to check at player startup for the correct sub files and directories.
     * </p>
     * @return <b>True</b> if all files and directories are created, and if Library file contains data.
     */
    private boolean validate_LibraryDirectory()
    {
    	boolean directory_Validated = false;

    	File dataDir = new File(Root+"\\"+Data_Dir);
        if(!dataDir.exists())
        {
        	System.out.println("DATA_DIR Doesn't Exist!");
        	
        	File tempLibFile = new File(dataDir.getPath()+"\\"+Library_File);
        	
        	try 
        	{
        		dataDir.mkdir();
        		System.out.println(dataDir.getPath()+"\n"+dataDir.getAbsolutePath());
        		System.out.println(tempLibFile.getPath()+"\n"+tempLibFile.getAbsolutePath());
				if(tempLibFile.createNewFile())
				{
					System.out.println("The Library File was created!");
				}
				
			}
        	catch (IOException e)
        	{
				e.printStackTrace();
				System.out.println("DATA_DIR Doesn't Exist!");
				return directory_Validated;
			}
        }
        
        if(dataDir.exists())
        {
        	//Now check files exist!
        	File tempLibFile = new File(Root+"\\"+Data_Dir+"\\"+Library_File);
        	long sizeInBytes;

			System.out.println("Library_File Exist!");

			// FileReader fr = new FileReader(tempLibFile);
			// BufferedReader br = new BufferedReader(fr);
			if(!tempLibFile.exists())
			{
				try 
				{
					tempLibFile.createNewFile();
					System.out.println("Library_File.txt was created!");
				}
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (tempLibFile.isFile())
			{
				if (tempLibFile.length() > 0) {
					System.out.println("Library_File contains data!");
					directory_Validated = true;
				} else {
					directory_Validated = false;
					System.out.println("Library_File contains no data! File is Fresh!");
				}
			}

        }
    
    	return directory_Validated;
    }

    /**
     * <b>
     * Checks the </b><u><i>slimeplayer.properties</i></u><b> file for validity, and
     * updates the default values of this class with that of the file.
     * </b>
     * <p>
     * To be considered valid, the Properties file must exist. This should only return invalid result
     * if there is an error thrown in opening or closing of the file.<br>
     * If no file exists one is created, and saved with the default values held by this class.
     * If instead the file exists then the values are read and used in-place of the defaults. 
     * </p>
     * <p>
     * The fields in the file should follow the below guide:<br>
     * <b>Field : "value"</b>
     * <ul>
     * 	<li>DIR:"%USERPROFILE%\\My Documents\\My Music"</li>
     * 	<li>PLAYER_ROOT:"\\"</li>
     * 	<li>PLAYER_DATA_DIR:"Data_Files"</li>
     *  <li><u>PATHS_FILE:"SongPaths.txt"</u>  <b>(Deprecated)</b></li>
     *  <li>LIBRARY_FILE:"Library.txt"</li>
     * </ul>
     * </p>
     * <p>TODO: Need to handle the Exception correctly.</p>
     * @return <b>True</b> if <u><i>slimeplayer.properties</i></u> is valid.
     */
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
				 * LIBRARY_FILE:"Library.txt"
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
        			// TODO Need to handle this Exception
        			e1.printStackTrace();
        		}
            	catch (IOException e1) 
            	{
            		// TODO Need to handle this Exception
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
    
    
    private boolean isMainPanel_InUse()
    {
    	return mainPanel_In_Use;
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
                	//Disable the menubar and adjust the player
                	//frame.setBounds(frame.getX(), frame.getY()+(menuBar.getHeight()), frame.getWidth(), frame.getHeight()-menu.getHeight());*/
                	
                	menuBar.setVisible(false);
                	if(isMainPanel_InUse() && isCreaterPanelOpen())
                	{
                		setCurrentOpenPanel(null);
                		isLibCreaterOpen = false;
                	}
                	frame.pack();                	
                }
                else
                {
                	menuBar.setVisible(true);
                	frame.pack();
                }
            }
            else if(source == playList)
            {
            	//System.out.println("<<< The GUI Panel >>>\nSize: "+getWidth()+" X "+getHeight());
            	//System.out.println("<<< The playListWindow Panel >>>\nSize: "+playListWindow.getWidth()+" X "+playListWindow.getHeight());
                
            	if(playListWindow != null)
            	{
            		
            		if(getCurrentOpenPanel()!=null)
	                {
            			if(getCurrentOpenPanel().getName().compareTo(playListWindow.getName())==0)
            			{
	            			System.out.println("The currentOpenPanel is: "+getCurrentOpenPanel().getName()+" | wanting to open : "+playListWindow.getName());
		            		//remove(mainWindowPanel);
	            			//mainWindowPanel.setVisible(false);
		                	//frame.setLocation(frame.getX(), frame.getY()+playListWindow.getHeight());
		                	//frame.setSize(frame.getWidth(), frame.getHeight()-playListWindow.getHeight());
		            		setCurrentOpenPanel(null);
            			}
	                }
	                else
	                {
	                	//frame.setLocation(frame.getX(), frame.getY()-playListWindow.getHeight());
	                	//frame.setSize(new Dimension(frame.getWidth(), frame.getHeight()+playListWindow.getHeight()));
	                	
	                	if(!isMainPanel_InUse())
	            		{
	                		setCurrentOpenPanel(playListWindow);
	            		}
	                	else
	                		setCurrentOpenPanel(playListWindow);               	
	                }
            		frame.pack();
            	}
            }
            if(source == exit)
            {
            	if(!guiObserverList.isEmpty() || musicLibraryManager != null)
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
    
    /**
     * <b>This is an alternative shutdown method called when it is known that there are no attached observers</b>
     * <p>
     * This can be called to simplify the shutdown process as it is already determined that the observers are already
     * de-registered, or are null. Bypassing the callback and sync processes, for a quicker shutdown.
     * </p>
     */
    private void lightShutdown()
    {
    	this.setVisible( false );
        System.exit(0);
        System.gc();
    }
    
    /**
     * <b>This is the normal shutdown method, and is called after sync-ing and shutdown of other observers</b> 
     * <p>It only differs in comparison to {@link lightShutdown() } as it removes the {@link #musicLibraryManager } observer
     * from its observer list {@link #deregisterGuiObserver(GuiObserver) }</p>
     * 
     * @see #deregisterGuiObserver(GuiObserver)
     */
    private void shutdownPlayer()
    {
    	System.out.println(NAME+"Deregistering MusicLibraryManager...");
		deregisterGuiObserver(musicLibraryManager);
		System.out.println(NAME+"Deregistered MusicLibraryManager...");
		this.setVisible( false );
        System.exit(0);
        System.gc();
    }

    /**
     * @inheritDoc registerGuiObserver(GuiObserver)
     */
	@Override
	public void registerGuiObserver(GuiObserver guiObserver)
	{
		guiObserverList.add(guiObserver);
		System.out.println(NAME+"<<<< "+guiObserver.getGuiObserverName()+" Added! >>>>");
		
	}

	/**
     * @inheritDoc deregisterGuiObserver(GuiObserver)
     */
	@Override
	public void deregisterGuiObserver(GuiObserver guiObserver) {
		guiObserverList.remove(guiObserver);
		System.out.println(NAME+"<<<< "+guiObserver.getGuiObserverName()+" Removed! >>>>");
		
	}

	/**
     * @inheritDoc notifyAllObservers(PlayState)
     */
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
	
	/**
     * @inheritDoc guiCallback(PlayState, Song)
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

