package org.mifos.application.master;

import static org.junit.Assert.assertEquals;

import java.util.Locale;
import java.util.Set;

import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.exceptions.ConfigurationException;
import org.mifos.application.configuration.persistence.ApplicationConfigurationPersistence;
import org.mifos.application.configuration.struts.action.LabelConfigurationAction;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.master.business.LookUpLabelEntity;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.MifosLookUpEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.config.LocalizedTextLookup;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.mifos.config.Localization;

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
 * Also see {@link ApplicationConfigurationPersistence}.
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
 * 
 * Enumerated types which implement the {@link LocalizedTextLookup}
 * interface can be passed to {@link MessageLookup} to look up
 * a localized text string for each instance of the enumerated type.
 * 
 * Text strings for enumerated types can currently be found in
 * org.mifos.config.resources.enumerations.properties (and 
 * associated versions for different locales).
 */
public class MessageLookup implements MessageSourceAware {
	private static MessageLookup messageLookupInstance = new MessageLookup();
	
	private MessageSource messageSource;
	
	public static final MessageLookup getInstance() {
		return messageLookupInstance;
	}
	
	public String lookup(LocalizedTextLookup namedObject, Locale locale) {
		return messageSource.getMessage(namedObject.getPropertiesKey(), null, namedObject.getPropertiesKey(), locale);		
	}
	
	/*
	 * TODO: this will need to change in order to support per user Locale selection
	 */
	public String lookup(LocalizedTextLookup namedObject) {
		Locale locale = Localization.getInstance().getMainLocale();
		return messageSource.getMessage(namedObject.getPropertiesKey(), null, namedObject.getPropertiesKey(), locale);		
	}
	
	public String lookup(LocalizedTextLookup namedObject, Object[] params) {
		Locale locale = Localization.getInstance().getMainLocale();
		return messageSource.getMessage(namedObject.getPropertiesKey(), params, namedObject.getPropertiesKey(), locale);		
	}
	
	public String lookup(LocalizedTextLookup namedObject, UserContext user) {
		return lookup(namedObject, user.getPreferredLocale());
	}

	public String lookup(String lookupKey, Locale locale) {
		return messageSource.getMessage(lookupKey, null, lookupKey, locale);		
	} 
	
	public String lookup(String lookupKey, UserContext userContext) {
		Locale locale = userContext.getPreferredLocale();
		return lookup(lookupKey, locale);
	}
	
	public String lookupLabel(String labelKey) {
		return lookupLabel(labelKey, Localization.getInstance().getMainLocale());
	}
	
	/*
	 * Return a label for given label key.  Label keys are listed in
	 * {@link ConfigurationConstants}.
	 */
	public String lookupLabel(String labelKey, Locale locale) {
		try {
			String labelText = MifosConfiguration.getInstance().getLabel(labelKey, locale);
			
			// if we don't find a label here, then it means that it has not been customized and
			// we should return the default label from the properties file
			if (labelText == null || labelText.length() == 0) {
				labelText = lookup(labelKey + ".Label", locale);
			}
			
			return labelText;
			
		}
		catch (ConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * Return a label for given label key.  Label keys are listed in
	 * {@link ConfigurationConstants}.
	 */
	public String lookupLabel(String labelKey, Short localeId) {
		return MifosConfiguration.getInstance().getLabelValue(labelKey, localeId);
	}
	
	/* 
	 * Set a custom label value that will override resource bundle values.
	 * 
	 * TODO: we need to add a method for getting and/or setting a label
	 * value directly rather than having to iterate. Also, we don't 
	 * necessarily want to reinitialize the MifosConfiguration after
	 * each update.  Ultimately, it would be cleaner to just use a key-value
	 * lookup to implement these overrides.  
	 */
	public void setCustomLabel(String labelKey, String value) throws PersistenceException {
		ApplicationConfigurationPersistence configurationPersistence = new ApplicationConfigurationPersistence();
		for (MifosLookUpEntity entity : configurationPersistence.getLookupEntities()) {
			if (entity.getEntityType().equals(labelKey)) {
				Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
				assertEquals(labels.size(),1);
				for (LookUpLabelEntity label : labels) {
					label.setLabelName(value);
					configurationPersistence.createOrUpdate(label);
					HibernateUtil.commitTransaction();
				}				
			}
		}
		MifosConfiguration.getInstance().init();
		
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
