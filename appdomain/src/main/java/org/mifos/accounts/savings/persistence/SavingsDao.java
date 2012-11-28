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
package org.mifos.accounts.savings.persistence;

import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.accounts.savings.interest.EndOfDayDetail;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.servicefacade.CollectionSheetCustomerSavingDto;
import org.mifos.application.servicefacade.CollectionSheetCustomerSavingsAccountDto;
import org.mifos.application.servicefacade.CustomerHierarchyParams;
import org.mifos.dto.domain.NoteSearchDto;
import org.mifos.dto.screen.NotesSearchResultsDto;

/**
 *
 */
public interface SavingsDao {

    SavingsBO findById(Long savingsId);

    SavingsBO findById(Integer savingsId);

    void save(SavingsBO savingsAccount);

    List<CollectionSheetCustomerSavingDto> findAllMandatorySavingAccountsForClientsOrGroupsWithCompleteGroupStatusForCustomerHierarchy(
            CustomerHierarchyParams customerHierarchyParams);

    List<CollectionSheetCustomerSavingDto> findAllVoluntarySavingAccountsForClientsAndGroupsWithCompleteGroupStatusForCustomerHierarchy(
            CustomerHierarchyParams customerHierarchyParams);

    List<CollectionSheetCustomerSavingDto> findAllMandatorySavingAccountsForIndividualChildrenOfCentersOrGroupsWithPerIndividualStatusForCustomerHierarchy(
            CustomerHierarchyParams customerHierarchyParams);

    List<CollectionSheetCustomerSavingDto> findAllVoluntarySavingAccountsForIndividualChildrenOfCentersOrGroupsWithPerIndividualStatusForCustomerHierarchy(
            CustomerHierarchyParams customerHierarchyParams);

    List<CollectionSheetCustomerSavingsAccountDto> findAllSavingAccountsForCustomerHierarchy(
            CustomerHierarchyParams customerHierarchyParams);

    List<EndOfDayDetail> retrieveAllEndOfDayDetailsFor(MifosCurrency currency, Long savingsId);

    List<Integer> retrieveAllActiveAndInActiveSavingsAccountsPendingInterestPostingOn(LocalDate interestPostingDate);

    List<SavingsScheduleEntity> retrieveAllCustomerSchedules(Integer savingAccountId, Integer customerId);

    void updateSavingScheduleEntity(List<SavingsScheduleEntity> savingScheduleList);
    
    SavingsBO findBySystemId(String globalAccountNum);

    NotesSearchResultsDto searchNotes(NoteSearchDto noteSearch);

    void save(List<SavingsBO> savingsAccounts);
    
    void prepareForInterestRecalculation(SavingsBO savingsAccount, Date fromDate);
}