package org.mifos.framework.security.activity;

import org.mifos.application.reports.util.helpers.ReportsConstants;
import org.mifos.framework.exceptions.ApplicationException;

public class ActivityGeneratorException extends ApplicationException{
	public ActivityGeneratorException()
	{
		super(ReportsConstants.ERROR_NOMOREDYNAMICACTIVITYID, (Object[])null, (String)null);
	}

	public ActivityGeneratorException(String internalMessage) {
			super(ReportsConstants.ERROR_NOMOREDYNAMICACTIVITYID, (Object[])null, internalMessage);
		}

}
