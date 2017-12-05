package com.bohai.finance.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;


public class ApplicationConfig {
	
	private static final String APPLICATION_PROPERTY_FILE_NAME = "application.properties";
	
	private static File sFile = new File(APPLICATION_PROPERTY_FILE_NAME);
	
	private static Properties properties;
	
	public static String LAST_UPLOAD_DIRECTORY = "lastUploadDirectory";
	
	public static String LAST_OUT_DIRECTORY = "lastOutDirectory";
	
	static {
		
		try {
			if (!sFile.exists()) {
				sFile.createNewFile();
			}
			System.out.println("配置文件路径："+sFile.getAbsolutePath());
			
			properties = new Properties();
			
			FileInputStream fis = new FileInputStream(sFile);
			
			properties.load(fis);
			
			fis.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 通过属性名称查询属性值
	 * @param key
	 * @return
	 */
	public static String getProperty(String key){
		
		String value = "";
		try {
			value = properties.getProperty(key);
			
			System.out.println(value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return value;
	}
	
	
	/**
	 * 设置属性
	 * @param key
	 * @param value
	 */
	public static void setProperty(String key, String value){
		
		//先删除所有
		//removeAllProperties();
		
		
		try {
			properties.setProperty(key, value);
			
			OutputStream out = new FileOutputStream(sFile);
			//保存属性
			properties.store(out, "");
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
