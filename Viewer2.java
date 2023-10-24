/*
 * Athabasca University
 * COMP435 - Multimedia Technologies
 * Nicholas O'Leary
 * 3466559
 * Assignment 2
 * File: Viewer2.java
 * Description: 
 *     An application which reads a bitmap file, converts it to a jpeg
 *     and displays both images side by side. Quantization tables for
 *     luminance and chrominance are displayed and can be manipulated
 *     by the user to alter the image output by the JPEG compression *    
 *
 * Based on: TopLevelDemo.java 
 * https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/uiswing/examples/components/TopLevelDemoProject/src/components/TopLevelDemo.java
 *           MenuLookDemo.java
 * https://docs.oracle.com/javase/tutorial/uiswing/examples/components/MenuLookDemoProject/src/components/MenuLookDemo.java
 *        JAISampleProgram.java
 * An example from the JAI Programming guide: ``Programming in Java Advanced Imaging''
 */

import components.ImagePane;
import components.MatrixPane;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

/*
 * CLASS Viewer2
 *
 * An extended JFrame which represents the application window.
 * 
 * attributes: 
 *
 * bmpImagePane           An ImagePane which manages the manipulation and display of the bitmap.
 * jpgImagePane           An ImagePane which manages the manipulation and display of the jpeg.
 * matrixPane             A MatrixPane which manages the quantization tables and their associated buttons.
 * componentPanel         A JPanel which groups the three panes together in a row.
 * menuBar                JMenuBar which consists of the menu and three buttons.
 * menu                   JMenu which consists of file and exit options.
 * fileMenuItem           Initiates a file open dialog to choose an image.
 * exitMenuItem           Exits the application.
 * saveMenuItem           Initiates a save file dialog to save the current jpeg.
 * screenWidth            An integer which stores the display screen width.
 * screenHeight           An integer which stores the display screen height.

 * methods:
 * getMaximumSize()       Returns the screen width and height.
 * Viewer2(string)        Constructor, initializes application components.
 * createAndShowGUI()     Method passed to swing dispatcher, instantiates and configures application.
 * main(String[])         Starts the application.
 */
public class Viewer2 extends JFrame {
    
    private ImagePane bmpImagePane;
    private ImagePane jpgImagePane;
    private MatrixPane matrixPane;
    private JPanel componentPanel;
    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem fileMenuItem;
    private JMenuItem exitMenuItem;
    private JMenuItem saveMenuItem;
    public int screenWidth;
    public int screenHeight;

    public Dimension getMaximumSize() {
	return new Dimension(screenWidth, screenHeight);	
    }
        
    public Viewer2(String title) {
	super(title);
	GraphicsDevice gd = getGraphicsConfiguration().getDevice();
	screenWidth = gd.getDisplayMode().getWidth();
	screenHeight = gd.getDisplayMode().getHeight();

	/* Screen height and width, and a reference to this JFrame are passed to the image
	 * panes so that the maximum size of the viewports can be set, and so that the image
	 * panes can call pack, and repaint on the JFrame. */ 
	bmpImagePane = new ImagePane("Original Image", screenWidth, screenHeight, this);
	jpgImagePane = new ImagePane("Converted Image", screenWidth, screenHeight, this);
	
	// Create the menu.
	menu = new JMenu("Menu");

	// Create file menu item and define its function.
	fileMenuItem = new JMenuItem("File");
	fileMenuItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    JFileChooser c = new JFileChooser();
		    FileNameExtensionFilter filter =
			new FileNameExtensionFilter("BMP Images", "bmp");
		    c.setFileFilter(filter);
		    int returnVal = c.showOpenDialog(fileMenuItem);
		    if (returnVal == JFileChooser.APPROVE_OPTION) {
			String fName = c.getSelectedFile().getAbsolutePath();
			bmpImagePane.setImage(fName);
			jpgImagePane.setImage(fName);
			/* Matrix pane buttons are disables on start up since there is
			 * no image data to manipulate. */
			matrixPane.enableButtons();
			/* pack() is called before converting the image to jpeg,
			 * because the image pane only sets the viewport size the 
			 * when setImage is called with a filename as an argument,
			 * in order to prevent the window from resizing when the image is 
			 * replaced during a zoom operation.
			 * If the call to pack() is omitted than the viewport will 
			 * not have the right size as makeJPEG calls setImage with
			 * a rendered op argument and the viewport size does
			 * not get configured correctly. */
			pack();
			jpgImagePane.makeJPEG();			
			pack();
			repaint();
		    }
		}
	    });

	// Create exit menu item and define its function.
	exitMenuItem = new JMenuItem("Exit");
	exitMenuItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    System.exit(0);
		}
	    });

	// Create save menu item and define its function
	saveMenuItem = new JMenuItem("Save");
	saveMenuItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    JFileChooser c = new JFileChooser();
		    FileNameExtensionFilter filter =
			new FileNameExtensionFilter("JPEG Images", "jpg", "jpeg");
		    c.setFileFilter(filter);
		    if (c.showSaveDialog(saveMenuItem) == JFileChooser.APPROVE_OPTION) {
			File file = c.getSelectedFile();
			/* If the user did not add the proper file extension, it is 
			 * added here.*/
			String name = file.getName();
			if (!(name.endsWith("jpg") || name.endsWith("jpeg"))) {
			    jpgImagePane.jStream.writeImage(new File(file.getAbsolutePath() + ".jpg"));

			} else {
			    jpgImagePane.jStream.writeImage(file);
			}
		    }			
		}
	    });

	// Add the menu items to the menu.
	menu.add(fileMenuItem);
	menu.add(saveMenuItem);				
	menu.add(exitMenuItem);

	// Create the menu bar.
	menuBar = new JMenuBar();
	menuBar.setOpaque(true);
	menuBar.setBackground(new Color(225, 225, 225));

	// Add menu and buttons to the menu bar.
	menuBar.add(menu);
	setJMenuBar(menuBar);

	/* Create the matrix pane, it needs references to the jpeg image pane
	 * and the JFrame to be able to call reloadJPEG, and pack/repaint.*/
	matrixPane = new MatrixPane("Quantization Matrix", jpgImagePane, this);
	// Set up the references for quantization tables in the jpeg image pane.
	jpgImagePane.setLumaQTable(matrixPane.lumaMatrix.getMatrix());
	jpgImagePane.setChromaQTable(matrixPane.chromaMatrix.getMatrix());

	// Create a JPanel to arrange the three panes.
	componentPanel = new JPanel();
	componentPanel.setLayout(new BoxLayout(componentPanel, BoxLayout.X_AXIS));
	componentPanel.add(bmpImagePane);
	componentPanel.add(jpgImagePane);
	componentPanel.add(matrixPane);
	add(componentPanel);
    }

    private static void createAndShowGUI() {
	Viewer2 v = new Viewer2("Image Viewer");
	v.setDefaultCloseOperation(EXIT_ON_CLOSE);
	v.pack();
	v.setVisible(true);
    }

    public static void main(String[] args) {
	javax.swing.SwingUtilities.invokeLater(new Runnable() {
		public void run() {
		    createAndShowGUI();
		}		    
	    });
    }
}
