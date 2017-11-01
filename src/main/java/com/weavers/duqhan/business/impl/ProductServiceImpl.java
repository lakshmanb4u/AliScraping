/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.weavers.duqhan.business.impl;

import com.google.gson.Gson;
import com.weavers.duqhan.business.ProductService;
import com.weavers.duqhan.dao.CategoryDao;
import com.weavers.duqhan.dao.ProductDao;
import com.weavers.duqhan.dao.ProductImgDao;
import com.weavers.duqhan.dao.ProductPropertiesDao;
import com.weavers.duqhan.dao.ProductPropertiesMapDao;
import com.weavers.duqhan.dao.ProductPropertyvaluesDao;
import com.weavers.duqhan.dao.TemtproductlinklistDao;
import com.weavers.duqhan.dao.VendorDao;
import com.weavers.duqhan.domain.Category;
import com.weavers.duqhan.domain.Product;
import com.weavers.duqhan.domain.ProductImg;
import com.weavers.duqhan.domain.ProductProperties;
import com.weavers.duqhan.domain.ProductPropertiesMap;
import com.weavers.duqhan.domain.ProductPropertyvalues;
import com.weavers.duqhan.domain.Temtproductlinklist;
import com.weavers.duqhan.dto.AxpProductDto;
import com.weavers.duqhan.dto.SkuVal;
import com.weavers.duqhan.dto.StatusBean;
import com.weavers.duqhan.util.CurrencyConverter;
import com.weavers.duqhan.util.GoogleBucketFileUploader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Android-3
 */
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductDao productDao;
    @Autowired
    CategoryDao categoryDao;
    @Autowired
    ProductImgDao productImgDao;
    @Autowired
    VendorDao vendorDao;
    @Autowired
    TemtproductlinklistDao temtproductlinklistDao;
    @Autowired
    ProductPropertiesDao productPropertiesDao;
    @Autowired
    ProductPropertyvaluesDao productPropertyvaluesDao;
    @Autowired
    ProductPropertiesMapDao productPropertiesMapDao;

   
    @Override
    public void loadTempProducts(List<StatusBean> statusBeans) {
        boolean isSuccess = true;
        String startDate = new Date().toString();
        Logger.getLogger(ProductServiceImpl.class.getName()).log(Level.SEVERE, "(==I==)DATE: " + startDate + "Store product details in temp product table start.....");
        try {
            String status = "";
            for (StatusBean statusBean : statusBeans) {
                status = "Link duplicate";
                Temtproductlinklist temtproductlinklist = temtproductlinklistDao.loadById(statusBean.getId());
                if (temtproductlinklist != null && temtproductlinklist.getStatus() == 0) {
                    Product testProduct = productDao.getProductByExternelLink(temtproductlinklist.getLink());
                    if (testProduct == null) {
                        String value = "";
                        Elements detailMain;
                        Elements detailSub;
                        Elements specifics;
                        double votes = 0.0;
                        double stars = 0.0;
                        double feedback = 0.0;
                        String url = temtproductlinklist.getLink();
                        try {
                            testProduct = new Product();
                            Product savedTestProduct;

                            //=================== Random sleep START ===================//
//                            TimeUnit.SECONDS.sleep(30 + (int) (Math.random() * 100));
                            Random randomObj = new Random();
                            TimeUnit.SECONDS.sleep(randomObj.ints(30, 60).findFirst().getAsInt());
                            //=================== Random sleep END =====================//

                            Document doc = Jsoup.connect(url).get();
                            detailMain = doc.select("#j-detail-page");
                            if (!detailMain.isEmpty()) {

                                //=================== Criteria Block START==================//
                                detailMain = doc.select(".rantings-num");
                                if (!detailMain.isEmpty()) {
                                    votes = Double.valueOf(detailMain.text().split(" votes")[0].split("\\(")[1]);
                                }
                                detailMain = doc.select(".percent-num");
                                if (!detailMain.isEmpty()) {
                                    stars = Double.valueOf(detailMain.text());
                                }
                                detailMain = doc.select("ul.ui-tab-nav li[data-trigger='feedback'] a");
                                if (!detailMain.isEmpty()) {
                                    feedback = Double.valueOf(detailMain.text().split("\\(")[1].split("\\)")[0]);
                                }
                                //=================== Criteria Block END==================//

                                if (votes > 10.0 && stars > 4.0 && feedback > 4.0) {
                                    detailMain = doc.select(".detail-wrap .product-name");
                                    testProduct.setName(detailMain.text());/*.substring(0, Math.min(detailMain.text().length(), 50))*/
                                    detailMain = doc.select(".detail-wrap .product-name");
                                    testProduct.setDescription(detailMain.text());
                                    testProduct.setExternalLink(url);
                                    testProduct.setVendorId(1l);//??????????????????????

                                    //=================== Packaging block START==================//
                                    Double weight = 1.0;
                                    Double width = 1.0;
                                    Double height = 1.0;
                                    Double length = 1.0;
                                    detailMain = doc.select("div#j-product-desc div.pnl-packaging-main ul li.packaging-item");
                                    for (Element element : detailMain) {
                                        String packagingTitle = element.select("span.packaging-title").text();
                                        String packagingDesc = element.select("span.packaging-des").text();
                                        if (packagingTitle.trim().equals("Package Weight:")) {
                                            String str = packagingDesc;
                                            str = str.replaceAll("[^.?0-9]+", " ");
                                            if (Arrays.asList(str.trim().split(" ")) != null) {
                                                if (!Arrays.asList(str.trim().split(" ")).isEmpty()) {
                                                    try {
                                                        weight = Double.parseDouble(Arrays.asList(str.trim().split(" ")).get(0));
                                                    } catch (Exception e) {
                                                        weight = 1.0;
                                                    }
                                                }
                                            }
                                            System.out.println("weight == " + weight);
                                        } else if (packagingTitle.trim().equals("Package Size:")) {
                                            String str = packagingDesc;
                                            str = str.replaceAll("[^.?0-9]+", " ");
                                            if (Arrays.asList(str.trim().split(" ")) != null) {
                                                if (!Arrays.asList(str.trim().split(" ")).isEmpty()) {
                                                    try {
                                                        width = Double.parseDouble(Arrays.asList(str.trim().split(" ")).get(0));
                                                        height = Double.parseDouble(Arrays.asList(str.trim().split(" ")).get(1));
                                                        length = Double.parseDouble(Arrays.asList(str.trim().split(" ")).get(2));
                                                    } catch (Exception e) {
                                                        width = 1.0;
                                                        height = 1.0;
                                                        length = 1.0;
                                                    }
                                                }
                                            }
                                            System.out.println("width == " + width);
                                            System.out.println("height == " + height);
                                            System.out.println("length == " + length);
                                        }
                                    }
                                    //=================== Packaging block END==================//

                                    //=================== Category block START==================//
                                    detailMain = doc.select("div.ui-breadcrumb div.container a");
                                    Long productCategoryId = 0L;
                                    String parentPath = "";
                                    String thisCategory = detailMain.last().text().trim();
                                    System.out.println("thisCategory == " + thisCategory);
                                    Category parentCategory = new Category();
                                    parentCategory.setId(0L);
                                    parentCategory.setParentPath("");
                                    for (Element element : detailMain) {
                                        String newCategory;
                                        newCategory = element.text().trim();
                                        System.out.println("newCategory======" + newCategory);
                                        if (newCategory.equals("Home") || newCategory.equals("All Categories")) {
                                        } else {
                                            Category category = categoryDao.getCategoryByName(newCategory);
                                            if (category != null) {
                                                if (category.getName().equals(thisCategory)) {
                                                    productCategoryId = category.getId();
                                                    parentPath = category.getParentPath();
                                                }
                                                parentCategory = category;
                                            } else {
                                                category = new Category();
                                                category.setId(null);
                                                category.setName(newCategory);
                                                category.setParentId(parentCategory.getId());
                                                category.setParentPath(parentCategory.getParentPath() + parentCategory.getId() + "=");
                                                category.setQuantity(0);
                                                Category category2 = categoryDao.save(category);
                                                if (category.getName().equals(thisCategory)) {
                                                    productCategoryId = category2.getId();
                                                    parentPath = category2.getParentPath();
                                                }
                                                parentCategory = category2;
                                            }
                                        }
                                    }
                                    //=================== Category block END==================//

                                    //=============== Specifications block START==============//
                                    detailMain = doc.select(".product-property-list .property-item");
                                    String specifications = "";
                                    for (Element element : detailMain) {
                                        specifications = specifications + element.select(".propery-title").get(0).text().replace(",", "/").replace(":", "-") + ":" + element.select(".propery-des").get(0).text().replace(",", "/").replace(":", "-") + ",";//TODO:, check
                                    }
                                    //=============== Specifications Block END==============//

                                    //=============== Shipping Time Block START==============//
                                    String shippingTime = "";
                                    detailMain = doc.select(".shipping-days[data-role='delivery-days']");
                                    System.out.println("value detailMain" + detailMain.toString());
                                    shippingTime = detailMain.text();
                                    //=============== Shipping Time Block END==============//

                                    //=============== Shipping Cost Block START==============//
                                    detailMain = doc.select(".logistics-cost");
                                    value = detailMain.text();
                                    if (!value.equalsIgnoreCase("Free Shipping")) {
//                                        f = 0.00;
                                    } else {
//                                        f = Double.parseDouble(value.replaceAll(".*?([\\d.]+).*", "$1"));
                                    }
                                    //=============== Shipping Cost Block END==============//

                                    //=================Product save 1st START==============//
                                    testProduct.setCategoryId(productCategoryId);
                                    testProduct.setLastUpdate(new Date());
                                    testProduct.setParentPath(parentPath);
                                    testProduct.setImgurl("-");
                                    testProduct.setProperties("-");
                                    testProduct.setProductWidth(width);
                                    testProduct.setProductLength(length);
                                    testProduct.setProductWeight(weight);
                                    testProduct.setProductHeight(height);
                                    testProduct.setShippingRate(0.0);
                                    testProduct.setShippingTime("45");
                                    testProduct.setSpecifications(specifications);
                                    savedTestProduct = productDao.save(testProduct);
                                    //====================Product save 1st END==============//

                                    //========= Property, Property Value, Property Product Map Block START ========//
                                    double discountPrice = 0.0;
                                    double actualPrice = 0.0;
                                    double markupPrice = 0.0;
                                    String id = "";
                                    String allProperties = "";
                                    //------------------------Read Color css START---------------------//
                                    specifics = doc.select("#j-product-info-sku dl.p-property-item");
                                    Elements cssdetailMain = doc.select("link[href]");
                                    Document cssdoc = new Document("");
                                    System.out.println("====================================================cssdetailMain" + cssdetailMain.size());
                                    for (Element element : cssdetailMain) {
                                        String cssurl = element.attr("abs:href");
                                        if (cssurl.contains("??main-detail")) {
                                            try {
                                                cssdoc = Jsoup.connect(cssurl).get();
                                            } catch (IOException ex) {

                                            }
                                            break;
                                        }
                                    }
                                    //-----------------------Read Color css END--------------------------//

                                    //-----------Product Property, Property Value START--------//
                                    Map<String, ProductPropertyvalues> propertyValuesMap = new HashMap<>();
                                    if (!specifics.isEmpty()) {
                                        ProductProperties testPorperties;
                                        ProductProperties saveTestPorperties;
                                        ProductPropertyvalues testPropertyValues;
                                        for (Element specific : specifics) {
                                            System.out.println("head  ==== " + specific.select("dt").text());
                                            testPorperties = productPropertiesDao.loadByName(specific.select("dt").text());
                                            if (testPorperties == null) {
                                                testPorperties = new ProductProperties();
                                                testPorperties.setPropertyName(specific.select("dt").text());
                                                saveTestPorperties = productPropertiesDao.save(testPorperties);
                                            } else {
                                                saveTestPorperties = testPorperties;
                                            }
                                            allProperties = allProperties + saveTestPorperties.getId().toString() + "-";
                                            detailSub = specific.select("dd ul li");
                                            String valu = "-";
                                            for (Element element : detailSub) {
                                                testPropertyValues = new ProductPropertyvalues();
                                                id = element.select("a[data-sku-id]").attr("data-sku-id").trim();
                                                testPropertyValues.setRefId(id);
                                                if (element.hasClass("item-sku-image")) {
                                                    valu = element.select("a img[src]").get(0).absUrl("src").split(".jpg")[0] + ".jpg";
                                                    String title = element.select("a img").get(0).attr("title");
                                                    String imgUrl = GoogleBucketFileUploader.uploadProductImage(valu, savedTestProduct.getId());
                                                    valu = "<img src='" + imgUrl + "' title='" + title + "' style='height:40px; width:40px;'/>";
                                                } else if (element.hasClass("item-sku-color")) {
                                                    String style = cssdoc.html().split("sku-color-" + id)[1].split("}")[0].substring(1);
                                                    valu = "<span style='" + style + "' ; height:40px; width:40px; display:block;'></span>";
                                                } else {
                                                    valu = element.select("a span").toString();
                                                }
                                                System.out.println("valu === " + valu);
                                                testPropertyValues.setProductId(savedTestProduct.getId());
                                                testPropertyValues.setPropertyId(saveTestPorperties.getId());
                                                testPropertyValues.setValueName(valu);
                                                propertyValuesMap.put(id, productPropertyvaluesDao.save(testPropertyValues));
                                            }
                                        }
                                        savedTestProduct.setProperties(allProperties);
                                    }
                                    //-----------Product Property, Property Value END--------//

                                    //----------------------Read json START------------------//
                                    List<AxpProductDto> axpProductDtos = new ArrayList<>();
                                    Elements scripts = doc.select("script"); // Get the script part
                                    for (Element script : scripts) {
                                        if (script.html().contains("var skuProducts=")) {
                                            String jsonData = "";
                                            jsonData = script.html().split("var skuProducts=")[1].split("var GaData")[0].trim();
                                            jsonData = jsonData.substring(0, jsonData.length() - 1);
                                            Gson gsonObj = new Gson();
                                            axpProductDtos = Arrays.asList(gsonObj.fromJson(jsonData, AxpProductDto[].class));
                                            break;
                                        }
                                    }
                                    //----------------------Read json END------------------//

                                    //-------------Product Properties Map START------------//
                                    for (AxpProductDto thisAxpProductDto : axpProductDtos) {
                                        SkuVal skuVal = thisAxpProductDto.getSkuVal();
                                        if (skuVal.getActSkuCalPrice() != null) {
                                            value = skuVal.getActSkuCalPrice().trim();
                                            discountPrice = CurrencyConverter.usdTOinr(Double.parseDouble(value.replaceAll(".*?([\\d.]+).*", "$1")));
                                            value = skuVal.getSkuCalPrice().trim();
                                            actualPrice = CurrencyConverter.usdTOinr(Double.parseDouble(value.replaceAll(".*?([\\d.]+).*", "$1")));
                                            markupPrice = discountPrice * 0.15 + 100;
                                            discountPrice = Math.ceil((discountPrice + markupPrice) / 10) * 10;
                                            actualPrice = Math.round(actualPrice + markupPrice);
                                        } else {
                                            discountPrice = 0.0;
                                            value = skuVal.getSkuCalPrice().trim();
                                            actualPrice = CurrencyConverter.usdTOinr(Double.parseDouble(value.replaceAll(".*?([\\d.]+).*", "$1")));
                                            markupPrice = actualPrice * 0.15 + 100;
                                            discountPrice = Math.round(actualPrice + markupPrice);
                                            actualPrice = Math.round(actualPrice + markupPrice);
                                        }

                                        ProductPropertiesMap productPropertyMap = new ProductPropertiesMap();
                                        String myPropValueIds = "";
                                        if (thisAxpProductDto.getSkuAttr() != null) {
                                            String[] skuPropIds = thisAxpProductDto.getSkuPropIds().split(",");
                                            for (String skuPropId : skuPropIds) {
                                                myPropValueIds = myPropValueIds + propertyValuesMap.get(skuPropId).getId().toString() + "_";
                                            }

                                            productPropertyMap.setPropertyvalueComposition(myPropValueIds);
                                        } else {
                                            productPropertyMap.setPropertyvalueComposition("_");
                                        }
                                        productPropertyMap.setDiscount(discountPrice);
                                        productPropertyMap.setPrice(actualPrice);
                                        productPropertyMap.setProductId(savedTestProduct.getId());
                                        productPropertyMap.setQuantity(5l);
                                        productPropertiesMapDao.save(productPropertyMap);
                                    }
                                    //-------------Product Properties Map START------------//
                                    //========= Property, Property Value, Property Product Map Block END ========//

                                    //============= Multiple Image Block START =============//
                                    detailMain = doc.select("ul.image-thumb-list span.img-thumb-item img[src]");
                                    int flg = 0;
                                    String imgUrl = "";
                                    for (Element element : detailMain) {
                                        imgUrl = GoogleBucketFileUploader.uploadProductImage(element.absUrl("src").split(".jpg")[0] + ".jpg", savedTestProduct.getId());
                                        if (flg == 0) {
                                            flg++;
                                            savedTestProduct.setImgurl(imgUrl);
                                        } else {
                                            ProductImg productImg = new ProductImg();
                                            productImg.setId(null);
                                            productImg.setImgUrl(imgUrl);
                                            productImg.setProductId(savedTestProduct.getId());
                                            productImgDao.save(productImg);
                                        }
                                    }
                                    //============= Multiple Image Block END =============//

                                    //=================Product save final START==============//
                                    if (productDao.save(savedTestProduct) != null) {
                                        temtproductlinklist.setStatus(1);//
                                        temtproductlinklistDao.save(temtproductlinklist);
                                        status = "Success";
                                    }
                                    //=================Product save final START==============//
                                } else {
                                    temtproductlinklist.setStatus(2);//
                                    temtproductlinklistDao.save(temtproductlinklist);
                                    status = "criteria mismatch";
                                }
                            } else {
                                status = "Page not found";
                            }
                        } catch (Exception ex) {
                            System.out.println("=============================================================Exception1" + ex);
                            temtproductlinklist.setStatus(4);//
                            temtproductlinklistDao.save(temtproductlinklist);
                            System.out.println("Exception === " + ex);
                            status = "Failure";
                            Logger.getLogger(ProductServiceImpl.class.getName()).log(Level.SEVERE, "(==E==)DATE: " + new Date().toString() + "Store product details in temp product table get error in sub process.....\n Link Id: " + statusBean.getId() + "\n Started on" + startDate, ex);
                        }
                    } else {
                        temtproductlinklist.setStatus(3);//
                        temtproductlinklistDao.save(temtproductlinklist);
                        status = "Product exsist";
                    }
                }
//                String body = "Id: " + temtproductlinklist.getId() + "<br/> Status: " + status;
//                MailSender.sendEmail("krisanu.nandi@pkweb.in", "Product captured", body, "subhendu.sett@pkweb.in");
                statusBean.setStatus(status);
            }
            System.out.println("=============================================================status" + status);
        } catch (Exception e) {
            System.out.println("=============================================================Exception2" + e);
            isSuccess = false;
            String body = "(==E==)DATE: " + new Date().toString() + "Store product details in temp product table get error.....<br/> Started on" + startDate + "<br/>";
            Logger.getLogger(ProductServiceImpl.class.getName()).log(Level.SEVERE, body, e);
//            MailSender.sendEmail("krisanu.nandi@pkweb.in", "Stopped store product details", body + e.getLocalizedMessage(), "subhendu.sett@pkweb.in");
        }
        if (isSuccess) {
            String body = "(==I==)DATE: " + new Date().toString() + "Store product details in temp product table end.....<br/> Started on" + startDate;
            Logger.getLogger(ProductServiceImpl.class.getName()).log(Level.SEVERE, body);
            /*ObjectMapper mapper = new ObjectMapper();
            try {
                MailSender.sendEmail("krisanu.nandi@pkweb.in", "Completed store product details", body + "=============<br/><br/>" + mapper.writeValueAsString(statusBeans), "subhendu.sett@pkweb.in");
            } catch (JsonProcessingException ex) {
                Logger.getLogger(ProductServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }*/
        }
//        return statusBeans;
        System.out.println("=============================================================end");
    }

}
