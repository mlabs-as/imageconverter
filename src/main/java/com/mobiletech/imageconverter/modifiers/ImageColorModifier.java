package com.mobiletech.imageconverter.modifiers;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.ColorQuantizerDescriptor;

import com.mobiletech.imageconverter.exception.ImageConverterException;

public class ImageColorModifier {
    public static BufferedImage colorChange(BufferedImage inImage, int inNumColors) throws ImageConverterException{
        /*
        ColorReducer reducer = new ColorReducer(inNumColors,true);
        Image redux = null;
        try {
            redux = reducer.getColorReducedImage(inImage);
        } catch (Exception ji){
            throw new ImageConverterException(ImageConverterException.Types.COLOR_REDUCER_ERROR,"JIMIException thrown when reducing image color ",ji);
        }
        int type = inImage.getType();
        if(type == 0){
            type = BufferedImage.TYPE_INT_RGB;
        }
        BufferedImage reducedImage = new BufferedImage( inImage.getWidth( null ), 
                inImage.getHeight( null ), type); 
        Graphics2D g2 = reducedImage.createGraphics(); 
        g2.drawImage( redux, null, null );      
        g2.dispose();
        
        g2 = null;
        inImage = null;
        redux = null;
        
        return reducedImage;*/
        
        RenderedOp l_renderedOp = ColorQuantizerDescriptor.create(inImage,
                ColorQuantizerDescriptor.MEDIANCUT,
                inNumColors,
                null,
                null,
                new Integer(1),
                new Integer(1), null);
        inImage = l_renderedOp.getAsBufferedImage();
        return inImage;        
    }            
    
    public static BufferedImage getGrayscale(BufferedImage inImage) throws ImageConverterException{
        if(false){ // Old Grayscale Code         
            ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
            BufferedImage gray = new BufferedImage(inImage.getWidth(),inImage.getHeight(),BufferedImage.TYPE_BYTE_GRAY);
            op.filter(inImage, gray);
            return gray;
        }
        // Circumvent Crash for images that have alpha channel (transparency)
        if (inImage.getType() == BufferedImage.TYPE_INT_ARGB) {
            int type = BufferedImage.TYPE_INT_RGB;           
            BufferedImage bi = new BufferedImage(inImage.getWidth(), inImage.getHeight(), type);
            Graphics2D graphics2D = null;
            try {
                graphics2D = bi.createGraphics();
                graphics2D.drawImage(inImage, null, 0, 0);
            } finally {
                if (graphics2D != null)
                    graphics2D.dispose();
            }
            inImage = bi;
        }
        //return source;
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(inImage);

        // Create a grayscale color model.
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        
        int bits[] = new int[] {8};
        ColorModel cm = new ComponentColorModel(cs, bits, false, false,
                Transparency.OPAQUE, DataBuffer.TYPE_BYTE);        
        pb.add(cm);

        //Create a sample model with the same number of bands
        //as the  number of components in the colour model.
        int numBands = cs.getNumComponents();
        //Set up the band offsets for the Sample Model.
        int[] bandOffsets = new int[numBands];
        for (int i = 0; i < numBands; i++) {
            bandOffsets[i] = numBands - 1 - i;
        }

        //Create pixel and scanline strides.
        int pixelStride = numBands;
        int scanlineStride = numBands * inImage.getWidth();

        //Create the sample model.
        SampleModel sm = new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE,
                inImage.getWidth(), inImage.getHeight(),
                pixelStride, scanlineStride, bandOffsets);

        // Create a tiled layout with the requested ColorModel.
        ImageLayout layout = new ImageLayout();

        layout.setTileWidth(inImage.getTileWidth());
        layout.setTileHeight(inImage.getTileHeight());
        layout.setColorModel(cm);
        layout.setSampleModel(sm);
        
        // Create RenderingHints for the ImageLayout.
        RenderingHints rh = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, layout);

        //Create the grayscale image using the ColorConvert operation.
        RenderedOp op = JAI.create("colorconvert", pb, rh);
        return op.getAsBufferedImage();
    }
}
