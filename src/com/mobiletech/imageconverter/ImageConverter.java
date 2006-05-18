/*
 * Created on Mar 11, 2005
 * 
 */

package com.mobiletech.imageconverter;

import java.io.*;
import java.util.Iterator;
import java.awt.image.BufferedImage;
import javax.imageio.*;
import javax.imageio.stream.*;

import com.mobiletech.imageconverter.exception.ImageConverterException;
import com.mobiletech.imageconverter.io.ImageDecoder;
import com.mobiletech.imageconverter.io.ImageEncoder;
import com.mobiletech.imageconverter.modifiers.ImageColorModifier;
import com.mobiletech.imageconverter.modifiers.ImageScaler;
import com.mobiletech.imageconverter.vo.ImageConverterParams;
import com.mobiletech.imageconverter.vo.ImageWatermark;
import com.mobiletech.imageconverter.vo.TextWatermark;
import com.mobiletech.imageconverter.watermarks.ImageWatermarker;

/**
 * This class functions as an Image converter. Taking images and converting them
 * to different image formats. It can also perform other related operations like
 * resizing and color change.
 * 
 * The class also provides the opportunity to add text and images as watermarks
 * on the original image.
 * 
 * @author Andreas Ryland
 *  
 */
public class ImageConverter {
    public static final int WMARK_POS_TOPLEFT = 1;
    public static final int WMARK_POS_TOPRIGHT = 2;
    public static final int WMARK_POS_BOTTOMLEFT = 3;
    public static final int WMARK_POS_BOTTOMRIGHT = 4;
    public static final int WMARK_POS_CENTER = 5;
    public static final int WMARK_POS_DIAGONAL_CENTER = 6;          

    /**
     * Takes an original image and performs conversion on it to produce a
     * converted image. See the ImageConverterParams class for more information
     * on how to specify the conversions to be done.
     * 
     * @param imageParams
     *            The ImageConverterParams object containing the original image
     *            and the desired settings specifying how the converted image
     *            should be.
     * @return A byte array containing the converted image
     * @throws ImageConverterException
     */
    public static byte[] convertImage(ImageConverterParams imageParams) throws ImageConverterException{     
       byte [] returnByte = null;
       
        try{
            // Just in case: Reset the internal variables of the ImageConverterParams object
            imageParams.resetInternal();
            //Validate input parameters
            imageParams = validateParams(imageParams);
            // Read all images into BufferedImage objects for processing           
            BufferedImage [] images  = ImageDecoder.readImages(imageParams.getImage(),imageParams);            
            // Run the processing pipeline for each image and retrieve the processed image            
            for(int i = 0; i<images.length ;i++){
                imageParams = doPipeline(images[i],imageParams);
                images[i] = imageParams.getInternalVariables().getBufferedImage();
            }
            // If the image has not been changed, check if the image format was to be converted, or, in case of jpeg to jpeg conversion, if the
            // compression factor should be changed (thus needing the jpeg to be re-encoded with the new compression setting) if neither of these
            // cases are true, then just return the original image
            if(!imageParams.getInternalVariables().isChanged()){
                // if there was no change in jpeg compression quality...
                if(imageParams.getJPEGCompressionQuality() <= 0.0){                         
                        if(imageParams.getFormat().equalsIgnoreCase( imageParams.getInternalVariables().getOldFormat() ) ){
                            return imageParams.getImage();
                        }else if((imageParams.getInternalVariables().getOldFormat().equalsIgnoreCase("jpg") ||
                                            imageParams.getInternalVariables().getOldFormat().equalsIgnoreCase("jpeg")) && 
                                                (imageParams.getFormat().equalsIgnoreCase("jpg") || imageParams.getFormat().equalsIgnoreCase("jpeg"))){
                                                    return imageParams.getImage();
                                                }
                }
            }                      
            // code the bufferedImage(s) back to an encoded byteStream in the requested format
            returnByte = ImageEncoder.getByteArray(images,imageParams);
        } catch(ImageConverterException e){
            imageParams.resetInternal(); // (Paranoia aka Just-In-Case)
            throw e;            
        } catch (Throwable t){
            throw new ImageConverterException(ImageConverterException.Types.EMBEDDED_EXCEPTION,t.getClass().getName() + " thrown: " + t.getMessage(),t);
        } finally {
            imageParams.resetInternal();
        }
        return returnByte;      
    }       
        
    private static ImageConverterParams doPipeline(BufferedImage image, ImageConverterParams imageParams) throws ImageConverterException{
        // Perform resize if requested
        if(imageParams.getWidth() > 0 || imageParams.getHeight() > 0){
            BufferedImage temp = ImageScaler.resizeImage(image, imageParams.getHeight(),imageParams.getWidth(),imageParams.isNoEnlargement(),(imageParams.getInternalVariables().getTransparentColor() != null ? true : false),imageParams);
            if(temp != null){
                image = null;
                image = temp;
                temp = null;
                imageParams.getInternalVariables().setChanged(true);
            }
        }
        // Add image watermarks if any
        if(imageParams.hasImageWatermarks()){
            Iterator watermarks = imageParams.getImageWatermarksIterator();
            while(watermarks.hasNext()){
                image = ImageWatermarker.applyImageWatermark(image,(ImageWatermark)watermarks.next());
            }
            imageParams.getInternalVariables().setChanged(true);
        }
        // Add text watermarks if any
        if(imageParams.hasTextWatermarks()){
            Iterator watermarks = imageParams.getTextWatermarksIterator();
            while(watermarks.hasNext()){
                image = ImageWatermarker.applyTextWatermark(image,(TextWatermark)watermarks.next());
            }           
            imageParams.getInternalVariables().setChanged(true);
        }       
        // Perform color reduction if requested
        if(imageParams.getNumberOfColors() > 0){
            //image = ImageColorModifier.colorChange(image,imageParams.getNumberOfColors());
            //imageParams.getInternalVariables().setChanged(true);
        }       
        // Convert to grayscale if requested
        if(imageParams.isGrayscale()){
            image = ImageColorModifier.getGrayscale(image);
            imageParams.getInternalVariables().setChanged(true);
        }
        
        imageParams.getInternalVariables().setBufferedImage(image);
        // Return converted image
        return imageParams;     
    }
    
    private static ImageConverterParams validateParams(ImageConverterParams inParams)throws ImageConverterException{                
        if(null == inParams.getImage()){
            throw new ImageConverterException(ImageConverterException.Types.BAD_INPUT_VARIABLE,"Bad input argument: Image ByteArray is null",null);
        }
        if(inParams.getImage().length <= 0){
            throw new ImageConverterException(ImageConverterException.Types.BAD_INPUT_VARIABLE,"Bad input argument: Image ByteArray of length 0!",null);
        }
        String format = inParams.getFormat();
        inParams.getInternalVariables().setOldFormat(getImageFormatName(inParams.getImage()));
        
        if(null == format || format.trim().compareTo("")==0){
            format = inParams.getInternalVariables().getOldFormat();
            inParams.setFormat(format);
        }
        if(format.length()<3 || format.length()>4){
            throw new ImageConverterException(ImageConverterException.Types.BAD_INPUT_VARIABLE,"Bad input argument: requested codec name not in 3 or 4 letter format Format: '" + format + "'",null);
        }
        if(!(format.compareToIgnoreCase("png") == 0 || 
             format.compareToIgnoreCase("gif") == 0 ||
             format.compareToIgnoreCase("wbmp") == 0 ||
             format.compareToIgnoreCase("jpg") == 0 ||
             format.compareToIgnoreCase("jpeg") == 0 ||
             format.compareToIgnoreCase("tif") == 0 ||
             format.compareToIgnoreCase("bmp") == 0)){
            //throw new ImageConverterException(ImageConverterException.Types.CODEC_NOT_SUPPORTED,"Requested codec not supported: " + format,null);
        }       
        //  throw new ImageConverterException(ImageConverterException.Types.BAD_RESIZE_SIZE,"Cannot resize to size height: "+ inHeight + " width: " + inWidth,null);        
        return inParams;
    }                                 
    
    private static String getImageFormatName(byte [] inImage) throws ImageConverterException{
        ByteArrayInputStream imageStream = null;
        String format = null;
        
        try {
            imageStream = new ByteArrayInputStream(inImage);            
            ImageInputStream iis = ImageIO.createImageInputStream(imageStream);
            Iterator readers = ImageIO.getImageReaders(iis);
            
            if(readers.hasNext()){              
                ImageReader reader = (ImageReader)readers.next();
                format = reader.getFormatName();
            } else {
                throw new ImageConverterException(ImageConverterException.Types.READ_CODEC_NOT_FOUND,"No image readers found for the image type of the supplied image",null);               
            }                                   
        } catch (IOException iox){
            throw new ImageConverterException(ImageConverterException.Types.IO_ERROR,"IOException caught when attempting to get imagereader for ByteArray: " + iox.getMessage(),iox);
        } finally {
            try {
                imageStream.close();
            } catch(IOException ignored){}
        }
        
        return format;
    }         
    
    public static boolean isSupportedImage(File image){
        boolean result = false;
        
        if(null == image){
            return result;
        }
        ImageInputStream iis = null;
        Iterator readers = null;
        try {
            iis = ImageIO.createImageInputStream(image);
            readers = ImageIO.getImageReaders(iis);
            if(readers.hasNext()){
                result = true;
            }
        } catch (Throwable ignored) {}
        finally{
            if(null != iis){
                try {
                    iis.close();
                } catch (IOException e) {}
            }
            readers = null;
        }
        
        return result;
    }
    
    public static String getFormatOfSupportedImage(File image){
        String format = null;
        
        if(null == image){
            return format;
        }
        ImageInputStream iis = null;
        Iterator readers = null;
        try {
            iis = ImageIO.createImageInputStream(image);
            readers = ImageIO.getImageReaders(iis);
            if(readers.hasNext()){
                ImageReader reader = (ImageReader)readers.next();
                format = reader.getFormatName();
                reader = null;
            }
        } catch (Throwable ignored) {}
        finally{
            if(null != iis){
                try {
                    iis.close();
                } catch (IOException e) {}
            }
            readers = null;
        }
        
        return format;
    }
    
    public static int getNumberOfFramesInImage(File image){
        int frames = 1;
        
        if(null == image){
            return frames;
        }
        ImageInputStream iis = null;
        Iterator readers = null;
        try {
            iis = ImageIO.createImageInputStream(image);
            readers = ImageIO.getImageReaders(iis);
            if(readers.hasNext()){
                ImageReader reader = (ImageReader)readers.next();
                frames = reader.getNumImages(true);
                reader = null;
            }
        } catch (Throwable ignored) {}
        finally{
            if(null != iis){
                try {
                    iis.close();
                } catch (IOException e) {}
            }
            readers = null;
        }        
        return frames;
    }
    
    public static boolean isSupportedImageFormat(String format){
        boolean returnvalue = false;
        if(null == format){
            return false;
        }        
        Iterator readers = null;
        try {
            readers = ImageIO.getImageReadersByFormatName(format);
            if(readers.hasNext()){
                returnvalue = true;
            }
        } catch (Throwable ignored) {}
        finally{            
            readers = null;
        }
        
        return returnvalue;
    }    
}
