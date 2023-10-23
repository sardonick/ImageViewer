/*
 * Matrix class used to represent the quantization matrices
 * for the image and matrix panes.
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

public class Matrix extends JPanel {
    private JPanel[] rows;
    private JLabel title;
    private int[] matrix;
    private ImagePane ip;

    public int[] getMatrix() {
	return matrix;
    }

    // Constructor for creating a matrix with given values.
    public Matrix(String title, int[] values) {
	matrix = new int[64];
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	this.title = new JLabel(title);
	this.title.setAlignmentX(Component.LEFT_ALIGNMENT);
	add(this.title);
	rows = new JPanel[8];
	update(values);
	/*
	  for(int i = 0; i < 8; i++) {
	  rows[i] = new JPanel();
	  rows[i].setLayout(new BoxLayout(rows[i], BoxLayout.X_AXIS));
	  for(int j = 0; j < 8; j++) {
	  JTextField tf = new JTextField(String.valueOf(values[8 * i + j]));
	  rows[i].add(tf);
	  }
	  add(rows[i]);
	  }
	*/
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
	    for(int j = 0; j < 8; j++) {
		matrix[8 * i + j] = values[8 * i + j];
		JTextField tf = new JTextField(String.valueOf(values[8 * i + j]));
		final int index = 8 * i + j;
		tf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    int currentValue = matrix[index];
			    int input = 0;
			    try {
				input = Integer.parseInt(tf.getText());
			    } catch (NumberFormatException exc) {}
			    if ((input > 0) && (input < 257)) {
				matrix[index] = input;
			    } else {
				matrix[index] = currentValue;
				tf.setText(String.valueOf(currentValue));
			    }
			    //TODO call a function to re-encode the image
			    ip.reloadJPEG();
			}
		    });
		rows[i].add(tf);
	    }
	    rows[i].validate();
	    rows[i].repaint();
	}
	validate();
	repaint();
    }

    public void makeConstant() {
	int firstElement = matrix[0];
	for (int i = 0; i < 64; i++)
	    matrix[i] = firstElement;
	update(matrix);
    }

    public void makeDC() {
	for (int i = 1; i < 64; i++) {
	    matrix[i] = 255;
	}
	update(matrix);
    }
	
} // class Matrix
