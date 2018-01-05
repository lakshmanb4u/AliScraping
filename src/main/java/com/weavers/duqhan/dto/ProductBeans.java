/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.weavers.duqhan.dto;

import java.util.List;

/**
 *
 * @author Android-3
 */
public class ProductBeans {

    private List<ProductBean> products;
    private int totalProducts;
    private String statusCode;
    private String status;
    private List<String> allImages;
    private String categoryName;
    private String searchString;

    /**
     * @return the statusCode
     */
    public String getStatusCode() {
        return statusCode;
    }

    /**
     * @param statusCode the statusCode to set
     */
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the products
     */
    public List<ProductBean> getProducts() {
        return products;
    }

    /**
     * @param products the products to set
     */
    public void setProducts(List<ProductBean> products) {
        this.products = products;
    }

    /**
     * @return the totalProducts
     */
    public int getTotalProducts() {
        return totalProducts;
    }

    /**
     * @param totalProducts the totalProducts to set
     */
    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

    /**
     * @return the allImages
     */
    public List<String> getAllImages() {
        return allImages;
    }

    /**
     * @param allImages the allImages to set
     */
    public void setAllImages(List<String> allImages) {
        this.allImages = allImages;
    }

    /**
     * @return the categoryName
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * @param categoryName the categoryName to set
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * @return the searchString
     */
    public String getSearchString() {
        return searchString;
    }

    /**
     * @param searchString the searchString to set
     */
    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }
}
