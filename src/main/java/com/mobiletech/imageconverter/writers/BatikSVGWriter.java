package com.mobiletech.imageconverter.writers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

//import org.apache.batik.dom.GenericDOMImplementation;
//import org.apache.batik.svggen.SVGGraphics2D;
//import org.apache.batik.svggen.SVGGraphics2DIOException;
//import org.w3c.dom.DOMImplementation;
//import org.w3c.dom.Document;

import com.mobiletech.imageconverter.exception.ImageConverterException;

public class BatikSVGWriter implements DexImageWriter {
	/*
	private SVGGraphics2D svgGenerator = null;
	private ByteArrayOutputStream output = null;
	private Writer out = null;
	*/
	public byte[] getByte() throws ImageConverterException{
		/*
		output = new ByteArrayOutputStream();
		out = new OutputStreamWriter(output);
		
        try {
			svgGenerator.stream(out, true);  // true is for using CSS style attributes
			output.flush();
		} catch (SVGGraphics2DIOException e) {
			throw new ImageConverterException(ImageConverterException.Types.EMBEDDED_EXCEPTION,"Got exception when writing svg.",e);
		} catch (IOException e){
			throw new ImageConverterException(ImageConverterException.Types.EMBEDDED_EXCEPTION,"Got IOException when flushing byte array output stream.",e);
		}
		return output.toByteArray();
		*/
		return null;
	}
	
	public void dispose(){
		/*
		if(svgGenerator != null){
			svgGenerator.dispose();
		}
        try{             
        	if(out != null){
        		out.close();
        	}
        	if(output != null){
        		output.close();
        	}
        } catch(IOException ignored){}
        svgGenerator = null;
        out = null;
        output = null;
        */
	}
	public void writeNext(BufferedImage image) throws ImageConverterException{
		/*
		DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
		String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);
        svgGenerator = new SVGGraphics2D(document);
        ((Graphics2D)svgGenerator).drawImage(image, null, null);
        //Graphics2D g2 = null;
        //g2.drawImage(image, null, 0, 0);
         * */
         
	}
	public boolean canWriteMore(){
		return false;
	}
}
