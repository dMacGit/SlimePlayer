package slime.utills;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;


import org.farng.mp3.TagException;
import org.tritonus.share.sampled.file.TAudioFileFormat;

import slime.exceptions.WrongFileTypeException;
import slime.song.SongTag;

/**
 * <b>
 * Class for manually adding directories and creating the required text files
 * for the application to operate.
 * </b>
 * 
 * <p>
 * Contains GUI elements so user can edit JTextfield to required path of directory, or
 * press the Add Folder JButton to open up a JFileChooser, and select the desired
 * directory. Once the user is satisfied, the Accept button can be pressed to start
 * the validation/Searching and file writing procedures.
 * </p>
 * 
 * @author dMacGit
 * 
 */
public class manualLibraryCreation extends JPanel implements ActionListener
{
    
	
	private static final long serialVersionUID = -1522556823783906998L;
	private static final boolean DEBUG = false;
	
	private String[] fileDirectory;
    private int identificationNumberStart;
    private int totalSongs = 1;
    private static String LIBRARY_FILE, SONG_PATHS_FILE, DATA_DIR, ROOT;
    
    private static String DEFAULT_HOME_MUSIC_DIR;
    
    private File[] listOfFiles;
    
    private HashMap<Integer, String> librarySongLines;
    private HashMap<Integer,String> filePaths;
    
    public boolean startPlayer = false;
    public String theCurrentSongTitle = null;
    public String theCurrentDirPath = DEFAULT_HOME_MUSIC_DIR;
    
    private String title = null;
    private String artist = null;
    private String recordingTitle = null;
    private int durration = 0;
    private int year = 0;
    
    private int popularity = 100;
    
    private String dateAdded = null;
    
    private JPanel libraryPanel, displayPanel, adjustPanel;
    private JTextField currentDirectoryToSearch;
    private JLabel searchDirectoryLabel;
    private JButton addToLibrary, Accept;

    /*
     * The main catch all constructor.
     * 
     * All other constructors make calls to here.
     */
    public manualLibraryCreation(String directoryToSrearch)
    {
    	
    	try
    	{
        	Properties config = new Properties();
        	config.load(new FileInputStream("player.properties"));
			DEFAULT_HOME_MUSIC_DIR = LibraryHelper.removeQuotes(config.getProperty("DIR"));
			ROOT = System.getProperty("user.dir");
			DATA_DIR = LibraryHelper.removeQuotes(config.getProperty("PLAYER_DATA_DIR"));
			SONG_PATHS_FILE = LibraryHelper.removeQuotes(config.getProperty("PATHS_FILE"));
			LIBRARY_FILE = LibraryHelper.removeQuotes(config.getProperty("LIBRARY_FILE"));
			
		}
    	catch (FileNotFoundException ex) 
    	{
			
			ex.getMessage();
		}
    	catch (IOException e1) 
    	{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	fileDirectory = new String[1];
        filePaths = new HashMap<Integer,String>();
        librarySongLines = new HashMap<Integer,String>();

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        //fileDirectory = directoryToSrearch;
        theCurrentDirPath = directoryToSrearch;
        identificationNumberStart = 1;

        displayPanel = new JPanel();
        adjustPanel = new JPanel();
        libraryPanel = new JPanel();

        addToLibrary = new JButton("Add Folder");
        Accept = new JButton("Accept");

        adjustPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        searchDirectoryLabel = new JLabel("Directory: ");
        
        currentDirectoryToSearch = new JTextField(theCurrentDirPath);
        
        //Null check
        if(theCurrentDirPath != null)
        {
        	//final String dirCheck = System.getProperty("user.home") + System.getProperty("file.separator")+"Music";
        	//if(dirCheck.compareToIgnoreCase(theCurrentDirPath) == 0)
        	//{
        		currentDirectoryToSearch.setColumns(currentDirectoryToSearch.getText().length());
        		
        	//}
        	//else
        	//	System.out.println("missmatch: "+dirCheck+" <> "+theCurrentDirPath);
        	
        }
        else{
        	theCurrentDirPath = DEFAULT_HOME_MUSIC_DIR;
        	currentDirectoryToSearch.setText(theCurrentDirPath);
        }
        
        String rootDir = System.getProperty("user.dir");
        File rDir = new File(rootDir);
        File dataDir = new File(rootDir+"\\"+DATA_DIR);
        
        if(rDir.isDirectory() && !dataDir.exists())
        {
        	System.out.println("DATA_DIR Doesn't Exist!");
        	
        	File dataDirFile = new File(rootDir+"\\"+DATA_DIR);
        	
        	
        	File tempLibFile = new File(rootDir+"\\"+DATA_DIR+"\\"+LIBRARY_FILE);
        	File tempSongPathsFile = new File(rootDir+"\\"+DATA_DIR+"\\"+SONG_PATHS_FILE);
        	
        	try 
        	{
        		dataDirFile.mkdir();
        		System.out.println(dataDirFile.getPath()+"\n"+dataDirFile.getAbsolutePath());
        		System.out.println(tempLibFile.getPath()+"\n"+tempLibFile.getAbsolutePath());
				if(tempLibFile.createNewFile())
				{
					System.out.println("The Library File was created!");
				}
				if(tempSongPathsFile.createNewFile())
				{
					System.out.println("The Song Paths File was created!");
				}
			}
        	catch (IOException e)
        	{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        //------->

        displayPanel.setLayout(new FlowLayout());

        libraryPanel.add(searchDirectoryLabel);
        libraryPanel.add(currentDirectoryToSearch);
        libraryPanel.add(addToLibrary);
        libraryPanel.add(Accept);

        add(libraryPanel);
        add(displayPanel);

        addToLibrary.addActionListener(this);
        Accept.addActionListener(this);
    }
    
    //Constructor called when no directory specified: Creation Method
    public manualLibraryCreation()
    {
    	//Call to the main catch-all constructor
        this(null);
    }
    
    /*
     *	SelectSingleFolder Method is called to open up a new JFileChooser
     *	in order to manually select the directory. This then initiates the
     *	validation, searching and the file writing process.  
     */
    public void selectSingleFolder()
    {
        JFileChooser fileChooser = new JFileChooser(theCurrentDirPath);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //This is a single file/directory selector so set Multi-selection to false
        fileChooser.setMultiSelectionEnabled(false);
        //Disables all files options
        fileChooser.setAcceptAllFileFilterUsed(false);
        int status = fileChooser.showOpenDialog(null);
        if (status == JFileChooser.APPROVE_OPTION)
        {
        	
        	String tempDir = fileChooser.getSelectedFile().getAbsolutePath();
        	
        	//Validation done by calls to validation utility methods
        	if( LibraryHelper.DirectoryChecker(tempDir) && LibraryHelper.FileChecker(new File(tempDir)))
        	{
        		theCurrentDirPath = tempDir;
        		System.out.println("Current Selected Directory: "+theCurrentDirPath);
            	currentDirectoryToSearch.setText(theCurrentDirPath); 		
        	}
        	else
        	{
        		System.out.println("That was not valid!");
        	}
            
        } else if (status == JFileChooser.CANCEL_OPTION) {
            //Do nothing!
        }
    }
    
    /*
     * SearchTheDirectory Method.
     * 
     * Firstly: Completes a recursive search through all the directories and
     * does a final validation for .mp3 files while adding them to a list.
     * 
     * Secondly: Proceeds to complete more checks on filename sizes and adds
     * the file path to the paths list. 
     */
    public void searchTheDirectory(String[] directory)
    {
        //Loop through paths directory and validate each file, and tag data
    	//and then add to the paths list!
        int countSongs = 1;
        for(int index = 0; index < directory.length; index++)
        {
        	//Creating file list of applicable music files through recursive search
        	MusicFileFilter musicFilter = new MusicFileFilter();
            File[] list = RecursiveSearch.listFilesAsArray(new File(fileDirectory[index]), musicFilter, true);

            //Code to help know number of files and or directories in the main directory
            //Helpful in debugging. ENABLE/DISABLE using DEBUG flag set to true/false
            
            listOfFiles = list;
            if(DEBUG)
            {
            	for(int countIndex = 0; countIndex < list.length; countIndex++)
            	{
            		File nextFile = list[countIndex];
            		System.out.println("{"+countIndex+"} ["+nextFile.getName()+"]");
            	}
            	
            	System.out.println(list.length+"\n");
            }
            
            
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
            
            System.out.println("=== Parent Directory Stats ===");
            
            //Initial info on selected Directory: Just states the number of subDirs etc
            for(int stats_index = 0; stats_index < listOfFiles.length; stats_index++)
            {
                File tempFile = listOfFiles[stats_index];
                if(tempFile.isDirectory() && (tempFile.getName().length() > 2) && (tempFile.listFiles().length > 0) )
                {
                    int childFiles = tempFile.listFiles().length;
                    String nameOrPath;
                    if(tempFile.getName().trim().isEmpty())
                    {
                        nameOrPath = tempFile.getPath();
                    }
                    else
                    {
                        nameOrPath = tempFile.getName().trim();
                    }
                    System.out.println("Directory ["+(stats_index+1)+"] "+nameOrPath+" "+childFiles+"");
                }
                else
                    stats_index++;
            }
            System.out.println("");
            
            //Of the validated files, proceed to scan tag info
            for (int i = 0; i < listOfFiles.length; i++)
            {
            	
                
                Character tab = (Character)('\t');
                try
                {
                	SongTag newTempTag = new SongTag(listOfFiles[i]);
                    title = newTempTag.getSongTitle();                
                    artist = newTempTag.getArtist();
                    recordingTitle = newTempTag.getRecordingTitle();
                    year = newTempTag.getYear();
                    durration += newTempTag.getDurration();
                    
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date timeAdded = new Date();
                    String yearDate = dateFormat.format(timeAdded).substring(0, dateFormat.format(timeAdded).indexOf("/"));
                    String findRest = dateFormat.format(timeAdded).substring(dateFormat.format(timeAdded).indexOf("/")+1);
                    String monthDate = findRest.substring(0, findRest.indexOf("/"));
                    String restDate = findRest.substring(findRest.indexOf("/")+1);
                    String dayDate = restDate.substring(restDate.indexOf("/")+1, restDate.indexOf(" "));
                    dateAdded = dayDate+"/"+monthDate+"/"+yearDate;
                    
                    String path = listOfFiles[i].getAbsolutePath();
                    String pathLine = Integer.toString(identificationNumberStart)+" "+path;
                    
                    String librarySongLine = LineDataInitializer(title,artist,recordingTitle,durration,year,popularity,dateAdded,"&&",path,"[,]");
                    
                    librarySongLines.put(identificationNumberStart, librarySongLine);
                    
                    
                    filePaths.put(identificationNumberStart, pathLine);
                    
                    identificationNumberStart++;
                    countSongs++;
                }
                catch(WrongFileTypeException ex)
                {
                	System.out.println("Error reading File!");
                	countSongs--;
                }
                catch(NumberFormatException ex)
                {
                    System.out.println("error formating number! ");
                    countSongs--;
                }
                catch(UnsupportedOperationException ex)
                {
                    countSongs--;
                    System.out.println("<< Unsupported Operation!! >> "+ex);
                } 
                catch (IOException e) 
                {
                	countSongs--;
                	System.out.println("<< IO Exception!! >> "+e);
				} 
                catch (TagException e) 
                {
                	countSongs--;
                	System.out.println("<< Tag Exception!! >> "+e);
				}
                catch (Exception e) 
                {
                	System.out.println("<< Exception!! >> "+e);
				}
                

                
                totalSongs = countSongs;
            }
            System.out.println("~~~ Scanned Directory! ~~~");
        }
        
        try
        {
        	System.out.println("Writing Library file....");
			FileIO.WriteData(DATA_DIR+"\\"+LIBRARY_FILE,librarySongLines.values().toArray());
			System.out.println("Finished Library file....");
			System.out.println("Writing Paths file....");
			FileIO.WriteData(DATA_DIR+"\\"+SONG_PATHS_FILE,filePaths.values().toArray());
			System.out.println("Finished Paths file....");
		} 
        catch (IOException e) 
        {
			e.printStackTrace();
		}       
    }
    
    private String LineDataInitializer_OldMethod(String title, String artist, String recordingTitle, int durration, int year, int popularity, String dateAdded, Character spacer)
    {
    	//Here is where the data format of the lines are set and saved to the String	
    	return new String(spacer+title+spacer+artist+spacer+recordingTitle+spacer+durration+spacer+year+spacer+popularity+spacer+dateAdded);
    }
    
    private String LineDataInitializer(String title, String artist, String recordingTitle, int durration, int year, int popularity, String dateAdded, String spacer, String path, String delimiter) throws Exception
    {
    	//Here is where the data format of the lines are set and saved to the String
    	String startDelimiter;
    	String endDelimiter;
    	String midDelimiter;
    	if(delimiter.length() > 1)
    	{
    		//Use first character as start of delimiter
    		startDelimiter = "'"+delimiter.charAt(0)+"'";
    		endDelimiter = "'"+delimiter.charAt(delimiter.length()-1)+"'";
    		if(delimiter.length() == 3)
    		{
    			midDelimiter = "'"+delimiter.charAt(1)+"'";
    			return new String(startDelimiter+title+spacer+artist+spacer+recordingTitle+spacer+durration+spacer+year+spacer+popularity+spacer+dateAdded+endDelimiter+midDelimiter+startDelimiter+path+endDelimiter);
    		}
    		else return new String(startDelimiter+title+spacer+artist+spacer+recordingTitle+spacer+durration+spacer+year+spacer+popularity+spacer+dateAdded+endDelimiter+startDelimiter+path+endDelimiter);
    	}
    	else throw new Exception("Invalid number of delimiters!"); 
    	
    }
    
    private void loadLibraryFile()
    {
        BufferedReader libraryReader = null;
        //PrintWriter songPathPrintWriter = null;
        try
        {
            libraryReader = new BufferedReader(new FileReader(LIBRARY_FILE));
            //songPathPrintWriter = new PrintWriter(new FileWriter(SONG_PATHS_FILE));
        }
        catch (IOException ex)
        {
            System.out.println("LIBRARY_FILE not found");
        }
    }
    
    public static void main(String[] args)
    {
    	Properties config = new Properties();
    	String dirValue = null;
    	try
    	{
			config.load(new FileInputStream("player.properties"));
			dirValue = config.getProperty("DIR");
			System.out.println("Found the dir location @ "+dirValue);
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
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Error setting native LAF: " + e);
        }
        manualLibraryCreation newFile = new manualLibraryCreation(dirValue);
        Toolkit tools = Toolkit.getDefaultToolkit();
        Dimension dimension = tools.getScreenSize();
        int width = (int)dimension.getWidth() / 2;
        int height = (int)dimension.getHeight() / 2;
        JFrame frame = new JFrame("Holdings File Writer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(newFile);
        frame.setSize(width, height);
        frame.setLocation(width / 2, height / 2);
        frame.setVisible(true);
        frame.pack();
    }

    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();
        if(addToLibrary == source)
        {
            selectSingleFolder();
        }
        else if(Accept == source)
        {
        	if(this.currentDirectoryToSearch.getText() != null)
        	{
        		System.out.println(theCurrentDirPath);
        		fileDirectory[0] = theCurrentDirPath;
        		for(int x = 0 ; x < fileDirectory.length; x++)
        		{
        			System.out.println("Checking: "+x+" <> "+fileDirectory[x]);
        		}
        		searchTheDirectory(fileDirectory);
        	}
        }
    }
}
