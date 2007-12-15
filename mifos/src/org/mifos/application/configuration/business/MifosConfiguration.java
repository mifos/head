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
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.LookUpLabelEntity;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.business.MifosLookUpEntity;
import org.mifos.config.Localization;

/**
 * This class encapsulate all the configuration related funtionality in system
 * e.g. Label configuration
 */

public class MifosConfiguration {

	private Map<LabelKey, String> labelCache;


	//private boolean centerHierarchyExist;

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
		ApplicationConfigurationPersistence configurationPersistence = 
			new ApplicationConfigurationPersistence();
		List<MifosLookUpEntity> entities = 
			configurationPersistence.getLookupEntities();
		for (MifosLookUpEntity entity : entities) {
			Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
			for (LookUpLabelEntity label : labels) {
				labelCache.put(new LabelKey( entity
						.getEntityType(),label.getLocaleId()), label.getLabelName());
			}
		}
		
		
		List<LookUpValueEntity> values = configurationPersistence.getLookupValues();
		for (LookUpValueEntity value : values) {
			Set<LookUpValueLocaleEntity> localeValues = value
					.getLookUpValueLocales();
			for (LookUpValueLocaleEntity locale : localeValues) {
				String keyString = value.getLookUpName();
				if (keyString == null) keyString = " ";
				labelCache.put(new LabelKey( keyString,locale.getLocaleId()), locale.getLookUpValue());

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
		if(locale==null || key==null)
			throw new ConfigurationException(ConfigurationConstants.KEY_NO_MESSAGE_FOR_THIS_KEY);
		
		Short curLocaleId = Localization.getInstance().getLocaleId();
		if(curLocaleId==null )
			throw new ConfigurationException(ConfigurationConstants.KEY_NO_MESSAGE_FOR_THIS_KEY);
		String labelText =getLabelValue(key, curLocaleId);
		
		//todo pass  properkey
		//if ( null==labelText) throw new ConfigurationException(ConfigurationConstants.KEY_NO_MESSAGE_FOR_THIS_KEY);
		return labelText;
	}

	
}
