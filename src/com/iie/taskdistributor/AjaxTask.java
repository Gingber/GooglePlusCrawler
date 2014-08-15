/**
 * 
 */
package com.iie.taskdistributor;

import com.iie.googleplus.Dboperator.MySQLOperator;
import com.iie.httpclient.crawler.AjaxUserProfileCrawler;

/**
 * @author IIEIR
 *
 */
/**
 * Ҫִ�е����񣬿���ִ��ʱ�ı�����ĳ��״̬���������ĳ������ ��������������״̬�����������У���ɣ�Ĭ��Ϊ����̬ Ҫ��һ�����ƣ���Ϊ Task
 * ����״̬��Ǩ�ļ���������֮����UI����ʾ
 */
public class AjaxTask {
	public static final int READY = 0;
	public static final int RUNNING = 1;
	public static final int FINISHED = 2;
	@SuppressWarnings("unused")
	private int status;
	// ����һ�����������ҵ����ı��������ڱ�ʶ����
	private String taskId;

	// ����ĳ�ʼ������
	public AjaxTask(String taskId) {
		this.status = READY;
		this.taskId = taskId;
	}

	/**
	 * ִ������
	 * @throws Exception 
	 */
	public void execute(AjaxUserProfileCrawler aupc) throws Exception {
		System.out.println("*******************************************************");
		long startTime2 = System.currentTimeMillis();   //��ȡ��ʼʱ��
		// ����״̬Ϊ������
		setStatus(Task.RUNNING);
		System.out.println("��ǰ�߳� ID �ǣ�" + Thread.currentThread().getName() + " | ���� ID �ǣ�" + this.taskId);

		String url = "https://plus.google.com/" + this.taskId + "/about";
		boolean flag = aupc.doCrawler(url);
   
		MySQLOperator.updateRecordToTable("user_profile_id", this.taskId);
		
		// ����һ����ʱ
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		// ִ����ɣ���״̬Ϊ���
		setStatus(FINISHED);
		
		long endTime2 = System.currentTimeMillis();
        System.out.println("Spend time fetching data�� "+(endTime2-startTime2)+"ms");
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTaskId() {
		return taskId;
	}
}