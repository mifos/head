/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.test.acceptance.framework.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class SeleniumSpeedControl {

    private static final Log LOG = LogFactory.getLog(SeleniumSpeedControl.class);

    @SuppressWarnings("PMD.ImmutableField") // making this final generates a compile error in the constructor
	private int delay = 0;

	public SeleniumSpeedControl() {
		String delayString = System.getProperty("mifos.selenium.delay", "0");
		delay = Integer.parseInt(delayString);
		LOG.info("Delay after each Selenium method call: " + delay + "ms");
	}

	@After("bean(selenium) && execution(public * *(..))")
	public void after(JoinPoint joinPoint) throws Throwable {
		if (delay > 0) {
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				// do nothing, we woke up
			}
		}
	}
}