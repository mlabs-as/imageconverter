/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mobiletech.imageconverter;

import com.mobiletech.imageconverter.exception.ImageConverterException;
import com.mobiletech.imageconverter.fx.FXProcessor;
import com.mobiletech.imageconverter.io.DexImageWriterFactory;
import com.mobiletech.imageconverter.io.ImageEncoder;
import com.mobiletech.imageconverter.vo.GeneratorTemplate;
import com.mobiletech.imageconverter.vo.ImageConverterInternalVariables;
import com.mobiletech.imageconverter.vo.ImageConverterParams;
import com.mobiletech.imageconverter.vo.ImageGeneratorParams;
import com.mobiletech.imageconverter.vo.templates.GradientGeneratorTemplate;
import com.mobiletech.imageconverter.vo.templates.IphoneButtonTemplate;
import com.mobiletech.imageconverter.writers.DexImageWriter;
import com.mobiletech.imageconverter.writers.OptimizingAnimGifWriter;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 *
 * @author andreas
 */
public class ImageGenerator {
    public static byte [] generateImage(ImageGeneratorParams params){
        byte [] returnByte = null;
        BufferedImage image = null;
        ImageConverterParams convParams = new ImageConverterParams(null);
        convParams.setFormat("png");
        convParams.setEffects(params.getEffects());
        ImageConverterInternalVariables internal = new ImageConverterInternalVariables();
        internal.setOldFormat(convParams.getFormat());
        convParams.setInternalVariables(internal);

        if(params.getTemplates() != null){
            image = initializeImage(params);
            for(GeneratorTemplate template : params.getTemplates()){
                if(template instanceof GradientGeneratorTemplate){
                    image = doGradient(image, (GradientGeneratorTemplate)template);
                } else if(template instanceof IphoneButtonTemplate){
                    image = doIphoneButton(image, (IphoneButtonTemplate)template);
                }
            }

            // do ImageFX            
            image = FXProcessor.processEffects(convParams, image);
        }

        if(image != null){                        
            try {
                DexImageWriter writer = DexImageWriterFactory.getImageWriter(image, convParams);

                if(!(writer instanceof OptimizingAnimGifWriter)){
                    //image = ImageEncoder.prepareForConversion(image, convParams);
                }
                writer.writeNext(image);
                if(!writer.canWriteMore()){

                }
                returnByte = writer.getByte();
            } catch (ImageConverterException ex){

            }
        }
        return returnByte;
    }

    private static BufferedImage initializeImage(ImageGeneratorParams params){
        BufferedImage image = new BufferedImage(params.getWidth(), params.getHeight(), BufferedImage.TYPE_INT_RGB);
        return image;
    }

    private static BufferedImage doGradient(BufferedImage image, GradientGeneratorTemplate template){
        if(template.getStartColor() != null && template.getEndColor() != null){
            GradientPaint gradient = null;

            switch(template.getType()){
                case GradientGeneratorTemplate.VERTICAL:
                    gradient = new GradientPaint(0, 0, template.getStartColor(), 0, image.getHeight(), template.getEndColor());
                    break;
                case GradientGeneratorTemplate.HORIZONTAL:
                    gradient = new GradientPaint(0, 0, template.getStartColor(), image.getHeight(), 0, template.getEndColor());
                    break;
                case GradientGeneratorTemplate.DIAGONAL_BOTTOMTOTOP:
                    gradient = new GradientPaint(0, image.getHeight(), template.getStartColor(), image.getWidth(), 0, template.getEndColor());
                    break;
                case GradientGeneratorTemplate.DIAGONAL_TOPTOBOTTOM:
                    gradient = new GradientPaint(0, 0, template.getStartColor(), image.getWidth(), image.getHeight(), template.getEndColor());
                    break;
            }
            
            Graphics2D g2d = image.createGraphics();
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, image.getHeight(), image.getWidth());
            g2d.dispose();
        }
        return image;
    }

    private static BufferedImage doIphoneButton(BufferedImage image, IphoneButtonTemplate template){
        GradientPaint gradient = null;
        int height = image.getHeight();
        int width = image.getWidth();

        int border = 3;
        int end = height / 2;

        Graphics2D g2d = image.createGraphics();

        // Upper Half
        gradient = new GradientPaint(0, border, new Color(255,0,0), 0, end, new Color(0,0,255));        
        g2d.setPaint(gradient);
        g2d.fillRect(border, border, width-border-border, end);

        // Lower Half
        gradient = new GradientPaint(end+1, border, new Color(255,0,0), 0, height-3, new Color(0,255,0));
        g2d.setPaint(gradient);
        g2d.fillRect(border, end+1, width-border-border, end-border);
g2d.dispose();
        // Border
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_IN, 1.0f);
        BufferedImage mask = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D gfx = mask.createGraphics();
        gfx.setColor(new Color(0,0,0));
        gfx.fillRect(0, 0, image.getWidth(), image.getHeight());
        gfx.dispose();
        
        mask = createAlphaGradient(mask);
        
        gfx = mask.createGraphics();
        gfx.setComposite(ac);
        gfx.setColor(new Color( 0x00ffffff, true ));
        gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gfx.fillRoundRect(border, border, image.getWidth()-border*2, image.getHeight()-border*2, 10, 20);
        gfx.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OVER, 1.0f));
        gfx.drawImage(image, 0, 0, null);
        gfx.dispose();

        //g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //g2d.fillRoundRect(0, 0, image.getWidth(), image.getHeight(), 20, 20);

        
        return mask;//image;
    }

    private static BufferedImage createAlphaGradient(BufferedImage image, int start, int end){
        BufferedImage gradient = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        WritableRaster ar = image.getAlphaRaster();

        int dif = end - start;
        System.out.println("dif "+dif);
        int steps = 0;
        if(dif > image.getHeight()){
            steps = image.getHeight();
        } else {
            if(dif < 0){
                System.out.println("neg "+(-dif));
                System.out.println("height "+image.getHeight());
                steps = image.getHeight() / 34;
            } else {
                steps = image.getHeight() / dif;
            }
            
        }
        System.out.println("steps "+steps);
        int increment = steps;
        boolean decrease = false;
        if(start > end){
            decrease = true;
        }
        steps = dif / increment;
        System.out.println("increment "+increment);

        if(ar != null){
            int w = ar.getWidth();
            int h = ar.getHeight();
            int[] pixel = new int[3];
            int incrementer = 0;
            for(int i = 0; i < h; i++){
                for(int e = 0; e < w; e++){
                    
                    
                    pixel = ar.getPixel(e, i, pixel);
                    pixel[0] = start;
                    ar.setPixel(e, i, pixel);
                }
                if(incrementer >= steps){
                        incrementer = 0;
                        if(decrease){
                            start -= increment;
                        } else {
                            start += increment;
                        }
                    } else {
                        incrementer++;
                    }
                System.out.print(", "+start);
                    if(start > 255){
                        start = 255;
                    }
            }
        }
        return image;
    }

    private static BufferedImage createAlphaGradient(BufferedImage image){
        return createAlphaGradient(image, 114, 80);
    }
}
