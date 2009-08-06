/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mobiletech.imageconverter.vo.fx;

import com.mobiletech.imageconverter.vo.ImageFXVO;

/**
 *
 * @author andreas
 */
public class RoundedCornersFX extends ImageFXVO{
    private int arcWidth = 10;
    private int arcHeight = 10;

    public RoundedCornersFX(){
        this.FXTYPE = ROUNDED_CORNERS;
    }

    public RoundedCornersFX(int arcWidth, int arcHeight){
        this.FXTYPE = ROUNDED_CORNERS;
        this.arcHeight = arcHeight;
        this.arcWidth = arcWidth;
    }

    public int getArcHeight() {
        return arcHeight;
    }

    public void setArcHeight(int arcHeight) {
        this.arcHeight = arcHeight;
    }

    public int getArcWidth() {
        return arcWidth;
    }

    public void setArcWidth(int arcWidth) {
        this.arcWidth = arcWidth;
    }    
    
}
