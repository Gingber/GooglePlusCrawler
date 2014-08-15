package com.iie.googleplus.DatabaseBean;

import com.iie.googleplus.CrawlerServer.InputStatus;
import com.iie.googleplus.CrawlerServer.InputType;

public class InputTaskBean {
	public int ID;
	public String TaskName,TaskParameter,TaskParameter2;
	public InputType InputType;
	public int TaskWeight,MessageRet,UserRet,UserRelRet;
	public InputStatus Status;
	public boolean ResultFlag;
}
