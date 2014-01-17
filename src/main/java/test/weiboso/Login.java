package test.weiboso;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;


/**
 * 登陆 获取cookie
 * 
 * @author mingyuan
 * 
 */
public class Login extends RequestCommons {
	
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
	
	/**
	 * 获取登陆参数。主要有三个值：第一个是表单提交地址、第二个是密码输入框的名字、第三个是vk的值
	 * 
	 * @return 返回登陆参数，string数组，里面的元素：第一个是表单提交地址、第二个是密码输入框的名字、第三个是vk的值
	 */
	private String[] getLoginParameters() {
		HttpClient httpClient = getHttpClient();
		//String location = "http://3g.sina.com.cn/prog/wapsite/sso/login.php?backURL=http%3A%2F%2Fweibo.cn%2F&backTitle=%D0%C2%C0%CB%CE%A2%B2%A9&vt=4&revalid=2&ns=1";
		String location = "https://accounts.google.com/ServiceLogin";
		HttpGet get = new HttpGet(location);
		setHeader(get);
		HttpResponse response;
		InputStream content;
		String retAction = null;
		String retPassword = null;
		String retVk = null;
		try {
			response = httpClient.execute(get);
			HttpEntity entity = response.getEntity();
			content = entity.getContent();

			// 提取登陆参数
			HtmlCleaner cleaner = new HtmlCleaner();
			TagNode tagNode = cleaner.clean(content, "utf-8");
			Object[] action = tagNode.evaluateXPath("//form/@action");

			if (action.length > 0) {
				retAction = action[0].toString();
			}
			Object[] passwordKey = tagNode.evaluateXPath("//form//input[@type='password']/@name");

			if (passwordKey.length > 0) {
				retPassword = passwordKey[0].toString();
			}
			Object[] vkKey = tagNode.evaluateXPath("//form//input[@type='email']/@name");

			if (vkKey.length > 0) {
				retVk = vkKey[0].toString();
			}
			consume(entity);
		} catch (ClientProtocolException e) {
			System.out.println("获取登陆页面失败，location=" + location);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("获取页面内容流失败");
			e.printStackTrace();
		} catch (XPatherException e) {
			System.out.println("解析登陆参数失败");
			e.printStackTrace();
		} finally {
//			if (get != null) {
//				get.releaseConnection();
//			}
		}

		System.out.println("请求页面：" + location);
		System.out.println("提交地址：" + retAction);
		System.out.println("密码输入框名称：" + retPassword);
		System.out.println("vk值：" + retVk);

		return new String[] { retAction, retPassword, retVk };
	}

	/**
	 * 提交账号密码，开始登陆
	 * 
	 * @param postAction
	 *            登陆地址
	 * @param userNameValue
	 *            微博登陆账号
	 * @param passwordValue
	 *            微博登陆密码
	 * @param passwordKey
	 *            微博登陆框的name
	 * @param vkValue
	 *            vk的值
	 * @return 返回取到的cookie与跳转地址，组合成一个String数组。第一个元素为cookie，第二个元素为跳转地址
	 */
	private String[] submitPassword(String postAction, String userNameValue, String passwordValue, String passwordKey, String vkValue) {
		HttpClient httpclient = getHttpClient();
		//String url = "http://3g.sina.com.cn/prog/wapsite/sso/" + postAction;
		String url = "" + postAction;
		System.out.println("开始提交账号密码：" + url);
		HttpPost post = new HttpPost(url);
		setHeader(post);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("mobile", userNameValue));
		nvps.add(new BasicNameValuePair(passwordKey, passwordValue));
		nvps.add(new BasicNameValuePair("remember", "on"));
		nvps.add(new BasicNameValuePair("vk", vkValue));
		nvps.add(new BasicNameValuePair("backURL", "https://www.google.cn/"));
		nvps.add(new BasicNameValuePair("backTitle", "Google"));
		nvps.add(new BasicNameValuePair("submit", "登录"));
		HttpResponse response;
		String cookie = null;
		String location = null;
		try {
			post.setEntity(new UrlEncodedFormEntity(nvps));
			response = httpclient.execute(post);
			HttpEntity entity2 = response.getEntity();
			Header[] setCookie = response.getHeaders("Set-Cookie");

			if (setCookie != null) {
				cookie = setCookie[0].getValue();
				System.out.println("获取到Cookie：" + cookie);
			}
			Header[] locations = response.getHeaders("Location");
			if (locations != null) {
				location = locations[0].getValue();
				System.out.println("获取到跳转链接：" + location);
			}
			consume(entity2);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
//			post.releaseConnection();
		}

		return new String[] { cookie, location };
	}

	/**
	 * 获取重定向页面内容
	 * 
	 * @param redirectUrl
	 *            获取重定向页面地址
	 * @return 获取cookie和要跳转的地址
	 */
	private String[] getRedirectPageInfo(String redirectUrl) {
		System.out.println("开始获取跳转链接页面");
		HttpGet get = new HttpGet(redirectUrl);
		setHeader(get);
		HttpResponse redirectResponse;
		String cookie = null;
		String clickHref = null;
		try {
			redirectResponse = httpclient.execute(get);
			Header[] headers = redirectResponse.getHeaders("Set-Cookie");
			if (headers != null) {
				cookie = headers[0].getValue();
				String[] splits = cookie.split(";");

				for (String str : splits) {
					if (str.startsWith("gsid_CTandWM")) {
						cookie = str;
						break;
					}
				}
			}
			HttpEntity entity = redirectResponse.getEntity();
			InputStream content = entity.getContent();
			HtmlCleaner cleaner = new HtmlCleaner();
			TagNode tagNode = cleaner.clean(content, "utf-8");
			Object[] clickHrefs = tagNode.evaluateXPath("//div/a/@href");

			if (clickHrefs != null) {
				clickHref = clickHrefs[0].toString();
				System.out.println("获取到跳转链接地址：" + clickHref);

			}
			consume(entity);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XPatherException e) {
			e.printStackTrace();
		} finally {
//			get.releaseConnection();
		}
		return new String[] { cookie, clickHref };
	}

	/**
	 * 跳转
	 * 
	 * @param cookie
	 *            上次请求取到的cookie
	 * @param redirectUrl
	 *            跳转url
	 * @return 返回跳转后取得的cookie
	 */
	private String doRedirection(String cookie, String redirectUrl) {
		HttpGet get = new HttpGet(redirectUrl);
		setHeader(get);
		get.setHeader("Cookie", cookie);
		HttpResponse response;
		try {
			response = httpclient.execute(get);
			HttpEntity entity = response.getEntity();
			Header[] headers2 = response.getHeaders("Set-Cookie");
			if (headers2 != null&&headers2.length>=1) {
				cookie = headers2[0].getValue();
				System.out.println("跳转页面取回的cookie：" + cookie);
				String[] splits = cookie.split(";");
				for (String str : splits) {
					if (str.startsWith("_WEIBO_UID")) {
						cookie = str;
						break;
					}
				}
			}
			consume(entity);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cookie;
	}

	/**
	 * 提交账号密码，登陆
	 * 
	 * @param userNameValue
	 *            微博账号
	 * @param passwordValue
	 *            微博密码
	 * @return 返回cookie
	 */
	public String doLogin(String userNameValue, String passwordValue) {
		// 获取登陆页面的参数
		String[] loginParameters = getLoginParameters();
		String postAction = loginParameters[0];
		String passwordKey = loginParameters[1];
		String vkValue = loginParameters[2];

		// 提交账号密码，获取重定向页面链接与cookie
		String[] cookieRedirectLocation = submitPassword(postAction, userNameValue, passwordValue, passwordKey, vkValue);
		String cookie = cookieRedirectLocation[0];
		String redirectUrl = cookieRedirectLocation[1];
		// 获取重定向页面内容
		String[] redirectInfo = getRedirectPageInfo(redirectUrl);
		cookie = redirectInfo[0];
		redirectUrl = redirectInfo[1];
		System.out.println("准备跳转");
		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("开始跳转");
		String cookieOfRedirect = doRedirection(cookie, redirectUrl);

		StringBuffer sb = new StringBuffer(cookie);
		sb.append(';').append(cookieOfRedirect);
		System.out.println("登陆成功，最终cookie为：" + sb.toString());
		return sb.toString();
	}

	/**
	 * 使用配置的账号、密码登陆
	 * 
	 * @return 返回登陆cookie
	 */
//	public String doLogin() {
//		return this.doLogin(Constants.LOGIN_USERNAME, Constants.LOGIN_PASSWORD);
//	}
}
