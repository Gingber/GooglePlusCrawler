package com.google.api.services.samples.plus.cmdline;

import java.io.IOException;
import java.io.IOException;  

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;  
import org.apache.commons.httpclient.Header;  
import org.apache.commons.httpclient.HttpClient;  
import org.apache.commons.httpclient.HttpException;  
import org.apache.commons.httpclient.HttpMethod;  
import org.apache.commons.httpclient.HttpStatus;  
import org.apache.commons.httpclient.NameValuePair;  
import org.apache.commons.httpclient.methods.GetMethod;  
import org.apache.commons.httpclient.methods.PostMethod;  
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Person;


public class HttpClientTutorial {
	
  //private static String url = "https://www.googleapis.com/plus/v1/people/104858747202533851149?key=AIzaSyANopDfUGWL-RwTEMrBGi091IYehYCbScI";
  private static String url = "https://plus.google.com/u/0/_/socialgraph/lookup/visible/?o=%5Bnull%2Cnull%2C%22109279407741322233569%22%5D&_reqid=6849188&rt=j";
	
  public static void main(String[] args) {
    // Create an instance of HttpClient.
    HttpClient client = new HttpClient();
    
    client.getHostConfiguration().setProxy("127.0.0.1", 8580);
    //ʹ��������֤
    client.getParams().setAuthenticationPreemptive(true);

    // Create a method instance.
    GetMethod method = new GetMethod(url);
    
    // Provide custom retry handler is necessary
    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
    		new DefaultHttpMethodRetryHandler(3, false));

    try {
      // Execute the method.
      int statusCode = client.executeMethod(method);

      if (statusCode != HttpStatus.SC_OK) {
        System.err.println("Method failed: " + method.getStatusLine());
      }

      // Read the response body.
      byte[] responseBody = method.getResponseBody();

      // Deal with the response.
      // Use caution: ensure correct character encoding and is not binary data
      System.out.println(new String(responseBody));

    } catch (HttpException e) {
      System.err.println("Fatal protocol violation: " + e.getMessage());
      e.printStackTrace();
    } catch (IOException e) {
      System.err.println("Fatal transport error: " + e.getMessage());
      e.printStackTrace();
    } finally {
      // Release the connection.
      method.releaseConnection();
    }  
  }
}