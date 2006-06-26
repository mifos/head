/**

 * GroupPerformanceHistory.java    version: 1.0



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

package org.mifos.application.customer.group.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This class denotes the data related to performance history of the group.
 * @author navitas
 */

public class GroupPerformanceHistory extends ValueObject{
	
	/**Denotes the number of clients assigned to group */
	private Integer clientCount;
	
	/**Denotes the last group loan amount availed by group */
	private Double lastGroupLoanAmount;
	
	/**Denotes the average loan taken by individual clients of the group */
	private Double avgLoanForMember;
	
	/**Denotes the total savings of the group */
	private Double totalSavings;
	
	/**Denotes the porfolio ar risk */
	private Double portfolioAtRisk;

	/**
     * Return the value of the avgLoanForMember attribute.
     * @return Double
     */
	public Double getAvgLoanForMember() {
		return avgLoanForMember;
	}

	/**
     * Sets the value of flagId
     * @param flagId
     */
	public void setAvgLoanForMember(Double avgLoanForMember) {
		this.avgLoanForMember = avgLoanForMember;
	}
	
	/**
     * Return the value of the clientCount attribute.
     * @return Integer
     */
	public Integer getClientCount() {
		return clientCount;
	}
	
	/**
     * Sets the value of clientCount
     * @param clientCount
     */
	public void setClientCount(Integer clientCount) {
		this.clientCount = clientCount;
	}
	
	/**
     * Return the value of the lastGroupLoanAmount attribute.
     * @return Double
     */
	public Double getLastGroupLoanAmount() {
		return lastGroupLoanAmount;
	}

	/**
     * Sets the value of lastGroupLoanAmount
     * @param lastGroupLoanAmount
     */
	public void setLastGroupLoanAmount(Double lastGroupLoanAmount) {
		this.lastGroupLoanAmount = lastGroupLoanAmount;
	}
	
	/**
     * Return the value of the totalSavings attribute.
     * @return Double
     */
	public Double getTotalSavings() {
		return totalSavings;
	}

	/**
     * Sets the value of totalSavings
     * @param totalSavings
     */
	public void setTotalSavings(Double totalSavings) {
		this.totalSavings = totalSavings;
	}
	
	/**
     * Return the value of the portfolioAtRisk attribute.
     * @return Double
     */
	public Double getPortfolioAtRisk() {
		return portfolioAtRisk;
	}
	
	/**
     * Sets the value of portfolioAtRisk
     * @param portfolioAtRisk
     */
	public void setPortfolioAtRisk(Double portfolioAtRisk) {
		this.portfolioAtRisk = portfolioAtRisk;
	}
}