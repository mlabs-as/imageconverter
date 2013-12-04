package com.mobiletech.imageconverter.readers;

import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.renderable.ParameterBlock;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;

import org.w3c.dom.NodeList;

import com.mobiletech.imageconverter.exception.ImageConverterException;
import com.mobiletech.imageconverter.filters.JPEGApp14Filter;
import com.mobiletech.imageconverter.io.ImageDecoder;
import com.mobiletech.imageconverter.jaiextensions.ByteArrayLoadOpDescriptor;
import com.mobiletech.imageconverter.util.ImageUtil;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.media.jai.codec.ByteArraySeekableStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JPEGImageReader implements DexImageReader {

    private byte[] inByteArray = null;
    private int counter = 0;

    public String getFormat() {
        return "jpg";
    }

    private JPEGImageReader() {
    }

    public JPEGImageReader(byte[] inByteArray) {
        this.inByteArray = inByteArray;
        if (inByteArray==null) {
            System.out.println("inByteArray was null");
        }
    }

    public BufferedImage getNext() throws ImageConverterException {
        ByteArrayInputStream image = null;
        try {
            image = new ByteArrayInputStream(inByteArray);
            return ImageIO.read(image);
        } catch (IOException ioe) {
            Logger.getLogger(JPEGImageReader.class.getName()).log(Level.SEVERE, "Got an exception when trying to read the JPEG image.", ioe);
            throw new ImageConverterException(ImageConverterException.Types.READ_CODEC_NOT_FOUND, "No image readers found for the image type of the supplied image", ioe);
        } finally {
                if (image != null) {
                    try {
                        image.close();
                    } catch (IOException ex) {
                        //Logger.getLogger(JPEGImageReader.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
        }
        /*
        counter++;
        BufferedImage result = null;

        ByteArrayInputStream imageStream = null;
        ImageInputStream iis = null;
        ImageReader reader = null;
        JPEGApp14Filter jpegFilter = new JPEGApp14Filter();
        int filterLevel = 1;
        int maxFilterLevel = JPEGApp14Filter.getMaxFilterLevel();
        boolean success = false;
        boolean isfirst = true;


        byte[] temp = null; //incrimental filtering not working, need to save the original
        try {
            while (filterLevel <= maxFilterLevel && !success) {
                temp = null;
                temp = jpegFilter.filter(inByteArray, filterLevel);
                imageStream = null;
                imageStream = new ByteArrayInputStream(temp);

                if (isfirst) {
                    isfirst = false;
                    String startdefinition = Integer.toHexString(imageStream.read()) + Integer.toHexString(imageStream.read()) + Integer.toHexString(imageStream.read()) + Integer.toHexString(imageStream.read());
                    if (startdefinition != null && !startdefinition.equalsIgnoreCase("ffd8ffe0")) {
                        // was not JFIF image
                        String signature = startdefinition + this.getJPEGHeaderSignature(imageStream);
                        if (signature.equalsIgnoreCase("ffd8ffdbffdbffc0ffc4ffc4ffc4ffc4")) {
                            // Trying to match troublesome images from Washington Post                            
                            imageStream = null;
                            imageStream = new ByteArrayInputStream(temp);
                            try {
                                JPEGImageDecoder dec = JPEGCodec.createJPEGDecoder(imageStream);
                                result = dec.decodeAsBufferedImage();
                                success = true;
                            } catch (Throwable t) {
                                // proceed with alternative process at filterlevel 2
                                continue;
                            }
                            break;
                        }
                    }
                    imageStream = null;
                    imageStream = new ByteArrayInputStream(temp);
                    isfirst = false;
                }
                iis = ImageIO.createImageInputStream(imageStream);

                Iterator readers = ImageIO.getImageReaders(iis);

                if (readers.hasNext()) {
                    reader = (ImageReader) readers.next();
                } else {
                    throw new ImageConverterException(ImageConverterException.Types.READ_CODEC_NOT_FOUND, "No image readers found for the image type of the supplied image", null);
                }

                        reader.setInput(iis, false);
                        try {
                            result = reader.read(0);
                            success = true;
                        } catch (Throwable e) {
                            Logger.getLogger(JPEGImageReader.class.getName()).log(Level.SEVERE, "Got an exception when trying to read the JPEG image.", e);
                            // If this was an unsupported jpeg image, it could have been CMYK or YCCK, these are not supported by
                            // ImageIO, we can try to load the image using thrid party code.
                            if (e.getMessage().equalsIgnoreCase("Unsupported Image Type")) {
                                try {
                                    IIOMetadata metadata = reader.getImageMetadata(0);
                                    if (null != metadata) {
                                        String metadataFormat = metadata.getNativeMetadataFormatName();
                                        IIOMetadataNode iioNode = (IIOMetadataNode) metadata.getAsTree(metadataFormat);

                                        NodeList children = iioNode.getElementsByTagName("app14Adobe");
                                        if (children.getLength() > 0) {
                                            result = createJPEG4(inByteArray);
                                            success = true;
                                        }
                                    }
                                } catch (IOException ioe) {
                                    Logger.getLogger(JPEGImageReader.class.getName()).log(Level.SEVERE, "Got an exception when trying to read MetaData of the JPEG image.", ioe);
                                }
                            }
                            // We got an io exception, attempt to increase level of filtering to make the image readable
                            filterLevel++;
                            if (filterLevel > maxFilterLevel) {
                                // If the filtering level has exceeded the max, then we have already tried to 
                                // strip the entire metadata and it makes no difference increasing the filter level any higher
                                throw e;
                            }
                        } finally {
                    if (reader != null) {
                                reader.dispose();
                                reader = null;
                                }
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
            }
        } catch (Throwable ioe) {
            Logger.getLogger(JPEGImageReader.class.getName()).log(Level.WARNING, "Throwable caught when reading JPEG image", ioe);
            throw new ImageConverterException(ImageConverterException.Types.IO_ERROR, "IOException thrown when reading from InputByteStream", ioe);
        } finally {
            if (reader != null) {
                reader.dispose();
                reader = null;
            }
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
        // this is for some known problem images that have been processed in photoshop
        // they will cause crashes and/or get wrong colors unless they are converted to
        // have a known image type, this conversion can be quite time consuming for some of these
        // images and will reduce the filesize in some cases
        if (result != null && result.getType() == 0) {
            result = ImageUtil.toBuffImageRGBorARGB(result);
        }
        return result;
        */
    }

    public boolean hasMore() {
        return counter < 1;
    }

    public void dispose() {
        // nothing to dispose
    }

    private static BufferedImage createJPEG4(byte[] source) throws IOException {
        ByteArrayLoadOpDescriptor.register();
        ByteArraySeekableStream bass = null;

        bass = new ByteArraySeekableStream(source);

        RenderedOp op = JAI.create("byteArrayLoad", bass);

        op = ImageDecoder.convertYCCKtoRGB(op);
        BufferedImage image = op.getAsBufferedImage();
        op.dispose();
        return image;
    }

    /**
     * Java's ImageIO can't process 4-component images and Java2D can't apply
     * AffineTransformOp either, so convert raster data to RGB. Technique due to
     * MArk Stephens. Free for any use.
     */
    public static RenderedOp convertYCCKtoRGB(RenderedOp src) {
        double[][] matrix = {
            {-1.0D, 0.0D, 0.0D, 1.0D, 0.0D},
            {0.0D, -1.0D, 0.0D, 1.0D, 0.0D},
            {0.0D, 0.0D, -1.0D, 1.0D, 0.0D},};

        // Step 1: 4-band nach 3-band
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(src);
        pb.add(matrix);
        // Perform the band combine operation.
        src = JAI.create("bandcombine", pb, null);

        // Step 2: CMY to RGB
        ParameterBlockJAI pbjai = new ParameterBlockJAI("colorconvert");
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        int[] bits = {8, 8, 8};
        ColorModel cm = new ComponentColorModel(cs, bits, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        pbjai.addSource(src);
        pbjai.setParameter("colormodel", cm);

        // ImageLayout for RenderingHints
        ImageLayout il = new ImageLayout();
        // compatible sample model
        il.setSampleModel(cm.createCompatibleSampleModel(src.getWidth(), src.getHeight()));
        RenderingHints hints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, il);
        // Perform the color conversion.
        RenderedOp dst = JAI.create("colorconvert", pbjai, hints);

        return dst;
    }

    private String getJPEGHeaderSignature(ByteArrayInputStream in) {
        String sign = "";
        String temp = null;
        do {
            temp = getNextSegment(in);
            if (temp != null && !temp.equalsIgnoreCase("ffda")) {
                sign += temp;
            }
        } while (temp != null && !temp.equalsIgnoreCase("ffda"));
        return sign;
    }

    private String getNextSegment(ByteArrayInputStream in) {
        int i = 0;
        while (i != -1) {
            i = in.read();
            if (i == 0xff) {
                i = in.read();
                return "ff" + Integer.toHexString(i);
            }
        }
        return null;
    }
}
