package com.weavers.duqhan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.scrapping.engine.bean.ProductDetailsLinks;
import com.weavers.duqhan.dao.ProductDao;
import com.weavers.duqhan.dao.TemtproductlinklistDao;
import com.weavers.duqhan.dao.jpa.ProductListLinkDaoJpa;
import com.weavers.duqhan.domain.Product;
import com.weavers.duqhan.domain.ProductListLink;
import com.weavers.duqhan.domain.Temtproductlinklist;
import com.weavers.duqhan.util.GoogleBucketFileUploader;

@Component
public class ScheduledTasks {
	
	@Autowired
    TemtproductlinklistDao temtproductlinklistDao;
	
	@Autowired
	ProductListLinkDaoJpa productListLinkDao;
	
	@Autowired
    private ProductDao productDao;
	
	//@Scheduled(fixedRate = 60*1000)
	public void schedule() {
		Logger.getLogger(ScheduledTasks.class.getName()).log(Level.INFO, "Starting  Scheduler 1");
        
		List<ProductListLink> lists = productListLinkDao.getUnprocessedProductListLink();
		if(lists.isEmpty()) {
			return;
		}
		ProductListLink productListLink = lists.get(0);
		String url = productListLink.getLink();///*"https://www.google.com";*/ "https://www.aliexpress.com/category/200188001/fine-jewelry.html";
		productListLink.setStatus(-1);
		productListLink.setStartedAt(new Date());
		productListLinkDao.save(productListLink);
		try {
			Logger.getLogger(ScheduledTasks.class.getName()).log(Level.INFO, "Starting " + url);
			Document doc = Utility.connect(url);
			
			if(doc == null) return;
			
			else {
				Logger.getLogger(ScheduledTasks.class.getName()).log(Level.INFO, "found for " + url);
				
			}
			
			Elements productUrlList = doc.select("div.ui-pagination-navi a");
			
			if(!productUrlList.isEmpty()) {
				handlingPagination(productUrlList,productListLink.getId());
				productListLink.setStatus(1);
				productListLink.setEndedAt(new Date());
				productListLinkDao.save(productListLink);
			} else {
				System.out.println("Something went wrong");
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public void handlingPagination(Elements productUrlList, Long id) throws HttpException, IOException, InterruptedException {
		int MAX_PAGE = 999;
		String nexturl = productUrlList.get(0).attr("href");
		if(!nexturl.startsWith("https")) {
			nexturl = "https:" + nexturl;
		}
		String firstPart = nexturl.split(".html")[0];
        firstPart = firstPart.substring(0, firstPart.length() - 1);
        String secondPart = nexturl.split(".html")[1];
        secondPart = ".html" /*+ secondPart*/;
        for (int i = 1; i < MAX_PAGE; i++) {
        	
        	nexturl = firstPart + i + secondPart; 
        	
            Document doc = Utility.connect(nexturl);
            if(doc == null) {
            	continue;
            }
            Elements singleProductUrls = doc.select(".son-list .list-item .pic a[href]");
            
            if(!singleProductUrls.isEmpty()) {
    			handlingSingleProductUrl(singleProductUrls,nexturl,id);
    		} else {
    			singleProductUrls = doc.select(".list-item .img a[href]");
    			if(!singleProductUrls.isEmpty()) {
        			handlingSingleProductUrl(singleProductUrls,nexturl,id);
        		} else {	
	    			System.out.println("Might be end of list, but not sure please debug for url ");
	    			String titleOfPage = doc.title();
	    			
	    			if("AliExpress.com - Maintaining".equalsIgnoreCase(titleOfPage)){
	    				Thread.currentThread().sleep(5000); //chilling out
	    				i--; //TODO
	    			} else if ("Buy Products Online from China Wholesalers at Aliexpress.com".equalsIgnoreCase(titleOfPage)) {
	    				Thread.currentThread().sleep(5000); //chilling out
	    				i--; //TODO: Need to add logic to avoid dead-loop.
	    			}
	    			else if("".equalsIgnoreCase(titleOfPage)) {
	    				System.out.println(titleOfPage + " Breaking now...");
	    				break;
	    			} else {
	    				System.out.println(titleOfPage + " Breaking now...");
	    				System.out.println(doc);
	    				break;
	    			}
        		}
    		}
            
        }
	}
	
	private  void handlingSingleProductUrl(Elements singleProductUrls,String urlOfListPage,Long id) throws HttpException, IOException {
		//System.out.println("Starting" + urlOfListPage);
		List<ProductDetailsLinks> productDetailsLinks = new ArrayList<ProductDetailsLinks>();
		  for (Element element : singleProductUrls) {
			  String link = element.attr("href");
			  if(!link.startsWith("http")) {
				  link = "https:" + link;
			  }
			  ProductDetailsLinks productLink = new ProductDetailsLinks();
			  productLink.setProductDetailLink(link);
			  productLink.setStatus('I');
			  productLink.setResponsibleServer(Utility.getRandomServer());
			  productDetailsLinks.add(productLink);
		  }
		  //System.out.println(String.format("For %s no of products %s", urlOfListPage, productDetailsLinks.size()));
		  saveLinksToDB(productDetailsLinks,urlOfListPage,id);
	}
	
	@Async
	private  void saveLinksToDB(List<ProductDetailsLinks> productDetailsLinks, String urlOfListPage,Long id) {
		
		for (ProductDetailsLinks link : productDetailsLinks) {
			Temtproductlinklist temtproductlinklist = temtproductlinklistDao.getTemtproductlinklistByLink(link.getProductDetailLink());
	        if (temtproductlinklist == null) {
	            temtproductlinklist = new Temtproductlinklist();
	            temtproductlinklist.setLink(link.getProductDetailLink());
	            temtproductlinklist.setParentUrl(id);
	            temtproductlinklist.setStatus(0);
	            temtproductlinklistDao.save(temtproductlinklist);
	        }
		}
		System.out.println("Done" + urlOfListPage);
		
	}

	//@Scheduled(fixedRate = 120*1000)
    public void makeThumbnailImage() {
    	List<Product> products = productDao.getNoThumbnail();
        for(Product p: products) {
        	System.out.println(p.getId());
        	String thumbImage = GoogleBucketFileUploader.uploadThumbProductImage(p.getImgurl(),p.getId());
        	p.setThumbImg(thumbImage);
        	productDao.save(p);
        }
    	
    }

}
