/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
package org.mifos.framework.components.logger;

import java.lang.reflect.Method;

import org.apache.commons.lang.time.StopWatch;

public class ServiceActivityLogger extends AbstractServiceLogger {
	protected final String serviceName;
	private StopWatch stopWatch;

	public ServiceActivityLogger() {
		this(null);
	}

	public ServiceActivityLogger(String serviceName) {
		super();
		this.serviceName = serviceName;
		stopWatch = new StopWatch();
	}

	public void startOfServiceCall(Method method, Object[] args) {
		startTiming();
		logStartActivity(method);
	}

	public void endOfServiceCall(Method method, Object[] args) {
		stopTiming();
		logEndActivity(method);
		stopWatch.reset();
	}

	private void logStartActivity(Method method) {
		info("Calling Service  : " + createStartLogMessage(method));
	}

	private void logEndActivity(Method method) {
		info("Finished Service : " + createEndLogMessage(method));
	}

	protected String createStartLogMessage(Method method) {
		return serviceInfoMsg(method);
	}

	protected String createEndLogMessage(Method method) {
		return "Time Taken " + getTiming() + createStartLogMessage(method);
	}

	private String serviceInfoMsg(Method method) {
		return "Service " + serviceName + " invoked Method " + method.getName();
	}

	// Apparently stop watch is not thread safe, will refine it if required
	private void startTiming() {
		try {
			stopWatch.start();
		}
		catch (IllegalStateException e) {

		}
	}

	private void stopTiming() {
		try {
			stopWatch.stop();
		}
		catch (IllegalStateException e) {
		}
	}

	private long getTiming() {
		long totalTimeSpent;
		try {
			totalTimeSpent = stopWatch.getTime();
		}
		catch (RuntimeException e) {
			return 0;
		}
		return totalTimeSpent;
	}
}
