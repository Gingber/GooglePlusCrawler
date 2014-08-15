/**
 * 
 */
package com.iie.googleplus.analyzer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.iie.googleplus.Dboperator.MongoOperator;
import com.iie.util.TimeFormat;
import com.iie.util.TxtWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.Bytes;
import com.mongodb.DB;
import com.mongodb.DBCursor;

/**
 * @author Gingber
 *
 */
public class TimeDistribution {
	
	public static void getTimeDistribution(DB mongodb) {
		BasicDBObject field = new BasicDBObject();
		field.put("published", true);
		DBCursor cursor = mongodb.getCollection("activity").find(new BasicDBObject(), field).skip(0).limit(30000000)
				  				.addOption(Bytes.QUERYOPTION_NOTIMEOUT);
		
		try {
			
			long  recordNum = mongodb.getCollection("activity").count();
			int zero = 0, one  = 0, two =0, three= 0, four = 0, five = 0, six = 0, seven = 0;
			int eight = 0, nine = 0, ten = 0, eleven = 0, twelve = 0;
			int thirteen  = 0, fourteen =0, fifteen = 0, sixteen = 0, seventeen = 0; 
			int eighteen = 0, nineteen = 0, twenty = 0, twentyone = 0, twentytwo = 0;
			int twentythree = 0, twentyfour = 0;
			int twfour = 0;
			
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			while (cursor.hasNext()) {
				String published = cursor.next().get("published").toString();
				String convertTime = TimeFormat.change(published);
				String[] datehour = convertTime.split(" ");
//				System.out.println(datehour[0] + "\t" + datehour[1]);

				String[] needTime = datehour[1].split(":");
				int foo = Integer.parseInt(needTime[0]);
			
						
				if(0 <= foo && foo < 1) {
					zero++;
				} else if(1 <= foo && foo < 2) {
					one++;
				} else if(2 <= foo && foo < 3) {
					two++;
				} else if(3 <= foo && foo < 4) {
					three++;
				} else if(4 <= foo && foo < 5) {
					four++;
				} else if(5 <= foo && foo < 6) {
					five++;
				} else if(6 <= foo && foo < 7) {
					six++;
				} else if(7 <= foo && foo < 8) {
					seven++;
				} else if(8 <= foo && foo < 9) {
					eight++;
				} else if(9 <= foo && foo < 10) {
					nine++;
				} else if(10 <= foo && foo < 11) {
					ten++;
				} else if(11 <= foo && foo < 12) {
					eleven++;
				} else if(12 <= foo && foo < 13) {
					twelve++;
				} else if(13 <= foo && foo < 14) {
					thirteen++;
				} else if(14 <= foo && foo < 15) {
					fourteen++;
				} else if(15 <= foo && foo < 16) {
					fifteen++;
				} else if(16 <= foo && foo < 17) {
					sixteen++;
				} else if(17 <= foo && foo < 18) {
					seventeen++;
				} else if(18 <= foo && foo < 19) {
					eighteen++;
				} else if(19 <= foo && foo < 20) {
					nineteen++;
				} else if(20 <= foo && foo < 21) {
					twenty++;
				} else if(21 <= foo && foo < 22) {
					twentyone++;
				} else if(22 <= foo && foo < 23) {
					twentytwo++;
				} else if(23 <= foo && foo < 24) {
					twentythree++;
				} 
			}
			
			System.out.println(zero + "\t" + zero*1.0/recordNum);
			System.out.println(one + "\t" + one*1.0/recordNum);
			System.out.println(two + "\t" + two*1.0/recordNum);
			System.out.println(three + "\t" + three*1.0/recordNum);
			System.out.println(four + "\t" + four*1.0/recordNum);
			System.out.println(five + "\t" + five*1.0/recordNum);
			System.out.println(six + "\t" + six*1.0/recordNum);
			System.out.println(seven + "\t" + seven*1.0/recordNum);
			System.out.println(eight + "\t" + eight*1.0/recordNum);
			System.out.println(nine + "\t" + nine*1.0/recordNum);
			System.out.println(ten + "\t" + ten*1.0/recordNum);
			System.out.println(eleven + "\t" + eleven*1.0/recordNum);
			System.out.println(twelve + "\t" + twelve*1.0/recordNum);
			
			System.out.println(thirteen + "\t" + thirteen*1.0/recordNum);
			System.out.println(fourteen + "\t" + fourteen*1.0/recordNum);
			System.out.println(fifteen + "\t" + fifteen*1.0/recordNum);
			System.out.println(sixteen + "\t" + sixteen*1.0/recordNum);
			System.out.println(seventeen + "\t" + seventeen*1.0/recordNum);
			System.out.println(eighteen + "\t" + eighteen*1.0/recordNum);
			System.out.println(nineteen + "\t" + nineteen*1.0/recordNum);
			System.out.println(twenty + "\t" + twenty*1.0/recordNum);
			System.out.println(twentyone + "\t" + twentyone*1.0/recordNum);
			System.out.println(twentytwo + "\t" + twentytwo*1.0/recordNum);
			System.out.println(twentythree + "\t" + twentythree*1.0/recordNum);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			DB mongodb = MongoOperator.LinkMongodb();
			
			getTimeDistribution(mongodb);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
