package org.mifos.application.admin.struts.action;

import java.util.Arrays;

import junit.framework.TestCase;


public class ViewConfigurationSettingsUIBeanTest extends TestCase {

	public void testCompareTo() throws Exception {
		assertTrue(new ViewConfigurationSettingsUIBean("key1", "value1",
				"This is UI for Key3")
				.compareTo(new ViewConfigurationSettingsUIBean("key3",
						"value3", "key2")) < 0);
	}
}
