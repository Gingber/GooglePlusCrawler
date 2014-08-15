package com.iie.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeFormat {
	public static String change(String t){
		
		SimpleDateFormat formatter, FORMATTER;
    	formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    	String oldDate = t;
    	Date dateT;
		try {
			dateT = formatter.parse(oldDate.substring(0, 24));
			FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	    	System.out.println("OldDate-->"+oldDate);
	    	//System.out.println("NewDate-->"+FORMATTER.format(dateT));
	    	return FORMATTER.format(dateT).toString();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		return null;
	}

}
