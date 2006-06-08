package com.mobiletech.imageconverter.filters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.mobiletech.imageconverter.exception.ImageConverterException;

public class JPEGApp14Filter {
    public static byte[] filter(byte [] inArray) throws ImageConverterException {
        ByteArrayInputStream input = null;
        ByteArrayOutputStream output = null;

        try {
            input = new ByteArrayInputStream(inArray);
            output = new ByteArrayOutputStream();
            
            JPEGFilter filter = new JPEGFilter(input);                
            filter.filter(input,output);
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
