package com.sihuatech.monitor.entity;

public class ResponseDataBean {

	 private String responseXMLData;
	 private long delayTime;
	 private boolean webserviceFailure;
	
	public boolean isWebserviceFailure() {
		return webserviceFailure;
	}
	public void setWebserviceFailure(boolean webserviceFailure) {
		this.webserviceFailure = webserviceFailure;
	}
	public String getResponseXMLData() {
		return responseXMLData;
	}
	public void setResponseXMLData(String responseXMLData) {
		this.responseXMLData = responseXMLData;
	}
	public long getDelayTime() {
		return delayTime;
	}
	public void setDelayTime(long delayTime) {
		this.delayTime = delayTime;
	}
}
