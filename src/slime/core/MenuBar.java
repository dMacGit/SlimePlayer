package slime.core;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

public class MenuBar  extends JMenuBar
{
    private final PlayerGUI playerGui;

   public MenuBar(PlayerGUI gui)
   {
      super();
      playerGui = gui;
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

         }
      };
      JMenu fileDrop = new JMenu("Menu");
      fileDrop.add(playList);
      fileDrop.add(addFolder);
      add(fileDrop);
   }
}
