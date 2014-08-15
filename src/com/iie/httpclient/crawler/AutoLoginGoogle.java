/**
 * 
 */
package com.iie.httpclient.crawler;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.iie.googleplus.Dboperator.MySQLOperator;


/**
 * @author Gingber
 *
 */
public class AutoLoginGoogle {
	
	class CookieItem{
		public String cookieList;
		public String username;
	}
	
	private DefaultHttpClient httpclient;
	private String logonCookies;
	
	public AutoLoginGoogle(DefaultHttpClient httpclient){		
		this.httpclient=httpclient;
	}
	
	public String doAutoLoginGoogle() throws IOException, SQLException {
		
		CookieItem item=new CookieItem();
		String username="";
		boolean find = this.getAvailableCookie(item);
		if(find) {
			System.out.println("发现能使用的账户"+item.username);
			return item.cookieList;
		} 
		
		List<String[]> list = getAvailableAccount();
		boolean success=false;
		for(int i=0; i<list.size(); i++) {
			String[] nameandpass = list.get(i);
			if(forceLogin(nameandpass)){//如果登陆成功的话
				//记得保存当前的Cookie信息啊

				if(logonCookies==null){
					System.out.println("大小错误啊");
				}
				MySQLOperator dbop = new MySQLOperator();
				dbop.SaveCookieToDB(nameandpass[0], logonCookies);
				success=true;
				break;
			}else{//标记当前账号失效啦
				System.out.println("当前账号失效啦");
				MySQLOperator dbop = new MySQLOperator();
				dbop.MaskAsNotAvailable(nameandpass[0]);
			}
			
		}
	
		
        return logonCookies;
	}
	
	public boolean forceLogin(String[] loginInfo) throws ClientProtocolException, IOException{
		boolean logined = false;
		String username, password;
		username = loginInfo[0];
		password = loginInfo[1];
		
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
 
             
        String tmpcookies = "";
        HttpPost httppost = new HttpPost("https://accounts.google.com/ServiceLoginAuth");
   
        List <NameValuePair> nvps = new ArrayList <NameValuePair>();  
        nvps.add(new BasicNameValuePair("Email", username));
        nvps.add(new BasicNameValuePair("Passwd", password));
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
                logonCookies += cookies.get(i).toString();
            }
        }
        
        return true;
	}
	
	private boolean getAvailableCookie(CookieItem item){
		try {
			MySQLOperator dbop = new MySQLOperator();
			ResultSet rs = dbop.getCookie();
			if(rs.next()){
				item.cookieList= rs.getString("cookie");
				item.username = rs.getString("user_name");
				rs.close();
				return true;
			}else{
				return false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return true;
	}
	
	private List<String[]> getAvailableAccount() throws SQLException {
		
		List<String[]> account = new ArrayList<String[]>();
		
		MySQLOperator dbop = new MySQLOperator();
		
		account = dbop.getAccount();
		
		return account;
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	

}
