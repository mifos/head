/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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
 
package org.mifos.application.customer.group.business;

import static org.mifos.framework.util.helpers.NumberUtils.SHORT_ZERO;

import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.PrdOfferingBO;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.util.helpers.Transformer;

public class GroupLoanCounter {
	public static Transformer<GroupLoanCounter, Short> TRANSFORM_GROUP_LOAN_COUNTER_TO_LOAN_CYCLE = new Transformer<GroupLoanCounter, Short>() {
		public Short transform(GroupLoanCounter input) {
			return input.getLoanCycleCounter();
		}
	};

	@SuppressWarnings("unused")
	private Integer loanCounterId;

	@SuppressWarnings("unused")
	private GroupPerformanceHistoryEntity groupPeformanceHistory;

	private Short loanCycleCounter = SHORT_ZERO;

	private LoanOfferingBO loanOffering;

	public GroupLoanCounter(
			GroupPerformanceHistoryEntity groupPerformanceHistory,
			LoanOfferingBO loanOffering, YesNoFlag yesNoFlag) {
		this.groupPeformanceHistory = groupPerformanceHistory;
		this.loanOffering = loanOffering;
		updateLoanCounter(yesNoFlag);
	}

	protected GroupLoanCounter() {
	}

	public LoanOfferingBO getLoanOffering() {
		return loanOffering;
	}

	public void updateLoanCounter(YesNoFlag yesNoFlag) {
		if (yesNoFlag.yes())
			loanCycleCounter++;
		else loanCycleCounter--;
	}

	public boolean isOfSameProduct(PrdOfferingBO prdOffering) {
		return loanOffering.isOfSameOffering(prdOffering);
	}

	public Short getLoanCycleCounter() {
		return loanCycleCounter;
	}
}
