package com.mobiletech.imageconverter.io;

import java.awt.image.BufferedImage;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;

import com.mobiletech.imageconverter.exception.ImageConverterException;
import com.mobiletech.imageconverter.vo.ImageConverterParams;
import com.mobiletech.imageconverter.writers.AnimGifWriter;
import com.mobiletech.imageconverter.writers.DexImageWriter;
import com.mobiletech.imageconverter.writers.GeneralImageIOWriter;
import com.mobiletech.imageconverter.writers.JPEGImageWriter;
import com.mobiletech.imageconverter.writers.OptimizingAnimGifWriter;

public class DexImageWriterFactory {
	
	public static DexImageWriter getImageWriter(BufferedImage image, ImageConverterParams imageParams) throws ImageConverterException{
		DexImageWriter writer = null;
		if(imageParams.getFormat().compareToIgnoreCase("jpg")==0 || imageParams.getFormat().compareToIgnoreCase("jpeg")==0){
			writer = new JPEGImageWriter(imageParams);
		} else {
			Iterator writers = ImageIO.getImageWriters(new ImageTypeSpecifier(image),imageParams.getFormat());       
			if(!writers.hasNext()){
				writers = ImageIO.getImageWritersBySuffix(imageParams.getFormat());
			}
	        if(!writers.hasNext()){
	            throw new ImageConverterException(ImageConverterException.Types.CODEC_NOT_FOUND,"No codec found for format name: " + imageParams.getFormat(),null);
	        }
	        ImageWriter iwriter = (ImageWriter)writers.next();
	        if(imageParams.getFormat().equalsIgnoreCase("gif") && imageParams.getInternalVariables().getImageMetadata() != null && imageParams.getInternalVariables().getImageMetadata().length > 1){
	        	if(imageParams.isFastMode() || imageParams.getInternalVariables().getGifNumColors() == 0){
	        		writer = new AnimGifWriter(iwriter, imageParams);
	        	} else {
	        		writer = new OptimizingAnimGifWriter(iwriter, imageParams);
	        	}
	        } else {
	        	writer = new GeneralImageIOWriter(iwriter);
	        }
		}       
		return writer;
	}
}