/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.dto.screen;

import java.util.List;

import org.mifos.dto.domain.MeetingDto;
import org.mifos.dto.domain.ValueListElement;

@SuppressWarnings("PMD")
public class LoanCreationLoanDetailsDto {

    private final boolean isRepaymentIndependentOfMeetingEnabled;
    private final MeetingDto loanOfferingMeetingDetail;
    private final MeetingDto customerMeetingDetail;
    private final List<ValueListElement> loanPurposes;

    public LoanCreationLoanDetailsDto(boolean isRepaymentIndependentOfMeetingEnabled,
            MeetingDto loanOfferingMeetingDetail, MeetingDto customerMeetingDetail,
            List<ValueListElement> loanPurposes) {
        this.isRepaymentIndependentOfMeetingEnabled = isRepaymentIndependentOfMeetingEnabled;
        this.loanOfferingMeetingDetail = loanOfferingMeetingDetail;
        this.customerMeetingDetail = customerMeetingDetail;
        this.loanPurposes = loanPurposes;
    }

    public boolean isRepaymentIndependentOfMeetingEnabled() {
        return this.isRepaymentIndependentOfMeetingEnabled;
    }

    public MeetingDto getLoanOfferingMeetingDetail() {
        return this.loanOfferingMeetingDetail;
    }

    public MeetingDto getCustomerMeetingDetail() {
        return this.customerMeetingDetail;
    }

    public List<ValueListElement> getLoanPurposes() {
        return this.loanPurposes;
    }
}