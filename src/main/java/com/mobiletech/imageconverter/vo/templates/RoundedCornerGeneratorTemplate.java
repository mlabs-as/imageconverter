/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mobiletech.imageconverter.vo.templates;

import com.mobiletech.imageconverter.vo.GeneratorTemplate;
import java.awt.Color;

/**
 *
 * @author andreas
 */
public class RoundedCornerGeneratorTemplate extends GeneratorTemplate {
    private Color color = null;
    private Color backgroundColor = null;
    private int type = TOPLEFT;
    private int radius = 80;
    
    public static final int TOPLEFT = 1;
    public static final int TOPRIGHT = 2;
    public static final int BOTTOMLEFT = 3;
    public static final int BOTTOMRIGHT = 4;

    public RoundedCornerGeneratorTemplate (){
        super();
    }    

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
    
}
