package test.weiboso;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * http请求基类
 * 
 * @author mingyuan
 * 
 */
public abstract class RequestCommons {
	protected HttpClient httpclient = null;

	public RequestCommons() {
		initHttpClient();
	}

	/**
	 * 初始化httpclient
	 */
	protected void initHttpClient() {
		httpclient = new DefaultHttpClient();
	}

	protected HttpClient getHttpClient() {
		return httpclient;
	}

	protected void addHeader(HttpRequestBase request, String key, String value) {
		request.addHeader(key, value);
	}

	protected void addCookie(HttpRequestBase request, String cookie) {
		addHeader(request, "Cookie", cookie);
	}

	protected void setCookie(HttpRequestBase request, String cookie) {
		request.setHeader("Cookie", cookie);
	}

	/**
	 * 设置请求的header值
	 * 
	 * @param request
	 *            http的get或者post请求
	 */
	protected void setHeader(HttpRequestBase request) {
		request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		request.setHeader("Accept-Language", "en-us,en;q=0.5");
		request.setHeader("Connection", "keep-alive");
		request.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:13.0) Gecko/20100101 Firefox/13.0.1");
	}
}
