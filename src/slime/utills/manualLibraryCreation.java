package slime.utills;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.tritonus.share.sampled.file.TAudioFileFormat;

import slime.media.SongTag;
import slime.media.WrongFileTypeException;

public class manualLibraryCreation extends JPanel implements ActionListener
{
    private static final boolean DEBUG = false;
	private String[] fileDirectory;
    private int identificationNumberStart;
    private int totalSongs = 1;
    private FileInputStream fis;
    private final String HOLDINGS_FILE = "Data_Files/HoldingsFile.txt";//The written holdings file
    private final String LIBRARY_FILE = "Data_Files/Lib_MP3player.txt";
    private final String SONG_PATHS_FILE = "Data_Files/SongPaths.txt";   
    private File[] listOfFiles;
    private HashMap<Integer, String> holdingSongLines;
    private HashMap<Integer,File> filePaths;
    public boolean startPlayer = false;
    public String theCurrentSongTitle = null;
    private String title = null;
    private String artist = null;
    private String recordingTitle = null;
    private int durration = 0;
    private int year = 0;
    private int popularity = 100;
    private String datAdded = null;
    private JPanel libraryPanel, displayPanel, adjustPanel,subTitlePanel, subArtistPanel,subAlbumPanel,subSecondsPanel,subYearPanel;
    private JTextField titleField,artistField,yearField,secondsField,albumField;
    private JLabel idLabel,titleLabel,artistLabel,albumLabel,timeLabel,yearLabel,ratingLabel,dateLabel;
    private boolean notPressedContinue = true;
    private JButton continueSearch, addToLibrary;

    public manualLibraryCreation(String[] directoryToSrearch)
    {
        filePaths = new HashMap<Integer,File>();
        holdingSongLines = new HashMap<Integer,String>();

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        //this.setPreferredSize(new Dimension(900,120));
        
        fileDirectory = directoryToSrearch;
        identificationNumberStart = 1;

        displayPanel = new JPanel();
        adjustPanel = new JPanel();
        libraryPanel = new JPanel();

        addToLibrary = new JButton("Add Folder");
        //adjustPanel.setPreferredSize(new Dimension(900,50));
        adjustPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        titleField = new JTextField("");
        titleField.setColumns(10);
        artistField = new JTextField("");
        artistField.setColumns(10);
        albumField = new JTextField("");
        albumField.setColumns(10);
        yearField = new JTextField("");
        yearField.setColumns(10);
        secondsField = new JTextField("");
        secondsField.setColumns(10);

        subTitlePanel = new JPanel();
        subTitlePanel.add(new JLabel("Title: "));
        subTitlePanel.add(titleField);

        subArtistPanel = new JPanel();
        subArtistPanel.add(new JLabel("Artist: "));
        subArtistPanel.add(artistField);

        subAlbumPanel = new JPanel();
        subAlbumPanel.add(new JLabel("Album: "));
        subAlbumPanel.add(albumField);

        subSecondsPanel = new JPanel();
        subSecondsPanel.add(new JLabel("Seconds: "));
        subSecondsPanel.add(secondsField);

        subYearPanel = new JPanel();
        subYearPanel.add(new JLabel("Year: "));
        subYearPanel.add(yearField);

        adjustPanel.add(subTitlePanel);
        adjustPanel.add(subArtistPanel);
        adjustPanel.add(subAlbumPanel);
        adjustPanel.add(subSecondsPanel);
        adjustPanel.add(subYearPanel);

        //displayPanel.setPreferredSize(new Dimension(900,50));
        displayPanel.setLayout(new FlowLayout());
        continueSearch = new JButton("Next");

        idLabel = new JLabel("[ID] ");
        titleLabel = new JLabel("[Title] ");
        artistLabel = new JLabel("[Artist] ");
        albumLabel = new JLabel("[Album] ");
        timeLabel = new JLabel("[Seconds] ");
        yearLabel = new JLabel("[Year] ");
        ratingLabel = new JLabel("[Rating] ");
        dateLabel = new JLabel("[Date added] ");

        displayPanel.add(idLabel);
        displayPanel.add(titleLabel);
        displayPanel.add(artistLabel);
        displayPanel.add(albumLabel);
        displayPanel.add(timeLabel);
        displayPanel.add(yearLabel);
        displayPanel.add(ratingLabel);
        displayPanel.add(dateLabel);
        displayPanel.add(continueSearch);

        libraryPanel.add(addToLibrary);

        add(libraryPanel);
        add(displayPanel);

        continueSearch.addActionListener(this);
        addToLibrary.addActionListener(this);
    }
    public manualLibraryCreation()
    {
        filePaths = new HashMap<Integer,File>();
        holdingSongLines = new HashMap<Integer,String>();

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        //this.setPreferredSize(new Dimension(900,120));

        fileDirectory = null;
        identificationNumberStart = 1;

        displayPanel = new JPanel();
        adjustPanel = new JPanel();
        libraryPanel = new JPanel();

        addToLibrary = new JButton("Add Folder");
        //adjustPanel.setPreferredSize(new Dimension(900,50));
        adjustPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        titleField = new JTextField("");
        titleField.setColumns(10);
        artistField = new JTextField("");
        artistField.setColumns(10);
        albumField = new JTextField("");
        albumField.setColumns(10);
        yearField = new JTextField("");
        yearField.setColumns(10);
        secondsField = new JTextField("");
        secondsField.setColumns(10);

        subTitlePanel = new JPanel();
        subTitlePanel.add(new JLabel("Title: "));
        subTitlePanel.add(titleField);

        subArtistPanel = new JPanel();
        subArtistPanel.add(new JLabel("Artist: "));
        subArtistPanel.add(artistField);

        subAlbumPanel = new JPanel();
        subAlbumPanel.add(new JLabel("Album: "));
        subAlbumPanel.add(albumField);

        subSecondsPanel = new JPanel();
        subSecondsPanel.add(new JLabel("Seconds: "));
        subSecondsPanel.add(secondsField);

        subYearPanel = new JPanel();
        subYearPanel.add(new JLabel("Year: "));
        subYearPanel.add(yearField);

        adjustPanel.add(subTitlePanel);
        adjustPanel.add(subArtistPanel);
        adjustPanel.add(subAlbumPanel);
        adjustPanel.add(subSecondsPanel);
        adjustPanel.add(subYearPanel);

        //displayPanel.setPreferredSize(new Dimension(900,50));
        displayPanel.setLayout(new FlowLayout());
        continueSearch = new JButton("Next");

        idLabel = new JLabel("[ID] ");
        titleLabel = new JLabel("[Title] ");
        artistLabel = new JLabel("[Artist] ");
        albumLabel = new JLabel("[Album] ");
        timeLabel = new JLabel("[Seconds] ");
        yearLabel = new JLabel("[Year] ");
        ratingLabel = new JLabel("[Rating] ");
        dateLabel = new JLabel("[Date added] ");

        displayPanel.add(idLabel);
        displayPanel.add(titleLabel);
        displayPanel.add(artistLabel);
        displayPanel.add(albumLabel);
        displayPanel.add(timeLabel);
        displayPanel.add(yearLabel);
        displayPanel.add(ratingLabel);
        displayPanel.add(dateLabel);
        displayPanel.add(continueSearch);

        libraryPanel.add(addToLibrary);

        add(libraryPanel);
        add(displayPanel);

        continueSearch.addActionListener(this);
        addToLibrary.addActionListener(this);
    }
    public void selectFolder()
    {
        JFileChooser fileChooser = new JFileChooser(".");
        //FileFilter filter1 = new ExtensionFileFilter("JPG and JPEG", new String[]{"JPG", "JPEG"});
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setMultiSelectionEnabled(true);
        //fileChooser.setFileFilter(filter1);
        //Disables all files options
        fileChooser.setAcceptAllFileFilterUsed(false);
        int status = fileChooser.showOpenDialog(null);
        if (status == JFileChooser.APPROVE_OPTION) {
            File[] files = fileChooser.getSelectedFiles();
            String[] dirs = new String[files.length];
            System.out.println("Adding ["+files.length+"] folders from: "+files[0].getParent());
            for(int i = 0; i < files.length; i++)
            {
                System.out.println("["+i+"] "+files[i].getName()+" ~ "+files[i].getPath());
                File subFile = new File (files[i].getPath());
                if(subFile.isDirectory()){
                	File[] value = subFile.listFiles();
                	System.out.println("\t ~ "+value.length + " Directories / Files!");
                }
                dirs[i] = files[i].getPath();
            }
            fileDirectory = dirs;
            //File selectedFile = fileChooser.getSelectedFile();
            
        } else if (status == JFileChooser.CANCEL_OPTION) {
            System.out.println(JFileChooser.CANCEL_OPTION);
        }
        searchTheDirectory(fileDirectory);
    }
    
    /*
     *
     * File path grabber
     * 
     * Grabs the path to the validated music file and adds it to the String Array!
     * 
     */
    public String[] MusicFilePathGrabber(File[] fileArray)
    {
    	//Iterate over the list and extract the paths!
    	String[] dirs = new String[fileArray.length];
    	
    	for(int index = 0; index < fileArray.length; index++)
    	{
    		dirs[index] = fileArray[index].getPath();
    	}
    	return dirs;
    }
    
    public void searchTheDirectory(String[] directory)
    {
        //Loop through paths directory and validate each file, and tag data
    	//and then add to the playlist paths list!
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
                filePaths.put(countSongs, listOfFiles[i]);
                
                Character tab = (Character)('\t');
                try
                {
                	SongTag newTempTag = new SongTag(listOfFiles[i]);
                    title = newTempTag.getSongTitle();                
                    artist = newTempTag.getArtist();
                    recordingTitle = newTempTag.getRecordingTitle();
                    year = newTempTag.getYear();
                    durration += newTempTag.getDurration();
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
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date timeAdded = new Date();
                String yearDate = dateFormat.format(timeAdded).substring(0, dateFormat.format(timeAdded).indexOf("/"));
                String findRest = dateFormat.format(timeAdded).substring(dateFormat.format(timeAdded).indexOf("/")+1);
                String monthDate = findRest.substring(0, findRest.indexOf("/"));
                String restDate = findRest.substring(findRest.indexOf("/")+1);
                String dayDate = restDate.substring(restDate.indexOf("/")+1, restDate.indexOf(" "));
                datAdded = dayDate+"/"+monthDate+"/"+yearDate;
                
                String librarySongLine = Integer.toString(identificationNumberStart)+tab+title+tab+artist+tab+recordingTitle+tab+durration+tab+year+tab+popularity+tab+datAdded;
                
                holdingSongLines.put(identificationNumberStart, librarySongLine);
                identificationNumberStart++;

                countSongs++;
                totalSongs = countSongs;
            }
            System.out.println("~~~ Scanned Directory! ~~~");
        }
        System.out.println("Writing holdings file....");
        writeLibraryFile();
        System.out.println("Finished holdings file....");
    }
    public void writeLibraryFile()
    {
        PrintWriter libraryPrintWriter = null;
        PrintWriter songPathPrintWriter = null;
        try
        {
            libraryPrintWriter = new PrintWriter(new FileWriter(LIBRARY_FILE));
            songPathPrintWriter = new PrintWriter(new FileWriter(SONG_PATHS_FILE));
        }
        catch (IOException ex)
        {
            Logger.getLogger(manualLibraryCreation.class.getName()).log(Level.SEVERE, null, ex);
        }
        int index = 1;
        while(index < totalSongs)
        {

            libraryPrintWriter.println(holdingSongLines.get(index));
            songPathPrintWriter.println(Integer.toString(index)+" "+filePaths.get(index).getAbsolutePath());
            //System.out.println(Integer.toString(index)+" "+filePaths.get(index).getAbsolutePath());
            index++;
        }
        libraryPrintWriter.flush();
        songPathPrintWriter.flush();
        libraryPrintWriter.close();
        songPathPrintWriter.close();
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
        /*int index = 1;
        while(index < totalSongs)
        {

            holdingsPrintWriter.println(holdingSongLines.get(index));
            songPathPrintWriter.println(Integer.toString(index)+" "+filePaths.get(index).getAbsolutePath());
            //System.out.println(Integer.toString(index)+" "+filePaths.get(index).getAbsolutePath());
            index++;
        }
        holdingsPrintWriter.flush();
        songPathPrintWriter.flush();
        holdingsPrintWriter.close();
        songPathPrintWriter.close();*/
    }
    
    public static void main(String[] args)
    {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Error setting native LAF: " + e);
        }

        /*String firstFir = "C:/Users/phantomfightr/Documents/D Programming/New Chart Music";
        String secondDir = "C:/Users/Phantom/Music/Audioslave/Revelations";//<----
        String thirdDir = "C:/Users/Phantom/Music/Foo Fighters/Echoes, Silence, Patience & Grace";//<----
        String fourthDir = "C:/Users/Phantom/Music/Metallica/Death Magnetic";//<----
        String[] listOfDir = {firstFir,secondDir,thirdDir,fourthDir};*/
        //LibraryFile newFile = new LibraryFile(listOfDir);
        manualLibraryCreation newFile = new manualLibraryCreation();
        Toolkit tools = Toolkit.getDefaultToolkit();
        Dimension dimension = tools.getScreenSize();
        int width = (int)dimension.getWidth() / 2;
        int height = (int)dimension.getHeight() / 2;
        //MP3PlayerGUI gui = new MP3PlayerGUI();<----
        //MenuBar bar = new MenuBar(gui);<----
        JFrame frame = new JFrame("Holdings File Writer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(newFile);
        //frame.setJMenuBar(bar);<----
        frame.setSize(width, height);
        frame.setLocation(width / 2, height / 2);
        frame.setVisible(true);
        frame.pack();
        newFile.loadLibraryFile();
        //newFile.searchTheDirectory();<----
        //newFile.writeHoldFile();<----
    }

    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();
        if(continueSearch == source)
        {
            notPressedContinue = false;
        }
        if(addToLibrary == source)
        {
            selectFolder();
        }
    }
}
