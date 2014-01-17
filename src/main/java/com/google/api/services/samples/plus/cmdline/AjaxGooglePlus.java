/**
 * 
 */
package com.google.api.services.samples.plus.cmdline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Gingber
 *
 */
public class AjaxGooglePlus {

	private static String url = "https://accounts.google.com/ServiceLogin";
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		DefaultHttpClient hc = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		
		request.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		 
		try {
			HttpResponse response = hc.execute(request);
			BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			
			String line = null;
			StringBuffer html = new StringBuffer();
			while((line = br.readLine()) != null){
				html.append(line);
			}
			
			Document doc = Jsoup.parse(html.toString());//将纯HTML文本转化成具有结构的Document对象
			Element formEl = doc.getElementById("gaia_loginform");//获取登录form
			Elements inputs = formEl.getElementsByTag("input");//获取form底下的所有input
			
			List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>(); 
			for(Element el: inputs) {//loop循环每一个input
			    String elName = el.attr("name");	//获取input的name属性
			    String elValue = el.attr("value");	//获取input的value属性
			    String elType = el.attr("type");	//获取input的type属性
			    
			    if(elName.equals("Email")) {
			        elValue = "kmust.jiangbo@gmail.com";	//如果属性名是Email，就将值设置成用户定义的值
			    }else if(elName.equals("Passwd")) {
			        elValue = "by14285700";	//如果属性名是Passwd，就将值设置为用户定义的值
			    }
			    
			    
			    if(!elName.equals("button")&&!elType.equals("submit")) {//此外有些button是input应排除
			        list.add(new BasicNameValuePair(elName, elValue));//创建键值对加入list
			    }
			}
			
			HttpEntity entity = new UrlEncodedFormEntity(list, "UTF-8");   
			HttpPost request2 = new HttpPost("https://accounts.google.com/ServiceLoginAuth");
			request2.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			request2.setEntity(entity); 
			
			HttpResponse response2 = hc.execute(request);//context 是 HttpContext的对象
			int statusCode = response.getStatusLine().getStatusCode();
			System.out.println(statusCode);
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
