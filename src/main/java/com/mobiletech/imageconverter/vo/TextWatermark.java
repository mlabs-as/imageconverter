/*
 * Created on May 23, 2005
 *
 */
package com.mobiletech.imageconverter.vo;

import java.awt.Color;
import java.awt.Font;

/**
 * @author Andreas Ryland
 *
 */
public class TextWatermark {
    private Font font = null;
    private Color color = null;
    private int WatermarkPosition = 0;
    private String text = null;
    
    private TextWatermark(){}
    /**
     * Creates a new TextWatermark
     * 
     * @param text
     *            The text that is to be watermarked to the converted image
     * @param WatermarkPosition
     *            The desired placement of the text watermark on the converted
     *            image. Acceptable position values can be retrieved from the
     *            ImageConverter class. ie ImageConverter.WMARK_POS_TOPLEFT
     */
    public TextWatermark(String text,int WatermarkPosition){
        this.text = text;
        this.WatermarkPosition = WatermarkPosition;
    }
    /**
     * Creates a new TextWatermark
     * 
     * @param text
     *            The text that is to be watermarked to the converted image
     * @param WatermarkPosition
     *            The desired placement of the text watermark on the converted
     *            image. Acceptable position values can be retrieved from the
     *            ImageConverter class. ie ImageConverter.WMARK_POS_TOPLEFT
     * @param font
     *            The font with which the text will be written
     */
    public TextWatermark(String text,int WatermarkPosition,Font font){
        this.text = text;
        this.WatermarkPosition = WatermarkPosition;
        this.font = font;       
    }
    /**
     * Creates a new TextWatermark
     * 
     * @param text
     *            The text that is to be watermarked to the converted image
     * @param WatermarkPosition
     *            The desired placement of the text watermark on the converted
     *            image. Acceptable position values can be retrieved from the
     *            ImageConverter class. ie ImageConverter.WMARK_POS_TOPLEFT
     * @param color
     *            The color of the text written
     */
    public TextWatermark(String text,int WatermarkPosition,Color color){
        this.text = text;
        this.WatermarkPosition = WatermarkPosition;
        this.color = color;
    }
    /**
     * Creates a new TextWatermark
     * 
     * @param text
     *            The text that is to be watermarked to the converted image
     * @param WatermarkPosition
     *            The desired placement of the text watermark on the converted
     *            image. Acceptable position values can be retrieved from the
     *            ImageConverter class. ie ImageConverter.WMARK_POS_TOPLEFT
     * @param font
     *            The font with which the text will be written
     * @param color
     *            The color of the text written
     */
    public TextWatermark(String text,int WatermarkPosition,Font font,Color color){
        this.text = text;
        this.WatermarkPosition = WatermarkPosition;
        this.font = font;
        this.color = color;
    }       
    /**
     * @return Returns the color.
     */
    public Color getColor() {
        return color;
    }
    /**
     * @return Returns the font.
     */
    public Font getFont() {
        return font;
    }    
    /**
     * @param color The color to set.
     */
    public void setColor(Color color) {
        this.color = color;
    }
    /**
     * @param font The font to set.
     */
    public void setFont(Font font) {
        this.font = font;
    }
    /**
     * @return Returns the text.
     */
    public String getText() {
        return text;
    }
    /**
     * @return Returns the watermarkPosition.
     */
    public int getWatermarkPosition() {
        return WatermarkPosition;
    }
}
