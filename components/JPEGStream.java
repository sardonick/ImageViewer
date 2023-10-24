/*
 * Athabasca University
 * COMP435 - Multimedia Technologies
 * Nicholas O'Leary
 * 3466559
 * Assignment 2
 * File: JPEGStream.java
 * Description: 
 *      A class that converts a bitmap image into a jpeg, provides
 *      constant definitions for the default quantization tables, and 
 *      a method for writing a JPEG file to disk.
 *
 * Based on: ``JPEGWriterTest.java'' from Programming in Java Advanced Imaging
 */

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
/*
 * CLASS JPEGStream
 *
 * attributes
 * defaultLumaQTable     A constant array of ints which is the canonical source of the default 
 *                       luminance quantization table for the application.
 * defaultChromaQTable   A constant array of ints which is the canonical source of the default 
 *                       chrominance quantization table for the application.
 * encodeParam           A JPEGEncodeParam object used to control the encoding done by JAI.
 * bOutputStream         A ByteArrayOutputStream where the encoder writes the encoded image data.
 * bStream               A ByteArraySeekableStream which inputs the encoded image data to JAI.create
 *                       in order to obtain a planar image/ rendered op of the JPEG image.
 * lumaQTable            The luminance quantization table used for encoding.
 * chromaQTable          The chrominance quantization table used for encoding.
 * 
 * methods
 * zigZag(int[])                      A static method for transforming a quantization table into zig-zag order.
 * encode(PlanarImage, int[], int[])  Encodes the PlanarImage as a JPEG using the quantization table arguments.
 *                                    The resulting stream is stored in bOutputStream.
 * JPEGStream()                       Default constructor.
 * JPEGStream(PlanarImage)            Constructor which calls encode with the default quantization table values.
 * getImage()                         Returns a RenderedOp created from the JPEG data using bStream.
 * writeImage(File)                   Writes the JPEG data stored in bOutputStream to a file.
 *
 */
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
	encodeParam = new JPEGEncodeParam();
	encodeParam.setLumaQTable(zigZag(lumaQT));
	encodeParam.setChromaQTable(zigZag(chromaQT));

	if (!encodeParam.isQTableSet(0)) {
	    System.out.println("encode param lumaninace Q table is not set.");
	    System.exit(1);
	}
	if (!encodeParam.isQTableSet(1)) {
	    System.out.println("encode param chrominance Q table is not set.");
	    System.exit(1);
	}
	
	bOutputStream = new ByteArrayOutputStream();
	ImageEncoder enc = ImageCodec.createImageEncoder("JPEG", bOutputStream, encodeParam);
	try {
	    enc.encode(img);
	    bStream = new ByteArraySeekableStream(bOutputStream.toByteArray());	    
	} catch(IOException e) {
	    System.out.println("IOException in JPEGStream");
	    System.exit(1);
	}
    }

    public JPEGStream() {	
    }
    
    public JPEGStream(PlanarImage img) {
	encode(img, defaultLumaQTable, defaultChromaQTable);
    }    
    
    public RenderedOp getImage() {
	RenderedOp res = JAI.create("stream", bStream);
	return res;
    }

    public void writeImage(File file) {
	try{
	    bOutputStream.writeTo(new FileOutputStream(file));
	} catch(IOException e) {
	    System.out.println("Problem creating JPEG");
	    System.exit(1);
	}
    }
}
