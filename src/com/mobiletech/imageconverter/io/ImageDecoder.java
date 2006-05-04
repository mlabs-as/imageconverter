package com.mobiletech.imageconverter.io;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.mobiletech.imageconverter.exception.ImageConverterException;
import com.mobiletech.imageconverter.vo.ImageConverterParams;
import com.sun.imageio.plugins.gif.GIFImageMetadata;
import com.sun.imageio.plugins.gif.GIFStreamMetadata;

public class ImageDecoder {
    public static BufferedImage [] readImages(byte [] inByteArray,ImageConverterParams imageParams) throws ImageConverterException{
        long start = System.currentTimeMillis();
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

            if(!imageParams.getFormat().equalsIgnoreCase("gif") || !reader.getFormatName().equalsIgnoreCase("gif")){
                finishedImages = new BufferedImage[1];
                finishedImages[0] = reader.read(0);
            } else {                         
                Iterator iioimages = reader.readAll(null);
                
                GIFStreamMetadata  streamMetaData = (GIFStreamMetadata)reader.getStreamMetadata();

                int numImages = reader.getNumImages(true);
                IIOImage [] images = new IIOImage[numImages];
                finishedImages = new BufferedImage[numImages];

                for(int counter = 0; iioimages.hasNext(); counter++) {
                    images[counter] = (IIOImage)iioimages.next();
                }

                BufferedImage resultImage = new BufferedImage(streamMetaData.logicalScreenWidth, streamMetaData.logicalScreenHeight, BufferedImage.TYPE_INT_ARGB);            
          
                Color backgroundColor = Color.white; 
                Color transparentColor = null;
                
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
                    byte [] colorTable = streamMetaData.globalColorTable;
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
                }
                
                RenderedImage r = null;
                Graphics2D resG = null;
                
                for(int i = 0; i < images.length; i++){  
                    if(numImages > 1){
                        previousImage.flush();
                        previousImage.setData(resultImage.copyData(null));
                    }
                
                     r = images[i].getRenderedImage();
                     metaData = (GIFImageMetadata)images[i].getMetadata();                                                                                                                                      
                                                                           
                     // Disposal of gif animation frames
                     if(images.length > 1){
                         switch(metaData.disposalMethod){
                         case 0: // "None"
                             // Do Nothing
                             break;
                         case 1: // "doNotDispose"
                             //resultImage.setData(previousImage.copyData(null));
                             break;
                         case 2: // "restoreToBackgroundColor"                             
                             resultImage = null;
                             resultImage = new BufferedImage(streamMetaData.logicalScreenWidth, streamMetaData.logicalScreenHeight, BufferedImage.TYPE_INT_ARGB);
                             Graphics2D g = resultImage.createGraphics(); 
                             g.setPaintMode(); 
    
                             if(metaData.transparentColorFlag && streamMetaData.backgroundColorIndex == metaData.transparentColorIndex) {    
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
                     
                     metaData.imageLeftPosition = 0;
                     metaData.imageTopPosition = 0;
                     metaTable[i] = metaData;
                     metaData = null;
                }   
                imageParams.getInternalVariables().setImageMetadata(metaTable);
            }
        } catch(IOException ioe){
            throw new ImageConverterException(ImageConverterException.Types.IO_ERROR,"IOException thrown when reading from InputByteStream",ioe);
        } finally {
           reader.dispose();
           reader = null;
           try {
               iis.close();            
               imageStream.close();            
           } catch (IOException ignored){}
           iis = null;
           imageStream = null;
        }   
        System.out.println("Time to read: " + (System.currentTimeMillis()-start)/1000);
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
