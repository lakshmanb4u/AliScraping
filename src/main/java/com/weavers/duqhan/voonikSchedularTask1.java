package com.weavers.duqhan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.weavers.duqhan.util.GoogleBucketFileUploader;

@EnableScheduling
@Configuration

public class voonikSchedularTask1 {

	@Autowired
    TemtproductlinklistDao temtproductlinklistDao;
	@Autowired
    private ProductDao productDao;
	@Autowired
    private CategoryDao categoryDao;
	@Autowired
    private ProductPropertiesMapDao productPropertiesMapDao;
	@Autowired
    private ProductImgDao productImgDao;
	@Autowired
    private ProductPropertiesDao productPropertiesDao;
	@Autowired
    private ProductPropertyvaluesDao productPropertyvaluesDao;
	
	/*@RequestMapping(value="/api/loadUrls", method = RequestMethod.GET)
	@ResponseBody*/
	@Scheduled(fixedDelay = 1000*60000)
	public String loadUrls() {
		try {
			List<String> urlList=new ArrayList<>();
			List<ProductDetailsLinks> productDetailsLinks = new ArrayList<ProductDetailsLinks>();
			String categoryName[]= {"womens-western-wear","fusion-wear","women-lingerie-sleepwear"};
			//String categoryName[]= {"men-topwear","men-bottomwear","men-ethnic-wear","men-plus-size-menu","men-innerwear","men-sportswear"};
			int pageCount=0;
			//select page_number from myntra_temtproductlinklist where category_name="fusion-wear" order by page_number desc limit 1
			for(String item :categoryName) {
				int pageNumber = 0;
				Temtproductlinklist temtproductlinklist = temtproductlinklistDao.getPageNumberByCategoryName(item);
				if(temtproductlinklist == null) {
					pageNumber = 0;
				} else {
					pageNumber = temtproductlinklist.getPageNumber();
				}
				String mainUrl="https://www.myntra.com/"+item;
				for(int i=pageNumber;i<9999;i++){
					try {
					System.out.println(item +"  Page Number : "+ i);
					String currentUrl="https://www.myntra.com/web/v2/search/data/"+item+"?f=&p="+i+"&rows=48";
					URL apiUrl = new URL(currentUrl);
					URLConnection urlConn = apiUrl.openConnection();
					InputStreamReader inputStreamReader = new InputStreamReader(urlConn.getInputStream());
					BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
					String line = bufferedReader.readLine();
					//System.out.println("string : "+line);
					ObjectMapper om = new ObjectMapper();
					//om.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
					JsonNode json = om.readTree(line).get("data").get("results").get("products");
					//List<JsonNode> results=json.findValues("results");
					
					
					if (json.isArray() && json.size()>0) {
						pageCount++;
						for (final JsonNode objNode : json) {
							System.out.println(objNode.findValue("dre_landing_page_url").toString());
							//urlList.add(objNode.findValue("dre_landing_page_url").toString());
							  ProductDetailsLinks productLink = new ProductDetailsLinks();
							  productLink.setProductDetailLink("https://www.myntra.com/"+objNode.findValue("dre_landing_page_url").asText());
							  productLink.setStatus('I');
							  productLink.setResponsibleServer(Utility.getRandomServer());
							  productDetailsLinks.add(productLink);
						}
						saveLinksToDB(productDetailsLinks,mainUrl,0L,i);
					}
					else
						break;
					System.out.println("count : "+json.size());
					} catch(Exception e) {
						System.out.println("Exception at page number :"+i);
						e.printStackTrace();
					}
				}
			}
			System.out.println("Total Page count : "+pageCount);
			System.out.println("Total Product : "+productDetailsLinks.size());
			System.out.println("Saving to db............");
		} catch(Exception e) {
			e.printStackTrace();
		}
		/*try {
			String resp = getSingleProduct();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		return "success";
	}
	
	private  void saveLinksToDB(List<ProductDetailsLinks> productDetailsLinks, String urlOfListPage,Long id, int pageNumber) {
		
		for (ProductDetailsLinks link : productDetailsLinks) {
			Temtproductlinklist temtproductlinklist = temtproductlinklistDao.getTemtproductlinklistByLink(link.getProductDetailLink());
	        if (temtproductlinklist == null) {
	            temtproductlinklist = new Temtproductlinklist();
	            temtproductlinklist.setLink(link.getProductDetailLink());
	            temtproductlinklist.setParentUrl(id);
	            temtproductlinklist.setStatus(0);
	            temtproductlinklist.setPageNumber(pageNumber);
	            System.out.println("category name:"+urlOfListPage.split("/")[3]);
	            temtproductlinklist.setCategoryName(urlOfListPage.split("/")[3]);
	            temtproductlinklistDao.save(temtproductlinklist);
	        }
		}
		System.out.println("Done" + urlOfListPage);
	}
	
	/*@RequestMapping(value="/api/getSingleProduct", method = RequestMethod.GET)
	@ResponseBody*/
	public String getSingleProduct() throws HttpException, IOException, InterruptedException{
		Elements detailMain;
		String status = "";
		String url = "https://www.myntra.com/bodysuit/miss-chase/miss-chase-women-navy-blue-solid-bodysuit/2466284/buy";
		List<Temtproductlinklist> productsList = temtproductlinklistDao.getAllUnsavedProduct();
		for(Temtproductlinklist product: productsList) {
		url = product.getLink();
		Document doc = Utility.connect(url);
		JsonNode json = null;
		try {
	        String documentText=doc.toString();
	        String stringJson = documentText.substring(documentText.indexOf("window.__myx = ") + 14);
	        stringJson = stringJson.substring(0, stringJson.indexOf("</script> "));
	        ObjectMapper om = new ObjectMapper();
			json = om.readTree(stringJson).get("pdpData");
		} catch(Exception ex) {
			product.setStatus(4);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String sStackTrace = sw.toString();
            product.setError(sStackTrace);
            temtproductlinklistDao.save(product);
            ex.printStackTrace();
            status = "Failure";
            continue;
		}
        
		//System.out.println("\n\n "+json.toString());
		if(json != null) {
			
				 Product testProduct = productDao.getProductByExternelLink(url);
				 if(testProduct == null) {
					product.setStatus(5);
					product.setLink(url);
		          	temtproductlinklistDao.save(product);
		          	try {
					 testProduct = new Product();
					 Product savedTestProduct;
					 testProduct.setName(json.findValue("name").asText());
					 testProduct.setDescription("-");
					 for(JsonNode json4 : json.findValue("descriptors")) {
						 if(json4.findValue("title").asText().contains("description")) {
							 testProduct.setDescription(Jsoup.parse(json4.findValue("description").asText()).text());
						 }
					 }	 
					 testProduct.setExternalLink(url);
					 testProduct.setVendorId(1L);
					 
					 Double weight = 1.0;
		             Double width = 1.0;
		             Double height = 1.0;
		             Double length = 1.0;
		             
					 Long productCategoryId = 0L;
		             String parentPath = "";
		             String thisCategory = "";
		             Category parentCategory = new Category();
		             parentCategory.setId(0L);
		             parentCategory.setParentPath("");
		             String newCategory = json.findValue("analytics").findValue("articleType").asText();
		             Category womensCategory = categoryDao.getCategoryByName("Women's Clothing & Accessories");
		             Category category = categoryDao.getCategoryByNameAndId(newCategory,womensCategory.getId());
					 if (category != null && category.getParentId() == womensCategory.getId()) {
		                 productCategoryId = category.getId();
		                 parentPath = category.getParentPath();
		                 parentCategory = category;
		             } else {
		            	 if(womensCategory != null) {
		            		 parentCategory = womensCategory;
		            	 }
		                 category = new Category();
		                 category.setId(null);
		                 category.setName(newCategory);
		                 category.setParentId(parentCategory.getId());
		                 category.setParentPath(parentCategory.getParentPath() + parentCategory.getId() + "=");
		                 category.setQuantity(5);
		                 category.setImgUrl("-");
		                 category.setDisplay(false);
		                 category.setDisplayText(newCategory);
		                 Category category2 = categoryDao.save(category);
		                 productCategoryId = category2.getId();
		                 parentPath = category2.getParentPath();
		                 parentCategory = category2;
		             }
					 String material = "";
					 /*if(json.findValue("descriptors").size() > 3) {
						 material = json.findValue("descriptors").get(3).findValue("description").asText();
					 }*/
						 for(final JsonNode json4 : json.findValue("descriptors")) {
							 if(json4.findValue("title").asText().contains("materials")) {
				     				material = Jsoup.parse(json4.findValue("description").asText()).text().replaceAll(",", " ");
				     			}
						 }
					 
					 String specifications = "Brand Name-:"+json.findValue("brand").findValue("name").asText()+",Gender-:Women"+",Material-:"+material;
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
		             testProduct.setLikeUnlikeCount(0);
		             testProduct.setLinkId(0);
		             testProduct.setDeleted(false);
		             testProduct.setSpecifications(specifications);
		             savedTestProduct = productDao.save(testProduct);
		             //Map<String, ProductPropertyvalues> propertyValuesMap = new HashMap<>();
		             double discountPrice = Double.parseDouble(json.findValue("price").findValue("discounted").asText());
		             double actualPrice = Double.parseDouble(json.findValue("price").findValue("mrp").asText());;
		             double markupPrice = 0.0;
		             String id = "";
		             String allProperties = "";
		             
		             ProductProperties testPorperties;
		             ProductProperties saveTestPorperties;
		             ProductPropertyvalues testPropertyValues;
		             String propertyName = "Size:";
		             testPorperties = productPropertiesDao.loadByName(propertyName);//
		             if (testPorperties == null) {
		                 testPorperties = new ProductProperties();
		                 testPorperties.setPropertyName(propertyName);//
		                 saveTestPorperties = productPropertiesDao.save(testPorperties);
		             } else {
		                 saveTestPorperties = testPorperties;
		             }
		             allProperties = allProperties + saveTestPorperties.getId().toString() + "-";
		             for (final JsonNode objNode : json.findValue("sizes")) {  	 
			             testPropertyValues = new ProductPropertyvalues();
			             testPropertyValues.setRefId(objNode.findValue("skuId").asText());//
			             testPropertyValues.setProductId(savedTestProduct.getId());
			             testPropertyValues.setPropertyId(saveTestPorperties.getId());
			             testPropertyValues.setValueName("<span>"+objNode.findValue("label").asText()+"</span>");//
			             productPropertyvaluesDao.save(testPropertyValues);
		             }
		             savedTestProduct.setProperties(allProperties);
		             String myPropValueIds = "";
		             List<ProductPropertyvalues> productPropertyValues = productPropertyvaluesDao.loadByProductId(savedTestProduct.getId());
		             if(productPropertyValues != null && !productPropertyValues.isEmpty()) {
			             for(ProductPropertyvalues propertyValue : productPropertyValues) {
			            	 ProductPropertiesMap productPropertyMap = new ProductPropertiesMap();
			            	 myPropValueIds = propertyValue.getId().toString() + "_";
			            	 productPropertyMap.setPropertyvalueComposition(myPropValueIds);
			            	 productPropertyMap.setDiscount(discountPrice);
				             productPropertyMap.setPrice(actualPrice);
				             productPropertyMap.setProductId(savedTestProduct);
				             productPropertyMap.setQuantity(5l);
				             productPropertiesMapDao.save(productPropertyMap);
			             }
			             
		             } else {
		            	 ProductPropertiesMap productPropertyMap = new ProductPropertiesMap();
		            	 productPropertyMap.setPropertyvalueComposition("_");
		            	 productPropertyMap.setDiscount(discountPrice);
			             productPropertyMap.setPrice(actualPrice);
			             productPropertyMap.setProductId(savedTestProduct);
			             productPropertyMap.setQuantity(5l);
			             productPropertiesMapDao.save(productPropertyMap);
		             }
		             
		             int flg = 0;
		             String imgUrl = "";
		             for (final JsonNode objNode : json.findValue("media").findValue("albums").get(0).findValues("images").get(0)) {
		                 
		            	 imgUrl = objNode.findValue("src").asText();
		            	 String ht = imgUrl.replace("h_($height)", "h_640");
		         		 String perc = ht.replace("q_($qualityPercentage)", "q_90");
		         		 String finalUrl = perc.replace("w_($width)", "w_480");
		         		String imgUrl2 = GoogleBucketFileUploader.uploadProductImage(finalUrl, savedTestProduct.getId());
		                 if (flg == 0) {
		                     flg++;
		                     savedTestProduct.setImgurl(imgUrl2);
		                 } else {
		                     ProductImg productImg = new ProductImg();
		                     productImg.setId(null);
		                     productImg.setImgUrl(imgUrl2);
		                     productImg.setProductId(savedTestProduct.getId());
		                     productImgDao.save(productImg);
		                 }
		             }
		             
		             if (productDao.save(savedTestProduct) != null) {
		            	 product.setStatus(1);
		                 
		                 temtproductlinklistDao.save(product);
		                 status = "Success";
		             }
		             
		          	} catch(Exception ex) {
		          		product.setStatus(4);
		                StringWriter sw = new StringWriter();
		                PrintWriter pw = new PrintWriter(sw);
		                ex.printStackTrace(pw);
		                String sStackTrace = sw.toString();
		                product.setError(sStackTrace);
		                temtproductlinklistDao.save(product);
		                ex.printStackTrace();
		                status = "Failure";
		          	}
				 } else {
					 product.setStatus(3);
					 product.setLink(url);
		             temtproductlinklistDao.save(product);
		             status = "Product exsist";
				 }
		} else {
			status = "json null";
		}
		
		System.out.println("status......"+status);
	}
		return status;
	}
}
