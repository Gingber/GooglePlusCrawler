/**
 * 
 */
package com.iie.googleplus.Crawler;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpResponseException;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.PeopleFeed;
import com.google.api.services.plus.model.Person;
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
 * @author Gingber
 *
 */
public class PeopleCrawler extends AbstractCrawler {
	
	//private Plus plus;
	public static int appNum = 1;
	
	public PeopleCrawler(Plus plus, MySQLOperator dbop) {
		this.plus = plus;
		super.dboperator = dbop;
	}
	
	public boolean doCrawl(Task task, MulityInsertDataBase dbo, Vector<GPlusUser> RelateUsers, ReportData reportData) {
		LogSys.nodeLogger.debug("UserProfile Grabber is starting......");
		String userId = task.getTargetString();
		
		try {
			Person person = fetch(task, 0);
			if(person != null && person.size() >= 1){
				super.dboperator.insertIntoUserProfile(person, "user_profile");
			}
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
	
	public static Person fetch(Task task, int count) throws RuntimeException, IOException{
		
		if(count <= 2) {
			Person person;
			person = getUserById(task);
			
			if(person != null && person.size() >= 1){
				return person;
			} else {
				return fetch(task, count+1);
			}
			
		} else {
			return null;
		}
	
	}
	
	
	/** Get the people list for the social circle of the authenticated user. */
	public static Person getUserById(Task task) throws IOException {

		String userId = task.getTargetString();
		System.out.println("userId = " + userId);
		Person person = null;
		try {

			person = plus.people().get(userId).execute();

		} catch (GoogleJsonResponseException e) {
			GoogleJsonError error = e.getDetails();
			System.err.println("Error code: " + error.getCode());
		    System.err.println("Error message: " + error.getMessage());
		    LogSys.nodeLogger.debug("Daily Limit Exceeded");
		    if(error.getCode() == 403 || error.getMessage().contains("Daily Limit Exceeded")) {
				plus = Authenticate.AuthorizedGenerator(++appNum);
				fetch(task, 0);
			}
		    return null;
		    // More error information can be retrieved with error.getErrors().
		} catch (HttpResponseException e) {
			// No Json body was returned by the API.
		    System.err.println("HTTP Status code: " + e.getStatusCode());
		    System.err.println("HTTP Reason: " + e.getMessage());
		    LogSys.nodeLogger.debug("Http Response Exception");
		    return null;
		} catch (IOException e) {
			// Other errors (e.g connection timeout, etc.).
		    LogSys.nodeLogger.error("an error occurred: " + e);
		    return null;
		}
		
		return person;
	}
	
	/** search some people when we already know user name. */
	/** only return 1000 records. */ 
	public static Map<Integer, String> searchUserByName(Plus plus, DBCollection searchCollection, String username) throws IOException {

		// Brett, Brit Morin 
		Plus.People.Search searchPeople = plus.people().search(username);
		searchPeople.setMaxResults(50L);

		Map<Integer, String> reponse = new HashMap<Integer, String>();
		
		try {
			 PeopleFeed peopleFeed = searchPeople.execute();
			 List<Person> people = peopleFeed.getItems();
			  
			 int count = 0;
			 // Loop through until we arrive at an empty page
			 while (people != null && people.size() != 0) {
				 for (Person person : people) {
					 person = plus.people().get(person.getId()).execute();
		             //Loop through until we arrive at an empty userId
		             if (person != null) {  
		            	 System.out.println((++count) + "\t" + person.toString());            
		            	 try {  
		            		 DBObject dbObject =(DBObject)JSON.parse(person.toPrettyString());  
		            		 searchCollection.insert(dbObject);  
//		            		 if(count > 100) {
//		            			 return reponse;
//		            		 }
		            	 } catch (Exception e) {  
		            		 e.printStackTrace();
		            		 reponse.put(404, e.toString());
		            		 return reponse;
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
	
	/** list user profile when we already know activity Id. */
	/** only return 1000 records. */ 
	public static void getUserByActivity(Plus plus, DBCollection searchCollection, String activityId) throws IOException {

		View.header1("Listing User's Profile By Activities");

		Plus.People.ListByActivity listPeople = plus.people().listByActivity(activityId, "plusoners");
		listPeople.setMaxResults(5L);

		PeopleFeed peopleFeed = listPeople.execute();
		List<Person> people = peopleFeed.getItems();

		int num = 0;
		while (people != null) {
			for (Person person : people) {
				System.out.println((++num) + "\t" + person.toString());
			}
			
			if (peopleFeed.getNextPageToken() == null) {
				break;
			}
		  
			listPeople.setPageToken(peopleFeed.getNextPageToken());
			
			peopleFeed = listPeople.execute();
		  	people = peopleFeed.getItems();
		}

	}
	
	public static List<String> getUserIds(DB mongodb) throws Exception {
		
		List<String> userIds = new ArrayList<String>();
		
		BasicDBObject field = new BasicDBObject();
		field.put("actor.id", true);
		DBCursor cursor = mongodb.getCollection("comment").find(new BasicDBObject(), field)
				  				.addOption(Bytes.QUERYOPTION_NOTIMEOUT);
		try { 
			int num = 0; 
			while (cursor.hasNext()) {
				BasicDBObject bdbObj = (BasicDBObject) cursor.next();
				if(bdbObj != null){
					String userId = ((DBObject)bdbObj.get("actor")).get("id").toString();  
					if(!userIds.contains(userId)) {	
						userIds.add(userId);
						System.out.println((++num) + "\t" + userId);
					}
				}
				
			}
		} finally {	  
			cursor.close();
		}
		  
		  return userIds;
	 }
	
	public static void getUserByActivity() throws Exception {
		Plus plus = Authenticate.AuthorizedGenerator(appNum);

		final DB mongodb = MongoOperator.LinkMongodb();
		PeopleCrawler.getUserByActivity(plus, mongodb.getCollection("user_profile"), "z12fgvajaprtvpu1322uttwrdwuhurnid04");

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
		
		getUserByActivity();

	}

}
