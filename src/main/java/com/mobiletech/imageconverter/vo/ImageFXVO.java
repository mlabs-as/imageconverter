/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mobiletech.imageconverter.vo;

/**
 *
 * @author andreas
 */
public class ImageFXVO {
    protected int FXTYPE = 0;
    public static final int ROUNDED_CORNERS = 1;

    public int getFXTYPE() {
        return FXTYPE;
    }

    public void setFXTYPE(int FXTYPE) {
        this.FXTYPE = FXTYPE;
    }


}
