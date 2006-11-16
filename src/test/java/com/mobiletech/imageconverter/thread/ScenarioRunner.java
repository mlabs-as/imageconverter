package com.mobiletech.imageconverter.thread;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import com.mobiletech.imageconverter.ImageConverter;
import com.mobiletech.imageconverter.exception.ImageConverterException;
import com.mobiletech.imageconverter.fileio.FileUtil;
import com.mobiletech.imageconverter.fileio.DirectoryUtil;
import com.mobiletech.imageconverter.vo.TsScenario;
import com.mobiletech.imageconverter.vo.ScenarioRunStatistics;

public class ScenarioRunner implements Runnable{
    private int imagesDone = 0;
    private boolean finished = false;
    private TsScenario ts = null;    
    private ScenarioRunStatistics stat = null;
    private DirectoryUtil out = null;
    private HashMap images = null;
    
    private ScenarioRunner(){}
    
    public ScenarioRunner(TsScenario tst, HashMap images, DirectoryUtil out){
        this.ts = tst;
        this.out = out;
        this.images = images;
        stat = new ScenarioRunStatistics(ts.getParams().getFormat());
    }
    public synchronized void startWork(){
        Thread t = new Thread(this);
        t.start();
    }
    public void run(){
        runScenario();
    }
    public ScenarioRunStatistics getStatistics(){
        return stat;
    }
    public void runScenario(){
        reset();        
        if(ts.isTestForAll()){
            Set keys = images.keySet();
            Iterator keyIt = keys.iterator();            
            String key = null;
            
            while(keyIt.hasNext()){
                key = keyIt.next().toString();
                if(!key.equalsIgnoreCase("others")){
                    runImageVector((Vector)images.get(key), key);                    
                }
            }
        } else {
            runImageVector((Vector)images.get(ts.getFormat()), ts.getFormat());
        }    
        finished = true;
    }
    public boolean isFinished(){
        return finished;
    }
    private void runImageVector(Vector images, String format){
        Iterator ite = images.iterator();
        
        while(ite.hasNext()){
            runTest(ite.next().toString(), format);            
        }
    }
    
    private void runTest(String fil, String format){
        byte [] image = FileUtil.readFileAsByte(fil);
        ts.getParams().setImage(image);
        long endTime = 0;
        long startTime = System.currentTimeMillis();
        boolean write = false;
        if(out != null){
            write = true;
        }
        try {  
            image = ImageConverter.convertImage(ts.getParams());                
            endTime = System.currentTimeMillis();
            stat.addStatistic(format,image.length,startTime, endTime);            
        } catch (ImageConverterException e) {
        	System.out.println("Image failed: "+fil+" error: "+e.getMessage());
            stat.addError(e);
            write = false;            
        }        
        if(write){
            try {
                out.writeImage(image,ts.getName(),fil.substring(fil.lastIndexOf("\\")+1, fil.lastIndexOf(".")+1)+ts.getParams().getFormat());
            } catch (RuntimeException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    private void reset(){
        imagesDone = 0;
        finished = false;        
    }
    
    public int getNumImagesProcessed(){
        return stat.getNumImagesProcessed();        
    }
    public int getNumErrors(){
        return stat.getNumErrors();
    }
}
