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
    
    public static final Color getUniqueColor(Color oldColor){
    	return getUniqueColor(null,oldColor);
    }
    public static final Color getUniqueColor(byte [] colorTable, Color oldColor){
        if(true){
            // the magical green color that works
            return new Color(0,255,0);
        }
        int numEntries = colorTable.length/3;    
        
        int r = oldColor.getRed();
        int g = oldColor.getGreen();
        int b = oldColor.getBlue();
        int switcher = 1;
        Color uniqueColor = null;
        
        do {
            switch(switcher){
                case 1:
                    r+=50;
                    if(r == 256){
                        r=1;
                    }
                    break;
                case 2:
                    g+=50;
                    if(g == 256){
                        g=1;
                    }
                    break;
                case 3:
                    b+=10;
                    if(b == 256){
                        b=1;
                    }
                    break;                
            }
            uniqueColor = null;
            uniqueColor = new Color(r,g,b);
            switcher++;
            if(switcher > 3){
                switcher=1;
            }
        } while(!isUniqueColor(colorTable,uniqueColor));
        return uniqueColor;        
    }
    
    public static final boolean isUniqueColor(byte []colorTable, Color color){
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int tableLength = colorTable.length/3;
        for (int e = 0; e < tableLength; e++) {
            if((colorTable[3*e] & 0xff) == r &&
                    (colorTable[3*e+1] & 0xff) == g &&
                    (colorTable[3*e+2] & 0xff) == b) {
                return false;
            }
        }
        return true;
    }
}
