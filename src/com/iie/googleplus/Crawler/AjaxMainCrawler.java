/**
 * 
 */
package com.iie.googleplus.Crawler;

import java.util.Vector;

import org.apache.http.impl.client.DefaultHttpClient;

import com.iie.googleplus.CrawlerNode.AjaxNode;
import com.iie.googleplus.Dboperator.MySQLOperator;
import com.iie.googleplus.Platform.LogSys;
import com.iie.googleplus.Report.ReportData;
import com.iie.googleplus.StatusTrack.MyTracker;
import com.iie.googleplus.analyzer.beans.GPlusUser;
import com.iie.googleplus.task.beans.Task;
import com.iie.googleplus.tool.AllHasInsertedException;
import com.iie.googleplus.tool.MulityInsertDataBase;
import com.iie.httpclient.crawler.GoogleClientManager;


/**
 * @author Gingber
 *
 */
public class AjaxMainCrawler extends AjaxMainCrawlerFramework {
	
	MyTracker tracker = new MyTracker();
	
	public AjaxMainCrawler(String name, AjaxNode fatherNode) {
		this.name = name;
		this.node = fatherNode;
	}
	
	private void InitHttpclientAndConnection(){
		
		GoogleClientManager gcm = new GoogleClientManager();
		DefaultHttpClient httpclient = gcm.getClientNoProxy();
		
		AdvanceLoginManager logon = new AdvanceLoginManager(httpclient);
		logon.trylogin();
		this.httpclient = httpclient;
		
		MySQLOperator dbop = new MySQLOperator();
		this.DBOp = dbop;
		
	}
	
	public void run() {
		InitHttpclientAndConnection();
		System.out.println("start run");
		while(true) {
			doWork();
		}
	}

	public void doWork() {
		System.out.println("The Crawler is running for Google+......");

		plus = Authenticate.AuthorizedGenerator(1);
		MulityInsertDataBase batchdb =  new MulityInsertDataBase();
		
		SearchCrawler search = new SearchCrawler(this.plus, this.DBOp);
		AjaxUserProfileCrawler people = new AjaxUserProfileCrawler(this.httpclient, this.DBOp);
		ActivityCrawler activities = new ActivityCrawler(this.plus, this.DBOp);
		AjaxFollowerCrawler follower = new AjaxFollowerCrawler(this.httpclient, this.DBOp);
		AjaxFolloweeCrawler followee = new AjaxFolloweeCrawler(this.httpclient, this.DBOp);
		
		while (true) {
			Task task = this.getTask();
			if(task==null){
				SLEEP(1000);
				continue;
			}
					
			Vector<GPlusUser> users = new Vector<GPlusUser>(100);
			ReportData reportData=new ReportData();
			boolean flag = false;
			String ErrorMsg="";
			try{
				switch (task.ownType) {
				  	case Search: {
				  		flag = search.doCrawl(task, batchdb, users, reportData);
				  		sentKeyUsers(users);
				  		break;
				  	}
				  	case About: {
				  		flag = people.doCrawl(task, batchdb, users, reportData);
				  		break;
				  	}
				  	case Timeline: {
				  		flag = activities.doCrawl(task, batchdb, users, reportData);
				  		break;
				  	}
				  	case Follower: {
				  		flag = follower.doCrawl(task, batchdb, users, reportData);
				  		sentNormalUsers(users);
				  		break;
				  	}
				  	case Followee: {
				  		flag = followee.doCrawl(task, batchdb, users, reportData);
				  		sentNormalUsers(users);
				  		break;
				  	}
					default:
						break;
			
				}
			
				GPlusUser[] userArray=new GPlusUser[users.size()];
				users.toArray(userArray);
				try{
					if(users.size()>0){
						batchdb.insertIntoUser(userArray, "user");
					}
				}catch(AllHasInsertedException ex){
					LogSys.nodeLogger.debug("所有用户均已经插入：Task:["+task.toString()+"]");
				}
			
				if(flag) {
					tracker.FinishTask(task, ErrorMsg);
				} else{
					tracker.FailTask(task, ErrorMsg);
				}
				
				//节点中的汇报数据进行累加，累加后MainSearch中数据清零。
				LogSys.nodeLogger.debug(String.format("Nodename:[%s] message:%d,user:%d,userrel%d",this.node.NodeName,reportData.message_increment,reportData.user_increment,reportData.message_rel_increment));
				node.rpdata.add(reportData);
				reportData=null;
				
			} catch(Exception ex) {
				LogSys.nodeLogger.error("采集发生错误");
				ex.printStackTrace();
			}
		
		}

	}
	
	private void sentKeyUsers(Vector<GPlusUser> users){
		StringBuffer sb=new StringBuffer();
		for(GPlusUser t:users){
			sb.append("<name>");
			String name=t.userId;
			if(name.contains("@")){
				name=name.replaceAll("@", "");
			}
			sb.append(name);
			sb.append("</name>");
		}
		node.addKeyUserIDs(sb.toString());
		LogSys.nodeLogger.debug("Send To Server KeyUser"+sb.toString());		
		
	}
	
	private void sentNormalUsers(Vector<GPlusUser> users) {		
		StringBuffer sb=new StringBuffer();
		sb.append("<count>"+users.size()+"</count>");
		for(GPlusUser item : users) {					
			String name = item.userId;
			sb.append("<name>"+name+"</name>");
			int sum=1000;
			sb.append("<sum>"+sum+"</sum>");		
		}
		node.addNomalUserIDs(sb.toString());
		LogSys.nodeLogger.info("向服务器回发NormalUserJms"+sb.toString());				
	}
		
	private Task getTask(){
		Task task=this.node.getTask();
		return task;
	}
	private void SLEEP(int count){
		try {
			Thread.sleep(count);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AjaxMainCrawler mainCrawler = new AjaxMainCrawler("First MainCrawler", null);
		Thread mainSearchThread = new Thread(mainCrawler);
		mainSearchThread.setName(mainCrawler.name);
		mainSearchThread.start();
	}

}
