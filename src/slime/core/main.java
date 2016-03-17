package slime.core;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import slime.controller.MediaController;
import slime.managers.MusicLibraryManager;
import slime.utills.ComponentMover;
import slime.utills.ImageLoader;

public class main
{
	public static PlayerGUI gui;
	public static MusicLibraryManager musicManagerLibrary;
	public static MediaController mediaController;
	
	//This is the dir path to the images folder		---> Change if necessary!
    
    public static String THE_FOLDER_DIR = "images/", FILE_DIR = "Data_Files"; ;
    private final static String defaultUserMusicDirectory = "%USERPROFILE%\\My Documents\\My Music";
    
    private static TrayIcon trayIcon;
	
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

            JFrame frame = new JFrame("My MP3 Player");
            /*MenuBar menuBar = new MenuBar(gui);
            frame.setJMenuBar(menuBar);*/
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
            
            //Now register all of the observers and subjects.
            
            
            
    	} 
    	else 
    	{
    		System.out.println("There was an error finding the icon directory folder [ "+location+""+fileName+" ]");
    	}
    	
	}

}
