package com.mobiletech.imageconverter.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.mobiletech.imageconverter.ImageConverter;
import com.mobiletech.imageconverter.exception.ImageConverterException;
import com.mobiletech.imageconverter.readers.AnimGIFReader;
import com.mobiletech.imageconverter.readers.DexImageReader;
import com.mobiletech.imageconverter.readers.FlatteningAnimGifReader;
import com.mobiletech.imageconverter.readers.GIFReader;
import com.mobiletech.imageconverter.readers.GeneralImageIOReader;
import com.mobiletech.imageconverter.readers.JPEGImageReader;
import com.mobiletech.imageconverter.readers.PNGImageReader;
import com.mobiletech.imageconverter.vo.ImageConverterParams;

public class DexImageReaderFactory {
	public static DexImageReader getImageReader(ImageConverterParams imageParams) throws ImageConverterException{
		return getImageReaderInternal(imageParams,false);
	}
	public static DexImageReader getImageOverlayReader(ImageConverterParams imageParams) throws ImageConverterException{
		return getImageReaderInternal(imageParams,true);
	}
	private static DexImageReader getImageReaderInternal(ImageConverterParams imageParams, boolean isOverlay) throws ImageConverterException{
		DexImageReader dexReader = null;
		byte [] image = null;
		String oldFormat = null;
		
		if(isOverlay){
			image = imageParams.getOverlay();
			oldFormat = ImageConverter.getImageFormatName(image);
		} else {
			image = imageParams.getImage();
			oldFormat = imageParams.getInternalVariables().getOldFormat();
		}
				
		if(oldFormat.equalsIgnoreCase("jpg") || oldFormat.equalsIgnoreCase("jpeg")){
			dexReader = new JPEGImageReader(image);
		} else {
			ByteArrayInputStream imageStream = null;                
	        ImageInputStream iis = null;        
	        ImageReader ireader = null;		       
	                              
	        imageStream = new ByteArrayInputStream(image);
	        
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
		        	if(numImages > 1 && imageParams.getFormat().equalsIgnoreCase("gif")){
		        		if(imageParams.isFastMode()){
		        			dexReader = new AnimGIFReader(ireader, imageParams, numImages);	
		        		} else {
		        			dexReader = new FlatteningAnimGifReader(ireader, imageParams, numImages);	
		        		}		        				        	
		        		//dexReader = new AnalyzingAnimGifReader(ireader, imageParams, numImages);
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