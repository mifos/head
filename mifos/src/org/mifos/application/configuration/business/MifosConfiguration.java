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
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.business.MifosLookUpEntity;
import org.mifos.config.Localization;

/**
 * This class encapsulate all the configuration related funtionality in system
 * e.g. Label configuration
 */

public class MifosConfiguration {

	private Map<LabelKey, String> labelCache;

	//kim commented out on 10/02 will remove soonmove the cache to Localization class, so everything related to the cache is moved, too
	// private Map<String, Short> loacleIdCache;

	private boolean centerHierarchyExist;

	private static final MifosConfiguration configuration = 
		new MifosConfiguration();

	public static MifosConfiguration getInstance() {
		return configuration;
	}

	private MifosConfiguration() {
		labelCache = new ConcurrentHashMap<LabelKey, String>();
		//kim commented out on 10/02 will remove soon loacleIdCache = new ConcurrentHashMap<String, Short>();
	}

	public boolean isCenterHierarchyExist() {
		return centerHierarchyExist;
	}

	public void setCenterHierarchyExist(boolean centerHierarchyExist) {
		this.centerHierarchyExist = centerHierarchyExist;
	}

	public Short getMFILocaleId() {
		// kim commented out on 10/02 will remove soon return Short.valueOf("1");
		return Localization.getInstance().getLocaleId();
	}

	public void init() {
		initializeLabelCache();
		// kim commented out on 10/02 will remove soon initializeloacleIdCache();

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

	/*kim commented out on 10/02 will remove soon this is moved to Localization class private void initializeloacleIdCache() {
		List<SupportedLocalesEntity> locales = 
			new ApplicationConfigurationPersistence().getSupportedLocale();

		for (SupportedLocalesEntity locale : locales) {

			//kim replace this loacleIdCache.put(locale.getLanguage().getLanguageShortName()
			//		.toLowerCase()
			//		+ "_" + locale.getCountry().getCountryShortName(), locale
			//		.getLocaleId());
			loacleIdCache.put(locale.getLanguageCode()
							.toLowerCase()
							+ "_" + locale.getCountryCode(), locale
							.getLocaleId());
		}
		
	}*/

	public Map<LabelKey, String> getLabelCache() {
		return labelCache;
	}

	public String getLabelValue(String key, Short localeId) {
		return labelCache.get(new LabelKey( key,localeId));
	}

	//public Map<String, Short> getLoacleIdCache() {
	//	return loacleIdCache;
	//}

	public String getLabel(String key, Locale locale)throws ConfigurationException {
		if(locale==null || key==null)
			throw new ConfigurationException(ConfigurationConstants.KEY_NO_MESSAGE_FOR_THIS_KEY);
		// kim commented out on 10/02 will remove soon commented this out String localeKey = locale.getLanguage() + "_" + locale.getCountry();
		// Short curLocaleId = getLoacleIdFromCache(localeKey);
		Short curLocaleId = Localization.getInstance().getLocaleId();
		if(curLocaleId==null )
			throw new ConfigurationException(ConfigurationConstants.KEY_NO_MESSAGE_FOR_THIS_KEY);
		String labelText =getLabelValue(key, curLocaleId);
		//todo pass  properkey
		//if ( null==labelText) throw new ConfigurationException(ConfigurationConstants.KEY_NO_MESSAGE_FOR_THIS_KEY);
		return labelText;
	}

	/*kim commented out on 10/02 will remove soon private Short getLoacleIdFromCache(String localeKey) {
		return loacleIdCache.get(localeKey);
	}*/
}
