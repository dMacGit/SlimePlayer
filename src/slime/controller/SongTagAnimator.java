package slime.controller;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * This is the class which handles the animation of the scrolling text in the
 * GUI Bar.
 *  
 * It takes a pre-formatted string as argument in its constructor, sets it a text in a JLabel,
 * and then sets up a timer object.
 * The timer then animates the string, by modifying the character(s) position in the label every
 * 'n' milliseconds defined by the variable "RATE" and the calculation (1000 / RATE).
 * 
 * This seems to meet the requirements of the animation properties that I had in mind.
 * 
 * NOTE: - 	There seems to be a small UI-Bug when the String / Song Info is under a certain
 * 			number of characters in length. Where by the point at which the characters dissapear
 * 			off-screen shifts / moves by an unknown number of characters, as the string is animated. 
 * 
 */

public class SongTagAnimator extends JPanel implements ActionListener 
{

	//Main delay variable in the timer object.
	private static final int RATE = 16;
	
	//The animation rate set for the timer object.
    private final Timer timer = new Timer(1000 / RATE, this);
    
    //The JLabel where the string is animated on.
    private final JLabel label = new JLabel();
    
    //Variables used in the animation.
    private String s;
    private int n;
    private int index;

    /*
     * Initial constructor for the class.
     * 
     * 'S' argument is the string to be animated.
     * 'N' argument is the size of the animation area.
     * 
     * NOTE: Animation area, has been set as the length of the string
     */
    public SongTagAnimator(String s, int n) {
        if (s == null || n < 1) {
            throw new IllegalArgumentException("Null string or n < 1");
        }
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            sb.append(' ');
        }
        this.s = sb + s + sb;
        this.n = n;
        //label.setFont(new Font("Serif", Font.ITALIC, 36));
        label.setText(sb.toString());
        label.setForeground(Color.WHITE);
        this.add(label);
        start();
    }

    public void updateText(String s, int n)
    {
    	stop();
    	reset();
    	if (s == null || n < 1) {
            throw new IllegalArgumentException("Null string or n < 1");
        }
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            sb.append(' ');
        }
        this.s = sb + s + sb;
        this.n = n;
        //label.setFont(new Font("Serif", Font.ITALIC, 36));
        label.setText(sb.toString());
        label.setForeground(Color.WHITE);
        this.add(label);
        start();
    }
    
    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
        
    }

    public void reset(){
    	index = 0;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        index++;
        if (index > s.length() - n) {
            index = 0;
        }
        label.setText(s.substring(index, index + n));
    }
}