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
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;


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
        //Add AlphaComposite with 50% alpha level
        AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        g2.setComposite(alpha);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
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
            resizeTarget = (int)(Math.sqrt((inImage.getWidth()*inImage.getWidth()+inImage.getHeight()*inImage.getHeight())*0.85));
        }
        //System.out.println("ResizeTarget = "+resizeTarget+", TextLength = "+textlength);
        if (textlength>=resizeTarget) {
            //System.out.println("Sizing down...");
            while(textlength>resizeTarget && g2.getFont().getSize()>1){
                Font font = g2.getFont();
                g2.setFont(font.deriveFont(font.getSize2D()-1));
                fM = g2.getFontMetrics();
                textlength = fM.stringWidth(watermark.getText())+2;
            }
            //Resize up
        } else {
            //System.out.println("Sizing up...");
            while (textlength < resizeTarget) {
                Font font = g2.getFont();
                g2.setFont(font.deriveFont(font.getSize2D() + 1));
                fM = g2.getFontMetrics();
                textlength = fM.stringWidth(watermark.getText())+2;
            }
        }
        //System.out.println("ResizeTarget = "+resizeTarget+", TextLength = "+textlength);
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
                // Keep shapes centered on panel.
                AffineTransform at = AffineTransform.getTranslateInstance(inImage.getWidth() / 2, inImage.getHeight() / 2);
                // Get exact Font Metrics before we draw text
                FontRenderContext frc = g2.getFontRenderContext();
                float width = (float) g2.getFont().getStringBounds(watermark.getText(), frc).getWidth();
                LineMetrics lm = g2.getFont().getLineMetrics(watermark.getText(), frc);
                float height = lm.getHeight();
                float descent = lm.getDescent();
                // Translate to origin of centered text.
                float px = (inImage.getWidth() - width) / 2;
                float py = (inImage.getHeight() + height) / 2 - descent;
                //System.out.printf("imgWidth=%d  imgHeight=%d  strWidth=%.1f  strHeight=%.1f  strDescent=%.1f  pointX=%.1f  pointY=%.1f%n",inImage.getWidth(),inImage.getHeight(),width,height,descent,px,py);
                // We have finished using at above for the spatial transform
                // work so we can refit/reuse it for the text rendering.
                at.setToTranslation(px, py);

                // Rotate text to align with sw - ne diagonal.
                // Determine the angle of rotation.
                double theta = Math.atan2(-inImage.getHeight(), inImage.getWidth());
                // In java, positive angles are measured clockwise from 3 o'clock
                // except in arc measure which is reversed.
                //System.out.printf("theta (angle) = %.1f%n", Math.toDegrees(theta));

                AffineTransform xfRotate = AffineTransform.getRotateInstance(theta, inImage.getWidth() / 2, inImage.getHeight() / 2);
                xfRotate.concatenate(at); // The order is important (matrix math).
                g2.setFont(g2.getFont().deriveFont(xfRotate));
                // Since we did all the work in the transform, draw text at (0,0)
                g2.drawString(watermark.getText(), 0, 0);
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
