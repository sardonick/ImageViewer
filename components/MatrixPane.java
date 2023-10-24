/*
 * Athabasca University
 * COMP435 - Multimedia Technologies
 * Nicholas O'Leary
 * 3466559
 * Assignment 2
 * File: MatrixPane.java
 * Description: 
 *      An extended JPanel which holds the components in the right hand side of the window.
 *      Consists of a title, two Matrix instances to handle the luminanace and quantization 
 *      tables, and a row of buttons.
 *
 */

package components;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/*
 * CLASS MatrixPane
 *
 * attributes
 * title           A JLabel that displays the text ``Quantization Matrix''
 * defaultButton   A JButton that sets the tables to their default values.
 * constantButton  A JButton that sets every element in the tables equal to their first element.
 * dcButton        A JButton that sets every element other than the DC component to 256.
 * buttonPanel     A JPanel that groups the buttons into a row.
 * jpgImagePane    A reference to the ImagePane with the jpeg image.
 * parent          A reference to the Viewer2 JFrame instance.
 * lumaMatrix      The Matrix used for the luminance quantization table.
 * chromaMatrix    The Matrix used for the chrominance quantization table.
 *
 * methods
 * getMaximumSize()   Returns a Dimension object that restricts this pane to 1/5th of the window.
 * resetQTables()     Resets the lumaMatrix and chromaMatrix to the default JPEG values.
 * enableButtons()    Enables the buttons in the button row. Called when the first image is loaded.
 * MatrixPane(String, ImagePane, JFrame) Constructor, initializes component.
 *     
 */
public class MatrixPane extends JPanel {
    
    private JLabel title;
    private JButton defaultButton;
    private JButton constantButton;
    private JButton dcButton;
    private JPanel buttonPanel;
    private ImagePane jpgImagePane;
    private JFrame parent;
    public Matrix lumaMatrix;
    public Matrix chromaMatrix;

    /*
     * Restrict the size of this panel to 1/5th the window.
     */
    public Dimension getMaximumSize() {
	Rectangle r = parent.getBounds();
	double w = r.width * 0.2;
	return new Dimension((int)w, r.height);
    }

    private void resetQTables() {
	lumaMatrix.update(JPEGStream.defaultLumaQTable);
	chromaMatrix.update(JPEGStream.defaultChromaQTable);	
    }

    public void enableButtons() {
	defaultButton.setEnabled(true);
	constantButton.setEnabled(true);
	dcButton.setEnabled(true);
    }

    public MatrixPane(String title, ImagePane ip, JFrame v) {
	this.title = new JLabel(title);
	this.title.setAlignmentX(Component.RIGHT_ALIGNMENT);
	jpgImagePane = ip;
	parent = v;

	// Create default button
	defaultButton = new JButton("Default");
	defaultButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    resetQTables();
		    jpgImagePane.reloadJPEG();
		}
	    });
	defaultButton.setEnabled(false);
	
	// Create constant button
	constantButton = new JButton("Constant");
	constantButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    lumaMatrix.makeConstant();
		    chromaMatrix.makeConstant();
		    jpgImagePane.reloadJPEG();	
		}
	    });	
	constantButton.setEnabled(false);
	
	// Create dc only button
	dcButton = new JButton("DC only");
	dcButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    lumaMatrix.makeDC();
		    chromaMatrix.makeDC();
		    jpgImagePane.reloadJPEG();	
		}
	    });
	dcButton.setEnabled(false);

	// Create Button Panel
	buttonPanel = new JPanel();
	buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
	buttonPanel.add(defaultButton);
	buttonPanel.add(constantButton);
	buttonPanel.add(dcButton);

	// Group the panel components together vertically.
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	add(this.title);

	// Create the quantization table Matrix instances.
	lumaMatrix = new Matrix("Luminance:", jpgImagePane);
	chromaMatrix = new Matrix("Chrominance:", jpgImagePane);

	// Add the components to the panel.
	add(lumaMatrix);
	add(chromaMatrix);
	add(buttonPanel);
    }
}
