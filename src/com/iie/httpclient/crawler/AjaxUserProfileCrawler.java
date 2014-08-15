/**
 * 
 */
package com.iie.httpclient.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.iie.googleplus.Dboperator.MySQLOperator;
import com.iie.googleplus.analyzer.beans.UserProfile;

/**
 * @author Gingber
 *
 */
public class AjaxUserProfileCrawler {
	
	private DefaultHttpClient httpclient;
	private String cookie;
	private String content;
	
	public 	AjaxUserProfileCrawler(DefaultHttpClient httpclient, String cookie) {
		this.httpclient = httpclient;
		this.cookie = cookie;
	}
	
	public boolean doCrawler(String url) throws ClientProtocolException, IOException, SQLException {
		HttpGet httpget = new HttpGet(url);
        httpget.setHeader("cookie", cookie);           
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        Logger logger = Logger.getLogger("User Profile Crawler Log");
        if(entity != null) { 
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(entity.getContent(),"utf-8"));
            try {
                StringBuilder sb = new StringBuilder(); 
                String line = null;
                while((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append('\n');
                }
                this.content = sb.toString();
                if (this.content.contains("Error 404") || this.content.contains("That’s an error.")
                		||this.content.contains("在此服务器上找不到请求的网址")) {
                	logger.info("fail url: " + url);  
                	return false;
                } else {
                	AjaxUserProfileAnalyzer aupa = new AjaxUserProfileAnalyzer();
                	UserProfile userprofile = new UserProfile();
                	aupa.doAnylyze(this.content, userprofile);  
                	if(userprofile.getUserId() != null) {
//                		MySQLOperator.insertIntoUserProfile(userprofile, "user_profile");
                		logger.info("success url: " + url); 
                	}
                }
            } catch (IOException ex) {
                throw ex;
            } catch (RuntimeException ex) {
                throw ex;
            } finally {
                reader.close();
            }
        }
        EntityUtils.consume(entity);	
        
        return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
