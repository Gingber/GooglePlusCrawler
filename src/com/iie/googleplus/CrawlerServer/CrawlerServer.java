package com.iie.googleplus.CrawlerServer;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.iie.googleplus.CrawlerMessage.MessageBusComponent;
import com.iie.googleplus.CrawlerNode.ControlReceiver;
import com.iie.googleplus.CrawlerNode.ControlSender;
import com.iie.googleplus.CrawlerNode.NodeStep;
import com.iie.googleplus.CrawlerSchedul.CrawlServerScheduler;
import com.iie.googleplus.CrawlerSchedul.KeyUserScheduler;
import com.iie.googleplus.CrawlerSchedul.KeyWordsScheduler;
import com.iie.googleplus.CrawlerSchedul.ReportTotalScheduler;
import com.iie.googleplus.MessageBus.GetAceiveMqConnection;
import com.iie.googleplus.MessageBus.MessageBusNames;
import com.iie.googleplus.MessageBus.Receiver;
import com.iie.googleplus.MessageBus.Sender;
import com.iie.googleplus.MessageBusTest.MessageBusCleanner;
import com.iie.googleplus.Platform.LogSys;
import com.iie.googleplus.Platform.PlatFormMain;
import com.iie.googleplus.Report.CrawlerServerReporter;
import com.iie.googleplus.Report.NodeReporterReceiver;
import com.iie.googleplus.Report.ReportData;
import com.iie.googleplus.StatusTrack.MyTracker;
import com.iie.googleplus.tool.BasePath;
import com.iie.googleplus.task.beans.Task;
import com.iie.googleplus.task.beans.Task.MainType;
import com.iie.googleplus.task.beans.Task.TaskType;

public class CrawlerServer extends MessageBusComponent implements Runnable,MessageBusNames{

	/**
	 * 必须要改！！
	 */
	public static final String Name="TwitterWEB";
	public enum ServerStep{
		init,searchStart,searchEnd,keyuserCaijiStart,keyuserCaijiEnd,normalCaijiStart,normalCaijiEnd
	}
	protected static enum OP {START, STOP, DUMP, RESTART, CUSTORM, REFRESH, SAMPLE};
	public com.iie.googleplus.CrawlerServer.CrawlerServer.ServerStep currentstep=ServerStep.init;
	public int Normal_User_Deepth = 20;
	
	//控制相关
	Receiver controlReceiver;	
	ControlSender controlSender;
	//Task相关
	Sender taskSender;
	Sender urgentTaskSender;
	Sender keyWordAndTopicTaskSender;
	Sender keyUserTaskSender;
	//NormalUser 相关
	Receiver NormalReceiver;
	Receiver KeyUserReceiver;
	Receiver nodeReporterReceiver;
	
	/***************************************/
	public HashMap<String,ReportData> NodeReportData;
	public Object reportlock=new Object();
	public ReportData ServerReportData;//服务器需要汇报的数据综合，来自各个采集节点的数据
	public CrawlerServerReporter crawlReporter;
	/***************************************/
	
	/***************************************/
	CrawlServerScheduler schedule;
	
	/****************************************/
	public static long count;
	public static long TaskID;
	
	public boolean isFirstChuiZhi=true;
	public boolean isFirstBingXing=true;

	public ServerBean sb=new ServerBean();
	
	public boolean isResume=false;
	
	
	//管理子节点进度信息
	public NodeManager nodeManager;
	private ServerControlReceiverListener scr;
	/***************************/
	protected String args[];
	protected OP op=null;
	protected int deepth=10;
	protected int keySearchCount=100;
	protected String CustomTaskFolderDir="";
	/***************************/
	
	/***************************/
	private MyTracker tracker;
	/***************************/
	public CrawlerServer(){
		//this("-Command Start -Deepth 10 -KeySearchCount 10".split(" "));	
		this("-Command Start -FolderDir D:\\ServerTask -Deepth 10 -KeySearchCount 10".split(" "));	
		
	}
	public CrawlerServer(String[] args){
		LogSys.crawlerServLogger.info("Crawlserver初始化");
		if (args.length < 1) {
			System.err.println("Usage: CrawlerServer -Command [Start|Stop|Dump|Restart] -Deepth 10 -KeySearchCount 10");
		    return;
	    }
		this.args=args;
	}
	public static void main(String[] args){
		for(int i=0;i<args.length;i++){
			System.out.print(args[i]+" ");
		}
		CrawlerServer crawler =  new CrawlerServer(args);
		Thread mthread= new Thread(crawler);
		mthread.setName("CrawlServer");
		mthread.start();
		
	}
	@Override
	public void run() {
		checkArgs();
		if(op==OP.START){
			Initiallize();
			StartCrawlServer();
		}else if(op==OP.CUSTORM){
			LogSys.crawlerServLogger.info("正在进入用户自定义采集");
			Initiallize();
			StartCustormServer();
		}else if(op==OP.REFRESH){
			Initiallize();
			StartRefreshServer();
		}
		else if(op==OP.STOP){
			System.err.println("此处需要修复");
			System.exit(-1);
			//StopCrawlServer();
		}else if(op==OP.SAMPLE){
			LogSys.crawlerServLogger.info("正在进入随机抽样采集");
			Initiallize();
			this.StartSampleServer();			
		}
		
	}
	public void checkArgs(){
		System.out.println("服务器正在检查配置参数");
		for(int i=0;i<args.length;i++){
			if(args[i].equals("-Command")){
				String command=args[++i];
				if(command.equals("Start"))
					op=OP.START;
				else if(command.equals("Stop")){
					op=OP.STOP;
				}else if(command.equals("Dump")){
					op=OP.DUMP;
				}else if(command.equals("Restart")){
					op=OP.RESTART;
				}else if(command.equals("Custorm")){
					op=OP.CUSTORM;
					if(args[++i].equalsIgnoreCase("-FolderDir")){
						this.CustomTaskFolderDir=args[++i];
						System.out.println("指定的任务文件夹是："+CustomTaskFolderDir);
					}else{
						System.err.println("没有指定FoldDir");
						System.exit(-1);
					}
				}else if(command.equals("Refresh")){
					op=OP.REFRESH;
				}else if(command.equalsIgnoreCase("Sample")){
					op=OP.SAMPLE;
					StartSampleServer();
				}
			}else if(args[i].equals("-Deepth")){
				deepth=Integer.parseInt(args[++i]);
			}else if(args[i].equals("-KeySearchCount")){
				keySearchCount=Integer.parseInt(args[++i]);
			}
		}
		this.Normal_User_Deepth=deepth;
	}

	public int run(String[] args) throws Exception {
				
		return 0;
	}
	
	



	public void Initiallize(){

		javax.jms.Connection connection=GetAceiveMqConnection.StaticGetConnection();
		LogSys.crawlerServLogger.info("--------------Server初始化-------------------");		
		basepath=BasePath.getBase();		
		NodeReportData=new HashMap<String,ReportData>();
		ServerReportData=new ReportData();
		nodeManager=new NodeManager();
		scr=new ServerControlReceiverListener(this);
		urgentTaskSender=new Sender(connection,MessageBusNames.UrgentTask+"?consumer.prefetchSize=0",false);
		keyUserTaskSender=new Sender(connection,MessageBusNames.KeyUserTask+"?consumer.prefetchSize=0",false);
		keyWordAndTopicTaskSender=new Sender(connection,MessageBusNames.KeyWordAndTopicTask+"?consumer.prefetchSize=0",false);
		taskSender=new Sender(connection,MessageBusNames.Task+"?consumer.prefetchSize=0",false);	
		
		//taskReceiver=new Receiver(connection, false, MessageBusNames.Task, this, true, null);
		controlReceiver=new ControlReceiver(connection,MessageBusNames.ControlC2S,this,false,scr);
   		controlSender=new ControlSender(connection,MessageBusNames.ControlS2C,true);
		NormalReceiver=new NormalUserReceiver(connection,MessageBusNames.NormalID,this,false);
		KeyUserReceiver=new KeyUserReceiver(connection,MessageBusNames.KeyID,this,false);
		nodeReporterReceiver=new NodeReporterReceiver(connection,MessageBusNames.ReportTwitterWEB,this,false);
		

		crawlReporter=new CrawlerServerReporter("TwitterWEB");
		tracker=new MyTracker();
	}

	public static String basepath;
	
	public boolean StartCrawlServer(){
		LogSys.crawlerServLogger.info("采集器总控端开始");
		StartReportTimer();
		StartSchedulTimer();//启动调度器
		StartKeyUserSchedulTimer();//启动对KeyUser的定时采集
		StartKeyWordSchedulTimer();//启动对KeyWords的定时采集
		StartPlantSchedulerTimer();//启动普通任务定时器（包含对InputTask的刷新）
		try{
			CollectionNodes();
			KeyWordSearch(false);		
			CrawlerServerKeyUserSearch(false);
			CrawlerServerNorUserSearch(this.deepth,false);
			LogSys.crawlerServLogger.info("正常采集结束All is Finish");
		}catch(Exception ex){
			ex.printStackTrace();
			LogSys.crawlerServLogger.error("crawlServer exit with error");
		}
		LogSys.crawlerServLogger.info("采集器总控端停止");
	
		return true;
	}
	//开启用户自定义采集
	private boolean StartCustormServer(){
		LogSys.crawlerServLogger.info("采集器总控端[自定义模式]启动");
		try{
			CollectionNodes();
			sb.InitCustomerByFile(this,this.CustomTaskFolderDir);
			sendNewStep(NodeStep.search_start);
			SleepWithCount(5000);			
			sendNewStep(NodeStep.search_end);
			
			LogSys.crawlerServLogger.info("正常采集结束All is Finish");
		}catch(Exception ex){
			ex.printStackTrace();
			LogSys.crawlerServLogger.error("crawlServer exit with error");
		}
		LogSys.crawlerServLogger.info("采集器总控端停止");
		return true;
	}

	private boolean StartRefreshServer(){
		LogSys.crawlerServLogger.info("采集器总控端[刷新关键账户表推文模式]启动");
		currentstep=ServerStep.searchStart;
		try{
			CollectionNodes();
			//只取第一页的用户信息
			sb.InitRefresh(this,"crawlstatus_0309_1427","message_result_0309",TaskType.Timeline, -1);
			sendNewStep(NodeStep.search_start);
			while(currentstep!=ServerStep.searchEnd){
				CollectNodesStatus();
				SleepWithCount(60000);				
				if(nodeManager.canNextStepByTaskBusName(MessageBusNames.KeyUserTask)){
					currentstep=ServerStep.searchEnd;
					nodeManager.show();
				}else{
					LogSys.crawlerServLogger.debug("不能进入下一个采集状态");
					
				}
			}
			
			LogSys.crawlerServLogger.info("采集器总控端[刷新关键账户表推文模式]FINISH");
		}catch(Exception ex){
			ex.printStackTrace();
			LogSys.crawlerServLogger.error("crawlServer exit with error");
		}
		
		return true;
	}
	
	private boolean StartSampleServer(){
		LogSys.crawlerServLogger.info("采集器总控端[随机用户Profile]启动");
		currentstep=ServerStep.searchStart;
		try{
			CollectionNodes();
			//只取第一页的用户信息
			sb.InitSample(this, "RandomUser_2.txt", "user_profile_sample_2", TaskType.About);
			sendNewStep(NodeStep.search_start);
			while(currentstep!=ServerStep.searchEnd){
				CollectNodesStatus();
				SleepWithCount(60000);				
				if(nodeManager.canNextStepByTaskBusName(MessageBusNames.KeyUserTask)){
					currentstep=ServerStep.searchEnd;
					nodeManager.show();
				}else{
					LogSys.crawlerServLogger.debug("不能进入下一个采集状态");
					
				}
			}
			
			LogSys.crawlerServLogger.info("采集器总控端[随机用户Profile]FINISH");
		}catch(Exception ex){
			ex.printStackTrace();
			LogSys.crawlerServLogger.error("crawlServer exit with error");
		}
		
		return true;
	}
		
	
	/*Try To Stop Current CrawlServer~~
	 * 1:TellNodeToPause (don't get Task from MessageBus);
	 * 2:TaskMessage Bus Save To File
	 * 3:Save Current Status--CollentionNodes,MainSearch,KeyUser,NormalUser
	 * 3:
	 * 4:CrawlServerToStop;
	 * 5:finish;
	 * 
	 * */
	public boolean StopCrawlServer(){
		this.TellNodeToPause();
		//等待确认---miss
		CrawlerServerDumper crawlerDumper=new CrawlerServerDumper(this);
		if(crawlerDumper.TaskSaver("Output\\Facebook\\TaskDump.dat"))
			LogSys.crawlerServLogger.info("Success To Save Task To File");
		else{
			LogSys.crawlerServLogger.info("Fail To Save Task To File");
			return false;
		}
		/*保存当前状态
		 * Status;ServerBean;
		*/
		if(crawlerDumper.OtherStatusSaver("Output\\Facebook\\OtherStatusDump.dat"))
			LogSys.crawlerServLogger.info("Success To Save Status To File");
		else{
			LogSys.crawlerServLogger.info("Fail To Save Status To File");
			return false;
		}
		return true;	
		
	}
	//restart from file
	
	public boolean RestartCrawlServer(){
		LogSys.crawlerServLogger.info("采集器开始恢复");			
		LogSys.crawlerServLogger.info("采集器恢复完成开始运行");
		this.TellNodeToPause();
		CrawlerServerDumper crawlerDumper=new CrawlerServerDumper(this);
		if(crawlerDumper.TaskResume("Output\\Facebook\\TaskDump.dat"))
			LogSys.crawlerServLogger.info("Success To Resume Task From File To CrawlServer");
		else{
			LogSys.crawlerServLogger.error("Fail To Resume Task From File To CrawlServer");
			return false;
		}
		if(crawlerDumper.OtherStatusResumer("Output\\Facebook\\OtherStatusDump.dat"))
			LogSys.crawlerServLogger.info("Success To Resume Status From File To CrawlServer");		
		else{
			LogSys.crawlerServLogger.error("Fail To Resume Status From File To CrawlServer");
			return false;
		}
		this.CollectionNodes();
		this.TellNodeToResume();
		switch(this.currentstep){
			case init:
			case searchStart:
				KeyWordSearch(true);
			case searchEnd:
			case keyuserCaijiStart:
				CrawlerServerKeyUserSearch(true);
			case keyuserCaijiEnd:
			case normalCaijiStart:
				CrawlerServerNorUserSearch(this.deepth,true);
			case normalCaijiEnd:
			default:
				System.err.println("没有发现可以恢复的起点，当前状态是"+currentstep);

		}
		LogSys.crawlerServLogger.info("恢复采集结束All is Finish");
		return true;
	}
	
	
	protected void SleepWithCount(int count){
		try {
			Thread.sleep(count);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	protected void CollectionNodes(){
		LogSys.crawlerServLogger.info("搜索节点15S");
		SleepWithCount(15000);		
		LogSys.crawlerServLogger.info("当前状态"+nodeManager.currentstep());
		LogSys.crawlerServLogger.info("节点个数"+nodeManager.nodecount);	
	}
	protected void KeyWordSearch(boolean isResume){
		currentstep=ServerStep.searchStart;
		LogSys.crawlerServLogger.info("关键词搜索开始");			
		//非常重要
		if(!isResume)
			KeyWordsSearch(keySearchCount);
		while(currentstep!=ServerStep.searchEnd){
			CollectNodesStatus();
			SleepWithCount(60000);				
			if(nodeManager.canNextStepByTaskBusName(MessageBusNames.KeyWordAndTopicTask)){
				currentstep=ServerStep.searchEnd;
				nodeManager.show();
			}else{
				LogSys.crawlerServLogger.debug("不能进入下一个采集状态");
				
			}
		}
		LogSys.crawlerServLogger.info("关键词搜索结束");
	}
	protected void CrawlerServerKeyUserSearch(boolean isResume){
		
		currentstep=ServerStep.keyuserCaijiStart;
		LogSys.crawlerServLogger.info("关键用户采集开始");		
		if(!isResume)
			ChuizhiCaiji();
		while(currentstep!=ServerStep.keyuserCaijiEnd){
			CollectNodesStatus();
			SleepWithCount(20000);
			if(nodeManager.canNextStepByTaskBusName(MessageBusNames.KeyUserTask))
				currentstep=ServerStep.keyuserCaijiEnd;
		}
		LogSys.crawlerServLogger.info("关键用户采集结束");
	}
	protected int CrawlerServerNorUserSearch(int deepth,boolean isResume){
		this.setCrawlServerDeepth(deepth);//设置CrawlServer的深度和递归的深度保持一致
		currentstep=ServerStep.normalCaijiStart;
		LogSys.crawlerServLogger.info("普通用户采集开始---深度："+deepth+"-----");		
		if(!isResume)
			NormalCaiji();
		
		while(currentstep!=ServerStep.normalCaijiEnd){
			CollectNodesStatus();
			SleepWithCount(20000);
			if(nodeManager.canNextStepByTaskBusName(MessageBusNames.Task))
				currentstep=ServerStep.normalCaijiEnd;
		}
		LogSys.crawlerServLogger.info("普通用户采集结束---深度："+deepth+"-----");
		if(deepth==1){
			return 0;
		}else{
			return CrawlerServerNorUserSearch(deepth-1,false);
		}
	}
	
		
	//采集种子用户信息的任务
	public void ChuizhiCaiji(){							
		String filelocation=basepath+"/UsefulFile/keyusers.txt";
		//添加从数据库中读取关键用户这一步
		sb.InitChuizhi(filelocation, this,isFirstChuiZhi);
		SleepWithCount(5000);
		sendNewStep(NodeStep.keyuser_start);
		
		isFirstChuiZhi=false;
		
		
	}
	public void KeyWordsSearch(int max){
		String filelocation=basepath+"/UsefulFile/keywords.txt";
		sb.InitSearch(filelocation,max,this);
		SleepWithCount(5000);
		sendNewStep(NodeStep.search_start);
	
		
			
	}
	public void NormalCaiji(){
		LogSys.crawlerServLogger.info("【Server】-----------开始并行采集-----------------");			
		//准备并行采集的种子信息
		int count=sb.InitBingxing(this,deepth);
		LogSys.crawlerServLogger.info("【Server】总共新加并行搜索任务数"+3*count+"个");
		SleepWithCount(5000);
		sendNewStep(NodeStep.normaluser_start);
		
		
	}
	public boolean addTask(Task task){
		task.setMainType(MainType.Normal);
		int taskTrackerID=tracker.AddTask(task);
		task.setTaskTrackID(taskTrackerID);
		taskSender.Send(task.TaskTOString());
		return true;
	}
	public boolean addUrgentTask(Task task){
		task.setMainType(MainType.Urgent);
		System.err.println("AddUrgentTaskAdded");
		int taskTrackerID=tracker.AddTask(task);
		task.setTaskTrackID(taskTrackerID);//设置TaskTrackID;
		urgentTaskSender.Send(task.TaskTOString());
		return true;
	}
	public boolean addKeyWord(Task task){
		task.setMainType(MainType.KeyWord);
		int taskTrackerID=tracker.AddTask(task);
		task.setTaskTrackID(taskTrackerID);
		keyWordAndTopicTaskSender.Send(task.TaskTOString());
		return true;
	}
	public boolean addTopic(Task task){
		task.setMainType(MainType.Topic);
		int taskTrackerID=tracker.AddTask(task);
		task.setTaskTrackID(taskTrackerID);
		keyWordAndTopicTaskSender.Send(task.TaskTOString());
		return true;
	}
	public boolean addKeyUserTask(Task task){
		task.setMainType(MainType.KeyUser);
		int taskTrackerID=tracker.AddTask(task);
		task.setTaskTrackID(taskTrackerID);
		keyUserTaskSender.Send(task.TaskTOString());
		return true;
	}
	
	
	//添加普通用户
	public void addNormalUser(NormalUser nu){		
		sb.addNormalUser(nu,deepth);		
	}
	
	public void addKeyUser(NormalUser nu){
		sb.addKeyUser(nu);
	}
	public int showNormalUserSize(){
		return sb.normalUserList.size();
	}
	
	
	//所有节点停止工作
	public boolean stopTaskOnAllNode(){
		LogSys.crawlerServLogger.info("发送停止工作指令");
		controlSender.Send("STOPALL");
		return true;
	}

	public boolean startTaskOnAllNode(){
		LogSys.crawlerServLogger.info("通知节点启动工作");
		controlSender.Send("STARTALL");
		return true;
	}
	
	public boolean sendNewStep(NodeStep step){
		LogSys.crawlerServLogger.info("通知所有节点进入新的工作状态"+step);
		controlSender.Send("NEWSTEP "+step.toString());
		return true;
	}
	private boolean CollectNodesStatus(){
		controlSender.Send("REPORTSTATUS");		
		return true;
	}
	
	//发送至node 暂停采集
	public void TellNodeToPause(){
		LogSys.crawlerServLogger.info("CrawlerServer Send Pause");
		controlSender.Send("PAUSE");
	}
	//发送至node 恢复采集
	public void TellNodeToResume(){
		controlSender.Send("RESUME");		
	}
	
	/*
	 * 1：清除队列中所有的消息
	 */
	public void ResetMessageBus(){
		MessageBusCleanner.Clean();

	}

	public void getNewNode(String nodeName) {
		PlatFormMain.log.info("【控制台】接受到新的节点");
		nodeManager.addNode(nodeName);
	}
	public void oneNodeFinishWork(String nodeName,NodeStep currentstep){
		nodeManager.onnodefinish(nodeName,currentstep);
		nodeManager.onnodefinish(nodeName, currentstep);
	}
	
	public void addNormalUserByID() {
		
		
	}
	public void StartReportTimer(){
		try{
			LogSys.crawlerServLogger.info("正在启动[增量]StartReporterTimer.....");
			Timer timer=new Timer();
			timer.schedule(new TimerTask(){
				public void run() {
					try {
						crawlReporter.doReportIncrementByDataBase(ServerReportData);
						ResetServerReportData();//汇报完毕进行清空操作;
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						System.err.println("汇报出现错误重新汇报");
						e.printStackTrace();
					}
				}
			}, 1000, 10000);//每10s向汇报服务器进行汇报
			LogSys.crawlerServLogger.info("启动成功[增量]StartReporterTimer");
			
			LogSys.crawlerServLogger.info("正在启动[全量]StartReporterTimer.....");
			Timer timerQuanliang=new Timer();
			timerQuanliang.schedule(
					new ReportTotalScheduler(this), 
					20000, //每次汇报的前20秒启动
					600000);//平均每5分钟汇报一次全量数据
			LogSys.crawlerServLogger.info("启动成功[全量]StartReporterTimer");
		}catch(Exception ex){
			ex.printStackTrace();
			LogSys.nodeLogger.error("启动定时器失败");
		}
	}
	
	//收到HeartReport
	public void onReceiveReportFromNode(ReportData rpdata){
		//服务器知识读取对应的数据并添加到
		//System.out.printf("Message:%d,User:%d,UserRel:%d\n",rpdata.message_increment,rpdata.user_increment,rpdata.user_rel_increment);
		synchronized (reportlock) {
			ServerReportData.add(rpdata);//服务器将数据进行累加；
		}
		
		
//		if(NodeReportData.containsKey(rpdata.NodeName)){
//			ReportData tmprpdata=NodeReportData.get(rpdata.NodeName);
//			tmprpdata.add(rpdata);			
//		}else{
//			NodeReportData.put(rpdata.NodeName, rpdata);
//			LogSys.crawlerServLogger.debug("Crawler 收到信息来自新节点("+rpdata.NodeName+")");
//		}
		
	}
	private void ResetServerReportData(){
		synchronized (reportlock) {
			ServerReportData=new ReportData();
		}
	}
	private void StartSchedulTimer(){
		Timer time=new Timer();
		schedule=new CrawlServerScheduler(this);
		time.schedule(schedule, 10000, 60000);
		System.out.println("启动CrawlerSchedul成功");
	}
	private void StartKeyUserSchedulTimer(){
		Timer time=new Timer();
		KeyUserScheduler kuser=new KeyUserScheduler(this);
		long minute=60000;
		long hour=60*minute;
		long day=hour*24;
		time.schedule(kuser, 10000, day);
		System.out.println("启动KeyUserSchedul成功");
	}
	//启动KeyWord调度
	private void StartKeyWordSchedulTimer(){
		Timer time=new Timer();
		KeyWordsScheduler keyword=new KeyWordsScheduler(this);
		long minute=60000;
		long hour=60*minute;
		long day=hour*12;
		time.schedule(keyword, 10*60000, day);//每天干完keyUser 10min后干Keywords
		System.out.println("启动KeyUserSchedul成功");
	}
	//InputTask 调度器
	private void StartPlantSchedulerTimer(){
		Timer time=new Timer();
		KeyWordsScheduler keyword=new KeyWordsScheduler(this);
		long minute=60000;
		long hour=60*minute;
		long day=hour*12;
		time.schedule(keyword, 10*60000, 4*hour);//每天干完keyUser10min后感Keywords
		System.out.println("启动PlantScheduler成功");
	}
	
	public void ShowAndLog(String msg){
		LogSys.crawlerServLogger.info("【CRAWLERSERVER】"+msg);
		
	}
	private void setCrawlServerDeepth(int dep){
		this.deepth=dep;
	}

}
