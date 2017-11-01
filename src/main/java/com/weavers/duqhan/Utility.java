package com.weavers.duqhan;

import java.io.IOException;
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

public class Utility {
	
	private static final String[] proxies = {
			/*"mamidilaxmanlnu:EHnMqzod@173.246.165.22:60099",
			"mamidilaxmanlnu:EHnMqzod@173.246.167.254:60099",*/
			//"pxu1039-0:ySza*EciKXT$U$G713uu@x.botproxy.net:8080",	
			//"Lakshmanb4u:Duqhan01@open.proxymesh.com:31280",		
			"Lakshmanb4u:Duqhan01@sg.proxymesh.com:31280",
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

}
