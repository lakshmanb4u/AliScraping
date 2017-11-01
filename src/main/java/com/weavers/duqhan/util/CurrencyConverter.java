/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.weavers.duqhan.util;

import java.io.IOException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *
 * @author weaversAndroid
 */
public class CurrencyConverter {

    public static Double convert(String currencyFrom, String currencyTo) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://quote.yahoo.com/d/quotes.csv?s=" + currencyFrom + currencyTo + "=X&f=l1&e=.csv");
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody = httpclient.execute(httpGet, responseHandler);
        httpclient.getConnectionManager().shutdown();
        return Double.parseDouble(responseBody);
    }

    public static Double usdTOinr(Double usdValue) {
        try {
            Double inrValue = CurrencyConverter.convert("USD", "INR");//usd to inr
            return Double.valueOf(String.valueOf(inrValue * usdValue));
        } catch (Exception e) {
            return null;
        }
    }

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
