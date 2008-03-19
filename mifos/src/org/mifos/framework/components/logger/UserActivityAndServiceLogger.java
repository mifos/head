package org.mifos.framework.components.logger;

import java.lang.reflect.Method;

public class UserActivityAndServiceLogger extends ServiceActivityLogger {
	private final Integer userId;

	public UserActivityAndServiceLogger() {
		this(null, null);
	}

	public UserActivityAndServiceLogger(String serviceName, Integer userId) {
		super(serviceName);
		this.userId = userId;
	}

	@Override
	protected String createEndLogMessage(Method method) {
		return userInfoLogMessage() + super.createEndLogMessage(method);
	}

	@Override
	protected String createStartLogMessage(Method method) {
		return userInfoLogMessage() + super.createStartLogMessage(method);
	}

	private String userInfoLogMessage() {
		return "User Id " + userId;
	}
}
