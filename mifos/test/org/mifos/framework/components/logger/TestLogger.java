package org.mifos.framework.components.logger;

import org.apache.log4j.Level;

public class TestLogger extends MifosLogger {

	public TestLogger() {
	}

	@Override
	public String getOfficeID() {
		return "test-office";
	}

	@Override
	public String getUserID() {
		return "test-user";
	}

	@Override
	protected void logMessage(Level level, 
		String key, boolean asString, Object[] args, Throwable t) {
	}

}
