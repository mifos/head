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

import org.joda.time.LocalDate;
import org.mifos.dto.domain.SavingsAccountClosureDto;
import org.mifos.dto.domain.SavingsAdjustmentDto;
import org.mifos.dto.domain.SavingsDepositDto;
import org.mifos.dto.domain.SavingsWithdrawalDto;
import org.mifos.dto.screen.DepositWithdrawalReferenceDto;
import org.mifos.dto.screen.SavingsAdjustmentReferenceDto;
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
}