package org.mifos.application.configuration.business;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.mifos.application.configuration.exceptions.ConfigurationException;
import org.mifos.application.configuration.persistence.ApplicationConfigurationPersistence;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.configuration.util.helpers.LabelKey;
import org.mifos.application.master.business.LookUpLabelEntity;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.MifosLookUpEntity;
import org.mifos.config.LocalizedTextLookup;
/**
 * This class caches label text and lookUpValue text.
 * 
 * Feb. 2008 - this class is slated to be removed.  It is unclear if we need
 * caching and if we do, we should be able to use the Hibernate 2nd level cache
 * or Spring based caching support.
 */

public class MifosConfiguration {

	private Map<LabelKey, String> labelCache;

	private static final MifosConfiguration configuration = 
		new MifosConfiguration();

	public static MifosConfiguration getInstance() {
		return configuration;
	}

	private MifosConfiguration() {
		labelCache = new ConcurrentHashMap<LabelKey, String>();
	}

	public void init() {
		initializeLabelCache();

	}
	
	
	
	public void updateLabelKey(String keyString, String newLabelValue, Short localeId)
	{
		synchronized(labelCache)
		{
			LabelKey key = new LabelKey( keyString,localeId);
			if (labelCache.containsKey(key))
			{
				labelCache.remove(key);
				labelCache.put(key, newLabelValue);
			}
			else
				labelCache.put(key, newLabelValue);
		}
	}

	private void initializeLabelCache() {
		labelCache.clear();
		ApplicationConfigurationPersistence configurationPersistence = 
			new ApplicationConfigurationPersistence();

		List<LookUpValueEntity> lookupValueEntities = configurationPersistence.getLookupValues();
		for (LookUpValueEntity lookupValueEntity : lookupValueEntities) {
			String keyString = lookupValueEntity.getPropertiesKey();
			if (keyString == null) keyString = " ";

			labelCache.put(new LabelKey( keyString,MasterDataEntity.CUSTOMIZATION_LOCALE_ID), lookupValueEntity.getMessageText());
		}
		
		List<MifosLookUpEntity> entities = 
			configurationPersistence.getLookupEntities();
		for (MifosLookUpEntity entity : entities) {
			Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
			for (LookUpLabelEntity label : labels) {
				labelCache.put(new LabelKey( entity
						.getEntityType(),label.getLocaleId()), label.getLabelText());
			}
		}
		
	}

	public void updateKey(LocalizedTextLookup keyContainer, String newValue) {
		synchronized(labelCache)
		{
			LabelKey key = new LabelKey( keyContainer.getPropertiesKey(), MasterDataEntity.CUSTOMIZATION_LOCALE_ID);
			if (labelCache.containsKey(key))
			{
				labelCache.remove(key);
				labelCache.put(key, newValue);
			}
			else
				labelCache.put(key, newValue);
		}		
	}
	
	public void updateKey(String lookupValueKey, String newValue) {
		synchronized(labelCache)
		{
			LabelKey key = new LabelKey(lookupValueKey, MasterDataEntity.CUSTOMIZATION_LOCALE_ID);
			if (labelCache.containsKey(key))
			{
				labelCache.remove(key);
				labelCache.put(key, newValue);
			}
			else
				labelCache.put(key, newValue);
		}		
	}
	
	public void deleteKey(String lookupValueKey) {
		synchronized(labelCache)
		{
			LabelKey key = new LabelKey(lookupValueKey, MasterDataEntity.CUSTOMIZATION_LOCALE_ID);
			if (labelCache.containsKey(key))
			{
				labelCache.remove(key);
			}
		}		
	}

	

	public Map<LabelKey, String> getLabelCache() {
		return labelCache;
	}

	public String getLabelValue(String key, Short localeId) {
		return labelCache.get(new LabelKey( key,localeId));
	}

	public String getLabel(String key, Locale locale)throws ConfigurationException {
//		if(locale==null || key==null)
//			throw new ConfigurationException(ConfigurationConstants.KEY_NO_MESSAGE_FOR_THIS_KEY);
		
		if (key == null) {
			return null;
		}
		
		// we only use localeId 1 to store labels since it is an override for
		// all locales
		String labelText =getLabelValue(key, (short)1);
		
		return labelText;
	}

	
}
