/**
 * 
 */
package com.iie.googleplus.analyzer;

import java.io.IOException;

import com.iie.googleplus.Dboperator.MongoOperator;
import com.mongodb.BasicDBObject;
import com.mongodb.Bytes;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * @author Gingber
 *
 */
public class Activity {
	
	public static void getActivity(DB mongodb) throws IOException {

		BasicDBObject field = new BasicDBObject();
		field.put("object", true);
		DBCursor cursor = mongodb.getCollection("activity").find(new BasicDBObject(), field)
				  				.addOption(Bytes.QUERYOPTION_NOTIMEOUT);
		try { 
			long recordTotal = mongodb.getCollection("activity").count();
			int plusoneNum = 0, replyNum = 0, reshareNum = 0; 
			int plusoneSum = 0, replySum = 0, reshareSum = 0; 
			while (cursor.hasNext()) {
				BasicDBObject bdbObj = (BasicDBObject) cursor.next().get("object");
				if(bdbObj != null){
					BasicDBObject plusonersObj = ((BasicDBObject)bdbObj.get("plusoners"));
					if(plusonersObj != null) {
						Integer plusoneTotal = (Integer)plusonersObj.get("totalItems");
						if(plusoneTotal > 0) {
							plusoneNum++;
							plusoneSum += plusoneTotal;
						}
					}
					
					BasicDBObject repliesObj = ((BasicDBObject)bdbObj.get("replies"));
					if(repliesObj != null) {
						Integer repliesTotal = (Integer)repliesObj.get("totalItems");
						if(repliesTotal > 0) {
							replyNum++;
							replySum += repliesTotal;
						}
					}
					
					BasicDBObject resharesObj = ((BasicDBObject)bdbObj.get("resharers"));
					if(resharesObj != null) {
						Integer resharesTotal = (Integer)resharesObj.get("totalItems");
						if(resharesTotal > 0) {
							reshareNum++;
							reshareSum += resharesTotal;
						}
					}
					
					
				}
			}
			
			System.out.println(recordTotal);
			System.out.println(plusoneNum + "\t" + replyNum+ "\t" + reshareNum);
			System.out.println(plusoneSum + "\t" + replySum+ "\t" + reshareSum);
			
		} finally {	  
			cursor.close();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			DB mongodb = MongoOperator.LinkMongodb();
			
			getActivity(mongodb);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
