package com.mobiletech.imageconverter.filters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.mobiletech.imageconverter.exception.ImageConverterException;

public class JPEGApp14Filter {
    public static final int APP_0 = 0xE0;
    public static final int APP_1 = 0xE1;
    public static final int APP_2 = 0xE2;
    public static final int APP_3 = 0xE3;
    public static final int APP_4 = 0xE4;
    public static final int APP_5 = 0xE5;
    public static final int APP_6 = 0xE6;
    public static final int APP_7 = 0xE7;
    public static final int APP_8 = 0xE8;
    public static final int APP_9 = 0xE9;
    public static final int APP_10 = 0xEA;
    public static final int APP_11 = 0xEB;
    public static final int APP_12 = 0xEC;
    public static final int APP_13 = 0xED;
    public static final int APP_14 = 0xEE;
    public static final int APP_15 = 0xEF;
    
	private int[] defaultMarkers = new int[]{};
	
	public static int getMaxFilterLevel(){
    	return 4;
    }
    
    public void setFilterLevel(int level){
    	switch(level){
	    	case 1:
	    		defaultMarkers = new int[]{
	    			APP_2, APP_3, APP_4, APP_5, APP_6, APP_7    	    	
	    	    };		    		
	    		break;
	    	case 2:
	    		defaultMarkers = new int[]{
	    			APP_3, APP_4, APP_5, APP_6, APP_7
	    	    };		    		
	    		break;    		
	    	case 3:
	    		defaultMarkers = new int[]{
	    			APP_1, APP_2, APP_3, APP_4, APP_5, APP_6, APP_7
	    	    };	    		
	    		break;
	    	default: // take everything away
	    		defaultMarkers = new int[]{
	    			
	    	    };
    	}
    }
    
    public byte[] filter(byte [] inArray, int filterLevel) throws ImageConverterException {
        ByteArrayInputStream input = null;
        ByteArrayOutputStream output = null;

        try {
            input = new ByteArrayInputStream(inArray);
            output = new ByteArrayOutputStream();
            
            JPEGFilter filter = new JPEGFilter(input);
            setFilterLevel(filterLevel);
            filter.filter(input,output, defaultMarkers);
            return output.toByteArray();
        } catch (IOException e) {
            throw new ImageConverterException(ImageConverterException.Types.IO_ERROR,"IOException thrown when filtering JPEG Image",e);
        } finally {
            if(input != null){
                try {
                    input.close();
                } catch (IOException e) {}
            }
            input = null;
            if(output != null){
                try {
                    output.close();
                } catch (IOException e) {}
            }
            output = null;
        }        
    }
}
