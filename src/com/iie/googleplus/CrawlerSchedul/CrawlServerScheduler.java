package com.iie.googleplus.CrawlerSchedul;

import java.util.TimerTask;
import java.util.Vector;

import com.iie.googleplus.CrawlerServer.CrawlerServer;
import com.iie.googleplus.CrawlerServer.InputType;
import com.iie.googleplus.DatabaseBean.DBBeanInputTask;
import com.iie.googleplus.DatabaseBean.InputTaskBean;
import com.iie.googleplus.Platform.LogSys;
import com.iie.googleplus.task.beans.Task;
import com.iie.googleplus.task.beans.Task.TaskType;

public class CrawlServerScheduler extends TimerTask {
	private CrawlerServer crawlserver;
	private DBBeanInputTask inputTaskOp;
	public CrawlServerScheduler(CrawlerServer crawlserver){
		this.crawlserver=crawlserver;
		inputTaskOp=new DBBeanInputTask();
	}	
	public void run() {
		checkUserInsert();
		
	}
	//检查是否有用户插入的数据
	public void checkUserInsert(){
		if(inputTaskOp.CheckHasNewInput()){//当发现存在新插入的数据时，将新插入的数据加入到采集任务表
			LogSys.crawlerServLogger.debug("发现新的需要加入的任务");
			Vector<InputTaskBean> vector=inputTaskOp.GetAllTask();
			for(int i=0;i<vector.size();i++){
				LogSys.crawlerServLogger.debug("【调度器】加入的任务:"+vector.get(i).TaskName+":"+vector.get(i).InputType);
				InserIntoMessbus(vector.get(i));
				inputTaskOp.ModifyStatus("Crawling", vector.get(i).ID);
			}			
		}
	}
	private void InserIntoMessbus(InputTaskBean inputTask){
		//根据获取到的InputTask调用CrawlServer方法进行加入操作
		if(inputTask.InputType==InputType.Topic){
			String parameter=inputTask.TaskParameter;
			String[] keywords=parameter.split(" ");
			for(int i=0;i<keywords.length;i++){
				Task task=new Task();
				task.setTrack(true);
				task.setMainTypeID(inputTask.ID);//设置MainTypeID
				task.setOwnType(TaskType.Search);
				task.setTargetString(keywords[i]);
				crawlserver.addTopic(task);
			}
			String parameter2=inputTask.TaskParameter2;
			String[] keywords2=parameter2.split(" ");
			for(int i=0;i<keywords2.length;i++){
				Task task=new Task();
				task.setTrack(true);
				task.setMainTypeID(inputTask.ID);//设置MainType为用户输入的ID
				task.setOwnType(TaskType.Timeline);
				task.setTargetString(keywords2[i]);
				crawlserver.addTopic(task);
				task.setOwnType(TaskType.Follower);////搜索用户的粉丝
				task.setTargetString(keywords2[i]);
				crawlserver.addTopic(task);
				task.setOwnType(TaskType.Followee);//搜索用户的关注
				task.setTargetString(keywords2[i]);
				crawlserver.addTopic(task);
				task.setOwnType(TaskType.About);//搜索用户的主页
				task.setTargetString(keywords2[i]);
				crawlserver.addTopic(task);
			}

		}
		if(inputTask.InputType==InputType.KeyUser){
			Task task=new Task();
			task.setTrack(true);
			task.setMainTypeID(inputTask.ID);
			task.setTargetString(inputTask.TaskName);//把主题的名称也作为关键词进行搜索
			task.setOwnType(TaskType.Timeline);//关注重点账户的推文
			crawlserver.addKeyUserTask(task);
			task.setOwnType(TaskType.Followee);
			crawlserver.addKeyUserTask(task);
			task.setOwnType(TaskType.Follower);
			crawlserver.addKeyUserTask(task);
		}
		if(inputTask.InputType==InputType.KeyWord){
			Task task=new Task();
			task.setTrack(true);
			task.setMainTypeID(inputTask.ID);//要跟踪的MainTypeID是
			task.setTargetString(inputTask.TaskName);//把主题的名称也作为关键词进行搜索
			task.setOwnType(TaskType.Search);//设置类型为Search类型
			crawlserver.addKeyUserTask(task);
			
		}
		if(inputTask.InputType==InputType.NorUser){
			Task task=new Task();
			task.setTrack(true);
			task.setMainTypeID(inputTask.ID);//要跟踪的MainTypeIDID是
			task.setTargetString(inputTask.TaskName);//对普通用户的检索等于对普通用户的时间，粉丝等进行检索
			task.setOwnType(TaskType.Timeline);//
			crawlserver.addTask(task);
			task.setOwnType(TaskType.Followee);
			crawlserver.addTask(task);
			task.setOwnType(TaskType.Follower);
			crawlserver.addTask(task);
		}
	}
	
}
