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

import org.joda.time.LocalDate;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.PrdOfferingDto;
import org.mifos.dto.domain.SavingsAccountClosureDto;
import org.mifos.dto.domain.SavingsAccountCreationDto;
import org.mifos.dto.domain.SavingsAccountDetailDto;
import org.mifos.dto.domain.SavingsAccountStatusDto;
import org.mifos.dto.domain.SavingsAccountUpdateStatus;
import org.mifos.dto.domain.SavingsAdjustmentDto;
import org.mifos.dto.domain.SavingsDepositDto;
import org.mifos.dto.domain.SavingsStatusChangeHistoryDto;
import org.mifos.dto.domain.SavingsWithdrawalDto;
import org.mifos.dto.screen.DepositWithdrawalReferenceDto;
import org.mifos.dto.screen.SavingsAccountDepositDueDto;
import org.mifos.dto.screen.SavingsAdjustmentReferenceDto;
import org.mifos.dto.screen.SavingsProductReferenceDto;
import org.mifos.dto.screen.SavingsRecentActivityDto;
import org.mifos.dto.screen.SavingsTransactionHistoryDto;
import org.springframework.security.access.prepost.PreAuthorize;

public interface SavingsServiceFacade {

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_MAKE_SAVINGS_DEPOSIT_OR_WITHDRAWAL')")
    void deposit(SavingsDepositDto savingsDeposit);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_MAKE_SAVINGS_DEPOSIT_OR_WITHDRAWAL')")
    void withdraw(SavingsWithdrawalDto savingsWithdrawal);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_ADJUST_SAVINGS_DEPOSIT_OR_WITHDRAWAL')")
    SavingsAdjustmentReferenceDto retrieveAdjustmentReferenceData(Long savingsId, Short localeId);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_ADJUST_SAVINGS_DEPOSIT_OR_WITHDRAWAL')")
    void adjustTransaction(SavingsAdjustmentDto savingsAdjustment);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_CLOSE_SAVINGS_ACCOUNT')")
    SavingsAccountClosureDto retrieveClosingDetails(Long savingsId, LocalDate closureDate, Short localeId);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_CLOSE_SAVINGS_ACCOUNT')")
    void closeSavingsAccount(Long savingsId, String notes, SavingsWithdrawalDto closeAccount);

    @PreAuthorize("isFullyAuthenticated()")
    void postInterestForLastPostingPeriod(LocalDate dateOfBatchJob);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_MAKE_SAVINGS_DEPOSIT_OR_WITHDRAWAL')")
    DepositWithdrawalReferenceDto retrieveDepositWithdrawalReferenceData(Long savingsId, Integer customerId, Short localeId);

    @PreAuthorize("isFullyAuthenticated()")
    List<PrdOfferingDto> retrieveApplicableSavingsProductsForCustomer(Integer customerId);

    @PreAuthorize("isFullyAuthenticated()")
    SavingsProductReferenceDto retrieveSavingsProductReferenceData(Integer productId);

    @PreAuthorize("isFullyAuthenticated()")
    Long createSavingsAccount(SavingsAccountCreationDto savingsAccountCreation);

    @PreAuthorize("isFullyAuthenticated()")
    SavingsAccountStatusDto retrieveAccountStatuses(Long savingsId, Short localeId);

    @PreAuthorize("isFullyAuthenticated()")
    void updateSavingsAccountStatus(SavingsAccountUpdateStatus updateStatus, Short localeId);

    @PreAuthorize("isFullyAuthenticated()")
    SavingsAccountDepositDueDto retrieveDepositDueDetails(String savingsSystemId, Short localeId);

    @PreAuthorize("isFullyAuthenticated()")
    List<SavingsRecentActivityDto> retrieveRecentSavingsActivities(Long savingsId, Short localeId);

    @PreAuthorize("isFullyAuthenticated()")
    List<SavingsTransactionHistoryDto> retrieveTransactionHistory(String globalAccountNum, Short localeId);

    @PreAuthorize("isFullyAuthenticated()")
    List<SavingsStatusChangeHistoryDto> retrieveStatusChangeHistory(String globalAccountNum, Short localeId);

    @PreAuthorize("isFullyAuthenticated()")
    List<CustomFieldDto> retrieveCustomFieldsForEdit(String globalAccountNum, Short localeId);

    @PreAuthorize("isFullyAuthenticated()")
    void updateSavingsAccountDetails(Long savingsId, String recommendedAmount, List<CustomFieldDto> accountCustomFieldSet);

    @PreAuthorize("isFullyAuthenticated()")
    SavingsAccountDetailDto retrieveSavingsAccountDetails(Long savingsId);

    void waiveNextDepositAmountDue(Long savingsId);
}