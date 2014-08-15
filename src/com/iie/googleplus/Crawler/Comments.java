/**
 * 
 */
package com.iie.googleplus.Crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpResponseException;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Comment;
import com.google.api.services.plus.model.CommentFeed;
import com.iie.googleplus.Dboperator.MongoOperator;
import com.iie.googleplus.Dboperator.MySQLOperator;
import com.iie.taskdistributor.Task;
import com.iie.taskdistributor.TaskDistributor;
import com.iie.taskdistributor.WorkThread;
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
public class Comments {
	
	static ExecutorService executor = Executors.newSingleThreadExecutor();
	public static int count = 0; 
	public static int appNum = 1;
	public static Plus plus = null;
	
	/** Get comments for an activity. */
	 public static Map<Integer, String> getComments(Plus plus, DBCollection commentCollection, String activityId) throws IOException {
		 
		 //View.header1("Get Activity's Comments");
		 
		 Plus.Comments.List listComments = plus.comments().list(activityId);
		 listComments.setMaxResults(100L);
		 
		 Map<Integer, String> reponse = new HashMap<Integer, String>();
	
		 try {
			 CommentFeed feed = listComments.execute();
			 List<Comment> comments = feed.getItems();
	   	  
			 // Loop through until we arrive at an empty page
			 while (comments != null) {
				 for (Comment comment : comments) {
					 if(comment != null) {
						 try {  
							 DBObject dbObject =(DBObject)JSON.parse(comment.toPrettyString());  
		                     commentCollection.insert(dbObject); 
		                     System.out.println((count++) + "\t" + comment.getId()+ "\t" + comment.getActor().getDisplayName()
		                    		 + "\t" + comment.getObject().getContent());
		                 } catch (Exception e) {  
		                      e.printStackTrace();
		                      reponse.put(404, e.toString());
		                      return reponse;
		                 }  
					 }
				 }
	   		 
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
			 
			 reponse.put(200, "OK");
			 
		 } catch (GoogleJsonResponseException e) {
		      GoogleJsonError error = e.getDetails();
		      System.err.println("Error code: " + error.getCode());
		      System.err.println("Error message: " + error.getMessage());
		      reponse.put(error.getCode(), error.getMessage());
		      return reponse;
		      // More error information can be retrieved with error.getErrors().
		 } catch (HttpResponseException e) {
		      // No Json body was returned by the API.
		      System.err.println("HTTP Status code: " + e.getStatusCode());
		      System.err.println("HTTP Reason: " + e.getMessage());
		      reponse.put(e.getStatusCode(), e.getMessage());
		      return reponse;
		 } catch (IOException e) {
		      // Other errors (e.g connection timeout, etc.).
		      System.out.println("An error occurred: " + e);
		      reponse.put(503, e.toString());
		      return reponse;
		 }
		 
		 return reponse;
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
				  
				  //System.out.println(cursor.next().get("id"));
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
