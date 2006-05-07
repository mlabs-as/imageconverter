/*
 * Created on 13.mar.2005
 *
 */
package com.mobiletech.imageconverter.oldtest;
import junit.framework.TestCase;

import java.io.*;
import java.awt.Font;
import java.awt.Color;
import java.io.IOException;
import com.mobiletech.imageconverter.ImageConverter;
import com.mobiletech.imageconverter.exception.ImageConverterException;
import com.mobiletech.imageconverter.fileio.FileUtil;
import com.mobiletech.imageconverter.vo.ImageConverterParams;

/**
 * @author Andreas Ryland
 *
 */
public class ImageConverterTest extends TestCase {
    private String wMarkTekst = "Mobiletech 2005";
    private String wMarkTekstOversize = "Software by Mobiletech 2005 All Rights Reserved";
	private File testImage = null;
	private File testImageGIF = null;
	private File testImageGIFdagbladet = null;
	private File testImagePNG = null;
	private File testImageBMP = null;
    private File testImageBMPRobo = null;
	private File testImageTIF = null;
	private File testMark = null;
	private File testMarkDiagonal = null;
	private Font textMarkFont = null;
	private File testImageWBMP = null;
	private File testImageGIFTRANS = null;
	private File testImageGIFNONTRANS = null;
	private File testImageVG = null;
    private File testImageT610 = null;
    private File testImageT6102 = null;
    private File testGif01 = null;
    private File testGif02 = null;
    private File testGif03 = null;
    private File testGif04 = null;
    private File testGif05 = null;
    private File testGif06 = null;
    private File testGif07 = null;
    private File testGifQ = null;
    private File testJPEGQuality = null;
    private File testPNGTB = null;
    private File testIkkeBilde = null;
    
	private File testAnimatedGIF = null;
    
	private Color textMarkColor = null;
	private boolean writeImages = true;
	
	public void setUp(){
		testImage = new File("test/com/mobiletech/imageconverter/oldtest/testimages/tarnation_-_V_766603a.jpg");
		testImagePNG = new File("test/com/mobiletech/imageconverter/oldtest/testimages/osakafc_png.png");
		testImageBMP = new File("test/com/mobiletech/imageconverter/oldtest/testimages/bmpTest.bmp");
		testImageTIF = new File("test/com/mobiletech/imageconverter/oldtest/testimages/osakafc_tif.tif");
		testImageGIF = new File("test/com/mobiletech/imageconverter/oldtest/testimages/osaka_fc.gif");
		testImageGIFdagbladet = new File("test/com/mobiletech/imageconverter/oldtest/testimages/dagbladet.no.gif");
        testImageGIFTRANS = new File("test/com/mobiletech/imageconverter/oldtest/testimages/transparent.gif");
        testImageBMPRobo = new File("test/com/mobiletech/imageconverter/oldtest/testimages/robopanda.bmp");
		testImageGIFNONTRANS = new File("test/com/mobiletech/imageconverter/oldtest/testimages/nontransparent.gif");
		testImageWBMP = new File("test/com/mobiletech/imageconverter/oldtest/testimages/testImageWBMP.wbmp");
		testMark = new File("test/com/mobiletech/imageconverter/oldtest/testimages/cerezo.gif");
		testMarkDiagonal = new File("test/com/mobiletech/imageconverter/oldtest/testimages/osaka_fc.gif");
		testAnimatedGIF  = new File("test/com/mobiletech/imageconverter/oldtest/testimages/animatedGIF2.gif");
		testImageVG = new File("test/com/mobiletech/imageconverter/oldtest/testimages/vgnlogo.gif");
        testImageT610 = new File("test/com/mobiletech/imageconverter/oldtest/testimages/t610feil1.jpg");
        testImageT6102 = new File("test/com/mobiletech/imageconverter/oldtest/testimages/t610feil2.jpg");
        testGifQ = new File("test/com/mobiletech/imageconverter/oldtest/testimages/animatedGIF3.jpg");
        testGif01 = new File("test/com/mobiletech/imageconverter/oldtest/testimages/gif/gif01.gif");
        testGif02 = new File("test/com/mobiletech/imageconverter/oldtest/testimages/gif/gif02.gif");
        testGif03 = new File("test/com/mobiletech/imageconverter/oldtest/testimages/gif/gif03.gif");
        testGif04 = new File("test/com/mobiletech/imageconverter/oldtest/testimages/gif/gif04.gif");
        testGif05 = new File("test/com/mobiletech/imageconverter/oldtest/testimages/gif/gif05.gif");
        testPNGTB = new File("test/com/mobiletech/imageconverter/oldtest/testimages/tblogo.png");
        testIkkeBilde = new File("test/com/mobiletech/imageconverter/oldtest/testimages/ikkebilde.jpg");
        testJPEGQuality = new File("test/com/mobiletech/imageconverter/oldtest/testimages/jpgKvalitet.jpg");
		textMarkFont = new Font("Arial", Font.PLAIN,  14);
		textMarkColor = Color.BLACK;
	}	
	
	private byte [] runImageTest(ImageConverterParams params,String filename){
		byte [] outputImage = null;
		
		try{		
			outputImage = ImageConverter.convertImage(params);
		}catch(Exception e){
			fail("Exception caught when converting image: " + e);
		}
		if(outputImage.length <= 0){
			fail("Returned ByteArray is empty!");
		}					
		if(writeImages){		    
            FileUtil.writeByteToFile(outputImage,"c:/temp/oldtest/"+filename);
		}
		return outputImage;
	}
    
    private byte [] runImageTestNoCatch(ImageConverterParams params,String filename) throws Exception{
        byte [] outputImage = null;
      
        outputImage = ImageConverter.convertImage(params);
              
        if(writeImages){
            FileUtil.writeByteToFile(outputImage,"c:/temp/oldtest/"+filename);
        }
        return outputImage;
    }
	
	public void testWatermarkTOPLEFT(){	
	    String formatToTest = "jpg";
		byte [] byteArray = null;
		byte [] wMark = null;
		
	    byteArray = FileUtil.readFileAsByte(testImage);
	    wMark = FileUtil.readFileAsByte(testMark);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray);
		params.addImageWatermark(wMark,ImageConverter.WMARK_POS_TOPLEFT,0.3);
		
		runImageTest(params,"testwmarkTOPLEFT." + formatToTest);							
	}
	
	public void testWatermarkTOPRIGHT(){	
	    String formatToTest = "jpg";
		byte [] byteArray = null;
		byte [] wMark = null;
		
	    byteArray = FileUtil.readFileAsByte(testImage);
	    wMark = FileUtil.readFileAsByte(testMark);
        if(byteArray == null){
            fail("Could not read test image");
        }
	
		ImageConverterParams params = new ImageConverterParams(byteArray);
		params.addImageWatermark(wMark,ImageConverter.WMARK_POS_TOPRIGHT,0.3);
		
		runImageTest(params,"testwmarkTOPRIGHT." + formatToTest);						
	}
	
	public void testWatermarkBOTTOMLEFT(){	
	    String formatToTest = "jpg";
		byte [] byteArray = null;
		byte [] wMark = null;
		
	    byteArray = FileUtil.readFileAsByte(testImage);
	    wMark = FileUtil.readFileAsByte(testMark);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray);
		params.addImageWatermark(wMark,ImageConverter.WMARK_POS_BOTTOMLEFT,0.3);
		
		runImageTest(params,"testwmarkBOTTOMLEFT." + formatToTest);						
	}
	
	public void testWatermarkBOTTOMRIGHT(){	
	    String formatToTest = "jpg";
		byte [] byteArray = null;
		byte [] wMark = null;
		
	    byteArray = FileUtil.readFileAsByte(testImage);
	    wMark = FileUtil.readFileAsByte(testMark);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray);
		params.addImageWatermark(wMark,ImageConverter.WMARK_POS_BOTTOMRIGHT,0.3);
		
		runImageTest(params,"testwmarkBOTTOMRIGHT." + formatToTest);						
	}
	
	public void testWatermarkCENTER(){	
	    String formatToTest = "jpg";
		byte [] byteArray = null;
		byte [] wMark = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImage);
	    wMark = FileUtil.readFileAsByte(testMark);
        
		if(byteArray == null || wMark == null){
			fail("Could not read test image");
		}
		
		ImageConverterParams params = new ImageConverterParams(byteArray);
		params.addImageWatermark(wMark,ImageConverter.WMARK_POS_CENTER,0.3);
		
		runImageTest(params,"testwmarkCENTER." + formatToTest);						
	}
	
	public void testWatermarkDIAGONAL(){	
	    String formatToTest = "jpg";
		byte [] byteArray = null;
		byte [] wMark = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImage);
	    wMark = FileUtil.readFileAsByte(testMarkDiagonal);
        if(byteArray == null || wMark == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray);
		params.addImageWatermark(wMark,ImageConverter.WMARK_POS_DIAGONAL_CENTER,0.8);
		
		runImageTest(params,"testwmarkDIAGONAL." + formatToTest);						
	}
	
	public void testWatermarkDIAGONALOPAQUE(){	
	    String formatToTest = "jpg";
		byte [] byteArray = null;
		byte [] wMark = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImage);
	    wMark = FileUtil.readFileAsByte(testMarkDiagonal);
        if(byteArray == null || wMark == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray);
		params.addImageWatermark(wMark,ImageConverter.WMARK_POS_DIAGONAL_CENTER,0.8,0.1f);
		
		runImageTest(params,"testwmarkDIAGONALOPAQUE." + formatToTest);						
	}
	
	public void testWatermarkCENTERFIXEDSIZE(){	
	    String formatToTest = "jpg";
		byte [] byteArray = null;
		byte [] wMark = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImage);
	    wMark = FileUtil.readFileAsByte(testMark);
        if(byteArray == null || wMark == null){
            fail("Could not read test image");
        }
            
		ImageConverterParams params = new ImageConverterParams(byteArray);
		params.addImageWatermark(wMark,ImageConverter.WMARK_POS_CENTER,60,60);
		
		runImageTest(params,"testwmarkCENTERFIXEDSIZE." + formatToTest);						
	}
	
	public void testWatermarkCENTERFIXEDSIZEOPAQUE(){	
	    String formatToTest = "jpg";
		byte [] byteArray = null;
		byte [] wMark = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImage);
	    wMark = FileUtil.readFileAsByte(testMark);
        if(byteArray == null || wMark == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray);
		params.addImageWatermark(wMark,ImageConverter.WMARK_POS_CENTER,60,60,0.6f);
		
		runImageTest(params,"testwmarkCENTERFIXEDSIZEOPAQUE." + formatToTest);						
	}		

	public void testWatermarkMULTIPLE(){	
	    String formatToTest = "jpg";
		byte [] byteArray = null;
		byte [] wMark = null;
		byte [] wMarkD = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImage);
	    wMark = FileUtil.readFileAsByte(testMark);
	    wMarkD = FileUtil.readFileAsByte(testMarkDiagonal);
        if(byteArray == null || wMark == null || wMarkD == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray);
		params.addImageWatermark(wMark,ImageConverter.WMARK_POS_TOPLEFT,0.2,0.3f);
		params.addImageWatermark(wMark,ImageConverter.WMARK_POS_TOPRIGHT,0.2,0.5f);
		params.addImageWatermark(wMark,ImageConverter.WMARK_POS_BOTTOMLEFT,0.2,0.7f);
		params.addImageWatermark(wMark,ImageConverter.WMARK_POS_BOTTOMRIGHT,0.2,0.9f);
		params.addImageWatermark(wMarkD,ImageConverter.WMARK_POS_DIAGONAL_CENTER,0.5);
		
		runImageTest(params,"testwmarkMULTIPLE_ALLCORNERS_DIAGONAL." + formatToTest);						
	}	
	
	public void testWatermarkTextTOPLEFT(){	
	    String formatToTest = "jpg";
		byte [] byteArray = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImage);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray);
		params.addTextWatermark(wMarkTekst,ImageConverter.WMARK_POS_TOPLEFT,textMarkFont,textMarkColor);
		
		runImageTest(params,"testwmarkTextTOPLEFT." + formatToTest);						
	}	
	
	public void testWatermarkTextTOPRIGHT(){	
	    String formatToTest = "jpg";
		byte [] byteArray = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImage);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray);
		params.addTextWatermark(wMarkTekst,ImageConverter.WMARK_POS_TOPRIGHT,textMarkFont,textMarkColor);
		
		runImageTest(params,"testwmarkTextTOPRIGHT." + formatToTest);						
	}	
	
	public void testWatermarkTextBOTTOMLEFT(){	
	    String formatToTest = "jpg";
		byte [] byteArray = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImage);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray);
		params.addTextWatermark(wMarkTekst,ImageConverter.WMARK_POS_BOTTOMLEFT,textMarkFont,textMarkColor);
		
		runImageTest(params,"testwmarkTextBOTTOMLEFT." + formatToTest);						
	}
	
	public void testWatermarkTextBOTTOMRIGHT(){	
	    String formatToTest = "jpg";
		byte [] byteArray = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImage);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray);
		params.addTextWatermark(wMarkTekst,ImageConverter.WMARK_POS_BOTTOMRIGHT,textMarkFont,textMarkColor);
		
		runImageTest(params,"testwmarkTextBOTTOMRIGHT." + formatToTest);						
	}
	
	public void testWatermarkTextCENTER(){	
	    String formatToTest = "jpg";
		byte [] byteArray = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImage);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray);
		params.addTextWatermark(wMarkTekst,ImageConverter.WMARK_POS_CENTER,textMarkFont,textMarkColor);
		
		runImageTest(params,"testwmarkTextCENTER." + formatToTest);						
	}
    
    public void testWatermarkTextCENTERBIG(){  
        String formatToTest = "jpg";
        byte [] byteArray = null;
        
        
        byteArray = FileUtil.readFileAsByte(testImage);
        if(byteArray == null){
            fail("Could not read test image");
        }
        
        ImageConverterParams params = new ImageConverterParams(byteArray);
        params.setHeight(150);
        params.setWidth(150);
        params.addTextWatermark("24",ImageConverter.WMARK_POS_CENTER,new Font("Arial", Font.PLAIN, 70),textMarkColor);
        
        runImageTest(params,"testwmarkTextCENTERBIG." + formatToTest);                     
    }
	
	public void testWatermarkTextDIAGONAL(){	
	    String formatToTest = "jpg";
		byte [] byteArray = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImage);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray);
		params.addTextWatermark(wMarkTekst,ImageConverter.WMARK_POS_DIAGONAL_CENTER,textMarkFont,textMarkColor);
		
		runImageTest(params,"testwmarkTextDIAGONAL." + formatToTest);						
	}	
	
	public void testWatermarkTextMULTIPLE(){	
	    String formatToTest = "jpg";
		byte [] byteArray = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImage);
        if(byteArray == null){
            fail("Could not read test image");
        }
	
		ImageConverterParams params = new ImageConverterParams(byteArray);
		params.addTextWatermark(wMarkTekst,ImageConverter.WMARK_POS_TOPLEFT,textMarkFont,textMarkColor);
		params.addTextWatermark(wMarkTekst,ImageConverter.WMARK_POS_TOPRIGHT,textMarkFont,textMarkColor);
		params.addTextWatermark(wMarkTekst,ImageConverter.WMARK_POS_BOTTOMLEFT,textMarkFont,textMarkColor);
		params.addTextWatermark(wMarkTekst,ImageConverter.WMARK_POS_BOTTOMRIGHT,textMarkFont,textMarkColor);
		params.addTextWatermark(wMarkTekst,ImageConverter.WMARK_POS_DIAGONAL_CENTER,textMarkFont,textMarkColor);
		
		runImageTest(params,"testwmarkTextMULTIPLE." + formatToTest);						
	}	

	public void testWatermarkTextANDimageMULTIPLE(){	
	    String formatToTest = "jpg";
		byte [] byteArray = null;
		byte [] wMark = null;
		byte [] wMarkD = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImage);
	    wMark = FileUtil.readFileAsByte(testMark);
	    wMarkD = FileUtil.readFileAsByte(testMarkDiagonal);
        if(byteArray == null || wMark == null || wMarkD == null){
            fail("Could not read test image");
        }
	
		ImageConverterParams params = new ImageConverterParams(byteArray);
		params.addTextWatermark(wMarkTekst,ImageConverter.WMARK_POS_TOPRIGHT,textMarkFont,textMarkColor);
		params.addTextWatermark(wMarkTekst,ImageConverter.WMARK_POS_BOTTOMLEFT,textMarkFont,textMarkColor);
		params.addImageWatermark(wMark,ImageConverter.WMARK_POS_TOPLEFT,0.2,0.3f);		
		params.addImageWatermark(wMark,ImageConverter.WMARK_POS_BOTTOMRIGHT,0.2,0.9f);
		params.addImageWatermark(wMarkD,ImageConverter.WMARK_POS_DIAGONAL_CENTER,0.5);		
		
		runImageTest(params,"testWatermarkTextANDimageMULTIPLE." + formatToTest);						
	}		
	
	public void testWatermarkTextBOTTOMRIGHTRESIZED(){	
	    String formatToTest = "jpg";
		byte [] byteArray = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImage);
        if(byteArray == null){
            fail("Could not read test image");
        }
            
		ImageConverterParams params = new ImageConverterParams(byteArray);
		params.addTextWatermark(wMarkTekstOversize,ImageConverter.WMARK_POS_BOTTOMRIGHT,new Font("Arial", Font.PLAIN,  40),textMarkColor);
		
		runImageTest(params,"testwmarkTextBOTTOMRIGHTRESIZED." + formatToTest);						
	}
	
	public void testWatermarkTextDIAGONALRESIZED(){	
	    String formatToTest = "jpg";
		byte [] byteArray = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImage);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray);
		params.addTextWatermark(wMarkTekstOversize+"bla bla jalla hurre for syttene mai",ImageConverter.WMARK_POS_DIAGONAL_CENTER,new Font("Arial", Font.PLAIN,  40),textMarkColor);
		
		runImageTest(params,"testwmarkTextDIAGONALRESIZED." + formatToTest);						
	}	
	
	public void testConvertAndResizeToGIFfromJPEG(){	
	    String formatToTest = "gif";
		byte [] byteArray = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImage);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,100,100);
		
		runImageTest(params,"convert_gif_from_jpeg." + formatToTest);						
	}
	
	public void testConvertAndResizeToBMPfromJPEG(){	
	    String formatToTest = "bmp";
		byte [] byteArray = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImage);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,100,100);
		
		runImageTest(params,"convert_bmp_from_jpeg." + formatToTest);						
	}
	
	public void testConvertAndResizeToPNGfromJPEG(){	
	    String formatToTest = "png";
		byte [] byteArray = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImage);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,100,100);
		
		runImageTest(params,"convert_png_from_jpeg." + formatToTest);						
	}
	
	public void testConvertAndResizeToWBMPfromJPEG(){	
	    String formatToTest = "wbmp";
		byte [] byteArray = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImage);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,100,100);
		
		runImageTest(params,"convert_wbmp_from_jpeg." + formatToTest);						
	}
	
	public void testConvertAndResizeToTIFfromJPEG(){	
	    String formatToTest = "tif";
		byte [] byteArray = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImage);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,100,100);
		
		runImageTest(params,"convert_tif_from_jpeg." + formatToTest);						
	}	
	
	public void testConvertAndResizeToJPEGfromJPEG(){	
	    String formatToTest = "jpg";
		byte [] byteArray = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImage);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,100,100);
		
		runImageTest(params,"convert_jpg_from_jpeg." + formatToTest);						
	}	
	
	public void testResizeJPEG(){	
	    String formatToTest = "jpg";
		byte [] byteArray = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImage);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray,null,100,100);
		
		runImageTest(params,"testresize_jpeg." + formatToTest);						
	}
    
    public void testResizeJPEGQuality(){   
        String formatToTest = "jpg";
        byte [] byteArray = null;
        
        
        byteArray = FileUtil.readFileAsByte(testJPEGQuality);
        if(byteArray == null){
            fail("Could not read test image");
        }
        
        ImageConverterParams params = new ImageConverterParams(byteArray,null,110,100);
        
        runImageTest(params,"testResizeJPEGQuality." + formatToTest);                     
    }
	
	public void testChangeJPEGQuality(){	
	    String formatToTest = "jpg";
		byte [] byteArray = null;
		byte [] outputImage = null;
		
		
		
	    byteArray = FileUtil.readFileAsByte(testImage);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray);
		params.setJPEGCompressionQuality(0.1f);
		
		outputImage = runImageTest(params,"test_jpeg_qualitychange." + formatToTest);
		
		if(outputImage.hashCode() == byteArray.hashCode()){
			fail("input and output byteArray is the same, quality not changed");
		}
	}	
	
	public void testConvertAndResizeToJPEGfromGIF(){	
	    String formatToTest = "jpg";
		byte [] byteArray = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImageGIF);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,100,100);
		
		runImageTest(params,"convert_jpeg_from_gif." + formatToTest);						
	}	
	
	public void testConvertAndResizeToJPEGfromGIFDagbladet(){	
	    String formatToTest = "jpg";
		byte [] byteArray = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImageGIFdagbladet);
        if(byteArray == null){
            fail("Could not read test image");
        }
	
		ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,100,100);
		
		runImageTest(params,"convert_jpeg_from_gif_dagbladet." + formatToTest);						
	}	
	
	public void testConvertAndResizeToGIFfromGIFDagbladet(){	
	    String formatToTest = "gif";
		byte [] byteArray = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImageGIFdagbladet);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,100,100);
		
		runImageTest(params,"convert_gif_from_gif_dagbladet." + formatToTest);						
	}	
	
	public void testConvertAndResizeToJPEGfromTransGIF(){	
	    String formatToTest = "jpg";
		byte [] byteArray = null;
		
	    byteArray = FileUtil.readFileAsByte(testImageGIFTRANS);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,100,100);
		
		runImageTest(params,"convert_jpeg_from_Trans_gif." + formatToTest);						
	}		
	
	public void testConvertAndResizeToJPEGfromNonTransGIF(){	
	    String formatToTest = "jpg";
		byte [] byteArray = null;
		
	    byteArray = FileUtil.readFileAsByte(testImageGIFNONTRANS);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,100,100);
		
		runImageTest(params,"convert_jpeg_from_NonTrans_gif." + formatToTest);						
	}	
	
	public void testResizeGIFfromTransGIF(){	
	    String formatToTest = "gif";
		byte [] byteArray = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImageGIFTRANS);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,100,100);
		
		runImageTest(params,"convert_gif_from_Trans_gif." + formatToTest);						
	}		

	public void testConvertAndResizeToBMPfromTransGIF(){	
	    String formatToTest = "BMP";
		byte [] byteArray = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImageGIFTRANS);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,100,100);
		
		runImageTest(params,"convert_bmp_from_Trans_gif." + formatToTest);						
	}	

	public void testConvertAndResizeToWBMPfromBMP(){	
	    String formatToTest = "wbmp";
		byte [] byteArray = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImageBMP);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,200,200);
		
		runImageTest(params,"testConvertAndResizeToWBMPfromBMP." + formatToTest);						
	}
	
	public void testResizeWBMP(){	
	    String formatToTest = "wbmp";
		byte [] byteArray = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImageWBMP);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray);
		params.setHeight(200);
		params.setWidth(200);
		
		runImageTest(params,"testResizeWBMP." + formatToTest);						
	}
	
	public void testConvertAndResizeToWBMPfromJPG(){	
	    String formatToTest = "wbmp";
		byte [] byteArray = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImage);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,200,200);
		
		runImageTest(params,"testConvertAndResizeToWBMPfromJPG." + formatToTest);						
	}
	
	public void testConvertAndResizeToWBMPfromGIF(){	
	    String formatToTest = "wbmp";
		byte [] byteArray = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImageGIF);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,200,200);
		
		runImageTest(params,"testConvertAndResizeToWBMPfromGIF." + formatToTest);						
	}
	
	public void testConvertAndResizeToWBMPfromPNG(){	
	    String formatToTest = "wbmp";
		byte [] byteArray = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImagePNG);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,200,200);
		
		runImageTest(params,"testConvertAndResizeToWBMPfromPNG." + formatToTest);						
	}
	
	public void testConvertAndResizeToWBMPfromTIF(){	
	    String formatToTest = "wbmp";
		byte [] byteArray = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImageTIF);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,200,200);
		
		runImageTest(params,"testConvertAndResizeToWBMPfromTIF." + formatToTest);						
	}	
	
	public void testConvertAndResizeToJPGfromPNG(){	
	    String formatToTest = "jpg";
		byte [] byteArray = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImagePNG);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,200,200);
		
		runImageTest(params,"convert_jpg_from_png." + formatToTest);						
	}	
	
	public void testConvertAndResizeToJPGfromTIF(){	
	    String formatToTest = "jpg";
		byte [] byteArray = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImageTIF);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,200,200);
		
		runImageTest(params,"convert_jpg_from_tif." + formatToTest);						
	}
	
	public void testResizeDownAnimatedGIF(){	
	    String formatToTest = "gif";
		byte [] byteArray = null;
	//	byte [] wMarkD = null;
		
		
//	    wMarkD = FileUtil.readFileAsByte(testMarkDiagonal);
	    byteArray = FileUtil.readFileAsByte(testAnimatedGIF);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,80,80);
		
		runImageTest(params,"testResizeDownAnimatedGIF." + formatToTest);						
	}	
	
	public void testResizeAndGrayscaleAnimatedGIF(){	
	    String formatToTest = "gif";
		byte [] byteArray = null;
//		byte [] wMarkD = null;
		
		
//	    wMarkD = FileUtil.readFileAsByte(testMarkDiagonal);
	    byteArray = FileUtil.readFileAsByte(testAnimatedGIF);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,80,80);
		params.setGrayscale(true);	
		
		runImageTest(params,"testResizeAndGrayscaleAnimatedGIF." + formatToTest);						
	}	
	
	public void testResizeAnimatedGIF(){	
	    String formatToTest = "gif";
		byte [] byteArray = null;
		byte [] wMarkD = null;
	
	    wMarkD = FileUtil.readFileAsByte(testMarkDiagonal);
	    byteArray = FileUtil.readFileAsByte(testAnimatedGIF);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,400,400);
		params.addImageWatermark(wMarkD,ImageConverter.WMARK_POS_DIAGONAL_CENTER,0.7,0.5f);	
		params.addTextWatermark(wMarkTekst,ImageConverter.WMARK_POS_BOTTOMRIGHT,textMarkFont,textMarkColor);
		runImageTest(params,"testResizeAnimatedGIF." + formatToTest);						
	}		
	
	public void testPreventionOfEnlargement(){	
	    String formatToTest = "jpg";
		byte [] byteArray = null;
		
	    byteArray = FileUtil.readFileAsByte(testImagePNG);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,2000,2000,true);
		
		runImageTest(params,"testPreventionOfEnlargement." + formatToTest);						
	}	
	
	public void testNoChange(){	
	    String formatToTest = "jpg";
		byte [] byteArray = null;
		byte [] returnedByteArray = null;
		
	    byteArray = FileUtil.readFileAsByte(testImage);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		
		
		ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest);
		
		params.setJPEGCompressionQuality(0);
		
		returnedByteArray = runImageTest(params,"testNoChange." + formatToTest);
		
		assertEquals("Image has been changed!",byteArray.hashCode(),returnedByteArray.hashCode());
	}
	
	public void testNoChange2(){	
		byte [] byteArray = null;
		byte [] returnedByteArray = null;
		
	
	    byteArray = FileUtil.readFileAsByte(testImage);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray);
		
		returnedByteArray = runImageTest(params,"testNoChange2.jpg");
		
		assertEquals("Image has been changed!",byteArray.hashCode(),returnedByteArray.hashCode());
	}
	
	public void testNoChange3(){	
	    String formatToTest = "gif";
		byte [] byteArray = null;
		byte [] returnedByteArray = null;
		
		
	    byteArray = FileUtil.readFileAsByte(testImageGIF);
        if(byteArray == null){
            fail("Could not read test image");
        }
		
		ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest);
		
		returnedByteArray = runImageTest(params,"testNoChange3." + formatToTest);
		
		assertEquals("Image has been changed!",byteArray.hashCode(),returnedByteArray.hashCode());
	}	
	
    public void testSET65Error(){    
        byte [] byteArray = null;
        byte [] returnedByteArray = null;
        
        byteArray = FileUtil.readFileAsByte(this.testImageT610);
        if(byteArray == null){
            fail("Could not read test image");
        }
        
        ImageConverterParams params = new ImageConverterParams(byteArray);
        params.setWidth(126);
        params.setHeight(540);
        returnedByteArray = runImageTest(params,"testT6102.jpg");
        
        //assertEquals("Image has been changed!",byteArray.hashCode(),returnedByteArray.hashCode());
    }
    
    public void testGIFTest01(){    
        String formatToTest = "gif";
        byte [] byteArray = null;
        byte [] wMarkD = null;
        
        byteArray = FileUtil.readFileAsByte(testGif01);
        if(byteArray == null){
            fail("Could not read test image");
        }
        
        ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,300,300);
        params.setNoEnlargement(true);
        //params.addImageWatermark(wMarkD,ImageConverter.WMARK_POS_DIAGONAL_CENTER,0.7,0.5f);   
        //params.addTextWatermark(wMarkTekst,ImageConverter.WMARK_POS_BOTTOMRIGHT,textMarkFont,textMarkColor);
        runImageTest(params,"testGif01." + formatToTest);                       
    }
    
    public void testGIF_to_BW_Test01(){    
        String formatToTest = "gif";
        byte [] byteArray = null;
        
        byteArray = FileUtil.readFileAsByte(testGif01);
        if(byteArray == null){
            fail("Could not read test image");
        }
        
        ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,300,300);
        params.setGrayscale(true);
        params.setNoEnlargement(true);
        runImageTest(params,"testGif01BW." + formatToTest);                       
    }
    
    public void testGIFTest02(){    
        String formatToTest = "gif";
        byte [] byteArray = null;
        byte [] wMarkD = null;
        
        byteArray = FileUtil.readFileAsByte(testGif02);
        if(byteArray == null){
            fail("Could not read test image");
        }
        
        ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,300,300);
        params.setNoEnlargement(true);
        //params.addImageWatermark(wMarkD,ImageConverter.WMARK_POS_DIAGONAL_CENTER,0.7,0.5f);   
        //params.addTextWatermark(wMarkTekst,ImageConverter.WMARK_POS_BOTTOMRIGHT,textMarkFont,textMarkColor);
        runImageTest(params,"testGif02." + formatToTest);                       
    }
    
    public void testGIF_to_BW_Test02(){    
        String formatToTest = "gif";
        byte [] byteArray = null;
        
        byteArray = FileUtil.readFileAsByte(testGif02);
        if(byteArray == null){
            fail("Could not read test image");
        }
        
        ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,300,300);
        params.setGrayscale(true);
        params.setNoEnlargement(true);
        runImageTest(params,"testGif02BW." + formatToTest);                       
    }
    
    public void testGIFTest03(){    
        String formatToTest = "gif";
        byte [] byteArray = null;
        byte [] wMarkD = null;
        
        byteArray = FileUtil.readFileAsByte(testGif03);
        if(byteArray == null){
            fail("Could not read test image");
        }
        ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,300,300);
        params.setNoEnlargement(true);
        //params.addImageWatermark(wMarkD,ImageConverter.WMARK_POS_DIAGONAL_CENTER,0.7,0.5f);   
        //params.addTextWatermark(wMarkTekst,ImageConverter.WMARK_POS_BOTTOMRIGHT,textMarkFont,textMarkColor);
        runImageTest(params,"testGif03." + formatToTest);                       
    }
    
    public void testGIF_to_BW_Test03(){    
        String formatToTest = "gif";
        byte [] byteArray = null;
        
        byteArray = FileUtil.readFileAsByte(testGif03);
        if(byteArray == null){
            fail("Could not read test image");
        }
        
        ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,300,300);
        params.setGrayscale(true);
        params.setNoEnlargement(true);
        runImageTest(params,"testGif03BW." + formatToTest);                       
    }
    
    public void testGIFTest04(){    
        String formatToTest = "gif";
        byte [] byteArray = null;
        byte [] wMarkD = null;
        
        byteArray = FileUtil.readFileAsByte(testGif04);
        if(byteArray == null){
            fail("Could not read test image");
        }
        
        ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,300,300);
        params.setNoEnlargement(true);
        //params.addImageWatermark(wMarkD,ImageConverter.WMARK_POS_DIAGONAL_CENTER,0.7,0.5f);   
        //params.addTextWatermark(wMarkTekst,ImageConverter.WMARK_POS_BOTTOMRIGHT,textMarkFont,textMarkColor);
        runImageTest(params,"testGif04." + formatToTest);                       
    }
    
    public void testGIF_to_BW_Test04(){    
        String formatToTest = "gif";
        byte [] byteArray = null;
        
        byteArray = FileUtil.readFileAsByte(testGif04);
        if(byteArray == null){
            fail("Could not read test image");
        }
        
        ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,300,300);
        params.setGrayscale(true);
        params.setNoEnlargement(true);
        runImageTest(params,"testGif04BW." + formatToTest);                       
    }
    
    public void testGIFTest05(){    
        String formatToTest = "gif";
        byte [] byteArray = null;
        byte [] wMarkD = null;
        
        byteArray = FileUtil.readFileAsByte(testGif05);
        if(byteArray == null){
            fail("Could not read test image");
        }
        
        ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,300,300);
        params.setNoEnlargement(false);
        //params.addImageWatermark(wMarkD,ImageConverter.WMARK_POS_DIAGONAL_CENTER,0.7,0.5f);   
        //params.addTextWatermark(wMarkTekst,ImageConverter.WMARK_POS_BOTTOMRIGHT,textMarkFont,textMarkColor);
        runImageTest(params,"testGif05." + formatToTest);                       
    }
    
    public void testGIF_to_BW_Test05(){    
        String formatToTest = "gif";
        byte [] byteArray = null;
        
        byteArray = FileUtil.readFileAsByte(testGif05);
        if(byteArray == null){
            fail("Could not read test image");
        }
        
        ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,300,300);
        params.setGrayscale(true);
        params.setNoEnlargement(false);
        runImageTest(params,"testGif05BW." + formatToTest);                       
    }
    
    public void testGIFfromJPEGqualityTest(){    
        String formatToTest = "gif";
        byte [] byteArray = null;
        
        byteArray = FileUtil.readFileAsByte(testGifQ);
        if(byteArray == null){
            fail("Could not read test image");
        }
        
        ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,400,400);
        params.setGrayscale(false);
        params.setNoEnlargement(false);
        runImageTest(params,"giffromjpegqtest." + formatToTest);                       
    }
    
    public void testPNGresizeTB(){    
        String formatToTest = "png";
        byte [] byteArray = null;
        
        byteArray = FileUtil.readFileAsByte(testPNGTB);
        if(byteArray == null){
            fail("Could not read test image");
        }
        
        ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,160,160);
        params.setGrayscale(false);
        params.setNoEnlargement(false);
        runImageTest(params,"testPNGresizeTB." + formatToTest);                       
    }
    
    public void testPNGtoGIFresizeTB(){    
        String formatToTest = "gif";
        byte [] byteArray = null;
        
        byteArray = FileUtil.readFileAsByte(testPNGTB);
        if(byteArray == null){
            fail("Could not read test image");
        }
        
        ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,160,160);
        params.setGrayscale(false);
        params.setNoEnlargement(false);
        runImageTest(params,"testPNGtoGIFresizeTB." + formatToTest);                       
    }
    
    public void testConvertAndResizeToGIFfromBMP(){    
        String formatToTest = "gif";
        byte [] byteArray = null;
    
        byteArray = FileUtil.readFileAsByte(testImageBMPRobo);
        if(byteArray == null){
            fail("Could not read test image");
        }
        
        ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,400,400);
        
        runImageTest(params,"testConvertAndResizeToGIFfromBMP." + formatToTest);                       
    }
    
    public void testConvertAndResizeBMP(){    
        String formatToTest = "bmp";
        byte [] byteArray = null;
        
        byteArray = FileUtil.readFileAsByte(testImageBMPRobo);
        if(byteArray == null){
            fail("Could not read test image");
        }
        
        ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,450,450);
        
        runImageTest(params,"testConvertAndResizeBMP." + formatToTest);                       
    }
    
    public void testConvertAndResizeToPNGfromBMP(){    
        String formatToTest = "png";
        byte [] byteArray = null;
        
        byteArray = FileUtil.readFileAsByte(testImageBMPRobo);
        if(byteArray == null){
            fail("Could not read test image");
        }
        
        ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,400,400);
        
        runImageTest(params,"testConvertAndResizeToPNGfromBMP." + formatToTest);                       
    }
    
    public void testConvertAndResizeToJPGfromBMP(){    
        String formatToTest = "jpg";
        byte [] byteArray = null;
        
        byteArray = FileUtil.readFileAsByte(testImageBMPRobo);
        if(byteArray == null){
            fail("Could not read test image");
        }
        
        ImageConverterParams params = new ImageConverterParams(byteArray,formatToTest,400,400);
        
        runImageTest(params,"testConvertAndResizeToJPGfromBMP." + formatToTest);                       
    }
    
    public void stresTest(){    
        ImageConverterParams [] params = new ImageConverterParams[2];
        params[0] = new ImageConverterParams(null,150,150);        
        params[1] = new ImageConverterParams(null,500,500);           

        //String dir = "C:/Documents and Settings/Andreas/Desktop/TestSite/For 903/wallpapers/people";
        String dir = "C:/Documents and Settings/Andreas/Desktop/TestSite/Problem Images";
        File directory = new File(dir);
        prosessDirectory(directory,dir, params);        
    }
    
    public void prosessDirectory(File directory,String dir,ImageConverterParams [] params){
        String [] files = directory.list();
        File fil = null;
        for(int i = 0;i < files.length;i++){
            try {                
                fil = new File(directory+"/"+files[i]);
                if(fil.isDirectory()){
                    prosessDirectory(fil,dir, params);
                } else {
                    for(int e = 0;e < params.length; e++){
                        params[e].setImage(FileUtil.readFileAsByte(fil));
                        runImageTestNoCatch(params[e],"RUN" + e + fil.getName());
                    }       
                }
            } catch (ImageConverterException ic){
                if(ic.getErrorCode() != ImageConverterException.Types.READ_CODEC_NOT_FOUND){
                    System.out.println("Got ImageConverterException " +fil.getAbsolutePath()+fil.getName() + ic);
                }
            } catch (Exception e) {
                System.out.println("Got exception "+e.getClass().getName() + " " +fil.getAbsolutePath()+fil.getName() + e);
            }
        }
    }
    
    
}
