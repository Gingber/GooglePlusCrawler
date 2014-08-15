package com.iie.googleplus.CrawlerNode;

import com.iie.googleplus.Crawler.AjaxMainCrawler;
import com.iie.googleplus.tool.DbOperation;


public class AjaxNode extends Node {

	public AjaxNode(String name, DbOperation dbOper) {
		super(name, dbOper);

	}
	
	protected void startMainSearch(){
		AjaxMainCrawler ms=new AjaxMainCrawler("AjaxMainSearch-"+this.NodeName+"",this);
		Thread mainThread=new Thread(ms);
		mainThread.setName("AjaxMainSearchThread-"+this.NodeName+"");
		mainThread.start();			
	}
}
