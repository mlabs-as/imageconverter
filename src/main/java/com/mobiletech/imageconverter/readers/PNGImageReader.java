package com.mobiletech.imageconverter.readers;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageReader;

import com.mobiletech.imageconverter.exception.ImageConverterException;
import com.mobiletech.imageconverter.util.ImageUtil;
import com.mobiletech.imageconverter.vo.ImageConverterParams;

public class PNGImageReader implements DexImageReader{
	private ImageReader reader = null; // NEEDED 
	private ImageConverterParams imageParams = null; // NEEDED
	private int counter = 0;
	
	private PNGImageReader(){}
	
	public PNGImageReader(ImageReader reader, ImageConverterParams imageParams){
		this.reader = reader;
		this.imageParams = imageParams;
	}
	public void dispose(){
		reader.dispose();
	}
	public BufferedImage getNext() throws ImageConverterException{
		imageParams.getInternalVariables().setOkToBlur(true);
		counter++;
		BufferedImage image = null;

		try {
			image = reader.read(0);
			/*
			PNGMetadata pmd = null;			
			pmd = (PNGMetadata)reader.getImageMetadata(0);
			Object obj = pmd.getStandardTransparencyNode();	
			WritableRaster ar = null;
			ar = image.getAlphaRaster();
			*/
			if(imageParams.getFormat().equalsIgnoreCase("gif")){
				imageParams.getInternalVariables().setTransparentColor(ImageUtil.getUniqueColor(null,null));
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
