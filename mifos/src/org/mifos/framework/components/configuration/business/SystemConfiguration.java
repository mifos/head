package org.mifos.framework.components.configuration.business;

import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.SupportedLocalesEntity;

public class SystemConfiguration{
	
	  private MifosCurrency currency;
	  private SupportedLocalesEntity mfiLocale;
	  private Locale locale;
	  private Short sessionTimeOut;
	  private TimeZone timeZone;

	  public SystemConfiguration(SupportedLocalesEntity mfiLocale, MifosCurrency currency, Short sessionTimeOut, int timeZoneOffSet){
		  this.mfiLocale = mfiLocale;
		  this.currency = currency;
		  this.sessionTimeOut = sessionTimeOut;
		  this.timeZone = new SimpleTimeZone(timeZoneOffSet,SimpleTimeZone.getAvailableIDs(timeZoneOffSet)[0]);
		  this.locale = new Locale(mfiLocale.getLanguage().getLanguageShortName(),mfiLocale.getCountry().getCountryShortName());
	  }

	  public Locale getMFILocale() {
		  return locale;
	  }
	  
	  public Short getMFILocaleId(){
		  return mfiLocale.getLocaleId();
	  }
	  
	  public MifosCurrency getCurrency() {
		  return currency;
	  }
	  
	  public Short getSessionTimeOut() {
		  return sessionTimeOut;
	  }
	  
	  public TimeZone getMifosTimeZone(){
		  return timeZone;
	  }	
}
