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

package org.mifos.application.collectionsheet.persistence;

import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.mifos.application.servicefacade.CollectionSheetCustomerAccountCollectionDto;
import org.mifos.application.servicefacade.CollectionSheetCustomerDto;
import org.mifos.application.servicefacade.CollectionSheetCustomerLoanDto;
import org.mifos.application.servicefacade.CollectionSheetCustomerSavingDto;
import org.mifos.application.servicefacade.CollectionSheetCustomerSavingsAccountDto;
import org.mifos.application.servicefacade.CollectionSheetFormEnteredDataDto;
import org.mifos.application.servicefacade.CollectionSheetLoanFeeDto;
import org.mifos.application.servicefacade.CustomerHierarchyParams;

/**
 *
 */
public interface CollectionSheetDao {

    List<CollectionSheetCustomerDto> findCustomerHierarchy(Integer customerId, LocalDate transactionDate);
    
    List<CollectionSheetCustomerDto> findCustomerHierarchy(CollectionSheetFormEnteredDataDto formEnteredDataDto, LocalDate transactionDate);

    Map<Integer, List<CollectionSheetCustomerLoanDto>> findAllLoanRepaymentsForCustomerHierarchy(Short branchId,
            String searchId, LocalDate transactionDate, Integer customerId);

    Map<Integer, Map<Integer, List<CollectionSheetLoanFeeDto>>> findOutstandingFeesForLoansOnCustomerHierarchy(
            Short branchId, String searchId, LocalDate transactionDate, Integer customerId);

    Map<Integer, List<CollectionSheetCustomerAccountCollectionDto>> findAccountCollectionsOnCustomerAccount(
            Short branchId, String searchId, LocalDate transactionDate, Integer customerId);

    Map<Integer, List<CollectionSheetCustomerAccountCollectionDto>> findOutstandingFeesForCustomerAccountOnCustomerHierarchy(
            Short branchId, String searchId, LocalDate transactionDate, Integer customerId);

    Map<Integer, List<CollectionSheetCustomerLoanDto>> findLoanDisbursementsForCustomerHierarchy(Short branchId,
            String searchId, LocalDate transactionDate, Integer customerId);

    Map<Integer, List<CollectionSheetCustomerSavingDto>> findSavingsDepositsforCustomerHierarchy(
            CustomerHierarchyParams customerHierarchyParams);

    Map<Integer, List<CollectionSheetCustomerSavingDto>> findAllSavingsAccountsPayableByIndividualClientsForCustomerHierarchy(
            CustomerHierarchyParams customerHierarchyParams);

    List<CollectionSheetCustomerSavingsAccountDto> findAllSavingAccountsForCustomerHierarchy(CustomerHierarchyParams customerHierarchyParams);

}
