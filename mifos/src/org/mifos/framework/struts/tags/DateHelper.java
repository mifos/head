/*
 * Created on Jul 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mifos.framework.struts.tags;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.StringTokenizer;

import org.mifos.framework.exceptions.ApplicationException;

public class DateHelper {

	private final static String dbFormat = "yyyy-MM-dd";
	public static String convertUserToDbFmt(String userDate, String userPattern){
		String returnVal = null;
		try{
			SimpleDateFormat userSdf = new SimpleDateFormat(userPattern);
			Date date = userSdf.parse(userDate); //util date not sql
			SimpleDateFormat dbSdf = new SimpleDateFormat(DateHelper.dbFormat);
			return dbSdf.format(date);

		}catch(ParseException parsee){
			parsee.printStackTrace();
			//TODO log and throw exception up
		}
		return returnVal;
	}

	public static String convertDbToUserFmt(String dbDate, String userPattern){
		String returnVal = null;
		try{
			SimpleDateFormat dbSdf = new SimpleDateFormat(DateHelper.dbFormat);
			Date date = dbSdf.parse(dbDate); //util date not sql
			SimpleDateFormat userSdf = new SimpleDateFormat(userPattern);
			return userSdf.format(date);

		}catch(ParseException parsee){
			parsee.printStackTrace();
			//TODO log and throw exception up
		}
		return returnVal;
	}

	public static String getUserLocaleDate(Locale locale,String value) {
		String returnDate="";
		if(locale!=null && value!=null && !value.equals("")){
			try{

				SimpleDateFormat sdf = (SimpleDateFormat)DateFormat.getDateInstance(DateFormat.SHORT, locale);
				String userfmt = convertToCurrentDateFormat(((SimpleDateFormat) sdf).toPattern());
				returnDate= convertDbToUserFmt((String)value,userfmt);
			}catch(Exception parsee){
				//TODO Exception handling and remove print stack trace
				parsee.printStackTrace();
			}
		}
		return returnDate;
	}
	public static Date getDate(String value) {
		Date date=null;
		if( value!=null && !value.equals("")){
			try{

				SimpleDateFormat dbSdf = new SimpleDateFormat("dd/MM/yyyy");
				date = dbSdf.parse(value); //util date not sql
			}catch(Exception parsee){
				//TODO Exception handling and remove print stack trace
				parsee.printStackTrace();
			}
		}
		return date;
	}
	//Bug id 26765. Added the method convertToCurrentDateFormat and called it from this method
	public static String getCurrentDate(Locale locale) {
		Calendar currentCalendar = new GregorianCalendar();
		int year=currentCalendar.get(Calendar.YEAR);
		int month=currentCalendar.get(Calendar.MONTH);
		int day=currentCalendar.get(Calendar.DAY_OF_MONTH);
		currentCalendar = new GregorianCalendar(year,month,day);
		java.sql.Date currentDate=new java.sql.Date(currentCalendar.getTimeInMillis());
		SimpleDateFormat sdf = (SimpleDateFormat)DateFormat.getDateInstance(DateFormat.SHORT, locale);
		String userfmt = convertToCurrentDateFormat(((SimpleDateFormat) sdf).toPattern());
		return convertDbToUserFmt(currentDate.toString(),userfmt);
	}

	//Bug id 26765. Added the method convertToCurrentDateFormat
	public static String convertToCurrentDateFormat(String pattern){
		char chArray[]=pattern.toCharArray();
		StringBuilder fmt = new StringBuilder();
		boolean d=false;
		boolean m=false;
		boolean y=false;
		String separator=DateHelper.getSeparator(pattern);
		for (int i=0; i<chArray.length;i++){
			if ((chArray[i]=='d' ||chArray[i]=='D')&& !d){
				fmt.append("dd"); d = true;
				fmt.append(separator);
			}
			else if ((chArray[i]=='m' ||chArray[i]=='M') && !m){
				fmt.append("MM"); m = true;
				fmt.append(separator);
			}
			else if ((chArray[i]=='y' ||chArray[i]=='Y') && !y){
				fmt.append("yyyy"); y = true;
				fmt.append(separator);
			}
		}
		return fmt.substring(0,fmt.length()-1);
	}
	public static String convertToMFIFormat(String date, String format){
		String MFIString;
		String MFIfmt=getMFIFormat();
		String day="";
		String month="";
		String year="";
		String token;
		MFIfmt=convertToDateTagFormat(MFIfmt);
		StringTokenizer stfmt = new StringTokenizer(format,"/");
		StringTokenizer stdt = new StringTokenizer(date,"/");
		while(stfmt.hasMoreTokens()&& stdt.hasMoreTokens()){
			token= stfmt.nextToken();
			if (token.equalsIgnoreCase("D")){
				day=stdt.nextToken();
			}else if (token.equalsIgnoreCase("M")){
				month=stdt.nextToken();
			}else
				year=stdt.nextToken();
		}
		MFIString=createDateString(day,month,year,MFIfmt);
		return  MFIString;
	}
	public static String[] getDayMonthYear(String date, String format){
		String day="";
		String month="";
		String year="";
		String token;
		StringTokenizer stfmt = new StringTokenizer(format,"/");
		StringTokenizer stdt = new StringTokenizer(date,"/");
		while(stfmt.hasMoreTokens()&& stdt.hasMoreTokens()){
			token= stfmt.nextToken();
			if (token.equalsIgnoreCase("D")){
				day=stdt.nextToken();
			}else if (token.equalsIgnoreCase("M")){
				month=stdt.nextToken();
			}else
				year=stdt.nextToken();
		}
		return new String[]{day,month,year};
	}

	public static String[] getDayMonthYear(String date, String format,String separator){
		String day="";
		String month="";
		String year="";
		String token;
		StringTokenizer stfmt = new StringTokenizer(format,"/");
		StringTokenizer stdt = new StringTokenizer(date,separator);
		while(stfmt.hasMoreTokens()&& stdt.hasMoreTokens()){
			token= stfmt.nextToken();
			if (token.equalsIgnoreCase("D")){
				day=stdt.nextToken();
			}else if (token.equalsIgnoreCase("M")){
				month=stdt.nextToken();
			}else
				year=stdt.nextToken();
		}
		return new String[]{day,month,year};
	}
	public static java.sql.Date getLocaleDate(Locale locale,String value) {
		java.sql.Date date = null;
		if(locale!=null && value!=null && !value.equals("")){
			try{

				SimpleDateFormat sdf = (SimpleDateFormat)DateFormat.getDateInstance(DateFormat.SHORT, locale);
				String userfmt = ((SimpleDateFormat) sdf).toPattern();
				String dbDate=convertUserToDbFmt((String)value,userfmt);
				date=java.sql.Date.valueOf(dbDate);

			}catch(Exception parsee){
				//TODO Exception handling and remove print stack trace
				parsee.printStackTrace();
			}
		}
		return date;
	}

	public static String[] getDayMonthYearDbFrmt(String date, String format){
		String day="";
		String month="";
		String year="";
		String token;
		StringTokenizer stfmt = new StringTokenizer(format,"-");
		StringTokenizer stdt = new StringTokenizer(date,"-");
		while(stfmt.hasMoreTokens()&& stdt.hasMoreTokens()){
			token= stfmt.nextToken();
			if (token.equalsIgnoreCase("D")){
				day=stdt.nextToken();
			}else if (token.equalsIgnoreCase("M")){
				month=stdt.nextToken();
			}else
				year=stdt.nextToken();
		}
		return new String[]{day,month,year};
	}
	public static String getMFIFormat(){
		//TODO change this to pick from app config
		return "dd/mm/yy";
	}
	public static String getMFIShortFormat(){
		return convertToDateTagFormat("dd/mm/yy");
	}
	public static String convertToDateTagFormat(String pattern){
		char chArray[]=pattern.toCharArray();
		StringBuilder fmt = new StringBuilder();
		boolean d=false;
		boolean m=false;
		boolean y=false;
		for (int i=0; i<chArray.length;i++){
			if ((chArray[i]=='d' ||chArray[i]=='D')&& !d){
				fmt.append("D/"); d = true;
			}
			else if ((chArray[i]=='m' ||chArray[i]=='M') && !m){
				fmt.append("M/"); m = true;
			}
			else if ((chArray[i]=='y' ||chArray[i]=='Y') && !y){
				fmt.append("Y/"); y = true;
			}
		}

		return fmt.substring(0,fmt.length()-1);
	}

	public static String getSeparator(String pattern){
		char chArray[]=pattern.toCharArray();
		for (int i=0; i<chArray.length;i++){
			if (chArray[i]!='d' && chArray[i]!='D' &&
					chArray[i]!='m' && chArray[i]!='M' && chArray[i]!='y'  &&chArray[i]!='Y') {
				return 	String.valueOf(chArray[i]);
			}
		}
		return "";
	}

	public static String createDateString(String day,String month,String year,String format){
		StringTokenizer stfmt = new StringTokenizer(format,"/");
		String token;
		StringBuilder dt =new StringBuilder();
		while(stfmt.hasMoreTokens()){
			token=stfmt.nextToken();
			if (token.equals("D")){
				dt.append(day+"/");
			}else if (token.equals("M")){
				dt.append(month+"/");
			}else
				dt.append(year+"/");
		}

		return dt.deleteCharAt((dt.length()-1)).toString();
	}

	public static java.sql.Date getSQLDate(String date) throws ApplicationException  {
		//TODO change this
		String format="yyyy/MM/dd";
		try {
			if(date != null || !date.equals("")){
				SimpleDateFormat sdf = new SimpleDateFormat(format);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(sdf.parse(date));
				// System.out.println("Date in Helper"+new java.sql.Date(calendar.getTime().getTime()));
				return new java.sql.Date(calendar.getTime().getTime());
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw new ApplicationException(e);
		}
		return null;
	}

	public static int DateDiffInYears(java.sql.Date fromDate){

		Calendar fromDateCal = new GregorianCalendar();

	    fromDateCal.setTime(fromDate);

	    // Create a calendar object with today's date
	    Calendar today = new GregorianCalendar();
	    //Calendar today = Calendar.getInstance();
	    // Get age based on year
	    int age = today.get(Calendar.YEAR) - fromDateCal.get(Calendar.YEAR);
	    int monthDiff = (today.get(Calendar.MONTH)+1) - (fromDateCal.get(Calendar.MONTH)+1);
	    int dayDiff = today.get(Calendar.DAY_OF_MONTH) - fromDateCal.get(Calendar.DAY_OF_MONTH);
	    // If this year's birthday has not happened yet, subtract one from age
	    if ( monthDiff < 0) {
	        age--;
	    }
	    else if ( monthDiff ==0 ){
	    	if(dayDiff < 0) {
	        age--;
	    	}
	    }
	    return age;
	}

	public static String getDBtoUserFormatString(Date dbDate, Locale userLocale){
		String ret = null;
		SimpleDateFormat sdf = (SimpleDateFormat)DateFormat.getDateInstance(DateFormat.MEDIUM, userLocale);
		ret = sdf.format(dbDate);
		return ret;
	}
	
	public static String getDBtoUserFormatShortString(Date dbDate, Locale userLocale){
		String ret = null;
		SimpleDateFormat sdf = (SimpleDateFormat)DateFormat.getDateInstance(DateFormat.SHORT, userLocale);
		String userfmt = convertToCurrentDateFormat(((SimpleDateFormat) sdf).toPattern());
		ret = sdf.format(dbDate);
		return ret;
	}
}
