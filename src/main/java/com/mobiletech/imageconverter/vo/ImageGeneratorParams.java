/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mobiletech.imageconverter.vo;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author andreas
 */
public class ImageGeneratorParams {
    private int width = 0;
    private int height = 0;
    private Collection<GeneratorTemplate> templates = null;
    private Collection<ImageFXVO> effects = null;

    public void addTemplate(GeneratorTemplate template){
        if(templates == null){
            templates = new ArrayList<GeneratorTemplate>();
        }
        templates.add(template);
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Collection<GeneratorTemplate> getTemplates() {
        return templates;
    }

    public void setTemplates(Collection<GeneratorTemplate> templates) {
        this.templates = templates;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public Collection<ImageFXVO> getEffects() {
        return effects;
    }

    public void addEffect(ImageFXVO effect){
        if(effects == null){
            effects = new ArrayList<ImageFXVO>();
        }
        effects.add(effect);
    }
}
