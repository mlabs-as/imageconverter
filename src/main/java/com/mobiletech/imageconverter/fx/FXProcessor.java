/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mobiletech.imageconverter.fx;

import com.mobiletech.imageconverter.vo.ImageConverterParams;
import com.mobiletech.imageconverter.vo.ImageFXVO;
import com.mobiletech.imageconverter.vo.fx.RoundedCornersFX;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 *
 * @author andreas
 */
public class FXProcessor {
    public static BufferedImage processEffects(ImageConverterParams icp, BufferedImage image){
        if(icp.getEffects() != null){
            for(ImageFXVO effect : icp.getEffects()){
                switch(effect.getFXTYPE()){
                    case ImageFXVO.ROUNDED_CORNERS:
                        image = doRoundedCorners(image, icp, (RoundedCornersFX) effect);
                        break;
                }
            }
        }
        return image;
    }

    private static BufferedImage doRoundedCorners(BufferedImage image, ImageConverterParams icp, RoundedCornersFX meta){
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_IN, 1.0f);
        BufferedImage mask = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D gfx = mask.createGraphics();
        gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if(icp.getFormat().equalsIgnoreCase("gif") && false){
            if(icp.getInternalVariables().getTransparentColor() != null){
                gfx.setColor(icp.getInternalVariables().getTransparentColor());
            } else {
                icp.getInternalVariables().setTransparentColor(Color.GREEN);
                gfx.setColor(Color.GREEN);
            }
            gfx.fillRect(0, 0, image.getWidth(), image.getHeight());
        }
        gfx.setColor(Color.WHITE);
        int base = image.getWidth();
        if(image.getHeight() < base){
            base = image.getHeight();
        }
        int aWidth = base / 100 * 100;
        int aHeight = base / 100 * 100;

        //System.out.println("Base: "+base);

        gfx.fillRoundRect(0, 0, image.getWidth(), image.getHeight(), 20, 20);
        gfx.setComposite(ac);
        gfx.drawImage(image, 0, 0, null);
        gfx.dispose();

        if(icp.getFormat().equalsIgnoreCase("gif")){
            BufferedImage bi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = null;
            
            try {
                graphics2D = bi.createGraphics();
                if(icp.getInternalVariables().getTransparentColor() != null){
                graphics2D.setColor(icp.getInternalVariables().getTransparentColor());
            } else {
                icp.getInternalVariables().setTransparentColor(Color.GREEN);
                graphics2D.setColor(Color.GREEN);
            }
            graphics2D.fillRect(0, 0, image.getWidth(), image.getHeight());
                graphics2D.drawImage(mask, null, 0, 0);
            } finally {
                if (graphics2D != null)
                    graphics2D.dispose();
            }
            mask = bi;
        }
        return mask;
    }
    
}
