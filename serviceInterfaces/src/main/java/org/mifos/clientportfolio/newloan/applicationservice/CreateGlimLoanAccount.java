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

package org.mifos.clientportfolio.newloan.applicationservice;

import java.math.BigDecimal;
import java.util.List;

public class CreateGlimLoanAccount {

    private final List<GroupMemberAccountDto> memberDetails;
    private final BigDecimal totalLoanAmount;
    private final CreateLoanAccount groupLoanAccountDetails;

    public CreateGlimLoanAccount(List<GroupMemberAccountDto> memberDetails,
            BigDecimal totalLoanAmount, CreateLoanAccount loanAccountDetails) {
        this.memberDetails = memberDetails;
        this.totalLoanAmount = totalLoanAmount;
        this.groupLoanAccountDetails = loanAccountDetails;
    }

    public List<GroupMemberAccountDto> getMemberDetails() {
        return memberDetails;
    }

    public BigDecimal getTotalLoanAmount() {
        return totalLoanAmount;
    }

    public CreateLoanAccount getGroupLoanAccountDetails() {
        return groupLoanAccountDetails;
    }
}