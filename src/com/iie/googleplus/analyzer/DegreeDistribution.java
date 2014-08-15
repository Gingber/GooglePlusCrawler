/**
 * 
 */
package com.iie.googleplus.analyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.iie.googleplus.Dboperator.MongoOperator;
import com.iie.util.TxtWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.Bytes;
import com.mongodb.DB;
import com.mongodb.DBCursor;

/**
 * @author Gingber
 *
 */
public class DegreeDistribution {
	
	public static void getFollowerCount(DB mongodb) throws IOException {

		List<Integer> userIds = new ArrayList<Integer>();
		
		BasicDBObject field = new BasicDBObject();
		field.put("circledByCount", true);
		DBCursor cursor = mongodb.getCollection("userprofile").find(new BasicDBObject(), field)
				  				.addOption(Bytes.QUERYOPTION_NOTIMEOUT);
		try { 
			int num = 0; 
			while (cursor.hasNext()) {
				BasicDBObject bdbObj = (BasicDBObject) cursor.next();
				if(bdbObj.get("circledByCount") != null){
					int followerNum = (Integer)bdbObj.get("circledByCount");  	
					userIds.add(followerNum);
					System.out.println((++num) + "\t" + followerNum);
					TxtWriter.saveToFile(""+followerNum, new File("D:/GooglePlusProgam/Matlab/degree.txt"), "utf-8");
				}
			}	
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
			
			getFollowerCount(mongodb);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
