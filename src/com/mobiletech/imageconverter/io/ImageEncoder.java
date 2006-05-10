package com.mobiletech.imageconverter.io;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.operator.ColorQuantizerDescriptor;

import com.mobiletech.imageconverter.exception.ImageConverterException;
import com.mobiletech.imageconverter.modifiers.ImageColorModifier;
import com.mobiletech.imageconverter.vo.ImageConverterParams;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.imageio.plugins.gif.GIFImageMetadata;

public class ImageEncoder {
    private static BufferedImage prepareForConversion(BufferedImage inImage, ImageConverterParams params) throws ImageConverterException{
        String format = params.getFormat();
                    
        // Special case for grayscale gif images, as it was more troublesome getting it not to hit in the wrong if statements below, see 
        // Description of below if statement as to why this is problematic
        if(params.getInternalVariables().getOldFormat().equalsIgnoreCase("gif") && format.equalsIgnoreCase("gif") && params.isGrayscale()){
            return inImage;
        }
        // This code gets run for gif to gif conversion since they are converted to RGB ARGB, oddly, the images appear faulty and striped if this code
        // is not run, although in principle, this code should not be run, as it destroys the transparency of the image and replaces it with white. 
        // Possibly the error is due to the gif conversion code not being able to handle ARGB images. 
        if(params.getInternalVariables().getOldFormat().equalsIgnoreCase("gif") && (inImage.getType() != BufferedImage.TYPE_BYTE_INDEXED || format.compareToIgnoreCase("gif")!=0)){
            BufferedImage buffered = new BufferedImage( inImage.getWidth( null ), inImage.getHeight( null ), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = buffered.createGraphics();
            if(params.getInternalVariables().getTransparentColor() == null){                             
                g2.setColor(Color.WHITE);
                g2.fillRect(0,0,inImage.getWidth( null ),inImage.getHeight( null ));
            } else {
                g2.setColor(params.getInternalVariables().getTransparentColor());
                g2.fillRect(0,0,inImage.getWidth( null ),inImage.getHeight( null ));
            }
            g2.drawImage( inImage, null, null );            
            inImage = buffered;
            g2.dispose();
            g2 = null;
            buffered = null;            
        }   
        if(format.compareToIgnoreCase("gif")==0 && inImage.getType() != BufferedImage.TYPE_BYTE_INDEXED && !params.isGrayscale()){   
            inImage = toIndexColorModel(inImage,params); 
        } 
        if(params.getFormat().compareToIgnoreCase("wbmp")==0){
            int bwWidth = inImage.getWidth(); 
            int bwHeight = inImage.getHeight(); 
            
            BufferedImage bwimage = new BufferedImage(bwWidth,bwHeight,BufferedImage.TYPE_BYTE_BINARY);
            
            Graphics graphics = bwimage.getGraphics(); 
            graphics.drawImage(inImage, 0, 0,bwWidth, bwHeight, null); 
            graphics.dispose();
            graphics = null;
            
            int [] pixels = new int[bwWidth * bwHeight]; 
            
            bwimage.getRGB(0,0,bwWidth,bwHeight,pixels,0,bwWidth); 
             
            int alpha = 0xff000000;
            int black = alpha; 
            int white = 0xffffff | alpha; 

            for( int i=0; i<pixels.length; i++) { 
              if( (pixels[i] & 0xff) > 128 ) { 
                  pixels[i] = white; 
              } else { 
                    pixels[i] = black; 
              } 
            }
        
            bwimage.setRGB(0,0,bwWidth,bwHeight,pixels,0,bwWidth);
            inImage = bwimage;
            bwimage = null;
        }
        
        return inImage;
    }
    
    public static byte[] getByteArray(BufferedImage [] images, ImageConverterParams params) throws ImageConverterException{        
        for(int i = 0; i < images.length; i++){
            images[i] = prepareForConversion(images[i],params);    
        }        
        
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String format = params.getFormat();
        
        byte [] outputImage = null;
        if(format.compareToIgnoreCase("jpg")==0 || format.compareToIgnoreCase("jpeg")==0){
            JPEGImageEncoder enc = JPEGCodec.createJPEGEncoder(output);
            
            if(params.getJPEGCompressionQuality() != 0){                
                JPEGEncodeParam param = JPEGCodec.getDefaultJPEGEncodeParam(images[0]);
                if(params.getJPEGCompressionQuality() > 0){
                    param.setQuality(params.getJPEGCompressionQuality(),true);
                }
                //if(params.useSubsampling()){
                    param.setHorizontalSubsampling(0, 1); 
                    param.setHorizontalSubsampling(1, 1); 
                    param.setHorizontalSubsampling(2, 1); 
                    param.setVerticalSubsampling(0, 1); 
                    param.setVerticalSubsampling(1, 1); 
                    param.setVerticalSubsampling(2, 1);
                //}
                enc.setJPEGEncodeParam(param);
            }
            try {
                enc.encode(images[0]);
                outputImage = output.toByteArray();
                output.flush();
            } catch (IOException e) {
                throw new ImageConverterException(ImageConverterException.Types.IO_ERROR,"IOException thrown when writing encoded image ",e);
            } finally {
                enc = null;
                try {
                    output.close();
                } catch(IOException ignored){}
                output = null;
            }
        } else {
            Iterator writers = ImageIO.getImageWriters(new ImageTypeSpecifier(images[0]),format);       
            //Iterator writers = ImageIO.getImageWritersByFormatName(format);
            if(!writers.hasNext()){
                throw new ImageConverterException(ImageConverterException.Types.CODEC_NOT_FOUND,"No codec found for format name: " + format,null);
            }
            ImageWriter writer = (ImageWriter)writers.next();
            ImageOutputStream ios = null;

            if(images.length > 1){
                // If multi image (Currently only animated gif will be allowed to fit this scenario)
                try {                   
                    ios = ImageIO.createImageOutputStream(output);      
                    writer.setOutput(ios);
                    
                    writer.prepareWriteSequence(null);
                    GIFImageMetadata [] metaDataTable = params.getInternalVariables().getImageMetadata();
                   
                    for(int i = 0;i<images.length;i++){
                        writer.writeToSequence(new IIOImage(images[i],null,metaDataTable[i]),writer.getDefaultWriteParam());
                    }                   
                    writer.endWriteSequence();                  
                    ios.flush();
                    outputImage = output.toByteArray();
                    output.flush();
                } catch(IOException e){
                    throw new ImageConverterException(ImageConverterException.Types.IO_ERROR,"IOException thrown when writing encoded image ",e);
                } finally {
                    writer.dispose();
                    try{                
                        ios.close();
                        output.close();
                    } catch(IOException ignored){}
                    writer = null;
                    ios = null;
                    output = null;
                }           
            } else {                            
                try{
                    ios = ImageIO.createImageOutputStream(output);      
                    
                    writer.setOutput(ios);
                    writer.write(images[0]);
                    ios.flush();
                    outputImage = output.toByteArray();
                    output.flush();
                        
                } catch(IOException ioe){
                    throw new ImageConverterException(ImageConverterException.Types.IO_ERROR,"IOException thrown when writing encoded image ",ioe);
                } finally {
                    writer.dispose();
                    try{                
                        ios.close();
                        output.close();
                    } catch(IOException ignored){}
                    writer = null;
                    ios = null;
                    output = null;
                }
            }
        }
        images = null;
        params = null;
        return outputImage; 
    }  
    
    private static BufferedImage toIndexColorModel(BufferedImage image, ImageConverterParams params) throws ImageConverterException{  
        if(params.getInternalVariables().getCm() == null){
            PlanarImage surrogateImage = PlanarImage.wrapRenderedImage(image);
            ParameterBlock pb = new ParameterBlock();
            int w = surrogateImage.getWidth();
            int h = surrogateImage.getHeight();
    
            BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
    
            WritableRaster wr = bi.getWritableTile(0, 0);
            WritableRaster wr3 = wr.createWritableChild(0, 0, w, h, 0, 0, new int[] { 0, 1, 2 });
    
            wr3.setRect(surrogateImage.getData());
            bi.releaseWritableTile(0, 0);
            surrogateImage = PlanarImage.wrapRenderedImage(bi);
    
            pb.removeParameters();
            pb.removeSources();
            if(params.getInternalVariables().getOldFormat().equalsIgnoreCase("gif")){
                pb.addSource(surrogateImage);
            } else {
                pb.addSource(surrogateImage).add(ColorQuantizerDescriptor.MEDIANCUT).add(new Integer(256));
            }
            
            LookupTableJAI colorMap = (LookupTableJAI)JAI.create("ColorQuantizer", pb).getProperty("LUT");
            
            int tableLength = colorMap.getNumEntries();
            // Circumventing bug in JAI gifwriter not accepting colortable with length less than 256, filling up array to become 256long
            
            byte[][] newTable = null;
            if(tableLength != 256) {
                newTable = new byte[3][256];
                for(int i = 0; i < 3; i++) {
                    System.arraycopy(colorMap.getByteData()[ i ], 0,
                            newTable[ i ], 0, tableLength);
                }
                newTable[0][tableLength] = (byte)0xFF;
                newTable[1][tableLength] = (byte)0xFF;
                newTable[2][tableLength] = (byte)0xFF;
                colorMap = new LookupTableJAI(newTable);
            }                        
            
            KernelJAI ditherMask = KernelJAI.ERROR_FILTER_FLOYD_STEINBERG;
            
            ColorModel cm = null;
            if(params.getInternalVariables().getTransparentColor() != null){
                int transIndex = getIndexOfColor(colorMap, tableLength,params.getInternalVariables().getTransparentColor());
                if(transIndex == 256){
                    if(tableLength != 255){
                        Color trans = params.getInternalVariables().getTransparentColor();
                        newTable[0][tableLength+1] = (byte)0x00;
                        newTable[1][tableLength+1] = (byte)0x00;
                        newTable[2][tableLength+1] = (byte)0x00;
                    } else {
                        transIndex = tableLength;
                    }
                }                
                GIFImageMetadata [] metaDataTable = params.getInternalVariables().getImageMetadata();
                for(int i = 0; i < metaDataTable.length; i++){
                    metaDataTable[i].transparentColorIndex = transIndex;                    
                }                                
                params.getInternalVariables().setImageMetadata(metaDataTable);                
                cm = new IndexColorModel(8, colorMap.getByteData()[0].length, colorMap.getByteData()[0], colorMap.getByteData()[1], colorMap.getByteData()[2], transIndex);
            } else {
                cm = new IndexColorModel(8, colorMap.getByteData()[0].length, colorMap.getByteData()[0], colorMap.getByteData()[1], colorMap.getByteData()[2]);
            }    
            PlanarImage op = PlanarImage.wrapRenderedImage(new BufferedImage(w, h, BufferedImage.TYPE_BYTE_INDEXED,(IndexColorModel) cm));

            ImageLayout layout = new ImageLayout();
            layout.setTileWidth(image.getWidth());
            layout.setTileHeight(image.getHeight());
            layout.setColorModel(cm);
            layout.setSampleModel(op.getSampleModel());
    
            RenderingHints rh = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, layout);
    
            pb.removeParameters();
            pb.removeSources();
            pb.addSource(surrogateImage);
            pb.add(colorMap);
            pb.add(ditherMask);
            op= JAI.create("errordiffusion",pb,rh);
            surrogateImage=(PlanarImage)op;   
            image = surrogateImage.getAsBufferedImage();
            params.getInternalVariables().setCm(image.getColorModel());
            pb = null;
            wr = null;
            wr3 = null;
            bi = null;
            surrogateImage = null;
            cm = null;
        } else {                        
            ColorModel cm = params.getInternalVariables().getCm();
            BufferedImage newImage = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_BYTE_INDEXED,(IndexColorModel)cm);
            Graphics2D gfx = newImage.createGraphics();
            gfx.drawImage(image,0,0,null);
            gfx.dispose();   
            image = null;
            image = newImage;
            newImage = null;
        }
        return image;
    } 
    
    private static int getIndexOfColor(LookupTableJAI colorMap, int tableLength, Color color){
        int cIndex = 256;
        for(int i = 0; i < tableLength; i++){
            if(color.getRed() == (colorMap.getByteData()[0][i] & 0xff) &&
                    color.getGreen() == (colorMap.getByteData()[1][i] & 0xff) &&
                    color.getBlue() == (colorMap.getByteData()[2][i] & 0xff)){
                        cIndex = i;
                        break;
            }
        }
        return cIndex;
    }        
}
