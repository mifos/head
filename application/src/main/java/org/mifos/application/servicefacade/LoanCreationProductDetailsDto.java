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

import java.util.Date;
import java.util.List;

import org.mifos.accounts.loan.struts.action.LoanCreationGlimDto;
import org.mifos.accounts.loan.util.helpers.LoanAccountDetailsDto;
import org.mifos.accounts.productdefinition.util.helpers.PrdOfferingDto;
import org.mifos.customers.util.helpers.CustomerDetailDto;

public class LoanCreationProductDetailsDto {

    private final List<PrdOfferingDto> loanProductDtos;
    private final CustomerDetailDto customerDetailDto;
    private final Date nextMeetingDate;
    private final boolean isGroup;
    private final boolean isGlimEnabled;
    private final LoanCreationGlimDto loanCreationGlimDto;
    private final List<LoanAccountDetailsDto> clientDetails;
    private final String recurMonth;

    public LoanCreationProductDetailsDto(List<PrdOfferingDto> loanProductDtos, CustomerDetailDto customerDetailDto,
            Date nextMeetingDate, String recurMonth, boolean isGroup, boolean isGlimEnabled, LoanCreationGlimDto loanCreationGlimDto, List<LoanAccountDetailsDto> clientDetails) {
        this.loanProductDtos = loanProductDtos;
        this.customerDetailDto = customerDetailDto;
        this.nextMeetingDate = nextMeetingDate;
        this.recurMonth = recurMonth;
        this.isGroup = isGroup;
        this.isGlimEnabled = isGlimEnabled;
        this.loanCreationGlimDto = loanCreationGlimDto;
        this.clientDetails = clientDetails;
    }

    public List<PrdOfferingDto> getLoanProductDtos() {
        return this.loanProductDtos;
    }

    public CustomerDetailDto getCustomerDetailDto() {
        return this.customerDetailDto;
    }

    public Date getNextMeetingDate() {
        return this.nextMeetingDate;
    }

    public boolean isGroup() {
        return this.isGroup;
    }

    public boolean isGlimEnabled() {
        return this.isGlimEnabled;
    }

    public LoanCreationGlimDto getLoanCreationGlimDto() {
        return this.loanCreationGlimDto;
    }

    public List<LoanAccountDetailsDto> getClientDetails() {
        return this.clientDetails;
    }

    public String getRecurMonth() {
        return this.recurMonth;
    }
}