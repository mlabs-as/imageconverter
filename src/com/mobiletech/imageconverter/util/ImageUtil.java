package com.mobiletech.imageconverter.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.mobiletech.imageconverter.vo.ImageConverterParams;

public class ImageUtil {
    
    public static final BufferedImage toBuffImageRGBorARGB(BufferedImage source) {
        if (source.getType() != BufferedImage.TYPE_INT_RGB && source.getType() != BufferedImage.TYPE_INT_ARGB) {
            int type = BufferedImage.TYPE_INT_RGB;
            if (source.getColorModel().hasAlpha())
                type = BufferedImage.TYPE_INT_ARGB;
            BufferedImage bi = new BufferedImage(source.getWidth(), source.getHeight(), type);
            Graphics2D graphics2D = null;
            try {
                graphics2D = bi.createGraphics();
                graphics2D.drawImage(source, null, 0, 0);
            } finally {
                if (graphics2D != null)
                    graphics2D.dispose();
            }
            return bi;
        }
        return source;
    }
}
