package com.mobiletech.imageconverter.io;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;

import org.w3c.dom.NodeList;

import com.mobiletech.imageconverter.exception.ImageConverterException;
import com.mobiletech.imageconverter.filters.JPEGApp14Filter;
import com.mobiletech.imageconverter.util.ImageUtil;
import com.mobiletech.imageconverter.vo.ImageConverterParams;
import com.sun.imageio.plugins.gif.GIFImageMetadata;
import com.sun.imageio.plugins.gif.GIFStreamMetadata;

public class ImageDecoder {
    public static BufferedImage [] readImages(byte [] inByteArray,ImageConverterParams imageParams) throws ImageConverterException{   
            /*
             * Filtering of the App14 segment for JPEG files only, the App14 segment contains photoshop information
             * this segment causes problems with the java jpeg reader in some cases
             */
            if(imageParams.getInternalVariables().getOldFormat().equalsIgnoreCase("jpg") 
                    || imageParams.getInternalVariables().getOldFormat().equalsIgnoreCase("jpeg")){
                inByteArray = JPEGApp14Filter.filter(inByteArray);
            }
        ByteArrayInputStream imageStream = null;                
        ImageInputStream iis = null;        
        ImageReader reader = null;

        BufferedImage [] finishedImages = null;
        
        try {                                               
            imageStream = new ByteArrayInputStream(inByteArray);
            iis = ImageIO.createImageInputStream(imageStream);
            
            Iterator readers = ImageIO.getImageReaders(iis);
            
            if(readers.hasNext()){
                reader = (ImageReader)readers.next();
            } else {
                throw new ImageConverterException(ImageConverterException.Types.READ_CODEC_NOT_FOUND,"No image readers found for the image type of the supplied image",null);
            }
            
            reader.setInput(iis,false); 

            // for all formats except gif
            if(!imageParams.getFormat().equalsIgnoreCase("gif") || !reader.getFormatName().equalsIgnoreCase("gif")){
                finishedImages = new BufferedImage[1];
                try{
                	finishedImages[0] = reader.read(0);
                } catch (IOException e){
                	// If this was an unsupported jpeg image, it could have been CMYK or YCCK, these are not supported by
                	// ImageIO, we can try to load the image using thrid party code.
                	if(false){//(reader.getFormatName().equalsIgnoreCase("jpeg") || reader.getFormatName().equalsIgnoreCase("jpg")) && e.getMessage().equalsIgnoreCase("Unsupported Image Type")){
                		IIOMetadata metadata = reader.getImageMetadata(0);
                		String metadataFormat = metadata.getNativeMetadataFormatName() ;
                		IIOMetadataNode iioNode = (IIOMetadataNode)metadata.getAsTree(metadataFormat);

                		NodeList children = iioNode.getElementsByTagName("app14Adobe") ;
                		if ( children.getLength() > 0 ) {
                			iioNode = (IIOMetadataNode) children.item(0) ;
                			int transform = Integer.parseInt(iioNode.getAttribute("transform")) ;
                			Raster raster = reader.readRaster(0, reader.getDefaultReadParam()) ;

                			finishedImages[0] = createJPEG4(raster,transform) ;
                		}
                	} else {
                		throw e;
                	}
                }
                // this is for some known problem images that have been processed in photoshop
                // they will cause crashes and/or get wrong colors unless they are converted to
                // have a known image type, this conversion can be quite time consuming for some of these
                // images and will reduce the filesize in some cases
                if(finishedImages[0].getType() == 0){
                    finishedImages[0] = ImageUtil.toBuffImageRGBorARGB(finishedImages[0]);
                }                
            } else { // GIF specific processing (gif is not well supported in java so we need to do some things manually)                         
                Iterator iioimages = reader.readAll(null);
                int numImages = reader.getNumImages(true);
                GIFStreamMetadata  streamMetaData = (GIFStreamMetadata)reader.getStreamMetadata();
                if(numImages == 1){ // Normal Gif
                    finishedImages = readGif(iioimages, imageParams, streamMetaData);
                } else { // Animated Gif 
                    finishedImages = readAnimatedGif(iioimages, numImages, imageParams, streamMetaData);
                }                               
            }
        } catch(IOException ioe){
            throw new ImageConverterException(ImageConverterException.Types.IO_ERROR,"IOException thrown when reading from InputByteStream",ioe);
        } finally {
           if(reader != null){
               reader.dispose();
           }          
           reader = null;
           try {
               iis.close();            
               imageStream.close();            
           } catch (IOException ignored){}
           iis = null;
           imageStream = null;
        }   
        return finishedImages;
    }          
        
    public static BufferedImage getBufferedImage(byte [] inByteArray) throws ImageConverterException{
        ByteArrayInputStream imageStream = new ByteArrayInputStream(inByteArray);
        ImageInputStream iis = null;
        BufferedImage image = null;
        ImageReader reader = null;
        
        try{                        
            iis = ImageIO.createImageInputStream(imageStream);          
            Iterator readers = ImageIO.getImageReaders(iis);
                        
            if(readers.hasNext()){  
                reader = (ImageReader)readers.next();
            } else {
                throw new ImageConverterException(ImageConverterException.Types.READ_CODEC_NOT_FOUND,"No image readers found for the image type of the supplied image",null);
            }
            reader.setInput(iis,false);
            image = reader.read(0);                                             
        } catch(IOException ioe){
            throw new ImageConverterException(ImageConverterException.Types.IO_ERROR,"IOException thrown when reading from InputByteStream",ioe);
        } finally {
               reader.dispose();
               reader = null;
               try {
                   iis.close();
                   iis = null;
                   imageStream.close();
                   imageStream = null;
               } catch (IOException ignored){}
            }           
        
        return image;
    }

    private static BufferedImage[] readGif(Iterator iioimages, ImageConverterParams imageParams, GIFStreamMetadata  streamMetaData){        
        IIOImage [] images = new IIOImage[1];
        BufferedImage [] finishedImages = new BufferedImage[1];
        
        images[0] = (IIOImage)iioimages.next();        

        BufferedImage resultImage = new BufferedImage(streamMetaData.logicalScreenWidth, streamMetaData.logicalScreenHeight, BufferedImage.TYPE_INT_ARGB);            
  
        Color transparentColor = null;
      
        imageParams.getInternalVariables().setImageMetadata(new GIFImageMetadata[1]);
        
        // Storing metadata for later use
        GIFImageMetadata [] metaTable = imageParams.getInternalVariables().getImageMetadata();                               
        metaTable[0] = (GIFImageMetadata)images[0].getMetadata(); 
        
        RenderedImage r = images[0].getRenderedImage();         
        
        if(metaTable[0].transparentColorFlag){
            if(streamMetaData.globalColorTable != null){           
                int numEntries = streamMetaData.globalColorTable.length/3;
                byte [] colorTable = streamMetaData.globalColorTable;
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
                                                                                    
         Graphics2D resG = resultImage.createGraphics();
         if(metaTable[0].imageLeftPosition != 0 || metaTable[0].imageTopPosition != 0){
             resG.drawRenderedImage(r, AffineTransform.getTranslateInstance(metaTable[0].imageLeftPosition, metaTable[0].imageTopPosition));
         } else {
             resG.drawRenderedImage(r,null);
         }
         
         resG.dispose();
         resG = null;                     
                              
         finishedImages[0] = resultImage;
         
         metaTable[0].imageLeftPosition = 0;
         metaTable[0].imageTopPosition = 0;
         r = null;
        imageParams.getInternalVariables().setImageMetadata(metaTable);
        return finishedImages;
    }
    
    private static BufferedImage[] readAnimatedGif(Iterator iioimages, int numImages, ImageConverterParams imageParams, GIFStreamMetadata  streamMetaData){
        IIOImage [] images = new IIOImage[numImages];
        BufferedImage [] finishedImages = new BufferedImage[numImages];

        for(int counter = 0; iioimages.hasNext(); counter++) {
            images[counter] = (IIOImage)iioimages.next();
        }

        BufferedImage resultImage = new BufferedImage(streamMetaData.logicalScreenWidth, streamMetaData.logicalScreenHeight, BufferedImage.TYPE_INT_ARGB);            
  
        Color backgroundColor = Color.white; 
        Color transparentColor = null;
        byte [] colorTable = null;
        
        BufferedImage previousImage = null;
        if(numImages > 1){
            previousImage = new BufferedImage(streamMetaData.logicalScreenWidth, streamMetaData.logicalScreenHeight, BufferedImage.TYPE_INT_ARGB);
        }
        imageParams.getInternalVariables().setImageMetadata(new GIFImageMetadata[images.length]);
        
        // Storing metadata for later use
        GIFImageMetadata [] metaTable = imageParams.getInternalVariables().getImageMetadata();                               
        GIFImageMetadata metaData = (GIFImageMetadata)images[0].getMetadata(); 
        
        if(streamMetaData.globalColorTable != null){
            int numEntries = streamMetaData.globalColorTable.length/3;
            colorTable = streamMetaData.globalColorTable;
            int found = 0;
            for (int e = 0; e < numEntries; e++) {
                if(e == streamMetaData.backgroundColorIndex) {
                    int r = colorTable[3*e] & 0xff; 
                    int g = colorTable[3*e + 1] & 0xff; 
                    int b = colorTable[3*e + 2] & 0xff; 
                    backgroundColor = new Color(r,g,b);   
                    found++; 
                }
                if(e == metaData.transparentColorIndex) {
                    int r1 = colorTable[3*e] & 0xff; 
                    int g1 = colorTable[3*e + 1] & 0xff; 
                    int b1 = colorTable[3*e + 2] & 0xff; 
                    transparentColor = new Color(r1,g1,b1);   
                    imageParams.getInternalVariables().setTransparentColor(transparentColor);  
                    //imageParams.getInternalVariables().setTransparentColor(ImageUtil.getUniqueColor(colorTable,transparentColor));
                    //imageParams.setNumberOfColors(6868);
                    found++; 
                }
                if(found == 2){
                    break;
                }
            }
            //This detects a known problem, for which no current solution is known, thus its not in use    
            if(transparentColor != null){
                for (int e = 0; e < numEntries; e++) {
                    if(e == metaData.transparentColorIndex) {
                        continue;
                    }
                    if(transparentColor.getRed() == (colorTable[3*e] & 0xff) &&
                            transparentColor.getGreen() == (colorTable[3*e+1] & 0xff) &&
                            transparentColor.getBlue() == (colorTable[3*e+2] & 0xff)){  
                        /*
                        WritableRaster wr = resultImage.getAlphaRaster();
                        int h = wr.getHeight();
                        int w = wr.getWidth();
                        Color newColor = ImageUtil.getUniqueColor(colorTable,transparentColor);
                        int[] newp = new int[] { newColor.getRed(), newColor.getGreen(), newColor.getBlue(), 255};
                        int[] old = new int[4];
                        
                        for (int row = 0; row < w; row++) {
                            for (int col = 0; col < h; col++) {                               
                                old = wr.getPixel(row, col, old);                   
                                if (old[0] == transparentColor.getRed() && old[1] == transparentColor.getGreen() && old[2] == transparentColor.getBlue()) {
                                    wr.setPixel(row, col, newp);
                                }                                
                            }
                        }           
                        transparentColor = newColor;
                        imageParams.getInternalVariables().setTransparentColor(transparentColor);*/
                        imageParams.getInternalVariables().setTransparentColor(ImageUtil.getUniqueColor(colorTable,transparentColor));
                        imageParams.setNumberOfColors(6868);
                        break;
                    }
                }
            }
            
            //imageParams.getInternalVariables().setTransparentColor(new Color(0,255,0));
        }
        
        RenderedImage r = null;
        Graphics2D resG = null;
        int noneCounter = 0;
        
        for(int i = 0; i < images.length; i++){
            resultImage = new BufferedImage(streamMetaData.logicalScreenWidth, streamMetaData.logicalScreenHeight, BufferedImage.TYPE_INT_ARGB);                    
            
             r = images[i].getRenderedImage();
             
             metaData = (GIFImageMetadata)images[i].getMetadata();                                                                                                                                      
                          
             // Disposal of gif animation frames                          
             switch(metaData.disposalMethod){
                 case 0: // "None"
                     // Do Nothing
                     noneCounter++;
                     break;
                 case 1: // "doNotDispose"
                     previousImage.flush();
                     previousImage.setData(resultImage.copyData(null));
                     break;
                 case 2: // "restoreToBackgroundColor"                                                  
                     Graphics2D g = resultImage.createGraphics(); 
                     g.setPaintMode(); 

                     if(metaData.transparentColorFlag){// && streamMetaData.backgroundColorIndex == metaData.transparentColorIndex) {    
                         g.setColor(transparentColor); 
                     } else {                      
                         g.setColor(backgroundColor); 
                         //g.setColor(transparentColor); 
                     }
                     g.fillRect(0,0, streamMetaData.logicalScreenWidth, streamMetaData.logicalScreenHeight);
                     break;
                 case 3: // "restoreToPrevious"
                     resultImage.setData(previousImage.copyData(null));
                     break;                             
             }
                         
             resG = resultImage.createGraphics();
             if(metaData.imageLeftPosition != 0 || metaData.imageTopPosition != 0){
                 resG.drawRenderedImage(r, AffineTransform.getTranslateInstance(metaData.imageLeftPosition, metaData.imageTopPosition));
             } else {
                 resG.drawRenderedImage(r,null);
             }
             
             resG.dispose();
             resG = null;                     
                                  
             finishedImages[i] = resultImage;
             resultImage.flush();
             resultImage = null;
             metaData.imageLeftPosition = 0;
             metaData.imageTopPosition = 0;
             metaTable[i] = metaData;
             metaData = null;
        }   
        if(noneCounter == images.length){     
            //imageParams.getInternalVariables().setTransparentColor(null);
        }
        if(finishedImages[0].getColorModel().hasAlpha() && imageParams.getInternalVariables().getTransparentColor() == null){
            //System.out.println("has alpha");
            imageParams.getInternalVariables().setTransparentColor(ImageUtil.getUniqueColor(colorTable,transparentColor));
            imageParams.setNumberOfColors(6868);
        }
        imageParams.getInternalVariables().setImageMetadata(metaTable);
        return finishedImages;
    }
    
    /**                                                                                                                                           
    Java's ImageIO can't process 4-component images                                                                                             
    and Java2D can't apply AffineTransformOp either,                                                                                            
    so convert raster data to RGB.                                                                                                              
    Technique due to MArk Stephens.                                                                                                             
    Free for any use.                                                                                                                           
  */
    private static BufferedImage createJPEG4(Raster raster, int xform) {
    	int w = raster.getWidth() ;
    	int h = raster.getHeight();

    	byte[] rgb = new byte[w*h*3];


//    	 if (Adobe_APP14 and transform==2) then YCCK else CMYK

    	if (false){//xform == 2) { // YCCK -- Adobe
	    	float[] Y = raster.getSamples(0,0,w,h, 0, (float[])null);
	    	float[] Cb = raster.getSamples(0,0,w,h, 1, (float[])null);
	    	float[] Cr = raster.getSamples(0,0,w,h, 2, (float[])null);
	    	float[] K = raster.getSamples(0,0,w,h, 3, (float[])null);

	        for (int i=0,imax=Y.length, base=0; i<imax; i++, base+=3) {
	            // faster to track last cmyk and save computations on stretches of same color?                                                      
	            // better to use ColorConvertOp?                                                                                                    
	            float k=K[i], y = Y[i], cb=Cb[i], cr=Cr[i];
	            double val = y + 1.402*(cr-128) - k;
	            rgb[base] = val<0.0? (byte)0: val>255.0? (byte)0xff: (byte)(val+0.5);
	            val = y - 0.34414*(cb-128) - 0.71414*(cr-128) - k;
	            rgb[base+1] = val<0.0? (byte)0: val>255.0? (byte)0xff: (byte)(val+0.5);
	            val = y + 1.772 * (cb-128) - k;
	            rgb[base+2] = val<0.0? (byte)0: val>255.0? (byte)0xff: (byte)(val+0.5);
	        }
    	}
    	else {
//    	assert xform==0: xform; // CMYK
    		int[] C = raster.getSamples(0,0,w,h, 0, (int[])null) ;
    		int[] M = raster.getSamples(0,0,w,h, 1, (int[])null) ;
    		int[] Y = raster.getSamples(0,0,w,h, 2, (int[])null) ;
    		int[] K = raster.getSamples(0,0,w,h, 3, (int[])null) ;

    		int val = 0;
            for (int i=0,imax=C.length, base=0; i<imax; i++, base+=3) {
            	int r = 1 - ((255-C[i]) - (255-K[i]));
            	int g = 1 - ((255-M[i]) - (255-K[i]));
            	int b = 1 - ((255-Y[i]) - (255-K[i]));
            	rgb[base] = (byte)r;
            	rgb[base+1] = (byte)g;
            	rgb[base+2] = (byte)b;
            	
            	/*
            	double c = ( C[i] * ( 1 - K[i] ) + K[i] )/255;
            	double m = ( M[i] * ( 1 - K[i] ) + K[i] )/255;
            	double y = ( Y[i] * ( 1 - K[i] ) + K[i] )/255;
            	            	
            	val = (int)( 1 - c ) * 255;
            	rgb[base] = (byte)val;
            	val = (int)( 1 - m ) * 255;
            	rgb[base+1] = (byte)val;
            	val = (int)( 1 - y ) * 255;
            	rgb[base+2] = (byte)val;
            	/*
            	int colors = 255 - 0;//K[i];
            	val = (colors * (255 - C[i]) / 255);
            	rgb[base] = (byte)val;
            	val = (colors * (255 - M[i]) / 255);
            	rgb[base+1] = (byte)val;
            	val = (colors * (255 - Y[i]) / 255);
            	rgb[base+2] = (byte)val;
            	*/
                /*int k = K[i];
                int m = M[i];
                int y = Y[i];
                int c = C[i];
                c = +(c-255);
                m = +(m-255);
                y = +(y-255);
                k = +(k-255);
                
                val = c + k;
                rgb[base] = (byte)val;
                val = m + k;
                rgb[base+1] = (byte) val;
                val = y + k;
                rgb[base+2] = (byte) val;*/
                /*
                val = (255 - Math.min(255, C[i] + k));
                rgb[base] = (byte)val;
                val = (255 - Math.min(255, M[i] + k));
                rgb[base+1] = (byte) val;
                val = (255 - Math.min(255, Y[i] + k));
                rgb[base+2] = (byte) val;
                */
                
            }
    		
    		/*
    		rgb[base] = (byte)(Math.min(255f, c * kk + k));
    		rgb[base+1] = (byte)(Math.min(255f, m * kk + k));
    		rgb[base+2] = (byte)(Math.min(255f, y * kk + k));
    		}
    		/*
    	int[] C = raster.getSamples(0,0,w,h, 0, (int[])null) ;
    	int[] M = raster.getSamples(0,0,w,h, 1, (int[])null) ;
    	int[] Y = raster.getSamples(0,0,w,h, 2, (int[])null) ;
    	int[] K = raster.getSamples(0,0,w,h, 3, (int[])null) ;

    	 for (int i=0,imax=C.length, base=0; i<imax; i++, base+=3) {
             int k = K[i];
             rgb[base] = (byte)(255 - Math.min(255, C[i] + k));
             rgb[base+1] = (byte)(255 - Math.min(255, M[i] + k));
             rgb[base+2] = (byte)(255 - Math.min(255, Y[i] + k));
         }*/
    	}

//    	 from other image types we know InterleavedRaster's can be
//    	 manipulated by AffineTransformOp, so create one of	those.
    	raster = Raster.createInterleavedRaster(new DataBufferByte(rgb, rgb.length), w, h, w*3,3, new int[] {0,1,2 }, null);

    	ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
    	ColorModel cm = new ComponentColorModel(cs, false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
    	
    	return new BufferedImage(cm, (WritableRaster) raster, true, null);
    }
    /* DisposalMetods:
     * 0 - "none"
     * 1 - "doNotDispose"
     * 2 - "restoreToBackgroundColor"
     * 3 - "restoreToPrevious"
     * 4 - "undefinedDisposalMethod4"
     * 5 - "undefinedDisposalMethod5"
     * 6 - "undefinedDisposalMethod6"
     * 7 - "undefinedDisposalMethod7
     */    
    
    // Processing local color table
    /*
     * 
                         if(metaData.transparentColorFlag){
                         int numEntries = 0;
                         if(metaData.localColorTable != null){
                             numEntries = metaData.localColorTable.length/3;
                             byte [] colorTable = metaData.localColorTable;
                             for (int e = 0; e < numEntries; e++) {
                                 if(e == metaData.transparentColorIndex) {
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
     */
     
}
