package com.scrapping.engine.bean;

public class ProductDetailsLinks {
	
	private String productDetailLink;
	private String responsibleServer;
	private char status;
	
	public String getProductDetailLink() {
		return productDetailLink;
	}
	public void setProductDetailLink(String productDetailLink) {
		this.productDetailLink = productDetailLink;
	}
	public char getStatus() {
		return status;
	}
	public void setStatus(char status) {
		this.status = status;
	}
	
	public void setResponsibleServer(String responsibleServer) {
		this.responsibleServer = responsibleServer;
	}
	public String getResponsibleServer() {
		return responsibleServer;
	}
	

}
