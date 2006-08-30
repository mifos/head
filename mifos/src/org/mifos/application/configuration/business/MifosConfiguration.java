package org.mifos.application.configuration.business;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;



import org.mifos.application.configuration.exceptions.ConfigurationException;
import org.mifos.application.configuration.persistence.service.ConfigurationPersistenceService;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.configuration.util.helpers.LabelKey;
import org.mifos.application.master.business.LookUpLabelEntity;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.business.MifosLookUpEntity;
import org.mifos.application.master.business.SupportedLocalesEntity;

/**
 * This class encapsulate all the configuration related funtionality in system
 * e.g. Label configuration
 */

public class MifosConfiguration implements ConfigurationIntf {

	private Map<LabelKey, String> labelCache;

	private Map<String, Short> loacleIdCache;

	private boolean centerHierarchyExist;

	private static final MifosConfiguration configuration = new MifosConfiguration();

	public static MifosConfiguration getInstance() {
		return configuration;
	}

	private MifosConfiguration() {
		labelCache = new ConcurrentHashMap<LabelKey, String>();
		loacleIdCache = new ConcurrentHashMap<String, Short>();
	}

	public boolean isCenterHierarchyExist() {
		return centerHierarchyExist;
	}

	public void setCenterHierarchyExist(boolean centerHierarchyExist) {
		this.centerHierarchyExist = centerHierarchyExist;
	}

	public Short getMFILocaleId() {
		// TODO remove later
		return Short.valueOf("1");
	}

	public void init() {
		initializeLabelCahe();
		initializeloacleIdCache();

	}

	private void initializeLabelCahe() {
		ConfigurationPersistenceService configurationPersistenceService = new ConfigurationPersistenceService();
		List<MifosLookUpEntity> entities = configurationPersistenceService.getLookupEntities();
		for (MifosLookUpEntity entity : entities) {
			Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
			for (LookUpLabelEntity label : labels) {
				labelCache.put(new LabelKey( entity
						.getEntityType(),label.getLocaleId()), label.getLabelName());
			}
		}
		
		
		List<LookUpValueEntity> values = configurationPersistenceService.getLookupValues();
		for (LookUpValueEntity value : values) {
			Set<LookUpValueLocaleEntity> localeValues = value
					.getLookUpValueLocales();
			for (LookUpValueLocaleEntity locale : localeValues) {

				labelCache.put(new LabelKey( value
						.getLookUpName(),locale.getLocaleId()), locale.getLookUpValue());

			}
		
		}
	}

	private void initializeloacleIdCache() {
		List<SupportedLocalesEntity> locales = new ConfigurationPersistenceService().getSupportedLocale();

		for (SupportedLocalesEntity locale : locales) {

			loacleIdCache.put(locale.getLanguage().getLanguageShortName()
					.toLowerCase()
					+ "_" + locale.getCountry().getCountryShortName(), locale
					.getLocaleId());
		}
		
	}

	public Map<LabelKey, String> getLabelCache() {
		return labelCache;
	}

	public String getLabelValue(String key, Short localeId) {
		return labelCache.get(new LabelKey( key,localeId));
	}

	public Map<String, Short> getLoacleIdCache() {
		return loacleIdCache;
	}

	public String getLabel(String key, Locale locale)throws ConfigurationException {
		if(locale==null || key==null)
			throw new ConfigurationException(ConfigurationConstants.KEY_NO_MESSAGE_FOR_THIS_KEY);
		String localeKey = locale.getLanguage() + "_" + locale.getCountry();
		Short curLocaleId = getLoacleIdFromCache(localeKey);
		if(curLocaleId==null )
			throw new ConfigurationException(ConfigurationConstants.KEY_NO_MESSAGE_FOR_THIS_KEY);
		String labelText =getLabelText(key, curLocaleId);
		//todo pass  properkey
		//if ( null==labelText) throw new ConfigurationException(ConfigurationConstants.KEY_NO_MESSAGE_FOR_THIS_KEY);
		return labelText;
	}

	public String getLabelText(String key, Short localeId) {
		return getLabelValue(key, localeId);
	}

	private Short getLoacleIdFromCache(String localeKey) {
		return loacleIdCache.get(localeKey);
	}
}
