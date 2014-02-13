package com.google.api.services.samples.plus.cmdline;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import util.TxtReader;
import util.TxtWriter;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpResponseException;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Activity;
import com.google.api.services.plus.model.ActivityFeed;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;

/** 锟斤拷态 */
public class Activities {
	
	/** List the public activities for the authenticated user. */
	public static HashMap<Integer, String> listUserActivities(Plus plus, DBCollection activityCollection, String userId) throws IOException {
	  
		View.header1("Listing User's Activities");
		System.out.println("userId = " + userId);
		
		ArrayList<String> activitylist = new ArrayList<String>();
		
		HashMap<Integer, String> statusMap = new HashMap<Integer, String>();
		Boolean flag = true;
		while(flag) {
			
			try {
				Plus.Activities.List listActivities = plus.activities().list(userId, "public");
				listActivities.setMaxResults(100L);
			
				// Pro tip: Use partial responses to improve response time considerably
				//listActivities.setFields("nextPageToken,items(id,url,object/content)");
		      
				// Execute the request for the first page
				ActivityFeed feed = listActivities.execute();
			
				// Unwrap the request and extract the pieces we want
				List<Activity> activities = feed.getItems();
				
				// read processed activity Id
				//ArrayList<String> processuserId = TxtReader.loadVectorFromFile(new File("file/processed_activityId.dat"), "UTF-8");
			  
				// Loop through until we arrive at an empty page
				while (activities != null) {
					for (Activity activity : activities) {
						if(activity != null) {
							try {  
								DBObject dbObject =(DBObject)JSON.parse(activity.toPrettyString()); 
								//System.out.println(activity.toPrettyString());
								activityCollection.insert(dbObject);
								if(!activitylist.contains(activity.getId())) {
									activitylist.add(activity.getId());
									System.out.println("activityId = " + activity.getId());
						    	    //TxtWriter.appendToFile(activity.getId(), new File("file/processed_activityId.dat"), "UTF-8");
								}
								
							} catch (Exception e) {  
		                      e.printStackTrace();  
							}  
						} 
					}
			
					// We will know we are on the last page when the next page token is null.
					// If this is the case, break.
					if (feed.getNextPageToken() == null) {
						break;
					}
			
					// Prepare to request the next page of activities
					listActivities.setPageToken(feed.getNextPageToken());
					
					// Execute and process the next page request
					//View.header2("New page of activities");
					feed = listActivities.execute();
					activities = feed.getItems();
				}		
			 } catch (GoogleJsonResponseException e) {
			      GoogleJsonError error = e.getDetails();

			      System.err.println("Error code: " + error.getCode());
			      System.err.println("Error message: " + error.getMessage());
			      statusMap.put(error.getCode(), error.getMessage());
			      // More error information can be retrieved with error.getErrors().
			 } catch (HttpResponseException e) {
			     // No Json body was returned by the API.
			      
				 System.err.println("HTTP Status code: " + e.getStatusCode());
				 System.err.println("HTTP Reason: " + e.getMessage());
				 statusMap.put(e.getStatusCode(), e.getMessage());
			 } catch (IOException e) {
			     // Other errors (e.g connection timeout, etc.).
			     System.out.println("An error occurred: " + e);			    
			 }
			flag = false;
		}
		
		return statusMap;
		
/*	  
		Mongo mg = new Mongo();
		DB db = mg.getDB("GooglePlus");
		DBCollection users = db.getCollection("userprofile");
      
		//锟斤拷询锟斤拷锟叫碉拷锟斤拷锟�
		DBObject query = new BasicDBObject();//要锟斤拷锟斤拷锟斤拷锟�
		DBObject field = new BasicDBObject();//要锟斤拷锟斤拷锟叫╋拷侄锟�
		field.put("id", true);  
		DBCursor cur = users.find(query,field); 
		int count = 0;
		while (cur.hasNext()) {
			// A known public userId(eg. 111826398079000706588)
			String userId = cur.next().get("id").toString();
			System.out.println(count + "\t" + userId);
			Plus.Activities.List listActivities = plus.activities().list(userId, "public");
			listActivities.setMaxResults(100L);
    	
			// Pro tip: Use partial responses to improve response time considerably
			//listActivities.setFields("nextPageToken,items(id,url,object/content)");
          
			// Execute the request for the first page
			ActivityFeed feed = listActivities.execute();
    	
			// Unwrap the request and extract the pieces we want
			List<Activity> activities = feed.getItems();
    	  
			// Loop through until we arrive at an empty page
			while (activities != null) {
				for (Activity activity : activities) {
					if(activity != null) {
						try {  
							DBObject dbObject =(DBObject)JSON.parse(activity.toPrettyString()); 
							//System.out.println(activity.toPrettyString());
							activityCollection.insert(dbObject);  
							count++;   
						} catch (Exception e) {  
	                      e.printStackTrace();  
						}  
					} 
					
					activity = plus.activities().get(activity.getId()).execute();
					if(activity != null) {
						try {  
							DBObject dbObject =(DBObject)JSON.parse(activity.toPrettyString());
							System.out.println(activity.toPrettyString());
							activityCollection.insert(dbObject);  
							//System.out.println(dbObject);  
							count++;   
						} catch (Exception e) {  
	                      e.printStackTrace();  
						}  
					} 
				}
    	
				// We will know we are on the last page when the next page token is null.
				// If this is the case, break.
				if (feed.getNextPageToken() == null) {
					break;
				}
    	
				// Prepare to request the next page of activities
				listActivities.setPageToken(feed.getNextPageToken());
				
				// Execute and process the next page request
				//View.header2("New page of activities");
				feed = listActivities.execute();
				activities = feed.getItems();
			}    	  
		}
		System.out.println("get list activity's number: " + count);
*/		
		
	}

	/** Search the public activities for the authenticated user. */
	public static void searchUserActivities(Plus plus, DBCollection activityCollection, String keyword) throws IOException {
	  
		View.header1("Search User's Activities");
	  
		Plus.Activities.Search searchActivities = plus.activities().search(keyword);
		searchActivities.setMaxResults(100L);

		ActivityFeed activityFeed = searchActivities.execute();
		List<Activity> activities = activityFeed.getItems();

		// 一直循锟斤拷锟斤拷直锟斤拷锟津开空帮拷页
		int count = 0;
		while (activities != null) {
			for (Activity activity : activities) {
				activity = plus.activities().get(activity.getId()).execute();
				if(activity != null) {
					try {  
						DBObject dbObject =(DBObject)JSON.parse(activity.toPrettyString());  
						activityCollection.insert(dbObject);  
						count++;   
					} catch (Exception e) {  
                      e.printStackTrace();  
					}  
				} 
			}

		  // 锟斤拷锟斤拷一页锟斤拷锟斤拷为锟斤拷时锟斤拷锟斤拷锟角就伙拷知锟斤拷锟皆硷拷位锟斤拷锟斤拷锟揭灰筹拷锟�
		  // 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟绞憋拷锟斤拷锟斤拷循锟斤拷锟斤拷
		  if (activityFeed.getNextPageToken() == null) {
			  break;
		  }

		  // 准锟斤拷锟斤拷锟斤拷锟斤拷一页锟斤拷态
		  searchActivities.setPageToken(activityFeed.getNextPageToken());

		  // 执锟叫诧拷锟斤拷锟斤拷锟斤拷一页锟斤拷锟斤拷
		  activityFeed = searchActivities.execute();
		  activities = activityFeed.getItems();
		}
		System.out.println("search activity's number: " + count);
	}
	
	  
}
