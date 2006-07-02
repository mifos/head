/**
 * GracePeriodHandler.java version:1.0
 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.
 * Apache License
 * Copyright (c) 2005-2006 Grameen Foundation USA
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the
 * License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license
 * and how it is applied.
 *
 */
package org.mifos.framework.components.repaymentschedule;

import org.mifos.framework.components.scheduler.SchedulerIntf;
import org.mifos.framework.components.scheduler.SchedulerException;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
/**
 *
 *  This class gets the grace period
 */
public class GracePeriodHandler
{
	private RepaymentScheduleInputsIfc  repaymentScheduleInputs = null;

	public int getGracePeriod(GraceInputs graceInputs) throws GraceException
	{
		validate(graceInputs);

		int graceType = graceInputs.getGraceType();
		int gracePeriod = graceInputs.getGracePeriod();

		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("GracePeriodHandler:getGracePeriod graceType "+graceType);
		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("GracePeriodHandler:getGracePeriod gracePeriod "+gracePeriod);

		if(graceType == RepaymentScheduleConstansts.GRACE_NONE)
			return 0;

		if(graceType == RepaymentScheduleConstansts.GRACE_ALLREPAYMENTS )
				return gracePeriod;

	    if(graceType == RepaymentScheduleConstansts.GRACE_PRINCIPAL)
	    		return 0;

	    throw new GraceException(RepaymentScheduleConstansts.NOT_SUPPORTED_GRACE_TYPE);

	}

	private void validate(GraceInputs graceInputs) throws GraceException
	{

	}

}
