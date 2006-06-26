/**
 * InterestInputs.java version:1.0
 * Copyright © 2005-2006 Grameen Foundation USA

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
package org.mifos.framework.components.interestcalculator;

import java.util.Date;

import org.mifos.framework.util.helpers.Money;
/**
 *
 *  This class holds interest inputs
 */
public class InterestInputs
{

		private Money principal = new Money();
		private double interestRate = 0;
		private int duration = 0;
		private String durationType = InterestCalculatorConstansts.WEEK_INSTALLMENT;
		Date installmentStartDate = null;
		Date installmentEndDate = null;


		public void setPrincipal(Money principal)
		{
			this.principal = principal;
		}
		public void setInterestRate(double interestRate)
		{
			this.interestRate = interestRate;
		}
		public void setDuration(int duration)
		{
			this.duration = duration;
		}
		public void setDurationType(String durationType)
		{
			this.durationType = durationType;
		}

		public Money getPrincipal()
		{
			return principal;
		}
		public double getInterestRate()
		{
			return interestRate;
		}
		public int getDuration()
		{
			return duration;
		}
		public String getDurationType()
		{
			return durationType;
		}
		public void setInstallmentStartDate(Date installmentStartDate)
		{
			this.installmentStartDate = installmentStartDate;
		}

		public void setInstallmentEndDate(Date installmentEndDate)
		{
			this.installmentEndDate = installmentEndDate;
		}
		public Date getInstallmentStartDate()
		{
			return installmentStartDate;
		}
		public Date getInstallmentEndDate()
		{
			return installmentEndDate;
		}



}
