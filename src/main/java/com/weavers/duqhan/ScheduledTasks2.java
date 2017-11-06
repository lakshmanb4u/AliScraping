package com.weavers.duqhan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

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
import com.weavers.duqhan.dao.ColorDao;
import com.weavers.duqhan.dao.TempProductDao;
import com.weavers.duqhan.dao.TempProductImgDao;
import com.weavers.duqhan.dao.TempProductSizeColorMapDao;
import com.weavers.duqhan.dao.TemtproductlinklistDao;
import com.weavers.duqhan.domain.Category;
import com.weavers.duqhan.domain.Color;
import com.weavers.duqhan.domain.TempProduct;
import com.weavers.duqhan.domain.TempProductImg;
import com.weavers.duqhan.domain.TempProductSizeColorMap;
import com.weavers.duqhan.domain.Temtproductlinklist;
import com.weavers.duqhan.dto.AxpProductDto;
import com.weavers.duqhan.dto.ImageDto;
import com.weavers.duqhan.dto.ProductBean;
import com.weavers.duqhan.dto.SizeColorMapDto;
import com.weavers.duqhan.dto.SkuVal;
import com.weavers.duqhan.util.CurrencyConverter;

@Component
public class ScheduledTasks2 {
	
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private TemtproductlinklistDao temtproductlinklistDao;
    @Autowired
    private TempProductDao tempProductDao;
    @Autowired
    private ColorDao colorDao;
    @Autowired
    private TempProductSizeColorMapDao tempProductSizeColorMapDao;
    @Autowired
    private TempProductImgDao tempProductImgDao;
    
    @Scheduled(fixedRate=3*60*1000)
    public void loadTempProducts() {
//        List<Temtproductlinklist> temtproductlinklists = temtproductlinklistDao.getUnprocessedTempProduct();
        String status = "failure";
        int i = 0;
        while (i < 100) {
            status = "failure";
            Temtproductlinklist temtproductlinklist = temtproductlinklistDao.getRandomeUnprocessedTempProduct();
            //if(temtproductlinklist == null) break;
            //Temtproductlinklist temtproductlinklist = temtproductlinklistDao.loadById(statusBean.getId());
            if (temtproductlinklist != null && temtproductlinklist.getStatus() == 0) {
                TempProduct tempProduct = tempProductDao.getProductByExternelLink(temtproductlinklist.getLink());
                if (tempProduct == null) {
                	temtproductlinklist.setStatus(5);
                	temtproductlinklistDao.save(temtproductlinklist);
                    ProductBean productBean = null;
                    String value = "";
                    Elements detailMain;
                    Elements detailSub;
                    Elements specifics;
                    double votes = 0.0;
                    double stars = 0.0;
                    double feedback = 0.0;
                    String url = temtproductlinklist.getLink();
                    try {
                        productBean = new ProductBean();
                        Document doc = Utility.connect(url);
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
                        if (votes > 20.0 && stars > 4.8 && feedback > 4.0) {
                            detailMain = doc.select(".detail-wrap .product-name");
                            productBean.setName(detailMain.text().substring(0, Math.min(detailMain.text().length(), 50)));

                            detailMain = doc.select(".detail-wrap .product-name");
                            productBean.setDescription(detailMain.text());

                            detailMain = doc.select(".detail-wrap .product-name");
                            productBean.setDescription(detailMain.text());

                            productBean.setVendorId(3l);//??????????????????????
                            detailMain = doc.select("div.ui-breadcrumb div.container a");
                            String newCategory = detailMain.last().text();
                            System.out.println("newCategory == " + newCategory);
                            Category category = categoryDao.getCategoryByName(newCategory);
                            if (category != null) {
                                productBean.setCategoryId(category.getId());
                            } else {
                                Category parentCategory = categoryDao.getCategoryByName("Jewellery");
                                category = new Category();
                                category.setId(null);
                                category.setName(newCategory);
                                category.setParentId(parentCategory.getId());
                                category.setParentPath(parentCategory.getParentPath() + parentCategory.getId() + "=");
                                Category category2 = categoryDao.save(category);
                                productBean.setCategoryId(category2.getId());
                            }

                            productBean.setExternalLink(url);

                            detailMain = doc.select(".product-property-list .property-item");
                            String specifications = "";
                            for (Element element : detailMain) {
                                specifications = specifications + element.select(".propery-title").get(0).text().replace(",", "/").replace(":", "-") + ":" + element.select(".propery-des").get(0).text().replace(",", "/").replace(":", "-") + ",";//TODO:, check
                            }
                            productBean.setSpecifications(specifications);

                            detailMain = doc.select(".shipping-days[data-role='delivery-days']");
                            System.out.println("value detailMain" + detailMain.toString());
                            value = detailMain.text();
//            productBean.setShippingTime(value);
                            productBean.setShippingTime("45");

                            detailMain = doc.select(".logistics-cost");
                            value = detailMain.text();
                            double discountPrice = 0.0;
                            double actualPrice = 0.0;
                            double markupPrice = 0.0;
                            if (!value.equalsIgnoreCase("Free Shipping")) {
//                f = Double.parseDouble(value.replaceAll(".*?([\\d.]+).*", "$1"));
                            }
                            productBean.setShippingRate(0.0);

                            //====multiple ProductSizeColorMap added=======//
                            //---------------------
                            int flag = 0;
                            specifics = doc.select("#j-product-info-sku dl.p-property-item");
                            List<String> arrPropIds = new ArrayList<>();
                            List<String> arrPropIds2 = new ArrayList<>();
                            HashMap<String, AxpProductDto> AxpProductMap = new HashMap();
                            String id = "";
                            for (Element specific : specifics) {
                                detailSub = specific.select("dd ul li");
                                System.out.println("head  ==== " + specific.select("dt").text());
                                for (Element element : detailSub) {
                                    if (flag > 0) {
                                        id = element.select("a[data-sku-id]").attr("data-sku-id").trim();

                                        Iterator<String> iter = arrPropIds.iterator();
                                        while (iter.hasNext()) {
                                            String arrPropId = iter.next();
                                            iter.remove();
                                            arrPropId = arrPropId + "," + id;
                                            arrPropIds2.add(arrPropId);
                                        }
                                        arrPropIds.addAll(arrPropIds2);
                                        arrPropIds2.removeAll(arrPropIds);
                                        break;
                                    } else {
                                        arrPropIds.add(element.select("a[data-sku-id]").attr("data-sku-id").trim());
                                        if (element.hasClass("item-sku-image")) {
//                            System.out.println("img== " + element.select("a img").attr("title"));
                                        } else {
//                            System.out.println("span== " + element.select("a span").toString());
                                        }
                                    }
                                }
                                flag++;
                            }
                            List<AxpProductDto> axpProductDtos = new ArrayList<>();
                            Elements scripts = doc.select("script"); // Get the script part
                            for (Element script : scripts) {
                                if (script.html().contains("var skuProducts=")) {
                                    String jsonData = "";
                                    jsonData = script.html().split("var skuProducts=")[1].split("var GaData")[0].trim();
                                    jsonData = jsonData.substring(0, jsonData.length() - 1);
                                    //System.out.println("script ======= " + jsonData);
                                    Gson gsonObj = new Gson();
                                    axpProductDtos = Arrays.asList(gsonObj.fromJson(jsonData, AxpProductDto[].class));
                                    for (AxpProductDto axpProductDto : axpProductDtos) {
                                        if (arrPropIds.contains(axpProductDto.getSkuPropIds())) {//if prisent in id list
                                            AxpProductMap.put(axpProductDto.getSkuPropIds(), axpProductDto);
                                        }
                                    }
                                }
                            }
                            //---------------------
                            List<SizeColorMapDto> sizeColorMapDtos = new ArrayList<>();
                            for (HashMap.Entry<String, AxpProductDto> entry : AxpProductMap.entrySet()) {
                                String thisId = entry.getKey();
                                AxpProductDto thisAxpProductDto = entry.getValue();
                                SkuVal skuVal = thisAxpProductDto.getSkuVal();
                                if (skuVal.getActSkuCalPrice() != null) {
                                    value = skuVal.getActSkuCalPrice().trim();
                                    discountPrice = CurrencyConverter.usdTOinr(Double.parseDouble(value.replaceAll(".*?([\\d.]+).*", "$1")));
                                    value = skuVal.getSkuCalPrice().trim();
                                    actualPrice = CurrencyConverter.usdTOinr(Double.parseDouble(value.replaceAll(".*?([\\d.]+).*", "$1")));
                                    markupPrice = discountPrice * 0.15 + 100;
                                    discountPrice = Math.ceil((discountPrice + markupPrice) / 10) * 10;;
                                    actualPrice = Math.round(actualPrice + markupPrice);
                                } else {
                                    discountPrice = 0.0;
                                    value = skuVal.getSkuCalPrice().trim();
                                    actualPrice = CurrencyConverter.usdTOinr(Double.parseDouble(value.replaceAll(".*?([\\d.]+).*", "$1")));
                                    markupPrice = actualPrice * 0.15 + 100;
                                    discountPrice = Math.round(actualPrice + markupPrice);
                                    actualPrice = Math.round(actualPrice + markupPrice);
                                }

                                SizeColorMapDto sizeColorMapDto = new SizeColorMapDto();

                                if (thisAxpProductDto.getSkuAttr().split("#").length > 1) {
                                    Color color = new Color();
                                    color.setName(thisAxpProductDto.getSkuAttr().split("#")[1].split(";")[0]);
                                    color.setCode(" ");
                                    sizeColorMapDto.setColorId(colorDao.save(color).getId()/*null*/);//?????????????????????
                                } else {
                                    sizeColorMapDto.setColorId(null);//?????????????????????
                                }
                                sizeColorMapDto.setSizeId(null);//??????????????????????
//            TimeUnit.SECONDS.sleep(10);
                                detailMain = doc.select("#j-sku-price");
                                System.out.println("value2 detailMain" + detailMain.text());
                                /*value = detailMain.text();/////??????????????????????*/
//                value = "0.25";
                                sizeColorMapDto.setSalesPrice(discountPrice);
                                sizeColorMapDto.setOrginalPrice(actualPrice);
                                sizeColorMapDto.setCount(1l);
                                sizeColorMapDto.setProductWidth(1.0);
                                sizeColorMapDto.setProductLength(1.0);
                                sizeColorMapDto.setProductWeight(1.0);
                                sizeColorMapDto.setProductHeight(1.0);
                                sizeColorMapDtos.add(sizeColorMapDto);
                            }
                            productBean.setSizeColorMaps(sizeColorMapDtos);

                            //===========multiple image add==========//
                            List<ImageDto> imageDtos = new ArrayList<>();//loop
                            detailMain = doc.select("ul.image-thumb-list span.img-thumb-item img[src]");
                            int flg = 0;
                            for (Element element : detailMain) {
                                if (flg == 0) {
                                    flg++;
                                    productBean.setImgurl(element.absUrl("src").split(".jpg")[0] + ".jpg");
                                } else {
                                    ImageDto imageDto = new ImageDto();
                                    imageDto.setImgUrl(element.absUrl("src").split(".jpg")[0] + ".jpg");
                                    imageDtos.add(imageDto);
                                }
                            }
                            productBean.setImageDtos(imageDtos);
                            if (this.saveTempProduct(productBean).equals("success")) {
                                temtproductlinklist.setStatus(1);//
                                temtproductlinklistDao.save(temtproductlinklist);
                                status = "success";
                            }
                        } else {
                            temtproductlinklist.setStatus(2);//
                            temtproductlinklistDao.save(temtproductlinklist);
                            status = "criteria mismatch";
                        }
                    } catch (Exception ex) {
                        System.out.println("Exception === " + ex);
                        java.util.logging.Logger.getLogger(ScheduledTasks2.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    temtproductlinklist.setStatus(3);//
                    temtproductlinklistDao.save(temtproductlinklist);
                    status = "product exsist";
                }
            }
            
        }
        
    }
    
    private String saveTempProduct(ProductBean productBean) {
        String status = "ERROR: Product can not be saved!!";
        if (productBean != null) {
            Category parentCategory = categoryDao.loadById(productBean.getCategoryId());//>>>>>>>>>>>>>>
            TempProduct product = new TempProduct();
            product.setId(null);
            product.setName(productBean.getName());//>>>>>>>>>>>>>>>>>
            product.setImgurl(productBean.getImgurl());//>>>>>>>>>>>>>>>>
            product.setCategoryId(productBean.getCategoryId());//>>>>>>>>>>>
            product.setDescription(productBean.getDescription());//>>>>>>>>>>>>>>>>
            product.setLastUpdate(new Date());
            product.setVendorId(productBean.getVendorId());     //>>>>>>>>>>>>>>>>>
            product.setParentPath(parentCategory.getParentPath());
            product.setExternalLink(productBean.getExternalLink()); //>>>>>>>>>>>>>>
            product.setSpecifications(productBean.getSpecifications());//>>>>>>>>>>>>
            product.setShippingTime(productBean.getShippingTime());
            product.setShippingRate(productBean.getShippingRate());
            TempProduct product1 = tempProductDao.save(product);
            if (product1 != null) {
                //====multiple ProductSizeColorMap added=======//
                List<SizeColorMapDto> sizeColorMapDtos = productBean.getSizeColorMaps();//>>>>>>>>>>>>>>
                if (!sizeColorMapDtos.isEmpty()) {
                    for (SizeColorMapDto sizeColorMapDto : sizeColorMapDtos) {
                        TempProductSizeColorMap sizeColorMap = new TempProductSizeColorMap();
                        sizeColorMap.setId(null);
                        sizeColorMap.setColorId(sizeColorMapDto.getColorId());
                        sizeColorMap.setSizeId(sizeColorMapDto.getSizeId());
                        sizeColorMap.setDiscount(sizeColorMapDto.getSalesPrice());
                        sizeColorMap.setPrice(sizeColorMapDto.getOrginalPrice());
                        sizeColorMap.setQuantity(sizeColorMapDto.getCount());
                        sizeColorMap.setProductId(product1.getId());
                        sizeColorMap.setProductHeight(sizeColorMapDto.getProductHeight());
                        sizeColorMap.setProductLength(sizeColorMapDto.getProductLength());
                        sizeColorMap.setProductWeight(sizeColorMapDto.getProductWeight());
                        sizeColorMap.setProductWidth(sizeColorMapDto.getProductWidth());
                        TempProductSizeColorMap sizeColorMap1 = tempProductSizeColorMapDao.save(sizeColorMap);
                    }
                }

                //===========multiple image add==========//
                List<ImageDto> imageDtos = productBean.getImageDtos();//>>>>>>>>>>>>
                if (!imageDtos.isEmpty()) {
                    for (ImageDto imageDto : imageDtos) {
                        TempProductImg productImg = new TempProductImg();
                        productImg.setId(null);
                        productImg.setImgUrl(imageDto.getImgUrl());
                        productImg.setProductId(product1.getId());
                        tempProductImgDao.save(productImg);
                    }
                }
                status = "success";
            }
        }
        return status;
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
}
