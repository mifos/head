package org.mifos.framework.components.configuration.business;

import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.config.Localization;

public class SystemConfiguration {
	
	  private final MifosCurrency currency;
	  //private final SupportedLocalesEntity mfiLocale;
	  private final Locale locale;
	  private final Integer sessionTimeOut;
	  private final TimeZone timeZone;

	  //public SystemConfiguration(SupportedLocalesEntity mfiLocale, 
		//	  MifosCurrency currency, int sessionTimeOut, 
		//	  int timeZoneOffSet) {
	  public SystemConfiguration( 
					  MifosCurrency currency, int sessionTimeOut, 
					  int timeZoneOffSet) {
		  //this.mfiLocale = Localization.getInstance().getSupportedLocale();
		  this.currency = currency;
		  this.sessionTimeOut = sessionTimeOut;
		  this.timeZone = new SimpleTimeZone(timeZoneOffSet,SimpleTimeZone.getAvailableIDs(timeZoneOffSet)[0]);
		  this.locale = Localization.getInstance().getMainLocale();
	  }

	  public Locale getMFILocale() {
		  return locale;
	  }
	  
	  public Short getMFILocaleId(){
		  //return mfiLocale.getLocaleId();
		  return Localization.getInstance().getLocaleId();
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
