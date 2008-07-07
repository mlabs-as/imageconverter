package com.mobiletech.imageconverter.readers;

import java.awt.image.BufferedImage;

import com.mobiletech.imageconverter.exception.ImageConverterException;

public interface DexImageReader {
	public BufferedImage getNext() throws ImageConverterException;
        public String getFormat();
	public boolean hasMore();
	public void dispose();
}
