package com.google.api.services.samples.plus.cmdline;

import java.io.IOException;
import java.util.List;

import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Comment;
import com.google.api.services.plus.model.CommentFeed;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;

/** 锟斤拷锟斤拷 */
public class Comments {

	 /** Get comments for an activity. */
	 public static void listUserComment(Plus plus, DBCollection commentCollection, String activityId) throws IOException {
		  
		 //View.header1("Get Activity's Comments");
		 
		 
		 Plus.Comments.List listComments = plus.comments().list(activityId);
    	 listComments.setMaxResults(100L);
    	
    	 CommentFeed feed = listComments.execute();
    	 List<Comment> comments = feed.getItems();
    	  
    	 // Loop through until we arrive at an empty page
    	 while (comments != null) {
    		 for (Comment comment : comments) {
    			 /*System.out.println(comment.toPrettyString());
    			 comment = plus.comments().get(comment.getId()).execute();*/
    			 if(comment != null) {
    				 try {  
	    				 DBObject dbObject =(DBObject)JSON.parse(comment.toPrettyString());  
	                     commentCollection.insert(dbObject); 
	                     //System.out.println("userId = " + comment.getId());
	                 }  catch (Exception e) {  
	                      e.printStackTrace();  
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
	 }
		 
/*		  
		 Mongo mg = new Mongo();
	     DB db = mg.getDB("GooglePlus");
	     DBCollection users = db.getCollection("activity");
	      
	     //锟斤拷询锟斤拷锟叫碉拷锟斤拷锟�
	     DBObject query = new BasicDBObject();//要锟斤拷锟斤拷锟斤拷锟�
	     DBObject field = new BasicDBObject();//要锟斤拷锟斤拷锟叫╋拷侄锟�
	     field.put("id", true);  
	     DBCursor cur = users.find(query,field);
	     int count  = 0;
	     while (cur.hasNext()) {
	    	 // A known public userId(eg. z13lerkrisz0uvxll22dep5w2sucyrg0r)
	    	 String activityId = cur.next().get("id").toString(); 
	    	 // A known public activity ID
	    	 Plus.Comments.List listComments = plus.comments().list(activityId);
	    	 listComments.setMaxResults(100L);
	    	
	    	 CommentFeed feed = listComments.execute();
	    	 List<Comment> comments = feed.getItems();
	    	  
	    	 // Loop through until we arrive at an empty page
	    	 while (comments != null) {
	    		 for (Comment comment : comments) {
	    			 System.out.println(comment.toPrettyString());
	    			 comment = plus.comments().get(comment.getId()).execute();
	    			 if(comment != null) {
	    				 try {  
		    				 DBObject dbObject =(DBObject)JSON.parse(comment.toPrettyString());  
		                     commentCollection.insert(dbObject);   
		                     count++;  
		                 }  catch (Exception e) {  
		                      e.printStackTrace();  
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
	      
	     }
	     System.out.println("get comment's number: " + count);
	 }
	*/ 
	 
}
