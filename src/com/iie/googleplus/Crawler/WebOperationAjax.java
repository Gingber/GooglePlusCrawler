/**
 * 
 */
package com.iie.googleplus.Crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.iie.googleplus.Platform.LogSys;
import com.iie.googleplus.task.beans.Task;
import com.iie.httpclient.crawler.AutoLoginGoogle;
import com.iie.httpclient.crawler.GoogleClientManager;

/**
 * @author IIEIR
 *
 */
public class WebOperationAjax {
	
	public static boolean debug=false;
	
	public static String openLink(DefaultHttpClient httpclient, Task task, String linkAddress, int count) throws RuntimeException{
		if(count<=2){
			LogSys.nodeLogger.debug("The Retry[" + count + "] OpenLink with Address:" + linkAddress);
			String res=openLink(httpclient, linkAddress);
			
			if(res!=null&&res.length()>=1){
				return res;
			}else{
				return openLink(httpclient,task,linkAddress,count+1);
			}
		}else{
			return null;
		}
	}
	

	public static String openLink(DefaultHttpClient httpclient, String linkAddress) {
		
		StringBuilder sb = new StringBuilder();
        try {
            HttpGet httpget = new HttpGet(linkAddress);
            HttpResponse response = httpclient.execute(httpget);

            StatusLine state = response.getStatusLine();
			int stateCode = state.getStatusCode();
			boolean needReLogin = false;
			if(HttpStatus.SC_OK == stateCode){
				BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"utf-8"));
				String inputLine = null;
				while ((inputLine = in.readLine()) != null) {
					sb.append(inputLine+"\r\n");
					//判断是否需要重新登录到Twitter
					if(inputLine.contains("Sign in to Twitter")||inputLine.contains("<form action=\"https://twitter.com/sessions\"")){
						LogSys.nodeLogger.error("需要重新登录");
						needReLogin=true;
					}
				}
				in.close();				
			}else if(HttpStatus.SC_MOVED_PERMANENTLY == stateCode 
					|| HttpStatus.SC_MOVED_TEMPORARILY == stateCode
					|| HttpStatus.SC_SEE_OTHER == stateCode
					|| HttpStatus.SC_TEMPORARY_REDIRECT == stateCode){
				Header[] headers=response.getHeaders("location");
				if(headers!=null&&headers.length>0){
					String redirectLocation = headers[0].getValue();
					String redirectAddress;
					if(redirectLocation!=null&&redirectLocation.isEmpty()==false){
						redirectAddress=redirectLocation;
						LogSys.nodeLogger.error("发生跳转~:location to"+redirectAddress);
						
					}else{
						redirectAddress="/";
						LogSys.nodeLogger.error("发生跳转~:location to"+redirectAddress);
					}
				}				
			}
			
			//执行后过滤，发现网页的异常情况,需要重新登录操作，则进行重新登录
			if(needReLogin){				
				AdvanceLoginManager twlogin = new AdvanceLoginManager(httpclient);
				twlogin.trylogin();
				return null;
			}
			
            
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
        
        return sb.toString();
           
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
