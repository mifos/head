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

import org.mifos.application.accounts.loan.persistance.ClientAttendanceDao;
import org.mifos.application.accounts.loan.persistance.LoanPersistence;
import org.mifos.application.accounts.loan.persistance.StandardClientAttendanceDao;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.savings.persistence.GenericDao;
import org.mifos.application.accounts.savings.persistence.GenericDaoHibernate;
import org.mifos.application.accounts.savings.persistence.SavingsDao;
import org.mifos.application.accounts.savings.persistence.SavingsDaoHibernate;
import org.mifos.application.accounts.savings.persistence.SavingsPersistence;
import org.mifos.application.collectionsheet.persistence.BulkEntryPersistence;
import org.mifos.application.collectionsheet.persistence.CollectionSheetDao;
import org.mifos.application.collectionsheet.persistence.CollectionSheetDaoHibernate;
import org.mifos.application.customer.client.persistence.ClientPersistence;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.personnel.persistence.PersonnelPersistence;

/**
 * I contain static factory methods for locating/creating application services.
 * 
 * NOTE: Use of DI frameworks method would make this redundant. e.g.
 * spring/juice
 */
public class DependencyInjectedServiceLocator {

    // service facade
    private static CollectionSheetServiceFacade collectionSheetServiceFacade;

    // services
    private static CollectionSheetService collectionSheetService;

    // DAOs
    private static OfficePersistence officePersistence = new OfficePersistence();
    private static MasterPersistence masterPersistence = new MasterPersistence();
    private static PersonnelPersistence personnelPersistence = new PersonnelPersistence();
    private static CustomerPersistence customerPersistence = new CustomerPersistence();
    private static ClientPersistence clientPersistence = new ClientPersistence();
    private static BulkEntryPersistence bulkEntryPersistence = new BulkEntryPersistence();
    private static SavingsPersistence savingsPersistence = new SavingsPersistence();
    private static LoanPersistence loanPersistence = new LoanPersistence();
    private static AccountPersistence accountPersistence = new AccountPersistence();
    private static ClientAttendanceDao clientAttendanceDao = new StandardClientAttendanceDao(masterPersistence);
    
    private static GenericDao genericDao = new GenericDaoHibernate();
    private static SavingsDao savingsDao = new SavingsDaoHibernate(genericDao);
    private static CollectionSheetDao collectionSheetDao = new CollectionSheetDaoHibernate(savingsDao);
    
    // translators
    private static CollectionSheetDtoTranslator collectionSheetTranslator = new CollectionSheetDtoTranslatorImpl();

    public static CollectionSheetService locateCollectionSheetService() {

        if (collectionSheetService == null) {
            collectionSheetService = new CollectionSheetServiceImpl(clientAttendanceDao, loanPersistence,
                    accountPersistence, savingsPersistence, collectionSheetDao);
        }
        return collectionSheetService;
    }

    public static CollectionSheetServiceFacade locateCollectionSheetServiceFacade() {

        if (collectionSheetServiceFacade == null) {
            collectionSheetService = DependencyInjectedServiceLocator.locateCollectionSheetService();
            final CollectionSheetEntryViewAssembler collectionSheetEntryViewAssembler = new CollectionSheetEntryViewAssembler(
                    bulkEntryPersistence, customerPersistence, clientAttendanceDao);

            final CollectionSheetEntryGridViewAssembler collectionSheetEntryGridViewAssembler = new CollectionSheetEntryGridViewAssembler(
                    customerPersistence, masterPersistence, collectionSheetEntryViewAssembler);

            final AccountPaymentAssembler accountPaymentAssembler = new AccountPaymentAssembler(personnelPersistence);
            final SavingsAccountAssembler savingsAccountAssembler = new SavingsAccountAssembler(savingsPersistence,
                    customerPersistence);
            final ClientAttendanceAssembler clientAttendanceAssembler = new ClientAttendanceAssembler(
                    clientPersistence, clientAttendanceDao);
            final LoanAccountAssembler loanAccountAssembler = new LoanAccountAssembler(loanPersistence);
            final CustomerAccountAssembler customerAccountAssember = new CustomerAccountAssembler(customerPersistence);

            collectionSheetServiceFacade = new CollectionSheetServiceFacadeWebTier(officePersistence,
                    masterPersistence, personnelPersistence, customerPersistence, collectionSheetService,
                    collectionSheetEntryGridViewAssembler, clientAttendanceAssembler, loanAccountAssembler,
                    customerAccountAssember, savingsAccountAssembler, accountPaymentAssembler,
                    collectionSheetTranslator);
        }
        return collectionSheetServiceFacade;
    }

}
