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

import org.mifos.dto.domain.AccountStatusDto;
import org.mifos.dto.domain.AccountUpdateStatus;
import org.mifos.dto.domain.CreateAccountNote;
import org.mifos.dto.domain.LoanAccountDetailsDto;
import org.mifos.dto.domain.LoanActivityDto;
import org.mifos.dto.domain.LoanInstallmentDetailsDto;
import org.mifos.dto.domain.LoanPaymentDto;
import org.mifos.dto.screen.LoanAccountDetailDto;
import org.mifos.dto.screen.LoanAccountInfoDto;
import org.mifos.dto.screen.LoanAccountMeetingDto;
import org.mifos.dto.screen.LoanCreationLoanDetailsDto;
import org.mifos.dto.screen.LoanCreationPreviewDto;
import org.mifos.dto.screen.LoanCreationProductDetailsDto;
import org.mifos.dto.screen.LoanCreationResultDto;
import org.mifos.dto.screen.LoanDisbursalDto;
import org.mifos.dto.screen.LoanInformationDto;
import org.mifos.dto.screen.LoanScheduledInstallmentDto;
import org.mifos.dto.screen.RepayLoanDto;
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

    @PreAuthorize("isFullyAuthenticated()")
    LoanCreationPreviewDto previewLoanCreationDetails(Integer customerId, List<LoanAccountDetailsDto> accountDetails, List<String> selectedClientIds);

    @PreAuthorize("isFullyAuthenticated()")
    LoanCreationResultDto createLoan(LoanAccountMeetingDto loanAccountMeetingDto, LoanAccountInfoDto loanAccountInfoDto, List<LoanScheduledInstallmentDto> loanRepayments);

    @PreAuthorize("isFullyAuthenticated()")
    LoanCreationResultDto redoLoan(LoanAccountMeetingDto loanAccountMeetingDto, LoanAccountInfoDto loanAccountInfoDto, List<LoanPaymentDto> existingLoanPayments);

    @PreAuthorize("isFullyAuthenticated()")
    void checkIfProductsOfferingCanCoexist(Integer loanAccountId);

    @PreAuthorize("isFullyAuthenticated()")
    LoanDisbursalDto retrieveLoanDisbursalDetails(Integer loanAccountId);

    @PreAuthorize("isFullyAuthenticated()")
    List<LoanActivityDto> retrieveAllLoanAccountActivities(String globalAccountNum);

    @PreAuthorize("isFullyAuthenticated()")
    LoanInstallmentDetailsDto retrieveInstallmentDetails(Integer accountId);

    @PreAuthorize("isFullyAuthenticated()")
    boolean isTrxnDateValid(Integer loanAccountId, Date trxnDate);

    @PreAuthorize("isFullyAuthenticated()")
    void makeEarlyRepayment(String globalAccountNum, String earlyRepayAmount, String receiptNumber, java.sql.Date receiptDate, String paymentTypeId, Short id, boolean waiveInterest);

    @PreAuthorize("isFullyAuthenticated()")
    LoanInformationDto retrieveLoanInformation(String globalAccountNum);

    @PreAuthorize("isFullyAuthenticated()")
    RepayLoanDto retrieveLoanRepaymentDetails(String globalAccountNum);

    @PreAuthorize("isFullyAuthenticated()")
    List<LoanAccountDetailsDto> retrieveLoanAccountDetails(LoanInformationDto loanInformationDto);
}