package com.mobiletech.imageconverter.writers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.mobiletech.imageconverter.exception.ImageConverterException;
import com.mobiletech.imageconverter.vo.ImageConverterParams;
import com.sun.media.jai.codec.JPEGEncodeParam;
import com.sun.media.jai.codecimpl.JPEGCodec;
import com.sun.media.jai.codecimpl.JPEGImageEncoder;

public class JPEGImageWriter implements DexImageWriter {
	private byte[] result= null;
	private ImageConverterParams params = null;
	private ByteArrayOutputStream output = null;
	
	private JPEGImageWriter(){}
	
	public JPEGImageWriter(ImageConverterParams params){
		this.params = params;
	}
	public boolean canWriteMore(){
		return false;
	}
	public byte[] getByte() throws ImageConverterException{
		try {              
			output.flush();
			result = output.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	public void dispose(){
        try{                
            output.close();
        } catch(IOException ignored){}
        output = null;
	}
	public void writeNext(BufferedImage image) throws ImageConverterException{
		output = new ByteArrayOutputStream();
		JPEGImageEncoder enc = JPEGCodec.createJPEGEncoder(output);
        
        if(params.getJPEGCompressionQuality() != 0){                
            JPEGEncodeParam param = JPEGCodec.getDefaultJPEGEncodeParam(image);
            if(params.getJPEGCompressionQuality() > 0){
                param.setQuality(params.getJPEGCompressionQuality());
            }
            //if(params.useSubsampling()){
                param.setHorizontalSubsampling(0, 1); 
                param.setHorizontalSubsampling(1, 1); 
                param.setHorizontalSubsampling(2, 1); 
                param.setVerticalSubsampling(0, 1); 
                param.setVerticalSubsampling(1, 1); 
                param.setVerticalSubsampling(2, 1);
            //}
            enc.setJPEGEncodeParam(param);
        }
        try {
            enc.encode(image);
        } catch (IOException e) {
            throw new ImageConverterException(ImageConverterException.Types.IO_ERROR,"IOException thrown when writing encoded image ",e);
        } finally {
            enc = null;
        }
	}
}
