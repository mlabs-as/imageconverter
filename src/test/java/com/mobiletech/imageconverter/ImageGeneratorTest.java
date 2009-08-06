package com.mobiletech.imageconverter;


import com.mobiletech.imageconverter.fileio.DirectoryUtil;

import com.mobiletech.imageconverter.vo.ImageGeneratorParams;
import com.mobiletech.imageconverter.vo.fx.RoundedCornersFX;
import com.mobiletech.imageconverter.vo.templates.GradientGeneratorTemplate;
import com.mobiletech.imageconverter.vo.templates.IphoneButtonTemplate;
import java.awt.Color;
import junit.framework.TestCase;

public class ImageGeneratorTest extends TestCase{
    private DirectoryUtil sourceDirectory = null;
    private DirectoryUtil testScenarioSourceDirectory = null; 
    private DirectoryUtil targetDirectory = null;
    private int maxThreads = 2;
    
    public ImageGeneratorTest(){               
        sourceDirectory = new DirectoryUtil("src/test/resources/testdata");       
        testScenarioSourceDirectory = new DirectoryUtil("src/test/resources/testdata/routine");
        
        // WARNING! TARGET DIRECTORY WILL BE EMPTIED! (ALL FILES DELETED)
        targetDirectory = new DirectoryUtil("/tmp/generatortest"); // WARNING! TARGET DIRECTORY WILL BE EMPTIED! (ALL FILES DELETED)
        // WARNING! TARGET DIRECTORY WILL BE EMPTIED! (ALL FILES DELETED)        
    }
    
    public static void main(String [] args){
        ImageGeneratorTest icte = new ImageGeneratorTest();
        icte.testMain();
    }
     
    public void testMain(){                            
        long start = System.currentTimeMillis();
        outp("\nEmptying Target Directory...");
        targetDirectory.deleteAllFilesAndDirectories();
        out(" done!");        

        byte [] image = null;

        // Test gradient                  
        //targetDirectory.createDirectory("gradient");
        ImageGeneratorParams params = new ImageGeneratorParams();
        /**
        params.setHeight(50);
        params.setWidth(10);
        GradientGeneratorTemplate gradient = new GradientGeneratorTemplate();
        gradient.setStartColor(new Color(230,50,90));
        gradient.setEndColor(new Color(40,30,240));
        params.addTemplate(gradient);

        gradient.setType(GradientGeneratorTemplate.VERTICAL);
        image = ImageGenerator.generateImage(params);        
        targetDirectory.writeImage(image, "gradient", "50x10_vertical.png");

        gradient.setType(GradientGeneratorTemplate.HORIZONTAL);
        image = ImageGenerator.generateImage(params);
        targetDirectory.writeImage(image, "gradient", "50x10_horizontal.png");

        gradient.setType(GradientGeneratorTemplate.DIAGONAL_TOPTOBOTTOM);
        image = ImageGenerator.generateImage(params);
        targetDirectory.writeImage(image, "gradient", "50x10_toptobottom.png");

        gradient.setType(GradientGeneratorTemplate.DIAGONAL_BOTTOMTOTOP);
        image = ImageGenerator.generateImage(params);
        targetDirectory.writeImage(image, "gradient", "50x10_bottomtotop.png");

        params.addEffect(new RoundedCornersFX());

        gradient.setType(GradientGeneratorTemplate.VERTICAL);
        image = ImageGenerator.generateImage(params);
        targetDirectory.writeImage(image, "gradient", "50x10_rounded_vertical.png");

        gradient.setType(GradientGeneratorTemplate.HORIZONTAL);
        image = ImageGenerator.generateImage(params);
        targetDirectory.writeImage(image, "gradient", "50x10_rounded_horizontal.png");

        gradient.setType(GradientGeneratorTemplate.DIAGONAL_TOPTOBOTTOM);
        image = ImageGenerator.generateImage(params);
        targetDirectory.writeImage(image, "gradient", "50x10_rounded_toptobottom.png");

        gradient.setType(GradientGeneratorTemplate.DIAGONAL_BOTTOMTOTOP);
        image = ImageGenerator.generateImage(params);
        targetDirectory.writeImage(image, "gradient", "50x10_rounded_bottomtotop.png");

        **/

        // Test iPhone button
        targetDirectory.createDirectory("iphoneButton");
        params = new ImageGeneratorParams();
        params.setHeight(46);
        params.setWidth(29);
        IphoneButtonTemplate iphoneButton = new IphoneButtonTemplate();
        params.addTemplate(iphoneButton);
        image = ImageGenerator.generateImage(params);
        targetDirectory.writeImage(image, "iphoneButton", "80x40_iphone.png");
        
        System.out.println("Total time: " + (System.currentTimeMillis()-start)/1000 + " seconds");       
    }
    
  
    private void out(String str){
        System.out.println(str);
    }
    private void outp(String str){
        System.out.print(str);
    }
}
