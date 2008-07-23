package org.mifos.application.admin.struts.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.configuration.business.service.ConfigurationBusinessService;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.components.configuration.business.ConfigurationKeyValueInteger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class ViewConfigurationSettingsAction extends BaseAction {

	private static final String CONFIGURATION_SETTINGS = "configSettings";

	@Override
	protected ConfigurationBusinessService getService() throws ServiceException {
		return new ConfigurationBusinessService();
	}

	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity(
				"viewConfigurationSettingsAction");
		security.allow("get", SecurityConstants.CAN_VIEW_SYSTEM_INFO);
		return security;
	}

	public Map<String, String> getConfigurationAsProperties()
			throws PersistenceException, ServiceException {
		Map<String, String> configProperties = new HashMap<String, String>();
		List<ConfigurationKeyValueInteger> configurationList = getService()
				.getConfiguration();
		for (ConfigurationKeyValueInteger configProperty : configurationList)
			configProperties.put(configProperty.getKey(), String
					.valueOf(configProperty.getValue()));
		return configProperties;
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ResourceBundle bundle = ResourceBundle.getBundle(
				FilePaths.CONFIG_KEY_VALUE_RESOURCE, getUserContext(request)
						.getPreferredLocale());
		request.setAttribute(CONFIGURATION_SETTINGS, convertConfigToUIBean(
				getConfigurationAsProperties().entrySet(),
				getKeyValuePairsFromBundle(bundle)));
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	ArrayList<ViewConfigurationSettingsUIBean> convertConfigToUIBean(
			Set<Entry<String, String>> configEntries,
			Map<String, String> keyValuePairsFromBundle) {
		ArrayList<ViewConfigurationSettingsUIBean> settings = new ArrayList<ViewConfigurationSettingsUIBean>();
		for (Entry<String, String> entry : configEntries) {
			if (keyValuePairsFromBundle.containsKey(entry.getKey())) {
				settings.add(new ViewConfigurationSettingsUIBean(
						entry.getKey(), entry.getValue(),
						keyValuePairsFromBundle.get(entry.getKey())));
			}
			else {
				settings.add(new ViewConfigurationSettingsUIBean(
						entry.getKey(), entry.getValue(), entry.getKey()));
			}
		}
		Collections.sort(settings);
		return settings;
	}

	private Map<String, String> getKeyValuePairsFromBundle(ResourceBundle bundle) {
		Enumeration<String> keys = bundle.getKeys();
		Map<String, String> map = new HashMap<String, String>();

		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			map.put(key, bundle.getString(key));
		}
		return map;
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

}
