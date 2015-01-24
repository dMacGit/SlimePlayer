/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package slime.utills;

import java.awt.Image;
import javax.swing.ImageIcon;

/**
 *
 * @author phantomfightr
 *
 * A simple utility program to convert the input image to a smaller desired size.
 */

public class ShrinkImageToSize
{
    public static ImageIcon shrinkImageToSize(ImageIcon image, int xSize, int ySize)
    {
        Image sourceImage = image.getImage();
        return new ImageIcon(sourceImage.getScaledInstance(xSize,ySize,Image.SCALE_SMOOTH));
    }
}
