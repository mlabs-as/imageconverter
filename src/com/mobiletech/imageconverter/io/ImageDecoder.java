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
import com.mobiletech.imageconverter.vo.ImageConverterParams;
import com.sun.imageio.plugins.gif.GIFImageMetadata;
import com.sun.imageio.plugins.gif.GIFStreamMetadata;

public class ImageDecoder {
    public static BufferedImage [] readImages(byte [] inByteArray,ImageConverterParams imageParams) throws ImageConverterException{                       
        ByteArrayInputStream imageStream = new ByteArrayInputStream(inByteArray);
        
        BufferedImage [] finishedImages = null;
        ImageInputStream iis = null;        
        ImageReader reader = null;

        try {   
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

                int imageWidth = streamMetaData.logicalScreenWidth;
                int imageHeight = streamMetaData.logicalScreenHeight;
                
                int numImages = reader.getNumImages(true);
                IIOImage [] images = new IIOImage[numImages];
                finishedImages = new BufferedImage[numImages];

                int counter = 0;
                
                while(iioimages.hasNext()) {
                    images[counter] = (IIOImage)iioimages.next();
                    counter++;
                }
                                
                BufferedImage resultImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);            
                
                if(images.length > 1){                    
                    BufferedImage colorm = reader.read(1);
                    imageParams.getInternalVariables().setCm(colorm.getColorModel());
                }
                int backgroundColorIndex = streamMetaData.backgroundColorIndex;

                Color backgroundColor = Color.white; 
                Color transparentColor = null;

                if(streamMetaData.globalColorTable != null){
                    int numEntries = streamMetaData.globalColorTable.length/3;
                    byte [] colorTable = streamMetaData.globalColorTable;
                    for (int e = 0; e < numEntries; e++) {
                        if(e == backgroundColorIndex) {
                            int r = colorTable[3*e] & 0xff; 
                            int g = colorTable[3*e + 1] & 0xff; 
                            int b = colorTable[3*e + 2] & 0xff; 
                            backgroundColor = new Color(r,g,b);   
                            break; 
                        }
                    }
                }
                BufferedImage previousImage = null;
                imageParams.getInternalVariables().setImageMetadata(new GIFImageMetadata[images.length]);
                // Storing metadata for later use
                GIFImageMetadata [] metaTable = imageParams.getInternalVariables().getImageMetadata();
                
                for(int i = 0; i < images.length; i++){  
                     previousImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB); 
                     previousImage.setData(resultImage.copyData(null));
                     RenderedImage r = images[i].getRenderedImage();
                     GIFImageMetadata metaData = (GIFImageMetadata)images[i].getMetadata();                                                                                                                    

                     int imageLeft = metaData.imageLeftPosition;
                     int imageTop = metaData.imageTopPosition;
                     int transParantColorIndex = metaData.transparentColorIndex;
                     int disposalMethod = metaData.disposalMethod;
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
                     
                     if(metaData.transparentColorFlag){
                         int numEntries = 0;
                         if(metaData.localColorTable != null){
                             numEntries = metaData.localColorTable.length/3;
                             byte [] colorTable = metaData.localColorTable;
                             for (int e = 0; e < numEntries; e++) {
                                 if(e == transParantColorIndex) {
                                     int r1 = colorTable[3*e] & 0xff; 
                                     int g1 = colorTable[3*e + 1] & 0xff; 
                                     int b1 = colorTable[3*e + 2] & 0xff; 
                                     transparentColor = new Color(r1,g1,b1);   
                                     imageParams.getInternalVariables().setTransparentColor(transparentColor);
                                     break; 
                                 }
                             }
                         } else if(streamMetaData.globalColorTable != null){                             
                             numEntries = streamMetaData.globalColorTable.length/3;
                             byte [] colorTable = streamMetaData.globalColorTable;
                             for (int e = 0; e < numEntries; e++) {
                                 if(e == transParantColorIndex) {
                                     int r1 = colorTable[3*e] & 0xff; 
                                     int g1 = colorTable[3*e + 1] & 0xff; 
                                     int b1 = colorTable[3*e + 2] & 0xff; 
                                     transparentColor = new Color(r1,g1,b1);   
                                     imageParams.getInternalVariables().setTransparentColor(transparentColor);  
                                     break; 
                                 }
                             }                             
                         } else if(transParantColorIndex > numEntries){
                             // Special case for gif images that have a transparent color that is not in the palette, set it to white.
                             transparentColor = new Color(255,255,255);
                             imageParams.getInternalVariables().setTransparentColor(transparentColor);                            
                         }                        
                     }
                     // we either restore it to the previous image 
                     if(disposalMethod == 3) { 
                         resultImage.setData(previousImage.copyData(null)); 
                      // or to the background color 
                     } else if(disposalMethod == 2) { 
                         resultImage = null;
                         resultImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
                         Graphics2D g = resultImage.createGraphics(); 
                         g.setPaintMode(); 

                         if(metaData.transparentColorFlag && backgroundColorIndex == transParantColorIndex) {    
                             g.setColor(transparentColor); 
                         } else {                      
                             g.setColor(backgroundColor); 
                         }
                         g.fillRect(0,0, imageWidth, imageHeight);
                     }                
                     Graphics2D resG = resultImage.createGraphics();

                     resG.drawRenderedImage(r, AffineTransform.getTranslateInstance(imageLeft, imageTop));
                     resG.dispose();
                     resG = null;
                     BufferedImage newIm = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
                     newIm.setData(resultImage.copyData(null));
                     finishedImages[i] = newIm;
                     newIm = null;
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

}
