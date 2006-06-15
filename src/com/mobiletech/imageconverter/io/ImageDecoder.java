package com.mobiletech.imageconverter.io;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

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
                finishedImages[0] = reader.read(0);
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
