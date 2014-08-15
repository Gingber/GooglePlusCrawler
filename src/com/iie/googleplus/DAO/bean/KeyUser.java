package com.iie.googleplus.DAO.bean;

public class KeyUser {
	public KeyUser(String userID, int crawlCount, int weight) {
		super();
		UserID = userID;
		CrawlCount = crawlCount;
		this.weight = weight;
	}
	public String UserID;
	public int CrawlCount;
	public int weight;

}
