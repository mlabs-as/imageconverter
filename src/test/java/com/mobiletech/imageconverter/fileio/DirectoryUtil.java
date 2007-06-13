package com.mobiletech.imageconverter.fileio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.mobiletech.imageconverter.ImageConverter;
import com.mobiletech.imageconverter.vo.TsScenario;
import com.mobiletech.imageconverter.xml.ScenarioXMLParser;

public class DirectoryUtil {
    
    private File directory = null;
    private HashMap map = null;
    private Vector<TsScenario> tScn = null;
    
    private DirectoryUtil(){}
    
    public DirectoryUtil(String dir){
        directory = new File(dir);
    }
    
    public DirectoryUtil(File dir){
        directory = dir;
    }
    
    public boolean validate(){
        if(directory != null){
            if(directory.isDirectory()){
                return true;
            }
        }
        return false;
    }
    
    public void deleteAllFiles(){
        if(validate()){
            emptyDirectoryNotRecursive(directory);
        }
    }
    
    public void deleteAllFilesAndDirectories(){
        if(validate()){
            emptyDirectoryRecursive(directory);
        }
    }
    
    public Vector<TsScenario> getAllTestScenarios(){
        if(validate()){            
            File [] files = directory.listFiles();
            TsScenario [] temp = null;
            Vector<TsScenario> scn = new Vector<TsScenario>();
            for(int i = 0;i < files.length; i++){
                if(!files[i].isDirectory()){
                    if(files[i].getName().lastIndexOf(".") > 0 && files[i].getName().substring(files[i].getName().lastIndexOf(".")).equalsIgnoreCase("xml")){
                        temp = ScenarioXMLParser.parseTestScenarios(files[i]);
                    }
                    if(temp != null){
                        for(int e = 0; e < temp.length; e++){
                            scn.add(temp[e]);
                            temp[e] = null;
                        }                        
                    }
                }
            }
            return scn;
        }
        return null;
    }
    
    public Vector<TsScenario> getAllTestScenariosScanSubdirs(){
        if(validate()){
            tScn = null;
            tScn = new Vector<TsScenario>();
            getAllTestScenariosRecursive(directory);
            return tScn;
        }
        return null;
    }
    private void getAllTestScenariosRecursive(File dir){            
        File [] files = dir.listFiles();
        TsScenario [] temp = null;
        
        for(int i = 0;i < files.length; i++){
            if(!files[i].isDirectory()){
                if(files[i].getName().lastIndexOf(".") > 0 && files[i].getName().substring(files[i].getName().lastIndexOf(".")+1).equalsIgnoreCase("xml")){
                    temp = ScenarioXMLParser.parseTestScenarios(files[i]);
                }
                if(temp != null){
                    for(int e = 0; e < temp.length; e++){
                        addTestScenario(temp[e]);
                        temp[e] = null;
                    }                        
                }
            } else {
                getAllTestScenariosRecursive(files[i]);
            }
        }
    }
    private synchronized void addTestScenario(TsScenario ts){
        tScn.add(ts);
    }
    public HashMap getAllSupportedImages(){
        if(validate()){
            map = null;
            map = new HashMap();
            File [] files = directory.listFiles();
            String format = null;
            
            for(int i = 0;i < files.length; i++){
                if(!files[i].isDirectory()){
                    format = ImageConverter.getFormatOfSupportedImage(files[i]);
                    if(format != null){
                        addImageToMap(format,files[i]);
                    } else {
                        incrementOtherFilesCount();
                    }
                    format = null;
                }
            }            
            return map;
        }
        return null;
    }
    
    public HashMap getAllSupportedImagesRecursive(){
        if(validate()){
            map = null;
            map = new HashMap();
            getAllSupportedImagesRecursive(directory);
            return map;
        }
        return null;
    }
    private void getAllSupportedImagesRecursive(File dir){           
        File [] files = dir.listFiles();
        String format = null;
        
        for(int i = 0;i < files.length; i++){
            if(!files[i].isDirectory()){
                format = ImageConverter.getFormatOfSupportedImage(files[i]);
                if(format != null){
                    addImageToMap(format,files[i]);
                } else {
                    incrementOtherFilesCount();
                }
                format = null;
            } else {
                getAllSupportedImagesRecursive(files[i]);
            }
        }                                  
    }
    public void createDirectoriesForScenarioNames(Vector<TsScenario> scenarios){
        Iterator<TsScenario> ite = scenarios.iterator();
        File temp = null;
        TsScenario tmpScn = null;
        while(ite.hasNext()){
            tmpScn = ite.next();
            if(tmpScn != null){
	            temp = new File(directory.getAbsolutePath() + "/" + tmpScn.getName());
	            int unique = 0;
	            while(temp.exists()){
	                temp = null;
	                temp = new File(directory.getAbsolutePath() + "/" + tmpScn.getName() + "(" + ++unique + ")");                
	            }
	            if(unique > 0){
	                tmpScn.setName(tmpScn.getName()+"("+unique+")");
	            }
	            temp.mkdirs();
	            temp = null;
	            tmpScn = null;
            }
        }
    }
    public boolean writeImage(byte [] image, String scenarioName, String fileName){
        File utFil = new File(directory.getAbsolutePath() + "/" + scenarioName + "/" + fileName);
        try{
            FileOutputStream outputStream = new FileOutputStream(utFil);
            outputStream.write(image);
            outputStream.flush();
            outputStream.close();
        } catch (IOException ioe){
            return false;
        }
        return true;
    }
    private synchronized void incrementOtherFilesCount(){
        Object count = map.get("others");
        if(count == null){
            map.put("others",new Integer(1));
        } else {
            Integer cnt = (Integer)count;
            cnt++;
            map.put("others",cnt);
        }
    }
    private synchronized void addImageToMap(String format, File image){
        Vector imgs = (Vector)map.get(format);
        if(imgs == null){
            imgs = new Vector();
        }
        imgs.add(image.getAbsolutePath());//+"/"+image.getName());       
        map.put(format,imgs);        
    }
    private void emptyDirectoryNotRecursive(File directory){
        if(validate()){
            File [] files = null;        
            files = directory.listFiles();        
            for(int i = 0;i < files.length; i++){
                if(!files[i].isDirectory()){
                    files[i].delete();
                }
            }
        }
    }
    
    private void emptyDirectoryRecursive(File directory){
        if(validate()){
            File [] files = null;        
            files = directory.listFiles();        
            for(int i = 0;i < files.length; i++){
                if(files[i].isDirectory()){
                    emptyDirectoryRecursive(files[i]);
                }
                files[i].delete();
            }
        }
    }
}
