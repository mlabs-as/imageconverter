package com.mobiletech.imageconverter.xml;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.mobiletech.imageconverter.vo.ImageConverterParams;
import com.mobiletech.imageconverter.vo.ImageWatermark;
import com.mobiletech.imageconverter.vo.TsScenario;
import com.mobiletech.imageconverter.vo.TextWatermark;

public class TestScenarioXMLParser {
    
    public static Vector<TsScenario> parseTestScenariosToVector(String xmlFile){
        File xmlF = new File(xmlFile);
        return parseTestScenariosToVector(xmlF);
    }
    public static Vector<TsScenario> parseTestScenariosToVector(File xmlFile){
        InputSource xmlSource = null;
        Document document = null;
        Vector<TsScenario> testScenarios = null;
        try {
            xmlSource = new InputSource(new FileInputStream(xmlFile));        
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();                        

            document = documentBuilder.parse(xmlSource);
            //XMLParserUtil.trimWhitespace(document); 
            
            Element root = document.getDocumentElement();
            NodeList scenarios = root.getElementsByTagName("TestScenario");
            testScenarios = new Vector<TsScenario>(scenarios.getLength());
            for(int i = 0; i < scenarios.getLength(); i++){
                testScenarios.add(parseTestScenario(scenarios.item(i)));
            }
        } catch (Throwable t){
            return null;
        }                
        return testScenarios;        
        
    }
    public static TsScenario [] parseTestScenarios(String xmlFile){
        File xmlF = new File(xmlFile);
        return parseTestScenarios(xmlF);
    }
    public static TsScenario [] parseTestScenarios(File xmlFile){
        InputSource xmlSource = null;
        Document document = null;
        TsScenario [] testScenarios = null;
        try {
            xmlSource = new InputSource(new FileInputStream(xmlFile));        
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();                        

            document = documentBuilder.parse(xmlSource);
            //XMLParserUtil.trimWhitespace(document);        
        } catch (Throwable t){
            return null;
        }
        
        Element root = document.getDocumentElement();
        NodeList scenarios = root.getElementsByTagName("TestScenario");
        testScenarios = new TsScenario[scenarios.getLength()];
        for(int i = 0; i < scenarios.getLength(); i++){
            testScenarios[i] = parseTestScenario(scenarios.item(i));
        }
        return testScenarios;        
    }
        
    public static String createScenarioXML(TsScenario ts){
        StringBuffer xml = new StringBuffer();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<TestScenarios>");
        xml.append(scenarioToXML(ts));
        xml.append("</TestScenarios>");
        return xml.toString();
    }
    public static String createScenarioXML(TsScenario [] ts){
        StringBuffer xml = new StringBuffer();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<TestScenarios>");
        for(int i = 0; i < ts.length; i++){
            xml.append(scenarioToXML(ts[i]));
        }
        xml.append("</TestScenarios>");
        return xml.toString();
    }
    public static String createScenarioXML(Vector<TsScenario> ts){
        StringBuffer xml = new StringBuffer();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<TestScenarios>");
        Iterator<TsScenario> ite = ts.iterator();
        while(ite.hasNext()){
            xml.append(scenarioToXML(ite.next()));
        }
        xml.append("</TestScenarios>");
        return xml.toString();
    }
    private static TsScenario parseTestScenario(Node scenario){
        TsScenario scn = new TsScenario();
        Node child = scenario.getFirstChild();
        while(child != null){
            if(child.getNodeName().equalsIgnoreCase("ScenarioName")){
                scn.setName(nodeToString(child));
            }
            if(child.getNodeName().equalsIgnoreCase("ScenarioFormat")){
                scn.setFormat(nodeToString(child));
            }
            if(child.getNodeName().equalsIgnoreCase("OnlyMultiframe")){
                scn.setOnlyForMultiframe(nodeToBoolean(child));
            }            
            if(child.getNodeName().equalsIgnoreCase("ImageParams")){
                scn.setParams(parseImageConverterParams(child));
            }
            child = child.getNextSibling();
        }        
        return scn;
    }
    private static ImageConverterParams parseImageConverterParams(Node n){
        ImageConverterParams icp = new ImageConverterParams(null,0,0);
        Node child = n.getFirstChild();
        while(child != null){
            if(child.getNodeName().equalsIgnoreCase("Format")){
                icp.setFormat(nodeToString(child));
            }
            if(child.getNodeName().equalsIgnoreCase("Height")){
                icp.setHeight(nodeToInt(child));
            }
            if(child.getNodeName().equalsIgnoreCase("Width")){
                icp.setWidth(nodeToInt(child));
            }
            if(child.getNodeName().equalsIgnoreCase("NumberOfColors")){
                icp.setNumberOfColors(nodeToInt(child));
            }
            if(child.getNodeName().equalsIgnoreCase("Grayscale")){
                icp.setGrayscale(nodeToBoolean(child));
            }
            if(child.getNodeName().equalsIgnoreCase("NoEnlargment")){
                icp.setNoEnlargement(nodeToBoolean(child));
            }
            if(child.getNodeName().equalsIgnoreCase("Image")){
                icp.setImage(nodeToByteArray(child));
            }            
            if(child.getNodeName().equalsIgnoreCase("TextWatermarks")){
                icp = parseTextWatermarks(child,icp);
            }
            if(child.getNodeName().equalsIgnoreCase("ImageWatermarks")){
                icp = parseImageWatermarks(child,icp);
            }
            child = child.getNextSibling();
        }                
        return icp;
    }
    private static ImageConverterParams parseImageWatermarks(Node n, ImageConverterParams icp){       
        NodeList children = n.getChildNodes();
        ImageWatermark temp = null;
        for(int i = 0; i < children.getLength(); i++){
            temp = parseImageWatermark(children.item(i));
            if(temp != null){
                icp.addImageWatermark(temp);
            }
        }
        return icp;
    }
    private static ImageConverterParams parseTextWatermarks(Node n, ImageConverterParams icp){       
        NodeList children = n.getChildNodes();
        TextWatermark temp = null;
        for(int i = 0; i < children.getLength(); i++){
            temp = parseTextWatermark(children.item(i));
            if(temp != null){
                icp.addTextWatermark(temp);
            }
        }
        return icp;
    }
    private static ImageWatermark parseImageWatermark(Node n){
        ImageWatermark tw = null;
        Node child = n.getFirstChild();
        
        byte [] image = null;
        int pos = -1;
        int width = -1;
        int height = -1;
        double sizeFactor = 0.0;
        float opaque = -1f;
        boolean noEnlargement = false;
        
        while(child != null){
            if(child.getNodeName().equalsIgnoreCase("Height")){
                height = nodeToInt(child);
            }
            if(child.getNodeName().equalsIgnoreCase("WatermarkPosition")){
                pos = nodeToInt(child);
            }
            if(child.getNodeName().equalsIgnoreCase("Width")){
                height = nodeToInt(child);
            }
            if(child.getNodeName().equalsIgnoreCase("SizeFactor")){
                sizeFactor = nodeToDouble(child);
            }
            if(child.getNodeName().equalsIgnoreCase("Opaque")){
                opaque = nodeToFloat(child);
            }            
            if(child.getNodeName().equalsIgnoreCase("Image")){
                image = nodeToByteArray(child);
            }
            if(child.getNodeName().equalsIgnoreCase("NoEnlargement")){
                noEnlargement = nodeToBoolean(child);
            }      
            child = child.getNextSibling();
        }        
        if(image != null && pos != -1){
            
            if(height != -1 && width != -1){
                if(opaque != -1f){
                    tw = new ImageWatermark(image, pos, width, height, opaque, noEnlargement);
                } else {
                    tw = new ImageWatermark(image, pos, width, height, noEnlargement);
                }                    
            } else if(sizeFactor != 0.0){
                if(opaque != -1f){
                    tw = new ImageWatermark(image, pos, sizeFactor, opaque, noEnlargement);
                } else {
                    tw = new ImageWatermark(image, pos, sizeFactor, noEnlargement);
                }
            }
        }
        return tw;
    }   
    private static TextWatermark parseTextWatermark(Node n){
        TextWatermark tw = null;
        Node child = n.getFirstChild();
        String text = null;
        int pos = -1;
        Color col = null;
        Font fnt = null;
        
        while(child != null){
            if(child.getNodeName().equalsIgnoreCase("Text")){
                text = nodeToString(child);
            }
            if(child.getNodeName().equalsIgnoreCase("WatermarkPosition")){
                pos = nodeToInt(child);
            }
            if(child.getNodeName().equalsIgnoreCase("Font")){
                fnt = parseFont(child);
            }
            if(child.getNodeName().equalsIgnoreCase("Color")){
                col = parseColor(child);
            }            
            child = child.getNextSibling();
        }        
        if(text != null && pos != -1){
            tw = new TextWatermark(text, pos);
            if(fnt != null){
                tw.setFont(fnt);
            }
            if(col != null){
                tw.setColor(col);
            }
        }
        return tw;
    }
    private static Font parseFont(Node n){
        Font fnt = null;
        Node child = n.getFirstChild();        
        int size = -1;
        String name = null;
        int style = -1;        
        
        while(child != null){            
            if(child.getNodeName().equalsIgnoreCase("Size")){
                size = nodeToInt(child);
            }
            if(child.getNodeName().equalsIgnoreCase("Name")){
                name =  nodeToString(child);
            }
            if(child.getNodeName().equalsIgnoreCase("Style")){
                style = nodeToInt(child);
            }            
            child = child.getNextSibling();
        }        
        if(size != -1 && style != -1 && name != null){
            fnt = new Font(name,style,size);
        }        
        return fnt;
    }    
    private static Color parseColor(Node n){
        Color col = null;
        
        Node child = n.getFirstChild();        
        int r = -1;
        int g = -1;
        int b = -1;
        int a = -1;        
        
        while(child != null){            
            if(child.getNodeName().equalsIgnoreCase("Red")){
                r = nodeToInt(child);
            }
            if(child.getNodeName().equalsIgnoreCase("Green")){
                g = nodeToInt(child);
            }
            if(child.getNodeName().equalsIgnoreCase("Blue")){
                b = nodeToInt(child);
            }
            if(child.getNodeName().equalsIgnoreCase("Alpha")){
                a = nodeToInt(child);
            }           
            child = child.getNextSibling();
        }        
        if(r != -1 && g != -1 && b != -1){
            if(a != -1){
                col = new Color(r,g,b);
            } else {
                col = new Color(r,g,b,a);
            }
        }        
        return col;
    }
    
    private static String nodeToString(Node n){
        return n.getFirstChild().getNodeValue();
    }
    private static int nodeToInt(Node n){
        return Integer.parseInt(n.getFirstChild().getNodeValue());
    }
    private static float nodeToFloat(Node n){
        return Float.parseFloat(n.getFirstChild().getNodeValue());
    }
    private static double nodeToDouble(Node n){
        return Double.parseDouble(n.getFirstChild().getNodeValue());
    }
    private static boolean nodeToBoolean(Node n){
        return (n.getFirstChild().getNodeValue().equalsIgnoreCase("true") ? true : false);
    }
    private static byte [] nodeToByteArray(Node n){
        return n.getFirstChild().getNodeValue().getBytes();
    }
    private static String scenarioToXML(TsScenario ts){
        StringBuffer xml = new StringBuffer();
        xml.append("<TestScenario>");
        if(ts.getName() != null){
            xml.append("<ScenarioName>");
            xml.append(ts.getName());
            xml.append("</ScenarioName>");
        }
        if(ts.getFormat() != null){
            xml.append("<ScenarioFormat>");
            xml.append(ts.getFormat());
            xml.append("</ScenarioFormat>");
        }
        xml.append("<OnlyMultiframe>");
        xml.append(ts.isOnlyForMultiframe());
        xml.append("</OnlyMultiframe>");
        
        if(ts.getParams() != null){            
            xml.append(imageParamsToXML(ts.getParams()));
        }
        xml.append("</TestScenario>");
        return xml.toString();
    }
    
    private static String imageParamsToXML(ImageConverterParams ts){
        StringBuffer xml = new StringBuffer();
        xml.append("<ImageParams>");        
        if(ts.getFormat() != null){
            xml.append("<Format>");
            xml.append(ts.getFormat());
            xml.append("</Format>");
        }
        if(ts.getHeight() > 0){
            xml.append("<Height>");
            xml.append(ts.getHeight());
            xml.append("</Height>");
        }
        if(ts.getWidth() > 0){
            xml.append("<Width>");
            xml.append(ts.getWidth());
            xml.append("</Width>");
        }
        if(ts.getImage() != null){
            xml.append("<Image>");
            xml.append(ts.getImage());
            xml.append("</Image>");
        }
        if(ts.getJPEGCompressionQuality() > 0f){
            xml.append("<JPEGCompressionQuality>");
            xml.append(ts.getJPEGCompressionQuality());
            xml.append("</JPEGCompressionQuality>");
        }
        if(ts.getNumberOfColors() > 0){
            xml.append("<NumberOfColors>");
            xml.append(ts.getNumberOfColors());
            xml.append("</NumberOfColors>");
        }
        xml.append("<Grayscale>");
        xml.append((ts.isGrayscale() ? "true" : "false"));
        xml.append("</Grayscale>");
        
        xml.append("<NoEnlargment>");
        xml.append((ts.isNoEnlargement() ? "true" : "false"));
        xml.append("</NoEnlargment>");
                     
        String temp = imageParamsWatermarksToXML(ts);
        
        if(temp != null){
            xml.append(temp);    
        }
        
        temp = null;
        temp = textParamsWatermarksToXML(ts);
        
        if(temp != null){
            xml.append(temp);    
        }
        
        xml.append("</ImageParams>");
        return xml.toString();
    }
    
    private static String imageParamsWatermarksToXML(ImageConverterParams ts){
        Iterator watermarks = ts.getImageWatermarksIterator();
        if(watermarks != null){
            if(watermarks.hasNext()){
                StringBuffer xml = new StringBuffer();
                xml.append("<ImageWatermarks>");
                do{
                    xml.append(imageWatermarkToXML((ImageWatermark)watermarks.next()));
                } while(watermarks.hasNext());
                xml.append("</ImageWatermarks>");
                return xml.toString();
            }
        }
        return null; 
    }
    
    private static String textParamsWatermarksToXML(ImageConverterParams ts){
        Iterator watermarks = ts.getTextWatermarksIterator();
        if(watermarks != null){
            if(watermarks.hasNext()){
                StringBuffer xml = new StringBuffer();
                xml.append("<TextWatermarks>");
                do{
                    xml.append(textWatermarkToXML((TextWatermark)watermarks.next()));
                } while(watermarks.hasNext());
                xml.append("</TextWatermarks>");
                return xml.toString();
            }
        }
        return null; 
    }
    
    private static String imageWatermarkToXML(ImageWatermark ts){        
        StringBuffer xml = new StringBuffer();
        xml.append("<ImageWatermark>");        
    
        if(ts.getHeight() > 0){
            xml.append("<Height>");
            xml.append(ts.getHeight());
            xml.append("</Height>");
        }
        if(ts.getWidth() > 0){
            xml.append("<Width>");
            xml.append(ts.getWidth());
            xml.append("</Width>");
        }
        xml.append("<WatermarkPosition>");
        xml.append(ts.getWatermarkPosition());
        xml.append("</WatermarkPosition>");
    
        xml.append("<Image>");
        xml.append(ts.getImage());
        xml.append("</Image>");
    
        if(ts.getSizeFactor() != 0.0){
            xml.append("<SizeFactor>");
            xml.append(ts.getSizeFactor());
            xml.append("</SizeFactor>");
        }
        if(ts.getOpaque() != 0.0f){
            xml.append("<Opaque>");
            xml.append(ts.getOpaque());
            xml.append("</Opaque>");
        }
        xml.append("<NoEnlargement>");
        xml.append(ts.isNoEnlargement());
        xml.append("</NoEnlargement>");
        
        xml.append("</ImageWatermark>");
        return xml.toString();
    }
    
    private static String textWatermarkToXML(TextWatermark ts){        
        StringBuffer xml = new StringBuffer();
        xml.append("<TextWatermark>");        
                             
        xml.append("<Text>");
        xml.append(ts.getText());
        xml.append("</Text>");
    
        xml.append("<WatermarkPosition>");
        xml.append(ts.getWatermarkPosition());
        xml.append("</WatermarkPosition>");
            
        if(ts.getColor() != null){
            xml.append(colorToXML(ts.getColor()));
        }        
        if(ts.getFont() != null){
            xml.append(fontToXML(ts.getFont()));
        }
        xml.append("</TextWatermark>");
        return xml.toString();
    }
    
    private static String colorToXML(Color ts){        
        StringBuffer xml = new StringBuffer();
        xml.append("<Color>");        
                             
        xml.append("<Red>");
        xml.append(ts.getRed());
        xml.append("</Red>");
    
        xml.append("<Green>");
        xml.append(ts.getGreen());        
        xml.append("</Green>");
    
        xml.append("<Blue>");
        xml.append(ts.getBlue());
        xml.append("</Blue>");
    
        xml.append("<Alpha>");
        xml.append(ts.getAlpha());
        xml.append("</Alpha>");
    
        /*
        xml.append("<Transparency>");
        xml.append(ts.getTransparency());
        xml.append("</Transparency>");
        */
        xml.append("</Color>");
        return xml.toString();
    }
    
    private static String fontToXML(Font ts){        
        StringBuffer xml = new StringBuffer();
        xml.append("<Font>");        
                             
        xml.append("<Size>");
        xml.append(ts.getSize());
        xml.append("</Size>");
    
        xml.append("<Name>");
        xml.append(ts.getFontName());
        xml.append("</Name>");
        
        xml.append("<Style>");
        xml.append(ts.getStyle());
        xml.append("</Style>");
               
        xml.append("</Font>");
        return xml.toString();
    }
}
