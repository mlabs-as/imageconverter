package com.mobiletech.imageconverter.writers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import com.mobiletech.imageconverter.exception.ImageConverterException;
import com.mobiletech.imageconverter.vo.ImageConverterParams;
import com.sun.imageio.plugins.gif.GIFImageMetadata;

public class AnimGifWriter implements DexImageWriter{
	private byte[] result= null;
	private ImageWriter writer = null; // NEEDED
	private ImageOutputStream ios = null;
	private ByteArrayOutputStream output = null;
	private ImageConverterParams imageParams = null;
	private int counter = 0;
	
	private AnimGifWriter(){}
	
	public AnimGifWriter(ImageWriter writer, ImageConverterParams imageParams){
		output = new ByteArrayOutputStream();
		this.writer = writer;
		this.imageParams = imageParams;
		this.initialize();
	}
	public boolean canWriteMore(){
		return true;
	}
	public byte[] getByte() throws ImageConverterException{
		try {
			writer.endWriteSequence();                  
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
		if(writer != null){
			writer.dispose();
			writer = null;
		}
        closeStreams();    		
	}
	private void closeStreams(){
        try{                
            ios.close();
            output.close();
        } catch(IOException ignored){}
        writer = null;
        ios = null;
        output = null;
	}
	private void initialize(){
        try {
			ios = ImageIO.createImageOutputStream(output);      
			writer.setOutput(ios);        
			writer.prepareWriteSequence(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void writeNext(BufferedImage image) throws ImageConverterException{
		GIFImageMetadata metaData =  imageParams.getInternalVariables().getImageMetadata()[counter];
		try {                               
			writer.writeToSequence(new IIOImage(image,null,metaData),writer.getDefaultWriteParam());                                      
        } catch(IOException e){
        	closeStreams();
            throw new ImageConverterException(ImageConverterException.Types.IO_ERROR,"IOException thrown when writing encoded image ",e);
        }        
        counter++;
	}
	
}
