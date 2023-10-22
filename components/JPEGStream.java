/*
 * A class that converts a bitmap image into a jpeg.
 *
 * Based on: ``JPEGWriterTest.java'' from Programming in Java Advanced Imaging
 */

// 1. Take a rendered image and encode it into a stream
// 2. Create a new buffered/rendered image from the jpeg encoded stream
// ^^ use the ``stream'' op
// 3. optionally write the stream to a file.

package components;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.JPEGEncodeParam;
import com.sun.media.jai.codec.ByteArraySeekableStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;

public class JPEGStream {
    /* The JAI image encoder expects these arrays to be 
     * in zig-zag order. This is how the arrays would
     * appear if they were in conventional order.
    */
    public static final int[] defaultLumaQTable = {
	16,  11,  10,  16,  24,  40,  51,  61,
	12,  12,  14,  19,  26,  58,  60,  55,
	14,  13,  16,  24,  40,  57,  69,  56,
	14,  17,  22,  29,  51,  87,  80,  62,
	18,  22,  37,  56,  68, 109, 103,  77,
	24,  35,  55,  64,  81, 104, 113,  92,
	49,  64,  78,  87, 103, 121, 120,  101,
	72,  92,  95,  98, 112, 100, 103,   99
    };
    public static final int[] defaultChromaQTable = {
	17,  18,  24,  47,  99,  99,  99,  99,
	18,  21,  26,  66,  99,  99,  99,  99,
	24,  26,  56,  99,  99,  99,  99,  99,
	47,  66,  99,  99,  99,  99,  99,  99,
	99,  99,  99,  99,  99,  99,  99,  99,
	99,  99,  99,  99,  99,  99,  99,  99,
	99,  99,  99,  99,  99,  99,  99,  99,
	99,  99,  99,  99,  99,  99,  99,  99,
    };
    
    private JPEGEncodeParam encodeParam;
    private ByteArrayOutputStream bOutputStream;
    private ByteArraySeekableStream bStream;

    // Initialize this with the default values from table 9.1
    private int[] lumaQTable = {
	16,  11,  10,  16,  24,  40,  51,  61,
	12,  12,  14,  19,  26,  58,  60,  55,
	14,  13,  16,  24,  40,  57,  69,  56,
	14,  17,  22,  29,  51,  87,  80,  62,
	18,  22,  37,  56,  68, 109, 103,  77,
	24,  35,  55,  64,  81, 104, 113,  92,
	49,  64,  78,  87, 103, 121, 120,  101,
	72,  92,  95,  98, 112, 100, 103,   99
    };

    // Initialize this with the default values from table 9.2
    private int[] chromaQTable  = {
	17,  18,  24,  47,  99,  99,  99,  99,
	18,  21,  26,  66,  99,  99,  99,  99,
	24,  26,  56,  99,  99,  99,  99,  99,
	47,  66,  99,  99,  99,  99,  99,  99,
	99,  99,  99,  99,  99,  99,  99,  99,
	99,  99,  99,  99,  99,  99,  99,  99,
	99,  99,  99,  99,  99,  99,  99,  99,
	99,  99,  99,  99,  99,  99,  99,  99,
    };
    //REMOVE later, want encode to automatically update the tables.
    public void setLumaQTable(int[] values) {
	lumaQTable = values;
    }
    // ditto REMOVE
    public void setChromaQTable(int[] values) {
	chromaQTable = values;
    }

    /*
    public void printQTable(int[] table) {
	for(int i = 0; i < 8; i++) {
	    for (int j = 0; j < 8; j++) {
		System.out.print(table[8*i+j] + " ");
	    }
	    System.out.print("\n");
	}
    }
    */

    /*
     * Transforms the table argument from conventional
     * order to zig zag order for the image encoder.
     */ 
    public static int[] zigZag(int[] table) {
	int[] indices = {
	     0,  1,  5,  6, 14, 15, 27, 28,
	     2,  4,  7, 13, 16, 26, 29, 42,
	     3,  8, 12, 17, 25, 30, 41, 43,
	     9, 11, 18, 24, 31, 40, 44, 53,
	    10, 19, 23, 32, 39, 45, 52, 54,
	    20, 22, 33, 38, 46, 51, 55, 60,
	    21, 34, 37, 47, 50, 56, 59, 61,
	    35, 36, 48, 49, 57, 58, 62, 63
	};
	int[] result = new int[64];

	for(int i = 0; i < 64; i++) {
	    result[i] = table[indices[i]];
	}
	return result;
    }


    public void encode(PlanarImage img, int[] lumaQT, int[] chromaQT) {
	/*
	setLumaQTable(lumaQT);
	setChromaQTable(chromaQT);
	*/
	lumaQTable = lumaQT;
	chromaQTable = chromaQT;
	encodeParam = new JPEGEncodeParam();
	encodeParam.setLumaQTable(zigZag(lumaQTable));
	encodeParam.setChromaQTable(zigZag(chromaQTable));

	if (!encodeParam.isQTableSet(0)) {
	    System.out.println("encode param lumaninace Q table is not set.");
	    System.exit(1);
	}
	if (!encodeParam.isQTableSet(1)) {
	    System.out.println("encode param chrominance Q table is not set.");
	    System.exit(1);
	}
	/*
	System.out.println("Luma table");
	printQTable(encodeParam.getQTable(0));
	System.out.println("Chroma table");
	printQTable(encodeParam.getQTable(1));
	*/
	bOutputStream = new ByteArrayOutputStream();
	ImageEncoder enc = ImageCodec.createImageEncoder("JPEG", bOutputStream, encodeParam);
	try {
	    enc.encode(img);
	    // protected byte[] buf inherited from ByteArrayOutputStream
	    bStream = new ByteArraySeekableStream(bOutputStream.toByteArray());	    
	} catch(IOException e) {
	    System.out.println("IOException in JPEGStream");
	    System.exit(1);
	}
    }

    public JPEGStream() {	
    }
    
    public JPEGStream(PlanarImage img) {
	encode(img, lumaQTable, chromaQTable);
	/*
	// set up the encodeParam variable;
	encodeParam = new JPEGEncodeParam();
	encodeParam.setLumaQTable(lumaQTable);
	encodeParam.setChromaQTable(chromaQTable);
	ImageEncoder enc = ImageCodec.createImageEncoder("JPEG", this, encodeParam);
	try {
	    enc.encode(img);
	    // protected byte[] bug inherited from ByteArrayOutputStream}
	    bStream = new ByteArraySeekableStream(buf);	    
	} catch(IOException e) {
	    System.out.println("IOException in ToJpeg");
	    System.exit(1);
	}
	*/
    }    
    
    public RenderedOp getImage() {
	RenderedOp res = JAI.create("stream", bStream);
	return res;
    }

    public void writeImage(File file) {
	try{
	    // methood inherited from ByteArrayOutputStream
	    bOutputStream.writeTo(new FileOutputStream(file));
	} catch(IOException e) {
	    System.out.println("Problem creating JPEG");
	    System.exit(1);
	}
    }
    /*    
    public static void main(String[] args) {
	// Test encode
	PlanarImage img = JAI.create("fileload", "flower.bmp");
	JPEGStream tj = new JPEGStream(img);
	// Test getImage
	RenderedOp ro = tj.getImage();
	// test writeImage
	tj.writeImage("flower.jpg");
    }
    */
}
