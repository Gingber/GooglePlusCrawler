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
 * �Զ���Ĺ����̣߳����з��ɸ���ִ�е������б�
 */
public class WorkThread extends Thread {
	// ���̴߳�ִ�е������б���Ҳ����ָΪ������������ʼֵ
	private List<Task> taskList = null;
	@SuppressWarnings("unused")
	private int threadId;
	private DB mongodb;
	private Plus plus;

	/**
	 * ���칤���̣߳�Ϊ��ָ�������б��������߳� ID
	 * 
	 * @param taskList
	 *            ��ִ�е������б�
	 * @param threadId
	 *            �߳� ID
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
	 * ִ�б�ָ�ɵ���������
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
