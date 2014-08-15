package com.iie.taskdistributor;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.http.impl.client.DefaultHttpClient;

import com.iie.httpclient.crawler.AjaxUserProfileCrawler;
import com.iie.httpclient.crawler.AutoLoginGoogle;
import com.iie.httpclient.crawler.GoogleClientManager;

public class AjaxWorkThread extends Thread {
	
	// ���̴߳�ִ�е������б���Ҳ����ָΪ������������ʼֵ
	private List<AjaxTask> taskList = null;
	@SuppressWarnings("unused")
	private int threadId;
	private DefaultHttpClient httpclient;
	private String tmpcookies;

	/**
	 * ���칤���̣߳�Ϊ��ָ�������б��������߳� ID
	 * 
	 * @param taskList
	 *            ��ִ�е������б�
	 * @param threadId
	 *            �߳� ID
	 */
	@SuppressWarnings("unchecked")
	public AjaxWorkThread(List taskList, int threadId) {
		this.taskList = taskList;
		this.threadId = threadId;
	}
	
	public void doWork() throws Exception {
		System.out.println(taskList.size() + "\t" + taskList.get(0).getTaskId());
		//Comments.getActivityIds(this.mongodb, taskList.get(0).getTaskId(), taskList.size());
	}

	public void InitHttpClient() throws IOException, SQLException {
		GoogleClientManager gcm = new GoogleClientManager();
		httpclient = gcm.getClientNoProxy();
		AutoLoginGoogle alg = new AutoLoginGoogle(httpclient);
		tmpcookies = alg.doAutoLoginGoogle();
		
	}	
	/**
	 * ִ�б�ָ�ɵ���������
	 */
	public void run() {
		
		try {
			InitHttpClient();
			AjaxUserProfileCrawler aupc = new AjaxUserProfileCrawler(httpclient, tmpcookies);
			
			for (AjaxTask task : taskList) {
				task.execute(aupc);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	
	}

}
