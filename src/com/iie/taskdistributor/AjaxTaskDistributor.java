/**
 * 
 */
package com.iie.taskdistributor;

import java.util.ArrayList;
import java.util.List;

import com.iie.googleplus.Dboperator.MongoOperator;
import com.iie.googleplus.Dboperator.MySQLOperator;
import com.mongodb.DB;

/**
 * @author Gingber
 *
 */
public class AjaxTaskDistributor {

	/**
	 * 测试方法
	 * @param args
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		// 初始化要执行的任务列表

		System.out.println("hey~");
		
	}
	

	/**
	 * 把 List 中的任务分配给每个线程，先平均分配，剩于的依次附加给前面的线程 返回的数组有多少个元素 (List) 就表明将启动多少个工作线程
	 * 
	 * @param taskList
	 *            待分派的任务列表
	 * @param threadCount
	 *            线程数
	 * @return 列表的数组，每个元素中存有该线程要执行的任务列表
	 */
	@SuppressWarnings("unchecked")
	public static List[] distributeTasks(List taskList, int threadCount) {
		// 每个线程至少要执行的任务数,假如不为零则表示每个线程都会分配到任务
		int minTaskCount = taskList.size() / threadCount;
		// 平均分配后还剩下的任务数，不为零则还有任务依个附加到前面的线程中
		int remainTaskCount = taskList.size() % threadCount;
		// 实际要启动的线程数,如果工作线程比任务还多
		// 自然只需要启动与任务相同个数的工作线程，一对一的执行
		// 毕竟不打算实现了线程池，所以用不着预先初始化好休眠的线程
		int actualThreadCount = minTaskCount > 0 ? threadCount
				: remainTaskCount;
		// 要启动的线程数组，以及每个线程要执行的任务列表
		List[] taskListPerThread = new List[actualThreadCount];
		int taskIndex = 0;
		// 平均分配后多余任务，每附加给一个线程后的剩余数，重新声明与 remainTaskCount
		// 相同的变量，不然会在执行中改变 remainTaskCount 原有值，产生麻烦
		int remainIndces = remainTaskCount;
		for (int i = 0; i < taskListPerThread.length; i++) {
			taskListPerThread[i] = new ArrayList();
			// 如果大于零，线程要分配到基本的任务
			if (minTaskCount > 0) {
				for (int j = taskIndex; j < minTaskCount + taskIndex; j++) {
					taskListPerThread[i].add(taskList.get(j));
				}
				taskIndex += minTaskCount;
			}
			// 假如还有剩下的，则补一个到这个线程中
			if (remainIndces > 0) {
				taskListPerThread[i].add(taskList.get(taskIndex++));
				remainIndces--;
			}
		}
		
		// 打印任务的分配情况
		for (int i = 0; i < taskListPerThread.length; i++) {
			System.out.println("线程 "
					+ i
					+ " 的任务数："
					+ taskListPerThread[i].size()
					+ " 区间["
					+ ((AjaxTask) taskListPerThread[i].get(0)).getTaskId()
					+ ","
					+ ((AjaxTask) taskListPerThread[i].get(taskListPerThread[i].size() - 1))
							.getTaskId() + "]");
		}
		return taskListPerThread;
	}

}
