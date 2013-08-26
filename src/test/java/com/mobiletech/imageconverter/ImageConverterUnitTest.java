/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mobiletech.imageconverter;

import com.mobiletech.imageconverter.exception.ImageConverterException;
import com.mobiletech.imageconverter.fileio.FileUtil;
import java.awt.Dimension;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author eivinn
 */
public class ImageConverterUnitTest {
 
    public ImageConverterUnitTest(){}
    
    
    @Test
    public void testHydroImage() {
        byte[] imgBytes = FileUtil.readFileAsByte("testdata/IMG_8133_Oyvind_100x150.jpg");
        try {
            assertEquals("JPEG", ImageConverter.getImageFormatName(imgBytes));
            Dimension dim = ImageConverter.getImageDimension(imgBytes);
            assertEquals(150, (int)dim.getHeight());
            assertEquals(100, (int)dim.getWidth());
        } catch (ImageConverterException ex) {
            fail(ex.getMessage());
        }
        
    }
    
    
    @Test
    public void testEdweekImage() {
        byte[] imgBytes = FileUtil.readFileAsByte("testdata/indianatesting_600.jpg");
        try {
            assertEquals("JPEG", ImageConverter.getImageFormatName(imgBytes));
            Dimension dim = ImageConverter.getImageDimension(imgBytes);
            assertEquals(399, (int)dim.getHeight());
            assertEquals(600, (int)dim.getWidth());
        } catch (ImageConverterException ex) {
            fail(ex.getMessage());
        }
        
    }
}
