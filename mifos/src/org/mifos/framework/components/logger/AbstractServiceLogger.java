package org.mifos.framework.components.logger;


public abstract class AbstractServiceLogger implements ServiceLogger {

	private final MifosLogger logger;

	public AbstractServiceLogger() {
		this(MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER));
	}

	public AbstractServiceLogger(MifosLogger logger) {
		this.logger = logger;
	}
	
	public void info(String message) {
		logger.info(message);
	}
}
