package com.mobiletech.imageconverter.readers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageReader;

import com.mobiletech.imageconverter.exception.ImageConverterException;
import com.mobiletech.imageconverter.util.ImageUtil;
import com.mobiletech.imageconverter.vo.ImageConverterParams;
import com.sun.imageio.plugins.gif.GIFImageMetadata;
import com.sun.imageio.plugins.gif.GIFStreamMetadata;

public class GIFReader implements DexImageReader{
	private ImageReader reader = null; // NEEDED 
	private ImageConverterParams imageParams = null;
	private int counter = 0;
	
	private GIFReader(){}
	
	public GIFReader(ImageReader reader, ImageConverterParams imageParams){
		this.reader = reader;
		this.imageParams = imageParams;
	}
	
	public void dispose(){
		reader.dispose();
	}
	public BufferedImage getNext() throws ImageConverterException{
		imageParams.getInternalVariables().setOkToBlur(false);
		counter++;
        IIOImage iioimage = null;
        GIFStreamMetadata streamMetaData = null;
        try {
        	iioimage = reader.readAll(0, null);
			streamMetaData = (GIFStreamMetadata)reader.getStreamMetadata();
		} catch(IOException ioe){
	        throw new ImageConverterException(ImageConverterException.Types.IO_ERROR,"IOException thrown when reading from InputByteStream",ioe);
	    }	

        BufferedImage resultImage = new BufferedImage(streamMetaData.logicalScreenWidth, streamMetaData.logicalScreenHeight, BufferedImage.TYPE_INT_ARGB);            
  
        Color transparentColor = null;
      
        imageParams.getInternalVariables().setImageMetadata(new GIFImageMetadata[1]);
        
        // Storing metadata for later use
        GIFImageMetadata [] metaTable = imageParams.getInternalVariables().getImageMetadata();                               
        metaTable[0] = (GIFImageMetadata)iioimage.getMetadata(); 
        
        RenderedImage r = iioimage.getRenderedImage();         
        int numEntries = 0;
        byte [] colorTable = streamMetaData.globalColorTable;
        
        if(colorTable == null){
        	if(metaTable[0].localColorTable != null){
                numEntries = metaTable[0].localColorTable.length/3;
                colorTable = metaTable[0].localColorTable;
                for (int e = 0; e < numEntries; e++) {
                    if(e == metaTable[0].transparentColorIndex) {
                        int r1 = colorTable[3*e] & 0xff; 
                        int g1 = colorTable[3*e + 1] & 0xff; 
                        int b1 = colorTable[3*e + 2] & 0xff; 
                        transparentColor = new Color(r1,g1,b1);   
                        imageParams.getInternalVariables().setTransparentColor(transparentColor);
                        break; 
                    }
                }
            }         	
        } else {
        	numEntries = streamMetaData.globalColorTable.length/3;
	        if(metaTable[0].transparentColorFlag){
	            if(streamMetaData.globalColorTable != null){                                           
	                for (int e = 0; e < numEntries; e++) {
	                    if(e == metaTable[0].transparentColorIndex) {
	                        int r1 = colorTable[3*e] & 0xff; 
	                        int g1 = colorTable[3*e + 1] & 0xff; 
	                        int b1 = colorTable[3*e + 2] & 0xff; 
	                        transparentColor = new Color(r1,g1,b1);   
	                        imageParams.getInternalVariables().setTransparentColor(transparentColor);  
	                        break; 
	                    }                
	                }
	            }
	        }    
        }
        if(transparentColor != null){
            for (int e = 0; e < numEntries; e++) {
                if(e == metaTable[0].transparentColorIndex) {
                    continue;
                }
                if(transparentColor.getRed() == (colorTable[3*e] & 0xff) &&
                        transparentColor.getGreen() == (colorTable[3*e+1] & 0xff) &&
                        transparentColor.getBlue() == (colorTable[3*e+2] & 0xff)){  
                    imageParams.getInternalVariables().setTransparentColor(ImageUtil.getUniqueColor(colorTable,transparentColor));
                    imageParams.setNumberOfColors(6868);
                    break;
                }
            }
        }
                                                                                    
         Graphics2D resG = resultImage.createGraphics();
         if(metaTable[0].imageLeftPosition != 0 || metaTable[0].imageTopPosition != 0){
             resG.drawRenderedImage(r, AffineTransform.getTranslateInstance(metaTable[0].imageLeftPosition, metaTable[0].imageTopPosition));
         } else {
             resG.drawRenderedImage(r,null);
         }
         
         resG.dispose();
         resG = null;                     

         metaTable[0].imageLeftPosition = 0;
         metaTable[0].imageTopPosition = 0;
         r = null;
        imageParams.getInternalVariables().setImageMetadata(metaTable);

		return resultImage;
	}
	
	public boolean hasMore(){		
		return counter < 1;
	}
}
