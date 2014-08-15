package com.iie.googleplus.Crawler;

import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.impl.client.DefaultHttpClient;

import com.google.api.services.plus.Plus;
import com.iie.googleplus.Dboperator.MySQLOperator;
import com.iie.googleplus.Platform.LogSys;
import com.iie.googleplus.Report.ReportData;
import com.iie.googleplus.analyzer.beans.GPlusUser;
import com.iie.googleplus.task.beans.Task;
import com.iie.googleplus.tool.BasePath;
import com.iie.googleplus.tool.MulityInsertDataBase;
import com.iie.googleplus.tool.ReadTxtFile;

public abstract class AbstractCrawler {
	
	public ExecutorService service = Executors.newCachedThreadPool();
	
	public static Plus plus;
	public MySQLOperator dboperator;
	public static int appNum = 1;
	public static int appTotal = 100;
		
	public abstract boolean doCrawl(Task task, MulityInsertDataBase dbo, Vector<GPlusUser> RelateUsers, ReportData reportData);
	
	public AbstractCrawler() {
		String base = BasePath.getBase();
		ReadTxtFile rxf = new ReadTxtFile(base + "/config/clientproperties.ini");
		Vector<String> vector = rxf.read();
		for (String t : vector) {
			if (t.startsWith("http.appTotal")) {
				String res = t.substring(t.indexOf('=') + 1);
				appTotal = Integer.parseInt(res);
			}
		}
	}
	
	public String openLink(final DefaultHttpClient httpclient, final String targetUrl, final Task task,final int count, WebOperationResult webres) {
		String WebPageContent = null;
		Future<String> future = service.submit(new Callable<String>() {
			public String call() throws Exception {
				try {
					return WebOperationAjax.openLink(httpclient,task,targetUrl,count);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				return null;
			}
		});
		
		try{
			WebPageContent = (String) future.get(60000, TimeUnit.MILLISECONDS);			
		}catch(TimeoutException ex){
			LogSys.nodeLogger.error("OpenURL TimeOut(60s):" + targetUrl);
			webres = WebOperationResult.TimeOut;
		}catch (Exception e) {
			e.printStackTrace();
			LogSys.nodeLogger.error(e.getMessage());
			LogSys.nodeLogger.error("OpenURL Error URL:" + targetUrl);
			webres = WebOperationResult.Fail;
			WebPageContent = null;
		}
		if(WebPageContent == null){
			
		}else{
			
		}
		return WebPageContent;

	}
	
	public boolean CheckValidation(String content){
		if(content==null||content.length()<=1||content.contains("在此服务器上找不到请求的网址")){
			return false;
		}
		return true;
	}

}
