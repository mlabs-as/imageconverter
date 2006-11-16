package com.mobiletech.imageconverter.writers;

import java.awt.image.BufferedImage;

import com.mobiletech.imageconverter.exception.ImageConverterException;

public interface DexImageWriter {
	public byte[] getByte() throws ImageConverterException;
	public void dispose();
	public void writeNext(BufferedImage image) throws ImageConverterException;
	public boolean canWriteMore();
}
