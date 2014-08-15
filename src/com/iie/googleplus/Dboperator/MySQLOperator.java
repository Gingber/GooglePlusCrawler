/**
 * 
 */
package com.iie.googleplus.Dboperator;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.http.cookie.Cookie;

import com.google.api.services.plus.Plus.People;
import com.google.api.services.plus.model.Activity;
import com.google.api.services.plus.model.Comment;
import com.google.api.services.plus.model.Person;
import com.google.api.services.plus.model.Person.Organizations;
import com.iie.googleplus.Platform.LogSys;
import com.iie.googleplus.analyzer.beans.UserProfile;
import com.iie.googleplus.analyzer.beans.UserRelationship;
import com.iie.googleplus.task.beans.Task;
import com.iie.googleplus.task.beans.Task.TaskType;
import com.iie.googleplus.tool.BasePath;
import com.iie.googleplus.tool.ReadTxtFile;


/**
 * @author IIEIR
 *
 */
public class MySQLOperator {
	
	private static Connection conn  = null;
	
	private static String ip = "127.0.0.1";
	private static String driver = "com.mysql.jdbc.Driver";
	private static String user = "";
	private static String password = "";
	private static String databaseName = "http_twitter";
	private static String encode = "utf-8";
	private Connection connection = null;
	public static int connectionCount;
	
	public MySQLOperator() {
		String base = BasePath.getBase();
		ReadTxtFile rxf = new ReadTxtFile(base + "/config/clientproperties.ini");
		Vector<String> vector = rxf.read();
		for (String t : vector) {
			if(t.startsWith("http.dbaddressIP")){
				String res = t.substring(t.indexOf('=') + 1);
				MySQLOperator.ip = res;
			}
			
			if (t.startsWith("http.dbusername")) {
				String res = t.substring(t.indexOf('=') + 1);
				MySQLOperator.user = res;
			} else if (t.startsWith("http.dbpassword")) {
				String res = t.substring(t.indexOf('=') + 1);
				MySQLOperator.password = res;
			} else if (t.startsWith("http.databasename")) {
				String res = t.substring(t.indexOf('=') + 1);
				MySQLOperator.databaseName = res;
			}
		}
	}

	public Connection getConnection() {
		
		try {
			if(connection != null && !connection.isClosed()){
				return connection;
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://" + ip
					+ ":3306/" + databaseName
					+ "?useUnicode=true&continueBatchOnError=true&characterEncoding=" + encode, user,
					password);
			connection.setAutoCommit(false);
		} catch (Exception e) {
			System.out.println("Error loading Mysql Driver!");
			e.printStackTrace();
		}
		LogSys.nodeLogger.debug("Success to connect to SQLServer [IP:" + ip + "] " + " [DBName:" + databaseName + "]");
		
		return connection;
	}
	
	public List<Task> fetchTask(String tblName) throws SQLException {
		 
		List<Task> tasklist = new ArrayList<Task>();
		PreparedStatement pstmt = null;
		Connection conn = this.getConnection();	
		try {
			String sql = "select taskStr, taskType from " + tblName + " where status = 'Created'";
			pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			
			
			while (rs.next()) {
				String taskName = rs.getString("taskStr");
				String ownType = rs.getString("taskType");
				
				Task task = new Task();
				task.setTargetString(taskName);
				if(ownType.equals("about")) {
					task.setOwnType(TaskType.About);
				} else if(ownType.equals("timeline")) {
					task.setOwnType(TaskType.Timeline);
				} else if(ownType.equals("follower")) {
					task.setOwnType(TaskType.Follower);
				} else if(ownType.equals("followee")) {
					task.setOwnType(TaskType.Followee);
				} else if(ownType.equals("search")) {
					task.setOwnType(TaskType.Search);
				}
				tasklist.add(task);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			
			if (conn != null) {
				conn.close();
			}
		}
		
		return tasklist;
	}
	
	public List<String> fetchKeyUser(String tblName) throws SQLException {
		 
		List<String> userIds = new ArrayList<String>();
		PreparedStatement pstmt = null;
		Connection conn = this.getConnection();	
		try {
			String sql = "select UserID from " + tblName;
			pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				String userId = rs.getString("user_id");
				if(!userIds.contains(userId)) {
					userIds.add(userId);
				}
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			
			if (conn != null) {
				conn.close();
			}
		}
		
		return userIds;
	}
	
	public List<String> fetchKeyWord(String tblName) throws SQLException {
		 
		List<String> keywords = new ArrayList<String>();
		PreparedStatement pstmt = null;
		Connection conn = this.getConnection();	
		try {
			String sql = "select WordStr from " + tblName;
			pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				String keyword = rs.getString("word");
				if(!keywords.contains(keyword)) {
					keywords.add(keyword);
				}
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			
			if (conn != null) {
				conn.close();
			}
		}
		
		return keywords;
	}
	
	public ResultSet getCookie() throws SQLException {
		 
		PreparedStatement pstmt = null;
		Connection conn = this.getConnection();	
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement("select cookie, user_name from account where `status` = 'using' and health = 1");
			rs = pstmt.executeQuery();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} 

		return rs;
	}
	
	public List<String[]> getAccount() throws SQLException{
		List<String[]> accounts = new ArrayList<String[]>();
		PreparedStatement pstmt = null;
		Connection conn = this.getConnection();	
		try {
			pstmt = conn.prepareStatement("select user_name, password from account where health=1");
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()){
				String[] t=new String[2];
				t[0]=rs.getString(1);
				t[1]=rs.getString(2);
				accounts.add(t);
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) {
					pstmt.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		}
		return accounts;
	}
	
	public boolean SaveCookieToDB(String username, String cookie) throws SQLException{
		PreparedStatement pstmt = null;
		Connection conn = this.getConnection();	
		try {
			pstmt = conn.prepareStatement("update account set status='using', health=true, cookie=? where username=?");
			pstmt.setString(1, cookie);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			
			if (pstmt != null) {
				pstmt.close();
			}
			
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) {
					pstmt.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		}
		
		return false;
	}
	
	public boolean MaskAsNotAvailable(String username){
		PreparedStatement pstmt = null;
		Connection conn = this.getConnection();	
		try {
			pstmt = conn.prepareStatement("update account set status='frozen', health=false where username=?");
			pstmt.setString(1, username);
			pstmt.executeUpdate();
			pstmt.close();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) {
					pstmt.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		}
		return false;
	}
	
	
	public void batchIntoTask(String tblName, List<Task> records) throws Exception {
 
		PreparedStatement pstmt = null;
		Connection conn = this.getConnection();	
		try {
	
			String sql = "insert into " + tblName + "(taskStr, taskType, CreateTime, Status) value(?,?,?,?)";
			pstmt = conn.prepareStatement(sql);
			conn.setAutoCommit(false);
 
			for(int i = 0; i < records.size(); i++) {
				pstmt.setString(1, records.get(i).getTargetString());
				pstmt.setString(2, records.get(i).getOwnType().toString());
				pstmt.setTimestamp(3, getCurrentTimeStamp());
				pstmt.setString(4, "Created");
				pstmt.addBatch();
				if(i%1000 == 0) {
					int[] updateCounts = pstmt.executeBatch();
					checkUpdateCounts(updateCounts); 
					conn.commit();
				}
			}
			int[] updateCounts = pstmt.executeBatch();
			checkUpdateCounts(updateCounts); 
			conn.commit();

		} catch (BatchUpdateException e) {
			try {
				int[] updateCounts = e.getUpdateCounts();
				checkUpdateCounts(updateCounts);
				conn.commit();
			} catch (Exception ex) {
		        ex.printStackTrace();  
			} 
		} catch (Exception e) {
		      e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) {
					pstmt.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		}
	}
	
	public void batchInsertMessage(List<Activity> activities, String tableName) throws SQLException {
		PreparedStatement pstmt = null;
		Connection conn = this.getConnection();
		try {
			String sql = "insert into " + tableName + "(message_id, user_id, user_name, content, "
					+ "plusoners_num, replies_num, reshares_num, verb, url, create_time, crawl_time) "
					+ "values(?,?,?,?,?,?,?,?,?,?,?)";
			
			pstmt = conn.prepareStatement(sql);
			conn.setAutoCommit(false);
 
			int num = 0;
			for (Activity activity : activities) {
				pstmt.setString(1, activity.getId());
				pstmt.setString(2, activity.getActor().getId());
				pstmt.setString(3, activity.getActor().getDisplayName());
				pstmt.setString(4, activity.getObject().getContent());
				pstmt.setLong(5, activity.getObject().getPlusoners().getTotalItems());
				pstmt.setLong(6, activity.getObject().getReplies().getTotalItems());
				pstmt.setLong(7, activity.getObject().getResharers().getTotalItems());				
				pstmt.setString(8, activity.getVerb());
				pstmt.setString(9, activity.getUrl());
				String createTime = convertTime(activity.getPublished().toString());
				pstmt.setString(10, createTime);
				pstmt.setTimestamp(11, getCurrentTimeStamp());
				pstmt.addBatch();
				if((++num)%100 == 0) {
					int[] updateCounts = pstmt.executeBatch();
					checkUpdateCounts(updateCounts); 
					conn.commit();
				}
			}
			int[] updateCounts = pstmt.executeBatch();
			checkUpdateCounts(updateCounts); 
			conn.commit();
			
		} catch (BatchUpdateException e) {
			try {
				int[] updateCounts = e.getUpdateCounts();
				checkUpdateCounts(updateCounts);
				conn.commit();
			} catch (Exception ex) {
		        ex.printStackTrace();  
			} 
		} catch (Exception e) {
		      e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) {
					pstmt.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}
	
	public void insertIntoUserProfile(UserProfile person, String tableName) throws SQLException {
		PreparedStatement pstmt = null;
		Connection conn = this.getConnection();	
		try {
			String sql = "insert ignore into " + tableName + "(user_id, user_name, gender, birthday, follower, friend, "
					+ "current_loc, image_url, url, about_me, tag_line, organizations, crawl_time) "
					+ "value(?,?,?,?,?,?,?,?,?,?,?,?,?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, person.getUserId());
			pstmt.setString(2, person.getUserName());
			pstmt.setString(3, person.getGender());
			pstmt.setString(4, person.getBirthday());
			pstmt.setInt(5, person.getFollowers());
			pstmt.setInt(6, person.getFriends());
			pstmt.setString(7, person.getCurrentLoc());
			pstmt.setString(8, person.getImageUrl());
			pstmt.setString(9, person.getUrl());
			pstmt.setString(10, person.getAboutMe());
			pstmt.setString(11, person.getTagLine());
			if(person.getOrganizations() != null) {
				pstmt.setString(12, person.getOrganizations().toString());
			} else {
				pstmt.setString(12, null);
			}
			pstmt.setTimestamp(13, getCurrentTimeStamp());
			
			pstmt.executeUpdate();
			conn.commit();	
			
		} catch (BatchUpdateException e) {
			try {
				int[] updateCounts = e.getUpdateCounts();
				checkUpdateCounts(updateCounts);
				conn.commit();
			} catch (Exception ex) {
		        ex.printStackTrace();  
			} 
		} catch (Exception e) {
		      e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) {
					pstmt.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	public void insertIntoUserProfile(Person person, String tableName) throws SQLException {
		PreparedStatement pstmt = null;
		Connection conn = this.getConnection();	
		try {
			String sql = "insert ignore into " + tableName + "(user_id, user_name, gender, birthday, follower, friend, "
					+ "current_loc, image_url, url, about_me, tag_line, organizations, crawl_time) "
					+ "value(?,?,?,?,?,?,?,?,?,?,?,?,?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, person.getId());
			pstmt.setString(2, person.getDisplayName());
			pstmt.setString(3, person.getGender());
			pstmt.setString(4, person.getBirthday());
			pstmt.setInt(5, person.getCircledByCount());
			pstmt.setInt(6, 0);
			pstmt.setString(7, person.getCurrentLocation());
			pstmt.setString(8, person.getImage().getUrl());
			pstmt.setString(9, person.getUrl());
			pstmt.setString(10, person.getAboutMe());
			pstmt.setString(11, person.getTagline());
			if(person.getOrganizations() != null) {
				pstmt.setString(12, person.getOrganizations().toString());
			} else {
				pstmt.setString(12, null);
			}
			pstmt.setTimestamp(13, getCurrentTimeStamp());
			
			pstmt.executeUpdate();
			conn.commit();	
			
		} catch (BatchUpdateException e) {
			try {
				int[] updateCounts = e.getUpdateCounts();
				checkUpdateCounts(updateCounts);
				conn.commit();
			} catch (Exception ex) {
		        ex.printStackTrace();  
			} 
		} catch (Exception e) {
		      e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) {
					pstmt.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public boolean batchIntoUserRel(UserRelationship[] rels,String targetTableName) {
		PreparedStatement pstmt = null;
		Connection conn=this.getConnection();
		try {
			pstmt = conn.prepareStatement("insert into "+targetTableName+"(channel_id, user_id_A, user_id_B, link_type, crawl_time) values(?,?,?,?,?)");
			for(int i = 0; i < rels.length; i++){
				UserRelationship userel = rels[i];
				pstmt.setInt(1, 8);
				pstmt.setString(2, userel.getUser_A());
				pstmt.setString(3, userel.getUser_B());
				pstmt.setString(4, userel.getLinkType());
				pstmt.setTimestamp(5, getCurrentTimeStamp());
				pstmt.addBatch();
			}
			
			int[] updateCounts = pstmt.executeBatch();
			checkUpdateCounts(updateCounts); 
			conn.commit();
		}catch(BatchUpdateException e){
			try {
				int[] updateCounts = e.getUpdateCounts();
				checkUpdateCounts(updateCounts);
				conn.commit();
			} catch (Exception ex) {
		        ex.printStackTrace();  
			} 			
		}catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if(pstmt != null) {
					pstmt.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
		
	}
	
	
	public void batchInsertComment(List<Comment> comments, String tableName) throws SQLException {
		PreparedStatement pstmt = null;
		Connection conn = this.getConnection();
		try {	 
			String sql = "insert into " + tableName + "(comment_id, message_id, user_id, user_name, title, "
					+ "plusoners_num, verb, create_time, crawl_time) values(?,?,?,?,?,?,?,?,?)";	
			
			pstmt = conn.prepareStatement(sql);
			conn.setAutoCommit(false);
 
			int num = 0;
			for (Comment comment : comments) {
				pstmt.setString(1, comment.getId());
				pstmt.setString(2, comment.getInReplyTo().get(0).getId());
				pstmt.setString(3, comment.getActor().getId());
				pstmt.setString(4, comment.getActor().getDisplayName());
				pstmt.setString(5, comment.getObject().getContent());
				pstmt.setLong(6, comment.getPlusoners().getTotalItems());				
				pstmt.setString(7, comment.getVerb());
				String createTime = convertTime(comment.getPublished().toString());
				pstmt.setString(8, createTime);
				pstmt.setTimestamp(9, getCurrentTimeStamp());
				pstmt.addBatch();
				if((++num)%100 == 0) {
					int[] updateCounts = pstmt.executeBatch();
					checkUpdateCounts(updateCounts); 
					conn.commit();
				}
			}
			int[] updateCounts = pstmt.executeBatch();
			checkUpdateCounts(updateCounts); 
			conn.commit();
		} catch (BatchUpdateException e) {
			try {
				int[] updateCounts = e.getUpdateCounts();
				checkUpdateCounts(updateCounts);
				conn.commit();
			} catch (Exception ex) {
		        ex.printStackTrace();  
			} 
		} catch (Exception e) {
		      e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) {
					pstmt.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}
	
	
	@SuppressWarnings("resource")
	public static void updateRecordToTable(String tblName, String field) throws Exception {
		
		PreparedStatement pstmt = null;
		try {
			if(tblName.equals("message_visit")) {
				String sql = "update " + tblName + " set is_visit = ?, finish_time = ? where id = ?";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setInt(1, 1);
				pstmt.setTimestamp(2, getCurrentTimeStamp());
				pstmt.setString(3, field);
				// execute update SQL stetement
				pstmt.executeUpdate();
			}
			
			if(tblName.equals("user_visit")) {
				String sql = "update " + tblName + " set is_visit = ?, finish_time = ? where id = ?";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setInt(1, 1);
				pstmt.setTimestamp(2, getCurrentTimeStamp());
				pstmt.setString(3, field);
				// execute update SQL stetement
				pstmt.executeUpdate();
			}
			
			if(tblName.equals("user_name")) {
				String sql = "update " + tblName + " set is_visit = ?, finish_time = ? where user_name = ?";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setInt(1, 1);
				pstmt.setTimestamp(2, getCurrentTimeStamp());
				pstmt.setString(3, field);
				// execute update SQL stetement
				pstmt.executeUpdate();
			}
			
			if(tblName.equals("user_profile_id")) {
				String sql = "update " + tblName + " set is_visit = ?, finish_time = ? where user_id = ?";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setInt(1, 1);
				pstmt.setTimestamp(2, getCurrentTimeStamp());
				pstmt.setString(3, field);
				// execute update SQL stetement
				pstmt.executeUpdate();
			}
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				if(pstmt != null) {
					pstmt.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void checkUpdateCounts(int[] updateCounts) {
		int successRows = 0, noInfoRows = 0, failRows = 0;
		for (int i = 0; i < updateCounts.length; i++) {
			if (updateCounts[i] >= 0) {
				successRows++;
//				System.out.println("Successfully executed; updateCount=" + updateCounts[i]);
			} else if (updateCounts[i] == Statement.SUCCESS_NO_INFO) {
				noInfoRows++;
//				System.out.println("Successfully executed; updateCount=Statement.SUCCESS_NO_INFO");
			} else if (updateCounts[i] == Statement.EXECUTE_FAILED) {
				failRows++;
//				System.out.println("Failed to execute; updateCount=Statement.EXECUTE_FAILED");
			}
	    }
		
		System.out.println(String.format("Success:%d  NoInfo:%d  Failed:%d",successRows, noInfoRows, failRows));
		if(failRows == updateCounts.length){
			LogSys.nodeLogger.error("所有的数据都插入过了");
		}
	}
 
	private static java.sql.Timestamp getCurrentTimeStamp() {
		java.util.Date today = new java.util.Date();
		return new java.sql.Timestamp(today.getTime());
	}
	

	public static String convertTime(String t){
		
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
