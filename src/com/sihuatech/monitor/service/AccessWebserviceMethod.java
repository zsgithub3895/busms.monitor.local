package com.sihuatech.monitor.service;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;

import sun.util.logging.resources.logging;

import com.onewaveinc.mip.log.Logger;
import com.sihuatech.monitor.entity.ResponseDataBean;
import com.sihuatech.monitor.servlet.MonitorServlet;

/**
 * 
 * @author lei.zhu
 * 
 */
public class AccessWebserviceMethod {
	private static Logger log = Logger.getInstance(AccessWebserviceMethod.class);
	private static final String soap11 = "1";
	private static final String soap12 = "2";
	private static final String post = "3";
	private static final String get = "4";
	/*private static final String connTimeout = "connection.timeout";
	private static final String resqTimeout = "request.timeout";*/
	/**
	 * 用soap1.1访问webservice
	 * 
	 * @param url
	 * @param soapRequestData
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public static ResponseDataBean sendXmlToWebservice(String url, String soapRequestData, String accessWay)
			throws HttpException, IOException {
		ResponseDataBean responseDataBean = null;
		if (accessWay.equals(soap11)) {
			responseDataBean = postSoap11(url, soapRequestData);
			return responseDataBean;
		}
		if (accessWay.equals(soap12)) {
			responseDataBean = postSoap12(url, soapRequestData);
			return responseDataBean;
		}
		if (accessWay.equals(post)) {
			responseDataBean = postWebservice(url, soapRequestData);
			return responseDataBean;
		}
		if (accessWay.equals(get)) {
			responseDataBean = getWebservice(url);
			return responseDataBean;
		}
		return null;
	}

	/**
	 * 用soap1.2访问webservice
	 * 
	 * @param url
	 * @param soapRequestData
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public static ResponseDataBean postSoap11(String url, String soapRequestData) throws HttpException, IOException {
		long before = 0;
		long after = 0;
		ResponseDataBean responseDataBean = new ResponseDataBean();
		PostMethod postMethod = new PostMethod(url);
		String soapResponseData = null;
		// 然后把Soap请求数据添加到PostMethod中
		byte[] b = soapRequestData.getBytes("utf-8");
		InputStream is = new ByteArrayInputStream(b, 0, b.length);
		RequestEntity re = new InputStreamRequestEntity(is, b.length, "text/xml; charset=utf-8");
		postMethod.setRequestEntity(re);
		// 最后生成一个HttpClient对象，并发出postMethod请求
		HttpClient httpClient = new HttpClient();
		before = System.currentTimeMillis();
		setTimeout(httpClient,30000,30000);
		int statusCode=0;
		try{
	         statusCode = httpClient.executeMethod(postMethod);
		}catch(Exception e){
			log.info("连接异常或请求超时！",e);
			responseDataBean.setDelayTime(-1);
			responseDataBean.setWebserviceFailure(true);
		}
		if (statusCode == 200) {
			soapResponseData = postMethod.getResponseBodyAsString();
			after = System.currentTimeMillis();
			responseDataBean.setDelayTime(after - before);
			responseDataBean.setResponseXMLData(soapResponseData);
			responseDataBean.setWebserviceFailure(false);
		} else {
			responseDataBean.setDelayTime(-1);
			responseDataBean.setWebserviceFailure(true);
			log.info("调用失败！错误码：" + statusCode);
		}

		return responseDataBean;

	}

	public static ResponseDataBean postSoap12(String url, String soapRequestData) throws HttpException, IOException {
		long before = 0;
		long after = 0;
		ResponseDataBean responseDataBean = new ResponseDataBean();
		PostMethod postMethod = new PostMethod(url);
		String soapResponseData = null;
		// 然后把Soap请求数据添加到PostMethod中
		byte[] b = soapRequestData.getBytes("utf-8");
		InputStream is = new ByteArrayInputStream(b, 0, b.length);
		RequestEntity re = new InputStreamRequestEntity(is, b.length, "application/soap+xml; charset=utf-8");
		postMethod.setRequestEntity(re);
		// 最后生成一个HttpClient对象，并发出postMethod请求
		HttpClient httpClient = new HttpClient();
	    setTimeout(httpClient,30000,30000);
		before = System.currentTimeMillis();
		int statusCode=0;
		try{
	         statusCode = httpClient.executeMethod(postMethod);
		}catch(Exception e){
			log.info("连接异常或请求超时！",e);
			responseDataBean.setDelayTime(-1);
			responseDataBean.setWebserviceFailure(true);
		}finally{
			if(null!=is){
				is.close();
			}
		}
		if (statusCode == 200) {
			soapResponseData = postMethod.getResponseBodyAsString();
			after = System.currentTimeMillis();
			responseDataBean.setDelayTime(after - before);
			responseDataBean.setResponseXMLData(soapResponseData);
			responseDataBean.setWebserviceFailure(false);
		} else {
			responseDataBean.setDelayTime(-1);
			responseDataBean.setWebserviceFailure(true);
			log.info("调用失败！错误码：" + statusCode);
		}

		return responseDataBean;

	}

	/**
	 * 用post访问webservice
	 * 
	 * @param url
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public static ResponseDataBean postWebservice(String url, String postRequestData) throws HttpException, IOException {
		long before = 0;
		long after = 0;
		System.out.println("postWebservice");
		PostMethod postMethod = new PostMethod(url);
		String postResponseData = null;
		// 然后把Soap请求数据添加到PostMethod中
		byte[] b = postRequestData.getBytes("utf-8");
		InputStream is = new ByteArrayInputStream(b, 0, b.length);
		RequestEntity re = new InputStreamRequestEntity(is, b.length, "application/x-www-form-urlencoded");
		postMethod.setRequestEntity(re);
		// 最后生成一个HttpClient对象，并发出postMethod请求
		ResponseDataBean responseDataBean = new ResponseDataBean();
		HttpClient httpClient = new HttpClient();
		setTimeout(httpClient,30000,30000);
		before = System.currentTimeMillis();
		int statusCode=0;
		try{
	         statusCode = httpClient.executeMethod(postMethod);
		}catch(Exception e){
			log.info("连接异常或请求超时！",e);
			responseDataBean.setDelayTime(-1);
			responseDataBean.setWebserviceFailure(true);
		}

		if (statusCode == 200) {
			// System.out.println("调用成功！");
			postResponseData = postMethod.getResponseBodyAsString();
			after = System.currentTimeMillis();
			responseDataBean.setDelayTime(after - before);
			responseDataBean.setResponseXMLData(postResponseData);
			responseDataBean.setWebserviceFailure(false);
		} else {
			responseDataBean.setDelayTime(-1);
			responseDataBean.setWebserviceFailure(true);
			System.out.println("调用失败！错误码：" + statusCode);
		}
		return responseDataBean;
	}

	/**
	 * 用get访问webservice
	 * 
	 * @param url
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public static ResponseDataBean getWebservice(String url) throws HttpException, IOException {
		long before = 0;
		long after = 0;
		System.out.println("getWebservice");
		GetMethod getMethod = new GetMethod(url);
		ResponseDataBean responseDataBean = new ResponseDataBean();
		getMethod.setRequestHeader("Content-type", "text/xml; charset=utf-8");
		String getResponseData = null;
		// 最后生成一个HttpClient对象，并发出postMethod请求
		HttpClient httpClient = new HttpClient();
		before = System.currentTimeMillis();
		setTimeout(httpClient,30000,30000);
		int statusCode=0;
		try{
	         statusCode = httpClient.executeMethod(getMethod);
		}catch(Exception e){
			log.info("连接异常或请求超时！",e);
			responseDataBean.setDelayTime(-1);
			responseDataBean.setWebserviceFailure(true);
		}
		if (statusCode == 200) {
			getResponseData = getMethod.getResponseBodyAsString();
			after = System.currentTimeMillis();
			responseDataBean.setDelayTime(after - before);
			responseDataBean.setResponseXMLData(getResponseData);
			responseDataBean.setWebserviceFailure(false);
		} else {
			responseDataBean.setDelayTime(-1);
			responseDataBean.setWebserviceFailure(true);
			System.out.println("调用失败！错误码：" + statusCode);
		}
		return responseDataBean;
	}
 
	public static void setTimeout(HttpClient httpClient,int connTime,int rsqTime) {
		/**连接超时*/
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(connTime);
		/**请求超时*/
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(rsqTime); 
	}
	
}
