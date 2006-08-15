package org.mifos.framework.components.logger;

public class TestLogger implements MifosLogger {

	public TestLogger() {
	}

	public void debug(String key, boolean asString, Object[] args) {
	}

	public void debug(String message) {
	}

	public void error(String key, boolean asString, Object[] args, Throwable t) {
	}

	public void error(String key, boolean asString, Object[] args) {
	}

	public void error(String message) {
	}

	public void fatal(String key, boolean asString, Object[] args, Throwable t) {
	}

	public void fatal(String key, boolean asString, Object[] args) {
	}

	public void fatal(String message) {
	}

	public String getOfficeID() {
		return "test-office";
	}

	public String getUserID() {
		return "test-user";
	}

	public void info(String key, boolean asString, Object[] args) {
	}

	public void info(String message) {
	}

	public void warn(String key, boolean asString, Object[] args, Throwable t) {
	}

	public void warn(String key, boolean asString, Object[] args) {
	}

	public void warn(String message) {
	}

}
