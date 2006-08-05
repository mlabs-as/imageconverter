/*
 * Created on May 23, 2005
 *
 */
package com.mobiletech.imageconverter.vo;

/**
 * @author Andreas Ryland
 *
 */
public class ImageWatermark {
    private byte [] image = null;
    private int WatermarkPosition = 0;
    private int width = 0;
    private int height = 0;
    private double sizeFactor = 0.0;
    private float opaque = 0.0f;
    private boolean no_enlargement = false;
    /**
     * Default constructor is made private to prevent creation without an actual watermark
     *
     */
    private ImageWatermark(){}
    /**
     * Creates a new ImageWatermark
     * 
     * @param image
     *            The image that is to be used as a watermark
     * @param WatermarkPosition
     *            The desired placement of the image watermark on the converted
     *            image. Acceptable position values can be retrieved from the
     *            ImageConverter class. ie ImageConverter.WMARK_POS_TOPLEFT
     * @param width
     *            The desired width of the watermark in pixels. The aspect ratio
     *            of the watermark will be preserved so if the set width and/or
     *            height violates the aspect ratio they will be adjusted
     *            automatically
     * @param height
     *            The desired height of the watermark in pixels. The aspect
     *            ratio of the watermark will be preserved so if the set width
     *            and/or height violates the aspect ratio they will be adjusted
     *            automatically
     */
    public ImageWatermark(byte [] image, int WatermarkPosition,int width, int height){
        this.image = image;
        this.WatermarkPosition = WatermarkPosition;
        this.width = width;
        this.height = height;        
    }
    /**
     * Creates a new ImageWatermark
     * 
     * @param image
     *            The image that is to be used as a watermark
     * @param WatermarkPosition
     *            The desired placement of the image watermark on the converted
     *            image. Acceptable position values can be retrieved from the
     *            ImageConverter class. ie ImageConverter.WMARK_POS_TOPLEFT
     * @param width
     *            The desired width of the watermark in pixels. The aspect ratio
     *            of the watermark will be preserved so if the set width and/or
     *            height violates the aspect ratio they will be adjusted
     *            automatically
     * @param height
     *            The desired height of the watermark in pixels. The aspect
     *            ratio of the watermark will be preserved so if the set width
     *            and/or height violates the aspect ratio they will be adjusted
     *            automatically
     * @param noEnlargement
     *            Determines if the image could be made larger than its original size.
     * 			  If set to true then the image will not be resized if the resize parameters
     * 			  specify a size larger than the original size of the image.  
     */
    public ImageWatermark(byte [] image, int WatermarkPosition,int width, int height, boolean noEnlargement){
        this.image = image;
        this.WatermarkPosition = WatermarkPosition;
        this.width = width;
        this.height = height;        
        this.no_enlargement = noEnlargement;
    }
    /**
     * Creates a new ImageWatermark
     * 
     * @param image
     *            The image that is to be used as a watermark
     * @param WatermarkPosition
     *            The desired placement of the image watermark on the converted
     *            image. Acceptable position values can be retrieved from the
     *            ImageConverter class. ie ImageConverter.WMARK_POS_TOPLEFT
     * @param sizeFactor
     *            The desired size of the watermark, given as a factor of the
     *            size of the image to which the watermark will be applied. IE
     *            0.1 = (10 percent of the size) etc.
     */
    public ImageWatermark(byte [] image, int WatermarkPosition,double sizeFactor){
        this.image = image;
        this.WatermarkPosition = WatermarkPosition;
        this.sizeFactor = sizeFactor;
    }     
    /**
     * Creates a new ImageWatermark
     * 
     * @param image
     *            The image that is to be used as a watermark
     * @param WatermarkPosition
     *            The desired placement of the image watermark on the converted
     *            image. Acceptable position values can be retrieved from the
     *            ImageConverter class. ie ImageConverter.WMARK_POS_TOPLEFT
     * @param sizeFactor
     *            The desired size of the watermark, given as a factor of the
     *            size of the image to which the watermark will be applied. IE
     *            0.1 = (10 percent of the size) etc.
     * @param noEnlargement
     *            Determines if the image could be made larger than its original size.
     * 			  If set to true then the image will not be resized if the resize parameters
     * 			  specify a size larger than the original size of the image.  
     */
    public ImageWatermark(byte [] image, int WatermarkPosition,double sizeFactor, boolean noEnlargement){
        this.image = image;
        this.WatermarkPosition = WatermarkPosition;
        this.sizeFactor = sizeFactor;
        this.no_enlargement = noEnlargement;
    }     
    /**
     * Creates a new ImageWatermark
     * 
     * @param image
     *            The image that is to be used as a watermark
     * @param WatermarkPosition
     *            The desired placement of the image watermark on the converted
     *            image. Acceptable position values can be retrieved from the
     *            ImageConverter class. ie ImageConverter.WMARK_POS_TOPLEFT
     * @param width
     *            The desired width of the watermark in pixels. The aspect ratio
     *            of the watermark will be preserved so if the set width and/or
     *            height violates the aspect ratio they will be adjusted
     *            automatically
     * @param height
     *            The desired height of the watermark in pixels. The aspect
     *            ratio of the watermark will be preserved so if the set width
     *            and/or height violates the aspect ratio they will be adjusted
     *            automatically
     * @param opaque
     *            The desired opacity of the watermark, the scale goes from 0.1f
     *            (transparent) to 1.0f (solid)
     */
    public ImageWatermark(byte [] image, int WatermarkPosition,int width, int height,float opaque){
        this.image = image;
        this.WatermarkPosition = WatermarkPosition;
        this.width = width;
        this.height = height;   
        this.opaque = opaque;
    }
    /**
     * Creates a new ImageWatermark
     * 
     * @param image
     *            The image that is to be used as a watermark
     * @param WatermarkPosition
     *            The desired placement of the image watermark on the converted
     *            image. Acceptable position values can be retrieved from the
     *            ImageConverter class. ie ImageConverter.WMARK_POS_TOPLEFT
     * @param width
     *            The desired width of the watermark in pixels. The aspect ratio
     *            of the watermark will be preserved so if the set width and/or
     *            height violates the aspect ratio they will be adjusted
     *            automatically
     * @param height
     *            The desired height of the watermark in pixels. The aspect
     *            ratio of the watermark will be preserved so if the set width
     *            and/or height violates the aspect ratio they will be adjusted
     *            automatically
     * @param opaque
     *            The desired opacity of the watermark, the scale goes from 0.1f
     *            (transparent) to 1.0f (solid)
     * @param noEnlargement
     *            Determines if the image could be made larger than its original size.
     * 			  If set to true then the image will not be resized if the resize parameters
     * 			  specify a size larger than the original size of the image.  
     */
    public ImageWatermark(byte [] image, int WatermarkPosition,int width, int height,float opaque, boolean noEnlargement){
        this.image = image;
        this.WatermarkPosition = WatermarkPosition;
        this.width = width;
        this.height = height;   
        this.opaque = opaque;
        this.no_enlargement = noEnlargement;
    }    
    /**
     * Creates a new ImageWatermark
     * 
     * @param image
     *            The image that is to be used as a watermark
     * @param WatermarkPosition
     *            The desired placement of the image watermark on the converted
     *            image. Acceptable position values can be retrieved from the
     *            ImageConverter class. ie ImageConverter.WMARK_POS_TOPLEFT
     * @param sizeFactor
     *            The desired size of the watermark, given as a factor of the
     *            size of the image to which the watermark will be applied. IE
     *            0.1 = (10 percent of the size) etc.
     * @param opaque
     *            The desired opacity of the watermark, the scale goes from 0.1f
     *            (transparent) to 1.0f (solid)
     */
    public ImageWatermark(byte [] image, int WatermarkPosition,double sizeFactor,float opaque){
        this.image = image;
        this.WatermarkPosition = WatermarkPosition;
        this.sizeFactor = sizeFactor;
        this.opaque = opaque;
    }          
    /**
     * Creates a new ImageWatermark
     * 
     * @param image
     *            The image that is to be used as a watermark
     * @param WatermarkPosition
     *            The desired placement of the image watermark on the converted
     *            image. Acceptable position values can be retrieved from the
     *            ImageConverter class. ie ImageConverter.WMARK_POS_TOPLEFT
     * @param sizeFactor
     *            The desired size of the watermark, given as a factor of the
     *            size of the image to which the watermark will be applied. IE
     *            0.1 = (10 percent of the size) etc.
     * @param opaque
     *            The desired opacity of the watermark, the scale goes from 0.1f
     *            (transparent) to 1.0f (solid)
     * @param noEnlargement
     *            Determines if the image could be made larger than its original size.
     * 			  If set to true then the image will not be resized if the resize parameters
     * 			  specify a size larger than the original size of the image.  
     */
    public ImageWatermark(byte [] image, int WatermarkPosition,double sizeFactor,float opaque, boolean noEnlargement){
        this.image = image;
        this.WatermarkPosition = WatermarkPosition;
        this.sizeFactor = sizeFactor;
        this.opaque = opaque;
        this.no_enlargement = noEnlargement;
    }      
    /**
     * @return Returns the height.
     */
    public int getHeight() {
        return height;
    }
    /**
     * @return Returns the image.
     */
    public byte[] getImage() {
        return image;
    }
    /**
     * @return Returns the sizeFactor.
     */
    public double getSizeFactor() {
        return sizeFactor;
    }
    /**
     * @return Returns the watermarkPosition.
     */
    public int getWatermarkPosition() {
        return WatermarkPosition;
    }
    /**
     * @return Returns the width.
     */
    public int getWidth() {
        return width;
    }    
    /**
     * @return Returns the opaque.
     */
    public float getOpaque() {
        return opaque;
    }
    /**
     * Checks if the image should be made larger than its original size. 
     * If no enlargment is true then nothing will be done with the image
     * if the resize parameter evaluate to a size larger than the original one.
     * 
     * @return Returns Returns the current value of the noEnlargment attribute
     */
    public boolean isNoEnlargement() {
        return no_enlargement;
    }
    /**
     * Set the noEnlargment property to true if the image should be made larger than its original size.
     *  
     * If no enlargment is true then nothing will be done with the image
     * if the resize parameter evaluate to a size larger than the original one.
     * 
     * @param no_enlargement set to true to prevent the image from being made larger than its original size
     */
    public void setNoEnlargement(boolean no_enlargement) {
        this.no_enlargement = no_enlargement;
    }
}
