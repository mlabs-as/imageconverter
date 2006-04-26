package com.mobiletech.imageconverter.vo;

import java.util.HashMap;
import java.util.Vector;

public class ScenarioRunStatistics {
    long totalTimeUsed = 0;
    long sizeOfInputImages = 0;
    int numErrors = 0;
    int numImagesRan = 0; 
    HashMap timeUsedByFormat = null;
    HashMap inputImageSizeByFormat = null;
    String outputFormat = null;
    Vector errors = null;
    
    private ScenarioRunStatistics(){};
        
    public ScenarioRunStatistics(String outputFormat){
        this.outputFormat = outputFormat;
        timeUsedByFormat = new HashMap();
        inputImageSizeByFormat = new HashMap();
        errors = new Vector();
    }
    public void addStatistic(String inputImageFormat, long inputImageSize, long startTime, long endTime){
        long timeUsed = endTime - startTime;
        timeUsedByFormat.put(inputImageFormat,timeUsed);
        inputImageSizeByFormat.put(inputImageFormat,inputImageSize);
        totalTimeUsed += timeUsed;
        sizeOfInputImages += inputImageSize;
        numImagesRan++;
    }
    public void addError(Throwable e){
        numErrors++;
        errors.add(e);
    }
    public int getNumImagesProcessed(){
        return numImagesRan;
    }
    public int getNumErrors(){
        return numErrors;
    }
}
