package com.iie.googleplus.tool;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class MYPropertyTool {

	/**
	 * @param args
	 */
	static Properties pro;
	public static void  Init(String filePath){
		pro=new Properties();
		try {
			pro.load(new FileInputStream(filePath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("�ļ�������");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("�ļ���д�쳣");
			e.printStackTrace();
		}		
	}
	public static Properties getPro(){
		return pro;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
