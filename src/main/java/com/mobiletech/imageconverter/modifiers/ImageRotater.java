package com.mobiletech.imageconverter.modifiers;

import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;

public class ImageRotater {
	public static BufferedImage rotate(BufferedImage image, int NewAngleValue) {
		float angle = (float)Math.toRadians(NewAngleValue);	
		float centerX = image.getWidth()/2f;
		float centerY = image.getHeight()/2f;
		
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(image);
		pb.add(centerX);
		pb.add(centerY);
		pb.add(angle);
		pb.add(new InterpolationNearest());
		
		return JAI.create("rotate",pb).getAsBufferedImage(); 	
	}
}
