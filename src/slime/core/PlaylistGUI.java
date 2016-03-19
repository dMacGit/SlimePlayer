package slime.core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Scrollbar;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import slime.managers.MusicLibraryManager;
import slime.media.SongTag;
import slime.utills.ComponentMover;
import slime.utills.ImageLoader;
import slime.utills.ShrinkImageToSize;

public class PlaylistGUI extends JPanel implements MouseListener, ActionListener
{
	//This is the dir path to the images folder		---> Change if necessary!
    public static String THE_FOLDER_DIR = "images/";
    
    private MusicLibraryManager player;
    private LinkedList<SongTag> mapOfSongs;
    private final int MAJOR_WIDTH = 500;
    private JTable list;
    private Object[][] rowData;
    private Object[] colomnNames = {"ID","Title","Artist","Album","Durration","Year"};
    private JPanel templatePanel,panelID,panelTitle,panelArtist,panelAlbum,panelDuration,panelYear, barGap;
    private JLabel templateID,templateTitle,templateArtist,templateAlbum,templateDuration,templateYear;
    private JPanel entireGUI;
    private JScrollPane verticalPane;
    private JScrollBar scrollBar;
    private final int panelHeight = 35;
    private boolean state = false;
    private JFrame frame;
    private JPanel windowControlls;
    							  //ShrinkImageToSize.shrinkImageToSize(new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource(THE_FOLDER_DIR+"playButtonGlossy.png"))),H_Size,H_Size)
    private ImageIcon CLOSE_BOX = ShrinkImageToSize.shrinkImageToSize(new ImageIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource(THE_FOLDER_DIR+"Close_Icon.png"))),23,23);
    private JLabel close_Box;
    private JPanel sliderPanel;
    private int MaxExt;
    private boolean fullyOpened = false, fullyClosed = true;
    private int Slider_Ext_Ammount = 20;
    private Dimension Ext_Dimension = new Dimension(506,0);
    private Timer sliderTimer;

    public PlaylistGUI(MusicLibraryManager songPlayer)
    {        
    	System.out.println("Entered into the Playlist class!");
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        sliderPanel = new JPanel();
        sliderPanel.setBackground(Color.red);
        barGap = new JPanel();
        windowControlls = new JPanel();
        windowControlls.setLayout(new FlowLayout(FlowLayout.RIGHT,0,0));
        windowControlls.setBackground(Color.BLACK);
        windowControlls.setPreferredSize(new Dimension(505,22));
        close_Box = new JLabel(CLOSE_BOX);
        windowControlls.add(close_Box);
        barGap.setPreferredSize(new Dimension(20,30));
        
        
        
        this.player = songPlayer;
        mapOfSongs = new LinkedList<SongTag>();
        //Set up the jfram and add to this class.

        Toolkit tools = Toolkit.getDefaultToolkit();
        Dimension dimension = tools.getScreenSize();
        int width = (int)dimension.getWidth() / 2;
        int height = (int)dimension.getHeight() / 2;
        frame = new JFrame("Play List");
        frame.setUndecorated( true );
        ComponentMover cm = new ComponentMover();
        cm.registerComponent(frame);
        frame.getContentPane().add(this);
        frame.setSize(width, height);
        frame.setLocation(width / 2, height / 2);
        frame.setVisible(true);
        
        //setup the jpanels and other display data
        entireGUI = new JPanel();
        scrollBar = new JScrollBar(Scrollbar.VERTICAL, 0, 8, -100, 100);
        entireGUI.setPreferredSize(new Dimension(MAJOR_WIDTH+barGap.getWidth(),this.getComponentCount()*height));
        sliderPanel.setPreferredSize(new Dimension(MAJOR_WIDTH+barGap.getWidth(),MaxExt));
        Font smallFontText = new Font("Dialog", Font.PLAIN, 9);
        entireGUI.setBackground(Color.BLACK);
        templateID = new JLabel("ID");
        templateID.setFont(smallFontText);
        templateTitle = new JLabel("Title");
        templateTitle.setFont(smallFontText);
        templateArtist = new JLabel("Artist");
        templateArtist.setFont(smallFontText);
        templateAlbum = new JLabel("Album");
        templateAlbum.setFont(smallFontText);
        templateDuration = new JLabel("Time");
        templateDuration.setFont(smallFontText);
        templateYear = new JLabel("Year");
        templateYear.setFont(smallFontText);
        templatePanel = new JPanel();

        panelID = new JPanel();
        panelID.setPreferredSize(new Dimension(25,20));
        panelID.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        panelID.add(templateID);
        panelTitle = new JPanel();
        panelTitle.setPreferredSize(new Dimension(150,20));
        panelTitle.setLayout(new FlowLayout(FlowLayout.CENTER));
        panelTitle.add(templateTitle);
        panelArtist = new JPanel();
        panelArtist.setPreferredSize(new Dimension(80,20));
        panelArtist.setLayout(new FlowLayout(FlowLayout.CENTER));
        panelArtist.add(templateArtist);
        panelAlbum = new JPanel();
        panelAlbum.setPreferredSize(new Dimension(110,20));
        panelAlbum.setLayout(new FlowLayout(FlowLayout.CENTER));
        panelAlbum.add(templateAlbum);
        panelDuration = new JPanel();
        panelDuration.setPreferredSize(new Dimension(45,20));
        panelDuration.setLayout(new FlowLayout(FlowLayout.CENTER));
        panelDuration.add(templateDuration);
        panelYear = new JPanel();
        panelYear.setPreferredSize(new Dimension(45,20));
        panelYear.setLayout(new FlowLayout(FlowLayout.CENTER));
        panelYear.add(templateYear);

        templatePanel.setPreferredSize(new Dimension(MAJOR_WIDTH+barGap.getWidth(),30));
        templatePanel.setBackground(Color.DARK_GRAY);
        templatePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        templatePanel.add(panelID);
        templatePanel.add(panelTitle);
        templatePanel.add(panelArtist);
        templatePanel.add(panelAlbum);
        templatePanel.add(panelDuration);
        templatePanel.add(panelYear);
        verticalPane = new JScrollPane(entireGUI); // component
        verticalPane.setVerticalScrollBar(scrollBar);
        verticalPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); // vertical bar
        verticalPane.getVerticalScrollBar().setUnitIncrement(35);
        //verticalPane
        verticalPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        verticalPane.setPreferredSize(new Dimension(MAJOR_WIDTH+barGap.getWidth(),498));
        
        entireGUI.add(templatePanel);
        
        createList();
        frame.pack();
        state = true;
        close_Box.addMouseListener(this);
        

    }
    public void createList()
    {
        generatePlaylist();
        for(int index = 0; index < rowData.length; index++)
        {
            jPanelPlaylistTemplate createRowPanel = new jPanelPlaylistTemplate();
            entireGUI.add(createRowPanel.setNewPanel(rowData[index]));
            
        }
        entireGUI.setPreferredSize(new Dimension(MAJOR_WIDTH+barGap.getWidth(),((entireGUI.getComponentCount())*panelHeight)+(this.barGap.HEIGHT*2)));
        MaxExt = (entireGUI.getComponentCount()*panelHeight)+(this.barGap.HEIGHT*2);
        sliderPanel.setPreferredSize(new Dimension(MAJOR_WIDTH+barGap.getWidth(),MaxExt));
        sliderPanel.setLocation(this.getX(),this.getHeight());
        add(sliderPanel);
        sliderTimer = new Timer(100,this);
        this.sliderTimer.start();
        add(windowControlls);
        add(verticalPane);
        revalidate();
    }

    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) 
    {
        Object source = e.getSource();
        if(source == this.close_Box)
        {
            frame.setVisible(false);
            if(fullyOpened)
            {
                this.sliderTimer.start();
            }
        }
    }
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    public void actionPerformed(ActionEvent e)
    {

        if(!fullyOpened)
        {
            if(Ext_Dimension.height < 600)
            {

                remove(sliderPanel);
                int oldValue = Ext_Dimension.height;
                Ext_Dimension.height+=Slider_Ext_Ammount;
                System.out.println("Sliding out from: "+oldValue+" to: "+Ext_Dimension.height);
                sliderPanel.setLocation(this.getX(),this.getY()-Slider_Ext_Ammount);
                add(sliderPanel);
                sliderPanel.setVisible(true);
                revalidate();
            }
            else
                fullyOpened = true;

        }
        if(!fullyClosed)
        {
            if(Ext_Dimension.height > 0)
            {
                remove(sliderPanel);
                int oldValue = Ext_Dimension.height;
                Ext_Dimension.height-=Slider_Ext_Ammount;
                System.out.println("Sliding out from: "+oldValue+" to: "+Ext_Dimension.height);
                sliderPanel.setLocation(this.getX(),this.getY()+Slider_Ext_Ammount);
                add(sliderPanel);
                sliderPanel.setVisible(true);
                revalidate();
            }
            else
                fullyClosed = true;

        }
    }
    private class jPanelPlaylistTemplate
    {
        private JPanel rowEnrty,idPanel,titlePanel,artistPanel,albumPanel,durationPanel,yearPanel;
        private JLabel id,title,artist,album,duration,year;
        private final Font textFont = new Font("Dialog", Font.BOLD, 9);
        private JPanel rowEntry;

        public JPanel setNewPanel(Object[] songDetails)
        {
            id = new JLabel(songDetails[0].toString());
            id.setPreferredSize(templateID.getPreferredSize());
            id.setFont(textFont);
            id.setVerticalTextPosition(SwingConstants.CENTER);
            id.setHorizontalTextPosition(SwingConstants.CENTER);
            title = new JLabel(songDetails[1].toString());
            title.setFont(textFont);
            artist = new JLabel(songDetails[2].toString());
            artist.setFont(textFont);
            album = new JLabel(songDetails[3].toString());
            album.setFont(textFont);
            duration = new JLabel(songDetails[4].toString());
            duration.setFont(textFont);
            year = new JLabel(songDetails[5].toString());
            year.setFont(textFont);
            rowEnrty = new JPanel();

            idPanel = new JPanel();
            idPanel.setPreferredSize(panelID.getPreferredSize());
            idPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

            idPanel.add(id);
            titlePanel = new JPanel();
            titlePanel.setPreferredSize(new Dimension(150, 20));
            titlePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            titlePanel.add(title);
            artistPanel = new JPanel();
            artistPanel.setPreferredSize(new Dimension(80, 20));
            artistPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            artistPanel.add(artist);
            albumPanel = new JPanel();
            albumPanel.setPreferredSize(new Dimension(110, 20));
            albumPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            albumPanel.add(album);
            durationPanel = new JPanel();
            durationPanel.setPreferredSize(panelDuration.getPreferredSize());
            durationPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            durationPanel.add(duration);
            yearPanel = new JPanel();
            yearPanel.setPreferredSize(new Dimension(panelYear.getPreferredSize()));
            yearPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            yearPanel.add(year);

            rowEnrty.setPreferredSize(new Dimension(500, 30));
            rowEnrty.setBackground(Color.WHITE);
            rowEnrty.setLayout(new FlowLayout(FlowLayout.LEFT));
            rowEnrty.add(idPanel);
            rowEnrty.add(titlePanel);
            rowEnrty.add(artistPanel);
            rowEnrty.add(albumPanel);
            rowEnrty.add(durationPanel);
            rowEnrty.add(yearPanel);
            return rowEnrty;
        }
    }
    
    /*
     * This method is used in order to initialize the playlist data structures
     * used for displaying the playlist in the player GUI.
     */
    private void generatePlaylist()
    {
    	
        try
        {
			mapOfSongs.addAll( (LinkedList<SongTag>)(player.getMapOfSong()));
		}
        catch (Exception ex) 
        {
			ex.getMessage();
		}
        rowData = new Object[mapOfSongs.size()][6];
        makeObjectArray(mapOfSongs.size());
    }
    
    /*
     * Old code. Needs removal or replacing!
     */
    public void makeObjectArray(int number)
    {
        String ID = "0";
        String Title = "";
        String Artist = "";
        String Album = "";
        String Durration = "";
        String Year = "";

        for(int index = 0; index < number; index++)
        {
            //String holdingInfoForCurrentSong = mapOfSongs.get(index+1);
            //ID = holdingInfoForCurrentSong.substring(0, holdingInfoForCurrentSong.indexOf('\t'));
            rowData[index][0] = ID;
            //String first = holdingInfoForCurrentSong.substring(holdingInfoForCurrentSong.indexOf('\t')+1);
            //Title = first.substring(0, first.indexOf('\t'));
            rowData[index][1] = mapOfSongs.get(index).getSongTitle();
            //String second = first.substring(first.indexOf('\t')+1);
            //Artist = second.substring(0, second.indexOf('\t'));
            rowData[index][2] = mapOfSongs.get(index).getArtist();
            //String third = second.ssubstring(second.indexOf('\t')+1);
            //Album = third.substring(0, third.indexOf('\t'));
            rowData[index][3] = mapOfSongs.get(index).getRecordingTitle();
            //String fourth = third.substring(third.indexOf('\t')+1);
            //Durration = fourth.substring(0,fourth.indexOf('\t'));
            rowData[index][4] = mapOfSongs.get(index).getDurration();
            //String fifth = fourth.substring(fourth.indexOf('\t')+1);
            //Year = fifth.substring(0,fifth.indexOf('\t'));
            rowData[index][5] = mapOfSongs.get(index).getYear();
        }
    }
    public boolean isOpen()
    {
        if(frame.isShowing())
        {
            state = true;
        }
        return state;
    }
    public void open()
    {
        frame.setVisible(true);
    }
}
