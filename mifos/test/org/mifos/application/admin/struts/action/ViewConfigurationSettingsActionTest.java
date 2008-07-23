package org.mifos.application.admin.struts.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Map.Entry;

import org.mifos.application.configuration.business.service.ConfigurationBusinessService;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;


public class ViewConfigurationSettingsActionTest extends
		MifosMockStrutsTestCase {


	private static final ViewConfigurationSettingsAction VIEW_CONFIGURATION_SETTINGS_ACTION = new ViewConfigurationSettingsAction();
	private static final String LOAN_INDIVIDUAL_MONITORING_ENABLED = "loanIndividualMonitoringIsEnabled";
	private static final String activityContext = "ActivityContext";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		UserContext userContext = TestUtils.makeUser();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);

		//copied from ViewOrganizationSettingsAction
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute(activityContext, ac);
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
	}

	public void testGetsAppropriateBusinessService() throws Exception {
		try {
			assertEquals(ConfigurationBusinessService.class,
					VIEW_CONFIGURATION_SETTINGS_ACTION.getService().getClass());
		}
		catch (ServiceException e) {
			e.printStackTrace();
			fail("Error while getting service");
		}
	}


	public void testLoadsAllConfigurationKeyValuePairsIntoProperties()
			throws Exception {
		Map<String, String> configProperties = VIEW_CONFIGURATION_SETTINGS_ACTION
				.getConfigurationAsProperties();
		assertTrue(configProperties
				.containsKey(LOAN_INDIVIDUAL_MONITORING_ENABLED));
	}


	public void testLoadsDefaultKeyIfKeyNotFoundInResourceBundle()
			throws Exception {
		HashMap<String, String> configMap = new HashMap<String, String>();
		configMap.put("key1", "value1");
		configMap.put("key2", "value2");
		configMap.put("key3", "value3");
		Set<Entry<String, String>> configEntries = configMap.entrySet();

		Map<String, String> keyValuePairsFromBundle = new HashMap<String, String>();
		keyValuePairsFromBundle.put("key1", "This is UI for Key1");
		keyValuePairsFromBundle.put("key3", "This is UI for Key3");

		List<ViewConfigurationSettingsUIBean> expected = Arrays.asList(
				new ViewConfigurationSettingsUIBean("key1", "value1",
						"This is UI for Key1"),
				new ViewConfigurationSettingsUIBean("key3", "value3",
						"This is UI for Key3"),
				new ViewConfigurationSettingsUIBean("key2", "value2", "key2"));

		assertEquals(expected, VIEW_CONFIGURATION_SETTINGS_ACTION
				.convertConfigToUIBean(configEntries, keyValuePairsFromBundle));
	}

	public void testGet() throws Exception {
		setRequestPathInfo("/viewConfigurationSettingsAction");
		addRequestParameter("method", "get");
		actionPerform();
		verifyNoActionErrors();
		verifyForward(ActionForwards.load_success.toString());
	}


}
