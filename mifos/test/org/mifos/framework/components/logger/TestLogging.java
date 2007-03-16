package org.mifos.framework.components.logger;

import junit.framework.TestCase;

import org.apache.log4j.Level;
import org.mifos.framework.exceptions.ResourceBundleNotFoundException;
import org.mifos.framework.util.helpers.DatabaseSetup;


public class TestLogging extends TestCase {

	@Override
	protected void setUp() throws Exception {
		DatabaseSetup.configureLogging();
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}


	public void testLogging() throws Exception {
		MifosLogger logger = MifosLogManager.getLogger("org.mifos.logger");
		logger.debug("test debug log", false, null);
		logger.info("test info log", false, null);
		logger.warn("test warn log");
		logger.warn("test warn log", false, null, new Exception());
		logger.warn("test warn log", false, null);
		logger.error("test error log", false, null);
		logger.warn("test warn log", false, null, new Exception());
		logger.fatal("test fatal log", false, null);
		logger.fatal("test fatal log");
		logger.fatal("test fatal log", false, null, new Exception());
	}

	public void testInvalidResourceBundle() throws Exception {
		try {
			MifosLogManager.getResourceBundle("unavailableResourceBundle");
			fail();
		} catch (ResourceBundleNotFoundException ex) {
		}
	}
	
	public void testNonKey() throws Exception {
		TestLogger logger = new TestLogger();
		logger.debug("test debug message", false, null);
		assertEquals(1, logger.nonKeyCount());
		assertEquals(Level.DEBUG, logger.nonKeyLevel(0));
		assertEquals("test debug message " +
			"Logged in user is test-user  from test-office", 
			logger.nonKeyMessage(0));
	}

}
