/**
 * EMIGenerator.java version:1.0
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

import java.util.ArrayList;
import java.util.List;

import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.util.helpers.Money;

/**
 *
 *  This class generates the emi
 */
public class EMIGenerator
{

	public List<EMIInstallment> generateEMI(EMIInputs emiInputs) throws EMIGenerationException
	{
		validate(emiInputs);


		boolean isInterestDeductedAtDisburesement = emiInputs.getIsInterestDedecutedAtDisburesement();
		boolean isPrincipalInLastPayment = emiInputs.getIsPrincipalInLastPayment();

		if(isInterestDeductedAtDisburesement && !isPrincipalInLastPayment)
		 	return interestDeductedAtDisbursement(emiInputs);

		if(isPrincipalInLastPayment && !isInterestDeductedAtDisburesement)
		 	return principalInLastPayment(emiInputs);

		if(!isPrincipalInLastPayment && !isInterestDeductedAtDisburesement)
		 	return allInstallments(emiInputs);

		if(isPrincipalInLastPayment && isInterestDeductedAtDisburesement)
		 	return interestDeductedFirstPrincipalLast(emiInputs);


		throw new EMIGenerationException(RepaymentScheduleConstansts.NOT_SUPPORTED_EMI_GENERATION);


	}

	private List<EMIInstallment> interestDeductedAtDisbursement(EMIInputs emiInputs) throws EMIGenerationException
	{
		List emiInstallments = new ArrayList();

		int graceType = emiInputs.getGraceType();

		// grace can only be  none
		if(graceType == GraceConstants.GRACE_ALLREPAYMENTS || graceType == GraceConstants.GRACE_PRINCIPAL)
			throw new EMIGenerationException(RepaymentScheduleConstansts.INTERESTDEDUCTED_INVALIDGRACETYPE);

		if(graceType == GraceConstants.GRACE_NONE)
		{
			Money interestFirstInstallment = emiInputs.getLoanInterest();
			// principal starts only from the second installment
			Money principalPerInstallment = new Money(Configuration.getInstance().getSystemConfig().getCurrency(),emiInputs.getPrincipal().getAmountDoubleValue()/(emiInputs.getNoOfInstallments() - 1));

			EMIInstallment installment = new EMIInstallment();
			installment.setPrincipal(new Money());
			installment.setInterest(interestFirstInstallment);

			emiInstallments.add(installment);

			for(int i =1; i < emiInputs.getNoOfInstallments()  ; i++)
			{
				installment = new EMIInstallment();
				installment.setPrincipal(principalPerInstallment);
				installment.setInterest(new Money());

				emiInstallments.add(installment);
			}
		}

		return emiInstallments;

	}


	private List<EMIInstallment> principalInLastPayment(EMIInputs emiInputs) throws EMIGenerationException
	{
		List emiInstallments = new ArrayList();

		int graceType = emiInputs.getGraceType();

		// grace can only be  none
		if(graceType == GraceConstants.GRACE_PRINCIPAL)
			throw new EMIGenerationException(RepaymentScheduleConstansts.PRINCIPALLASTPAYMENT_INVALIDGRACETYPE);

		if(graceType == GraceConstants.GRACE_NONE || graceType == GraceConstants.GRACE_ALLREPAYMENTS)
		{
			Money principalLastInstallment = emiInputs.getPrincipal();
			// principal starts only from the second installment
			Money interestPerInstallment = new Money(Configuration.getInstance().getSystemConfig().getCurrency(),emiInputs.getLoanInterest().getAmountDoubleValue()/emiInputs.getNoOfInstallments());


			EMIInstallment  installment = null;

			for(int i =0; i < emiInputs.getNoOfInstallments() -1 ; i++)
			{
				installment = new EMIInstallment();
				installment.setPrincipal(new Money());
				installment.setInterest(interestPerInstallment);

				emiInstallments.add(installment);
			}

			// principal set in the last installment
			installment = new EMIInstallment();

			installment.setPrincipal(principalLastInstallment);
			installment.setInterest(interestPerInstallment);

			emiInstallments.add(installment);

			return emiInstallments;
		}

		throw new EMIGenerationException(RepaymentScheduleConstansts.NOT_SUPPORTED_GRACE_TYPE);


}

	private List<EMIInstallment> allInstallments(EMIInputs emiInputs) throws EMIGenerationException
	{
		List emiInstallments = new ArrayList();

		int graceType = emiInputs.getGraceType();


		if(graceType == GraceConstants.GRACE_ALLREPAYMENTS || graceType == GraceConstants.GRACE_NONE)
		{
				Money principalPerInstallment = new Money(Configuration.getInstance().getSystemConfig().getCurrency(),emiInputs.getPrincipal().getAmountDoubleValue()/emiInputs.getNoOfInstallments());
				Money interestPerInstallment = new Money(Configuration.getInstance().getSystemConfig().getCurrency(),emiInputs.getLoanInterest().getAmountDoubleValue()/emiInputs.getNoOfInstallments());

			EMIInstallment  installment = null;

			for(int i = 0; i < emiInputs.getNoOfInstallments()  ; i++)
			{
				installment = new EMIInstallment();
				installment.setPrincipal(principalPerInstallment);
				installment.setInterest(interestPerInstallment);

				emiInstallments.add(installment);
			}

			return emiInstallments;


		}

		if(graceType == GraceConstants.GRACE_PRINCIPAL)
		{
				Money principalPerInstallment = new Money(Configuration.getInstance().getSystemConfig().getCurrency(),emiInputs.getPrincipal().getAmountDoubleValue()/(emiInputs.getNoOfInstallments() - emiInputs.getGracePeriod()));
				Money interestPerInstallment = new Money(Configuration.getInstance().getSystemConfig().getCurrency(),emiInputs.getLoanInterest().getAmountDoubleValue()/emiInputs.getNoOfInstallments());

			EMIInstallment  installment = null;

			for(int i = 0; i < emiInputs.getGracePeriod()  ; i++)
			{
				installment = new EMIInstallment();
				installment.setPrincipal(new Money());
				installment.setInterest(interestPerInstallment);

				emiInstallments.add(installment);
			}

			for(int i = emiInputs.getGracePeriod(); i < emiInputs.getNoOfInstallments(); i++)
			{
				installment = new EMIInstallment();
				installment.setPrincipal(principalPerInstallment);
				installment.setInterest(interestPerInstallment);

				emiInstallments.add(installment);
			}


			return emiInstallments;


		}

		throw new EMIGenerationException(RepaymentScheduleConstansts.NOT_SUPPORTED_GRACE_TYPE);


	}


	private List<EMIInstallment> interestDeductedFirstPrincipalLast(EMIInputs emiInputs) throws EMIGenerationException
	{
	    List emiInstallments = new ArrayList();

		int graceType = emiInputs.getGraceType();

		if(graceType == GraceConstants.GRACE_ALLREPAYMENTS || graceType == GraceConstants.GRACE_PRINCIPAL)
			throw new EMIGenerationException(RepaymentScheduleConstansts.INTERESTDEDUCTED_PRINCIPALLAST);

		if(graceType == GraceConstants.GRACE_NONE)
		{

			Money principalLastInstallment = emiInputs.getPrincipal();
			Money interestFirstInstallment = emiInputs.getLoanInterest();

			EMIInstallment  installment = null;
			installment = new EMIInstallment();
			installment.setPrincipal(new Money());
			installment.setInterest(interestFirstInstallment);

			emiInstallments.add(installment);

			for(int i = 1; i < emiInputs.getNoOfInstallments() -1  ; i++)
			{
				installment = new EMIInstallment();
				installment.setPrincipal(new Money());
				installment.setInterest(new Money());

				emiInstallments.add(installment);
			 }

			installment = new EMIInstallment();
			installment.setPrincipal(principalLastInstallment);
			installment.setInterest(new Money());

			emiInstallments.add(installment);

			return emiInstallments;
		}

		throw new EMIGenerationException(RepaymentScheduleConstansts.NOT_SUPPORTED_GRACE_TYPE);
	}






	private void validate(EMIInputs emiInputs) throws EMIGenerationException
	{


		int graceType = emiInputs.getGraceType();
		int gracePeriodInInstallments = emiInputs.getGracePeriod();
		boolean isInterestDeductedAtDisburesement = emiInputs.getIsInterestDedecutedAtDisburesement();
		boolean isPrincipalInLastPayment = emiInputs.getIsPrincipalInLastPayment();
		Money principal = emiInputs.getPrincipal();
		int noOfInstallments = emiInputs.getNoOfInstallments();
		Money loanInterest = emiInputs.getLoanInterest();

		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).info("EMIGENERATOR:EMI inputs ");

	    MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).info("EMIGENERATOR:EMI inputs grace type..."+graceType);
		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).info("EMIGENERATOR:EMI inputs gracePeriodInInstallments..."+gracePeriodInInstallments);
		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).info("EMIGENERATOR:EMI inputs isInterestDeducted..."+isInterestDeductedAtDisburesement);
		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).info("EMIGENERATOR:EMI inputs isPrincipalInLastPayment..."+isPrincipalInLastPayment);
		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).info("EMIGENERATOR:EMI inputs principal..."+principal);
		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).info("EMIGENERATOR:EMI inputs noOfInstallments..."+noOfInstallments);
		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).info("EMIGENERATOR:EMI inputs loanInterest..."+loanInterest);
	}
}
