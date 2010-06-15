/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.application.servicefacade;

import java.util.List;

import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.fund.business.FundBO;
import org.mifos.accounts.productdefinition.business.LoanAmountOption;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingInstallmentRange;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldDto;
import org.mifos.application.master.business.CustomValueListElementDto;
import org.mifos.application.master.business.ValueListElement;
import org.mifos.application.meeting.business.MeetingDetailsEntity;
import org.mifos.application.meeting.util.helpers.RecurrenceType;

public class LoanCreationLoanDetailsDto {

    private final boolean isRepaymentIndependentOfMeetingEnabled;
    private final List<FeeDto> additionalFees;
    private final List<FeeDto> defaultFees;
    private final MeetingDetailsEntity loanOfferingMeetingDetail;
    private final RecurrenceType loanProductRecurrenceType;
    private final MeetingDetailsEntity customerMeetingDetail;
    private final LoanOfferingBO loanOffering;
    private final LoanAmountOption eligibleLoanAmount;
    private final LoanOfferingInstallmentRange eligibleNoOfInstall;
    private final List<CustomValueListElementDto> collateralTypes;
    private final List<ValueListElement> loanPurposes;
    private final List<CustomFieldDefinitionEntity> customFieldDefs;
    private final List<CustomFieldDto> customFields;
    private final List<FundBO> funds;

    public LoanCreationLoanDetailsDto(boolean isRepaymentIndependentOfMeetingEnabled, List<FeeDto> additionalFees,
            List<FeeDto> defaultFees, MeetingDetailsEntity loanOfferingMeetingDetail,
            RecurrenceType loanProductRecurrenceType, MeetingDetailsEntity customerMeetingDetail,
            LoanOfferingBO loanOffering, LoanAmountOption eligibleLoanAmount,
            LoanOfferingInstallmentRange eligibleNoOfInstall, List<CustomValueListElementDto> collateralTypes,
            List<ValueListElement> loanPurposes, List<CustomFieldDefinitionEntity> customFieldDefs,
            List<CustomFieldDto> customFields, List<FundBO> funds) {
        this.isRepaymentIndependentOfMeetingEnabled = isRepaymentIndependentOfMeetingEnabled;
        this.additionalFees = additionalFees;
        this.defaultFees = defaultFees;
        this.loanOfferingMeetingDetail = loanOfferingMeetingDetail;
        this.loanProductRecurrenceType = loanProductRecurrenceType;
        this.customerMeetingDetail = customerMeetingDetail;
        this.loanOffering = loanOffering;
        this.eligibleLoanAmount = eligibleLoanAmount;
        this.eligibleNoOfInstall = eligibleNoOfInstall;
        this.collateralTypes = collateralTypes;
        this.loanPurposes = loanPurposes;
        this.customFieldDefs = customFieldDefs;
        this.customFields = customFields;
        this.funds = funds;
    }

    public boolean isRepaymentIndependentOfMeetingEnabled() {
        return this.isRepaymentIndependentOfMeetingEnabled;
    }

    public List<FeeDto> getAdditionalFees() {
        return this.additionalFees;
    }

    public List<FeeDto> getDefaultFees() {
        return this.defaultFees;
    }

    public MeetingDetailsEntity getLoanOfferingMeetingDetail() {
        return this.loanOfferingMeetingDetail;
    }

    public RecurrenceType getLoanProductRecurrenceType() {
        return this.loanProductRecurrenceType;
    }

    public MeetingDetailsEntity getCustomerMeetingDetail() {
        return this.customerMeetingDetail;
    }

    public LoanOfferingBO getLoanOffering() {
        return this.loanOffering;
    }

    public LoanAmountOption getEligibleLoanAmount() {
        return this.eligibleLoanAmount;
    }

    public LoanOfferingInstallmentRange getEligibleNoOfInstall() {
        return this.eligibleNoOfInstall;
    }

    public List<CustomValueListElementDto> getCollateralTypes() {
        return this.collateralTypes;
    }

    public List<ValueListElement> getLoanPurposes() {
        return this.loanPurposes;
    }

    public List<CustomFieldDefinitionEntity> getCustomFieldDefs() {
        return this.customFieldDefs;
    }

    public List<CustomFieldDto> getCustomFields() {
        return this.customFields;
    }

    public List<FundBO> getFunds() {
        return this.funds;
    }

}
