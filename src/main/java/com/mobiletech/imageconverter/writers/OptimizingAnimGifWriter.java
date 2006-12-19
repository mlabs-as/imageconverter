package com.mobiletech.imageconverter.writers;

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import com.mobiletech.imageconverter.exception.ImageConverterException;
import com.mobiletech.imageconverter.io.ImageEncoder;
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
	          	 int[] marker = new int[]{0,255,0,0};
	          	 int count = 0;
	          	//pixel = current.getPixel(1, 1, pixel);
	          	//System.out.println("ARGB, A:"+pixel[0]+" R:"+pixel[1]+" G:"+pixel[2]+" B:"+pixel[3]);
	          	//pixel = base.getPixel(1, 1, pixel);
	          	//System.out.println("ARGB, A:"+pixel[0]+" R:"+pixel[1]+" G:"+pixel[2]+" B:"+pixel[3]);
	          	 int x1 = 0, y1 = 0, x2 = w, y2 = h;
	          	for(int i = 0; i < h; i++){
	         		 for(int e = 0; e < w; e++){
	         			pixel = current.getPixel(e, i, pixel);
	         			opixel = base.getPixel(e, i, opixel);
	         			if(pixel[0] == opixel[0]
	         			    && pixel[1] == opixel[1]
	         			    && pixel[2] == opixel[2]
	         			    && pixel[3] == opixel[3]){		          				
	         				//current.setPixel(e, 1, marker);
	         				count++;
	         				//current.setPixel(e, i, marker);	          				
	         			}          			
	         		 }
	         		 if(count == w){
	         			 y1 = i;
	         		 } else {
	         			 break;
	         		 }
	         	 }
	          	//System.out.println("changed "+count);
	          	baseFrame.flush();
		        baseFrame.setData(tempFrame.copyData(null));
	          	//baseFrame.setData(baseFrame.copyData(null));
			}
			if(false){				
				 BufferedImage tempFrame = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
				 tempFrame.setData(image.copyData(null));
				 WritableRaster base = baseFrame.getRaster();				 
	        	 WritableRaster current = image.getRaster();
	          	 int w = image.getWidth();
	          	 int h = image.getHeight();
	          	 int[] pixel = new int[4];
	          	 int[] opixel = new int[4];
	          	 int[] marker = new int[]{0,255,0,0};
	          	 int count = 0;
	          	//pixel = current.getPixel(1, 1, pixel);
	          	//System.out.println("ARGB, A:"+pixel[0]+" R:"+pixel[1]+" G:"+pixel[2]+" B:"+pixel[3]);
	          	//pixel = base.getPixel(1, 1, pixel);
	          	//System.out.println("ARGB, A:"+pixel[0]+" R:"+pixel[1]+" G:"+pixel[2]+" B:"+pixel[3]);
	          	for(int i = 0; i < h; i++){
	         		 for(int e = 0; e < w; e++){
	         			pixel = current.getPixel(e, i, pixel);
	         			opixel = base.getPixel(e, i, opixel);
	         			if(pixel[0] == opixel[0]
	         			    && pixel[1] == opixel[1]
	         			    && pixel[2] == opixel[2]
	         			    && pixel[3] == opixel[3]){		          				
	         				current.setPixel(e, i, marker);
	         				count++;
	         				//current.setPixel(e, i, marker);	          				
	         			}          			
	         		 }
	         	 }
	          	//System.out.println("changed "+count);
	          	baseFrame.flush();
		        baseFrame.setData(tempFrame.copyData(null));
	          	//baseFrame.setData(baseFrame.copyData(null));
			}
			if(false){				
				 BufferedImage tempFrame = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
				 tempFrame.setData(image.copyData(null));
				 
				 WritableRaster base = baseFrame.getRaster();				 
	        	 WritableRaster current = image.getRaster();
	        	 
	        	 IndexColorModel icm = (IndexColorModel)image.getColorModel();
	        	 int [] RGB2 = new int[icm.getMapSize()];
		         icm.getRGBs(RGB2);
	          	 byte [] R = new byte [icm.getMapSize()];
	          	 byte [] G = new byte [icm.getMapSize()];
	          	 byte [] B = new byte [icm.getMapSize()];
	          	 
	          	IndexColorModel icm2 = (IndexColorModel)baseFrame.getColorModel();
	          	int [] RGB = new int[icm2.getMapSize()];
	          	icm2.getRGBs(RGB);
	          	 byte [] R2 = new byte [icm2.getMapSize()];
	          	 byte [] G2 = new byte [icm2.getMapSize()];
	          	 byte [] B2 = new byte [icm2.getMapSize()];
	          	 
	          	 int w = image.getWidth();
	          	 int h = image.getHeight();
	          	 int[] pixel = new int[4];
	          	 int[] opixel = new int[4];
	          	 int[] marker = new int[]{1,0,0,0};
	          	 int count = 0;
	          	//pixel = current.getPixel(1, 1, pixel);
	          	//System.out.println("ARGB, A:"+pixel[0]+" R:"+pixel[1]+" G:"+pixel[2]+" B:"+pixel[3]);
	          	//pixel = base.getPixel(1, 1, pixel);
	          	//System.out.println("ARGB, A:"+(R[5] & 0xFF) +" R:"+R2[5]+" G:"+G[5]+" B:"+G2[5]);
	          	for(int i = 0; i < h; i++){
	         		 for(int e = 0; e < w; e++){
	         			pixel = current.getPixel(e, i, pixel);
	         			opixel = base.getPixel(e, i, opixel);
	         			if(RGB[pixel[0]] == RGB2[opixel[0]]){
	         			   //&& G[pixel[0]] == G2[opixel[0]]
	         			   //&& B[pixel[0]] == B2[opixel[0]]){
	         				current.setPixel(e, i, marker);
	         			}	         			      	
	         		 }
	         	 }
	          	//System.out.println("changed "+count);
	          	baseFrame.flush();
		        baseFrame.setData(tempFrame.copyData(null));
	          	//baseFrame.setData(baseFrame.copyData(null));
			}
			if(false){
			 WritableRaster base = baseFrame.getRaster();
        	 WritableRaster current = image.getRaster();
          	 int w = image.getWidth();
          	 int h = image.getHeight();
          	 
          	//System.out.println("Trans Pixel: A: "+col.getAlpha()+" R: "+col.getRed()+" G: "+col.getGreen()+" B: "+col.getBlue());

          	 int[] pixel = new int[4];
          	 int[] opixel = new int[4];
          	 int[] marker = new int[]{40,0,0,0};
          	 IndexColorModel icm = (IndexColorModel)image.getColorModel();
          	 //int [] RGB = new int[icm.getMapSize()];
          	byte [] R = new byte [icm.getMapSize()];
          	byte [] G = new byte [icm.getMapSize()];
          	byte [] B = new byte [icm.getMapSize()];
          	 //icm.getRGBs(RGB);
          	icm.getReds(R);
          	icm.getGreens(G);
          	icm.getBlues(B);
          	
          	int trans = icm.getTransparentPixel();
          	//System.out.println("Transparent Pixel Index: "+trans);
          	int difference = -1;
          	int tmp = -1;
          	for(int i = 0; i < R.length-1; i++){
          		if(difference < 0){
          			difference = (R[i]+G[i]+B[i])-(R[i+1]+G[i+1]+B[i+1]);
          			if(difference < 0){
          				difference = -difference;
          			}
          		} else {
          			tmp = (R[i]+G[i]+B[i])-(R[i+1]+G[i+1]+B[i+1]);
          			if(tmp < 0){
          				tmp = -tmp;
          			}
          			if(tmp < difference){
          				difference = tmp;
          			}
          		}
          	}
          	System.out.println("Smallest Difference: "+difference);
          	//System.out.println("Transparent Pixel RGB, R:"+(R[trans] & 0xFF)+" G:"+(G[trans] & 0xFF)+" B:"+(B[trans] & 0xFF));
          	//System.out.println("RGB Size: "+RGB.length);
          	//System.out.println("ARGB, A:"+RGB[0]+" R:"+RGB[0]+" G:"+RGB[0]+" B:"+RGB[0]);
          	//System.out.println("RGB, R:"+(R[0] & 0xFF)+" G:"+(G[0] & 0xFF)+" B:"+(B[0] & 0xFF));
          	
          	 /*
          	pixel = current.getPixel(10, 10, pixel);
          	for(int i = 0; i < pixel.length; i++){
          		System.out.println("Pixel "+i+":"+pixel[i]);
          	}
          	*/
          	/*
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
          	 /*
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
         	 }*/
			}
		}
		image = ImageEncoder.prepareForConversion(image, imageParams);
		try {                               
			writer.writeToSequence(new IIOImage(image,null,metaData),writer.getDefaultWriteParam());                                      
        } catch(IOException e){
        	closeStreams();
            throw new ImageConverterException(ImageConverterException.Types.IO_ERROR,"IOException thrown when writing encoded image ",e);
        }        
        counter++;
	}
	
}