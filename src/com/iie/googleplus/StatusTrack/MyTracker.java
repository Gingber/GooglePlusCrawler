package com.iie.googleplus.StatusTrack;
import org.junit.Before;
import org.junit.Test;

import com.iie.googleplus.task.beans.Task;
import com.iie.googleplus.task.beans.Task.TaskType;


public class MyTracker extends BaseTracker {
	CrawlTaskDB statusdb;
	public MyTracker(){
		statusdb=new CrawlTaskDB();
	}
	
	@Override
	public int AddTask(Task task){		
		return statusdb.AddTask(task.getTargetString(), TaskType2CrawlTaskType(task.ownType));
	}

	@Override
	public void CheckTask(Task task) {
		// TODO Auto-generated method stub

	}


	@Override
	public void FinishTask(Task task,String ErrorMsg) {
		statusdb.FinishTask(task.getTargetString(), TaskType2CrawlTaskType(task.ownType),true,"OK");
		
	}

	@Override
	public void FailTask(Task task,String ErrorMsg) {
		// TODO Auto-generated method stub
		statusdb.FinishTask(task.getTargetString(), TaskType2CrawlTaskType(task.ownType),false,ErrorMsg);
	}

	private CrawlTaskType TaskType2CrawlTaskType(TaskType tasktype){
		switch(tasktype){
		case About:
			return CrawlTaskType.Profile;
		case Follower:
			return CrawlTaskType.Follower;
		case Followee:
			return CrawlTaskType.Followee;
		case Search:
			return CrawlTaskType.Search;
		case Timeline:
			return CrawlTaskType.Timeline;
		default:
			return CrawlTaskType.Timeline;
			
		}
	}
	MyTracker track;
	@Before
	public void before(){
		track=new MyTracker();
	}
	
	@Test
	public void test(){
		Task t=new Task(TaskType.Timeline, "BigBang");
		track.AddTask(t);
		track.FailTask(t,"~~");
		
	}
}
