package test.weiboso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

/**
 * 测试，取页面内容
 * @author mingyuan
 *
 */
public class WeiboTest extends RequestCommons {

	/**
	 * 打印流
	 * 
	 * @param in
	 *            InputStream
	 */
	private void printContent(InputStream in) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
			String line;
			System.out.println("-----------------------------------");
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 这里不关闭流，留作他用
		}
	}
	
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
	 * 测试，读取一个微博地址，打印出页面内容
	 * 
	 * @param finalCookie
	 *            cookie
	 */
	private void test(String finalCookie) {
		HttpGet get = new HttpGet("http://weibo.cn/search/?keyword=%E6%96%B9%E8%88%9F%E5%AD%90&suser=%E6%89%BE%E4%BA%BA&gsid=4uQEc85f16s4rcVzgHc7D9cGL7W&vt=4");
		setHeader(get);
		get.setHeader("Cookie", finalCookie.toString());
		HttpResponse response;
		try {
			response = httpclient.execute(get);
			HttpEntity entity = response.getEntity();
			printContent(entity.getContent());
			consume(entity);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
//			get.releaseConnection();
		}
	}

	public static void main(String[] args) {
		Login login = new Login();
		String userNameValue = "kmust.jiangbo@gmail.com";
		String passwordValue = "by14285700";
		String cookie = login.doLogin(userNameValue, passwordValue);
		System.out.println("final Cookie=" + cookie);
		new WeiboTest().test(cookie);
		//gsid_CTandWM=4uQh2ba21iYIDuBB0dfxdfIJ7fp
		//gsid_CTandWM=4ugB2ba216WHFDD4UlalmfJi7b1
	}

}
