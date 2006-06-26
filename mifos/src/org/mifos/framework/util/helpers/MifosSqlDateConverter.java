package org.mifos.framework.util.helpers;


import java.util.Locale;

import org.apache.commons.beanutils.Converter;
import org.mifos.framework.struts.tags.DateHelper;

public class MifosSqlDateConverter implements Converter {

	private Locale locale = null;
	
	/**
	 * @param format The format to set.
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}


	public MifosSqlDateConverter() {
	}


	public Object convert(Class type, Object value) {
		java.sql.Date date = null;
		/*if(locale!=null && value!=null && type!=null && !value.equals("")){	
			try{
				
				SimpleDateFormat sdf = (SimpleDateFormat)DateFormat.getDateInstance(DateFormat.SHORT, locale);
				String userfmt = ((SimpleDateFormat) sdf).toPattern();
				String[] dayMnthYear=DateHelper.getDayMonthYear((String)value,DateHelper.convertToDateTagFormat(userfmt));
				//java.util.Date parsedDate = sdf.parse((String)value);
				date = new java.sql.Date(new GregorianCalendar(Integer.parseInt(dayMnthYear[2]),
						Integer.parseInt(dayMnthYear[1])-1,Integer.parseInt( dayMnthYear[0])).getTimeInMillis());
				String dbDate=DateHelper.convertUserToDbFmt((String)value,userfmt);
				date=java.sql.Date.valueOf(dbDate);
				
			}catch(Exception parsee){
				//TODO Exception handling and remove print stack trace
				parsee.printStackTrace();
				//date= new java.sql.Date(0l);
			}
		}
		return date;*/
		if(locale!=null && value!=null && type!=null && !value.equals("")){	
			date= DateHelper.getLocaleDate(locale,(String)value);
		}
		return date;
	}
	
}
