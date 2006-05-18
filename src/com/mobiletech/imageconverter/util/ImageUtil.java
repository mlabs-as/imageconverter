package com.mobiletech.imageconverter.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

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
    
    public static final Color getUniqueColor(byte [] colorTable, Color oldColor){
        int numEntries = colorTable.length/3;    
        
        int r = oldColor.getRed();
        int g = oldColor.getGreen();
        int b = oldColor.getBlue();
        /*
        for (int e = 0; e < numEntries; e++) {
            if((colorTable[3*e] & 0xff) == r &&
                    (colorTable[3*e+1] & 0xff) == g &&
                    (colorTable[3*e+2] & 0xff) == b) {
                     
            }
        }*/
        Color uniqueColor = new Color(r+1,g+1,b+1);
        return uniqueColor;
    }
}
