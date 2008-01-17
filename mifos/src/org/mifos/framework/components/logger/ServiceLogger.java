package org.mifos.framework.components.logger;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;

public class ServiceLogger {

	public static final ServiceLogger MUTED_LOGGER = new ServiceLogger() {
		@Override
		public void endOfServiceCall(Method method, Object[] args) {}

		@Override
		public void startOfServiceCall(Method method, Object[] args) {}
	};
	
	public static final ServiceLogger ARGS_LOGGER = new ServiceLogger() {
		@Override
		public void endOfServiceCall(Method method, Object[] args) {}

		@Override
		public void startOfServiceCall(Method method, Object[] args) {
			logger.error(method.getName()+":"+Arrays.toString(args));
			for (Object arg : args) {
				if (arg instanceof Date) {
					logger.error("Date"+((Date)arg).getTime());
				}
			}
		}
	};
	
	protected final String serviceName;
	protected MifosLogger logger;
	private long startTime;

	public ServiceLogger() {
		this(null);
	}

	public ServiceLogger(String serviceName) {
		this.serviceName = serviceName;
		logger = MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER);
	}

	public void startOfServiceCall(Method method, Object[] args) {
		logger.info("Calling Service  : " + serviceName + " "
				+ method.getName());
		startTime = System.currentTimeMillis();
	}

	public void endOfServiceCall(Method method, Object[] args) {
		logger.info("Finished Service : "
				+ (System.currentTimeMillis() - startTime) + " " + serviceName
				+ " " + method.getName());
	}
}
