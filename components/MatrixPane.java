/*
 * The Quantization matrix component of the application.
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
import javax.swing.JTextField;

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
		    v.pack();
		    v.repaint();
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
		    jpgImagePane.validate();
		    jpgImagePane.repaint();
		    v.pack();
		    v.repaint();
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
		    v.pack();
		    v.repaint();
		}
	    });
	dcButton.setEnabled(false);

	// Create Button Panel
	buttonPanel = new JPanel();
	buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
	buttonPanel.add(defaultButton);
	buttonPanel.add(constantButton);
	buttonPanel.add(dcButton);

	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	add(this.title);
	// TODO Add the matrices
	lumaMatrix = new Matrix("Luminance:");
	chromaMatrix = new Matrix("Chrominance:");

	//resetQTables();
		
	add(lumaMatrix);
	add(chromaMatrix);
	add(buttonPanel);
    }
}
