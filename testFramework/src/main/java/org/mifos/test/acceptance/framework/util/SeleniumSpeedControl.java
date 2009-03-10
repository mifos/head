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