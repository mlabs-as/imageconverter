package com.mobiletech.imageconverter.fileio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class FileUtil {
    public static boolean writeByteToFile(byte [] data, String fileName){
        File utFil = new File(fileName);
        try{
            FileOutputStream outputStream = new FileOutputStream(utFil);
            outputStream.write(data);
            outputStream.flush();
            outputStream.close();
        } catch (IOException ioe){
            return false;
        }
        return true;
    }
    public static boolean writeStringToFile(String data, String fileName){
        File utFil = new File(fileName);
        try{
            FileOutputStream outputStream = new FileOutputStream(utFil);
            PrintStream ps = new PrintStream(outputStream);
            ps.println(data);
            ps.flush();
            outputStream.flush();
            outputStream.close();
        } catch (IOException ioe){
            return false;
        }
        return true;
    }
    public static byte [] readFileAsByte(String path){
        File file = new File(path);
        byte[] fileContents = new byte[Long.valueOf(file.length()).intValue()];
        FileInputStream in = null;
        
        try {
            try{
                in = new FileInputStream(file);
                
                int offset = 0;
                int numRead = 0;
    
                while (offset < fileContents.length
                        && (numRead 
                           = in.read(fileContents, offset, fileContents.length - offset)) >= 0) {
                    offset += numRead;
                }
            }
            finally{
                in.close();
            }
        } catch (Exception e){
           return null; 
        }
        
        return fileContents;
    }
}
