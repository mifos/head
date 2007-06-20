package org.mifos.application.master;

import java.util.Locale;

import org.mifos.application.configuration.persistence.ConfigurationPersistence;
import org.mifos.application.configuration.struts.action.LabelConfigurationAction;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.framework.security.util.UserContext;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

/**
 * This class looks up messages
 * from tables like {@link LookUpValueEntity}, {@link LookUpValueLocaleEntity}
 * and the like.
 * 
 * The idea is that we'll be able to come up with a simpler mechanism 
 * than the rather convoluted
 * one in {@link MasterPersistence}, {@link MasterDataEntity}, etc.
 * Or at least we can centralize where we call the convoluted
 * mechanism.
 * 
 * Also see {@link ConfigurationPersistence}.
 * 
 * The word "label" might be better than "message"; at least that's
 * what we call them in places like {@link LabelConfigurationAction}.
 * 
 * An initial pass has been made at moving to resource bundle based
 * localization.  A Spring MessageSource is injected into the single 
 * instance of MessageLookup.  The MessageSource is used to manage
 * the loading and lookup from external resource bundle files.
 * See {@link org.mifos.config.applicationContext.xml}
 * for the Spring configuration. 
 */
public class MessageLookup implements MessageSourceAware {
	private static MessageLookup messageLookupInstance = new MessageLookup();
	
	private MessageSource messageSource;
	
	public static final MessageLookup getInstance() {
		return messageLookupInstance;
	}
	
	public String lookup(WeekDay weekDay, Locale locale) {
		return messageSource.getMessage(weekDay.getPropertiesKey(), null, weekDay.getPropertiesKey(), locale);		
	}
	
	public String lookup(WeekDay weekDay, UserContext user) {
		return lookup(weekDay, user.getPreferredLocale());

	}

	/**
	 * This is a dependency injection method used by Spring to
	 * inject a MessageSource for resource bundle based message
	 * lookup.  
	 */
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource; 
	}

}
