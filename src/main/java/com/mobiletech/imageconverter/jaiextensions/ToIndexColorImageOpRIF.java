package com.mobiletech.imageconverter.jaiextensions;

import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import java.util.Map;

import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.ROI;
import javax.media.jai.operator.ColorQuantizerDescriptor;
import javax.media.jai.operator.ColorQuantizerType;

import com.sun.media.jai.opimage.MedianCutOpImage;
import com.sun.media.jai.opimage.NeuQuantOpImage;
import com.sun.media.jai.opimage.OctTreeOpImage;

public class ToIndexColorImageOpRIF implements RenderedImageFactory {

    /** <p> Default constructor (there is no input). */
    public ToIndexColorImageOpRIF() {}

    /**
     * <p> Creates a new instance of ColorQuantizerOpImage in the
     * rendered layer.  This method satisfies the implementation of RIF.
     *
     * @param paramBlock  The source image, the color quantization algorithm
     *                    name, the maximum number of colors, the
     *                    parameter for training (the histogram size for
     *                    median-cut, the cycle for neuquant, and maximum tree
     *                    size for oct-tree), and the ROI.
     * @param renderHints RenderingHints.
     */
    public RenderedImage create(ParameterBlock paramBlock,
                                RenderingHints renderHints) {
        RenderedImage source = paramBlock.getRenderedSource(0);

        ImageLayout layout = renderHints == null ? null :
            (ImageLayout)renderHints.get(JAI.KEY_IMAGE_LAYOUT);

        ToIndexColorImageType algorithm =
            (ToIndexColorImageType)paramBlock.getObjectParameter(0);
        int maxColorNum = paramBlock.getIntParameter(1);
        int upperBound = paramBlock.getIntParameter(2);
        ROI roi= (ROI)paramBlock.getObjectParameter(3);
        int xPeriod = paramBlock.getIntParameter(4);
        int yPeriod = paramBlock.getIntParameter(5);
        Color col = (Color)paramBlock.getObjectParameter(6);
        LookupTableJAI colorMap = (LookupTableJAI)paramBlock.getObjectParameter(7);
        ColorModel cm = (ColorModel)paramBlock.getObjectParameter(8);

        // check if 3-band byte-type image
	SampleModel sm = source.getSampleModel();
        if (sm.getNumBands() != 3  && sm.getDataType() == DataBuffer.TYPE_BYTE)
            throw new IllegalArgumentException("ColorQuantizerRIF0");
     else {
            return new dexMedianCutOpImage(source, (Map)renderHints, layout,
                                        maxColorNum, upperBound, roi,
                                        xPeriod, yPeriod, col, colorMap, cm);

    } // create
    }
} // ColorQuantizerRIF