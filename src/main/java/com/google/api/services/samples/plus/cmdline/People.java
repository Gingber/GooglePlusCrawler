package com.google.api.services.samples.plus.cmdline;

import java.io.IOException;
import java.util.List;

import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.PeopleFeed;
import com.google.api.services.plus.model.Person;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;

/** 锟斤拷员锟斤拷  */
public class People {
	/** Get the people list for the social circle of the authenticated user. */
	public static void listUserProfile(Plus plus, DBCollection meprofileCollection, String userId) throws IOException {
	    
		  
		View.header1("Get my Google+ profile");
		System.out.println("userId = " + userId);

		Person person = plus.people().get(userId).execute();
		 if (person != null) {  
        	 //System.out.println(person.toPrettyString());            
        	 try {  
        		 DBObject dbObject =(DBObject)JSON.parse(person.toPrettyString());  
        		 meprofileCollection.insert(dbObject);  
        		 //System.out.println("success insert " + (++count) + " user"); 
        	 } catch (Exception e) {      		  
        		 e.printStackTrace();  
        	 }              
         }  

/*		
  		Plus.People.List listPeople = plus.people().list("me", "visible");
		listPeople.setMaxResults(100L); 
		PeopleFeed peopleFeed = listPeople.execute();
		List<Person> people = peopleFeed.getItems();
	    
		 //Loop through until we arrive at an empty page
		 int count = 0;
		 while (people != null) {
			 for (Person person : people) {
	             person = plus.people().get(person.getId()).execute();
	             //Loop through until we arrive at an empty userId
	             if (person != null) {  
	            	 //System.out.println(person.toPrettyString());            
	            	 try {  
	            		 DBObject dbObject =(DBObject)JSON.parse(person.toPrettyString());  
	            		 meprofileCollection.insert(dbObject);  
	            		 //System.out.println("success insert " + (++count) + " user"); 
	            		 count++;
	            	 } catch (Exception e) {  
	           		  e.printStackTrace();  
	            	 }              
	             }  
			 }
	    
		     // We will know we are on the last page when the next page token is null.
		     // If this is the case, break.
		     if (peopleFeed.getNextPageToken() == null) {
		             break;
		     }
		    
		     // Prepare the next page of results
		     listPeople.setPageToken(peopleFeed.getNextPageToken());
		    
		     // Execute and process the next page request
		     peopleFeed = listPeople.execute();
		     people = peopleFeed.getItems();   
		 }
		 System.out.println("Get my Google+ user's number: " + count);
*/		 
	}
	
	/** search some people when we already know user name. */
	/** only return 1000 records. */ 
	public static void searchUserProfile(Plus plus, DBCollection searchCollection, String username) throws IOException {

		// Brett, Brit Morin 
		Plus.People.Search searchPeople = plus.people().search(username);
		searchPeople.setMaxResults(50L);

		 PeopleFeed peopleFeed = searchPeople.execute();
		 List<Person> people = peopleFeed.getItems();
		  
		 int count = 0;
		 // Loop through until we arrive at an empty page
		 while (people != null) {
			 for (Person person : people) {
				 person = plus.people().get(person.getId()).execute();
	             //Loop through until we arrive at an empty userId
	             if (person != null) {  
	            	 //System.out.println(person.toPrettyString());            
	            	 try {  
	            		 DBObject dbObject =(DBObject)JSON.parse(person.toPrettyString());  
	            		 searchCollection.insert(dbObject);  
	            		 count++;
	            	 } catch (Exception e) {  
	            		 e.printStackTrace();  
	            	 }              
	             }  
			 }

			 // We will know we are on the last page when the next page token is null.
			 // If this is the case, break.
			 if (peopleFeed.getNextPageToken() == null) {
				 break;
			 }

			 // Prepare the next page of results
			 searchPeople.setPageToken(peopleFeed.getNextPageToken());

			 // Execute and process the next page request
			 peopleFeed = searchPeople.execute();
			 people = peopleFeed.getItems();
		 }
		 System.out.println("search user's number: " + count);
	}
	
	/** list user profile when we already know activity Id. */
	/** only return 1000 records. */ 
	public static void ListUserProfileByActivity(Plus plus, DBCollection searchCollection) throws IOException {

		View.header1("Listing User's Profile By Activities");
		
		Mongo mg = new Mongo();
		DB db = mg.getDB("GooglePlus");
		DBCollection users = db.getCollection("activity");
      
		//锟斤拷询锟斤拷锟叫碉拷锟斤拷锟�
		DBObject query = new BasicDBObject();//要锟斤拷锟斤拷锟斤拷锟�
		DBObject field = new BasicDBObject();//要锟斤拷锟斤拷锟叫╋拷侄锟�
		field.put("id", true);  
		DBCursor cur = users.find(query,field); 
		int count = 0;
		while (cur.hasNext()) {
			String activityId = cur.next().get("id").toString();
			/** plusoners : 锟叫筹拷锟皆此讹拷态执锟斤拷 +1 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷员锟斤拷
			 *  resharers : 锟叫筹拷转锟斤拷锟剿讹拷态锟斤拷锟斤拷锟斤拷锟斤拷员锟斤拷
			*/
			Plus.People.ListByActivity listPeople = plus.people().listByActivity(activityId, "plusoners");
			listPeople.setMaxResults(5L);

			PeopleFeed peopleFeed = listPeople.execute();
			List<Person> people = peopleFeed.getItems();

			// 一直循锟斤拷锟斤拷直锟斤拷锟津开空帮拷页
			while (people != null) {
				for (Person person : people) {
					person = plus.people().get(person.getId()).execute();
					//Loop through until we arrive at an empty userId
		            if (person != null) {  
		            	//System.out.println(person.toPrettyString());            
		            	try {  
		            		 
		            		DBObject dbObject =(DBObject)JSON.parse(person.toPrettyString());  
		            		searchCollection.insert(dbObject);  
		            		count++;
		            	} catch (Exception e) {  
		            		 e.printStackTrace();  
		            	}              
		            }  
				}

			  // 锟斤拷锟斤拷一页锟斤拷锟斤拷为锟斤拷时锟斤拷锟斤拷锟角就伙拷知锟斤拷锟皆硷拷位锟斤拷锟斤拷锟揭灰筹拷锟�
			  // 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟绞憋拷锟斤拷锟斤拷循锟斤拷锟斤拷
			  if (peopleFeed.getNextPageToken() == null) {
				  break;
			  }

			  // 准锟斤拷锟斤拷一锟斤拷锟揭筹拷锟�
			  listPeople.setPageToken(peopleFeed.getNextPageToken());

			  // 执锟叫诧拷锟斤拷锟斤拷锟斤拷一页锟斤拷锟斤拷
			  peopleFeed = listPeople.execute();
			  people = peopleFeed.getItems();
			}
		}
		System.out.println("list user profile by activity user's number: " + count);
	}


}
