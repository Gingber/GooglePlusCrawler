package com.iie.googleplus.Crawler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.impl.client.DefaultHttpClient;

import com.iie.googleplus.Dboperator.MySQLOperator;
import com.iie.googleplus.Platform.LogSys;
import com.iie.googleplus.Report.ReportData;
import com.iie.googleplus.analyzer.beans.GPlusUser;
import com.iie.googleplus.task.beans.Task;
import com.iie.googleplus.task.beans.Task.TaskType;
import com.iie.googleplus.tool.MulityInsertDataBase;
import com.iie.httpclient.crawler.GoogleClientManager;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpResponseException;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Activity;
import com.google.api.services.plus.model.ActivityFeed;

public class ActivityCrawler extends AbstractCrawler {
	
	public ActivityCrawler(Plus plus, MySQLOperator dbop) {
		this.plus = plus;
		super.dboperator = dbop;
	}
	
	public boolean doCrawl(Task task, MulityInsertDataBase dbo, Vector<GPlusUser> RelateUsers, ReportData reportData) {
		LogSys.nodeLogger.debug("Timeline Grabber is starting......");
		String userId = task.getTargetString();
		System.out.println("current crawl user:\t" + userId);

		try {
			List<Activity> activities = fetch(task, 0);
			if(activities != null && activities.size() >= 1){
				super.dboperator.batchInsertMessage(activities, "message");
			}
			
			// crawl message's comments
			CommentCrawler cc = new CommentCrawler(plus, super.dboperator);
			cc.fetchComments(plus, activities);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public List<Activity> fetch(Task task, int count) throws RuntimeException, IOException{

		if(count <= 2) {
			
			List<Activity> activities = listUserActivities(task);

			if(activities != null && activities.size() >= 1){
				return activities;
			} else {
				return fetch(task, count+1);
			}	
		} else {
			return null;
		}
	
	}
	
	/** List the public activities for the authenticated user. */
	public List<Activity> listUserActivities(Task task) throws IOException {
	  
		String userId = task.getTargetString();
			
		List<Activity> allActivities = new ArrayList<Activity>();
	
		try {
			Plus.Activities.List listActivities = plus.activities().list(userId, "public");
			listActivities.setMaxResults(100L);
	      
			// Execute the request for the first page
			ActivityFeed feed = listActivities.execute();
		
			// Unwrap the request and extract the pieces we want
			List<Activity> activities = feed.getItems();

			int pageNum = 0;
			// Loop through until we arrive at an empty page
			while (activities != null && activities.size() > 0) {
				for (Activity activity : activities) {
					if(activity != null) {
						allActivities.add(activity);
					} 
				}

				LogSys.nodeLogger.debug("Crawling " + (++pageNum) + " page for user's Timeline");
				
				// We will know we are on the last page when the next page token is null.
				// If this is the case, break.
				if (feed.getNextPageToken() == null) {
					break;
				}
		
				// Prepare to request the next page of activities
				listActivities.setPageToken(feed.getNextPageToken());
				
				// Execute and process the next page request
				feed = listActivities.execute();
				activities = feed.getItems();
			}
		 } catch (GoogleJsonResponseException e) {
		      GoogleJsonError error = e.getDetails();
		      LogSys.nodeLogger.debug("Error code: " + error.getCode() + "\t" + "Error message: " + error.getMessage());
		      if(error.getCode() == 403 || error.getMessage().contains("Daily Limit Exceeded")) {
		    	  if(appNum < appTotal) {	
		    		  plus = Authenticate.AuthorizedGenerator(++appNum);
		    		  fetch(task, 0);
		    	  } else {
		    		  appNum = 1;
		    		  plus = Authenticate.AuthorizedGenerator(appNum);
		    		  fetch(task, 0);
		    	  }
		      }
		      return null;
		      // More error information can be retrieved with error.getErrors().
		 } catch (HttpResponseException e) {
		     // No Json body was returned by the API.
			 LogSys.nodeLogger.debug("HTTP Status code: " + e.getStatusCode() + "\t" + "HTTP Reason: " + e.getMessage());
			 return null;
		 } catch (IOException e) {
		     // Other errors (e.g connection timeout, etc.).
			 LogSys.nodeLogger.error("an error occurred: " + e);		    
		 }

		return allActivities;
	
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		MySQLOperator dbop = new MySQLOperator();
		Plus plus = Authenticate.AuthorizedGenerator(1);
		
		ActivityCrawler activities = new ActivityCrawler(plus, dbop);
		
		Task task = new Task(TaskType.Timeline, "105397673422829042455");
		MulityInsertDataBase batchdb =  new MulityInsertDataBase();
		ReportData reportData=new ReportData();
		Vector<GPlusUser> RelateUsers = new Vector<GPlusUser>(100);
		boolean flag = activities.doCrawl(task, batchdb, RelateUsers, reportData);
		if(flag) {
			System.out.println("the messages crawl all~");
		} else {
			System.out.println("occur at error~");
		}
		
	}

}
