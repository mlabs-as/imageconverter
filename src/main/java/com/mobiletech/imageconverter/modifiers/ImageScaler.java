package com.mobiletech.imageconverter.modifiers;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.InterpolationBicubic;
import javax.media.jai.InterpolationBicubic2;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import com.mobiletech.imageconverter.util.ImageUtil;
import com.mobiletech.imageconverter.vo.ImageConverterParams;

public class ImageScaler {
	
	public static double getResizeScale(int inWidth, int inHeight, int inNewWidth, int inNewHeight){
	    double scale = 0.0; 

        if (((double) inNewWidth / inWidth ) < ((double) inNewHeight / inHeight)) {
            scale = (double) inNewWidth / inWidth; 
        } else { 
            scale = (double) inNewHeight / inHeight;
        }   

        return scale;
	}
	
    public static BufferedImage resizeImage(BufferedImage inImage,int height, int width,boolean noEnlargement, boolean hasTransparency, ImageConverterParams params){        
        int oldWidth = inImage.getWidth(); 
        int oldHeight = inImage.getHeight(); 

        double scale = 0.0; 

        if(params.ignoreHeight()){
            scale = (double) width / oldWidth;             
        } else {
        	if (((double) width / oldWidth ) < ((double) height / oldHeight)) {
                scale = (double) width / oldWidth; 
            } else { 
                scale = (double) height / oldHeight;
            }   	
        }
        
    	if(noEnlargement){
            if(scale > 1.0){
                return null;
            }
        }
        if(scale == 0){
        	return null;
        }
        int newWidth = 0;
        int newHeight = 0;
               
        if(params.isKeepAspectRatio()){
        	newWidth = (int) (inImage.getWidth() * scale); 
        	newHeight = (int) (inImage.getHeight() * scale); 
        } else {
        	newWidth = width;
        	newHeight = height;
        }
        
        inImage = ImageUtil.toBuffImageRGBorARGB(inImage);
        int type = inImage.getType();
        
        if(type == 0){
            type = BufferedImage.TYPE_INT_RGB;
        } 

        if(hasTransparency){
        	if (scale <= 0.8){
        		if(params.getInternalVariables().isOkToBlur()){
        			int bl = (int) Math.floor(1 / scale);
        	        inImage = blur(bl,inImage);
        	        
        	        if(params.getInternalVariables().getOldFormat().equalsIgnoreCase("png")){
                		inImage = scaleImageWithGetScaledInstance(inImage,newWidth,newHeight,type);
                		//inImage = scaleImageWithAfflineTransformOp(inImage,scale,newWidth,newHeight,type);
                		//inImage = scaleUsingJAI(inImage,scale,newWidth,newHeight,type);
                	} else {
                		inImage = scaleUsingJAI(inImage,scale,newWidth,newHeight,type, true,params.isKeepAspectRatio());
                	}
        		} else {
        			if(params.getInternalVariables().getOldFormat().equalsIgnoreCase("png")){
                		inImage = scaleImageWithGetScaledInstance(inImage,newWidth,newHeight,type);
                	} else { // should be transparent gif at this point, if quality mode, attempt back-correction
                		if(params.isFastMode()){
                			inImage = scaleUsingJAI(inImage,scale,newWidth,newHeight,type, false,params.isKeepAspectRatio());
                		} else {
                			// Get copy for back-correction
                			BufferedImage copy = new BufferedImage(inImage.getWidth(), inImage.getHeight(), inImage.getType());   
                			copy.setData(inImage.copyData(null));
                			int bl = (int) Math.floor(1 / scale);
                	        inImage = blur(bl,inImage);
                			// Back-Correction
                			WritableRaster base = copy.getRaster();
           	        	 	WritableRaster current = inImage.getRaster();
           	        	 	int w = inImage.getWidth();
           	        	 	int h = inImage.getHeight();
           	        	 	int[] pixel = new int[4];
           	        	 	int count = 0;

           	        	 	for(int i = 0; i < h; i++){
           	        	 		for(int e = 0; e < w; e++){
           	        	 			pixel = current.getPixel(e, i, pixel);
           	        	 			//System.out.println("pix: "+pixel[3]);
           	        	 			if(pixel[0] == 0 && pixel[1] == 0 && pixel[2] == 0 && pixel[3] == 0){
           	        	 				count++;
           	        	 			}
           	        	 			if(pixel[3] != 0 && pixel[3] != 255){
           	        	 				pixel = base.getPixel(e, i, pixel);
           	        	 				current.setPixel(e, i, pixel);          				
           	        	 			}          			
           	        	 		}
           	        	 	}
           	        	 Graphics2D g = copy.createGraphics(); 
                         g.setPaintMode(); 

                         if(params.getInternalVariables().getTransparentColor() == null || !params.getFormat().equalsIgnoreCase("gif")){                             
                             g.setColor(Color.WHITE);
                             g.fillRect(0,0,inImage.getWidth( null ),inImage.getHeight( null ));
                         } else {
                             g.setColor(params.getInternalVariables().getTransparentColor());
                             g.fillRect(0,0,inImage.getWidth( null ),inImage.getHeight( null ));
                         }
                         
                         //g.setColor(new Color(0,255,0)); 
                         //g.setColor(params.getInternalVariables().getTransparentColor());
                         //g.fillRect(0,0, w, h);
                         g.drawImage(inImage, 0, 0, null);
                         g.dispose();
                         inImage = copy;
                         copy = null;
           	        	 inImage = scaleUsingJAI(inImage,scale,newWidth,newHeight,type, false,params.isKeepAspectRatio());
                		}
                	}
        		}
        	} else {
        		if(!params.getInternalVariables().isOkToBlur()){
        			BufferedImage copy = new BufferedImage(inImage.getWidth(), inImage.getHeight(), inImage.getType());
        			Graphics2D g = copy.createGraphics(); 
                    g.setPaintMode(); 

                    if(params.getInternalVariables().getTransparentColor() == null || !params.getFormat().equalsIgnoreCase("gif")){                             
                        g.setColor(Color.WHITE);
                        g.fillRect(0,0,inImage.getWidth( null ),inImage.getHeight( null ));
                    } else {
                        g.setColor(params.getInternalVariables().getTransparentColor());
                        g.fillRect(0,0,inImage.getWidth( null ),inImage.getHeight( null ));
                    }
                    
                    //g.setColor(params.getInternalVariables().getTransparentColor());
                    //g.fillRect(0,0, inImage.getWidth(), inImage.getHeight());
                    g.drawImage(inImage, 0, 0, null);
                    g.dispose();
                    inImage = copy;
                    copy = null;
        		}
        		inImage = scaleUsingJAI(inImage,scale,newWidth,newHeight,type, false,params.isKeepAspectRatio());
        	}    		        
        } else if (scale <= 0.8) {
            int bl = (int) Math.floor(1 / scale);
            inImage = blur(bl,inImage);    
            inImage = scaleImageWithAfflineTransformOp(inImage,scale,newWidth,newHeight,type,params.isKeepAspectRatio());            
        } else {
            inImage = scaleImageWithGetScaledInstance(inImage,newWidth,newHeight,type);
        }                         
        return inImage;               
    }   
    
    private static BufferedImage scaleImageWithGetScaledInstance(BufferedImage inImage, int newWidth, int newHeight, int type){
        Image image3 = inImage.getScaledInstance(newWidth,newHeight,BufferedImage.SCALE_SMOOTH);
        
        if(newWidth == 0){
        	newWidth = 1;
        }
        if(newHeight == 0){
        	newHeight = 1;
        }
        BufferedImage theNewImage2 = new BufferedImage( newWidth, newHeight, type ); 
        Graphics2D g2 = theNewImage2.createGraphics(); 
        
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);         
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2.setComposite(AlphaComposite.Src);
        g2.drawImage(image3, 0, 0, null); 
        g2.dispose();
        g2 = null;
        
        inImage = null;
        //inImage = theNewImage2;
        image3 = null;
        //theNewImage2 = null;
                
        return theNewImage2;
    }
    
    private static BufferedImage scaleImageWithAfflineTransformOp(BufferedImage inImage,double scale, int newWidth, int newHeight, int type, boolean keepAspect){
        RenderingHints renderHint = new RenderingHints(null); 
        renderHint.put(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        renderHint.put(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        renderHint.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        renderHint.put(RenderingHints.KEY_FRACTIONALMETRICS,RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        
        AffineTransformOp scaleOp = null;
        
        if(keepAspect){
	        if(newHeight == 0){
	        	double hScale = 1.0 / inImage.getHeight();
	        	newHeight = 1;
	        	scaleOp = new AffineTransformOp(AffineTransform.getScaleInstance(scale, hScale), renderHint);
	        } else if(newWidth == 0){
	        	double vScale = 1.0 / inImage.getWidth();
	        	newWidth = 1;
	        	scaleOp = new AffineTransformOp(AffineTransform.getScaleInstance(vScale, scale), renderHint);
	        } else {
	        	scaleOp = new AffineTransformOp(AffineTransform.getScaleInstance(scale, scale), renderHint);
	        }
        } else {
        	double vScale = (double)newWidth / (double)inImage.getWidth();
        	double hScale = (double)newHeight / (double)inImage.getHeight();
        	scaleOp = new AffineTransformOp(AffineTransform.getScaleInstance(vScale, hScale), renderHint);
        }
        BufferedImage scaledImg = new BufferedImage( newWidth, newHeight, inImage.getType() );
 
        Rectangle2D dstBounds = scaleOp.getBounds2D(inImage);
        scaledImg = new BufferedImage((int) dstBounds.getWidth(),(int) dstBounds.getHeight(), type);
        
        scaledImg = scaleOp.filter(inImage, scaledImg);
        inImage = scaledImg;
        scaledImg = null;
        return inImage;
    }
        
    private static BufferedImage blur(int radius, BufferedImage img) {        
        RenderingHints renderHint = new RenderingHints(null);
        
        renderHint.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);         
        renderHint.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
         
        int klen = Math.max(radius, 2);    
        int ksize = klen * klen;
    //     kernel is constant 1/k
        float f = 1f / ksize;
        float[] kern = new float[ksize];
        
        for (int i = 0; i < ksize; i++) {
            kern[i] = f;
        }
        Kernel blur = new Kernel(klen, klen, kern);
        ConvolveOp blurOp = new ConvolveOp(blur, ConvolveOp.EDGE_NO_OP, renderHint);
        BufferedImage blurredImg = null;
        if (img.getType() == BufferedImage.TYPE_3BYTE_BGR) {
            blurredImg = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        }
        blurredImg = blurOp.filter(img, blurredImg);        
        img = null;
        return blurredImg;
    }    
    
    private static final BufferedImage scaleUsingJAI(BufferedImage image, double scale, int newWidth, int newHeight, int type, boolean flattened, boolean keepAspect){
        ParameterBlock scalePb = new ParameterBlock();
        scalePb.addSource( image );  
        float hScale = 0.0f, vScale = 0.0f;
        
        if(keepAspect){
	        if(newHeight == 0){
	        	hScale = 1.0f / image.getHeight();
	        	vScale = (float)scale;
	        } else if(newWidth == 0){
	        	vScale = 1.0f / image.getWidth();
	        	hScale = (float)scale;
	        } else {
	        	hScale = vScale = (float)scale;
	        }
        } else {
        	vScale = (float)newWidth / (float)image.getWidth();
        	hScale = (float)newHeight / (float)image.getHeight();
        }
        
        scalePb.add( vScale );
        scalePb.add( hScale );
        scalePb.add( 0.0F );
        scalePb.add( 0.0F );
        if(flattened){
        	scalePb.add( new InterpolationBilinear() ); // nearest looks ok
        } else {
        	scalePb.add( new InterpolationNearest() ); // nearest looks ok
        }
//
        //InterpolationBilinear
        //InterpolationBicubic2
        //InterpolationBicubic
        //InterpolationNearest
        //
        RenderedOp scaleImg = JAI.create( "scale", scalePb );
        
        return scaleImg.getAsBufferedImage();
    }
}

/*
 * else {  
            		if(false){
            			int[] pixel = new int[4];
            			WritableRaster before = inImage.getRaster();
            			pixel = before.getPixel(76, 76, pixel);
            			System.out.println("Pixel: A: "+pixel[0]+" R: "+pixel[1]+" G: "+pixel[2]+" B: "+pixel[3]);
            			
            			int bl = (int) Math.floor(1 / scale);
		    	        inImage = blur(bl,inImage);
		    	        
		    	        WritableRaster after = inImage.getRaster();
            			pixel = after.getPixel(76, 76, pixel);
            			after.setPixel(76, 76, before.getPixel(76, 76, pixel));
            			System.out.println("Pixel: A: "+pixel[0]+" R: "+pixel[1]+" G: "+pixel[2]+" B: "+pixel[3]);
            		}
            		if(false){
            			 BufferedImage buffered = new BufferedImage( inImage.getWidth( null ), inImage.getHeight( null ), BufferedImage.TYPE_INT_RGB);
            	            Graphics2D g2 = buffered.createGraphics();
            	            g2.setColor(Color.WHITE);
        	                g2.fillRect(0,0,inImage.getWidth( null ),inImage.getHeight( null ));
            	            g2.drawImage( inImage, null, null );            
            	            inImage = buffered;
            	            g2.dispose();
            	            g2 = null;
            	            buffered = null;
            	            int bl = (int) Math.floor(1 / scale);
    		    	        inImage = blur(bl,inImage);
            		}
            		if(false){
            			int bl = (int) Math.floor(1 / scale);
		    	        inImage = blur(bl,inImage);
            		}
            		if(false){
            			int[] pixel = new int[4];
            			WritableRaster before = inImage.getRaster();
            			/*
            			pixel = before.getPixel(137, 177, pixel);
		    	        System.out.println("Pixel: A: "+pixel[0]+" R: "+pixel[1]+" G: "+pixel[2]+" B: "+pixel[3]);
            			            			*/
            		/*	int bl = (int) Math.floor(1 / scale);
		    	        inImage = blur(bl,inImage);
		    	        
		    	        WritableRaster after = inImage.getRaster();
		    	        		    	        
		    	        int w = inImage.getWidth();
	                  	 int h = inImage.getHeight();
	                  	 //int[] marker = new int[]{0,0,255,255};
	                  	 //int count = 0;
	                  	//Color col = params.getInternalVariables().getTransparentColor();
	                  	 for(int i = 0; i < h; i++){
	                  		 for(int e = 0; e < w; e++){
	                  			pixel = after.getPixel(e, i, pixel);	                  			
	                  			if(pixel[3] != 255 && pixel[3] != 0){
	                  				after.setPixel(e, i, before.getPixel(e, i, pixel));
	                  				//after.setPixel(e, i, marker);	                  				
	                  			} 
	                  		 }
	                  	 }    
	                  	 /*
	                  	pixel = after.getPixel(137, 177, pixel);
		    	        System.out.println("Pixel: A: "+pixel[0]+" R: "+pixel[1]+" G: "+pixel[2]+" B: "+pixel[3]);
	                  	 System.out.println("changed "+count+" pixels out of "+(w*h));*/
/*	}
            		if(false){
	            		WritableRaster alpha = inImage.getAlphaRaster();
	            		WritableRaster raster = inImage.getRaster();
	                  	 int w = inImage.getWidth();
	                  	 int h = inImage.getHeight();
	                  	 LinkedList<Pixel> protect = new LinkedList<Pixel>();
	                  	 Color col = params.getInternalVariables().getTransparentColor();
	                  	System.out.println("Trans Pixel: A: "+col.getAlpha()+" R: "+col.getRed()+" G: "+col.getGreen()+" B: "+col.getBlue());
	                  	 int result = 0;
	                  	 int[] pixel = new int[4];
	                  	 for(int i = 0; i < h; i++){
	                  		 for(int e = 0; e < w; e++){
	                  			pixel = raster.getPixel(e, i, pixel);
	                  			if(col.getRed() == pixel[1] &&
	        		                    col.getGreen() == pixel[2] &&
	        		                    col.getBlue() == pixel[3]){
	                  				if(e == i && e == 76){
	                  					System.out.println("protecting 76");
	                  				}
	                  				protect.add(new Pixel(e, i, raster.getPixel(e, i, pixel)));
	                  			}
	                  			/*
	                  			if(pixel[0] != 255){
	                  				protect.add(new Pixel(e, i, raster.getPixel(e, i, pixel)));
	                  			}*/
	          /*        		 }
	                  	 }                  	 
	            		int bl = (int) Math.floor(1 / scale);
		    	        inImage = blur(bl,inImage);
		    	        alpha = inImage.getAlphaRaster();
		    	        raster = inImage.getRaster();
		    	        Iterator<Pixel> ite = protect.iterator();
		    	        Pixel tmp = null;
		    	        while(ite.hasNext()){
		    	        	tmp = ite.next();
		    	        	pixel = raster.getPixel(tmp.getX(), tmp.getY(), pixel);
		    	        	if(col.getRed() != (pixel[1] & 0xff) ||
        		                    col.getGreen() != (pixel[2] & 0xff) ||
        		                    col.getBlue() != (pixel[3] & 0xff)){
		    	        		System.out.println("1 pixel changed! X:"+tmp.getX()+" Y:"+tmp.getY());
		    	        		raster.setPixel(tmp.getX(), tmp.getY(), tmp.getRgb());
		    	        	}
		    	        }
		    	        alpha = inImage.getAlphaRaster();
		    	        raster = inImage.getRaster();
		    	        ite = protect.iterator();
		    	        while(ite.hasNext()){
		    	        	tmp = ite.next();
		    	        	pixel = alpha.getPixel(tmp.getX(), tmp.getY(), pixel);
		    	        	if(pixel[0] == 255){
		    	        		System.out.println("2 pixel changed!");
		    	        	}
		    	        }
            		}
            		if(false){
            			int bl = (int) Math.floor(1 / scale);
    	    	        inImage = blur(bl,inImage);
            		}
            	}
            	*/
