/**
 * 
 */
package com.iie.googleplus.Crawler;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;

import org.apache.http.impl.client.DefaultHttpClient;

import com.iie.googleplus.Dboperator.MySQLOperator;
import com.iie.googleplus.Platform.LogSys;
import com.iie.googleplus.Report.ReportData;
import com.iie.googleplus.analyzer.beans.GPlusUser;
import com.iie.googleplus.analyzer.beans.UserProfile;
import com.iie.googleplus.task.beans.Task;
import com.iie.googleplus.task.beans.Task.TaskType;
import com.iie.googleplus.tool.MulityInsertDataBase;
import com.iie.httpclient.crawler.GoogleClientManager;
import com.iie.util.TxtWriter;

/**
 * @author Gingber
 *
 */
public class AjaxSearchCrawler extends AbstractCrawler {
	
	private static String baseUrl = "https://plus.google.com/s/%s";
	private DefaultHttpClient httpclient;
	
	public 	AjaxSearchCrawler(DefaultHttpClient httpclient, MySQLOperator dbop) {
		this.httpclient = httpclient;
		super.dboperator = dbop;
	}
	
	public boolean doCrawl(Task task, MulityInsertDataBase dbo, Vector<GPlusUser> RelateUsers, ReportData reportData) {
		LogSys.nodeLogger.debug("Search Grabber is starting......");
		String keyword = task.getTargetString();
		System.out.println("current crawl keyword:\t" + keyword);

		try {
			
			String URL = String.format(baseUrl, keyword);
			
			String ajaxContent = super.openLink(httpclient, URL, task, 0, null);
			if(!this.CheckValidation(ajaxContent)) {
				LogSys.nodeLogger.error("ProfileÍøÂçÇëÇóÊ§°Ü:" + keyword);
				return false;			
			}
			
			try {
				TxtWriter.saveToFile(ajaxContent, new File("d:/gp.txt"), "utf-8");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("haha~");
			
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		
		
		return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		GoogleClientManager gcm = new GoogleClientManager();
		DefaultHttpClient httpclient = gcm.getClientNoProxy();
		
		AdvanceLoginManager logon = new AdvanceLoginManager(httpclient);
		logon.trylogin();

		MySQLOperator dbop = new MySQLOperator();
		AjaxSearchCrawler search = new AjaxSearchCrawler(httpclient, dbop);
		Task task = new Task(TaskType.Search, "Ðì²Åºñ¸¸×Ó");
		MulityInsertDataBase batchdb =  new MulityInsertDataBase();
		ReportData reportData=new ReportData();
		Vector<GPlusUser> RelateUsers = new Vector<GPlusUser>(100);
		search.doCrawl(task, batchdb, RelateUsers, reportData);

	}
}
