/**
 * 
 */
package com.iie.googleplus.Crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import com.google.api.services.plus.Plus;
import com.iie.googleplus.Dboperator.MySQLOperator;
import com.iie.googleplus.Platform.LogSys;
import com.iie.googleplus.Report.ReportData;
import com.iie.googleplus.analyzer.beans.GPlusUser;
import com.iie.googleplus.analyzer.beans.UserProfile;
import com.iie.googleplus.analyzer.beans.UserRelationship;
import com.iie.googleplus.task.beans.Task;
import com.iie.googleplus.tool.MulityInsertDataBase;
import com.iie.httpclient.crawler.AutoLoginGoogle;
import com.iie.httpclient.crawler.GoogleClientManager;

/**
 * @author Gingber
 *
 */
public class AjaxUserProfileCrawler extends AbstractCrawler {
	
	private static String baseUrl = "https://plus.google.com/%s/about";
	private DefaultHttpClient httpclient;
	
	public 	AjaxUserProfileCrawler(DefaultHttpClient httpclient, MySQLOperator dbop) {
		this.httpclient = httpclient;
		super.dboperator = dbop;
	}
	
	public boolean doCrawl(Task task, MulityInsertDataBase dbo, Vector<GPlusUser> RelateUsers, ReportData reportData) {
		LogSys.nodeLogger.debug("Follower Grabber is starting......");
		String userId = task.getTargetString();
		System.out.println("current crawl user:\t" + userId);
		
		try {
			
			String URL = String.format(baseUrl, userId);
			
			String ajaxContent = super.openLink(httpclient, URL, task, 0, null);
			if(!this.CheckValidation(ajaxContent)){
				LogSys.nodeLogger.error("ProfileÕ¯¬Á«Î«Û ß∞‹:" + userId);
				return false;			
			}
			
			AjaxUserProfileAnalyzer aupa = new AjaxUserProfileAnalyzer();
        	UserProfile userprofile = new UserProfile();
        	aupa.doAnylyze(ajaxContent, userprofile);  
        	if(userprofile.getUserId() != null) {
        		super.dboperator.insertIntoUserProfile(userprofile, "user_profile");
        		LogSys.nodeLogger.debug("success url: " + URL); 
        	}
			
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
