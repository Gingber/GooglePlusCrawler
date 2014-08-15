/**
 * 
 */
package com.iie.googleplus.Dboperator;

import com.google.api.services.plus.Plus;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

/**
 * @author IIEIR
 *
 */
public class MongoOperator {

	public static DB LinkMongodb() throws Exception {  
        
	      /*  
	       * Link Mongodb   
	       * build a data named FourS2  
	       * build a collection named Foursquare  
	       *    
	       */  
		  Mongo mongo = new Mongo("192.168.120.152", 27017);  
	      DB db = mongo.getDB("GooglePlus");
	      if(db != null) {
	    	  System.out.println("Success Link Mongodb!");
		      return db;
	      } else {
	    	  System.out.println("Failure Link Mongodb!");
		      return null;
	      }
	      
	  }
}
