package com.iie.httpclient.crawler;

import java.io.File;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.iie.googleplus.analyzer.beans.UserProfile;

public class AjaxUserProfileAnalyzer {
	
	public AjaxUserProfileAnalyzer() {
		
	}
	
	public void doAnylyze(String content, UserProfile userprofile) {
		
		try {
			String jsonHtml = ParseToJsonStr(content);
			//parse user info
			JSONObject jok5 = new JSONObject(jsonHtml);
			
			JSONArray data = jok5.getJSONArray("data");
			String userId = data.getString(0);
			
			JSONArray nameArray = data.getJSONArray(2).getJSONArray(4);    //json array with names
			String fullName = nameArray.getString(3);
			
			String url = data.getJSONArray(2).getString(2) + "/about";
		
			String gender = "";
			if(data.getJSONArray(4).getJSONArray(7).length() != 0) {
				if(data.getJSONArray(4).getJSONArray(7).getJSONArray(0).getInt(0) == 1002) {  //==1002表示内部嵌套JSON object存在
					int index = data.getJSONArray(4).getJSONArray(7).getJSONArray(0).length() -1;  //内部嵌套json object的下标
					JSONObject innerJO = new JSONObject(data.getJSONArray(4).getJSONArray(7).getJSONArray(0).getJSONObject(index).toString());
					if(innerJO.getJSONArray("33558957").getJSONArray(136).length() == 7) {
						gender = innerJO.getJSONArray("33558957").getJSONArray(136).getString(6);
					}
				} else {
					gender = "other";
				}
			}
			
			

			String birthday = "";
			int year = 0, month = 0, day = 0;
			if(data.getJSONArray(2).getJSONArray(16).length() != 0) {
				if(data.getJSONArray(2).getJSONArray(16).getString(1).equals("")) {//birthday 描述串空
					year = 0;
					month = 0;
					day = 0;
				} else {
					month = data.getJSONArray(2).getJSONArray(16).getInt(4);
					day = data.getJSONArray(2).getJSONArray(16).getInt(3);
				}	
				if(data.getJSONArray(2).getJSONArray(16).getInt(2)  == 1)      //birthday是否存在年标志
					year = data.getJSONArray(2).getJSONArray(16).getInt(5);
				birthday = Integer.toString(year) + '/' + Integer.toString(month) + '/' + Integer.toString(day);
			}
			
			
			String currentLoc = data.getJSONArray(2).getJSONArray(9).getString(1);
			String imageUrl = data.getJSONArray(2).getString(3);
			String aboutMe = data.getJSONArray(2).getJSONArray(14).getString(1);
			String tagLine = data.getJSONArray(2).getJSONArray(33).getString(1);
			
			String organizations = "";
			int nOrganization = data.getJSONArray(2).getJSONArray(7).getJSONArray(1).length();
			for(int i=0; i<nOrganization; ++i) {
				organizations += data.getJSONArray(2).getJSONArray(7).getJSONArray(1).getJSONArray(i).getString(0);
				organizations += '#';
			}
			if(!organizations.equals(""))
				organizations = organizations.substring(0, organizations.length()-1);
			
			int followers = 0;
			if(!data.getJSONArray(2).getJSONArray(39).isNull(1))
				followers = data.getJSONArray(2).getJSONArray(39).getInt(1);
			
			int friends = 0;
			if(data.getJSONArray(4).getJSONArray(7).length() > 3 && 
					!data.getJSONArray(4).getJSONArray(7).getJSONArray(2).isNull(2)) {
				int index = data.getJSONArray(4).getJSONArray(7).getJSONArray(2).getJSONArray(2).length() -1;  //内部嵌套json object的下标
				JSONObject innerJO = new JSONObject(data.getJSONArray(4).getJSONArray(7).getJSONArray(2).getJSONArray(2).getJSONObject(index).toString());
				if(!innerJO.getJSONArray("44801125").getJSONArray(2).getJSONArray(0).isNull(0)){
					friends = innerJO.getJSONArray("44801125").getJSONArray(2).getJSONArray(0).getInt(0);
				}
			}
			
			userprofile.setUserId(userId);
			userprofile.setUserName(fullName);
			userprofile.setUrl(url);
			userprofile.setGender(gender);
			userprofile.setBirthday(birthday);
			userprofile.setCurrentLoc(currentLoc);
			userprofile.setImageUrl(imageUrl);
			userprofile.setAboutMe(aboutMe);
			userprofile.setTagLine(tagLine);
			userprofile.setOrganizations(organizations);
			userprofile.setFollowers(followers);
			userprofile.setFriends(friends);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Constructor
	public String ParseToJsonStr(String content) throws IOException, JSONException{		
		
		String strJsonKey5 = "";
		String strJsonKey4 = "";
		
		Document doc = Jsoup.parse(content);
		Elements scriptElements = doc.getElementsByTag("script");
		for (Element element : scriptElements ){                
			for (DataNode node : element.dataNodes()) {
	        	String nodesData = node.getWholeData();
	        	if(nodesData.substring(0, 19).equals("AF_initDataCallback")) {
		        	int leftBracePos = nodesData.indexOf('{');
		        	int rightBracePos = nodesData.lastIndexOf('}');
		        	nodesData = nodesData.substring(leftBracePos, rightBracePos+1);
		        	//System.out.println(nodesData);
		        	JSONObject jo = new JSONObject(nodesData);
		        	if(jo.get("key").toString().equals("5")) {
		        		strJsonKey5 = nodesData;		        		
		        	} else if(jo.get("key").toString().equals("4")) {
		        		strJsonKey4 = nodesData;
		        	}
	        	}
	        }	                
		}
		
		return strJsonKey5;
		
	}

}
