/**
 * 
 */
package com.iie.googleplus.Crawler;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.Set;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
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

/**
 * @author Gingber
 *
 */
public class Authenticate {
	
 
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
				new InputStreamReader(new FileInputStream("resources/client_secrets_" + appNum + ".json")));
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
	
	public static Plus AuthorizedGenerator(int appNum) {
		
		try {
			
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			// initialize the data store factory
			DATA_STORE_DIR = new java.io.File(System.getProperty("user.dir"), ".store/plus_app_" + appNum);
		    dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
	 
		    // authorization 
		    final Credential credential = authorize(appNum);

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
		          }).build();
		      
		} catch (GeneralSecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return plus;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
