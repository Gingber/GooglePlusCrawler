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
public class UserProfile {
	
	public static void getUserGender(DB mongodb) throws IOException {

		BasicDBObject field = new BasicDBObject();
		field.put("gender", true);
		DBCursor cursor = mongodb.getCollection("userprofile").find(new BasicDBObject(), field)
				  				.addOption(Bytes.QUERYOPTION_NOTIMEOUT);
		try { 
			long recordTotal = mongodb.getCollection("userprofile").count();
			int maleNum = 0, femaleNum = 0; 
			while (cursor.hasNext()) {
				BasicDBObject bdbObj = (BasicDBObject) cursor.next();
				if(bdbObj.get("gender") != null){
					String gender = bdbObj.get("gender").toString();  	
					if(gender.equals("male")) {
						maleNum++;
					} else if(gender.equals("female")) {
						femaleNum++;
					}
				}
			}
			
			System.out.println(recordTotal + "\t" + maleNum + "\t" + femaleNum);
			
		} finally {	  
			cursor.close();
		}
	}
	
	public static void isVerified(DB mongodb) throws IOException {

		long recordTotal = mongodb.getCollection("userprofile").count();
		
		BasicDBObject field = new BasicDBObject();
		field.put("verified", true);
		DBCursor cursor = mongodb.getCollection("userprofile").find(new BasicDBObject(), field)
				  				.addOption(Bytes.QUERYOPTION_NOTIMEOUT);
		try { 
			int verifiedNum = 0, unverifiedNum = 0; 
			while (cursor.hasNext()) {
				BasicDBObject bdbObj = (BasicDBObject) cursor.next();
				if(bdbObj.get("verified") != null){
					String isVerified = bdbObj.get("verified").toString();  	
					if(isVerified.equals("true")) {
						verifiedNum++;
					} else if(isVerified.equals("false")) {
						unverifiedNum++;
					}
				}
			}
			
			System.out.println(recordTotal + "\t" + verifiedNum + "\t" + unverifiedNum);
			
		} finally {	  
			cursor.close();
		}
	} 
	
	public static void countUserAttribute(DB mongodb) {

		long recordTotal = mongodb.getCollection("user_profile").count();
		
		BasicDBObject field = new BasicDBObject();
		//field.put("verified", true);
		DBCursor cursor = mongodb.getCollection("user_profile").find(new BasicDBObject())
				  				.addOption(Bytes.QUERYOPTION_NOTIMEOUT);
		try { 
			int aboutMeNum = 0, birthdayNum = 0, braggingRightsNum = 0, genderNum = 0, imageNum = 0;
			int occupationNum = 0, organizationsNum = 0, placesLivedNum = 0, relationshipStatusNum = 0, urlsNum = 0;
			while (cursor.hasNext()) {
				BasicDBObject bdbObj = (BasicDBObject) cursor.next();
				if(bdbObj.get("aboutMe") != null){
					aboutMeNum++;
				}
				
				if(bdbObj.get("birthday") != null){
					birthdayNum++;
				}
				
				if(bdbObj.get("braggingRights") != null){
					braggingRightsNum++;
				}
				
				if(bdbObj.get("gender") != null){
					genderNum++;
				}
				
				if(bdbObj.get("image") != null){
					imageNum++;
				}
				
				if(bdbObj.get("occupation") != null){
					occupationNum++;
				}
				
				if(bdbObj.get("organizations") != null){
					organizationsNum++;
				}
				
				if(bdbObj.get("placesLived") != null){
					placesLivedNum++;
				}
				
				if(bdbObj.get("relationshipStatus") != null){
					relationshipStatusNum++;
				}
				
				if(bdbObj.get("urls") != null){
					urlsNum++;
				}
			}
			
			System.out.println(recordTotal + "\t" + aboutMeNum + "\t" + birthdayNum + "\t" + braggingRightsNum
					+ "\t" + genderNum + "\t" + imageNum + "\t" + occupationNum + "\t" + organizationsNum
					+ "\t" + placesLivedNum + "\t" + relationshipStatusNum + "\t" + urlsNum);
			
			System.out.println(aboutMeNum*1.0/recordTotal);
			System.out.println(birthdayNum*1.0/recordTotal);
			System.out.println(braggingRightsNum*1.0/recordTotal);
			System.out.println(genderNum*1.0/recordTotal);
			System.out.println(imageNum*1.0/recordTotal);
			System.out.println(occupationNum*1.0/recordTotal);
			System.out.println(organizationsNum*1.0/recordTotal);
			System.out.println(placesLivedNum*1.0/recordTotal);
			System.out.println(relationshipStatusNum*1.0/recordTotal);
			System.out.println(urlsNum*1.0/recordTotal);
			
			
			
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
			
//			getUserGender(mongodb);
//			isVerified(mongodb);

			countUserAttribute(mongodb);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
