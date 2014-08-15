/**
 * 
 */
package com.iie.taskdistributor;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import com.google.api.services.plus.Plus;
import com.google.api.services.plus.Plus.People;
import com.iie.googleplus.Crawler.Authenticate;
import com.iie.googleplus.Crawler.PeopleCrawler;
import com.iie.googleplus.Dboperator.MySQLOperator;
import com.mongodb.DB;

/**
 * @author IIEIR
 *
 */
/**
 * Ҫִ�е����񣬿���ִ��ʱ�ı�����ĳ��״̬���������ĳ������ ��������������״̬�����������У���ɣ�Ĭ��Ϊ����̬ Ҫ��һ�����ƣ���Ϊ Task
 * ����״̬��Ǩ�ļ���������֮����UI����ʾ
 */
public class Task {
	public static final int READY = 0;
	public static final int RUNNING = 1;
	public static final int FINISHED = 2;
	@SuppressWarnings("unused")
	private int status;
	// ����һ�����������ҵ����ı��������ڱ�ʶ����
	private String taskId;
	
	private int networkerror = 0;

	// ����ĳ�ʼ������
	public Task(String taskId) {
		this.status = READY;
		this.taskId = taskId;
	}

	/**
	 * ִ������
	 * @throws Exception 
	 */
	public void execute(Plus plus, DB mongodb, int count) throws Exception {
		// ����״̬Ϊ������
		setStatus(Task.RUNNING);
		System.out.println("��ǰ�߳� ID �ǣ�" + Thread.currentThread().getName() + " | ���� ID �ǣ�" + this.taskId);

		// ����һ����ʱ
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// ִ����ɣ���״̬Ϊ���
		setStatus(FINISHED);
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTaskId() {
		return taskId;
	}
}