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
public class ScheduledTasks3 {
	@Autowired
    private ProductDao productDao;
    @Autowired
    private TemtproductlinklistDao temtproductlinklistDao;
    @Autowired
    private ProductPropertiesDao productPropertiesDao;
    @Autowired
    private ProductPropertyvaluesDao productPropertyvaluesDao;
    @Autowired
    private ProductPropertiesMapDao productPropertiesMapDao;
    
    //@Scheduled(fixedRate=3*60*1000)
    public void loadTempProducts() {
    	 boolean isSuccess = true;
         String startDate = new Date().toString();
         Logger.getLogger(ScheduledTasks3.class.getName()).log(Level.INFO, "Starting  Scheduler 2");
         try {
             String status = "";
             int i = 0;
             while (i < 100) {
             	
                 status = "Link duplicate";
                 //temtproductlist = 722058
                 // productid = 562943
                 //http://duqhan.com/#/store/product/562943/overview
                 Temtproductlinklist temtproductlinklist = temtproductlinklistDao.getTempProductToRecrawl();
                 if(temtproductlinklist == null) break;
                 //Temtproductlinklist temtproductlinklist = temtproductlinklistDao.loadById(statusBean.getId());
                 if (temtproductlinklist != null && temtproductlinklist.getStatus() == 7) {
                 	 Logger.getLogger(ScheduledTasks3.class.getName()).log(Level.INFO, "Starting  Scheduler 2" + temtproductlinklist.getLink() );
                     Product testProduct = productDao.getProductByExternelLink(temtproductlinklist.getLink());
                     if (testProduct != null) {
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
                             //Product savedTestProduct;
                
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
                                                 testPropertyValues = productPropertyvaluesDao.loadByProductIdAndPropertyIdAndRefId(testProduct.getId(),
                                                  		  saveTestPorperties.getId(),id);
                                                 if(testPropertyValues !=null) {
                                                  if (element.hasClass("item-sku-color")) {
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
                                                     testPropertyValues.setValueName(valu);
                                                     testPropertyValues = productPropertyvaluesDao.save(testPropertyValues);
                                                  }
                                                 }
                                                 
                                                 propertyValuesMap.put(id, testPropertyValues);
                                             }
                                         }
                                         
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
                                             discountPrice = CurrencyConverter.usdTOinr(Double.parseDouble(value.replaceAll(".*?([\\d.]+).*", "$1")));
                                             value = skuVal.getSkuCalPrice().trim().replaceAll(",", "");
                                             actualPrice = CurrencyConverter.usdTOinr(Double.parseDouble(value.replaceAll(".*?([\\d.]+).*", "$1")));
                                             markupPrice = discountPrice * 0.15 + 100;
                                             discountPrice = Math.ceil((discountPrice + markupPrice) / 10) * 10;
                                             actualPrice = Math.round(actualPrice + markupPrice);
                                         } else {
                                             discountPrice = 0.0;
                                             value = skuVal.getSkuCalPrice().trim().replaceAll(",", "");
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
                                         productPropertyMap = productPropertiesMapDao.loadByProductIdAndPropertyvalueComposition(testProduct.getId(),
                                        		 productPropertyMap.getPropertyvalueComposition());
                                         if(productPropertyMap != null) {
	                                         productPropertyMap.setDiscount(discountPrice);
	                                         productPropertyMap.setPrice(actualPrice);
	                                         productPropertiesMapDao.save(productPropertyMap);
                                         }
                                     }
                                 } else {
                                 }
                             } else {
                                 
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
                        temtproductlinklist.setStatus(6);
                      	temtproductlinklistDao.save(temtproductlinklist);
                             
                     } else {
                     }
                     i++;
                 }
             }
         } catch (Exception e) {
             isSuccess = false;
             String body = "(==E==)DATE: " + new Date().toString() + "Store product details in temp product table get error.....<br/> Started on" + startDate + "<br/>";
             Logger.getLogger(ScheduledTasks3.class.getName()).log(Level.SEVERE, body, e);
         }
         if (isSuccess) {
         	Logger.getLogger(ScheduledTasks3.class.getName()).log(Level.INFO, "Stopping At  Scheduler");
             
            
         }
        
    }
    
	
}
