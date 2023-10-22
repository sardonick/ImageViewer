/*
 * Athabasca University
 * COMP435 - Multimedia Technologies
 * Nicholas O'Leary
 * 3466559
 * Assignment 2
 * File: ImagePane.java
 * Description: 
 *
 */


package components;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

public class ImagePane extends JPanel {
    public static final float ZOOM_IN_FACTOR = 1.5F;
    public static final float ZOOM_OUT_FACTOR = 0.75F;

    private JScrollPane scrollPane = null;
    private RenderedOp image = null;
    private RenderedOp imageOriginal = null;
    private JButton zoomInButton;
    private JButton zoomOutButton;
    private JButton actualSizeButton;
    private JPanel buttonPanel;
    private JLabel title;
    private Interpolation interp;
    private boolean firstImage = true;
    public JPEGStream jStream;
    private int[] lumaQTable;
    private int[] chromaQTable;
    private int maxWidth;
    private int maxHeight;

    public Dimension getMaximumSize() {
	return new Dimension(maxWidth, maxHeight);
    }

    public void setLumaQTable(int[] table) {
	lumaQTable = table;	
    }

    public void setChromaQTable(int[] table) {
	chromaQTable = table;
    }

    public ImagePane(String title, int screenWidth, int screenHeight) {
	this.title = new JLabel(title);
	this.title.setAlignmentX(Component.RIGHT_ALIGNMENT);
	this.maxWidth = (int) (screenWidth * 0.4);
	this.maxHeight = screenHeight;
	// Initialize an interpolation object, which will be used for zoomIn
	// and zoomOut operations.
	interp = Interpolation.getInstance(Interpolation.INTERP_BILINEAR);

	// Create zoomInButton and assign its function.
	zoomInButton = new JButton("Zoom In");
	zoomInButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if (image != null) {
			setImage(zoom(ZOOM_IN_FACTOR));
		    }
		}
	    });
	zoomInButton.setEnabled(false); // zoom buttons are disabled on startup.

	// Create zoomOutButton and assign its function.
	zoomOutButton = new JButton("Zoom Out");
	zoomOutButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if (image != null) {
			setImage(zoom(ZOOM_OUT_FACTOR));
		    }
		}
	    });
	zoomOutButton.setEnabled(false);
	
	// Create actualSizeButton and assign its function.
	actualSizeButton = new JButton("ActualSize");
	actualSizeButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if ((image != null) && (imageOriginal != null)) {
			setImage(imageOriginal);
			// After restoring the image, the actualSizeButton is disabled.
			actualSizeButton.setEnabled(false);
		    }
		}
	    });
	actualSizeButton.setEnabled(false); // actualSizeButton is disabled on startup.

	buttonPanel = new JPanel();
	buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
	buttonPanel.add(zoomInButton);
	buttonPanel.add(zoomOutButton);
	buttonPanel.add(actualSizeButton);
	
	scrollPane = new JScrollPane();

	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	add(this.title);
	add(scrollPane);
	add(buttonPanel);
    }
    
    /*
     * Produce a RenderedOp that performs a scale transformation
     * by factor in both x and y directions.
     */
    public RenderedOp zoom(float factor) {
	// Re-enable the normalButton whenever a
	// zoom operation is performed.		
	actualSizeButton.setEnabled(true);
	// Create and initialize the params object for the transformation.
	ParameterBlock params = new ParameterBlock();
	params.addSource(image);
	params.add(factor);         // x scale factor
	params.add(factor);         // y scale factor
	params.add(0.0F);           // x translate
	params.add(0.0F);           // y translate
	params.add(interp);         // interpolation method
	return JAI.create("scale", params);
    }    

    /*
     * Load the image stored in fileName into the view of the JScrollPane.
     */
    public void setImage(String fileName) {
	// Reset the button state whenever an image is loaded.
	zoomInButton.setEnabled(true);
	zoomOutButton.setEnabled(true);
	actualSizeButton.setEnabled(false);
	RenderedOp img = JAI.create("fileload", fileName);
	// Save the initial representation of the image so that it can be restored
	// by the normalButton.
	imageOriginal = img;
	/* 
	 * If this is the first time an image has been loaded, then we 
	 * allow the window to take on the preferred size of the image,
	 * by not calling scrollPane.setPreferredSize.
	 */
	if (firstImage) {
	    firstImage = false;
	    image = img;
	    // Set the first image.
	    scrollPane.setViewportView(new ImageCanvas(img.getAsBufferedImage(), maxWidth, maxHeight));
	    //	    pack();
	    //	    repaint();	    
	} else { // If this is not the first image, then we keep the window size
	         // constant by calling setImage.
	    setImage(img);
	}
	revalidate();
	repaint();
    }

    /*
     * Load the image represented by ``ro'' into the view of the JScrollPane.
     */
    public void setImage(RenderedOp ro) {
	image = ro;
	// This method invocation causes the scrollPane's size to remain
	// constant even though the ViewportView is replaced. This is done
	// so that the window does not change size with each zoom operation.
	scrollPane.setPreferredSize(scrollPane.getSize());
	// Replace the current image being displayed.
	scrollPane.setViewportView(new ImageCanvas(ro.getAsBufferedImage(), maxWidth, maxHeight));
	// Redraw the Frame.
	revalidate();
	repaint();
    }

    public void makeJPEG() {
	if (image == null) {
	    System.out.println("Tried to convert null image to JPEG.");
	    System.exit(1);
	}
	jStream = new JPEGStream(image);
	setImage(jStream.getImage());
    }

    public void printQTable(int[] table) {
	for(int i = 0; i < 8; i++) {
	    for (int j = 0; j < 8; j++) {
		System.out.print(table[8*i+j] + " ");
	    }
	    System.out.print("\n");
	}
    }

    public void reloadJPEG() {

	System.out.println("Luma table");
	printQTable(lumaQTable);
	System.out.println("Chroma table");
	printQTable(chromaQTable);

	//	makeJPEG();
	if (image == null) {
	    System.out.println("Tried to convert null image to JPEG.");
	    System.exit(1);
	}
	setImage(imageOriginal);
	jStream = new JPEGStream();
	jStream.encode(imageOriginal, JPEGStream.zigZag(lumaQTable), JPEGStream.zigZag(chromaQTable));
	setImage(jStream.getImage());
	//	repaint();
    }
}
