package com.mobiletech.imageconverter.vo;

public class TestScenario {
    private ImageConverterParams params = null;
    private String name = null;
    private boolean testForAll = false;
    private String format = null;
    private boolean onlyForMultiframe = false;
    /**
     * @return Returns the format.
     */
    public String getFormat() {
        return format;
    }
    /**
     * @param format The format to set.
     */
    public void setFormat(String format) {
        this.format = format;
        if(format != null){
            if(format.equalsIgnoreCase("All")){
                testForAll = true;
            } else {
                testForAll = false;
            }
        }
    }
    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return Returns the params.
     */
    public ImageConverterParams getParams() {
        return params;
    }
    /**
     * @param params The params to set.
     */
    public void setParams(ImageConverterParams params) {
        this.params = params;
    }
    /**
     * @return Returns the testForAll.
     */
    public boolean isTestForAll() {
        return testForAll;
    }
    /**
     * @param testForAll The testForAll to set.
     */
    public void setTestForAll(boolean testForAll) {
        this.testForAll = testForAll;
    }
    /**
     * @return Returns the onlyForMultiframe.
     */
    public boolean isOnlyForMultiframe() {
        return onlyForMultiframe;
    }
    /**
     * @param onlyForMultiframe The onlyForMultiframe to set.
     */
    public void setOnlyForMultiframe(boolean onlyForMultiframe) {
        this.onlyForMultiframe = onlyForMultiframe;
    }
    
    
}
