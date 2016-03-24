package slime.core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

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

public class SlimeMenuBar  extends JMenuBar
{
   
   private Color BACKGROUND_COLOR = Color.BLACK;
   private Color FOREGROUND_COLOR = Color.white;

   public SlimeMenuBar(PlayerGUI gui)
   {
      super();
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
