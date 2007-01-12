package com.mobiletech.imageconverter.writers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.media.jai.JAI;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.operator.ColorQuantizerDescriptor;

import com.mobiletech.imageconverter.exception.ImageConverterException;
import com.mobiletech.imageconverter.io.ImageEncoder;
import com.mobiletech.imageconverter.jaiextensions.ToIndexColorImageOpDescriptor;
import com.mobiletech.imageconverter.modifiers.ImageCropper;
import com.mobiletech.imageconverter.vo.ImageConverterParams;
import com.sun.imageio.plugins.gif.GIFImageMetadata;

public class OptimizingAnimGifWriter implements DexImageWriter{
	private byte[] result= null;
	private ImageWriter writer = null; // NEEDED
	private ImageOutputStream ios = null;
	private ByteArrayOutputStream output = null;
	private ImageConverterParams imageParams = null;
	private int counter = 0;
	private BufferedImage baseFrame = null;
	private LookupTableJAI colorMap = null;
	private ColorModel cm = null;
	
	private OptimizingAnimGifWriter(){}
	
	public OptimizingAnimGifWriter(ImageWriter writer, ImageConverterParams imageParams){
		output = new ByteArrayOutputStream();
		this.writer = writer;
		this.imageParams = imageParams;
		this.initialize();
	}
	public boolean canWriteMore(){
		return true;
	}
	public byte[] getByte() throws ImageConverterException{
		try {
			writer.endWriteSequence();                  
			ios.flush();
			output.flush();
			result = output.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	public void dispose(){		
		if(writer != null){
			writer.dispose();
			writer = null;
		}
        closeStreams();    		
	}
	private void closeStreams(){
        try{                
            ios.close();
            output.close();
        } catch(IOException ignored){}
        writer = null;
        ios = null;
        output = null;
	}
	private void initialize(){
        try {
			ios = ImageIO.createImageOutputStream(output);      
			writer.setOutput(ios);        
			writer.prepareWriteSequence(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void writeNext(BufferedImage image) throws ImageConverterException{
		GIFImageMetadata metaData =  imageParams.getInternalVariables().getImageMetadata()[counter];
		//image = ImageEncoder.prepareForConversion(image, imageParams);
		if(true){
			image = cropForProgressiveEncoding(image, metaData);
		}		
		BufferedImage buffered = new BufferedImage( image.getWidth( null ), image.getHeight( null ), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = buffered.createGraphics();
        if(imageParams.getInternalVariables().getTransparentColor() == null || !imageParams.getFormat().equalsIgnoreCase("gif")){                             
            g2.setColor(Color.WHITE);
            g2.fillRect(0,0,image.getWidth( null ),image.getHeight( null ));
        } else {
            g2.setColor(imageParams.getInternalVariables().getTransparentColor());
            g2.fillRect(0,0,image.getWidth( null ),image.getHeight( null ));
        }
       
        g2.drawImage( image, null, null );            
        image = buffered;
        g2.dispose();
        g2 = null;
        buffered = null;  
        
		if(colorMap == null){						
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
	        
	        pb.addSource(surrogateImage).add(ColorQuantizerDescriptor.MEDIANCUT).add(new Integer(imageParams.getInternalVariables().getGifNumColors()));
	       	        
	        colorMap = (LookupTableJAI)JAI.create("ColorQuantizer", pb).getProperty("LUT");
	       // image = JAI.create("ColorQuantizer", pb).getAsBufferedImage();
			//int numEntries = ((IndexColorModel)image.getColorModel()).getMapSize();
	        //System.out.println("After number of colors in global colortable: "+numEntries);
		} 
		Object o = null;
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
        	
            if(cm == null){
            	pb.addSource(surrogateImage).add(ToIndexColorImageOpDescriptor.MEDIANCUT).add(new Integer(32)).add(null).add(null).add(new Integer(6)).add(new Integer(1)).add(imageParams.getInternalVariables().getTransparentColor()).add(colorMap);            	
            } else {
            	pb.addSource(surrogateImage).add(ToIndexColorImageOpDescriptor.MEDIANCUT).add(new Integer(32)).add(null).add(null).add(new Integer(6)).add(new Integer(1)).add(imageParams.getInternalVariables().getTransparentColor()).add(colorMap).add(cm);
            }
            //p.add(new Integer(210));
            // Threshold the image with the new operator.
            PlanarImage output = JAI.create("toIndexColorImage",pb,null);
            if(cm == null){
            	o = output.getProperty("colorModel");
            	if(!(o instanceof IndexColorModel)){
            		o = output.getColorModel();
            	}
            	//System.out.println("O"+o.getClass().getName());
            	cm = (ColorModel)o;
            }
            image = output.getAsBufferedImage();            
		}
		//image = ImageEncoder.prepareForConversion(image, imageParams, colorMap);
		//metaData.localColorTable = null;
		if(false){
			image = cropForProgressiveEncoding(image, metaData);
		}
		try {                               
			writer.writeToSequence(new IIOImage(image,null,metaData),writer.getDefaultWriteParam());                                      
        } catch(IOException e){
        	closeStreams();
            throw new ImageConverterException(ImageConverterException.Types.IO_ERROR,"IOException thrown when writing encoded image ",e);
        }        
        counter++;
	}
	
	private BufferedImage cropForProgressiveEncoding(BufferedImage image, GIFImageMetadata metaData){
		if(baseFrame == null){
			baseFrame = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
			baseFrame.setData(image.copyData(null));
		} else {
			if(true){				
				 BufferedImage tempFrame = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
				 tempFrame.setData(image.copyData(null));
				 WritableRaster base = baseFrame.getRaster();				 
	        	 WritableRaster current = image.getRaster();
	          	 int w = image.getWidth();
	          	 int h = image.getHeight();
	          	 int[] pixel = new int[4];
	          	 int[] opixel = new int[4];	          	 
	          	 int count = 0;
	          	 int stage = 1;

	          	int x1 = 0, y1 = 0, x2 = w, y2 = h;
	          	
	          	int outerStart = 0;
	          	int outerLimit = h;
	          	
	          	int innerStart = 0;	          	
	          	int innerLimit = w;
	          	
	          	boolean xScan = false;
	          	boolean breakIt = false;
	          	boolean innerNegative = false, outerNegative = false;
	          	
	          	while(stage <= 4){
		          	for(int x = outerStart; ; ){
		          		 if(outerNegative){
		         			 if(x <= outerLimit){
		         				 breakIt = true;
		         			 }
	         			 } else {
	         				if(x >= outerLimit){
	         					breakIt = true;
		         			 } 
	         			 }
		         		 for(int y = innerStart; ; ){
		         			 if(innerNegative){
			         			 if(y <= innerLimit){
			         				 break;
			         			 }
		         			 } else {
		         				if(y >= innerLimit){
			         				 break;
			         			 } 
		         			 }		         			
							if(xScan){
								pixel = current.getPixel(x, y, pixel);
								opixel = base.getPixel(x, y, opixel);
							 } else {
								pixel = current.getPixel(y, x, pixel);
								opixel = base.getPixel(y, x, opixel);
							 }							 
		         			if(pixel[0] == opixel[0] &&
		         					pixel[1] == opixel[1] &&
		         					pixel[2] == opixel[2] &&
		         					pixel[3] == opixel[3]){		          				
		         				count++;          				
		         			} 
		         			 if(innerNegative){
		         				 y--;
		         			 } else {
		         				 y++;
		         			 }
		         		 }
		         		if(outerNegative){
	         				 x--;
	         			 } else {
	         				 x++;
	         			 }
		         		switch(stage){
			         		case 1:
				         		if((count-w*y1) == w){
				         			y1++;
				   	         	} else {
				   	         		outerStart = 0;
				   	         		outerLimit = w;
				   	         		innerStart = y1;
				   	         		innerLimit = h;
				   	         		xScan = true;
				   	         		count = 0;
				   	         		stage++;
				   	         		breakIt = true;
				   	         	}
				         		break;
			         		case 2:
				         		if(count-((h-y1)*x1) == (h-y1)){
				         			x1++;
				   	         	} else {
					   	         	outerStart = h-1;
				   	         		outerLimit = y1;
				   	         		innerStart = x1;
				   	         		innerLimit = w;
				   	         		xScan = false;
				   	         		outerNegative = true;
				   	         		count = 0;
				   	         		stage++;
				   	         		breakIt = true;
				   	         	}
				         		break;
		         			case 3:
				         		if(count == (w-x1)){				         			
				         			y2--;
				         			count = 0;
				   	         	} else {	
					   	         	outerStart = w-1;
				   	         		outerLimit = x1;
				   	         		innerStart = y1;
				   	         		innerLimit = y2;
				   	         		xScan = true;
				   	         		outerNegative = true;
				   	         		count = 0;
				   	         		stage++;
				   	         		breakIt = true;
				   	         	}
				         		break;	
		         			case 4:
				         		if(count == h-(y1+(h-y2))){				         			
				         			x2--;
				         			count = 0;
				   	         	} else {	
				   	         		stage++;
				   	         		breakIt = true;
				   	         	}
				         		break;	
		         		 	default:
		         		 		stage++;
			         		}	         			
	         			if(breakIt){
			          		breakIt = false;
			          		break;		          		
			          	}
		          	}	     
		          
	         	}
	          	//System.out.println("changed "+count);
	          	//System.out.println("first point (X,Y) " + x1 + "," + y1);
	          	//System.out.println("second point (X,Y) " + x2 + "," + y2);
	          	baseFrame.flush();
		        baseFrame.setData(tempFrame.copyData(null));
		        int width = (w - x1) - (w - x2);
		        int height = (h - y1) - (h - y2);
		        image = ImageCropper.cropImage(image, x1, y1, width, height);
		        metaData.imageLeftPosition = x1;
		        metaData.imageTopPosition = y1;
	          	//baseFrame.setData(baseFrame.copyData(null));
			}
		}
		return image;
	}
	
}