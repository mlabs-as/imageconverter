package com.mobiletech.imageconverter.watermarks;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import com.mobiletech.imageconverter.ImageConverter;
import com.mobiletech.imageconverter.exception.ImageConverterException;
import com.mobiletech.imageconverter.io.ImageDecoder;
import com.mobiletech.imageconverter.modifiers.ImageScaler;
import com.mobiletech.imageconverter.vo.ImageWatermark;
import com.mobiletech.imageconverter.vo.TextWatermark;


public class ImageWatermarker {
    public static BufferedImage applyImageWatermark(BufferedImage inImage,ImageWatermark watermark) throws ImageConverterException{
        int x = 0;
        int y = 0;
        
        BufferedImage inMark = ImageDecoder.getBufferedImage(watermark.getImage());
        
        if(watermark.getWidth() > 0 || watermark.getHeight() > 0){
            inMark = ImageScaler.resizeImage(inMark,watermark.getHeight(),watermark.getWidth(), watermark.isNoEnlargement(),false,null);
        } else {
            inMark = ImageScaler.resizeImage(inMark,(int)(inImage.getHeight()*watermark.getSizeFactor()),(int)(inImage.getWidth()*watermark.getSizeFactor()),watermark.isNoEnlargement(),false,null);
        }
        
        Graphics2D g2 = inImage.createGraphics();
        
        if(watermark.getOpaque() != 0.0f){
            Composite alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, watermark.getOpaque());
            g2.setComposite(alphaComp);
        }
                
        switch(watermark.getWatermarkPosition()){
            case ImageConverter.WMARK_POS_TOPLEFT:{
                x = y = 0;
                break;
            }
            case ImageConverter.WMARK_POS_TOPRIGHT:{
                x = inImage.getWidth()-inMark.getWidth();
                y = 0;
                break;
            }
            case ImageConverter.WMARK_POS_BOTTOMLEFT:{
                x = 0;
                y = inImage.getHeight()-inMark.getHeight();
                break;
            }
            case ImageConverter.WMARK_POS_CENTER:{
                y = (inImage.getHeight()/2)-(inMark.getHeight()/2);
                x = (inImage.getWidth()/2)-(inMark.getWidth()/2);
                break;
            }           
            case ImageConverter.WMARK_POS_DIAGONAL_CENTER:{
                AffineTransform oldAT = g2.getTransform(); 
                AffineTransform newAT = new AffineTransform(); 

                newAT.rotate(Math.toRadians(-45),inImage.getWidth()/2,inImage.getHeight()/2);               
                g2.setTransform(newAT);             
                g2.drawImage(inMark,inImage.getWidth()/2-inMark.getWidth()/2,inImage.getHeight()/2-inMark.getHeight()/2,null,null);
                g2.setTransform(oldAT);
                g2.dispose();
                g2 = null;
                return inImage;
            }               
            case ImageConverter.WMARK_POS_BOTTOMRIGHT:
            default:{
                x = inImage.getWidth()-inMark.getWidth();
                y = inImage.getHeight()-inMark.getHeight();
                break;
            }                       
        }
        
        g2.drawImage( inMark, x, y, null, null);
        g2.dispose();
        g2 = null;
        inMark = null;
        return inImage;
    }
        
    public static BufferedImage applyTextWatermark(BufferedImage inImage, TextWatermark watermark) throws ImageConverterException{
        Graphics2D g2 = inImage.createGraphics(); 
        int x = 0;
        int y = 0;
        
        if(null != watermark.getFont()){
            g2.setFont(watermark.getFont());
        }       
        if(null != watermark.getColor()){
            g2.setColor(watermark.getColor());
        }
        
        FontMetrics fM = g2.getFontMetrics();
        int textlength = fM.stringWidth(watermark.getText())+2;
        int resizeTarget = 0;
        
        // Auto Resize too big watermarks
        if(watermark.getWatermarkPosition() != ImageConverter.WMARK_POS_DIAGONAL_CENTER){
            resizeTarget = inImage.getWidth();
        } else {
            resizeTarget = (int)(Math.sqrt(inImage.getWidth()*inImage.getWidth()+inImage.getHeight()*inImage.getHeight()));
        }
        while(textlength>resizeTarget && g2.getFont().getSize()>1){
            Font font = g2.getFont();
            g2.setFont(font.deriveFont(font.getSize2D()-1));
            fM = g2.getFontMetrics();
            textlength = fM.stringWidth(watermark.getText())+2;
        }
        int textheight = fM.getAscent();
        
        switch(watermark.getWatermarkPosition()){
            case ImageConverter.WMARK_POS_TOPLEFT:{
                x = 2;
                y = textheight;
                break;
            }
            case ImageConverter.WMARK_POS_TOPRIGHT:{
                x = inImage.getWidth()-textlength;
                y = textheight;
                break;
            }
            case ImageConverter.WMARK_POS_BOTTOMLEFT:{
                x = 2;
                y = inImage.getHeight()-4;
                break;
            }
            case ImageConverter.WMARK_POS_CENTER:{
                y = (inImage.getHeight()/2) + textheight/2;
                x = (inImage.getWidth()/2)-(textlength/2);
                break;
            }           
            case ImageConverter.WMARK_POS_DIAGONAL_CENTER:{
                AffineTransform oldAT = g2.getTransform(); 
                AffineTransform newAT = new AffineTransform(); 

                newAT.rotate(Math.toRadians(-45),inImage.getWidth()/2,inImage.getHeight()/2);
                g2.setTransform(newAT); 
                y = inImage.getHeight()/2;
                x = inImage.getWidth()/2-textlength/2;          
                g2.drawString(watermark.getText(),x,y);
                g2.setTransform(oldAT);
                g2.dispose();
                return inImage;
            }               
            case ImageConverter.WMARK_POS_BOTTOMRIGHT:
            default:{
                x = inImage.getWidth()-textlength;
                y = inImage.getHeight()-4;
                break;
            }                       
        }

        g2.drawString(watermark.getText(),x,y);
        g2.dispose();
        
        return inImage;
    }
}
