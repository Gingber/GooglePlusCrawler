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
 * 要执行的任务，可在执行时改变它的某个状态或调用它的某个操作 例如任务有三个状态，就绪，运行，完成，默认为就绪态 要进一步完善，可为 Task
 * 加上状态变迁的监听器，因之决定UI的显示
 */
public class AjaxTask {
	public static final int READY = 0;
	public static final int RUNNING = 1;
	public static final int FINISHED = 2;
	@SuppressWarnings("unused")
	private int status;
	// 声明一个任务的自有业务含义的变量，用于标识任务
	private String taskId;

	// 任务的初始化方法
	public AjaxTask(String taskId) {
		this.status = READY;
		this.taskId = taskId;
	}

	/**
	 * 执行任务
	 * @throws Exception 
	 */
	public void execute(AjaxUserProfileCrawler aupc) throws Exception {
		System.out.println("*******************************************************");
		long startTime2 = System.currentTimeMillis();   //获取开始时间
		// 设置状态为运行中
		setStatus(Task.RUNNING);
		System.out.println("当前线程 ID 是：" + Thread.currentThread().getName() + " | 任务 ID 是：" + this.taskId);

		String url = "https://plus.google.com/" + this.taskId + "/about";
		boolean flag = aupc.doCrawler(url);
   
		MySQLOperator.updateRecordToTable("user_profile_id", this.taskId);
		
		// 附加一个延时
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		// 执行完成，改状态为完成
		setStatus(FINISHED);
		
		long endTime2 = System.currentTimeMillis();
        System.out.println("Spend time fetching data： "+(endTime2-startTime2)+"ms");
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTaskId() {
		return taskId;
	}
}