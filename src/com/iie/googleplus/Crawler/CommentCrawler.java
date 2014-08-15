/**
 * 
 */
package com.iie.googleplus.Crawler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpResponseException;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Activity;
import com.google.api.services.plus.model.Comment;
import com.google.api.services.plus.model.CommentFeed;
import com.iie.googleplus.Dboperator.MongoOperator;
import com.iie.googleplus.Dboperator.MySQLOperator;
import com.iie.googleplus.Platform.LogSys;
import com.iie.googleplus.Report.ReportData;
import com.iie.googleplus.analyzer.beans.GPlusUser;
import com.iie.googleplus.task.beans.Task;
import com.iie.googleplus.tool.MulityInsertDataBase;
import com.mongodb.BasicDBObject;
import com.mongodb.Bytes;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

/**
 * @author IIEIR
 *
 */
public class CommentCrawler extends AbstractCrawler {
	
	static ExecutorService executor = Executors.newSingleThreadExecutor();
	public static int count = 0; 
	
	public CommentCrawler(Plus plus, MySQLOperator dbop) {
		this.plus = plus;
		super.dboperator = dbop;
	}
	
	public boolean doCrawl(Task task, MulityInsertDataBase dbo, Vector<GPlusUser> RelateUsers, ReportData reportData) {
		LogSys.nodeLogger.debug("This is null method~");
		return true;
	}
	
	 public void fetchComments(Plus plus,  List<Activity> activitylist) {
		 LogSys.nodeLogger.debug("Comment Grabber is starting......");
		 	
		 try {
			 for(int i = 0; i < activitylist.size(); i++) {	
				 if(activitylist.get(i).getObject().getReplies().size() > 0) {
					 List<Comment> comments = crawlComments(activitylist.get(i).getId());
					 if(comments != null && comments.size() > 0) {
						 super.dboperator.batchInsertComment(comments, "comment");
					 }
				 }
				 
				 try {
					Thread.sleep(2000L);
				 } catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				 }
			 }
		 } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		 } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	 }
	
	/** Get comments for an activity. */
	 public List<Comment> crawlComments(String activityId) throws IOException {
		 
		 List<Comment> allComments = new ArrayList<Comment>();
	
		 try {
			 Plus.Comments.List listComments = plus.comments().list(activityId);
			 listComments.setMaxResults(100L);
			 
			 CommentFeed feed = listComments.execute();
			 List<Comment> comments = feed.getItems();
	   	  
			 int pageNum = 0;
			 // Loop through until we arrive at an empty page
			 while (comments != null && comments.size() > 0) {
				 for (Comment comment : comments) {
					 if(comment != null) {
						 allComments.add(comment);
					 }
				 }
				 
				 LogSys.nodeLogger.debug("Crawling " + (++pageNum) + " page for user's Comment");
	   		 
				 // We will know we are on the last page when the next page token is null.
				 // If this is the case, break.
				 if (feed.getNextPageToken() == null) { 
					 break;
				 }
	   	
				 // Prepare the next page of results
				 listComments.setPageToken(feed.getNextPageToken());
	   	
				 // Execute and process the next page request
				 feed = listComments.execute();
				 comments = feed.getItems();
				 
			 }	 
		 } catch (GoogleJsonResponseException e) {
		      GoogleJsonError error = e.getDetails();
		      LogSys.nodeLogger.debug("Error code: " + error.getCode() + "\t" + "Error message: " + error.getMessage());
		      if(error.getCode() == 403 || error.getMessage().contains("Daily Limit Exceeded")) {
		    	  if(appNum < appTotal) {
		    		  plus = Authenticate.AuthorizedGenerator(++appNum);
		    		  crawlComments(activityId);
		    	  } else {
		    		  appNum = 1;
		    		  plus = Authenticate.AuthorizedGenerator(appNum);
		    		  crawlComments(activityId);
		    	  }
		      }
		      // More error information can be retrieved with error.getErrors().
		 } catch (HttpResponseException e) {
		     // No Json body was returned by the API.
			 LogSys.nodeLogger.debug("HTTP Status code: " + e.getStatusCode() + "\t" + "HTTP Reason: " + e.getMessage());
			 return null;
		 } catch (IOException e) {
		     // Other errors (e.g connection timeout, etc.).
			 LogSys.nodeLogger.error("an error occurred: " + e);
			 return null;
		 }
		 
		 return allComments;
	 }
	 
	 public static boolean getActivityIds(DB mongodb, int skip, int limit) throws Exception {
		 
		 MySQLOperator dbop =  new MySQLOperator();
		 plus = Authenticate.AuthorizedGenerator(appNum);
			
		 
		 BasicDBObject query = new BasicDBObject();
		 query.put("id", 1);
		 DBCursor cursor = mongodb.getCollection("activity").find(new BasicDBObject(), query).skip(skip).limit(limit)
				  				.addOption(Bytes.QUERYOPTION_NOTIMEOUT);
		 try {
			 int num = 0;
			 List<String> activityIds = new ArrayList<String>();
			 while (cursor.hasNext()) {
				  String activityId = cursor.next().get("id").toString();
			      if(!activityIds.contains(activityId)) {	
			    	  activityIds.add(activityId);
			    	  System.out.println((++num) + "\t" + activityId);
			      }
			      
			      if(activityIds.size() == 10000) {
			    	  dbop.batchInsertMessage(null, "message");
			    	  activityIds = new ArrayList<String>();
			      }
			  
			 }
		 } finally {
			  cursor.close();
		 }

		 return true;
	 }
	 
	 public static boolean compareCommentId(DB mongodb) throws Exception {
		 
		 BasicDBObject query = new BasicDBObject();
		 query.put("id", true);
		 //query.put("published", new BasicDBObject("$gte", "2014-06-01").append("$lt", "2014-06-18"));

		 DBCursor cursorMore = mongodb.getCollection("comment_2014-06-20 11:29:42").find(query)
				  				.addOption(Bytes.QUERYOPTION_NOTIMEOUT);
		 
		 DBCursor cursorLess = mongodb.getCollection("comment_2014-06-20 00:12:40").find(query)
	  				.addOption(Bytes.QUERYOPTION_NOTIMEOUT);
		  
		 try {
			 int num = 0;
			 int moreNum = 0, lessNum = 0;
			 List<String> commentMoreIds = new ArrayList<String>();
			 while (cursorMore.hasNext()) {
				 BasicDBObject bdbObj = (BasicDBObject) cursorMore.next();
				 if(bdbObj != null) {
					 String commentId = bdbObj.getString("id");
					 if(!commentMoreIds.contains(commentId)) {	
						 commentMoreIds.add(commentId);
//						 System.out.println((++num) + "\t" + commentId);
						 moreNum++;
					 }
				 }
				
			 }
			 
			 while (cursorLess.hasNext()) {
				 BasicDBObject bdbObj = (BasicDBObject) cursorLess.next();
				 if(bdbObj != null) {
					 String commentId = bdbObj.getString("id");
					 if(!commentMoreIds.contains(commentId)) {	
						 //commentMoreIds.add(commentId);
						 System.out.println((++num) + "\t" + bdbObj.getString("object.content"));
						 lessNum++;
					 }
				 }
			 }
			 
			 System.out.println("moreNum = " + moreNum + "\t" + "lessNum = " + lessNum);
		  } finally {
			  cursorMore.close();
			  cursorLess.close();
		  }
		  
		 
		  return true;
	 }
		 
	 
	 

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		boolean needsProxy = true;
		if (needsProxy) {  
			System.getProperties().put("proxySet", "true");
			System.getProperties().put("proxyHost", "192.168.120.135");
			System.getProperties().put("proxyPort", "8087");
		} else {
			System.getProperties().put("proxySet", "false"); 
			System.getProperties().put("proxyHost", "");
			System.getProperties().put("proxyPort", "");
		}

		DB mongodb = MongoOperator.LinkMongodb();
		compareCommentId(mongodb);

	

	}

}
