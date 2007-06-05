package org.mifos.framework.components.configuration.business;

import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.SupportedLocalesEntity;

public class SystemConfiguration {
	
	  private final MifosCurrency currency;
	  private final SupportedLocalesEntity mfiLocale;
	  private final Locale locale;
	  private final Integer sessionTimeOut;
	  private final TimeZone timeZone;

	  public SystemConfiguration(SupportedLocalesEntity mfiLocale, 
			  MifosCurrency currency, int sessionTimeOut, 
			  int timeZoneOffSet) {
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
	  
	  public Integer getSessionTimeOut() {
		  return sessionTimeOut;
	  }
	  
	  public TimeZone getMifosTimeZone(){
		  return timeZone;
	  }	
}
