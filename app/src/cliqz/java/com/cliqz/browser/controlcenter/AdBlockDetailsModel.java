package com.cliqz.browser.controlcenter;

/**
 * @author  Ravjit Uppal
 */
public class AdBlockDetailsModel {
    public final String companyName;
    public final int adBlockCount;

    public AdBlockDetailsModel(String companyName, int adBlockCount) {
        this.companyName = companyName;
        this.adBlockCount = adBlockCount;
    }
}
