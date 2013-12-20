package com.mobiletech.imageconverter.writers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.mobiletech.imageconverter.exception.ImageConverterException;
import com.mobiletech.imageconverter.vo.ImageConverterParams;
import com.sun.media.jai.codecimpl.JPEGImageEncoder;
import java.util.Iterator;
import java.util.Locale;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

public class JPEGImageWriter implements DexImageWriter {

    private byte[] result = null;
    private ImageConverterParams params = null;
    private ByteArrayOutputStream output = null;

    private JPEGImageWriter() {
    }

    public JPEGImageWriter(ImageConverterParams params) {
        this.params = params;
    }

    public boolean canWriteMore() {
        return false;
    }

    public byte[] getByte() throws ImageConverterException {
        try {
            output.flush();
            result = output.toByteArray();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public void dispose() {
        try {
            output.close();
        } catch (IOException ignored) {
        }
        output = null;
    }

    public void writeNext(BufferedImage image) throws ImageConverterException {
        output = new ByteArrayOutputStream();
        ImageWriter imgWriter = null;
        try {
            ImageOutputStream ios = ImageIO.createImageOutputStream(output);
            JPEGImageEncoder enc;

            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
            if (writers != null) {
                imgWriter = writers.next();
            }

            ImageWriteParam writeParam = imgWriter.getDefaultWriteParam();
            writeParam = new JPEGImageWriteParam(Locale.US);
            ((JPEGImageWriteParam) writeParam).setOptimizeHuffmanTables(true);
            if (params.getJPEGCompressionQuality() != 0 && writeParam.canWriteCompressed()) {
                writeParam.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
                writeParam.setCompressionQuality(params.getJPEGCompressionQuality());
            }
            IIOImage img = new IIOImage(image, null, null);
            imgWriter.write(null, img, writeParam);
        } catch (IOException e) {
            throw new ImageConverterException(ImageConverterException.Types.IO_ERROR, "IOException thrown when writing encoded image ", e);
        } finally {
            if (imgWriter != null) {
                imgWriter.dispose();
            }
        }
    }
}
