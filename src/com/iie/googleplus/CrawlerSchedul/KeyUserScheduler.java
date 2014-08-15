package com.iie.googleplus.CrawlerSchedul;

import java.util.Iterator;
import java.util.TimerTask;
import java.util.Vector;

import org.junit.Test;

import com.iie.googleplus.CrawlerServer.CrawlerServer;
import com.iie.googleplus.DAO.DBKeyUserDAO;
import com.iie.googleplus.DAO.bean.KeyUser;
import com.iie.googleplus.Platform.LogSys;
import com.iie.googleplus.task.beans.Task;
import com.iie.googleplus.task.beans.Task.TaskType;
import com.iie.googleplus.tool.BasePath;
import com.iie.googleplus.tool.ReadTxtFile;

//
public class KeyUserScheduler extends TimerTask{
	private CrawlerServer crawlserver;
	private DBKeyUserDAO dbkeyuser;
	private String basepath=BasePath.getBase();
	private String FilePath=basepath+"/UsefulFile/keyusers.txt";
	
	public KeyUserScheduler(){
		LogSys.crawlerServLogger.info("KeyUserScheduler Created");
	}
	public KeyUserScheduler(CrawlerServer _crawlserver){
		this();
		this.crawlserver=_crawlserver;
		this.dbkeyuser=new DBKeyUserDAO();
		
	}
	@Override
	public void run() {
		Vector<KeyUser> all=this.getAllKeyUsers();
		LogSys.crawlerServLogger.info("KeyUserScheduler[触发采集] Find Keyuser["+all.size()+"]");
		for(int i=0;i<all.size();i++){
			String t=all.get(i).UserID;
			Task task=new Task();
			task.setOwnType(TaskType.Timeline);
			task.setTargetString(t);
			crawlserver.addKeyUserTask(task);
	
			//关键账户主要对推文和Profile进行扫描

			task=new Task();
			task.setOwnType(TaskType.About);
			task.setTargetString(t);
			crawlserver.addKeyUserTask(task);	
		}
		
	}
	public Vector<KeyUser> getAllKeyUsers(){
		Vector<KeyUser> all=new Vector<KeyUser>();
		all.addAll(GetTextUser());
		all.addAll(GetDBUser());
		return all;

	}
	private Vector<KeyUser> GetDBUser(){
		Vector<KeyUser> dbUser=dbkeyuser.GetKeyUser();
		return dbUser;
	}
	public Vector<KeyUser> GetTextUser(){
		Vector<KeyUser> all=new Vector<KeyUser>();
		ReadTxtFile rxf=new ReadTxtFile(FilePath);
		Vector<String> allTxtName=rxf.read();
		Iterator<String> it=allTxtName.iterator();
		while(it.hasNext()){
			String name=it.next();
			if(name.startsWith("//")){
				continue;
			}
			KeyUser keyUser=new KeyUser(name,0,0);
			all.add(keyUser);
		}
		return all;
	}
	
	@Test
	public void doText(){
		KeyUserScheduler t=new KeyUserScheduler(null);
		Vector mm=t.getAllKeyUsers();
		System.out.println(mm.size());
	}
	


}
