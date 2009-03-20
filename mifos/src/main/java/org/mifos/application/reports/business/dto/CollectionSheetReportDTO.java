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
 
package org.mifos.application.reports.business.dto;

import static org.mifos.application.reports.util.helpers.ReportUtils.toDisplayDate;

import java.util.Date;

import org.mifos.application.collectionsheet.business.CollSheetLnDetailsEntity;
import org.mifos.application.collectionsheet.business.CollSheetSavingsDetailsEntity;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.framework.util.helpers.NumberUtils;

// Understands collection sheet report properties
public class CollectionSheetReportDTO {
	private final Integer customerId;
	private final Integer groupId;
	private final Integer centerId;
	private final Integer loanOfficerId;
	private final String customerName;
	private final String branchName;
	private final String centerName;
	private final String groupName;
	private final String loanOfficerName;
	private final LoanProductDetails[] loanProducts;
	private SavingsProductDetails[] savingsProducts;
	private Date meetingDate;

	public CollectionSheetReportDTO(
			CollSheetLnDetailsEntity loanDetailsEntity1,
			CollSheetLnDetailsEntity loanDetailsEntity2,
			CollSheetSavingsDetailsEntity mm1SavingsDetailEntity,
			CollSheetSavingsDetailsEntity mm2SavingsDetailEntity,
			Integer customerId, Integer groupId, String customerName,
			String branchName, String groupName, CustomerBO center,
			String loanOfferingShortName1, String loanOfferingShortName2,
			String savingsOfferingShortName1, String savingsOfferingShortName2, Date meetingDate) {

		this.meetingDate = meetingDate;
		loanProducts = new LoanProductDetails[2];
		loanProducts[0] = new LoanProductDetails(loanDetailsEntity1, loanOfferingShortName1);
		loanProducts[1] = new LoanProductDetails(loanDetailsEntity2, loanOfferingShortName2);

		this.customerId = customerId;
		this.groupId = groupId;
		this.loanOfficerId = NumberUtils.convertShortToInteger(center
				.getPersonnel().getPersonnelId());
		this.loanOfficerName = center.getPersonnel().getDisplayName();
		this.customerName = customerName;
		centerId = center.getCustomerId();
		centerName = center.getDisplayName();
		this.branchName = branchName;
		this.groupName = groupName;

		savingsProducts = new SavingsProductDetails[2];
		savingsProducts[0] = new SavingsProductDetails(mm1SavingsDetailEntity, savingsOfferingShortName1);
		savingsProducts[1] = new SavingsProductDetails(mm2SavingsDetailEntity, savingsOfferingShortName2);
	}

	public SavingsProductDetails getSavingsProduct(int index) {
		return savingsProducts[index];
	}

	public LoanProductDetails getLoanProduct(int index) {
		return loanProducts[index];
	}


	public String getBranchName() {
		return branchName;
	}

	public Integer getCenterId() {
		return centerId;
	}

	public String getCenterName() {
		return centerName;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public Integer getLoanOfficerId() {
		return loanOfficerId;
	}

	public String getLoanOfficerName() {
		return loanOfficerName;
	}
	
	public String getMeetingDate() {
		return toDisplayDate(meetingDate);
	}	

	public Double getOtherFee() {
		return loanProducts[0].getOtherFee() + loanProducts[1].getOtherFee();
	}

	public Double getOtherFine() {
		return loanProducts[0].getOtherFine() + loanProducts[1].getOtherFine();
	}

}
