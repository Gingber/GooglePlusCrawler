package com.iie.googleplus.Report;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import com.iie.googleplus.CrawlerServer.CrawlerServer;
import com.iie.googleplus.MessageBus.Receiver;

//此类运行在采集服务器端，接收采集节点的汇报信息。
public class NodeReporterReceiver extends Receiver {

	public NodeReporterReceiver(javax.jms.Connection connetion,String queue, CrawlerServer server, boolean isTopic) {
		super(connetion,false,queue, server, false,null);
	}
	public void onMessage(Message resmsg) {
		try {
			if(resmsg instanceof ObjectMessage){
			ReportData rpdata=(ReportData)((ObjectMessage)resmsg).getObject();
			server.onReceiveReportFromNode(rpdata);
			}
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	

	
}
