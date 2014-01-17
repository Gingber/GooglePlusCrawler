package com.google.api.services.samples.plus.cmdline;
 
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.List;
 

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
 
public class HttpCilentExample {
 
  private String cookies;
  
  private final String USER_AGENT = "Mozilla/5.0";
 
  public static void main(String[] args) throws Exception {
	  
	HttpClient client =  new HttpClient();
 
	String url = "https://accounts.google.com/ServiceLoginAuth";
	String gmail = "https://mail.google.com/mail/";
	String personInfo = "https://mail.google.com/mail/u/0/#inbox/1436b23c0c9d175c";
 
	// make sure cookies is turn on
	CookieHandler.setDefault(new CookieManager());
 
	HttpCilentExample http = new HttpCilentExample();
 
	String page = http.GetPageContent(url);
 
	List<NameValuePair> postParams = 
               http.getFormParams(page, "kmust.jiangbo@gmail.com","by14285700");
 
	http.sendPost(url, postParams);
 
	String result = http.GetPageContent(gmail);
	System.out.println(result);
 
	System.out.println("Done");
  }
 
  private void sendPost(String url, List<NameValuePair> postParams) 
        throws Exception {
 
	//HttpPost post = new HttpPost(url);
	PostMethod post = new PostMethod(url);
 
	// add header
	post.setRequestHeader("Host", "accounts.google.com");
	post.setRequestHeader("User-Agent", USER_AGENT);
	post.setRequestHeader("Accept", 
             "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
	post.setRequestHeader("Accept-Language", "en-US,en;q=0.5");
	post.setRequestHeader("Cookie", getCookies());
	post.setRequestHeader("Connection", "keep-alive");
	post.setRequestHeader("Referer", "https://accounts.google.com/ServiceLoginAuth");
	post.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	
	//设置登陆时要求的信息，一般就用户名和密码，验证码自己处理了
    NameValuePair[] data = {
            new BasicNameValuePair("username", "Unmi"),
            new BasicNameValuePair("password", "123456"),
            new BasicNameValuePair("code", "anyany")
    };
 
	post.setRequestBody((org.apache.commons.httpclient.NameValuePair[]) data);
 
	client.executeMethod(post);
 
	int responseCode = response.getStatusLine().getStatusCode();
 
	System.out.println("\nSending 'POST' request to URL : " + url);
	System.out.println("Post parameters : " + postParams);
	System.out.println("Response Code : " + responseCode);
 
	BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));
 
	StringBuffer result = new StringBuffer();
	String line = "";
	while ((line = rd.readLine()) != null) {
		result.append(line);
	}
 
	// System.out.println(result.toString());
 
  }
 
  private String GetPageContent(String url) throws Exception {
 
	HttpGet request = new HttpGet(url);
 
	request.setHeader("User-Agent", USER_AGENT);
	request.setHeader("Accept",
		"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
	request.setHeader("Accept-Language", "en-US,en;q=0.5");
 
	HttpResponse response = client.execute(request);
	int responseCode = response.getStatusLine().getStatusCode();
 
	System.out.println("\nSending 'GET' request to URL : " + url);
	System.out.println("Response Code : " + responseCode);
 
	BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));
 
	StringBuffer result = new StringBuffer();
	String line = "";
	while ((line = rd.readLine()) != null) {
		result.append(line);
	}
 
	// set cookies
	setCookies(response.getFirstHeader("Set-Cookie") == null ? "" : 
                     response.getFirstHeader("Set-Cookie").toString());
 
	return result.toString();
 
  }
 
  public List<NameValuePair> getFormParams(
             String html, String username, String password)
			throws UnsupportedEncodingException {
 
	System.out.println("Extracting form's data...");
 
	Document doc = Jsoup.parse(html);
 
	// Google form id
	Element loginform = doc.getElementById("gaia_loginform");
	Elements inputElements = loginform.getElementsByTag("input");
 
	List<NameValuePair> paramList = new ArrayList<NameValuePair>();
 
	for (Element inputElement : inputElements) {
		String key = inputElement.attr("name");
		String value = inputElement.attr("value");
 
		if (key.equals("Email"))
			value = username;
		else if (key.equals("Passwd"))
			value = password;
 
		paramList.add(new BasicNameValuePair(key, value));
 
	}
 
	return paramList;
  }
 
  public String getCookies() {
	return cookies;
  }
 
  public void setCookies(String cookies) {
	this.cookies = cookies;
  }
 
}