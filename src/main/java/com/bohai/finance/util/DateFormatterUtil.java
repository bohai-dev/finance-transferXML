package com.bohai.finance.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatterUtil {
	
	public static Date getDate(String dateStr) throws ParseException{
		
	    if(dateStr == null || dateStr.equals("")){
			return null;
		}
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.parse(dateStr);
	}
	
	public static Date getDateyyyyMMdd(String dateStr) throws ParseException{
		
		if(dateStr == null || dateStr.equals("")){
			return null;
		}
		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		return format.parse(dateStr);
	}
	
	public static String getCurrentDateStr(){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return simpleDateFormat.format(new Date());
	}
	
	
	public static String getDateStr(Date date){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return simpleDateFormat.format(date);
	}
	
	public static String getDateStryyyyMMdd(Date date){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		return simpleDateFormat.format(date);
	}
	
	/**
	 * @param date
	 * @param formatter yyyy-mm-dd
	 * @return
	 */
	public static String getDateStrByFormatter(Date date, String formatter){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatter);
        return simpleDateFormat.format(date);
    }
	
	/**
	 * 根据字符串获取日期
	 * @param dateStr
	 * @return
	 * @throws ParseException
	 */
    public static Date getDateByFormatter(String dateStr, String formatter) throws ParseException{
        
        if(dateStr == null || dateStr.equals("")){
            return null;
        }
        
        SimpleDateFormat format = new SimpleDateFormat(formatter);
        return format.parse(dateStr);
    }

}
