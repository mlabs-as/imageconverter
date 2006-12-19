package com.mobiletech.imageconverter.readers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.imageio.IIOImage;
import javax.imageio.ImageReader;

import com.mobiletech.imageconverter.exception.ImageConverterException;
import com.mobiletech.imageconverter.modifiers.ImageScaler;
import com.mobiletech.imageconverter.util.ImageUtil;
import com.mobiletech.imageconverter.util.Pixel;
import com.mobiletech.imageconverter.vo.ImageConverterParams;
import com.sun.imageio.plugins.gif.GIFImageMetadata;
import com.sun.imageio.plugins.gif.GIFStreamMetadata;

public class FlatteningAnimGifReader implements DexImageReader{
	private ImageReader reader = null; // NEEDED
	private BufferedImage previousImage = null;
	private BufferedImage baseImage = null;
	private GIFStreamMetadata streamMetaData = null;
	private int counter = 0;
	private int numImages = 0;
    private Color backgroundColor = Color.white; 
    private Color transparentColor = null;
	private ImageConverterParams imageParams = null;
	
	private FlatteningAnimGifReader(){}
	
	public FlatteningAnimGifReader(ImageReader reader, ImageConverterParams imageParams){
		this.reader = reader;
		try {
			this.numImages = reader.getNumImages(true);
		} catch (IOException e) {
			this.numImages = 1;
		}
		this.imageParams = imageParams;
	}
	public FlatteningAnimGifReader(ImageReader reader, ImageConverterParams imageParams, int numImages){
		this.reader = reader;		
		this.numImages = numImages;		
		this.imageParams = imageParams;
	}
	public void dispose(){
		previousImage = null;
		streamMetaData = null;
		backgroundColor = null;
		transparentColor = null;
		imageParams = null;
		reader.dispose();
		reader = null;
	}
	private void initialize(IIOImage first){
        byte [] colorTable = null;
        try {
			streamMetaData = (GIFStreamMetadata)reader.getStreamMetadata();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		imageParams.getInternalVariables().setOkToBlur(false);
        previousImage = new BufferedImage(streamMetaData.logicalScreenWidth, streamMetaData.logicalScreenHeight, BufferedImage.TYPE_INT_ARGB);
        baseImage = new BufferedImage(streamMetaData.logicalScreenWidth, streamMetaData.logicalScreenHeight, BufferedImage.TYPE_INT_ARGB);   
        imageParams.getInternalVariables().setImageMetadata(new GIFImageMetadata[numImages]);
        
        // Storing metadata for later use                             
        GIFImageMetadata metaData = (GIFImageMetadata)first.getMetadata(); 
        
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
                    imageParams.getInternalVariables().setTransparentColor(ImageUtil.getUniqueColor(colorTable,transparentColor));
                    transparentColor = imageParams.getInternalVariables().getTransparentColor();
                    imageParams.setNumberOfColors(6868);
                    found++; 
                }
                if(found == 2){
                    break;
                }
            }
            //This detects a known problem, for which no current solution is known, thus its not in use    
            if(false){//transparentColor != null){
                for (int e = 0; e < numEntries; e++) {
                    if(e == metaData.transparentColorIndex) {
                        continue;
                    }
                    if(transparentColor.getRed() == (colorTable[3*e] & 0xff) &&
                            transparentColor.getGreen() == (colorTable[3*e+1] & 0xff) &&
                            transparentColor.getBlue() == (colorTable[3*e+2] & 0xff)){  
                        imageParams.getInternalVariables().setTransparentColor(ImageUtil.getUniqueColor(colorTable,transparentColor));
                        transparentColor = imageParams.getInternalVariables().getTransparentColor();
                        imageParams.setNumberOfColors(6868);
                        break;
                    }
                }
            }
        }
	}
	private void afterInitialize(BufferedImage firstResult){
        if(firstResult.getColorModel().hasAlpha()&& imageParams.getInternalVariables().getTransparentColor() == null){
            //imageParams.getInternalVariables().setTransparentColor(ImageUtil.getUniqueColor(colorTable,transparentColor));
        	imageParams.getInternalVariables().setTransparentColor(ImageUtil.getUniqueColor(transparentColor));
        	transparentColor = imageParams.getInternalVariables().getTransparentColor();
            imageParams.setNumberOfColors(6868);
        }
	}
	
	public BufferedImage getNext() throws ImageConverterException{
		imageParams.getInternalVariables().setOkToBlur(true);
		BufferedImage resultImage = null;           		
		IIOImage iioimage = null;
		try {
			iioimage = reader.readAll(counter, null);
		} catch(IOException ioe){
	        throw new ImageConverterException(ImageConverterException.Types.IO_ERROR,"IOException thrown when reading from InputByteStream",ioe);
	    }
		if(counter == 0){
			initialize(iioimage);
		}
		Graphics2D resG = null;
		
		RenderedImage r = iioimage.getRenderedImage();
             
		GIFImageMetadata metaData = (GIFImageMetadata)iioimage.getMetadata(); 

         resultImage = new BufferedImage(streamMetaData.logicalScreenWidth, streamMetaData.logicalScreenHeight, BufferedImage.TYPE_INT_ARGB);
         
         // Disposal of gif animation frames                          
         switch(metaData.disposalMethod){
             case 0: // "None"
                 // Do Nothing
                 //noneCounter++;
                 break;
             case 1: // "doNotDispose"
                 previousImage.flush();
                 previousImage.setData(resultImage.copyData(null));
                 break;
             case 2: // "restoreToBackgroundColor"                                                  
                 Graphics2D g = resultImage.createGraphics(); 
                 g.setPaintMode(); 

                 if(metaData.transparentColorFlag){    
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
                              
         resultImage.flush();
         // Flattening Code
         if(true){
        	 if(counter == 0){
	        	 baseImage.setData(resultImage.copyData(null));
        	 } else {
        		 WritableRaster base = baseImage.getRaster();
	        	 WritableRaster current = resultImage.getRaster();
	          	 int w = resultImage.getWidth();
	          	 int h = resultImage.getHeight();
	          	 
	          	 int[] pixel = new int[4];

	          	 for(int i = 0; i < h; i++){
	          		 for(int e = 0; e < w; e++){
	          			pixel = current.getPixel(e, i, pixel);
	          			if(pixel[3] == 0){
	          				pixel = base.getPixel(e, i, pixel);
	          				current.setPixel(e, i, pixel);          				
	          			}          			
	          		 }
	          	 }
	          	 baseImage.setData(resultImage.copyData(null));
        	 }
         }
         if(false){
        	 boolean backCorrection = false;
        	 boolean optimization = false;
	         if(counter == 0){
	        	 baseImage.setData(resultImage.copyData(null));
	        	 if(backCorrection){
	        		 int bl = (int) Math.floor(1 / ImageScaler.getResizeScale(resultImage.getWidth(), resultImage.getHeight(), imageParams.getWidth(), imageParams.getHeight()));
	        		// resultImage = ImageScaler.blur(bl,resultImage);
	        	 }
	        	 if(false){//optimization){
	        		 int bl = (int) Math.floor(1 / ImageScaler.getResizeScale(resultImage.getWidth(), resultImage.getHeight(), imageParams.getWidth(), imageParams.getHeight()));
	        		// resultImage = ImageScaler.blur(bl,resultImage);
	        	 }
	         } else {	        	 
	        	 WritableRaster base = baseImage.getRaster();
	        	 WritableRaster current = resultImage.getRaster();
	          	 int w = resultImage.getWidth();
	          	 int h = resultImage.getHeight();
	          	 
	          	//System.out.println("Trans Pixel: A: "+col.getAlpha()+" R: "+col.getRed()+" G: "+col.getGreen()+" B: "+col.getBlue());
	          	 int result = 0;
	          	 int[] pixel = new int[4];
	          	 int[] marker = new int[]{255,0,255,255};
	          	LinkedList<Pixel> protect = new LinkedList<Pixel>();
          		
	          	if(counter == 15){
	          		pixel = current.getPixel(227, 138, pixel);
	          		//System.out.println("Trans Pixel: A: "+pixel[0]+" R: "+pixel[1]+" G: "+pixel[2]+" B: "+pixel[3]);
	          	}
	          	 for(int i = 0; i < h; i++){
	          		 for(int e = 0; e < w; e++){
	          			pixel = current.getPixel(e, i, pixel);
	          			if(pixel[3] == 0){
	          				if(backCorrection){
	          					protect.add(new Pixel(e, i, new int[]{pixel[0],pixel[1],pixel[2],pixel[3]}));
	          				}
	          				pixel = base.getPixel(e, i, pixel);
	          				current.setPixel(e, i, pixel);
	          				//current.setPixel(e, i, marker);	          				
	          			}          			
	          		 }
	          	 } 	          	
	          	if(optimization){
	          		base = baseImage.getRaster();		        	
	          		int bl = (int) Math.floor(1 / ImageScaler.getResizeScale(resultImage.getWidth(), resultImage.getHeight(), imageParams.getWidth(), imageParams.getHeight()));
	          		//resultImage = ImageScaler.blur(bl,resultImage);
	          		current = resultImage.getRaster();
	          		marker = new int[]{0,255,255,0};
	          		int[] opixel = new int[4];
	          		for(int i = 0; i < h; i++){
		          		 for(int e = 0; e < w; e++){
		          			pixel = current.getPixel(e, i, pixel);
		          			opixel = base.getPixel(e, i, opixel);
		          			if(pixel[0] == opixel[0]
		          			    && pixel[1] == opixel[1]
		          			    && pixel[2] == opixel[2]
		          			    && pixel[3] == opixel[3]){		          				
		          				current.setPixel(e, i, marker);
		          				//current.setPixel(e, i, marker);	          				
		          			}          			
		          		 }
		          	 }
	          		baseImage.setData(resultImage.copyData(null));
	          	} else {
	          		baseImage.setData(resultImage.copyData(null));
	          	}
	          	if(backCorrection){
	          		int bl = (int) Math.floor(1 / ImageScaler.getResizeScale(resultImage.getWidth(), resultImage.getHeight(), imageParams.getWidth(), imageParams.getHeight()));
	          		//resultImage = ImageScaler.blur(bl,resultImage);
	          		current = resultImage.getRaster();
	          		Iterator<Pixel> ite = protect.iterator();
	    	        Pixel tmp = null;
	    	        while(ite.hasNext()){
	    	        	tmp = ite.next();
	    	        	if(counter == 15){
	    	        	pixel = current.getPixel(tmp.getX(), tmp.getY(), pixel);
	    	        	//System.out.println("Before Pixel: A: "+pixel[0]+" R: "+pixel[1]+" G: "+pixel[2]+" B: "+pixel[3]);
	    	        	}
	    	        	current.setPixel(tmp.getX(), tmp.getY(), tmp.getRgb());
	    	        	if(counter == 15){
	    	        	pixel = current.getPixel(tmp.getX(), tmp.getY(), pixel);
	    	        	//System.out.println("After Pixel: A: "+pixel[0]+" R: "+pixel[1]+" G: "+pixel[2]+" B: "+pixel[3]);
	    	        	}
	    	        }
	          	}
	         }
         }
         metaData.imageTopPosition = 0;
         metaData.imageLeftPosition = 0;
         if(metaData.localColorTable != null){
        	 metaData.localColorTable = null; // if the frame has a localcolortable, this will override the new global colortable and cause problems
         }
         
         imageParams.getInternalVariables().getImageMetadata()[counter] = metaData;
         
                      
        if(counter == 0){
        	afterInitialize(resultImage);
        }
        counter++;
        if(false){//metaData.disposalMethod == 0){
       	 WritableRaster wr = resultImage.getAlphaRaster();
       	 int w = resultImage.getWidth();
       	 int h = resultImage.getHeight();
       	 int result = 0;
       	 int[] pixel = new int[3];
       	 for(int i = 0; i < h; i++){
       		 for(int e = 0; e < w; e++){
       			pixel = wr.getPixel(e, i, pixel);
       			if(pixel[0] != 255){
       				i = h;
       				e = w;
       				break;
       			} else {
       				result++;
       			}
       		 }
       	 }
       	 if(result == (w * h)){
       		 imageParams.getInternalVariables().setOkToBlur(true);
       	 }
       	metaData = null;
       	//System.out.println("result: "+result);
       	//System.out.println("resultss: "+(w*h));
        }
        imageParams.getInternalVariables().setOkToBlur(true);
		return resultImage;
	}
	public boolean hasMore(){		
		return counter < numImages;
	}		 
}