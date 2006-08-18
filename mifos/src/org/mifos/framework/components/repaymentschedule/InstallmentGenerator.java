/**
 * InstallmentGenerator.java version:1.0
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
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 *  This class generates the installments
 */

public class InstallmentGenerator
{

	public Date getInstallmentStartDate(SchedulerIntf scheduler,RepaymentScheduleInputsIfc repaymentScheduleInputs,int gracePeriod) throws InstallmentException,SchedulerException
	{
		validate(repaymentScheduleInputs);

		int installmentSkipToStartRepayment = getInstallmentSkipToStartRepayment(repaymentScheduleInputs,gracePeriod);

		return scheduler.getSpecificScheduleDate(installmentSkipToStartRepayment+1);
	}

	public int getInstallmentSkipToStartRepayment(RepaymentScheduleInputsIfc repaymentScheduleInputs,int gracePeriod) throws InstallmentException
	{
		if(repaymentScheduleInputs.getMeetingToConsider() == RepaymentScheduleConstansts.MEETING_CUSTOMER)
            return 0;
		if(repaymentScheduleInputs.getMeetingToConsider() == RepaymentScheduleConstansts.MEETING_LOAN)
		{
            boolean isInterestDeductedAtDisburesement = repaymentScheduleInputs.getIsInterestDedecutedAtDisburesement();
            if(isInterestDeductedAtDisburesement)
            	return 0;
            else
                return gracePeriod +1 ;
		}
		return 0;
	}

	public List<InstallmentDate> getInstallmentDates(SchedulerIntf scheduler,int noOfInstallments,int meetingToConsider) throws InstallmentException,SchedulerException
	{
		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("InstallmentGenerator:getInstallmentDates no of installments.."+noOfInstallments);

		List dueDates = null;
		if(RepaymentScheduleConstansts.MEETING_LOAN == meetingToConsider)
			 dueDates = scheduler.getAllDates(noOfInstallments);
		else
		if(RepaymentScheduleConstansts.MEETING_CUSTOMER == meetingToConsider)
			 dueDates = scheduler.getAllDates();
		else
			throw new InstallmentException(RepaymentScheduleConstansts.NOT_SUPPORTED_MEETING_TYPE);

		return buildInstallmentDates(dueDates);

	}

	private List<InstallmentDate> buildInstallmentDates(List dueDates)
	{
		int installmentId = 1;
		List<InstallmentDate> installmentDates = new ArrayList();

		for(Object obj : dueDates)
		{
				installmentDates.add(new InstallmentDate(installmentId++,((Date)obj)));
		}
		return installmentDates;

	}

	private void validate(RepaymentScheduleInputsIfc repaymentScheduleInputs) throws InstallmentException
	{

	}

}
