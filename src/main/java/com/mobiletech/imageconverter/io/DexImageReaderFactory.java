package com.mobiletech.imageconverter.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.mobiletech.imageconverter.exception.ImageConverterException;
import com.mobiletech.imageconverter.readers.AnimGIFReader;
import com.mobiletech.imageconverter.readers.DexImageReader;
import com.mobiletech.imageconverter.readers.GIFReader;
import com.mobiletech.imageconverter.readers.GeneralImageIOReader;
import com.mobiletech.imageconverter.readers.JPEGImageReader;
import com.mobiletech.imageconverter.readers.PNGImageReader;
import com.mobiletech.imageconverter.vo.ImageConverterParams;

public class DexImageReaderFactory {
	public static DexImageReader getImageReader(ImageConverterParams imageParams) throws ImageConverterException{
		DexImageReader dexReader = null;
		
		if(imageParams.getInternalVariables().getOldFormat().equalsIgnoreCase("jpg") || imageParams.getInternalVariables().getOldFormat().equalsIgnoreCase("jpeg")){
			dexReader = new JPEGImageReader(imageParams.getImage());
		} else {
			ByteArrayInputStream imageStream = null;                
	        ImageInputStream iis = null;        
	        ImageReader ireader = null;		       
	                              
	        imageStream = new ByteArrayInputStream(imageParams.getImage());
	        
	        try {
				iis = ImageIO.createImageInputStream(imageStream);

		        Iterator readers = ImageIO.getImageReaders(iis);
		         
		        if(readers.hasNext()){
		            ireader = (ImageReader)readers.next();
		        } else {
		            throw new ImageConverterException(ImageConverterException.Types.READ_CODEC_NOT_FOUND,"No image readers found for the image type of the supplied image",null);
		        }
		         
		        ireader.setInput(iis,false); 
		        if(ireader.getFormatName().equalsIgnoreCase("png")){
		        	dexReader = new PNGImageReader(ireader, imageParams);
		        } else if(!ireader.getFormatName().equalsIgnoreCase("gif")){
		        	dexReader = new GeneralImageIOReader(ireader);
		        } else {
		        	int numImages = ireader.getNumImages(true);
		        	if(numImages > 1){
		        		dexReader = new AnimGIFReader(ireader, imageParams, numImages);
//		        		dexReader = new FlatteningAnimGifReader(ireader, imageParams, numImages);
		        	} else {
		        		dexReader = new GIFReader(ireader, imageParams);
		        	}
		        }
			} catch(IOException ioe){
		        throw new ImageConverterException(ImageConverterException.Types.IO_ERROR,"IOException thrown when reading from InputByteStream",ioe);
		    }
	         
		}
		return dexReader;
	}
}
