package org.mifos.framework.components.scheduler;

import org.mifos.framework.exceptions.ApplicationException;

/**
 * @author navitas
 * Created on Aug 12, 2005
 * It is an Exception thrown by Scheduler Classes.
 */
public class SchedulerException extends ApplicationException
{
	SchedulerException(String key)
	{
		super(key);
	}
}
