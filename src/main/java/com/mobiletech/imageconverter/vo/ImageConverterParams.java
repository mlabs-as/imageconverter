/*
 * Created on May 23, 2005
 *
 */
package com.mobiletech.imageconverter.vo;

import java.awt.Color;
import java.awt.Font;
import java.util.Iterator;
import java.util.Vector;

import com.mobiletech.imageconverter.vo.ImageConverterInternalVariables;
import com.mobiletech.imageconverter.vo.ImageWatermark;
import com.mobiletech.imageconverter.vo.TextWatermark;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This class is used to pass all desired parameters to the ImageConverter in a
 * convenient way.
 * 
 * When created, an ImageConverterParms represents an origianl image, if no
 * attributes is set then the ImageConverter will do nothing to the image and
 * pass it back as it was.
 * 
 * To use this class, set all the atributes that are to be changed from how they
 * originally were. For instance, if a different format is desired (BMP instead
 * of originally JPG, for instance) then set the format atribute.
 * 
 * Do not set the atributes to what they originally were, for instance: do not
 * set grayscale to true if the image is already grayscale etc.
 * 
 * @author Andreas Ryland
 *  
 */
public class ImageConverterParams {

    // User set Variables
    private byte[] image = null;
    private byte[] overlay = null;
    private String format = null;
    private int width = 0;
    private int height = 0;
    private boolean grayscale = false;
    private int numberOfColors = 0;
    private float JPEGCompressionQuality = 0;
    private Vector imageWatermarks = null;
    private Vector textWatermarks = null;           
    private boolean no_enlargement = false;
    private int cropLeft = 0;
    private int cropRight = 0;
    private int cropTop = 0;
    private int cropBottom = 0;
    private boolean fastMode = true;
    private boolean keepAspectRatio = true;
    private boolean ignoreHeight = false;
    private RotationType rotation = null;
    private Collection<ImageFXVO> effects = null;
    
    // Automatic Variables (Set by this class)
    private boolean hasImageWatermarks = false;
    private boolean hasTextWatermarks = false;       
    
    // Internal Variables (Set and used by the ImageConverter)
    private ImageConverterInternalVariables internalVariables = null;
    
    public void reset(){
        image = null;
        format = null;
        width = 0;
        height = 0;
        grayscale = false;
        numberOfColors = 0;
        JPEGCompressionQuality = 0;
        imageWatermarks = null;
        textWatermarks = null;
        hasImageWatermarks = false;
        hasTextWatermarks = false;
        no_enlargement = false;
        internalVariables = null;
        rotation = null;
    }    
    
    /**
     * Resets the internalVariables
     *
     */
    public void resetInternal(){
        internalVariables = null;
    }   

	public String toString(){
    	return toXML();
    }
    
    public String toXML(){
    	  StringBuffer xml = new StringBuffer();
          xml.append("<ImageParams>");        
          if(this.getFormat() != null){
              xml.append("<Format>");
              xml.append(this.getFormat());
              xml.append("</Format>");
          }
          if(this.getHeight() > 0){
              xml.append("<Height>");
              xml.append(this.getHeight());
              xml.append("</Height>");
          }
          if(this.getWidth() > 0){
              xml.append("<Width>");
              xml.append(this.getWidth());
              xml.append("</Width>");
          }
          if(this.getJPEGCompressionQuality() > 0f){
              xml.append("<JPEGCompressionQuality>");
              xml.append(this.getJPEGCompressionQuality());
              xml.append("</JPEGCompressionQuality>");
          }
          if(this.getNumberOfColors() > 0){
              xml.append("<NumberOfColors>");
              xml.append(this.getNumberOfColors());
              xml.append("</NumberOfColors>");
          }
          xml.append("<Grayscale>");
          xml.append((this.isGrayscale() ? "true" : "false"));
          xml.append("</Grayscale>");
          
          xml.append("<NoEnlargment>");
          xml.append((this.isNoEnlargement() ? "true" : "false"));
          xml.append("</NoEnlargment>");                                 
          
          xml.append("</ImageParams>");
          return xml.toString();
    }

    /**
     * Default constructor is private because there has to be an image which to
     * perform processing to
     */
    private ImageConverterParams() {
    }

    /**
     * Creates a new ImageConverterParms object with the original image that is
     * to be converted.
     * 
     * @param inImage
     *            The original image that is to be processed to produce a
     *            converted image
     */
    public ImageConverterParams(byte[] inImage) {
        init(inImage, null, 0, 0, false);
    }

    /**
     * Creates a new ImageConverterParams object specifying the original image
     * and the format attribute
     * 
     * @param inImage
     *            The original image that is to be processed to produce a
     *            converted image
     * @param inFormat
     *            The format of the converted image. Supported formats are:
     *            "jpg"/"jpeg","gif","bmp","wbmp","tif","png"
     */
    public ImageConverterParams(byte[] inImage, String inFormat) {
        init(inImage, inFormat, 0, 0, false);
    }

    /**
     * Creates a new ImageConverterParams object specifying the original image
     * and the width and height attributes
     * 
     * @param inImage
     *            The original image that is to be processed to produce a
     *            converted image
     * @param width
     *            The desired width of the converted image in pixels. The aspect
     *            ratio of the original image will be preserved so if the set
     *            width and/or height violates the aspect ratio they will be
     *            adjusted automatically
     * @param height
     *            The desired height of the converted image in pixels. The
     *            aspect ratio of the original image will be preserved so if the
     *            set width and/or height violates the aspect ratio they will be
     *            adjusted automatically
     */
    public ImageConverterParams(byte[] inImage, int inHeight,
            int inWidth) {
        init(inImage, null, inHeight, inWidth, false );
    }   
    
    /**
     * Creates a new ImageConverterParams object specifying the original image
     * and the width and height attributes
     * 
     * @param inImage
     *            The original image that is to be processed to produce a
     *            converted image
     * @param width
     *            The desired width of the converted image in pixels. The aspect
     *            ratio of the original image will be preserved so if the set
     *            width and/or height violates the aspect ratio they will be
     *            adjusted automatically
     * @param height
     *            The desired height of the converted image in pixels. The
     *            aspect ratio of the original image will be preserved so if the
     *            set width and/or height violates the aspect ratio they will be
     *            adjusted automatically
     * @param noEnlargement
     *            Determines if the image could be made larger than its original size.
     * 			  If set to true then the image will not be resized if the resize parameters
     * 			  specify a size larger than the original size of the image.      
     */
    public ImageConverterParams(byte[] inImage, int inHeight,
            int inWidth, boolean noEnlargement) {
        init(inImage, null, inHeight, inWidth, noEnlargement );
    }      
    
    /**
     * Creates a new ImageConverterParams object specifying the original image
     * and the format, width and height attributes
     * 
     * @param inImage
     *            The original image that is to be processed to produce a
     *            converted image
     * @param inFormat
     *            The format of the converted image. Supported formats are:
     *            "jpg"/"jpeg","gif","bmp","wbmp","tif","png"
     * @param width
     *            The desired width of the converted image in pixels. The aspect
     *            ratio of the original image will be preserved so if the set
     *            width and/or height violates the aspect ratio they will be
     *            adjusted automatically
     * @param height
     *            The desired height of the converted image in pixels. The
     *            aspect ratio of the original image will be preserved so if the
     *            set width and/or height violates the aspect ratio they will be
     *            adjusted automatically
     */
    public ImageConverterParams(byte[] inImage, String inFormat, int inHeight,
            int inWidth) {
        init(inImage, inFormat, inHeight, inWidth, false );
    }    

    /**
     * Creates a new ImageConverterParams object specifying the original image
     * and the format, width and height attributes
     * 
     * @param inImage
     *            The original image that is to be processed to produce a
     *            converted image
     * @param inFormat
     *            The format of the converted image. Supported formats are:
     *            "jpg"/"jpeg","gif","bmp","wbmp","tif","png"
     * @param width
     *            The desired width of the converted image in pixels. The aspect
     *            ratio of the original image will be preserved so if the set
     *            width and/or height violates the aspect ratio they will be
     *            adjusted automatically
     * @param height
     *            The desired height of the converted image in pixels. The
     *            aspect ratio of the original image will be preserved so if the
     *            set width and/or height violates the aspect ratio they will be
     *            adjusted automatically
     * @param noEnlargement
     *            Determines if the image could be made larger than its original size.
     * 			  If set to true then the image will not be resized if the resize parameters
     * 			  specify a size larger than the original size of the image.    
     */
    public ImageConverterParams(byte[] inImage, String inFormat, int inHeight,
            int inWidth, boolean noEnlargement) {
        init(inImage, inFormat, inHeight, inWidth ,noEnlargement );
    }    

    /**
     * Initializes an ImageConverterParams object
     * 
     * @param inImage
     *            The original image that is to be processed to produce a
     *            converted image
     * @param inFormat
     *            The format of the converted image. Supported formats are:
     *            "jpg"/"jpeg","gif","bmp","wbmp","tif","png"
     * @param width
     *            The desired width of the converted image in pixels. The aspect
     *            ratio of the original image will be preserved so if the set
     *            width and/or height violates the aspect ratio they will be
     *            adjusted automatically
     * @param height
     *            The desired height of the converted image in pixels. The
     *            aspect ratio of the original image will be preserved so if the
     *            set width and/or height violates the aspect ratio they will be
     *            adjusted automatically
     */
    private void init(byte[] inImage, String inFormat, int inHeight, int inWidth, boolean noEnlargement) {
        this.image = inImage;
        this.format = inFormat;
        this.width = inWidth;
        this.height = inHeight;
        this.no_enlargement = noEnlargement;
    }

    /**
     * Adds a text watermark that will be applied to the converted image.
     * Several watermarks can be added.
     * 
     * @param text
     *            The text that is to be watermarked to the converted image
     * @param WatermarkPosition
     *            The desired placement of the text watermark on the converted
     *            image. Acceptable position values can be retrieved from the
     *            ImageConverter class. ie ImageConverter.WMARK_POS_TOPLEFT
     */
    public void addTextWatermark(String text, int WatermarkPosition) {
        if(textWatermarks == null){
            textWatermarks = new Vector(1);
        }
        textWatermarks.add(new TextWatermark(text, WatermarkPosition));
        hasTextWatermarks = true;
    }
    
    /**
     * Adds a text watermark that will be applied to the converted image.
     * Several watermarks can be added.
     * 
     * @param tw The TextWatermark to add
     */
    public void addTextWatermark(TextWatermark tw) {
        if(textWatermarks == null){
            textWatermarks = new Vector(1);
        }
        textWatermarks.add(tw);
        hasTextWatermarks = true;
    }

    /**
     * Adds a text watermark that will be applied to the converted image.
     * Several watermarks can be added.
     * 
     * @param text
     *            The text that is to be watermarked to the converted image
     * @param WatermarkPosition
     *            The desired placement of the text watermark on the converted
     *            image. Acceptable position values can be retrieved from the
     *            ImageConverter class. ie ImageConverter.WMARK_POS_TOPLEFT
     * @param font
     *            The font with which the text will be written
     */
    public void addTextWatermark(String text, int WatermarkPosition, Font font) {
        if(textWatermarks == null){
            textWatermarks = new Vector(1);
        }
        textWatermarks.add(new TextWatermark(text, WatermarkPosition, font));
        hasTextWatermarks = true;
    }

    /**
     * Adds a text watermark that will be applied to the converted image.
     * Several watermarks can be added.
     * 
     * @param text
     *            The text that is to be watermarked to the converted image
     * @param WatermarkPosition
     *            The desired placement of the text watermark on the converted
     *            image. Acceptable position values can be retrieved from the
     *            ImageConverter class. ie ImageConverter.WMARK_POS_TOPLEFT
     * @param color
     *            The color of the text written
     */
    public void addTextWatermark(String text, int WatermarkPosition, Color color) {
        if(textWatermarks == null){
            textWatermarks = new Vector(1);
        }
        textWatermarks.add(new TextWatermark(text, WatermarkPosition, color));
        hasTextWatermarks = true;
    }

    /**
     * Adds a text watermark that will be applied to the converted image.
     * Several watermarks can be added.
     * 
     * @param text
     *            The text that is to be watermarked to the converted image
     * @param WatermarkPosition
     *            The desired placement of the text watermark on the converted
     *            image. Acceptable position values can be retrieved from the
     *            ImageConverter class. ie ImageConverter.WMARK_POS_TOPLEFT
     * @param font
     *            The font with which the text will be written
     * @param color
     *            The color of the text written
     */
    public void addTextWatermark(String text, int WatermarkPosition, Font font,            
            Color color) {
        if(textWatermarks == null){
            textWatermarks = new Vector(1);
        }
        textWatermarks.add(new TextWatermark(text, WatermarkPosition, font,
                color));
        hasTextWatermarks = true;
    }

    /**
     * Adds an image watermark that will be applied to the converted image.
     * Several watermarks can be added.
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
    public void addImageWatermark(byte[] image, int WatermarkPosition,
            int width, int height) {
        if(imageWatermarks == null){
            imageWatermarks = new Vector(1);
        }
        imageWatermarks.add(new ImageWatermark(image, WatermarkPosition, width,
                height));
        hasImageWatermarks = true;
    }
    
    /**
     * Adds an image watermark that will be applied to the converted image.
     * Several watermarks can be added.
     * 
     * @param iw The ImageWatermark to add
     * 
     */
    public void addImageWatermark(ImageWatermark iw) {
        if(imageWatermarks == null){
            imageWatermarks = new Vector(1);
        }
        imageWatermarks.add(iw);
        hasImageWatermarks = true;
    }

    /**
     * Adds an image watermark that will be applied to the converted image.
     * Several watermarks can be added.
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
    public void addImageWatermark(byte[] image, int WatermarkPosition,
            double sizeFactor) {
        if(imageWatermarks == null){
            imageWatermarks = new Vector(1);
        }
        imageWatermarks.add(new ImageWatermark(image, WatermarkPosition,
                sizeFactor));
        hasImageWatermarks = true;
    }

    /**
     * Adds an image watermark that will be applied to the converted image.
     * Several watermarks can be added.
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
    public void addImageWatermark(byte[] image, int WatermarkPosition,
            int width, int height, float opaque) {
        if(imageWatermarks == null){
            imageWatermarks = new Vector(1);
        }
        imageWatermarks.add(new ImageWatermark(image, WatermarkPosition, width,
                height, opaque));
        hasImageWatermarks = true;
    }

    /**
     * Adds an image watermark that will be applied to the converted image.
     * Several watermarks can be added.
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
     *            A float representing the desired opacity of the watermark, the
     *            scale goes from 0.1f (transparent) to 1.0f (solid)
     */
    public void addImageWatermark(byte[] image, int WatermarkPosition,
            double sizeFactor, float opaque) {
        if(imageWatermarks == null){
            imageWatermarks = new Vector(1);
        }
        imageWatermarks.add(new ImageWatermark(image, WatermarkPosition,
                sizeFactor, opaque));
        hasImageWatermarks = true;
    }

    /**
     * Retrieves an Iterator to traverse and access the image matermarks
     * collection contained in the ImageConverterParams object
     * 
     * @return An Iterator object with which one can traverse the current image
     *         watermarks contained in the ImageConverterParams object
     */
    public Iterator getImageWatermarksIterator() {
        if(imageWatermarks != null){
            return imageWatermarks.iterator();
        } else {
            return null;
        }
    }

    /**
     * Retrieves an Iterator to traverse and access the text matermarks
     * collection contained in the ImageConverterParams object
     * 
     * @return An Iterator object with which one can traverse the current text
     *         watermarks contained in the ImageConverterParams object
     */
    public Iterator getTextWatermarksIterator() {
        if(textWatermarks != null){
            return textWatermarks.iterator();
        } else {
            return null;
        }
    }

    /**
     * Checks if the ImageConverterParams object contains any image watermarks
     * 
     * @return Returns true if the ImageConverterParams object has one or more
     *         image watermarks set
     */
    public boolean hasImageWatermarks() {
        return hasImageWatermarks;
    }

    /**
     * Checks if the ImageConverterParams object contains any text watermarks
     * 
     * @return Returns true if the ImageConverterParams object has one or more
     *         text watermarks set
     */
    public boolean hasTextWatermarks() {
        return hasTextWatermarks;
    }

    /**
     * Gets the format attribute of the ImageConverterParams object
     * 
     * @return Returns the current value of the format attribute
     */
    public String getFormat() {
        return format;
    }

    /**
     * Set the format attribute to specify the format of the conveted image. If
     * the format attribute is not set then the converted image will have the
     * same format as the original image
     * 
     * @param format
     *            The format of the converted image. Supported formats are:
     *            "jpg"/"jpeg","gif","bmp","wbmp","tif","png"
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * Checks what the grayscale attribute of the ImageConverterParams object
     * has been set to
     * 
     * @return Returns the current value of the Grayscale attribute
     */
    public boolean isGrayscale() {
        return grayscale;
    }

    /**
     * Set the Grayscale attribute to true to convert the original image to
     * grayscale. Setting Grayscale to false will only prevent the converted
     * image from being converted to grayscale, it cannot be used to change a
     * grayscale image to a color image.
     * 
     * @param grayscale
     *            Set to true to have the converted image in grayscale
     */
    public void setGrayscale(boolean grayscale) {
        this.grayscale = grayscale;
    }

    /**
     * Gets the height attribute of the ImageConverterParams object
     * 
     * @return Returns the current value of the height attribute
     */
    public int getHeight() {
        return height;
    }

    /**
     * Set the height attribute to specify the height of the converted image.
     * The aspect ratio of the original image will be preserved so if the set
     * height violates the aspect ratio it will be adjusted automatically
     * 
     * @param height
     *            The desired height of the converted image in pixels.
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Retrieves the original image
     * 
     * @return Returns the original image.
     */
    public byte[] getImage() {
        return image;
    }

    /**
     * Gets the JPEGCompressionQuality attribute of the ImageConverterParams
     * object
     * 
     * @return Returns the current value of the jPEGCompressionQuality attribute
     */
    public float getJPEGCompressionQuality() {
        return JPEGCompressionQuality;
    }

    /**
     * Applicable only if the converted image is to be in jpeg format. Set this
     * attribute to change the level of compression applied to the converted
     * image. A smaller value meeans more compression which means a smaller image with less quality.
     * 
     * @param compressionQuality
     *            The jPEGCompressionQuality to set.
     */
    public void setJPEGCompressionQuality(float compressionQuality) {
        JPEGCompressionQuality = compressionQuality;
    }

    /**
     * Gets the NumberOfColors attribute of the ImageConverterParams object
     * 
     * @return Returns the current value of the numberOfColors attribute
     */
    public int getNumberOfColors() {
        return numberOfColors;
    }

    /**
     * Set the numberOfColors attribute to specify how many colors the converted
     * image should have
     * 
     * @param numberOfColors
     *            The desired number of colors
     */
    public void setNumberOfColors(int numberOfColors) {
        this.numberOfColors = numberOfColors;
    }

    /**
     * Gets the width attribute of the ImageConverterParams object
     * 
     * @return Returns the current value of the width attribute
     */
    public int getWidth() {
        return width;
    }

    /**
     * Set the width attribute to specify the height of the converted image. The
     * aspect ratio of the original image will be preserved so if the set width
     * violates the aspect ratio it will be adjusted automatically
     * 
     * @param width
     *            The desired width of the converted image in pixels.
     */
    public void setWidth(int width) {
        this.width = width;
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
     /**
     * Sets the image that will be converted.
     * 
     * @param image The image to set.
     */
    public void setImage(byte[] image) {
        this.image = image;
    }

    /**
     * @return Returns the internalVariables.
     */
    public ImageConverterInternalVariables getInternalVariables() {
        if(internalVariables == null){
            internalVariables = new ImageConverterInternalVariables();
        }
        return internalVariables;
    }

    /**
     * @param internalVariables The internalVariables to set.
     */
    public void setInternalVariables(
            ImageConverterInternalVariables internalVariables) {
        this.internalVariables = internalVariables;
    }

    /**
     * @return Returns The amount (in percentage) that the image will be cropped from the bottom
     */
	public int getCropBottom() {
		return cropBottom;
	}

    /**
     * @param cropBottom The amount (in percentage) that the image will be cropped from the bottom
     */
	public void setCropBottom(int cropBottom) {
		this.cropBottom = cropBottom;
	}

    /**
     * @return Returns The amount (in percentage) that the image will be cropped from the left
     */
	public int getCropLeft() {
		return cropLeft;
	}

	/**
     * @param cropLeft The amount (in percentage) that the image will be cropped from the left
     */
	public void setCropLeft(int cropLeft) {
		this.cropLeft = cropLeft;
	}

    /**
     * @return Returns The amount (in percentage) that the image will be cropped from the right
     */
	public int getCropRight() {
		return cropRight;
	}

	/**
     * @param cropRight The amount (in percentage) that the image will be cropped from the right
     */
	public void setCropRight(int cropRight) {
		this.cropRight = cropRight;
	}

    /**
     * @return Returns The amount (in percentage) that the image will be cropped from the top
     */
	public int getCropTop() {
		return cropTop;
	}

	/**
     * @param cropTop The amount (in percentage) that the image will be cropped from the top
     */
	public void setCropTop(int cropTop) {
		this.cropTop = cropTop;
	}

	public boolean isFastMode() {
		return fastMode;
	}

	/**
	 * Set this to true to enable fast mode, this will priorities speed over quality and resulting image size. 
	 * Fast equals lower quality and bigger files. Fast mode only affects animated gif images. 
	 * @param fastMode true to enable fast mode, false to disable fast mode.
	 */
	public void setFastMode(boolean fastMode) {
		this.fastMode = fastMode;
	}

	public boolean isKeepAspectRatio() {
		return keepAspectRatio;
	}

	/**
	 * Set this to true if the aspect ratio of the original image should be preserved. 
	 * Setting this to false means the resulting size will be exactly as specified, even though 
	 * that may mean that the image will be stretched to fit the desired size. 
	 * 
	 * @param keepAspectRatio true to protect aspect ratio, false to resize to the exact specified target size. 
	 */
	public void setKeepAspectRatio(boolean keepAspectRatio) {
		this.keepAspectRatio = keepAspectRatio;
	}

	/**
	 * If a second image is provided via the overlay parameter, that image will be painted on top of the supplied image.
	 * 
	 * @param overlay The image that will be used as an overlay>
	 */
	public void setOverlay(byte[] overlay) {
		this.overlay = overlay;
	}

	public byte[] getOverlay() {
		return overlay;
	}

	public boolean ignoreHeight() {
		return this.ignoreHeight;
	}

	/**
	 * Setting this to true will ignore the height variable, thus setting the height to whatever
	 *  that will allow for the set width to be achieved within aspect ratio constraints.
	 * @param ignoreHeight
	 */
	public void setIgnoreHeight(boolean ignoreHeight) {
		this.ignoreHeight = ignoreHeight;
	}
       
	public enum RotationType {
		CLOCKWISE_90(1), ANTI_CLOCKWISE_90(2), FLIP(3);

		private final int type;

		RotationType(int value) {
			this.type = value;
		}

		public int getType() {
			return type;
		}

		public static RotationType convert(int value) {
			RotationType[] values = RotationType.values();
			for (RotationType current : values) {
				if (current.getType() == value) {
					return current;
				}
			}

			throw new EnumConstantNotPresentException(Enum.class,
					"Unknown ratation type: " + value);
		}
	}

	public RotationType getRotation() {
		return this.rotation;
	}

	/**
	 * Use this to rotate the image in one of the predefined ways that are available throught
	 *  the RotationType enum.
	 *  
	 * @param rotation
	 */
	public void setRotation(RotationType rotation) {
		this.rotation = rotation;
	}

    public Collection<ImageFXVO> getEffects() {
        return effects;
    }

    public void addEffect(ImageFXVO effect){
        if(effects == null){
            effects = new ArrayList<ImageFXVO>();
        }
        effects.add(effect);
    }

    public void setEffects(Collection<ImageFXVO> effects) {
        this.effects = effects;
    }
    
}
