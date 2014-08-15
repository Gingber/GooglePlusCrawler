package com.iie.googleplus.Crawler;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


import org.apache.http.*;
import org.apache.http.conn.params.*;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.*;


public class GoogleClientManager {
	
	DefaultHttpClient httpclient;
	SchemeRegistry sr;
	PoolingClientConnectionManager cm;
	SSLSocketFactory socketFactory;
	
	public GoogleClientManager(){

		try {
			Initiallize();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void Initiallize() throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException{
		Scheme http = new Scheme("http", 80, PlainSocketFactory.getSocketFactory());
		//创建TrustManager
		X509TrustManager xtm = new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		//这个好像是HOST验证
		X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier() {
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
			public void verify(String arg0, SSLSocket arg1) throws IOException {}
			public void verify(String arg0, String[] arg1, String[] arg2) throws SSLException {}
			public void verify(String arg0, X509Certificate arg1) throws SSLException {}
		};
		SSLContext ctx = null;
		try {
			ctx = SSLContext.getInstance("TLS");
			ctx.init(null, new TrustManager[]{(TrustManager) xtm}, null);
			//创建SSLSocketFactory
			socketFactory = new SSLSocketFactory(ctx);
			socketFactory.setHostnameVerifier(hostnameVerifier);
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		SSLContext.setDefault(ctx);
		SSLSocketFactory ssf = new SSLSocketFactory(ctx);
		Scheme https = new Scheme("https", 443, ssf);		
		sr = new SchemeRegistry();
		sr.register(http);
		sr.register(https);	
		cm = new PoolingClientConnectionManager(sr);
		cm.setMaxTotal(200);
		cm.setDefaultMaxPerRoute(100);
		
	}
	
	
	public void Initiallize2() throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException{
		Scheme http = new Scheme("http", 80, PlainSocketFactory.getSocketFactory());
		X509TrustManager tm = new X509TrustManager() {	        
	        public X509Certificate[] getAcceptedIssuers() {
	        	return null;
	        	}
			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException {
			}
			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException {
			}			
		};
		SSLContext ctx = null;
		try {
			ctx = SSLContext.getInstance("TLS");
			ctx.init(null, new TrustManager[]{(TrustManager) tm}, null);			
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		SSLContext.setDefault(ctx);
		SSLSocketFactory ssf = new SSLSocketFactory(ctx);
		Scheme https = new Scheme("https", 443, ssf);		
		sr = new SchemeRegistry();
		sr.register(http);
		sr.register(https);	
		cm = new PoolingClientConnectionManager(sr);
		cm.setMaxTotal(200);
		cm.setDefaultMaxPerRoute(100);
		
	}
	public DefaultHttpClient getClient(String ip, int port){		
		httpclient = new DefaultHttpClient(cm);
		//通过SchemeRegistry将SSLSocketFactory注册到我们的HttpClient上
		httpclient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", socketFactory, 443));
		HttpHost proxy = new HttpHost(ip, port);
		httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);	
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000); 
		httpclient.setCookieStore(new GoogleLoginCookieStore());
		return httpclient;		
	}
	public DefaultHttpClient getClientByIpAndPort(String ip,int port){
		httpclient = new DefaultHttpClient(cm);
		//通过SchemeRegistry将SSLSocketFactory注册到我们的HttpClient上
		httpclient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", socketFactory, 443));
		HttpHost proxy = new HttpHost(ip, port);
		httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000); 
		httpclient.setCookieStore(new GoogleLoginCookieStore());
		return httpclient;
	}
	public DefaultHttpClient getClientNoProxy(){

		httpclient = new DefaultHttpClient(cm);
		//通过SchemeRegistry将SSLSocketFactory注册到我们的HttpClient上
		httpclient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", socketFactory, 443));
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
		//httpclient.setHttpRequestRetryHandler(new myRetryHandler());
		httpclient.setCookieStore(new GoogleLoginCookieStore());
		return httpclient;		
	}
	
	
	
	
	
}
