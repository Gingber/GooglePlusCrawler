/**
 * 
 */
package com.iie.googleplus.Crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Activity;
import com.iie.googleplus.Dboperator.MySQLOperator;
import com.iie.googleplus.Platform.LogSys;
import com.iie.googleplus.Report.ReportData;
import com.iie.googleplus.analyzer.beans.GPlusUser;
import com.iie.googleplus.analyzer.beans.UserRelationship;
import com.iie.googleplus.task.beans.Task;
import com.iie.googleplus.tool.MulityInsertDataBase;
import com.iie.httpclient.crawler.AutoLoginGoogle;
import com.iie.httpclient.crawler.GoogleClientManager;
import com.iie.util.TxtWriter;

/**
 * @author Gingber
 *
 */
public class AjaxFollowerCrawler extends AbstractCrawler {
	
	private static String baseUrl = "https://plus.google.com/_/socialgraph/lookup/incoming/?o=%5Bnull%2Cnull%2C%22$%22%5D&s=true&n=1000&_reqid=1434115&rt=j";
	private DefaultHttpClient httpclient;
	
	public 	AjaxFollowerCrawler(DefaultHttpClient httpclient, MySQLOperator dbop) {
		this.httpclient = httpclient;
		super.dboperator = dbop;
	}
	
	public boolean doCrawl(Task task, MulityInsertDataBase dbo, Vector<GPlusUser> RelateUsers, ReportData reportData) {
		LogSys.nodeLogger.debug("Follower Grabber is starting......");
		String userId = task.getTargetString();
		System.out.println("current crawl user:\t" + userId);
		
		try {
			
			String URL = baseUrl.replace("$", userId);
			String ajaxContent = super.openLink(httpclient, URL, task, 0, null);
			
			if(!this.CheckValidation(ajaxContent)){
				LogSys.nodeLogger.error("FollowerÕ¯¬Á«Î«Û ß∞‹:" + userId);
				return false;			
			}
			
			Vector<UserRelationship> userrels = new Vector<UserRelationship>(20);
			
			String followerContent = ajaxContent.substring(ajaxContent.indexOf('['), ajaxContent.length());
	        JSONArray fansJsonArray = new JSONArray(followerContent);  
	        JSONArray followerInnerArray = fansJsonArray.getJSONArray(0).getJSONArray(0).getJSONArray(2);
	        
	        for(int i = 0; i < followerInnerArray.length(); i++) {	
	        	String followerId =  followerInnerArray.getJSONArray(i).getJSONArray(0).getString(2);
	        	String followerName = followerInnerArray.getJSONArray(i).getJSONArray(2).getString(0);

	        	userrels.add(new UserRelationship(task.getTargetString(), followerId, false+""));
	        	
	        	GPlusUser gpUser = new GPlusUser();
	        	gpUser.setUserId(followerId);
				gpUser.setUserName(followerName);
				if(!RelateUsers.contains(gpUser)) {
					RelateUsers.add(gpUser);
				}
	        }
	            
            UserRelationship[] rels = new  UserRelationship[userrels.size()];
            userrels.toArray(rels);
			
			if(rels != null && rels.length >= 1){
				super.dboperator.batchIntoUserRel(rels, "user_relationship");
			}

		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
