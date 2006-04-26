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
import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;

import javax.media.jai.InterpolationBicubic;
import javax.media.jai.InterpolationBicubic2;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import com.mobiletech.imageconverter.vo.ImageConverterParams;

public class ImageScaler {
    public static BufferedImage resizeImage(BufferedImage inImage,int height, int width,boolean noEnlargement, boolean hasTransparency, ImageConverterParams params){
        int oldWidth = inImage.getWidth(); 
        int oldHeight = inImage.getHeight(); 

        double scale = 0.0; 

        if (((double) width / oldWidth ) < ((double) height / oldHeight)) {
            scale = (double) width / oldWidth; 
        } else { 
            scale = (double) height / oldHeight;
        }   
        
        return resizeImage(inImage,scale,noEnlargement,hasTransparency,params);
    }   
    
    public static BufferedImage resizeImage(BufferedImage inImage,double scale,boolean noEnlargement, boolean hasTransparency, ImageConverterParams params){                
        if(noEnlargement){
            if(scale > 1.0){
                return null;
            }
        }
        int newWidth = (int) (inImage.getWidth() * scale); 
        int newHeight = (int) (inImage.getHeight() * scale); 
               
        inImage = toBuffImageRGBorARGB(inImage,params);
        int type = inImage.getType();
        
        if(type == 0){
            type = BufferedImage.TYPE_INT_RGB;
        } 
        if(hasTransparency){
            inImage = scaleUsingJAI(inImage,scale,newWidth,newHeight,type);
        } else if (scale <= 0.7) {
            int bl = (int) Math.floor(1 / scale);
            inImage = blur(bl,inImage);    
            inImage = scaleImageWithAfflineTransformOp(inImage,scale,newWidth,newHeight,type);            
        } else {
            inImage = scaleImageWithGetScaledInstance(inImage,newWidth,newHeight,type);
        }                         
        return inImage;               
    }   
    
    private static BufferedImage scaleImageWithGetScaledInstance(BufferedImage inImage, int newWidth, int newHeight, int type){
        Image image3 = inImage.getScaledInstance(newWidth,newHeight,BufferedImage.SCALE_SMOOTH);
        
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
    
    private static BufferedImage scaleImageWithAfflineTransformOp(BufferedImage inImage,double scale, int newWidth, int newHeight, int type){
        RenderingHints renderHint = new RenderingHints(null); 
        renderHint.put(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        renderHint.put(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        renderHint.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        renderHint.put(RenderingHints.KEY_FRACTIONALMETRICS,RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        
        AffineTransformOp scaleOp = new AffineTransformOp(AffineTransform.getScaleInstance(scale, scale), renderHint);
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
    
    
    private static final BufferedImage toBuffImageRGBorARGB(BufferedImage source, ImageConverterParams params) {
        if (source.getType() != BufferedImage.TYPE_INT_RGB && source.getType() != BufferedImage.TYPE_INT_ARGB) {
            int type = BufferedImage.TYPE_INT_RGB;
            if (source.getColorModel().hasAlpha())
                type = BufferedImage.TYPE_INT_ARGB;
            BufferedImage bi = new BufferedImage(source.getWidth(), source.getHeight(), type);
            Graphics2D graphics2D = null;
            try {
                graphics2D = bi.createGraphics();
                graphics2D.drawImage(source, null, 0, 0);
            } finally {
                if (graphics2D != null)
                    graphics2D.dispose();
            }
            return bi;
        }
        return source;
    }
    
    private static final BufferedImage scaleUsingJAI(BufferedImage image, double scale, int newWidth, int newHeight, int type){
        ParameterBlock scalePb = new ParameterBlock();
        scalePb.addSource( image );  // Even so, a source of 'im' shows the same problem
        scalePb.add( (float)scale );
        scalePb.add( (float)scale );
        scalePb.add( 0.0F );
        scalePb.add( 0.0F );
        scalePb.add( new InterpolationNearest() ); // nearest looks ok
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
