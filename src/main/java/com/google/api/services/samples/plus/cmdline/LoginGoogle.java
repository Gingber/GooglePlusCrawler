/**
 * 
 */
package com.google.api.services.samples.plus.cmdline;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
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
public class LoginGoogle {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
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
	    try {
			httpclient.execute(httpGet, handler);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    List<Cookie> cookies = cookieStore.getCookies();
	    String galxValue = cookies.get(0).getValue();

	    HttpPost httpPost = new HttpPost("https://www.google.com/accounts/ServiceLoginAuth");
	    HttpParams params = httpPost.getParams();
	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	    nameValuePairs.add(new BasicNameValuePair("GALX", galxValue));
	    nameValuePairs.add(new BasicNameValuePair("Email", "kmust.jiangbo@gmail.com"));
	    nameValuePairs.add(new BasicNameValuePair("Passwd", "by14285700"));
	    try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    try {
			httpclient.execute(httpPost, handler);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    httpGet = new HttpGet("https://www.google.com/accounts/b/0/ManageAccount");
	    params.setParameter(ClientPNames.COOKIE_POLICY, "easy");
	    try {
			httpclient.execute(httpGet, handler);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
