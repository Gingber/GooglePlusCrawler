package com.iie.googleplus.MessageBus;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Connection;

import com.iie.googleplus.CrawlerNode.Node;
import com.iie.googleplus.CrawlerNode.TaskReceiver;

public class ObjReceiver extends TaskReceiver {
	public ObjReceiver(Connection con, String queue, Node _node) {
		super(con, queue, _node);
		// TODO Auto-generated constructor stub
	}
	
	public Message TryToReceive(){
		try {
			Message msg = consumer.receive();
			return msg;
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}

}
