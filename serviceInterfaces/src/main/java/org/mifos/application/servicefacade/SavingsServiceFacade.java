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

package org.mifos.application.servicefacade;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.LocalDate;
import org.mifos.dto.domain.AccountStatusDto;
import org.mifos.dto.domain.AccountUpdateStatus;
import org.mifos.dto.domain.AuditLogDto;
import org.mifos.dto.domain.CreateAccountNote;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.CustomerDto;
import org.mifos.dto.domain.CustomerSearchDto;
import org.mifos.dto.domain.CustomerSearchResultDto;
import org.mifos.dto.domain.FundTransferDto;
import org.mifos.dto.domain.NoteSearchDto;
import org.mifos.dto.domain.OpeningBalanceSavingsAccount;
import org.mifos.dto.domain.PrdOfferingDto;
import org.mifos.dto.domain.SavingsAccountClosureDto;
import org.mifos.dto.domain.SavingsAccountCreationDto;
import org.mifos.dto.domain.SavingsAccountDetailDto;
import org.mifos.dto.domain.SavingsAdjustmentDto;
import org.mifos.dto.domain.SavingsDepositDto;
import org.mifos.dto.domain.SavingsDetailDto;
import org.mifos.dto.domain.SavingsStatusChangeHistoryDto;
import org.mifos.dto.domain.SavingsWithdrawalDto;
import org.mifos.dto.screen.DepositWithdrawalReferenceDto;
import org.mifos.dto.screen.NotesSearchResultsDto;
import org.mifos.dto.screen.SavingsAccountDepositDueDto;
import org.mifos.dto.screen.SavingsAdjustmentReferenceDto;
import org.mifos.dto.screen.SavingsProductReferenceDto;
import org.mifos.dto.screen.SavingsRecentActivityDto;
import org.mifos.dto.screen.SavingsTransactionHistoryDto;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.springframework.security.access.prepost.PreAuthorize;

public interface SavingsServiceFacade {

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_MAKE_SAVINGS_DEPOSIT_OR_WITHDRAWAL')")
    void deposit(SavingsDepositDto savingsDeposit);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_MAKE_SAVINGS_DEPOSIT_OR_WITHDRAWAL')")
    void withdraw(SavingsWithdrawalDto savingsWithdrawal);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_ADJUST_SAVINGS_DEPOSIT_OR_WITHDRAWAL')")
    SavingsAdjustmentReferenceDto retrieveAdjustmentReferenceData(Long savingsId);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_ADJUST_SAVINGS_DEPOSIT_OR_WITHDRAWAL')")
    void adjustTransaction(SavingsAdjustmentDto savingsAdjustment);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_CLOSE_SAVINGS_ACCOUNT')")
    SavingsAccountClosureDto retrieveClosingDetails(Long savingsId, LocalDate closureDate);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_CLOSE_SAVINGS_ACCOUNT')")
    void closeSavingsAccount(Long savingsId, String notes, SavingsWithdrawalDto closeAccount);

    @PreAuthorize("isFullyAuthenticated()")
    void postInterestForLastPostingPeriod(LocalDate dateOfBatchJob);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_MAKE_SAVINGS_DEPOSIT_OR_WITHDRAWAL')")
    DepositWithdrawalReferenceDto retrieveDepositWithdrawalReferenceData(Long savingsId, Integer customerId);

    @PreAuthorize("isFullyAuthenticated()")
    List<PrdOfferingDto> retrieveApplicableSavingsProductsForCustomer(Integer customerId);

    @PreAuthorize("isFullyAuthenticated()")
    SavingsProductReferenceDto retrieveSavingsProductReferenceData(Integer productId);

    @PreAuthorize("isFullyAuthenticated() and hasAnyRole('ROLE_CAN_CREATE_NEW_SAVINGS_ACCOUNT_IN_SAVE_FOR_APPROVAL_STATE', 'ROLE_CAN_CREATE_NEW_SAVINGS_ACCOUNT_IN_SAVE_FOR_LATER_STATE')")
    Long createSavingsAccount(SavingsAccountCreationDto savingsAccountCreation);
    
    @PreAuthorize("isFullyAuthenticated() and hasAnyRole('ROLE_CAN_CREATE_NEW_SAVINGS_ACCOUNT_IN_SAVE_FOR_APPROVAL_STATE', 'ROLE_CAN_CREATE_NEW_SAVINGS_ACCOUNT_IN_SAVE_FOR_LATER_STATE')")
	Long createSavingsAccount(SavingsAccountCreationDto savingsAccountCreation, List<QuestionGroupDetail> questionGroups);

    @PreAuthorize("isFullyAuthenticated() and hasAnyRole('ROLE_CAN_CREATE_NEW_SAVINGS_ACCOUNT_IN_SAVE_FOR_APPROVAL_STATE', 'ROLE_CAN_CREATE_NEW_SAVINGS_ACCOUNT_IN_SAVE_FOR_LATER_STATE')")
    String createSavingsAccount(OpeningBalanceSavingsAccount openingBalanceSavingsAccount);

    @PreAuthorize("isFullyAuthenticated()")
    AccountStatusDto retrieveAccountStatuses(Long savingsId);

    @PreAuthorize("isFullyAuthenticated()")
    void updateSavingsAccountStatus(AccountUpdateStatus updateStatus);

    @PreAuthorize("isFullyAuthenticated()")
    SavingsAccountDepositDueDto retrieveDepositDueDetails(String savingsSystemId);

    @PreAuthorize("isFullyAuthenticated()")
    List<SavingsRecentActivityDto> retrieveRecentSavingsActivities(Long savingsId);

    @PreAuthorize("isFullyAuthenticated()")
    List<SavingsTransactionHistoryDto> retrieveTransactionHistory(String globalAccountNum);

    @PreAuthorize("isFullyAuthenticated()")
    List<SavingsStatusChangeHistoryDto> retrieveStatusChangeHistory(String globalAccountNum);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_EDIT_UPDATE_SAVINGS_ACCOUNT')")
    void updateSavingsAccountDetails(Long savingsId, String recommendedAmount, List<CustomFieldDto> accountCustomFieldSet);

    @PreAuthorize("isFullyAuthenticated()")
    SavingsAccountDetailDto retrieveSavingsAccountDetails(Long savingsId);
    
    @PreAuthorize("isFullyAuthenticated()")
    SavingsAccountDetailDto retrieveSavingsAccountDetails(String globalAccountNum);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_WAIVE_NEXT_SAVINGS_DEPOSIT_DUE_AMOUNT')")
    void waiveNextDepositAmountDue(Long savingsId);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_WAIVE_OVER_DUE_SAVINGS_DEPOSITS')")
    void waiveDepositAmountOverDue(Long savingsId);

    @PreAuthorize("isFullyAuthenticated()")
    List<AuditLogDto> retrieveSavingsAccountAuditLogs(Long savingsId);

    @PreAuthorize("isFullyAuthenticated()")
    NotesSearchResultsDto retrievePagedNotesDto(NoteSearchDto noteSearch);

    @PreAuthorize("isFullyAuthenticated()")
    SavingsAccountDetailDto retrieveSavingsAccountNotes(Long savingsId);

    @PreAuthorize("isFullyAuthenticated()")
    void addNote(CreateAccountNote accountNote);
    
    @PreAuthorize("isFullyAuthenticated() and hasAnyRole('ROLE_CAN_CREATE_NEW_SAVINGS_ACCOUNT_IN_SAVE_FOR_APPROVAL_STATE', 'ROLE_CAN_CREATE_NEW_SAVINGS_ACCOUNT_IN_SAVE_FOR_LATER_STATE')")
	List<CustomerSearchResultDto> retrieveCustomerThatQualifyForSavings(CustomerSearchDto customerSearchDto);

    @PreAuthorize("isFullyAuthenticated()")
    List<CustomerSearchResultDto> retrieveCustomersThatQualifyForTransfer(CustomerSearchDto customerSearchDto);

    @PreAuthorize("isFullyAuthenticated()")
	CustomerDto retreieveCustomerDetails(Integer customerId);

    @PreAuthorize("isFullyAuthenticated()")
    SavingsDetailDto retrieveSavingsDetail(String accountGlobalNum);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_TRANSFER_FUNDS')")
    void fundTransfer(FundTransferDto fundTransferDto);

    void putSavingsBusinessKeyInSession(String globalAccountNum, HttpServletRequest request);
}