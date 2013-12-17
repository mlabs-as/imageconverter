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
import com.mobiletech.imageconverter.jaiextensions.ToIndexColorImageOpDescriptor;
import com.mobiletech.imageconverter.util.ImageUtil;
import com.mobiletech.imageconverter.vo.ImageConverterParams;
import com.sun.imageio.plugins.gif.GIFImageMetadata;
import com.sun.media.jai.codec.JPEGEncodeParam;
import com.sun.media.jai.codecimpl.JPEGCodec;
import com.sun.media.jai.codecimpl.JPEGImageEncoder;

public class ImageEncoder {
	private static void printPixel(int [] pixel){
		int ps = 0;
		for(int i = 0; i < pixel.length; i++){
			ps += pixel[i];			
		}
		/*
		 *             for(int i = 0; i < w; i++){
            	for(int e = 0; e < h; e++){
            		printPixel(ar.getPixel(i, e, pixel));
            	}
            	System.out.println("");
            }
		 * 
		 * 
		 * 
		if(ps != 255 && ps != 0){
			System.out.println(pixel[0]+" "+pixel[1]+" "+pixel[2]);
		}
		*/
		
		System.out.print(" ");	
		if(ps < 100){
			System.out.print(" ");	
		}
		if(ps < 10){
			System.out.print(" ");	
		}
		System.out.print(ps);
		
	}
	public static BufferedImage prepareForConversion(BufferedImage inImage, ImageConverterParams params) throws ImageConverterException{
		return prepareForConversion(inImage, params, null);
	}
    public static BufferedImage prepareForConversion(BufferedImage inImage, ImageConverterParams params, LookupTableJAI table) throws ImageConverterException{
    	String format = params.getFormat();
    	if(format.equalsIgnoreCase("png") && params.getInternalVariables().getOldFormat().equalsIgnoreCase("gif")){
    		return inImage;
    	} else if(format.equalsIgnoreCase("png") && params.getInternalVariables().getOldFormat().equalsIgnoreCase("png")){
        	return inImage;
        } else if(format.equalsIgnoreCase("gif") && params.getInternalVariables().getOldFormat().equalsIgnoreCase("png")){
    		WritableRaster ar = inImage.getAlphaRaster();
    		if(ar != null){
	            int w = ar.getWidth();
	            int h = ar.getHeight();
	            int[] pixel = new int[3];
	
	            for(int i = 0; i < w; i++){
	            	for(int e = 0; e < h; e++){
	            		//printPixel(ar.getPixel(i, e, pixel));
	            		pixel = ar.getPixel(i, e, pixel);
	            		if(pixel[0] > 0 && pixel[0] < 255){
	            			if(pixel[0] < 140){
	            				pixel[0] = 0;
	            				ar.setPixel(i, e, pixel);
	            			} else {
	            				pixel[0] = 255;
	            				ar.setPixel(i, e, pixel);
	            			}
	            		}
	            	}
	            }
    		}
    	}
        

        // Special case for grayscale gif images, as it was more troublesome getting it not to hit in the wrong if statements below, see 
        // Description of below if statement as to why this is problematic
        if(params.getInternalVariables().getOldFormat().equalsIgnoreCase("gif") && format.equalsIgnoreCase("gif") && params.isGrayscale()){
            return inImage;
        }
        // This code gets run for gif to gif conversion since they are converted to RGB ARGB, oddly, the images appear faulty and striped if this code
        // is not run, although in principle, this code should not be run, as it destroys the transparency of the image and replaces it with white. 
        // Possibly the error is due to the gif conversion code not being able to handle ARGB images. 
        if((params.getInternalVariables().getOldFormat().equalsIgnoreCase("gif") || params.getInternalVariables().getOldFormat().equalsIgnoreCase("png"))
                && (inImage.getType() != BufferedImage.TYPE_BYTE_INDEXED || format.compareToIgnoreCase("gif")!=0)){
            BufferedImage buffered = new BufferedImage( inImage.getWidth( null ), inImage.getHeight( null ), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = buffered.createGraphics();
            if(params.getInternalVariables().getTransparentColor() == null || !params.getFormat().equalsIgnoreCase("gif")){                             
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
            inImage = toIndexColorModel(inImage,params, table); 
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
                    	//writer.writeToSequence(new IIOImage(images[i],null,null),writer.getDefaultWriteParam());
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
    	return toIndexColorModel(image, params);
    }
    private static BufferedImage toIndexColorModel(BufferedImage image, ImageConverterParams params, LookupTableJAI table) throws ImageConverterException{  
    	if(true){
        	ToIndexColorImageOpDescriptor.register();
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
        	
            //pb.addSource(surrogateImage).add(ToIndexColorImageOpDescriptor.MEDIANCUT).add(new Integer(32)).add(null).add(null).add(new Integer(6)).add(new Integer(1)).add(params.getInternalVariables().getTransparentColor()).add(table);
            pb.addSource(surrogateImage).add(ToIndexColorImageOpDescriptor.MEDIANCUT).add(new Integer(256)).add(null).add(null).add(new Integer(6)).add(new Integer(1)).add(params.getInternalVariables().getTransparentColor());
            //p.add(new Integer(210));
            // Threshold the image with the new operator.
            PlanarImage output = JAI.create("toIndexColorImage",pb,null);
            image = output.getAsBufferedImage();
            return image;
        }
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
            int transIndex = 256;
            
            if(tableLength != 256) {
                newTable = new byte[3][256];
                for(int i = 0; i < 3; i++) {
                    System.arraycopy(colorMap.getByteData()[ i ], 0,
                            newTable[ i ], 0, tableLength);
                }
                // This fixes the problem in animated gifs where the transparent color is not present in the first frame, and
                // as such is removed from the palette, so it needs to be added again
                if(params.getInternalVariables().getTransparentColor() != null){
                    if(params.getNumberOfColors() != 6868){
                        transIndex = getIndexOfColor(colorMap, tableLength-1,params.getInternalVariables().getTransparentColor());  
                    }
                    //
                    if(transIndex == 256){
                        newTable[0][tableLength] = (byte)params.getInternalVariables().getTransparentColor().getRed();
                        newTable[1][tableLength] = (byte)params.getInternalVariables().getTransparentColor().getGreen();
                        newTable[2][tableLength] = (byte)params.getInternalVariables().getTransparentColor().getBlue();  
                        if(params.getNumberOfColors() != 6868){
                            transIndex = tableLength;
                        } else {
                            transIndex = 255;//tableLength;    
                        }                        
                        tableLength++;
                    }
                }          
                /*
                if(params.getNumberOfColors()==666){
                    newTable[0][tableLength] = (byte)params.getInternalVariables().getTransparentColor().getRed();
                    newTable[1][tableLength] = (byte)params.getInternalVariables().getTransparentColor().getGreen();
                    newTable[2][tableLength] = (byte)params.getInternalVariables().getTransparentColor().getBlue();
                    transIndex = tableLength;
                    tableLength++;
                }
                */
                // Filling the rest of the palette with some random color, this should be changed so the color that is
                // filled is a unique color
                if(params.getNumberOfColors() != 6868){
                    Color col = ImageUtil.getUniqueColor(null,null);
                    for(int i = tableLength; i < 256; i++){                        
                        newTable[0][i] = (byte)col.getRed();
                        newTable[1][i] = (byte)col.getGreen();
                        newTable[2][i] = (byte)col.getBlue();                          
                    }
                } else {
                    for(int i = tableLength; i < 256; i++){
                        newTable[0][i] = (byte)params.getInternalVariables().getTransparentColor().getRed();
                        newTable[1][i] = (byte)params.getInternalVariables().getTransparentColor().getGreen();
                        newTable[2][i] = (byte)params.getInternalVariables().getTransparentColor().getBlue();
                        /*
                        newTable[0][i] = (byte)0x00;
                        newTable[1][i] = (byte)0x00;
                        newTable[2][i] = (byte)0x00; 
                        */   
                    }    
                }
                   
                colorMap = new LookupTableJAI(newTable);
            }                        
            
            KernelJAI ditherMask = KernelJAI.ERROR_FILTER_FLOYD_STEINBERG;
            
            ColorModel cm = null;
            if(params.getInternalVariables().getTransparentColor() != null){                                        
                GIFImageMetadata [] metaDataTable = params.getInternalVariables().getImageMetadata();
                if(transIndex == 256){
                    transIndex = getIndexOfColor(colorMap, tableLength-1,params.getInternalVariables().getTransparentColor());
                }
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
            op= JAI.create("errorDiffusion",pb,rh);
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
        for(int i = tableLength; i >= 0; i--){
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