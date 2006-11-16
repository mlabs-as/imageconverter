package com.mobiletech.imageconverter.vo;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

import com.sun.imageio.plugins.gif.GIFImageMetadata;

public class ImageConverterInternalVariables {
    
    private boolean changed = false;    
    private BufferedImage bufferedImage = null;    
    private String oldFormat = null;
    private ColorModel cm = null;
    private Color transparentColor = null;
    private GIFImageMetadata [] imageMetadata = null;
    private double scale = 0.0;
    private boolean isOkToBlur = true;
    
    public ImageConverterInternalVariables(){
        oldFormat = null;
        cm = null;
        changed = false;
        bufferedImage = null;
        transparentColor = null;
    }    
      
    public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public boolean isOkToBlur() {
		return isOkToBlur;
	}

	public void setOkToBlur(boolean isOkToBlur) {
		this.isOkToBlur = isOkToBlur;
	}

	/**
     * Gets the oldFormat attribute of the ImageConverterParams object
     * 
     * @return Returns the current value of the oldFormat attribute
     */
    public String getOldFormat() {
        return oldFormat;
    }
    /**
     * This parameter is used only internally in the ImageConverter. 
     * Setting this parameter will have no effect. 
     * 
     * @param oldFormat only for internal use
     */
    public void setOldFormat(String oldFormat) {
        this.oldFormat = oldFormat;
    }
    
    
    /**
     * Gets the changed attribute of the ImageConverterParams object
     * 
     * @return Returns the current value of the changed attribute
     */
    public boolean isChanged() {
        return changed;
    }
    /**
     * This parameter is used only internally in the ImageConverter. 
     * Setting this parameter will have no effect. 
     * 
     * @param changed only for internal use
     */
    public void setChanged(boolean changed) {
        this.changed = changed;
    }
        
    /**
     * Gets the bufferedImage attribute of the ImageConverterParams object
     * 
     * @return Returns the current value of the bufferedImage attribute
     */
    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }
    /**
     * This parameter is used only internally in the ImageConverter. 
     * Setting this parameter will have no effect. 
     * 
     * @param changed only for internal use
     */
    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    /**
     * Only for internal use by the Imageconverter
     * 
     * @return Returns the transparentColor.
     */
    public Color getTransparentColor() {
        return transparentColor;
    }

    /**
     * Only for internal use by the Imageconverter
     * 
     * @param transparentColor The transparentColor to set.
     */
    public void setTransparentColor(Color transparentColor) {
        this.transparentColor = transparentColor;
    } 
    
    /**
     * @return Returns the cm.
     */
    public ColorModel getCm() {
        return cm;
    }

    /**
     * @param cm The cm to set.
     */
    public void setCm(ColorModel cm) {
        this.cm = cm;
    }

    /**
     * @return Returns the imageMetadata.
     */
    public GIFImageMetadata[] getImageMetadata() {
        return imageMetadata;
    }

    /**
     * @param imageMetadata The imageMetadata to set.
     */
    public void setImageMetadata(GIFImageMetadata[] imageMetadata) {
        this.imageMetadata = imageMetadata;
    }        
}
