package com.iie.googleplus.Platform;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import com.iie.googleplus.MessageBus.GetAceiveMqConnection;

public class MessageBusTest{

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		

	}
	public boolean doTest(){
		ActiveMQConnection connection=GetAceiveMqConnection.StaticGetConnection();
		try {
			connection.start();
			if(connection!=null){
				connection.stop();
				connection.close();
			}
		}catch (Exception ex){
			ex.printStackTrace();
			return false;
		}
		return true;
	}
	

}
