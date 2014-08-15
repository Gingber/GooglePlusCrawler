package com.iie.googleplus.CrawlerNode;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import com.iie.googleplus.CrawlerMessage.ControlMessage;
import com.iie.googleplus.MessageBus.Sender;


public class ControlSender extends Sender {

	public ControlSender(javax.jms.Connection connection,String queue, boolean isTopic) {
		super(connection,queue, isTopic);
		// TODO Auto-generated constructor stub
	}
	public boolean Send(ControlMessage conMsg){
		if( (session!=null)&&(producer!=null)){			
			ObjectMessage message;
			try {				
				message = session.createObjectMessage(conMsg);			
				producer.send(message);				
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			//ͨ����Ϣ�����߷�����Ϣ
		}else{
			System.out.println("����������ʧ��");
			return false;
		}

		return true;
		
	}
}
