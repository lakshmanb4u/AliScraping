/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.weavers.duqhan.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.weavers.duqhan.dao.CurrencyRatesDao;
import com.weavers.duqhan.dao.ProductDao;
import com.weavers.duqhan.dao.jpa.ProductDaoJpa;

import java.io.IOException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
/**
 *
 * @author weaversAndroid
 */
public class CurrencyConverter {
	public static Map<String,String> ratesByDate = new HashMap<String,String>();
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	static {
		new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	}

    public static Double convert(String currencyFrom, String currencyTo) throws IOException, UnirestException {
        //http://api.fixer.io/latest?symbols=INR,USD&base=USD
    	Date thisDate = new Date();
    	String date1 = sdf.format(thisDate);
    	String rate = ratesByDate.get(date1);
    	if(rate != null){
    		return Double.parseDouble(rate);
    	}
    	
    	
    	String date2 = sdf.format(new Date(thisDate.getTime() - 1000*60*60*24L));
    	rate = ratesByDate.get(date2);
    	if(rate != null){
    		return Double.parseDouble(rate);
    	}
    	
    	JsonNode node = Unirest.get("http://api.fixer.io/latest?symbols="+currencyTo+"&base="+currencyFrom)
		.asJson().getBody();
		String date = node.getObject().getString("date");//format 2017-11-01
		rate = node.getObject().getJSONObject("rates").get(currencyTo).toString();
		ratesByDate.put(date, rate);
		return Double.parseDouble(rate);
    }
   /* public static Double convertNew(String currencyFrom, String currencyTo) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
//        HttpGet httpGet = new HttpGet("https://finance.yahoo.com/webservice/v1/symbols/allcurrencies/quote?format=json");
        HttpGet httpGet = new HttpGet("https://cdn.shopify.com/s/javascripts/currencies.js");
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody = httpclient.execute(httpGet, responseHandler);
        httpclient.getConnectionManager().shutdown();

        String rates = responseBody.split("rates:")[1].split("convert")[0];
        rates = rates.substring(0, rates.length() - 1);
        ObjectMapper mapper = new ObjectMapper();
        CurrencyRates jSONReader = null;
        jSONReader = mapper.readValue(rates, CurrencyRates.class);
//        System.out.println("ssssssssssss == " + jSONReader.getINR());
        double ratio = 0.0;
        if (currencyTo.equals("USD")) {
            ratio = jSONReader.getINR() / jSONReader.getUSD();
        } else {
            ratio = jSONReader.getUSD() / jSONReader.getINR();
        }
        return ratio;
    }*/

    /*public static Double usdTOinr(Double usdValue) {
        try {
            Double inrValue = CurrencyConverter.convertNew("USD", "INR");//usd to inr
            return Double.valueOf(String.valueOf(inrValue * usdValue));
        } catch (Exception e) {
            return null;
        }
    }*/

    public static Double inrTOusd(Double inrValue) {
        try {
            Double usdValue = CurrencyConverter.convert("INR", "USD");//inr to usd
            return Double.valueOf(String.valueOf(inrValue * usdValue));
        } catch (Exception e) {
            return null;
        }
    }

//    public static void main(String[] args) {
//        CurrencyConverter ycc = new CurrencyConverter();
//        try {
//            System.out.println(ycc.inrTOusd(700.0));
//            System.out.println(ycc.usdTOinr(ycc.inrTOusd(700.0)));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
