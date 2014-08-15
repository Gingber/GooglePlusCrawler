package com.iie.httpclient.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import com.iie.util.TxtWriter;
 
/**
 * A example that demonstrates how HttpClient APIs can be used to perform
 * form-based logon.
 */
public class FetchByHttpClient2 {
 
    @SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {
    	
        //String url = "https://plus.google.com/108600099996421567363/posts";
        String url = "https://plus.google.com/u/0/+AiMoDu/";
        String commniuteUrl = "https://plus.google.com/u/0/communities/101399526652980913951";
           
        GoogleClientManager gcm = new GoogleClientManager();
		DefaultHttpClient httpclient = gcm.getClientNoProxy();
         
        try {
        	
            HttpGet httpget = new HttpGet("https://www.google.com/");
 
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
 
            System.out.println("Login form get: " + response.getStatusLine());
            EntityUtils.consume(entity);
  
            System.out.println("Initial set of cookies:");
            List<Cookie> cookies = httpclient.getCookieStore().getCookies();
            if (cookies.isEmpty()) {
                System.out.println("None");
            } else {
                for (int i = 0; i < cookies.size(); i++) {
                    System.out.println("- " + cookies.get(i).toString());
                }
            }
 
            //https://accounts.google.com/ServiceLoginAuth
            String tmpcookies = "";
            HttpPost httppost = new HttpPost("https://accounts.google.com/ServiceLoginAuth");
 
            List <NameValuePair> nvps = new ArrayList <NameValuePair>();
            nvps.add(new BasicNameValuePair("Email", "ouyangjiamu0609@gmail.com"));
            nvps.add(new BasicNameValuePair("Passwd", "yangjiamu"));
            nvps.add(new BasicNameValuePair("Passwd", "by14285700"));
            nvps.add(new BasicNameValuePair("signIn", "登录"));
 
            httppost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
 
            response = httpclient.execute(httppost);
            entity = response.getEntity();
 
            System.out.println("Login form get: " + response.getStatusLine());
            EntityUtils.consume(entity);
 
            System.out.println("Post logon cookies:");
            cookies = httpclient.getCookieStore().getCookies();
            if (cookies.isEmpty()) {
                System.out.println("None");
            } else {
                for (int i = 0; i < cookies.size(); i++) {
                    System.out.println("- " + cookies.get(i).toString());
                    tmpcookies += cookies.get(i).toString();
                }
            }

            // crawl url
            HttpGet httpgetLogon = new HttpGet(url);

            httpgetLogon.setHeader("cookie", tmpcookies);
            
            HttpResponse responseLogon = httpclient.execute(httpgetLogon);
            HttpEntity entityLogon = responseLogon.getEntity();
 
            System.out.println("Login form find: " + response.getStatusLine());
            
 
            System.out.println("Initial set of cookies:");
            List<Cookie> cookies1 = httpclient.getCookieStore().getCookies();
            if (cookies1.isEmpty()) {
                System.out.println("None");
            } else {
                for (int i = 0; i < cookies1.size(); i++) {
                    System.out.println("- " + cookies1.get(i).toString());
                }
            }
            
            //
            // Read the contents of an entity and return it as a String.
            //
           /* String content = EntityUtils.toString(entityLogon);
            System.out.println(content);*/
            
            if(entityLogon !=null){ 
                //System.out.println(entity1.getContentLength());
                //System.out.println(EntityUtils.toString(entity1));
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(entityLogon.getContent(),"utf-8"));
                try {
                    StringBuilder sb = new StringBuilder(); 
                    String str = null;
                    while((str = reader.readLine()) != null){
                        //System.out.println(str);
                        sb.append(str);
                    }
                    TxtWriter.saveToFile(sb.toString(), new File("D:/google+.html"), "UTF-8");
                } catch (IOException ex) {
                    throw ex;
                } catch (RuntimeException ex) {
                    throw ex;
                } finally {
                    reader.close();
                }
            }
            EntityUtils.consume(entityLogon);
            
            //String ajxUrl = "https://plus.google.com/u/0/_/stream/getactivities/?hl=zh_CN&ozv=es_oz_20140422.11_p4&avw=pr%3Apr&f.sid=-8531514186121603752&_reqid=1564544&rt=j";
            String ajxUrl = "https://plus.google.com/u/0/_/stream/getactivities/";
            httppost = new HttpPost(ajxUrl);   
      
        
             
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            StringBuilder pageToken = new StringBuilder();
            pageToken.append("[[1,2,\"110844809911623902704\",null,null,null,null,\"social.google.com\",[],null,null,null,null,null,null,[],null,null,2,null,null,null,null,null,null,[[1002,2],[119,127,110,120,132,135,115,138,142]]],");
            pageToken.append("\"EgkI-PeC_uGVuQIomKSX_qeCvAIw28jm64z5vQI4AUAC\"");
            pageToken.append(",null,null,null,null,null,true,[360,2,[0,368]],null,null,\"CAA=\",null,[]]");
            params.add(new BasicNameValuePair("f.req", pageToken.toString())); 
             
            //params.add(new BasicNameValuePair("f.req", "[[1,2,\"110844809911623902704\",null,null,null,null,\"social.google.com\",[],null,null,null,null,null,null,[],null,null,2,null,null,null,null,null,null,[[1002,2],[119,127,110,120,132,135,115,138,142]]],\"EgkIgrPA_9eFuQIomKSX_qeCvAIwhcCO0e34vQI4AkAC\",null,null,null,null,null,true,[360,2,[193,0]],null,null,\"CAA=\",null,[]]")); 
             
            params.add(new BasicNameValuePair("at", "AObGSAhEl-ChnRDUwkYqOoxzlO7W2PJgHw:1398333342306"));
            params.add(new BasicNameValuePair("", ""));
             
             
             //params.add(new BasicNameValuePair("hl", "zh_CN"));
             //params.add(new BasicNameValuePair("ozv", "es_oz_20140422.11_p4"));
             //params.add(new BasicNameValuePair("avw", "pr:pr"));
             //params.add(new BasicNameValuePair("f.sid", "-8531514186121603752"));
             //params.add(new BasicNameValuePair("_reqid", "1564544"));
             //params.add(new BasicNameValuePair("rt", "j"));
            
            
             
             //params.add(new BasicNameValuePair(":host", "plus.google.com"));
             //params.add(new BasicNameValuePair(":method", "POST"));
             //params.add(new BasicNameValuePair(":path", "/u/0/_/stream/getactivities/?hl=zh_CN&ozv=es_oz_20140422.11_p4&avw=pr%3Apr&f.sid=-8531514186121603752&_reqid=1164544&rt=j"));
             //params.add(new BasicNameValuePair(":scheme", "https"));
             //params.add(new BasicNameValuePair(":version", "HTTP/1.1"));
             //params.add(new BasicNameValuePair("accept", "*/*"));
             //params.add(new BasicNameValuePair("accept-encoding", "gzip,deflate,sdch"));
             //params.add(new BasicNameValuePair("accept-language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4"));
             //params.add(new BasicNameValuePair("cache-control", "max-age=0"));
             //params.add(new BasicNameValuePair("content-length", "537"));
             //params.add(new BasicNameValuePair("content-type", "application/x-www-form-urlencoded;charset=UTF-8"));
             //params.add(new BasicNameValuePair("cookie", "NID=67=StEkpqangxoAXEUEur4BBQAVeQEtKOI2cUFJT3U_IHdGrYzvTsQPU33PG6dP3XRxOksws6Kvd9XK3w0HoQo4RGPqydfW1AQY_RxQhNbg4H2l1sIB2Qjjv6qocndej0coESpBbowT89pwyqFTrbOjwj2Px9AGLrj7a0oO8_9mOUFYPE6vFAY-fBBss0V8r6BHtQ; HSID=AMzOdw5BLokWp8okK; SSID=AcOm06XQIBGed6MMH; APISID=8b7wfdDm8hOXTP54/AU45hCkJaz2A8pipe; SAPISID=XWHCbNIyqknX9qq7/A-e2LlOXKtYdw0aME; S=talkgadget=e9I4zyeu7ptsglS4-wDq_w; PREF=ID=503e4e1616a90067:LD=zh-CN:TM=1398331206:LM=1398332825:S=QfOHMVex6jd2eLuY; SID=DQAAAMsAAAD-Mqun95v71HmXTHwSDVlC7txy3RdqnaqJIiAwywfPo1nHfWjRndTt_Y5TbJ7TXO4ZocnPGpNo7ZmL3ZK-beQgoHuBBOqJ5MiCaKh7nXMhj0a2i0F5R8Up82K5tQvoWMrmSkJcRjfRRcXuzkHBLBA4WBi-77BFAIXIGFgaGonjgQtHBcpXLs0UkolQ5iPKbUa7hbcxJlJZheEUWXnadDxJ13qn1ufCQkbckNtQ-1t_Nk790g7QgwlFLm5ZzgaZq7Jm23mOQsEVQ2g3l-evYlU_; OTZ=2267156_24_24__24_"));
             //params.add(new BasicNameValuePair("origin", "https://plus.google.com"));
             //params.add(new BasicNameValuePair("referer", "https://plus.google.com/"));
             //params.add(new BasicNameValuePair("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36"));
             //params.add(new BasicNameValuePair("x-client-data", "CNK1yQEIhbbJAQiltskBCKm2yQEIwbbJAQiehsoBCLmIygEI24jKAQ=="));
             //params.add(new BasicNameValuePair("x-same-domain", "1"));
             
             
             
             
             httppost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));   
           
     
            HttpResponse httpresponse = httpclient.execute(httppost);  
       
            entity = httpresponse.getEntity();  
          
            if(entity !=null){ 
                //System.out.println(entity1.getContentLength());
                //System.out.println(EntityUtils.toString(entity1));
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(entity.getContent(),"utf-8"));
                try {
                    StringBuilder sb = new StringBuilder(); 
                    String str = null;
                    while((str = reader.readLine()) != null){
                        //System.out.println(str);
                        sb.append(str);
                    }
                    TxtWriter.saveToFile(sb.toString(), new File("D:/google+2.dat"), "UTF-8");
                } catch (IOException ex) {
                    throw ex;
                } catch (RuntimeException ex) {
                    throw ex;
                } finally {
                    reader.close();
                }
            }
            EntityUtils.consume(entity);
           
        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
    }
}