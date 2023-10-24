/*
 * Athabasca University
 * COMP435 - Multimedia Technologies
 * Nicholas O'Leary
 * 3466559
 * Assignment 2
 * File: Matrix.java
 * Description: 
 *      Matrix class used to represent the quantization tables
 *      for the image and matrix panes. Handles the user interface for 
 *      one of the quantization tables.
 */

package components;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/*
 * CLASS Matrix
 * 
 * attributes
 * rows                        An array of JPanels which each holds 8 TextFields for each row of the table.
 * title                       A JLabel which displays the text ``Quantization Matrix''
 * matrix                      An array of integers holding the values of the elements of the table.
 * ip                          A reference to the image pane which will be using this table.
 *
 * methods
 * getMatrix                   An accessor method for the array of integers.
 * Matrix(String, ImagePane)   Constructor for creating an empty matrix.
 * update(int[])               Updates the integer values stored in matrix with the supplied values.
 *                             Also recreates the textfields with the updated values.
 * makeConstant()              Sets every element in the table equal to the first (DC) element.
 * makeDC()                    Sets every elemnt other than the first element to 256.
 *
 */
public class Matrix extends JPanel {
    private JPanel[] rows;
    private JLabel title;
    private int[] matrix;
    private ImagePane ip;

    public int[] getMatrix() {
	return matrix;
    }
    
    // Constructor for creating an empty matrix
    public Matrix(String title, ImagePane ip) {
	matrix = new int[64];
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	this.title = new JLabel(title);
	this.ip = ip;
	this.title.setAlignmentX(Component.LEFT_ALIGNMENT);
	add(this.title);
	rows = new JPanel[8];
	for(int i = 0; i < 8; i++) {
	    rows[i] = new JPanel();
	    rows[i].setLayout(new BoxLayout(rows[i], BoxLayout.X_AXIS));
	    for(int j = 0; j < 8; j++) {
		JTextField tf = new JTextField();		    
		rows[i].add(tf);
	    }
	    add(rows[i]);
	}	    
    }

    public void update(int[] values) {	    
	for(int i = 0; i < 8; i++) {
	    // This also removes the Layout Manager for this component.
	    rows[i].removeAll();
	    // So we create a new Layout Manager.
	    rows[i].setLayout(new BoxLayout(rows[i], BoxLayout.X_AXIS));

	    /* Copy the content of values into matrix
	    *  and update the TextFields.*/
	    for(int j = 0; j < 8; j++) {
		matrix[8 * i + j] = values[8 * i + j];
		JTextField tf = new JTextField(String.valueOf(values[8 * i + j]));
		final int index = 8 * i + j;
		tf.addActionListener(new ActionListener() {
			/* This handles accepting input in the TextField.
			 * the values in the quantization tables used by JPEG are
			 * 8-bit integers, and cannot be zero, so only values
			 * between 1 and 256 are accepted. The user must hit
			 * the enter key in order for this callback function to 
			 * be executed.
			 */
			public void actionPerformed(ActionEvent e) {			    
			    int currentValue = matrix[index];
			    int input = 0;
			    try {
				input = Integer.parseInt(tf.getText());
			    } catch (NumberFormatException exc) {} // Reject any non-numeric values.
			    if ((input > 0) && (input < 257)) {
				matrix[index] = input;
			    } else {
				matrix[index] = currentValue;
				tf.setText(String.valueOf(currentValue));
			    }
			    ip.reloadJPEG();
			}
		    }); // ActionListener
		rows[i].add(tf);
	    } // for(int j ...
	    rows[i].validate();
	    rows[i].repaint();
	} // for(int i ...
	validate();
	repaint();
    }

    /*
     * Set all the values in the table equal to the
     * first value in the table.
     */
    public void makeConstant() {
	int firstElement = matrix[0];
	for (int i = 0; i < 64; i++)
	    matrix[i] = firstElement;
	update(matrix);
    }

    /*
     * Set all the values in the table other than the first 
     * value to 256 (effectively reducing the non DC components
     * of the DCT to 0).
     */
    public void makeDC() {
	for (int i = 1; i < 64; i++) {
	    matrix[i] = 256;
	}
	update(matrix);
    }	
} 
