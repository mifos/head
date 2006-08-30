/**

 * GroupPerformanceHistory.java    version: 1.0



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

package org.mifos.application.customer.group.util.helpers;

import org.mifos.framework.business.View;

/**
 * This class denotes the data related to performance history of the group.
 */

public class GroupPerformanceHistoryView extends View {

	private Integer clientCount;

	private Double lastGroupLoanAmount;

	private Double avgLoanForMember;

	private Double totalSavings;

	private Double portfolioAtRisk;

	public Double getAvgLoanForMember() {
		return avgLoanForMember;
	}

	public void setAvgLoanForMember(Double avgLoanForMember) {
		this.avgLoanForMember = avgLoanForMember;
	}

	public Integer getClientCount() {
		return clientCount;
	}

	public void setClientCount(Integer clientCount) {
		this.clientCount = clientCount;
	}

	public Double getLastGroupLoanAmount() {
		return lastGroupLoanAmount;
	}

	public void setLastGroupLoanAmount(Double lastGroupLoanAmount) {
		this.lastGroupLoanAmount = lastGroupLoanAmount;
	}

	public Double getTotalSavings() {
		return totalSavings;
	}

	public void setTotalSavings(Double totalSavings) {
		this.totalSavings = totalSavings;
	}

	public Double getPortfolioAtRisk() {
		return portfolioAtRisk;
	}

	public void setPortfolioAtRisk(Double portfolioAtRisk) {
		this.portfolioAtRisk = portfolioAtRisk;
	}
}
