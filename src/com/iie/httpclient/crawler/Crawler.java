package com.iie.httpclient.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/*
 * crawl the page 
 * */
public class Crawler {
	private String content;

	public Crawler(String url, String cookie) throws ClientProtocolException, IOException{
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
		GoogleClientManager gcm = new GoogleClientManager();		
		HttpGet httpget = new HttpGet(url);
        httpget.setHeader("cookie",cookie);           
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        if(entity !=null){ 
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(entity.getContent(),"utf-8"));
            try {
                StringBuilder sb = new StringBuilder(); 
                String line = null;
                while((line = reader.readLine()) != null){
                    //System.out.println(str);
                    sb.append(line);
                    sb.append('\n');
                }
                this.content = sb.toString();
                //TxtWriter.saveToFile(sb.toString(), new File(userInfoPath), "UTF-8");
            } catch (IOException ex) {
                throw ex;
            } catch (RuntimeException ex) {
                throw ex;
            } finally {
                reader.close();
            }
        }
        EntityUtils.consume(entity);	
	}
	
	public String GetPageContent(){
		return this.content;
	}
}
