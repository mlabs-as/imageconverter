package com.mobiletech.imageconverter.modifiers;

import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

public class ImageCropper {
	public static BufferedImage cropImageByPercentage(BufferedImage image, int top, int bottom, int left, int right){
		double w = image.getWidth();
		double h = image.getHeight();
		double x = 0, y = 0, width = w, height = h;
		if(top > 0){		
			y = (h*(top/100.0));
			height -= y;
		}
		if(left > 0){		
			x = (w*(left/100.0));
			width -= x;
		}
		if(right > 0){		
			width -= (width*(right/100.0));
		}
		if(bottom > 0){		
			height -= (height*(bottom/100.0));
		}	
		return cropImage(image,(int)x,(int)y,(int)width,(int)height);
	}
	public static BufferedImage cropImage(BufferedImage image, int x, int y, int width, int height){
		float ix = Float.parseFloat(""+x);
		float iy = Float.parseFloat(""+y);
		float iwidth = Float.parseFloat(""+width);
		float iheight = Float.parseFloat(""+height);
//		 Read the image.		
//		 Create a ParameterBlock with information for the cropping.
		ParameterBlock pb = new ParameterBlock();
		PlanarImage surrogateImage = PlanarImage.wrapRenderedImage(image);
		pb.addSource(surrogateImage);
		pb.add(ix);
		pb.add(iy);
		pb.add(iwidth);
		pb.add(iheight);
//		 Create the output image by cropping the input image.
		return JAI.create("crop",pb,null).getAsBufferedImage();
	}
}
