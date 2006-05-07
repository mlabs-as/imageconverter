package com.mobiletech.imageconverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import com.mobiletech.imageconverter.fileio.TestDirectory;
import com.mobiletech.imageconverter.thread.TestScenarioRunner;
import com.mobiletech.imageconverter.vo.TestScenario;

import junit.framework.TestCase;

public class ImageConverterTestEngine extends TestCase{
    private TestDirectory sourceDirectory = null;
    private TestDirectory testScenarioSourceDirectory = null; 
    private TestDirectory targetDirectory = null;
    private int maxThreads = 2;
    
    public ImageConverterTestEngine(){        
        //sourceDirectory = new TestDirectory("C:/Documents and Settings/Andreas/Desktop/desktop/TestSite/problem images");
        //sourceDirectory = new TestDirectory("C:/Documents and Settings/Andreas/Desktop/desktop/TestSite/gif images");
        sourceDirectory = new TestDirectory("C:/Documents and Settings/Andreas/Desktop/desktop/TestSite/animated gifs");
        //sourceDirectory = new TestDirectory("C:/Documents and Settings/Andreas/Desktop/desktop/TestSite");
        //sourceDirectory = new TestDirectory("C:/Documents and Settings/Andreas/Desktop/desktop/TestSite/Single Run");
        //sourceDirectory = new TestDirectory("C:/Documents and Settings/Andreas/Desktop/desktop/TestSite/gif transparency");
        testScenarioSourceDirectory = new TestDirectory("C:/data/work/workspace/imageconverter/test/testdata/routine");
        // WARNING! TARGET DIRECTORY WILL BE EMPTIED! (ALL FILES DELETED)
        targetDirectory = new TestDirectory("C:/temp/imagetest"); // WARNING! TARGET DIRECTORY WILL BE EMPTIED! (ALL FILES DELETED)
        // WARNING! TARGET DIRECTORY WILL BE EMPTIED! (ALL FILES DELETED)
        
    }
    
    public static void main(String [] args){
        ImageConverterTestEngine icte = new ImageConverterTestEngine();
        icte.testMain();
    }
    
    public void runWithGUI(){
        Vector<TestScenario> scenarios = testScenarioSourceDirectory.getAllTestScenariosScanSubdirs();
        scenarios = runGui();
        runTest(scenarios);
    }
    public void testMain(){        
        Vector<TestScenario> scenarios = testScenarioSourceDirectory.getAllTestScenariosScanSubdirs();
        runTest(scenarios);
    }
    private void runTest(Vector<TestScenario> scenarios){
        InputStreamReader stdin =
            new InputStreamReader(System.in);
            BufferedReader console =
            new BufferedReader(stdin);
                    
        HashMap images = sourceDirectory.getAllSupportedImagesRecursive();
        printSourceImageSummary(images);        
        printTestScenarioSummary(scenarios);
        outp("\nEmptying Target Directory...");
        targetDirectory.deleteAllFilesAndDirectories();
        targetDirectory.createDirectoriesForScenarioNames(scenarios);
        out(" done!");        
        int totalNumberOfTests = calculateAndPrintTotalNumberOfTests(scenarios,images);
        int incrementPoint = totalNumberOfTests / 50;
        int testsRun = 0;
        int pointsPrinted = 0;
        
        Iterator<TestScenario> ite = scenarios.iterator();
        TestScenario currentScenario = null;
        Vector statVector = new Vector();
        
        while(ite.hasNext()){
            currentScenario = ite.next();
            TestScenarioRunner tsc = new TestScenarioRunner(currentScenario, images, targetDirectory);
            tsc.runScenario();
        }
        /*
         * THREADED PART
         
        outp("\n|0%          |25%         |50%           |75%      |100%\n|");
        //Thread [] testRunners = new Thread[maxThreads];
        TestScenarioRunner[] testRunners = new TestScenarioRunner[maxThreads];
        Iterator<TestScenario> ite = scenarios.iterator();
        TestScenario currentScenario = null;
        Vector statVector = new Vector();
        while(ite.hasNext()){
            currentScenario = ite.next();
            for(int i = 0; i < testRunners.length; i++){
                if(testRunners[i] == null){
                    //testRunners[i] = new Thread(new TestScenarioRunner(currentScenario, images, targetDirectory));
                    testRunners[i] = new TestScenarioRunner(currentScenario, images, targetDirectory);
                    testRunners[i].startWork();
                    break;
                }
            }
            boolean noFreeThreads = true;
            for(int i = 0; i < testRunners.length; i++){
                if(testRunners[i] == null){
                    noFreeThreads = false;
                }
            }            
            testsRun = 0;
            while(noFreeThreads){               
                for(int i = 0; i < testRunners.length; i++){
                    if(testRunners[i].isFinished()){
                        statVector.add(testRunners[i].getStatistics());
                        testsRun += testRunners[i].getNumImagesProcessed()+testRunners[i].getNumErrors();
                        testRunners[i] = null;
                        noFreeThreads = false;
                    }
                }   
                /*
                try {
                    this.wait(3);
                } catch (InterruptedException e) {}
                *//*
            }
            for(int i = 0; i < testRunners.length; i++){
                if(testRunners[i] != null){
                    testsRun += testRunners[i].getNumImagesProcessed()+testRunners[i].getNumErrors();
                }
            }
            while(testsRun - (pointsPrinted * incrementPoint) > incrementPoint){
                outp("*");
                pointsPrinted++;
            }
        }                
        out("|\n|**************************************************|");
        */
    }
    
    private Vector<TestScenario> runGui(){
        
        return null;
    }
    private int calculateAndPrintTotalNumberOfTests(Vector<TestScenario> scenarios, HashMap images){
        outp("\nCalculating number of tests to be run...");
        Iterator<TestScenario> ite = scenarios.iterator();
        TestScenario tmp = null;
        int numTests = 0;
        while(ite.hasNext()){
            tmp = ite.next();
            numTests += calculateNumberOfTestsForScenario(tmp, images);
        }
        out(" done!");
        out("\nBased on the current input data, a total of -> " + numTests + " <- tests will be run.");
        return numTests;
    }
    private int calculateNumberOfTestsForScenario(TestScenario ts, HashMap images){
        int num = 0;
        if(ts.isTestForAll()){
            Set keys = images.keySet();
            Iterator keyIt = keys.iterator();            
            String key = null;
            Vector temp = null;            
            while(keyIt.hasNext()){
                key = keyIt.next().toString();
                if(!key.equalsIgnoreCase("others")){
                    temp = (Vector)images.get(key);
                    num += temp.size();
                }
            }
        } else {
            Vector tmp = (Vector)images.get(ts.getFormat());
            if(tmp == null){
                num = 0;
            } else {
                num = tmp.size();
            }
        }                    
        return num;
    }
    private void printTestScenarioSummary(Vector<TestScenario> scenarios){
        out("\nScanned test scenario source Directory, found " + scenarios.size() + " Test Scenarios");        
    }
    
    private void printSourceImageSummary(HashMap images){        
        Set keys = images.keySet();
        Iterator keyIt = keys.iterator();
        int fileCount = 0;
        int otherCount = 0;
        String key = null;
        Vector temp = null;
        String outS = "";
        while(keyIt.hasNext()){
            key = keyIt.next().toString();
            if(key.equalsIgnoreCase("others")){
                otherCount = (Integer)images.get(key);
            } else {
                temp = (Vector)images.get(key);
                fileCount += temp.size();
                outS += temp.size() + " " + key + " images\n";
            }
            temp = null;
            key = null;
        }
        out("Scanned image source Directory, found " + fileCount + " images:");
        out(outS);
        out(otherCount + " other files");
        out(otherCount + fileCount + " total files");
    }
    private void out(String str){
        System.out.println(str);
    }
    private void outp(String str){
        System.out.print(str);
    }
}
