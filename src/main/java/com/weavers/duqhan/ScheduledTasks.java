package com.weavers.duqhan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.scrapping.engine.bean.ProductDetailsLinks;
import com.weavers.duqhan.dao.TemtproductlinklistDao;
import com.weavers.duqhan.dao.jpa.ProductListLinkDaoJpa;
import com.weavers.duqhan.domain.ProductListLink;
import com.weavers.duqhan.domain.Temtproductlinklist;

@Component
public class ScheduledTasks {
	
	@Autowired
    TemtproductlinklistDao temtproductlinklistDao;
	
	@Autowired
	ProductListLinkDaoJpa productListLinkDao;
	
	private static final String[] proxies = {
			/*"mamidilaxmanlnu:EHnMqzod@173.246.165.22:60099",
			"mamidilaxmanlnu:EHnMqzod@173.246.167.254:60099",*/
			//"pxu1039-0:ySza*EciKXT$U$G713uu@x.botproxy.net:8080",	
			"Lakshmanb4u:Duqhan01@world.proxymesh.com:31280",		
			//"Lakshmanb4u:Duqhan01@sg.proxymesh.com:31280",
			};
			
	private static final String[] servers = {
			"server-1",
			"server-2"};
	
	public static String getRandomProxy() {
	    int rnd = new Random().nextInt(proxies.length);
	    return proxies[rnd];
	}
	
	public static String getRandomServer() {
	    int rnd = new Random().nextInt(proxies.length);
	    return servers[rnd];
	}

	@Scheduled(fixedRate = 60000)
	public void schedule() {
		List<ProductListLink> lists = productListLinkDao.getUnprocessedProductListLink();
		if(lists.isEmpty()) {
			return;
		}
		ProductListLink productListLink = lists.get(0);
		String url = productListLink.getLink();///*"https://www.google.com";*/ "https://www.aliexpress.com/category/200188001/fine-jewelry.html";
		productListLink.setStatus(-1);
		productListLinkDao.save(productListLink);
		try {
			Document doc = connect(url);
			if(doc == null) return;
			else {
				System.out.println("found for "+ url);
			}
			
			Elements productUrlList = doc.select("div.ui-pagination-navi a");
			
			if(!productUrlList.isEmpty()) {
				handlingPagination(productUrlList);
			} else {
				System.out.println("Something went wrong");
			}
			productListLink.setStatus(1);
			productListLinkDao.save(productListLink);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Document connect(String url) throws HttpException, IOException, InterruptedException {
		
		String proxyToken[] = getRandomProxy().split("@");
		String proxyCredentials[] = proxyToken[0].split(":");
		String proxyHost[] = proxyToken[1].split(":");
		
		HttpHost targetHost = new HttpHost(proxyHost[0], Integer.parseInt(proxyHost[1]));
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(
		        new AuthScope(targetHost.getHostName(), targetHost.getPort()),
		        new UsernamePasswordCredentials(proxyCredentials[0], proxyCredentials[1]));
		HttpClientContext context = HttpClientContext.create();
		context.setCredentialsProvider(credsProvider);
		
		HttpGet  method = new HttpGet (url);
		CloseableHttpClient httpclient = HttpClientBuilder.
				create().
				setProxy(targetHost).
				setDefaultCredentialsProvider(credsProvider).
				build();
		CloseableHttpResponse response = httpclient.execute(
	             method);
		int code = response.getStatusLine().getStatusCode();
		
		try {
		if(code == 200) {
			return  Jsoup.parse(response.getEntity().getContent(),"ISO-8859-9", "");
		} else {
			System.out.println(response.getStatusLine().getReasonPhrase());
			System.out.println(response.getStatusLine().getStatusCode());
		}
		return null;
		} finally {
			method.releaseConnection();
		}
	}
	
	public void handlingPagination(Elements productUrlList) throws HttpException, IOException, InterruptedException {
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
        	
            Document doc = connect(nexturl);
            Elements singleProductUrls = doc.select(".son-list .list-item .pic a[href]");
            
            if(!singleProductUrls.isEmpty()) {
    			handlingSingleProductUrl(singleProductUrls,nexturl);
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
	
	private  void handlingSingleProductUrl(Elements singleProductUrls,String urlOfListPage) throws HttpException, IOException {
		System.out.println("Starting" + urlOfListPage);
		List<ProductDetailsLinks> productDetailsLinks = new ArrayList<ProductDetailsLinks>();
		  for (Element element : singleProductUrls) {
			  String link = element.attr("href");
			  if(!link.startsWith("http")) {
				  link = "https:" + link;
			  }
			  ProductDetailsLinks productLink = new ProductDetailsLinks();
			  productLink.setProductDetailLink(link);
			  productLink.setStatus('I');
			  productLink.setResponsibleServer(getRandomServer());
			  productDetailsLinks.add(productLink);
		  }
		  //System.out.println(String.format("For %s no of products %s", urlOfListPage, productDetailsLinks.size()));
		  saveLinksToDB(productDetailsLinks,urlOfListPage);
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
