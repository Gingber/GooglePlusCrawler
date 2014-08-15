/**
 * 
 */
package com.iie.taskdistributor;

import java.io.IOException;
import java.util.List;

import com.google.api.services.plus.Plus;
import com.iie.googleplus.Crawler.Authenticate;
import com.iie.googleplus.Crawler.Comments;
import com.iie.googleplus.Dboperator.MongoOperator;
import com.iie.googleplus.Dboperator.MySQLOperator;
import com.mongodb.DB;

/**
 * @author IIEIR
 *
 */
/**
 * 自定义的工作线程，持有分派给它执行的任务列表
 */
public class WorkThread extends Thread {
	// 本线程待执行的任务列表，你也可以指为任务索引的起始值
	private List<Task> taskList = null;
	@SuppressWarnings("unused")
	private int threadId;
	private DB mongodb;
	private Plus plus;

	/**
	 * 构造工作线程，为其指派任务列表，及命名线程 ID
	 * 
	 * @param taskList
	 *            欲执行的任务列表
	 * @param threadId
	 *            线程 ID
	 */
	@SuppressWarnings("unchecked")
	public WorkThread(List taskList, int threadId) {
		this.taskList = taskList;
		this.threadId = threadId;
	}
	
	public void doWork() throws Exception {
		System.out.println(taskList.size() + "\t" + taskList.get(0).getTaskId());
		//Comments.getActivityIds(this.mongodb, taskList.get(0).getTaskId(), taskList.size());
	}
	
	public void InitDB() throws Exception {
		mongodb = MongoOperator.LinkMongodb();
		plus = Authenticate.AuthorizedGenerator(1);
	}

	/**
	 * 执行被指派的所有任务
	 */
	public void run() {

		try {
			InitDB();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for (Task task : taskList) {
			try {
				task.execute(plus, mongodb, 1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
