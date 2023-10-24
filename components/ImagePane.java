/*
 * Athabasca University
 * COMP435 - Multimedia Technologies
 * Nicholas O'Leary
 * 3466559
 * Assignment 2
 * File: ImagePane.java
 * Description: 
 *      This class takes the functionality of the ImageViewer from the first assignment
 *      and puts it into a JPanel, so that multiple images can be displayed and manipulated 
 *      within the same JFrame.
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

/*
 * CLASS ImagePane
 *
 * attributes:
 * ZOOM_IN_FACTOR    A constant float which controls the scale factor for zooming in.
 * ZOOM_OUT_FACTOR   A constant float which controls the scale factor for zooming out.
 * scrollPane        JScrollPane which provides a viewport and scrollbars for the image using
                          an ImageCanvas.
 * image             The current image being displayed.
 * imageOriginal     The original representation of the image, used by the actualSizeButton. For 
 *                   the jpegImagePane this attribute stores the original bmp data.
 * zoomInButton      Enlarges the image.
 * zoomOutButton     Shrinks the image.
 * actualSizeButton  Resets the image to its original size.
 * buttonPanel       A JPanel which groups the buttons into a row.
 * title             A JLabel which displays either ``converted image'' or ``original image''
 * interp            Bilinear Interpolation object used by JAI to perform scale transformations.
 * firstImage        Flag used to control the component size.
 * jStream           A JPEGStream instance which is used to convert the image data to JPEG encoding.
 * lumaQTable        A pointer to a luminance quantization table used by make and reload JPEG.
 * chromaQTable      A pointer to a chrominance quantization table used by make and reload JPEG.
 * maxWidth          An integer storing the max width of this component.
 * maxHeight         An integer storing the max width of this component.
 * viewer            A reference to the parent of this component, the JFrame subclass Viewer2 instance.
 *
 * methods
 * getMaximumSize                       Returns the maximum Dimension of this component.
 * setLumaQTable                        Setter for the lumaQTable attribute.
 * setChromaQTable                      Setter for the chromaQTable attribute.
 * ImagePane(string, int, int , JFrame) Constructor, initializes the component.
 * zoom(float)                          Creates a RenderedOp which scales the image in both x and y directions by 
                                        the float factor argument.
 * setImage(string)                     Loads an image from a String filename.
 * setImage(renderedop)                 Loads a transformed image from a RenderedOp
 * makeJPEG()                           Encodes image as a JPEG using the default quantization tables.
 * reloadJPEG()                         Re-encodes image as a JPEG using the quantization tables stored in
 *                                      lumaQTable and chromaQTable, and the bitmap image data stored in 
 *                                      imageOriginal.
 */
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
    private JFrame viewer;

    public Dimension getMaximumSize() {
	return new Dimension(maxWidth, maxHeight);
    }

    public void setLumaQTable(int[] table) {
	lumaQTable = table;	
    }

    public void setChromaQTable(int[] table) {
	chromaQTable = table;
    }

    public ImagePane(String title, int screenWidth, int screenHeight, JFrame v) {
	this.title = new JLabel(title);
	this.title.setAlignmentX(Component.RIGHT_ALIGNMENT);
	this.maxWidth = (int) (screenWidth * 0.4);
	this.maxHeight = screenHeight;
	this.viewer = v;
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

	// Create button panel and add buttons to it.
	buttonPanel = new JPanel();
	buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
	buttonPanel.add(zoomInButton);
	buttonPanel.add(zoomOutButton);
	buttonPanel.add(actualSizeButton);

	// Create ScrollPane.
	scrollPane = new JScrollPane();

	// Arrange components vertically.
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
	/* Re-enable the normalButton whenever a
	   zoom operation is performed.*/
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
	/* Save the initial representation of the image so that it can be restored
	   by the normalButton, and used by make and reload JPEG.*/
	imageOriginal = img;
	/* If this is the first time an image has been loaded, then we 
	 * allow the window to take on the preferred size of the image,
	 * by not calling scrollPane.setPreferredSize. */
	if (firstImage) {
	    firstImage = false;
	    image = img;
	    // Set the first image. Making the image canvas maximum 80% of the screen height
	    scrollPane.setViewportView(new ImageCanvas
				       (img.getAsBufferedImage(), maxWidth, (int) (0.8 * maxHeight)));
	} else { /* If this is not the first image, then we keep the window size
		    constant by calling setImage.*/
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
	/* This method invocation causes the scrollPane's size to remain
	 * constant even though the ViewportView is replaced. This is done
	 * so that the window does not change size with each zoom operation.*/
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
	/* This JPEGStream constructor automatically encodes the image using the
	 * default quantization tables.*/
	jStream = new JPEGStream(image);
	setImage(jStream.getImage());
    }

    public void reloadJPEG() {
	if (image == null) {
	    System.out.println("Tried to convert null image to JPEG.");
	    System.exit(1);
	}
	setImage(imageOriginal);
	/* Here we specify the quantization tables specifically. JPEGStream.ziZag puts
	 * the tables into zigzag order which is expected by the JAI library.*/
	jStream = new JPEGStream();	
	jStream.encode(imageOriginal, JPEGStream.zigZag(lumaQTable), JPEGStream.zigZag(chromaQTable));
	setImage(jStream.getImage());
	viewer.pack();
	viewer.repaint();
    }
}
