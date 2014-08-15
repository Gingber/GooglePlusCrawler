package com.iie.googleplus.MessageBusTest;

import com.iie.googleplus.CrawlerNode.runGPlusAjaxNodes;
import com.iie.googleplus.CrawlerServer.CrawlerServer;
import com.iie.googleplus.Platform.LogSys;


public class GPlusWEBControlClient extends ControlClient {
	public CrawlerServer server;
	public Thread serverThread;
	public GPlusWEBControlClient(ClientsID name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	@Override
	public boolean ToStart(){
		
		LogSys.clientLogger.info("����TwitterWeb�ɼ�ϵͳCrawlerServer");
		server=new CrawlerServer();
		serverThread=new Thread(server);
		serverThread.setName("TwitterWeBCrawlerServer");
		serverThread.start();
		LogSys.clientLogger.info("����TwitterWeb�ɼ�ϵͳCrawlerNodes�ڵ�");
		if(runGPlusAjaxNodes.run()){
			LogSys.clientLogger.info("����TwitterWeb�ɼ�ϵͳCrawlerNodes�ڵ�ɹ�");
		}else{
			LogSys.clientLogger.info("����TwitterWeb�ɼ�ϵͳCrawlerNodes�ڵ�ʧ��");
			return false;
		}
		return true;
	}
	
	@Override
	public boolean ToPause() {
		// TODO Auto-generated method stub
		return false;
	}
	

	@Override
	public boolean ToDisplay(){
		LogSys.clientLogger.info("TwitterWeb�յ�չʾTwitterNode ��Ϣ");
		LogSys.clientLogger.info("TwitterWeb Nodes ��Ϣ����--------------");
		if(server!=null){
			server.nodeManager.show();
			LogSys.clientLogger.info("TwitterWeb TaskSize{"+server.nodeManager.getTaskSizeCount()+"}");			
		}
		LogSys.clientLogger.info("---------------------------------------");
		return true;
	}
	@Override
	public boolean ToResume() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean ToStop() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean ToRestart() {
		// TODO Auto-generated method stub
		return false;
	}


}
