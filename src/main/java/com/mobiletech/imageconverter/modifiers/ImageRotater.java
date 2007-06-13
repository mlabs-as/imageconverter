package com.mobiletech.imageconverter.modifiers;

import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.JAI;
import javax.media.jai.operator.TransposeDescriptor;

import com.mobiletech.imageconverter.vo.ImageConverterParams;

public class ImageRotater {
	public static BufferedImage rotate(BufferedImage image, ImageConverterParams params) {
		//float angle = (float)Math.toRadians(NewAngleValue);	
		//float centerX = image.getWidth()/2f;
		//float centerY = image.getHeight()/2f;
		
		ParameterBlock pb = new ParameterBlock();
		/*
		pb.addSource(image);
		pb.add(centerX);
		pb.add(centerY);
		pb.add(angle);
		pb.add(new InterpolationBilinear());
		*/		 
		pb.addSource(image);
		if(params.getRotation().getType()== ImageConverterParams.RotationType.CLOCKWISE_90.getType()){
			pb.add(TransposeDescriptor.ROTATE_90);				
		} else if(params.getRotation().getType()== ImageConverterParams.RotationType.ANTI_CLOCKWISE_90.getType()){
			pb.add(TransposeDescriptor.ROTATE_270);
		} else if(params.getRotation().getType()== ImageConverterParams.RotationType.FLIP.getType()){
			pb.add(TransposeDescriptor.ROTATE_180);
		}		
		return JAI.create("transpose", pb).getAsBufferedImage();
		//return JAI.create("rotate",pb).getAsBufferedImage(); 	
	}
}
