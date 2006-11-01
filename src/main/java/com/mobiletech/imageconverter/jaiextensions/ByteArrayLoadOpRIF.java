package com.mobiletech.imageconverter.jaiextensions;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

import javax.media.jai.JAI;
import javax.media.jai.OpImage;
import javax.media.jai.OperationRegistry;
import javax.media.jai.RenderedImageAdapter;
import javax.media.jai.registry.RIFRegistry;
import javax.media.jai.util.ImagingListener;

import com.sun.media.jai.codec.ByteArraySeekableStream;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageDecodeParam;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.util.ImageUtil;

class StreamImage extends RenderedImageAdapter {
    private InputStream stream;

    /*
     * Create the object and cache the stream.
     */
    public StreamImage(RenderedImage image,
                       InputStream stream) {
        super(image);
        this.stream = stream;
        if(image instanceof OpImage) {
            // Set the properties related to TileCache key as used in
            // RenderedOp.
            setProperty("tile_cache_key", image);
            Object tileCache = ((OpImage)image).getTileCache();
            setProperty("tile_cache",
                        tileCache == null ?
                        java.awt.Image.UndefinedProperty : tileCache);
        }
    }

    public void dispose() {
        // Use relection to invoke dispose();
        RenderedImage trueSrc = getWrappedImage();
        Method disposeMethod = null;
        try {
            Class cls = trueSrc.getClass();
            disposeMethod = cls.getMethod("dispose", null);
            if(!disposeMethod.isAccessible()) {
                AccessibleObject.setAccessible(new AccessibleObject[] {
                    disposeMethod
                }, true);
            }
            disposeMethod.invoke(trueSrc, null);
        } catch(Exception e) {
            // Ignore it.
        }
    }

    /*
     * Close the stream.
     */
    protected void finalize() throws Throwable {
        stream.close();
        super.finalize();
    }
}

/**
 * @see javax.media.jai.operator.FileDescriptor
 *
 * @since EA3
 *
 */
public class ByteArrayLoadOpRIF implements RenderedImageFactory {

    /** Constructor. */
    public ByteArrayLoadOpRIF() {}

    /**
     * Creates an image from a String containing a file name.
     */
    public RenderedImage create(ParameterBlock args,
                                RenderingHints hints) {
        ImagingListener listener = ImageUtil.getImagingListener(hints);
        ByteArraySeekableStream bass = (ByteArraySeekableStream)args.getObjectParameter(0);
        try {
            ImageDecodeParam param = null;
            if (args.getNumParameters() > 1) {
                param = (ImageDecodeParam)args.getObjectParameter(1);
            }

            ParameterBlock newArgs = new ParameterBlock();
            newArgs.add(bass);
            newArgs.add(param);

            RenderingHints.Key key = JAI.KEY_OPERATION_BOUND;
            int bound = OpImage.OP_IO_BOUND;
            if (hints == null) {
                hints = new RenderingHints(key, new Integer(bound));
            } else if (!hints.containsKey(key)) {
                hints = (RenderingHints)hints.clone();
                hints.put(key, new Integer(bound));
            }

            // Get the registry from the hints, if any.
            // Don't check for null hints as it cannot be null here.
            OperationRegistry registry =
                (OperationRegistry)hints.get(JAI.KEY_OPERATION_REGISTRY);

            // Create the image using the most preferred RIF for "stream".
            RenderedImage image = RIFRegistry.create(registry, "stream", newArgs, hints);

            return image == null ? null : new StreamImage(image, bass);

        } catch (Exception e) {
            String message = "FileLoadRIF1";
            listener.errorOccurred(message, e, this, false);
//            e.printStackTrace();
            return null;
        }
    }
}