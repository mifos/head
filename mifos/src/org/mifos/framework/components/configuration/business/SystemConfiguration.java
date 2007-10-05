package org.mifos.framework.components.configuration.business;

import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.config.Localization;

public class SystemConfiguration {
	
	  private final MifosCurrency currency;
	  private final SupportedLocalesEntity mfiLocale;
	  private final Locale locale;
	  private final Integer sessionTimeOut;
	  private final TimeZone timeZone;

	  //public SystemConfiguration(SupportedLocalesEntity mfiLocale, 
		//	  MifosCurrency currency, int sessionTimeOut, 
		//	  int timeZoneOffSet) {
	  public SystemConfiguration( 
					  MifosCurrency currency, int sessionTimeOut, 
					  int timeZoneOffSet) {
		  this.mfiLocale = Localization.getInstance().getSupportedLocale();
		  this.currency = currency;
		  this.sessionTimeOut = sessionTimeOut;
		  this.timeZone = new SimpleTimeZone(timeZoneOffSet,SimpleTimeZone.getAvailableIDs(timeZoneOffSet)[0]);
		  //kim commented out on 10/02 will remove soon replace this with Localization method this.locale = new Locale(mfiLocale.getLanguage().getLanguageShortName(),mfiLocale.getCountry().getCountryShortName());
		  this.locale = Localization.getInstance().getLocale();
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
