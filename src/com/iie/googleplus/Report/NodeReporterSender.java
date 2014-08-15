package com.iie.googleplus.Report;

import com.iie.googleplus.MessageBus.Sender;


public class NodeReporterSender extends Sender {

	public NodeReporterSender(javax.jms.Connection connection,String queue, boolean isTopic) {
		super(connection,queue, isTopic);
	}

}
