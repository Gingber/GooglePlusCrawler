/**
 * 
 */
package com.google.api.services.samples.plus.cmdline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

/**
 * @author Gingber
 *
 */
public class GoogleAccounts {
	
	public static void consume(final HttpEntity entity) throws IOException {
    	if (entity == null) {
            return;
        }
        if (entity.isStreaming()) {
        	final InputStream instream = entity.getContent();
        	if (instream != null) {
                instream.close();
            }
        }
    }
	
	private final static String USER_AGENT = "Mozilla/5.0";
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static void main(String[] args) throws ClientProtocolException, IOException {
		
		/*String url = "http://www.javaeye.com/";
		PostMethod postMethod = new PostMethod(url);
		//濂藉儚鐢℅etMethod灏变笉琛岋紝HttpClient灏变細鑷姩澶勭悊閲嶅畾鍚�
		HttpClient httpClient = new HttpClient();
		httpClient.getParams().setParameter(HttpMethodParams.USER_AGENT,
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.202 Safari/535.1");// 璁剧疆UA
		int statusCode = httpClient.executeMethod(postMethod);
		System.out.println("HTTP鐘舵�鐮�" + statusCode);
		if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY)
		{
			// 浠庡ご涓彇鍑鸿浆鍚戠殑鍦板潃
			org.apache.commons.httpclient.Header locationHeader = postMethod.getResponseHeader("location");
			String location = null;
			location = locationHeader.getValue();
			System.out.println("椤甸潰閲嶅畾鍚戝埌:" + location);
		}*/
		
		// TODO Auto-generated method stub
		String url = "https://accounts.google.com/ServiceLoginAuth";
		String gmail = "https://mail.google.com/mail/";
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
	    httpclient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);

	    CookieStore cookieStore = new BasicCookieStore();
	    httpclient.setCookieStore(cookieStore);
	    ResponseHandler<byte[]> handler = new ResponseHandler<byte[]>() {
	        public byte[] handleResponse(HttpResponse response) throws IOException {
	            HttpEntity entity = response.getEntity();
	            if (entity != null) {
	                return EntityUtils.toByteArray(entity);
	            } else {
	                return null;
	            }
	        }
	    };

	    HttpGet httpGet = new HttpGet("https://www.google.com/accounts/Login");
	   
	    HttpResponse response = httpclient.execute(httpGet);
	    int responseCode = response.getStatusLine().getStatusCode();
		 
		System.out.println("\nSending 'POST' request to URL : https://www.google.com/accounts/Login");
		System.out.println("Response Code : " + responseCode);
		
		HttpEntity entity = response.getEntity();
		consume(entity);
		
	    List<Cookie> cookies = cookieStore.getCookies();
	    for(int i = 0; i<cookies.size(); i++) {
	    	System.out.println(cookies.get(i));
	    }
	    String galxValue = cookies.get(0).getValue();
	     

	    HttpPost httpPost = new HttpPost(url);
	    HttpParams params = httpPost.getParams();
	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	    nameValuePairs.add(new BasicNameValuePair("GALX", galxValue));
	    nameValuePairs.add(new BasicNameValuePair("Email", "yuqingee01@gmail.com"));
	    nameValuePairs.add(new BasicNameValuePair("Passwd", "iee123456"));
	    
	    
		httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
		response = httpclient.execute(httpPost);
		responseCode = response.getStatusLine().getStatusCode();
		 
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
		
		List<Cookie> cookies2 = cookieStore.getCookies();
	    String cookie = cookies2.get(0).getValue();
		
		int responseCode2 = 0;
		if ((responseCode == HttpStatus.SC_MOVED_TEMPORARILY) || (responseCode == HttpStatus.SC_MOVED_PERMANENTLY) 
				|| (responseCode == HttpStatus.SC_SEE_OTHER) || (responseCode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
			 //璇诲彇鏂扮殑URL鍦板潃
			Header[] header = response.getHeaders("location");  
			entity = response.getEntity();
			consume(entity);
			
			if (header != null) { 
			
				String newuri = header[0].getValue();
				System.out.println("Redirect URL:" + newuri);  
				if ((newuri == null) || (newuri.equals(""))) newuri = "/";

				HttpGet redirect = new HttpGet(newuri);
				
				redirect.setHeader("Host", "accounts.google.com");
				redirect.setHeader("path", "/CheckCookie?chtml=LoginDoneHtml&continue=https%3A%2F%2Faccounts.google.com%2FManageAccount&gidl=CAA");
				redirect.setHeader("Content-Type", "application/x-www-form-urlencoded");  
		        //涓嬮潰涓�闇�娉ㄩ噴鎺夛紝涓嶇劧杩斿洖鏁版嵁鏄痝zip鍘嬬缉鍚庣殑涔辩爜  
		        //getMethod.setRequestHeader("Accept-Encoding","gzip, deflate");  
				redirect.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4");  
				redirect.setHeader("Connection", "Keep-Alive");  
				redirect.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.72");  
				redirect.setHeader("Cookie", "SMSV=ADHTe-CwY3izsGyD9AV0AXeQBS--zuOW_8q6aic6ZhQM1W64yZFNEfqvWKkvuNyDM4TW3Vu3RGvaPw_nmeeY75wQYvDJJ_GHTOcc-MH2ZUGerhuVX4s8Q0SdsJtHR33yYzTrSW_lzYtc; __utma=72592003.809868009.1389185443.1389185443.1389185443.1; __utmz=72592003.1389185443.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); PREF=ID=503870a8c45f0d1d:U=3c56478f3393c83f:LD=zh-CN:NW=1:TM=1389087544:LM=1389186210:DV=glU6Slao4W0ZGg-rTA2jnmvqXurjhgI:S=dctbrfyXtPlhT_Tp; ACCOUNT_CHOOSER=AFx_qI6rANkTRM-oz68UUxvE82DqLmOWflvNKiNOvoVlC0VrvO8b3GLmTFJRUkRdKQo-ePSNpqCte-2wwMuVyeUdKBOsUXOwbcd-2NDYmLpVEnf7gcuxjAxtHj-17HifbjeTJ45dYVb7TpZFCw0fg3vKtWUDQo_3r_MCUnNzgYPr5eeUBfMsB6-oCVCXtUVj-M3xF7UUSDJO; GALX=eVZuxkQjiEY; GoogleAccountsLocale_session=zh_CN; GAPS=1:m2ZOScayD1bM8D6Ofrh0unbAmMTd_w:KI12cJBVUECQ39ln; NID=67=hqtZr1f3p_vjOV0TPFHIjgTN310ANURTbsZIhzDY4WY005sUGBe9g6SEOQZM6Z_jcTB1TWAifYeVAt3gev1hz2cplW7qjy4M9QZe0AYKMnmOriYImYNwRH9024UT8nObL32gWBrCLauz_d7DS5WqAObOdge-3M61DKoi; SID=DQAAAMQAAABfgYbRPZj-rbJ6MqgEjx9mAU1Cl9mXwLASL8EhLpV5nxtOwRuU4boZxZKkzbcnNRUxzcvPzI35qRXrFrY-N-01gtD0KbI-XuX78QQ_lr567SfO4BrNjaHoNd4Vx1vG99aLTONP4_7l2GewrKSDLB_EFRxkCXsdMIEGWw6Gfw2C-YREz7ELN1WxTIN1PUq0i5-QC-akDS0Zg5dvLfddJglkXg1Bb9_tezSqQ9CfgUIypkaUBew7kZvmjSej0IqlQHhPDyoyC4DkCUNxwVNvlgrY; LSID=DQAAAMgAAADMJM056rn2DRIF5oJqSeST16ecf3jSzn9FK3nZz6PFAQgcUDK_dZbB5ecK4Ye5y61MrjVsCsD-FBWlVHTICfkkmLFnA6C8jvYnW4IO2DuCF6QLRQPtdmbMEanLavnDj0e7AUDvLGTb7UB_E1UEeL6fUmKE-Rs7T_vX8eB_ztx5zWADe3B9eGUjMffIsO9_ZpjzsAC0kx1g_0q4cL_ncsGugt1t8wEJZ3smOH_KI4OeqLDKcWo6eIgnCeOt2pflMq-Ao3horcaTHDQpHTVcw8Ta; HSID=A_dNvAuBaTEGPOMUo; SSID=AT8_5DJuDK_WMNSLb; APISID=xc3IcL2FM__42X2p/AvbLIgxFQRmYFJ6q2; SAPISID=6uhVIC3euI_A82Pi/A1n1Prd3sssIaquzx");  
				
				response = httpclient.execute(redirect);
				responseCode2 = response.getStatusLine().getStatusCode();
				System.out.println("Redirect:"+ responseCode2);  
			} else   
				System.out.println("Invalid redirect");
		}
		
		entity = response.getEntity();
		consume(entity);
		
		
		
		int responseCode3 = 0;
		if ((responseCode2 == HttpStatus.SC_MOVED_TEMPORARILY) || (responseCode2 == HttpStatus.SC_MOVED_PERMANENTLY) 
				|| (responseCode2 == HttpStatus.SC_SEE_OTHER) || (responseCode2 == HttpStatus.SC_TEMPORARY_REDIRECT)) {
			 //璇诲彇鏂扮殑URL鍦板潃
			Header[] header = response.getHeaders("location");  
			if (header != null) { 
			
				String newuri = header[0].getValue();
				System.out.println("Redirect URL:" + newuri);
				if ((newuri == null) || (newuri.equals(""))) newuri = "/";
		        
				response.setHeader("Host", "accounts.google.com");
				response.setHeader("User-Agent", USER_AGENT);
			        
				response.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
				response.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4");
			        
				response.setHeader("Connection", "keep-alive");
				
				HttpGet redirect = new HttpGet(newuri);
				response = httpclient.execute(redirect);
				responseCode3 = response.getStatusLine().getStatusCode();
				System.out.println("Redirect:"+ responseCode3);  
			} else   
				System.out.println("Invalid redirect");
		}
		
		entity = response.getEntity();
		consume(entity);
		

	    httpGet = new HttpGet("https://www.google.com/accounts/b/0/ManageAccount");
	    params.setParameter(ClientPNames.COOKIE_POLICY, "easy");
	  
	    response = httpclient.execute(httpGet);
	    responseCode = response.getStatusLine().getStatusCode();
	    System.out.println("\nSending 'GET' request to URL : " + "https://www.google.com/accounts/b/0/ManageAccount");
		System.out.println("ManageAccount Response Code : " + responseCode);
		
		BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));
 
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		System.out.println(result);
		
		Header[] header = response.getHeaders("location");  
		entity = response.getEntity();
		consume(entity);
		
		if (header != null) { 
		
			String newuri = header[0].getValue();
			System.out.println("Redirect URL:" + newuri);  
			if ((newuri == null) || (newuri.equals(""))) newuri = "/";
	        
			HttpGet redirect = new HttpGet(newuri);
			response = httpclient.execute(redirect);
			responseCode2 = response.getStatusLine().getStatusCode();
			System.out.println("Redirect:"+ responseCode2);  
		} else   
			System.out.println("Invalid redirect");
		
		entity = response.getEntity();
		consume(entity);

	}

}
