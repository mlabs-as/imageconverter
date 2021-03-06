/*
 * Created on Mar 11, 2005
 * 
 */

package com.mobiletech.imageconverter;

import java.io.*;
import java.util.Iterator;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import javax.imageio.*;
import javax.imageio.stream.*;

import com.mobiletech.imageconverter.exception.ImageConverterException;
import com.mobiletech.imageconverter.fx.FXProcessor;
import com.mobiletech.imageconverter.io.DexImageReaderFactory;
import com.mobiletech.imageconverter.io.DexImageWriterFactory;
import com.mobiletech.imageconverter.io.ImageEncoder;
import com.mobiletech.imageconverter.modifiers.ImageColorModifier;
import com.mobiletech.imageconverter.modifiers.ImageCropper;
import com.mobiletech.imageconverter.modifiers.ImageOverlayWriter;
import com.mobiletech.imageconverter.modifiers.ImageRotater;
import com.mobiletech.imageconverter.modifiers.ImageScaler;
import com.mobiletech.imageconverter.readers.DexImageReader;
import com.mobiletech.imageconverter.util.ImageUtil;
import com.mobiletech.imageconverter.vo.ImageConverterParams;
import com.mobiletech.imageconverter.vo.ImageWatermark;
import com.mobiletech.imageconverter.vo.TextWatermark;
import com.mobiletech.imageconverter.vo.ImageConverterParams.RotationType;
import com.mobiletech.imageconverter.watermarks.ImageWatermarker;
import com.mobiletech.imageconverter.writers.DexImageWriter;
import com.mobiletech.imageconverter.writers.OptimizingAnimGifWriter;

/**
 * This class functions as an Image converter. Taking images and converting them
 * to different image formats. It can also perform other related operations like
 * resizing and color change.
 * <p/>
 * The class also provides the opportunity to add text and images as watermarks
 * on the original image.
 *
 * @author Andreas Ryland
 */
public class ImageConverter {
    public static final String version = "ImageConverter version 1.3.8";

    public static final int WMARK_POS_TOPLEFT = 1;
    public static final int WMARK_POS_TOPRIGHT = 2;
    public static final int WMARK_POS_BOTTOMLEFT = 3;
    public static final int WMARK_POS_BOTTOMRIGHT = 4;
    public static final int WMARK_POS_CENTER = 5;
    public static final int WMARK_POS_DIAGONAL_CENTER = 6;
    public static boolean HAS_SCANNED = false;

    /**
     * Takes an original image and performs conversion on it to produce a
     * converted image. See the ImageConverterParams class for more information
     * on how to specify the conversions to be done.
     *
     * @param imageParams The ImageConverterParams object containing the original image
     *                    and the desired settings specifying how the converted image
     *                    should be.
     * @return A byte array containing the converted image
     * @throws ImageConverterException
     */
    public static byte[] convertImage(ImageConverterParams imageParams) throws ImageConverterException {
        if (!HAS_SCANNED) {
            ImageIO.scanForPlugins();
            HAS_SCANNED=true;
        }
        return convertImage(imageParams, null);
    }

    /**
     * Takes an original image and performs conversion on it to produce a
     * converted image. See the ImageConverterParams class for more information
     * on how to specify the conversions to be done.
     *
     * @param imageParams The ImageConverterParams object containing the original image
     *                    and the desired settings specifying how the converted image
     *                    should be.
     * @param dim         A preallocated Dimension object that wil be used to return the width and height of the resulting image.
     * @return A byte array containing the converted image
     * @throws ImageConverterException
     */
    public static byte[] convertImage(ImageConverterParams imageParams, Dimension dim) throws ImageConverterException {
        if (!HAS_SCANNED) {
            ImageIO.scanForPlugins();
            HAS_SCANNED=true;
        }
        // Determine pipeline
        //imageParams.setFastMode(false);
        // run pipeline
        //imageParams.setNumberOfColors(-1);
        byte[] returnByte = null;

        try {
            // Just in case: Reset the internal variables of the ImageConverterParams object
            imageParams.resetInternal();
            //Validate input parameters
            imageParams = validateParams(imageParams);

            DexImageReader reader = DexImageReaderFactory.getImageReader(imageParams);
            DexImageWriter writer = null;
            BufferedImage temp = null;

            try {
                while (reader.hasMore()) {
                    temp = reader.getNext();

                    doPipeline(temp, imageParams);

                    // write image
                    temp = imageParams.getInternalVariables().getBufferedImage();
                    //temp = ImageEncoder.prepareForConversion(temp, imageParams);
                    if (writer == null) {
                        if (dim != null) {
                            if (temp != null) {
                                dim.height = temp.getHeight();
                                dim.width = temp.getWidth();
                            }
                        }
                        writer = DexImageWriterFactory.getImageWriter(temp, imageParams);
                    }
                    if (!(writer instanceof OptimizingAnimGifWriter)) {
                        temp = ImageEncoder.prepareForConversion(temp, imageParams);
                    }
                    writer.writeNext(temp);
                    if (!writer.canWriteMore()) {
                        break;
                    }
                }
                returnByte = writer.getByte();
            } finally {
                if (reader != null) {
                    reader.dispose();
                    reader = null;
                }
                if (writer != null) {
                    writer.dispose();
                    writer = null;
                }
            }
            // If the image has not been changed, check if the image format was to be converted, or, in case of jpeg to jpeg conversion, if the
            // compression factor should be changed (thus needing the jpeg to be re-encoded with the new compression setting) if neither of these
            // cases are true, then just return the original image
            if (!imageParams.getInternalVariables().isChanged()) {
                // if there was no change in jpeg compression quality...
                if (imageParams.getJPEGCompressionQuality() <= 0.0) {
                    if (imageParams.getFormat().equalsIgnoreCase(imageParams.getInternalVariables().getOldFormat())) {
                        return imageParams.getImage();
                    } else if ((imageParams.getInternalVariables().getOldFormat().equalsIgnoreCase("jpg") ||
                            imageParams.getInternalVariables().getOldFormat().equalsIgnoreCase("jpeg")) &&
                            (imageParams.getFormat().equalsIgnoreCase("jpg") || imageParams.getFormat().equalsIgnoreCase("jpeg"))) {
                        return imageParams.getImage();
                    }
                }
            }
        } catch (ImageConverterException e) {
            imageParams.resetInternal(); // (Paranoia aka Just-In-Case)
            throw e;
        } catch (Throwable t) {
            throw new ImageConverterException(ImageConverterException.Types.EMBEDDED_EXCEPTION, t.getClass().getName() + " thrown: " + t.getMessage(), t);
        } finally {
            imageParams.resetInternal();
        }
        return returnByte;
    }

    public static BufferedImage getBufferedImage(byte[] image) throws ImageConverterException {
        if (!HAS_SCANNED) {
            ImageIO.scanForPlugins();
            HAS_SCANNED=true;
        }
        ImageConverterParams params = new ImageConverterParams(image);
        BufferedImage temp = null;
        params = validateParams(params);
        DexImageReader reader = DexImageReaderFactory.getImageReader(params);

        if (reader.hasMore()) {
            temp = reader.getNext();
        }

        return temp;
    }

    private static ImageConverterParams doPipeline(BufferedImage image, ImageConverterParams imageParams) throws ImageConverterException {
        // add overlay if one was provided
        if (imageParams.getOverlay() != null) {
            BufferedImage temp = null;
            temp = ImageOverlayWriter.addOverlay(image, imageParams);

            if (temp != null) {
                image = null;
                image = temp;
                temp = null;
                imageParams.getInternalVariables().setChanged(true);
            }
        }
        // perform cropping if requested
        if (imageParams.getCropBottom() > 0 ||
                imageParams.getCropLeft() > 0 ||
                imageParams.getCropRight() > 0 ||
                imageParams.getCropTop() > 0) {
            image = ImageCropper.cropImageByPercentage(image, imageParams.getCropTop(), imageParams.getCropBottom(), imageParams.getCropLeft(), imageParams.getCropRight());
            imageParams.getInternalVariables().setChanged(true);
        }
        // Rotate if requested
        if (imageParams.getRotation() != null) {
            image = ImageRotater.rotate(image, imageParams);
        }
        // Perform resize if requested
        //imageParams.getInternalVariables().setChanged(true);
        if (imageParams.getWidth() > 0 || imageParams.getHeight() > 0) {
            BufferedImage temp = null;
            temp = ImageScaler.resizeImage(image, imageParams.getHeight(), imageParams.getWidth(), imageParams.isNoEnlargement(), (imageParams.getInternalVariables().getTransparentColor() != null ? true : false), imageParams);

            if (temp != null) {
                image = null;
                image = temp;
                temp = null;
                imageParams.getInternalVariables().setChanged(true);
            }
        }
        // Add image watermarks if any
        if (imageParams.hasImageWatermarks()) {
            Iterator watermarks = imageParams.getImageWatermarksIterator();
            while (watermarks.hasNext()) {
                image = ImageWatermarker.applyImageWatermark(image, (ImageWatermark) watermarks.next());
            }
            imageParams.getInternalVariables().setChanged(true);
        }
        // Add text watermarks if any
        if (imageParams.hasTextWatermarks()) {
            Iterator watermarks = imageParams.getTextWatermarksIterator();
            while (watermarks.hasNext()) {
                image = ImageWatermarker.applyTextWatermark(image, (TextWatermark) watermarks.next());
            }
            imageParams.getInternalVariables().setChanged(true);
        }
        // Perform color reduction if requested
        if (imageParams.getNumberOfColors() > 0) {
            //image = ImageColorModifier.colorChange(image,imageParams.getNumberOfColors());
            //imageParams.getInternalVariables().setChanged(true);
        }
        // Convert to grayscale if requested
        if (imageParams.isGrayscale()) {
            image = ImageUtil.toBuffImageRGBorARGB(image);
            image = ImageColorModifier.getGrayscale(image);
            imageParams.getInternalVariables().setChanged(true);
        }
        // do ImageFX
        if (imageParams.getEffects() != null) {
            image = FXProcessor.processEffects(imageParams, image);
            imageParams.getInternalVariables().setChanged(true);
        }
        imageParams.getInternalVariables().setBufferedImage(image);
        // Return converted image
        return imageParams;
    }

    private static ImageConverterParams validateParams(ImageConverterParams inParams) throws ImageConverterException {
        if (null == inParams.getImage()) {
            throw new ImageConverterException(ImageConverterException.Types.BAD_INPUT_VARIABLE, "Bad input argument: Image ByteArray is null", null);
        }
        if (inParams.getImage().length <= 0) {
            throw new ImageConverterException(ImageConverterException.Types.BAD_INPUT_VARIABLE, "Bad input argument: Image ByteArray of length 0!", null);
        }
        String format = inParams.getFormat();
        inParams.getInternalVariables().setOldFormat(getImageFormatName(inParams.getImage()));

        if (null == format || format.trim().compareTo("") == 0) {
            format = inParams.getInternalVariables().getOldFormat();
            inParams.setFormat(format);
        }
        if (format.length() < 3 || format.length() > 4) {
            throw new ImageConverterException(ImageConverterException.Types.BAD_INPUT_VARIABLE, "Bad input argument: requested codec name not in 3 or 4 letter format Format: '" + format + "'", null);
        }
        if (inParams.getFormat().equalsIgnoreCase("wbmp") && inParams.isGrayscale()) {
            inParams.setGrayscale(false);
        }
        /*
        if(!(format.compareToIgnoreCase("png") == 0 || 
             format.compareToIgnoreCase("gif") == 0 ||
             format.compareToIgnoreCase("wbmp") == 0 ||
             format.compareToIgnoreCase("jpg") == 0 ||
             format.compareToIgnoreCase("jpeg") == 0 ||
             format.compareToIgnoreCase("tif") == 0 ||
             format.compareToIgnoreCase("bmp") == 0)){
            //throw new ImageConverterException(ImageConverterException.Types.CODEC_NOT_SUPPORTED,"Requested codec not supported: " + format,null);
        }       
        */
        //  throw new ImageConverterException(ImageConverterException.Types.BAD_RESIZE_SIZE,"Cannot resize to size height: "+ inHeight + " width: " + inWidth,null);        
        return inParams;
    }

    public static String getImageFormatName(byte[] inImage) throws ImageConverterException {
        if (!HAS_SCANNED) {
            ImageIO.scanForPlugins();
            HAS_SCANNED=true;
        }
        ByteArrayInputStream imageStream = null;
        ImageInputStream iis = null;
        String format = null;

        try {
            imageStream = new ByteArrayInputStream(inImage);
            iis = ImageIO.createImageInputStream(imageStream);
            Iterator readers = ImageIO.getImageReaders(iis);

            if (readers.hasNext()) {
                ImageReader reader = (ImageReader) readers.next();
                format = reader.getFormatName();
            } else {
                throw new ImageConverterException(ImageConverterException.Types.READ_CODEC_NOT_FOUND, "No image readers found for the image type of the supplied image", null);
            }
        } catch (IOException iox) {
            throw new ImageConverterException(ImageConverterException.Types.IO_ERROR, "IOException caught when attempting to get imagereader for ByteArray: " + iox.getMessage(), iox);
        } finally {
            if (iis != null) {
                try {
                    iis.close();
                } catch (IOException ignored) {
                }
                iis = null;
            }
            if (imageStream != null) {
                try {
                    imageStream.close();
                } catch (IOException ignored) {
                }
                imageStream = null;
            }
        }

        return format;
    }

    public static boolean isSupportedImage(File image) {
        if (!HAS_SCANNED) {
            ImageIO.scanForPlugins();
            HAS_SCANNED=true;
        }
        boolean result = false;

        if (null == image) {
            return result;
        }
        ImageInputStream iis = null;
        Iterator readers = null;
        try {
            iis = ImageIO.createImageInputStream(image);
            readers = ImageIO.getImageReaders(iis);
            if (readers.hasNext()) {
                result = true;
            }
        } catch (Throwable ignored) {
        } finally {
            if (null != iis) {
                try {
                    iis.close();
                } catch (IOException e) {
                }
            }
            readers = null;
        }

        return result;
    }

    public static String getFormatOfSupportedImage(File image) {
        if (!HAS_SCANNED) {
            ImageIO.scanForPlugins();
            HAS_SCANNED=true;
        }
        String format = null;

        if (null == image) {
            return format;
        }
        ImageInputStream iis = null;
        Iterator readers = null;
        try {
            iis = ImageIO.createImageInputStream(image);
            readers = ImageIO.getImageReaders(iis);
            if (readers.hasNext()) {
                ImageReader reader = (ImageReader) readers.next();
                format = reader.getFormatName();
                reader = null;
            }
        } catch (Throwable ignored) {
        } finally {
            if (null != iis) {
                try {
                    iis.close();
                } catch (IOException e) {
                }
            }
            readers = null;
        }

        return format;
    }

    public static int getNumberOfFramesInImage(File image) {
        if (!HAS_SCANNED) {
            ImageIO.scanForPlugins();
            HAS_SCANNED=true;
        }
        int frames = 1;

        if (null == image) {
            return frames;
        }
        ImageInputStream iis = null;
        Iterator readers = null;
        try {
            iis = ImageIO.createImageInputStream(image);
            readers = ImageIO.getImageReaders(iis);
            if (readers.hasNext()) {
                ImageReader reader = (ImageReader) readers.next();
                frames = reader.getNumImages(true);
                reader = null;
            }
        } catch (Throwable ignored) {
        } finally {
            if (null != iis) {
                try {
                    iis.close();
                } catch (IOException e) {
                }
            }
            readers = null;
        }
        return frames;
    }

    public static Dimension calculateConvertedImageDimension(int width, int height, int desiredWidth, int desiredHeight, boolean noEnlargement) {
        if (!HAS_SCANNED) {
            ImageIO.scanForPlugins();
            HAS_SCANNED=true;
        }
        return calculateConvertedImageDimension(width, height, desiredWidth, desiredHeight, noEnlargement, null, null, null, null, false, null);
    }

    public static Dimension calculateConvertedImageDimension(int width, int height, int desiredWidth, int desiredHeight, boolean noEnlargement, String cropLeft, String cropRight, String cropTop, String cropBottom) {
        if (!HAS_SCANNED) {
            ImageIO.scanForPlugins();
            HAS_SCANNED=true;
        }
        return calculateConvertedImageDimension(width, height, desiredWidth, desiredHeight, noEnlargement, cropLeft, cropRight, cropTop, cropBottom, false, null);
    }


    private static double calculateCropping(String crop, double widthHeight) {
        try {
            if (crop.contains("px")) {
                crop = crop.replaceAll("px", "");
                double iCrop = Double.parseDouble(crop);
                return widthHeight - iCrop;
            }
        } catch (Exception ignore) {

        }

        try {
            double iCrop = Double.parseDouble(crop);
            return (widthHeight * (iCrop / 100));
        } catch (Exception ignore) {

        }


        return widthHeight;
    }

    public static Dimension calculateConvertedImageDimension(int width, int height, int desiredWidth, int desiredHeight, boolean noEnlargement, String cropLeft, String cropRight, String cropTop, String cropBottom, boolean ignoreHeight, RotationType rotate) {
        if (!HAS_SCANNED) {
            ImageIO.scanForPlugins();
            HAS_SCANNED=true;
        }
        if (cropLeft != null || cropRight != null || cropTop != null || cropBottom != null) {
            double w = width;
            double h = height;
            double x = 0, y = 0, dwidth = w, dheight = h;
            if (cropTop != null) {
                y =  calculateCropping(cropTop,h);//(h * (cropTop / 100.0));
                y =
                        dheight -= y;
            }
            if (cropLeft != null) {
                x = calculateCropping(cropLeft,w);//(w * (cropLeft / 100.0));
                dwidth -= x;
            }
            if (cropRight != null) {
                dwidth -= calculateCropping(cropRight,dwidth);//(dwidth * (cropRight / 100.0));
            }
            if (cropBottom != null) {
                dheight -= calculateCropping(cropBottom,dheight);//(dheight * (cropBottom / 100.0));
            }
            width = (int) dwidth;
            height = (int) dheight;
        }
        Dimension dim = new Dimension();
        double scale = ImageScaler.getResizeScale(width, height, desiredWidth, desiredHeight, ignoreHeight, rotate);
        if (noEnlargement && scale > 1.0) {
            dim.height = height;
            dim.width = width;
        } else {
            int newWidth = (int) (width * scale);
            int newHeight = (int) (height * scale);
            if (newWidth == 0) {
                newWidth = 1;
            }
            if (newHeight == 0) {
                newHeight = 1;
            }
            dim.height = newHeight;
            dim.width = newWidth;
            if (rotate != null) {
                if (rotate == RotationType.CLOCKWISE_90 || rotate == RotationType.ANTI_CLOCKWISE_90) {
                    dim.height = newWidth;
                    dim.width = newHeight;
                }
            }
        }

        return dim;
    }

    public static boolean isSupportedImageFormat(String format) {
        if (!HAS_SCANNED) {
            ImageIO.scanForPlugins();
            HAS_SCANNED=true;
        }
        boolean returnvalue = false;
        if (null == format) {
            return false;
        }
        Iterator readers = null;
        try {
            readers = ImageIO.getImageReadersByFormatName(format);
            if (readers.hasNext()) {
                returnvalue = true;
            }
        } catch (Throwable ignored) {
        } finally {
            readers = null;
        }

        return returnvalue;
    }

    public static String getVersionInformation() {
        /*
        if (com.sun.medialib.codec.jiio.Util.isCodecLibAvailable()) {
            return version + " Native Libraries Found.";
        } else {
            return version + " Not using Native Libraries.";
        } 
        */
        return version;
        //return "";
    }

    public static Dimension getImageDimension(byte[] image) throws ImageConverterException {
        if (!HAS_SCANNED) {
            ImageIO.scanForPlugins();
            HAS_SCANNED=true;
        }
        Dimension dim = new Dimension();
        getImageDimensionAndFormat(image, dim);
        return dim;
    }

    public static String getImageDimensionAndFormat(byte[] image, Dimension dim) throws ImageConverterException {
        if (!HAS_SCANNED) {
            ImageIO.scanForPlugins();
            HAS_SCANNED=true;
        }
        ByteArrayInputStream imageStream = null;
        ImageInputStream iis = null;
        String format = null;

        try {
            imageStream = new ByteArrayInputStream(image);
            iis = ImageIO.createImageInputStream(imageStream);
            Iterator readers = ImageIO.getImageReaders(iis);

            if (readers.hasNext()) {
                ImageReader reader = (ImageReader) readers.next();
                reader.setInput(iis);
                format = reader.getFormatName();
                if (dim!=null) {
                    dim.setSize(reader.getWidth(reader.getMinIndex()), reader.getHeight(reader.getMinIndex()));
                }
            } else {
                throw new ImageConverterException(ImageConverterException.Types.READ_CODEC_NOT_FOUND, "No image readers found for the image type of the supplied image", null);
            }
        } catch (ImageConverterException i) {
            throw i;
        } catch (IOException iox) {
            throw new ImageConverterException(ImageConverterException.Types.IO_ERROR, "IOException caught when attempting to get imagereader for ByteArray: " + iox.getMessage(), iox);
        } finally {
            if (iis != null) {
                try {
                    iis.close();
                } catch (IOException ignored) {
                }
            }
            if (imageStream != null) {
                try {
                    imageStream.close();
                } catch (IOException ignored) {
                }
            }
        }

        return format;        
    }
}