/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.weavers.duqhan.dto;

/**
 *
 * @author weaversAndroid
 */
public class AxpProductDto {

    private String skuAttr;
    private String skuPropIds;
    private SkuVal skuVal;

    /**
     * @return the skuAttr
     */
    public String getSkuAttr() {
        return skuAttr;
    }

    /**
     * @param skuAttr the skuAttr to set
     */
    public void setSkuAttr(String skuAttr) {
        this.skuAttr = skuAttr;
    }

    /**
     * @return the skuPropIds
     */
    public String getSkuPropIds() {
        return skuPropIds;
    }

    /**
     * @param skuPropIds the skuPropIds to set
     */
    public void setSkuPropIds(String skuPropIds) {
        this.skuPropIds = skuPropIds;
    }

    /**
     * @return the skuVal
     */
    public SkuVal getSkuVal() {
        return skuVal;
    }

    /**
     * @param skuVal the skuVal to set
     */
    public void setSkuVal(SkuVal skuVal) {
        this.skuVal = skuVal;
    }
}
