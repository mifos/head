/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.loan.persistance.ClientAttendanceDao;
import org.mifos.application.accounts.loan.util.helpers.LoanAccountView;
import org.mifos.application.accounts.savings.util.helpers.SavingsAccountView;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryAccountFeeActionView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryInstallmentView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryView;
import org.mifos.application.collectionsheet.persistence.BulkEntryPersistence;
import org.mifos.application.collectionsheet.util.helpers.BulkEntryNodeBuilder;
import org.mifos.application.customer.business.CustomerView;
import org.mifos.application.customer.client.business.ClientAttendanceBO;
import org.mifos.application.customer.client.business.service.ClientAttendanceDto;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerAccountView;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.exceptions.PersistenceException;

/**
 * I am responsible for building {@link CollectionSheetEntryView}'s
 */
public class CollectionSheetEntryViewAssembler {

    private final BulkEntryPersistence bulkEntryPersistence;
    private final CustomerPersistence customerPersistence;
    private final ClientAttendanceDao clientAttendanceDao;

    public CollectionSheetEntryViewAssembler(BulkEntryPersistence bulkEntryPersistence,
            CustomerPersistence customerPersistence,
            ClientAttendanceDao clientAttendanceDao) {
        this.bulkEntryPersistence = bulkEntryPersistence;
        this.customerPersistence = customerPersistence;
        this.clientAttendanceDao = clientAttendanceDao;
    }

    public CollectionSheetEntryView toDto(final CollectionSheetFormEnteredDataDto formEnteredDataDto) {

        final java.sql.Date meetingDate = new java.sql.Date(formEnteredDataDto.getMeetingDate().getTime());
        final String selectedCustomerSearchId = formEnteredDataDto.getCustomer().getCustomerSearchId();
        final Short officeId = formEnteredDataDto.getOffice().getOfficeId();
        final Date transactionDate = formEnteredDataDto.getMeetingDate();
        try {
            List<LoanAccountView> loanAccountViewList = customerPersistence.findAllActiveLoansForHierarchy(officeId,
                    selectedCustomerSearchId, transactionDate);
            
            List<SavingsAccountView> savingsAccountViewList = customerPersistence.findAllActiveSavingsUnderCenter(
                    officeId, selectedCustomerSearchId);
            
            List<CustomerAccountView> customerAccountViewList = customerPersistence.findAllCustomerAccountsForHierarchy(
                    officeId, selectedCustomerSearchId);
            
            List<CollectionSheetEntryInstallmentView> bulkEntryLoanScheduleViews = bulkEntryPersistence
                    .getBulkEntryActionView(meetingDate, selectedCustomerSearchId, officeId, AccountTypes.LOAN_ACCOUNT);
            List<CollectionSheetEntryInstallmentView> bulkEntrySavingsScheduleViews = bulkEntryPersistence
                    .getBulkEntryActionView(meetingDate, selectedCustomerSearchId, officeId,
                            AccountTypes.SAVINGS_ACCOUNT);
            List<CollectionSheetEntryInstallmentView> bulkEntryCustomerScheduleViews = bulkEntryPersistence
                    .getBulkEntryActionView(meetingDate, selectedCustomerSearchId, officeId,
                            AccountTypes.CUSTOMER_ACCOUNT);
            List<CollectionSheetEntryAccountFeeActionView> bulkEntryLoanFeeScheduleViews = bulkEntryPersistence
                    .getBulkEntryFeeActionView(meetingDate, selectedCustomerSearchId, officeId,
                            AccountTypes.LOAN_ACCOUNT);
            List<CollectionSheetEntryAccountFeeActionView> bulkEntryCustomerFeeScheduleViews = bulkEntryPersistence
                    .getBulkEntryFeeActionView(meetingDate, selectedCustomerSearchId, officeId,
                            AccountTypes.CUSTOMER_ACCOUNT);

            final List<CustomerView> allChildNodes = customerPersistence.findCustomerHierarchyForOfficeBySearchId(
                    officeId, selectedCustomerSearchId);
            final int countOfCustomers = allChildNodes.size();

            List<ClientAttendanceBO> clientAttendanceBOs = clientAttendanceDao.getClientAttendance(meetingDate,
                    officeId);
            ArrayList<ClientAttendanceDto> clientAttendance = new ArrayList<ClientAttendanceDto>();
            for (ClientAttendanceBO clientAttendanceBo : clientAttendanceBOs) {
                clientAttendance.add(new ClientAttendanceDto(clientAttendanceBo.getId(), clientAttendanceBo
                        .getMeetingDate(), clientAttendanceBo.getAttendance()));
            }

            final CollectionSheetEntryView bulkEntryParent = BulkEntryNodeBuilder.buildBulkEntry(allChildNodes,
                    formEnteredDataDto.getCustomer(), meetingDate, bulkEntryLoanScheduleViews,
                    bulkEntryCustomerScheduleViews, bulkEntryLoanFeeScheduleViews, bulkEntryCustomerFeeScheduleViews,
                    clientAttendance, customerAccountViewList, loanAccountViewList, savingsAccountViewList);

            BulkEntryNodeBuilder.buildBulkEntrySavingsAccounts(bulkEntryParent, meetingDate,
                    bulkEntrySavingsScheduleViews);
            BulkEntryNodeBuilder.buildBulkEntryClientAttendance(bulkEntryParent, meetingDate, clientAttendance);
            
            bulkEntryParent.setCountOfCustomers(countOfCustomers);

            return bulkEntryParent;
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }
}
