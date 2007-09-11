package org.mifos.config;

import static org.junit.Assert.assertEquals;

import java.util.NoSuchElementException;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.util.helpers.FilePaths;

import junit.framework.JUnit4TestAdapter;

public class TestConfigurationManager {

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestConfigurationManager.class);
	}
	
	Configuration configuration;
	
	private static final String testValue = "Just Testing";
	private static final String badKey = "Bad Key";
	
	@BeforeClass
	public static void init() throws Exception {
		MifosLogManager.configure(FilePaths.LOGFILE);
	}
	
	@Before
	public void before() {
		configuration = ConfigurationManager.getInstance().getConfiguration();
	}
	@Test
	public void testGetProperty() {
		assertEquals(testValue, configuration.getString(ConfigurationManager.TestKey));
	}
	
	@Test(expected=NoSuchElementException.class)
	public void testGetUndefinedProperty() {
		configuration.getShort(badKey);
	}
}
