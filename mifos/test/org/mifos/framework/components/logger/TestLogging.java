package org.mifos.framework.components.logger;

import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ResourceBundleNotFoundException;


public class TestLogging extends MifosTestCase {

	@Override
	protected void setUp() throws Exception {
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
}
