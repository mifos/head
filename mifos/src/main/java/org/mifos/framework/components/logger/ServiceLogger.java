package org.mifos.framework.components.logger;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;

public interface ServiceLogger {

	public static final ServiceLogger MUTED_LOGGER = new ServiceLogger() {
		public void endOfServiceCall(Method method, Object[] args) {
		}

		public void startOfServiceCall(Method method, Object[] args) {
		}
	};

	public static final ServiceLogger ARGS_LOGGER = new AbstractServiceLogger() {
		public void endOfServiceCall(Method method, Object[] args) {
		}

		public void startOfServiceCall(Method method, Object[] args) {
			info(method.getName() + ":" + Arrays.toString(args));
			for (Object arg : args) {
				if (arg instanceof Date) {
					info("Date" + ((Date) arg).getTime());
				}
			}
		}
	};

	public void startOfServiceCall(Method method, Object[] args);

	public void endOfServiceCall(Method method, Object[] args);
}
