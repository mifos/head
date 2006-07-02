/**
 * RepaymentSchedule.java version:1.0
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

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.mifos.framework.util.helpers.Money;
/**
 *
 *  This class is the object that holds the repayment schedule information
 */
public class RepaymentSchedule
{

	private List<RepaymentScheduleInstallment> repaymentScheduleInstallment = null;

	private Money principal= new Money();
	private Money interest = new Money();
	private Money fees = new Money();
	private Money penalty= new Money();

	/**
	 * This method returns the repayment schedule installment
	 * @return  List
	 */
	public List<RepaymentScheduleInstallment> getRepaymentScheduleInstallment()
	{
		return repaymentScheduleInstallment;
	}
/**
	 * This method sets the repayment schedule installment , used internally
	 * @param List
	 */
	public void setRepaymentScheduleInstallment(List<RepaymentScheduleInstallment> repaymentScheduleInstallment)
	{
		this.repaymentScheduleInstallment = repaymentScheduleInstallment;

	}
/**
	 * This method returns the fee repayment schedule installment
	 * @return  List
	 */
	public List<FeeInstallment> getRepaymentFeeInstallment()
	{
		if(repaymentScheduleInstallment == null)
				return null;

	   List<FeeInstallment> feeInstallmentList = new ArrayList();
	   Iterator<RepaymentScheduleInstallment> iterRepaymentScheduleInstallment  = repaymentScheduleInstallment.iterator();
	   RepaymentScheduleInstallment repaymentSch = null;
	   FeeInstallment feeInstallment = null;
	   while(iterRepaymentScheduleInstallment.hasNext())
	   {
			repaymentSch = iterRepaymentScheduleInstallment.next();
			feeInstallment = repaymentSch.getFeeInstallment();
			if(feeInstallment != null)
				feeInstallmentList.add(feeInstallment);
	   }

	   return feeInstallmentList;

	}

	public void setPrincipal(Money principal)
	{
		this.principal = principal;
	}

	public void setInterest(Money interest)
	{
		this.interest = interest;
	}

	public void setFees(Money fees)
	{
		this.fees = fees;
	}
	public Money getPrincipal()
	{
		return principal;
	}

	public Money getInterest()
	{
		return interest;
	}

	public Money getFees()
	{
		return fees;
	}
	
	public Money getPenalty() {
		return penalty;
	}
	
	public void setPenalty(Money penalty) {
		this.penalty = penalty;
	}
	
	

}
