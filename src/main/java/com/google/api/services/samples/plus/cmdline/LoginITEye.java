package com.google.api.services.samples.plus.cmdline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 妯′豢鐧诲綍iTeye
 * @author Li
 */
public class LoginITEye {
    private String cookies;
    private DefaultHttpClient http = new DefaultHttpClient();
    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36";
    
    public static void consume(final HttpEntity entity) throws IOException {
    	if (entity == null) {
            return;
        }
        if (entity.isStreaming()) {
        	final InputStream instream = entity.getContent();
        	if (instream != null) {
                instream.close();
            }
        }
    }
    
    public static void main(String[] args) throws Exception {
        /*String url = "http://www.iteye.com/login";
        String itUrl = "http://www.iteye.com";*/
    	String url = "https://accounts.google.com/ServiceLoginAuth";
    	String itUrl = "https://www.google.com/settings/personalinfo";
    	
    	
        LoginITEye iteye = new LoginITEye();
        
        // 打开cookie 
        CookieHandler.setDefault(new CookieManager());
        
        // 获取请求登录的页面HTML 
        String page = iteye.getPageContent(url);
        //System.out.println(page);
        
        // 设置登录参数  
        List<NameValuePair> params = iteye.tranParams(page, "kmust.jiangbo@gmail.com","by14285700");
        
        // 开始登录  
        iteye.startLogin(url, params);
        
        String itEyePage = iteye.getPageContent(itUrl);
        System.out.println("----------可以看到welcome欢迎***。表示登录成功.-----------");
        System.out.println(itEyePage);
    }
    
    /**
     * 开始模拟登录
     * @param url
     * @param params
     * @throws IOException 
     * @throws ClientProtocolException 
     */
    private void startLogin(String url, List<NameValuePair> params) throws Exception {
    	http.setRedirectHandler(new DefaultRedirectHandler() {                
    	    @Override
    	    public boolean isRedirectRequested(HttpResponse response, HttpContext context) {
    	        boolean isRedirect = super.isRedirectRequested(response, context);
    	        System.out.println("自动跳转到"+response.getFirstHeader("location"));
    	        if (!isRedirect) {
    	            int responseCode = response.getStatusLine().getStatusCode();
    	            if (responseCode == 301 || responseCode == 302) {
    	                return true;
    	            }
    	        }
    	        return isRedirect;
    	    }
    	});
    	HttpPost request = new HttpPost(url);
        
        request.setHeader("Host", "accounts.google.com");
        request.setHeader("User-Agent", USER_AGENT);
        
        request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        request.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4");
        
        request.setHeader("Cookie", getCookies());
        request.setHeader("Connection", "keep-alive");
        
        //request.setHeader("Referer", "https://accounts.google.com/ServiceLogin?sacu=1&elo=1");
       // request.setHeader("Content-Type", "application/x-www-form-urlencoded");
        
        request.setEntity(new UrlEncodedFormEntity(params));
        
        HttpHost proxy = new HttpHost("127.0.0.1", 8580);
        http.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,proxy);
        http.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookieSpecs.BEST_MATCH);
        HttpResponse response = http.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        
        // statusCode==200表示请求成功.
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("send paramer: "+ params);
        System.out.println("Response Code : " + statusCode);
        
        
        /*-------------添加代码---------------*/
        String cookie = null;
		String redirectUrl = null;
        HttpEntity entity2 = response.getEntity();
        consume(entity2);
        
		Header[] locations = response.getHeaders("location");
		if (locations != null&&locations.length>0) {
			redirectUrl = locations[0].getValue();
			System.out.println("获取到跳转链接1：" + redirectUrl);
		}
		
		
		
		HttpGet get = new HttpGet(redirectUrl);
		
		get.setHeader("Host", "accounts.google.com");
        get.setHeader("User-Agent", USER_AGENT);
        
        //get.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        get.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4");
        
        get.setHeader("Cookie", getCookies());
        get.setHeader("Connection", "keep-alive");
        
        get.setHeader("Referer", "https://accounts.google.com/ServiceLogin?sacu=1&elo=1");
        get.setHeader("Content-Type", "application/x-www-form-urlencoded");
        
        response = http.execute(get);
		Header[] headers = response.getHeaders("set-cookie");
		if (headers != null) {
			cookie = headers[0].getValue();
			System.out.println("获取到Cookie2：" + cookie);
		}
		locations = response.getHeaders("location");
		if (locations != null) {
			redirectUrl = locations[0].getValue();
			System.out.println("获取到跳转链接2：" + redirectUrl);
		}
		
		consume(response.getEntity());
		
		get = new HttpGet(redirectUrl);
		
		get.setHeader("Host", "accounts.google.com");
        get.setHeader("User-Agent", USER_AGENT);
        
        //get.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        get.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4");
        
        get.setHeader("Cookie", getCookies());
        get.setHeader("Connection", "keep-alive");
        
        get.setHeader("Referer", "https://accounts.google.com/ServiceLogin?sacu=1&elo=1");
        get.setHeader("Content-Type", "application/x-www-form-urlencoded");
        
        response = http.execute(get);
		headers = response.getHeaders("set-cookie");
		if (headers != null) {
			cookie = headers[0].getValue();
			System.out.println("获取到Cookie3：" + cookie);
		}
		locations = response.getHeaders("location");
		if (locations != null) {
			redirectUrl = locations[0].getValue();
			System.out.println("获取到跳转链接3：" + redirectUrl);
		}
		
		consume(response.getEntity());
		
		get = new HttpGet(redirectUrl);
		
		get.setHeader("Host", "accounts.google.com");
        get.setHeader("User-Agent", USER_AGENT);
        
        //get.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        get.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4");
        
        get.setHeader("Cookie", getCookies());
        get.setHeader("Connection", "keep-alive");
        
        get.setHeader("Referer", "https://accounts.google.com/ServiceLogin?sacu=1&elo=1");
        get.setHeader("Content-Type", "application/x-www-form-urlencoded");
        
        response = http.execute(get);
		headers = response.getHeaders("set-cookie");
		if (headers != null) {
			cookie = headers[0].getValue();
			System.out.println("获取到Cookie4：" + cookie);
		}
		locations = response.getHeaders("location");
		if (locations != null) {
			redirectUrl = locations[0].getValue();
			System.out.println("获取到跳转链接4：" + redirectUrl);
		}
		
		consume(response.getEntity());
		
		get = new HttpGet(redirectUrl);
		
		get.setHeader("Host", "accounts.google.com");
        get.setHeader("User-Agent", USER_AGENT);
        
        //get.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        get.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4");
        
        get.setHeader("Cookie", getCookies());
        get.setHeader("Connection", "keep-alive");
        
        get.setHeader("Referer", "https://accounts.google.com/ServiceLogin?sacu=1&elo=1");
        get.setHeader("Content-Type", "application/x-www-form-urlencoded");
        
        response = http.execute(get);
		headers = response.getHeaders("set-cookie");
		if (headers != null) {
			cookie = headers[0].getValue();
			System.out.println("获取到Cookie5：" + cookie);
		}
		locations = response.getHeaders("location");
		if (locations != null) {
			redirectUrl = locations[0].getValue();
			System.out.println("获取到跳转链接5：" + redirectUrl);
		}
		
		consume(response.getEntity());
		
		get = new HttpGet(redirectUrl);
		
		get.setHeader("Host", "accounts.google.com");
        get.setHeader("User-Agent", USER_AGENT);
        
        //get.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        get.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4");
        
        get.setHeader("Cookie", getCookies());
        get.setHeader("Connection", "keep-alive");
        
        get.setHeader("Referer", "https://accounts.google.com/ServiceLogin?sacu=1&elo=1");
        get.setHeader("Content-Type", "application/x-www-form-urlencoded");
        
        response = http.execute(get);
		locations = response.getHeaders("location");
		if (locations != null) {
			redirectUrl = locations[0].getValue();
			System.out.println("获取到跳转链接6：" + redirectUrl);
		}
		
		consume(response.getEntity());
		
		get = new HttpGet(redirectUrl);
		
		get.setHeader("Host", "accounts.google.com");
        get.setHeader("User-Agent", USER_AGENT);
        
        //get.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        get.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4");
        
        get.setHeader("Cookie", getCookies());
        get.setHeader("Connection", "keep-alive");
        
        get.setHeader("Referer", "https://accounts.google.com/ServiceLogin?sacu=1&elo=1");
        get.setHeader("Content-Type", "application/x-www-form-urlencoded");
        
        response = http.execute(get);
		locations = response.getHeaders("location");
		if (locations != null) {
			redirectUrl = locations[0].getValue();
			System.out.println("获取到跳转链接7：" + redirectUrl);
		}
		
		consume(response.getEntity());
		
		
		
		
		
		
		
		/*-------------添加结束---------------*/
		
		
        
        BufferedReader br = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));
        
        StringBuffer sb = new StringBuffer();
        String line = "";
        while((line = br.readLine()) != null) {
            sb.append(line);
        }
        
        //System.out.println(sb.toString());
    }
    
    /**
     * 把你的登录账号和密码设置到form里面去
     * @param html
     * @param name 登陆名
     * @param word 密码
     * @return
     */
    private List<NameValuePair> tranParams(String html, String name, String word) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        
        // 转化HTML --> document  
        Document doc = Jsoup.parse(html);
        
        // 获取ITEye登录form 
        Element formEl = doc.getElementById("gaia_loginform");
        Elements inputs = formEl.getElementsByTag("input");
        
        // 解析ITEye登录form里面的登录名和登录密码 
        for(Element el: inputs) {//loop循环每一个input
            String elName = el.attr("name");//获取input的name属性
            String elValue = el.attr("value");//获取input的value属性
            String elType = el.attr("type");//获取input的type属性
            
            if(elName.equals("Email")) {
                elValue = name;//如果属性名是Email，就将值设置成用户定义的值
            }else if(elName.equals("Passwd")) {
                elValue = word;//如果属性名是Passwd，就将值设置为用户定义的值
            }
            
            if(!elName.equals("button")&&!elType.equals("submit")) {//此外有些button是input应排除
                list.add(new BasicNameValuePair(elName, elValue));//创建键值对加入list
            }
        }

        return list;
    }
    
    /**
     * 获取iTeye登录页面 
     * @param url
     * @return
     * @throws IOException 
     * @throws ClientProtocolException 
     */
    private String getPageContent(String url) throws Exception {
        StringBuffer result = new StringBuffer();
        
        // 设置请求信息 
        HttpGet request = new HttpGet(url);
        request.setHeader("User-Agent", USER_AGENT);
        request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        request.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4");
        
        // 发送请求 
        HttpResponse response = http.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        
        // statusCode==200表示请求成功.
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + statusCode);
        
        // 读取返回内容解析成字符串
        BufferedReader br = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));
        
        String line = "";
        while((line = br.readLine()) != null) {
            result.append(line);
        }
        
        //System.out.println(result.toString());
        
        // 设置cookie 
        setCookies(response.getFirstHeader("Set-Cookie") == null ? "":
                                response.getFirstHeader("Set-Cookie").toString());
        return result.toString();
    }

    /**
     * @return the cookid
     */
    public String getCookies() {
        return cookies;
    }

    /**
     * @param cookid the cookid to set
     */
    public void setCookies(String cookies) {
        this.cookies = cookies;
    }
    

}

