/**
 * 
 */
package com.iie.googleplus.Crawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
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
import com.iie.googleplus.Platform.LogSys;
import com.iie.googleplus.tool.DbOperation;
import com.iie.httpclient.crawler.GoogleLoginCookieStore;


/**
 * @author Gingber
 *
 */
public class AdvanceLoginManager {
	
	class CookieItem{
		public List<Cookie> cookieList;
		public String username;
	}
	
	private DefaultHttpClient httpclient;
	private String logonCookies;
	DbOperation dbo;
	
	public AdvanceLoginManager(DefaultHttpClient httpclient){		
		this.httpclient=httpclient;
		dbo = new DbOperation();
	}
	
	
	private boolean getAvailableCookie(CookieItem item){
		//ByteArrayInputStream in = new ByteArrayInputStream(data);
	    //ObjectInputStream is = new ObjectInputStream(in);
		java.sql.Connection con=dbo.GetConnection();
		java.sql.Statement sta;
		try {
			sta = con.createStatement();
			ResultSet rs=sta.executeQuery("select cookie, user_name from account where status='using' and health=1");
			if(rs.next()){
				InputStream is = rs.getBlob("cookie").getBinaryStream();
				ObjectInputStream ois = new ObjectInputStream(is);
				item.cookieList= (List<Cookie>) ois.readObject(); 
				item.username=rs.getString(2);
				rs.close();
				sta.close();
				return true;
			}else{
				return false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	
	private List<String[]> getAvailableAccount(){
		List<String[]> res=new ArrayList<String[]>();
		java.sql.Connection con=dbo.GetConnection();
		java.sql.Statement sta;
		try {
			sta = con.createStatement();
			ResultSet rs=sta.executeQuery("select user_name, password from account where health=1");
			while(rs.next()){
				String[] t=new String[2];
				t[0]=rs.getString(1);
				t[1]=rs.getString(2);
				res.add(t);
			}
			rs.close();
			sta.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	
	public boolean trylogin() {
		
		CookieItem item=new CookieItem();
		String username="";
		boolean find = this.getAvailableCookie(item);
		if(find) {
			try {
				System.out.println("发现能使用的账户"+item.username);
				GoogleLoginCookieStore mycookiestore = new GoogleLoginCookieStore();
				mycookiestore.resume(item.cookieList);
				httpclient.setCookieStore(mycookiestore);
				if(checkLoginStatus()) {
					return true;
				} else {//证明当前账户已经失效啦
					markStatus(item.username,"frozen");
					System.out.println("刚刚发现的账户无法进行恢复会话"+item.username);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		List<String[]> list = this.getAvailableAccount();
		boolean success=false;
		for(int i=0; i<list.size(); i++) {
			String[] nameandpass = list.get(i);
			try {
				if(forceLogin(nameandpass)){//如果登陆成功的话
					//记得保存当前的Cookie信息啊
					GoogleLoginCookieStore mycookiestore = new GoogleLoginCookieStore();
					List<Cookie> cookie = mycookiestore.savetodb();
					if(cookie == null || cookie.size() == 0){
						System.out.println("大小错误啊");
					}
					SaveCookieToDB(nameandpass[0],cookie);
					success=true;
					break;
				}else{//标记当前账号失效啦
					System.out.println("当前账号失效啦");
					MaskAsNotAvailable(nameandpass[0]);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	
		//将表权限释放掉。
		if(!success){
			LogSys.clientLogger.error("注意，所有账户都无法正常使用");
			System.exit(-1);
		}
		
        return success;
	}
	
	public boolean forceLogin(String[] loginInfo) throws ClientProtocolException, IOException{
		boolean logined=false;
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
    
        SaveToHtml(entity, "Output/GooglePlus/LogAfter.html");
        LogSys.nodeLogger.debug("Login form get: " + response.getStatusLine());
        EntityUtils.consume(entity);
        LogSys.nodeLogger.debug("Post logon cookies:");
        cookies = httpclient.getCookieStore().getCookies();
        if (cookies.isEmpty()) {
            System.out.println("None");
            logined=false;
        } else {
        	for (int i = 0; i < cookies.size(); i++) {
            	LogSys.nodeLogger.info("- " + cookies.get(i).toString());
            	if(cookies.get(i).getName().equals("auth_token")){
            		LogSys.nodeLogger.info("Success To Login To Twitter");
            		logined=true;
            	}
            }
        	
        	if(logined){
                LogSys.nodeLogger.info("Success To Save Cookies");
            }else{
            	LogSys.nodeLogger.info("Fail To Login To Twitter RM the datFile");
            }
        }
        
        return true;
	}
	
	private boolean markStatus(String username,String status){
		java.sql.Connection con=dbo.GetConnection();
		java.sql.Statement sta;
		try {
			PreparedStatement pst = con.prepareStatement("update account set status=? where user_name=?");
			pst.setString(1, status);
			pst.setString(2, username);
			pst.executeUpdate();
			pst.close();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean MaskAsNotAvailable(String username){
		java.sql.Connection con = dbo.GetConnection();
		java.sql.Statement sta;
		try {
			PreparedStatement pst=con.prepareStatement("update account set status='frozen', health=false, where user_name=?");
			pst.setString(1, username);
			pst.executeUpdate();
			pst.close();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean SaveCookieToDB(String username,List<Cookie> cookie){
		java.sql.Connection con=dbo.GetConnection();
		java.sql.Statement sta;
		try {
			PreparedStatement pst=con.prepareStatement("update account set status='using', count=1, health=true, cookie=? where user_name=?");
			pst.setObject(1, (Object)cookie);
			pst.setString(2, username);
			pst.executeUpdate();
			pst.close();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean checkLoginStatus(){
	
		HttpGet httpget = new HttpGet("https://plus.google.com/108440629924187901881/about");
		try {
			HttpResponse response = httpclient.execute(httpget);
			StatusLine state = response.getStatusLine();
			int stateCode = state.getStatusCode();
			if(HttpStatus.SC_OK == stateCode){
				String res=SaveToHtml(response.getEntity(),"Output/GooglePlus/CheckLogin.html");
				if(res.contains("We gotta check... are you human?")||res.contains("Sign in to Twitter")||
								res.contains("<form action=\"https://twitter.com/sessions\"")
								){
					return false;
				}else{
					return true;
				}
			}else if(HttpStatus.SC_MOVED_PERMANENTLY == stateCode 
					|| HttpStatus.SC_MOVED_TEMPORARILY == stateCode
					|| HttpStatus.SC_SEE_OTHER == stateCode
					|| HttpStatus.SC_TEMPORARY_REDIRECT == stateCode){
				return false;
			}else{
				return false;
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return true;
	}

	private String SaveToHtml(HttpEntity entity,String fileName){       
        try{
        	 BufferedReader br=new BufferedReader(new InputStreamReader(entity.getContent()));
             BufferedWriter bw=new BufferedWriter(new FileWriter(fileName));
        	
             String t="";
             StringBuffer sb=new StringBuffer();
             while((t=br.readLine())!=null){
        		bw.write(t+"\n\r");
        		sb.append(t+"\n\r");
             }
             bw.close();
             br.close();
             return sb.toString();
        }catch (Exception ex){
        	ex.printStackTrace();
        	return null;
        }
        
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
