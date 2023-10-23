/*
 * Athabasca University
 * COMP435 - Multimedia Technologies
 * Nicholas O'Leary
 * 3466559
 * Assignment 1
 * File: ImageCanvas.java
 * Description: 
 *     A subclass of JLabel which displays a buffered image.
 *     It is used as the view of the scrollpane in Viewer.java.
 */
package components;

import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.GraphicsDevice;
import java.awt.image.*; // BufferedImage
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Scrollable;

/*
 * CLASS ImageCanvas
 *
 * A JLabel subclass intended for use as a JViewport View.
 *
 * attributes:
 * image                                          BufferedImage that holds the image to be displayed. 
 *
 * methods:
 * ImageCanvas()                                  Default constructor.
 * ImageCanvas(BufferedImage)                     Constructor which intializes the image.
 * paint(Graphics)                                Redefine the paint method in order to draw the BufferedImage.
 * setImage(BufferedImage)                        Change the image.
 * mouseMoved(MouseEvent)                         Needed for the MouseMotionListener interface.
 * mouseDragged(MouseEvent)                       Needed for the MouseMotionListener interface.
 * getPreferredSize()                             Needed for the Scrollable interface.
 * getPreferredScrollableViewportSize()           Needed for the Scrollable interface.
 * getScrollableUnitIncrement(Rectangle,int,int)  Needed for the Scrollable interface.
 * getScrollableBlockIncrement(Rectangle,int,int) Needed for the Scrollable interface.
 * getScrollableTracksViewportWidth()             Needed for the Scrollable interface.
 * getScrollableTracksViewportHeight()           Needed for the Scrollable interface.
 */
public class ImageCanvas extends JLabel
    implements Scrollable, MouseMotionListener{
    
    private BufferedImage image;
    private int maxWidth;
    private int maxHeight;

    public ImageCanvas() {
	this.image = null;
    }
    
    public ImageCanvas(BufferedImage i) {
	this.image = i;
	setAutoscrolls(true);
	addMouseMotionListener(this);
    }

    public ImageCanvas(BufferedImage i, int maxWidth, int maxHeight) {
	this.image = i;
	setAutoscrolls(true);
	addMouseMotionListener(this);
	this.maxWidth = maxWidth;
	this.maxHeight = maxHeight;
    }
    
    public void paint(Graphics g) {
	g.drawImage(image, 0, 0, this);
    }

    public void setImage(BufferedImage i) {
	this.image = i;
    }    

    /*
     * MouseMotionListener Interface
     */
    public void mouseMoved(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {
	Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
	scrollRectToVisible(r);
    }
    
    /*
     *  Scrollable Interface
     */
    public Dimension getPreferredSize() {
	return new Dimension(image.getWidth(), image.getHeight());
	/*
	int w = image.getWidth();
	int h = image.getHeight();
	if (w <= maxWidth && h <= maxHeight) {
	    // Return the image size.
	    return new Dimension(image.getWidth(), image.getHeight());		
	}
	return new Dimension(maxWidth, maxHeight);
	*/
    }

    public Dimension getMaximumSize() {
	return new Dimension(maxWidth, maxHeight);	
	//	return getPreferredSize();
    }
    
    public Dimension getPreferredScrollableViewportSize() {
	return getMaximumSize();
	//	return getPreferredSize();
    }

    /*
     * For components that have meaningful rows or columns, this function
     * returns the appropriate scroll increment to reveal one complete
     * row/column. That does not apply for our images, so we always return 10.
     */
    public int getScrollableUnitIncrement(Rectangle visibleRect,
					  int orientation,
					  int direction) {
	return 10;
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect,
					   int orientation,
					   int direction) {
	return 10;
    }

    public boolean getScrollableTracksViewportWidth() {
	return false;
    }

    public boolean getScrollableTracksViewportHeight() {
	return false;
    }    
}
