package com.iie.googleplus.DAO;


import java.sql.*;
import java.util.*;

import com.iie.googleplus.DAO.bean.KeyWord;
import com.iie.googleplus.tool.DbOperation;

public class DBKeyWordDAO {
	DbOperation dbo;
	public DBKeyWordDAO(){
		dbo=new DbOperation();
	}
	public Vector<KeyWord> getKeyWords(){
		Vector<KeyWord> res=new Vector<KeyWord>();
		Connection con=dbo.GetConnection();
		Statement sta=null;
		try {
			sta=con.createStatement();
			ResultSet rs=sta.executeQuery("select * from keyword");
			while(rs.next()){
				KeyWord keyword=new KeyWord(rs.getString("WordStr"));
				res.add(keyword);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try{
				sta.close();
				con.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		
		return res;
	}

}
