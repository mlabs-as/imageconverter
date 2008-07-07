package com.mobiletech.imageconverter.readers;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageReader;

import com.mobiletech.imageconverter.exception.ImageConverterException;
import com.mobiletech.imageconverter.util.ImageUtil;
import com.sun.imageio.plugins.png.PNGMetadata;

public class GeneralImageIOReader implements DexImageReader{
	private ImageReader reader = null; // NEEDED 
	private int counter = 0;
	
        public String getFormat(){
            String format = null;
            if(reader != null){
                try {
                format =  reader.getFormatName();
                } catch(IOException io){
                    // Ignoring this
                }
            } 
            return format;
        }
        
	private GeneralImageIOReader(){}
	
	public GeneralImageIOReader(ImageReader reader){
		this.reader = reader;
	}
	public void dispose(){
		reader.dispose();
	}
	public BufferedImage getNext() throws ImageConverterException{
		counter++;
		BufferedImage image = null;

		try {
			image = reader.read(0);

	        // this is for some known problem images that have been processed in photoshop
	        // they will cause crashes and/or get wrong colors unless they are converted to
	        // have a known image type, this conversion can be quite time consuming for some of these
	        // images and will reduce the filesize in some cases
	        if(image.getType() == 0){
	        	image = ImageUtil.toBuffImageRGBorARGB(image);
	        }       
		} catch(IOException ioe){
	        throw new ImageConverterException(ImageConverterException.Types.IO_ERROR,"IOException thrown when reading from InputByteStream",ioe);
	    }
		return image;
	}
	public boolean hasMore(){
		return counter < 1;		
	}
}
