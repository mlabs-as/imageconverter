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
public class GradientGeneratorTemplate extends GeneratorTemplate {
    private Color startColor = null;
    private Color endColor = null;
    private int type = VERTICAL;
    
    public static final int VERTICAL = 1;
    public static final int HORIZONTAL = 2;
    public static final int DIAGONAL_TOPTOBOTTOM = 3;
    public static final int DIAGONAL_BOTTOMTOTOP = 4;

    public GradientGeneratorTemplate (){
        super();
    }

    public Color getEndColor() {
        return endColor;
    }

    public void setEndColor(Color endColor) {
        this.endColor = endColor;
    }

    public Color getStartColor() {
        return startColor;
    }

    public void setStartColor(Color startColor) {
        this.startColor = startColor;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    
}
