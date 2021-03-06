package com.weavers.duqhan;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.scrapping.engine.bean.ProductDetailsLinks;
import com.weavers.duqhan.dao.CategoryDao;
import com.weavers.duqhan.dao.CurrencyRatesDao;
import com.weavers.duqhan.dao.ProductDao;
import com.weavers.duqhan.dao.ProductImgDao;
import com.weavers.duqhan.dao.ProductPropertiesDao;
import com.weavers.duqhan.dao.ProductPropertiesMapDao;
import com.weavers.duqhan.dao.ProductPropertyvaluesDao;
import com.weavers.duqhan.dao.TemtproductlinklistDao;
import com.weavers.duqhan.domain.Category;
import com.weavers.duqhan.domain.Product;
import com.weavers.duqhan.domain.ProductImg;
import com.weavers.duqhan.domain.ProductProperties;
import com.weavers.duqhan.domain.ProductPropertiesMap;
import com.weavers.duqhan.domain.ProductPropertyvalues;
import com.weavers.duqhan.domain.Temtproductlinklist;
import com.weavers.duqhan.dto.AxpProductDto;
import com.weavers.duqhan.dto.SkuVal;
import com.weavers.duqhan.util.CurrencyConverter;
import com.weavers.duqhan.util.GoogleBucketFileUploader;

@Component
public class ScheduledTasks2 {
	@Autowired
    private ProductDao productDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private ProductImgDao productImgDao;
    @Autowired
    private TemtproductlinklistDao temtproductlinklistDao;
    @Autowired
    private ProductPropertiesDao productPropertiesDao;
    @Autowired
    private ProductPropertyvaluesDao productPropertyvaluesDao;
    @Autowired
    private ProductPropertiesMapDao productPropertiesMapDao;
    @Autowired
    private CurrencyRatesDao currencyRatesDao;
    @Scheduled(fixedRate=3*60*1000)
    public void loadTempProducts() {
    	 boolean isSuccess = true;
         String startDate = new Date().toString();
         Logger.getLogger(ScheduledTasks2.class.getName()).log(Level.INFO, "Starting  Scheduler 2");
         try {
             String status = "";
             int i = 0;
             while (i < 100) {
             	
                 status = "Link duplicate";
                 Temtproductlinklist temtproductlinklist = temtproductlinklistDao.getRandomeUnprocessedTempProduct();
                 if(temtproductlinklist == null) break;
                 //Temtproductlinklist temtproductlinklist = temtproductlinklistDao.loadById(statusBean.getId());
                 if (temtproductlinklist != null && temtproductlinklist.getStatus() == 0) {
                 	 Logger.getLogger(ScheduledTasks2.class.getName()).log(Level.INFO, "Starting  Scheduler 2" + temtproductlinklist.getLink() );
                     Product testProduct = productDao.getProductByExternelLink(temtproductlinklist.getLink());
                     if (testProduct == null) {
                     	temtproductlinklist.setStatus(5);
                     	temtproductlinklistDao.save(temtproductlinklist);
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
                
                             Document doc = Utility.connect(url);
                             detailMain = doc.select("#j-detail-page");
                             if (!detailMain.isEmpty()) {

                                 //=================== Criteria Block START==================//
                                 detailMain = doc.select(".rantings-num");
                                 if (!detailMain.isEmpty()) {
                                     try {
                                 	votes = Double.valueOf(detailMain.text().split(" votes")[0].split("\\(")[1]);
                                     } catch(NumberFormatException e) {
                                     	e.printStackTrace();
                                     }
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

                                 if (1==1 || votes > 10.0 && stars > 4.0 && feedback > 4.0) {
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
                                            // System.out.println("weight == " + weight);
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
                                             //System.out.println("width == " + width);
                                             //System.out.println("height == " + height);
                                             //System.out.println("length == " + length);
                                         }
                                     }
                                     //=================== Packaging block END==================//

                                     //=================== Category block START==================//
                                     detailMain = doc.select("div.ui-breadcrumb div.container a");
                                     Long productCategoryId = 0L;
                                     String parentPath = "";
                                     String thisCategory = detailMain.last().text().trim();
                                     //System.out.println("thisCategory == " + thisCategory);
                                     Category parentCategory = new Category();
                                     parentCategory.setId(0L);
                                     parentCategory.setParentPath("");
                                     for (Element element : detailMain) {
                                         String newCategory;
                                         newCategory = element.text().trim();
                                         //System.out.println("newCategory======" + newCategory);
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
                                                 category.setImgUrl("-");
                                                 category.setDisplayText(newCategory);
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
                                     //System.out.println("value detailMain" + detailMain.toString());
                                     shippingTime = detailMain.text();
                                     //=============== Shipping Time Block END==============//

                                     //=============== Shipping Cost Block START==============//
                                     detailMain = doc.select(".logistics-cost");
                                     value = detailMain.text();
                                     if (!value.equalsIgnoreCase("Free Shipping")) {
//                                         f = 0.00;
                                     } else {
//                                         f = Double.parseDouble(value.replaceAll(".*?([\\d.]+).*", "$1"));
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
                                     //System.out.println("====================================================cssdetailMain" + cssdetailMain.size());
                                     for (Element element : cssdetailMain) {
                                         String cssurl = element.attr("href");
                                         if (cssurl.contains("??main-detail")) {
                                        	 String cssUrl = "https:"+cssurl;
                                             try {
                                                 cssdoc = Utility.connect(cssUrl);
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
                                             //System.out.println("head  ==== " + specific.select("dt").text());
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
                                                     String imgUrl =  GoogleBucketFileUploader.uploadProductImage(valu, savedTestProduct.getId());
                                                     valu = "<img src='" + imgUrl + "' title='" + title + "' style='height:40px; width:40px;'/>";
                                                 } else if (element.hasClass("item-sku-color")) {
                                                     if(cssdoc.html().length() == 0){
                                                     	 valu = element.select("a span").toString();
                                                     } else {
                                                     	if(cssdoc.html().contains("sku-color-" + id)){
                                                        	String style = cssdoc.html().split("sku-color-" + id)[1].split("}")[0].substring(1);
                                                        	valu = "<span style='" + style + ";height:40px;width:40px;display:block;'></span>";
                                                        	}else{
                                                        	String style="background:#FFF!important";
                                                        	valu = "<span style='" + style + ";height:40px;width:40px;display:block;'></span>";	
                                                        	}
                                                        	}
                                                     } else {
                                                     valu = element.select("a span").toString();
                                                 }
                                                 //System.out.println("valu === " + valu);
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
                                             value = skuVal.getActSkuCalPrice().trim().replaceAll(",", "");
                                             discountPrice = this.usdTOinr(Double.parseDouble(value.replaceAll(".*?([\\d.]+).*", "$1")));
                                             value = skuVal.getSkuCalPrice().trim().replaceAll(",", "");
                                             actualPrice = this.usdTOinr(Double.parseDouble(value.replaceAll(".*?([\\d.]+).*", "$1")));
                                             markupPrice = discountPrice * 0.15 + 100;
                                             discountPrice = Math.ceil((discountPrice + markupPrice) / 10) * 10;
                                             actualPrice = Math.round(actualPrice + markupPrice);
                                         } else {
                                             discountPrice = 0.0;
                                             value = skuVal.getSkuCalPrice().trim().replaceAll(",", "");
                                             actualPrice = this.usdTOinr(Double.parseDouble(value.replaceAll(".*?([\\d.]+).*", "$1")));
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
                                         productPropertyMap.setProductId(savedTestProduct);
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
                             temtproductlinklist.setStatus(4);//
                             StringWriter sw = new StringWriter();
                             PrintWriter pw = new PrintWriter(sw);
                             ex.printStackTrace(pw);
                             String sStackTrace = sw.toString();
                             temtproductlinklist.setError(sStackTrace);
                             temtproductlinklistDao.save(temtproductlinklist);
                             ex.printStackTrace();
                             status = "Failure";
                         }
                     } else {
                         temtproductlinklist.setStatus(3);//
                         temtproductlinklistDao.save(temtproductlinklist);
                         status = "Product exsist";
                     }
                     i++;
                 }
             }
         } catch (Exception e) {
             isSuccess = false;
             String body = "(==E==)DATE: " + new Date().toString() + "Store product details in temp product table get error.....<br/> Started on" + startDate + "<br/>";
             Logger.getLogger(ScheduledTasks2.class.getName()).log(Level.SEVERE, body, e);
         }
         if (isSuccess) {
         	Logger.getLogger(ScheduledTasks2.class.getName()).log(Level.INFO, "Stopping At  Scheduler");
             
            
         }
        
    }
    
	@Async
	private  void saveLinksToDB(List<ProductDetailsLinks> productDetailsLinks, String urlOfListPage) {
		System.out.println("Saving" + urlOfListPage);
		for (ProductDetailsLinks link : productDetailsLinks) {
			Temtproductlinklist temtproductlinklist = temtproductlinklistDao.getTemtproductlinklistByLink(link.getProductDetailLink());
	        if (temtproductlinklist == null) {
	            temtproductlinklist = new Temtproductlinklist();
	            temtproductlinklist.setLink(link.getProductDetailLink());
	            temtproductlinklist.setStatus(0);
	            temtproductlinklistDao.save(temtproductlinklist);
	        }
		}
		System.out.println("Done" + urlOfListPage);
		
	}
	 public Double usdTOinr(Double usdValue) {
	        try {
	        	com.weavers.duqhan.domain.CurrencyRates currencyRates=currencyRatesDao.getCurrencyRates("USDTOINR");
	        	Double inrValue = currencyRates.getRates();
	            return Double.valueOf(String.valueOf(inrValue * usdValue));
	        } catch (Exception e) {
	            return null;
	        }
	    }
}
