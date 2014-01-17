package com.google.api.services.samples.plus.cmdline;

import java.io.IOException; 
import java.util.ArrayList; 
import java.util.List; 

import org.apache.commons.httpclient.Cookie; 
import org.apache.commons.httpclient.HttpClient; 
import org.apache.commons.httpclient.HttpException; 
import org.apache.commons.httpclient.NameValuePair; 
import org.apache.commons.httpclient.cookie.CookiePolicy; 
import org.apache.commons.httpclient.methods.PostMethod; 
import org.apache.commons.httpclient.methods.StringRequestEntity; 
import org.apache.http.message.BasicNameValuePair; 

public class NewTest1 { 
public static void main(String[] args) throws Exception { 

HttpClient client = new HttpClient(); 
// /////////////////////////////第一次登陆///////////////////////////// 
List<NameValuePair> data = new ArrayList<NameValuePair>(); 
data.add(new NameValuePair("ltmpl", "yj_blanco")); 
data 
.add(new NameValuePair("continue", 
"https://mail.google.com/mail/")); 
data.add(new NameValuePair("ltmplcache", "2")); 
data.add(new NameValuePair("service", "mail")); 
data.add(new NameValuePair("rm", "false")); 
data.add(new NameValuePair("hl", "en")); 
data.add(new NameValuePair("Email", "kmust.jiangbo@gmail.com")); 
data.add(new NameValuePair("Passwd", "by14285700")); 
data.add(new NameValuePair("rmShown", "1")); 
data.add(new NameValuePair("null", "Sign in")); 
// data.add(new NameValuePair("verifycookie", "0")); 
// data.add(new NameValuePair("product", "mail163")); 
// data.add(new NameValuePair("username", "zhangbaoxin231@163.com")); 
// data.add(new NameValuePair("password", "3266472")); 
PostMethod httpPost = new PostMethod( 
"https://www.google.com/accounts/ServiceLoginAuth"); 
//	 httpPost.setRequestBody(data.toArray(new NameValuePair[data.size()])); 
client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY); 



// 一定要有，否则会生成多个Cookie header送给web server 
client.getParams().setParameter("http.protocol.single-cookie-header", 
true); 
client.getParams().setParameter("http.protocol.content-charset", 
"gb2312"); 


PostMethod httpPost1 = new PostMethod( 
"https://www.google.com/accounts/ServiceLogin?service=mail&passive=true&rm=false&continue=https%3A%2F%2Fmail.google.com%2Fmail%2F%3Fui%3Dhtml%26zy%3Dl&bsv=1eic6yu9oa4y3&ss=1&scc=1&ltmpl=default&ltmplcache=2&hl=zh-CN"); 
client.executeMethod(httpPost1); 
        String GALX = null; 
StringBuffer bu1 = new StringBuffer(); 
for (Cookie c : client.getState().getCookies()) { 
bu1.append(c.getName()); 
bu1.append("="); 
bu1.append(c.getValue()); 
bu1.append("\n"); 
if(c.getName().equals("GALX")) 
GALX = c.getValue(); 

} 
System.out.println(bu1.toString()); 
httpPost.setRequestHeader("Cookie", 
"__utmx=173272373.; __utmxx=173272373.; __utma=173272373.277235200.1284868629.1284868629.1286418541.2;" + 
" __utmz=173272373.1284868629.1.1.utmccn=(direct)|utmcsr=(direct)|utmcmd=(none); " + 
"GoogleAccountsLocale_session=zh_CN; GALX="+GALX+"; PREF=ID=ec39501eba0ce0e3:U=d0ecc3b0a29bd9a5:TM=1284451856:LM=1284877014:S=VnxXUxRIJxO4dHoM; NID=39=YFRCbJct1WhHp1IRvhz5skCskZ6dW82Ave_-EoBrEymXm_mzJQ8VVVJSfomVxZgFG5TcGaDzZUMs1TiH7fo9VGaxRZ1ftHpUyQkgceVLYTMyugnbIRDQhNL_yKOeqEqH; TZ=-480; GMAIL_RTT=280; GMAIL_LOGIN=T1286846861638/1286846861638/1286847020950"); 
httpPost.setRequestHeader("Content-Type", "application/x-www-form-urlencoded"); 
httpPost.setRequestEntity(new StringRequestEntity("ltmpl=default" + 
"&ltmplcache=2" + 
"&continue=https%3A%2F%2Fmail.google.com%2Fmail%2F%3F" + 
"&service=mail" + 
"&rm=false" + 
//	 "&dsh=-1094667995636017014" + 
"&ltmpl=default" + 
"&hl=zh-CN" + 
"&ltmpl=default" + 
"&scc=1" + 
"&ss=1" + 
"&timeStmp=" + 
"&secTok=" + 
"&GALX="+ GALX+ 
"&Email=username" + 
"&Passwd=pass" + 
"&rmShown=1" + 
"&signIn=%E7%99%BB%E5%BD%95" + 
"&asts=","application/xml","UTF-8")); 
client.executeMethod(httpPost); 

//	 bu1 = new StringBuffer(); 
//	 for (Cookie c : client.getState().getCookies()) { 
//	 bu1.append(c.getName()); 
//	 bu1.append("="); 
//	 bu1.append(c.getValue()); 
//	 bu1.append("\n"); 
//	 } 

//System.err.println(bu1.toString()); 
//System.err.println(httpPost.getResponseBodyAsString()); 

// 

PostMethod httpPost2 = new PostMethod( 
"https://mail.google.com/mail/contacts/data/contacts?thumb=true&show=ALL&enums=true&psort=Name&max=10000&out=js&rf=&jsx=true"); 
client.executeMethod(httpPost2); 

System.err.println(httpPost2.getResponseBodyAsString());
}
}