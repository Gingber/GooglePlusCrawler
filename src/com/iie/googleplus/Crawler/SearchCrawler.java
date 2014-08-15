/**
 * 
 */
package com.iie.googleplus.Crawler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpResponseException;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Activity;
import com.google.api.services.plus.model.ActivityFeed;
import com.iie.googleplus.Dboperator.MySQLOperator;
import com.iie.googleplus.Platform.LogSys;
import com.iie.googleplus.Report.ReportData;
import com.iie.googleplus.analyzer.beans.GPlusUser;
import com.iie.googleplus.task.beans.Task;
import com.iie.googleplus.tool.MulityInsertDataBase;

/**
 * @author IIEIR
 *
 */
public class SearchCrawler extends AbstractCrawler {
		
	public SearchCrawler(Plus plus, MySQLOperator dbop) {
		this.plus = plus;
		super.dboperator = dbop;
	}
	
	public boolean doCrawl(Task task, MulityInsertDataBase dbo, Vector<GPlusUser> RelateUsers, ReportData reportData) {
		LogSys.nodeLogger.debug("Search Grabber is starting......");
		String keyword = task.getTargetString();
		System.out.println("current crawl keyword:\t" + keyword);
		
		try {
			List<Activity> activities = fetch(task, 0);
			if(activities != null && activities.size() >= 1){
				super.dboperator.batchInsertMessage(activities, "message");
			}
			
			// crawl message's comments
			CommentCrawler cc = new CommentCrawler(plus, super.dboperator);
			cc.fetchComments(plus, activities);
			
			for(int i = 0; i < activities.size(); i++) {
				GPlusUser gpUser = new GPlusUser();
				gpUser.setUserId(activities.get(i).getActor().getId());
				gpUser.setUserName(activities.get(i).getActor().getDisplayName());
				if(!RelateUsers.contains(gpUser)) {
					RelateUsers.add(gpUser);
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogSys.nodeLogger.debug("插入数据库语句发生错误");
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
			
			List<Activity> activities = searchActivities(task);

			if(activities != null && activities.size() >= 1){
				return activities;
			} else {
				return fetch(task, count+1);
			}	
		} else {
			return null;
		}
	
	}
	
	/** Search the public activities for the authenticated user. */
	public List<Activity> searchActivities(Task task) throws IOException {
	  
		List<Activity> allActivities = new ArrayList<Activity>();
		
		try {
			String keyword = task.getTargetString();
			
			Plus.Activities.Search searchActivities = plus.activities().search(keyword);
			searchActivities.setMaxResults(5L);
	
	  	    ActivityFeed activityFeed = searchActivities.execute();
	  	    List<Activity> activities = activityFeed.getItems();

	  	    int pageNum = 0;
	  	    while (activities != null && activities.size() > 0 && pageNum <= 2) {
	  	    	for (Activity activity : activities) {
	  	    		if(activity != null) {
	  	    			allActivities.add(activity);
	  	    		}
	  	    	}
	  	    	
	  	    	LogSys.nodeLogger.debug("Crawling " + (++pageNum) + " page for keyword: " + keyword);

		  	    if (activityFeed.getNextPageToken() == null) {
		  	    	break;
		  	    }

		  	    searchActivities.setPageToken(activityFeed.getNextPageToken());

		  	    try {
					Thread.sleep(2000L);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
		  	    activityFeed = searchActivities.execute();
		  	    activities = activityFeed.getItems();

	  	    	
	  	    }
		} catch (GoogleJsonResponseException e) {
			GoogleJsonError error = e.getDetails();
			// More error information can be retrieved with error.getErrors().
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
				
		} catch (HttpResponseException e) {	
			// No Json body was returned by the API.
			LogSys.nodeLogger.debug("HTTP Status code: " + e.getStatusCode() + "\t" + "HTTP Reason: " + e.getMessage());
			return null;
		} catch (IOException e) {
			// Other errors (e.g connection timeout, etc.).
			LogSys.nodeLogger.error("An error occurred: " + e);
			return null;
		}
		
		return allActivities;
	}
		  	  

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
