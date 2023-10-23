 /*
 * updated and adapted viewer
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
			matrixPane.enableButtons();
			/* pack() is called before converting the image to jpeg,
			 * because the image pane only sets the viewport size the 
			 * when setImage is called with a filename as an argument.
			 * If the call to pack() is omitted than the viewport will 
			 * not have the right size as makeJPEG calls setImage with
			 * a rendered op argument and does the viewport size does
			 * not get configured correctly.			 */
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
			String name = file.getName();
			if (!(name.endsWith("jpg") || name.endsWith("jpeg"))) {
			    jpgImagePane.jStream.writeImage(new File(file.getAbsolutePath() + ".jpg"));

			} else {
			    jpgImagePane.jStream.writeImage(file);
			}
		    }
			
		}
	    }); // actionlistener

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

	// Create the matrix pane
	matrixPane = new MatrixPane("Quantization Matrix", jpgImagePane, this);
	jpgImagePane.setLumaQTable(matrixPane.lumaMatrix.getMatrix());
	jpgImagePane.setChromaQTable(matrixPane.chromaMatrix.getMatrix());
	

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
