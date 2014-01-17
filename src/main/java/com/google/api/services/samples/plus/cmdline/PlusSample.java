/*
 * Copyright (c) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.api.services.samples.plus.cmdline;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.PlusScopes;
import com.google.api.services.plus.model.PeopleFeed;
import com.google.api.services.plus.model.Person;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONException;
import org.json.JSONObject;

import util.dbOperator;

/**
 * Main class for the Google+ API command line sample.
 * Demonstrates how to make an authenticated API call using OAuth 2 helper classes.
 */
public class PlusSample {

  /**
   * Be sure to specify the name of your application. If the application name is {@code null} or
   * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
   */
  private static final String APPLICATION_NAME = "MyGooglePlusProject";

  /** Directory to store user credentials. */
  private static java.io.File DATA_STORE_DIR;

  /**
   * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
   * globally shared instance across your application.
   */
  private static FileDataStoreFactory dataStoreFactory;

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  /** Global instance of the HTTP transport. */
  private static NetHttpTransport httpTransport;

  @SuppressWarnings("unused")
  private static Plus plus;

  /** Authorizes the installed application to access user's protected data. */
  private static Credential authorize(int appNum) throws Exception {
    // load client secrets
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
        new InputStreamReader(PlusSample.class.getResourceAsStream("/client_secrets_" + appNum + ".json")));
    if (clientSecrets.getDetails().getClientId().startsWith("Enter") ||
        clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
      System.out.println(
          "Overwrite the src/main/resources/client_secrets.json file with the client secrets file "
          + "you downloaded from the Quickstart tool or manually enter your Client ID and Secret "
          + "from https://code.google.com/apis/console/?api=plus#project:411821161553 "
          + "into src/main/resources/client_secrets.json");
      System.exit(1);
    }

    // Set up authorization code flow.
    // Ask for only the permissions you need. Asking for more permissions will
    // reduce the number of users who finish the process for giving you access
    // to their accounts. It will also increase the amount of effort you will
    // have to spend explaining to users what you are doing with their data.
    // Here we are listing all of the available scopes. You should remove scopes
    // that you are not actually using.
    Set<String> scopes = new HashSet<String>();
    scopes.add(PlusScopes.PLUS_LOGIN);
    scopes.add(PlusScopes.PLUS_ME);
    scopes.add(PlusScopes.USERINFO_EMAIL);
    scopes.add(PlusScopes.USERINFO_PROFILE);

    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        httpTransport, JSON_FACTORY, clientSecrets, scopes)
        .setDataStoreFactory(dataStoreFactory)
        .setAccessType("offline")
        .build();
    // authorize
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
  }
  
  public static DBCollection userprofileCollection, listByActivityCollection, searchCollection,
  							 peoplelistCollection, activityCollection, commentCollection;  
  public static Mongo mongo;  
  public static int count = 1;  
    
  public static void LinkMongodb() throws Exception {  
        
      /*  
       * Link Mongodb   
       * build a data named FourS2  
       * build a collection named Foursquare  
       *    
       */  
      mongo = new Mongo("localhost", 27017);  
      DB db = mongo.getDB("GooglePlus");
      userprofileCollection = db.getCollection("userprofile");
      searchCollection = db.getCollection("searchprofile");
      listByActivityCollection = db.getCollection("userprofileByActivity");
      
      peoplelistCollection = db.getCollection("peoplelist");
      
      activityCollection = db.getCollection("activity");
      commentCollection = db.getCollection("comment");
      
      System.out.println("Link Mongodb!");  
  }  

  public static void main(String[] args) throws SQLException, IOException {
	  
	  try {
    	  Crawler();
      } catch(IOException e) {
    	  System.err.println(e.getMessage());
    	  Crawler();
      }
	  
	    
  }
  
  private static void Crawler() throws IOException, SQLException{
	  
	  try {

	      /*******************************Database Initialization*******************************/
	      String sql = "select * from user";     
	      dbOperator.connSQL();    
	      ResultSet rs = dbOperator.selectSQL(sql);     
	        
	      List<String> userIdlist = new ArrayList<String>();
	      while (rs.next()) { 
 
	          String user_id = rs.getString("user_id");  

	          //System.out.println(user_id);
	          if (!user_id.isEmpty()) {
	        	  userIdlist.add(user_id);
	          }
	      }  
	      dbOperator.deconnSQL();   
	      
	      // connect mongodb
	      try {
	    	  LinkMongodb();
	      }  catch (Exception e) {
				e.printStackTrace();
	      }
	      /*******************************Database Finish**************************************/
	      
	      /*******************************Google+ Initialization*******************************/
	      for (int i = 1; i <= 4; i++) {
	    	  
	    	  long startTime = System.currentTimeMillis();    //获取�?��时间
	    	  
	    	  // Set the http proxy to "127.0.0.1:8580" to solution connection time out or read time out
			  //Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8580));
			  //httpTransport = new NetHttpTransport.Builder().setProxy(proxy).build();
			  
		      // initialize the transport
		      httpTransport = GoogleNetHttpTransport.newTrustedTransport();

		      // initialize the data store factory
			  DATA_STORE_DIR = new java.io.File(System.getProperty("user.dir"), ".store/plus_app_" + i);
		      dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

		      // authorization
		      final Credential credential = authorize(i);

		      // set up global Plus instance
		      plus = new Plus.Builder(httpTransport, JSON_FACTORY, credential)
		          .setApplicationName(APPLICATION_NAME)
		          .setHttpRequestInitializer(new HttpRequestInitializer() {
		        	  @Override
		        	  public void initialize(HttpRequest httpRequest) throws IOException {
		        		  credential.initialize(httpRequest);
		        		  httpRequest.setConnectTimeout(3*60000);  // 3 minutes connect timeout
		        		  httpRequest.setReadTimeout(3*60000);  // 3 minutes read timeout
		        	  }
		          })
		          .build();

		      System.out.println("Success! Now add code here.");
		      System.out.println("**********************************************");
		      
		      for(int j = 0; j < userIdlist.size(); j++) {
		    	  // run commands
			      /** People   */
			      People.listUserProfile(plus, userprofileCollection, userIdlist.get(j));
			      
			      /*String username = "Christmas";
			      People.searchUserProfile(plus, searchCollection, username);*/
			      
			      //People.ListUserProfileByActivity(plus, listByActivityCollection);
			      
			      /** Activities  */
			      ArrayList<String> activitylist = Activities.listUserActivities(plus, activityCollection, userIdlist.get(j));
			      
			      /*String keyword = "awesome";
			      Activities.searchUserActivities(plus, activityCollection, keyword);*/
			      
			      /*View.header1("Get Activity's Comments");
			      for (int k = 0; k < activitylist.size(); k++) {
			    	  *//** Comments  *//*
				      Comments.listUserComment(plus, commentCollection, activitylist.get(k));
			      }*/
			      
			      long endTime = System.currentTimeMillis();    //获取结束时间
			      long diffHours = (endTime - startTime) / (1000 * 60 * 60);
			      if (diffHours > 6) // Google+ application request exhausted 
			    	  break;
		      } 
	      }
	      
	      /*******************************Database Finish**************************************/

	      // success!
	      mongo.close();

	    } catch (IOException e) {
	      System.err.println(e.getMessage());
	    } catch (Throwable t) {
	      t.printStackTrace();
	    }
	    //System.exit(1);
	  
  }
  
  private static void GetProfile() throws IOException {
	  
	 /* HttpClient httpClient = MpUtils.createHttpClient();
	  HttpGet get = new HttpGet("https://www.googleapis.com/oauth2/v1/userinfo?alt=json" + "&access_token=ya29.1.AADtN_V1DfC8xA_RxwuVNRpbzg2U4raOY9Wzqz1ZXrf9wUysNgWCLBz2NiTU93xMV0Dhiw");
	  HttpResponse response = httpClient.execute(get);*/

	  
	  final GoogleCredential credential = new GoogleCredential().setAccessToken("ya29.1.AADtN_V1DfC8xA_RxwuVNRpbzg2U4raOY9Wzqz1ZXrf9wUysNgWCLBz2NiTU93xMV0Dhiw");
	  Plus plus = new Plus.Builder(httpTransport, JSON_FACTORY, credential)
	  	.setApplicationName(APPLICATION_NAME)
        .setHttpRequestInitializer(credential)
        .build();
	  
	  
	  Plus.People.List listPeople = plus.people().list("114812034185904027002", "visible");
      listPeople.setMaxResults(100L);

      PeopleFeed peopleFeed = listPeople.execute();
      List<Person> people = peopleFeed.getItems();
      
      //Loop through until we arrive at an empty page
      while (people != null) {
    	  for (Person person : people) {
                System.out.println(person.getDisplayName());
                System.out.println(person.toPrettyString());   
    	  }
      }
	  
          
	  BufferedReader bRead = null; 
	  JSONObject profile = null;
	  try {
		  System.out.println("Processing profile of  114812034185904027002");
          boolean flag = true;
          URL url = new URL("https://www.googleapis.com/plus/v1/people/114812034185904027002/people/visible?key={AIzaSyCkXakZmy1bDoPjikhKSBPRI_oorjKoJBo}");
          Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8580));
          HttpURLConnection huc = (HttpURLConnection) url.openConnection(proxy);
          huc.setConnectTimeout(6000);
          huc.setReadTimeout(6000);
          huc.setRequestMethod("GET");
          
          
          // Step 2: Sign the request using the OAuth Secret
          //Consumer.sign(huc);
          huc.connect();
          if(huc.getResponseCode()==404||huc.getResponseCode()==401)
          {
             System.out.println(huc.getResponseMessage());
          }           
          else
          if(huc.getResponseCode()==500||huc.getResponseCode()==502||huc.getResponseCode()==503)
          {
              try {
                  huc.disconnect();
                  System.out.println(huc.getResponseMessage());
                  Thread.sleep(3000);
              } catch (InterruptedException ex) {
                  ex.printStackTrace();
              }
          }
          else
              // Step 3: If the requests have been exhausted, then wait until the quota is renewed
          if(huc.getResponseCode()==429)
          {
              try {
                  huc.disconnect();
                  Thread.sleep(6000);
                  flag = false;
              } catch (InterruptedException ex) {
                  ex.printStackTrace();
              }
          }
          if(!flag)
          {
              //recreate the connection because something went wrong the first time.
              huc.connect();
          }
          StringBuilder content=new StringBuilder();
          if(flag)
          {
              bRead = new BufferedReader(new InputStreamReader((InputStream) huc.getContent()));
              String temp= "";
              while((temp = bRead.readLine())!=null)
              {
                  content.append(temp);
              }
          }
          huc.disconnect();
          try {
              profile = new JSONObject(content.toString());
              System.out.println(profile);
          } catch (JSONException ex) {
              ex.printStackTrace();
          }
      } catch (IOException ex) {
          ex.printStackTrace();
      }
  }
 
}
