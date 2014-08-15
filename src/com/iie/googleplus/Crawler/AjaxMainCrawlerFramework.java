/**
 * 
 */
package com.iie.googleplus.Crawler;

import org.apache.http.impl.client.DefaultHttpClient;

import com.google.api.services.plus.Plus;
import com.iie.googleplus.CrawlerNode.AjaxNode;
import com.iie.googleplus.Dboperator.MySQLOperator;

/**
 * @author Gingber
 *
 */
public abstract class AjaxMainCrawlerFramework implements Runnable {
	
	protected String name;
	protected MySQLOperator DBOp;
	
	protected Plus plus;
	protected DefaultHttpClient httpclient;
	protected AjaxNode node;
	public abstract void run();
	public abstract void doWork();

}
