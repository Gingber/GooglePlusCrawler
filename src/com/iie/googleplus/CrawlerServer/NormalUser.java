package com.iie.googleplus.CrawlerServer;

import java.io.Serializable;

public class NormalUser implements Serializable {
	public String userID;
	public int sum;
	public NormalUser(String userID, int sum) {
		super();
		this.userID = userID;
		this.sum = sum;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public int getSum() {
		return sum;
	}
	public void setSum(int sum) {
		this.sum = sum;
	}
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final NormalUser other = (NormalUser) obj;
		if (userID == null) {
			if (other.userID != null)
				return false;
		} else if (!userID.equals(other.userID))
			return false;
		return true; 
		
		
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userID == null) ? 0 : userID.hashCode());
		return result;  
	}
	@Override
	public String toString(){
		return userID+":"+sum+" ";
	}
	

}
