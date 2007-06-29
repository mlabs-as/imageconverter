package com.mobiletech.imageconverter.modifiers;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.mobiletech.imageconverter.exception.ImageConverterException;
import com.mobiletech.imageconverter.io.DexImageReaderFactory;
import com.mobiletech.imageconverter.readers.DexImageReader;
import com.mobiletech.imageconverter.vo.ImageConverterParams;

public class ImageOverlayWriter {
	public static BufferedImage addOverlay(BufferedImage image, ImageConverterParams params) throws ImageConverterException{
		try {
			DexImageReader reader = DexImageReaderFactory.getImageOverlayReader(params);
			BufferedImage overlay = reader.getNext();
			Graphics2D g2 = null;
			try {
				g2 = image.createGraphics();
				g2.drawImage(overlay, 0, 0, null, null);
			} finally {
				if(g2 != null){
					g2.dispose();
					g2 = null;
				}
			}			
			overlay = null;
		} catch (ImageConverterException e) {
			throw new ImageConverterException(ImageConverterException.Types.EMBEDDED_EXCEPTION,"Got ImageConverterException when adding image overlay: "+e.getMessage(),e);
		}
		return image;
	}
}
