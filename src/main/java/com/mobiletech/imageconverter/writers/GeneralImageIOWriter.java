package com.mobiletech.imageconverter.writers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import com.mobiletech.imageconverter.exception.ImageConverterException;

public class GeneralImageIOWriter implements DexImageWriter{
	private byte[] result= null;
	private ImageWriter writer = null; // NEEDED
	private ImageOutputStream ios = null;
	private ByteArrayOutputStream output = null;

	private GeneralImageIOWriter(){}
	
	public GeneralImageIOWriter(ImageWriter writer){
		this.writer = writer;
	}
	public boolean canWriteMore(){
		return false;
	}
	public byte[] getByte() throws ImageConverterException{
		try {              
			ios.flush();
			output.flush();
			result = output.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	public void dispose(){
		writer.dispose();
        try{             
        	if(ios != null){
        		ios.close();
        	}
        	if(output != null){
        		output.close();
        	}
        } catch(IOException ignored){}
        writer = null;
        ios = null;
        output = null;
	}
	public void writeNext(BufferedImage image) throws ImageConverterException{
		output = new ByteArrayOutputStream();
		try{
            ios = ImageIO.createImageOutputStream(output);                  
            writer.setOutput(ios);
            writer.write(image);
            ios.flush();                
        } catch(IOException ioe){
        	dispose();
            throw new ImageConverterException(ImageConverterException.Types.IO_ERROR,"IOException thrown when writing encoded image ",ioe);            
        } 
	}
}
