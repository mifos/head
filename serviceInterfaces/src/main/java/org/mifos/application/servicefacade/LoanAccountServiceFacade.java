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

import org.mifos.dto.domain.AccountStatusDto;
import org.mifos.dto.domain.AccountUpdateStatus;
import org.mifos.dto.domain.CreateAccountNote;
import org.mifos.dto.screen.LoanAccountDetailDto;
import org.mifos.dto.screen.LoanCreationLoanDetailsDto;
import org.mifos.dto.screen.LoanCreationProductDetailsDto;
import org.springframework.security.access.prepost.PreAuthorize;

public interface LoanAccountServiceFacade {

    @PreAuthorize("isFullyAuthenticated()")
    AccountStatusDto retrieveAccountStatuses(Long loanAccountId);

    @PreAuthorize("isFullyAuthenticated()")
    void updateLoanAccountStatus(AccountUpdateStatus updateStatus);

    @PreAuthorize("isFullyAuthenticated()")
    LoanAccountDetailDto retrieveLoanAccountNotes(Long loanAccountId);

    @PreAuthorize("isFullyAuthenticated()")
    void addNote(CreateAccountNote accountNote);

    @PreAuthorize("isFullyAuthenticated()")
    LoanCreationProductDetailsDto retrieveGetProductDetailsForLoanAccountCreation(Integer customerId);

    @PreAuthorize("isFullyAuthenticated()")
    LoanCreationLoanDetailsDto retrieveLoanDetailsForLoanAccountCreation(Integer customerId, Short productId);
}