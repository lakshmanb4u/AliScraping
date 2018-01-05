/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.weavers.duqhan.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
//import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Android-3
 */
public class ProductBean {

    /**
     * @return the sizeColorMaps
     */
    public List<SizeColorMapDto> getSizeColorMaps() {
        return sizeColorMaps;
    }

    /**
     * @param sizeColorMaps the sizeColorMaps to set
     */
    public void setSizeColorMaps(List<SizeColorMapDto> sizeColorMaps) {
        this.sizeColorMaps = sizeColorMaps;
    }

    /**
     * @return the imageDtos
     */
    public List<ImageDto> getImageDtos() {
        return imageDtos;
    }

    /**
     * @param imageDtos the imageDtos to set
     */
    public void setImageDtos(List<ImageDto> imageDtos) {
        this.imageDtos = imageDtos;
    }

    private Long productId;
    private String name;
    private Long categoryId;
    private String categoryName;
    private String description;
    private String imgurl;
    private Date lastUpdate;
    private Double price;
    private Double discountedPrice;
    private Double discountPCT;
    private Long sizeId;
    private String size;
    private String sizeUnit;
    private Long colorId;
    private String color;
    private int available;
    private Long sizeColorMapId;
    private Long cartId;
    private List<SizeColorMapDto> sizeColorMaps;
    private List<ImageDto> imageDtos;
    //private MultipartFile frontImage;
    private String qty;
    private Long vendorId;
    private String shippingTime;
    private Double shippingRate;
    private Double productLength;
    private Double productWidth;
    private Double productHeight;
    private Double productWeight;
    private String externalLink;
    private String specifications;
    private HashMap<String,String> specificationsMap;
    private String statusCode;
    private String status;

    /**
     * @return the productId
     */
    public Long getProductId() {
        return productId;
    }

    /**
     * @param productId the productId to set
     */
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the categoryId
     */
    public Long getCategoryId() {
        return categoryId;
    }

    /**
     * @param categoryId the categoryId to set
     */
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the imgurl
     */
    public String getImgurl() {
        return imgurl;
    }

    /**
     * @param imgurl the imgurl to set
     */
    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    /**
     * @return the lastUpdate
     */
    public Date getLastUpdate() {
        return lastUpdate;
    }

    /**
     * @param lastUpdate the lastUpdate to set
     */
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * @return the price
     */
    public Double getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * @return the discountedPrice
     */
    public Double getDiscountedPrice() {
        return discountedPrice;
    }

    /**
     * @param discountedPrice the discountedPrice to set
     */
    public void setDiscountedPrice(Double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    /**
     * @return the discountPCT
     */
    public Double getDiscountPCT() {
        return discountPCT;
    }

    /**
     * @param discountPCT the discountPCT to set
     */
    public void setDiscountPCT(Double discountPCT) {
        this.discountPCT = discountPCT;
    }

    /**
     * @return the size
     */
    public String getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(String size) {
        this.size = size;
    }

    /**
     * @return the sizeUnit
     */
    public String getSizeUnit() {
        return sizeUnit;
    }

    /**
     * @param sizeUnit the sizeUnit to set
     */
    public void setSizeUnit(String sizeUnit) {
        this.sizeUnit = sizeUnit;
    }

    /**
     * @return the color
     */
    public String getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * @return the available
     */
    public int getAvailable() {
        return available;
    }

    /**
     * @param available the available to set
     */
    public void setAvailable(int available) {
        this.available = available;
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
     * @return the sizeId
     */
    public Long getSizeId() {
        return sizeId;
    }

    /**
     * @param sizeId the sizeId to set
     */
    public void setSizeId(Long sizeId) {
        this.sizeId = sizeId;
    }

    /**
     * @return the colorId
     */
    public Long getColorId() {
        return colorId;
    }

    /**
     * @param colorId the colorId to set
     */
    public void setColorId(Long colorId) {
        this.colorId = colorId;
    }

    /**
     * @return the sizeColorMapId
     */
    public Long getSizeColorMapId() {
        return sizeColorMapId;
    }

    /**
     * @param sizeColorMapId the sizeColorMapId to set
     */
    public void setSizeColorMapId(Long sizeColorMapId) {
        this.sizeColorMapId = sizeColorMapId;
    }

    /**
     * @return the cartId
     */
    public Long getCartId() {
        return cartId;
    }

    /**
     * @param cartId the cartId to set
     */
    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    /**
     * @return the frontImage
     *//*
    public MultipartFile getFrontImage() {
        return frontImage;
    }

    *//**
     * @param frontImage the frontImage to set
     *//*
    public void setFrontImage(MultipartFile frontImage) {
        this.frontImage = frontImage;
    }*/

    /**
     * @return the qty
     */
    public String getQty() {
        return qty;
    }

    /**
     * @param qty the quantity to set
     */
    public void setQty(String qty) {
        this.qty = qty;
    }

    /**
     * @return the vendorId
     */
    public Long getVendorId() {
        return vendorId;
    }

    /**
     * @param vendorId the vendorId to set
     */
    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    /**
     * @return the shippingTime
     */
    public String getShippingTime() {
        return shippingTime;
    }

    /**
     * @param shippingTime the shippingTime to set
     */
    public void setShippingTime(String shippingTime) {
        this.shippingTime = shippingTime;
    }

    /**
     * @return the shippingRate
     */
    public Double getShippingRate() {
        return shippingRate;
    }

    /**
     * @param shippingRate the shippingRate to set
     */
    public void setShippingRate(Double shippingRate) {
        this.shippingRate = shippingRate;
    }

    /**
     * @return the productLength
     */
    public Double getProductLength() {
        return productLength;
    }

    /**
     * @param productLength the productLength to set
     */
    public void setProductLength(Double productLength) {
        this.productLength = productLength;
    }

    /**
     * @return the productWidth
     */
    public Double getProductWidth() {
        return productWidth;
    }

    /**
     * @param productWidth the productWidth to set
     */
    public void setProductWidth(Double productWidth) {
        this.productWidth = productWidth;
    }

    /**
     * @return the productHeight
     */
    public Double getProductHeight() {
        return productHeight;
    }

    /**
     * @param productHeight the productHeight to set
     */
    public void setProductHeight(Double productHeight) {
        this.productHeight = productHeight;
    }

    /**
     * @return the productWeight
     */
    public Double getProductWeight() {
        return productWeight;
    }

    /**
     * @param productWeight the productWeight to set
     */
    public void setProductWeight(Double productWeight) {
        this.productWeight = productWeight;
    }

    /**
     * @return the externalLink
     */
    public String getExternalLink() {
        return externalLink;
    }

    /**
     * @param externalLink the externalLink to set
     */
    public void setExternalLink(String externalLink) {
        this.externalLink = externalLink;
    }

    /**
     * @return the specifications
     */
    public String getSpecifications() {
        return specifications;
    }

    /**
     * @param specifications the specifications to set
     */
    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    /**
     * @return the specificationsMap
     */
    public HashMap<String,String> getSpecificationsMap() {
        return specificationsMap;
    }

    /**
     * @param specificationsMap the specificationsMap to set
     */
    public void setSpecificationsMap(HashMap<String,String> specificationsMap) {
        this.specificationsMap = specificationsMap;
    }

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

}
