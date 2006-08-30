package org.mifos.framework.components.scheduler;

import org.mifos.framework.exceptions.ApplicationException;

/**
 * It is an Exception thrown by Scheduler Classes.
 */
public class SchedulerException extends ApplicationException
{
	SchedulerException(String key)
	{
		super(key);
	}
}
