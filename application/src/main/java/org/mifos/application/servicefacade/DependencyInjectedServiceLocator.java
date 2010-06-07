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

import org.mifos.accounts.loan.persistance.ClientAttendanceDao;
import org.mifos.accounts.loan.persistance.LoanPersistence;
import org.mifos.accounts.loan.persistance.StandardClientAttendanceDao;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.accounts.productdefinition.business.LoanProductDaoHibernate;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.accounts.savings.persistence.GenericDaoHibernate;
import org.mifos.accounts.savings.persistence.SavingsDao;
import org.mifos.accounts.savings.persistence.SavingsDaoHibernate;
import org.mifos.accounts.savings.persistence.SavingsPersistence;
import org.mifos.application.collectionsheet.persistence.CollectionSheetDao;
import org.mifos.application.collectionsheet.persistence.CollectionSheetDaoHibernate;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.holiday.persistence.HolidayDaoHibernate;
import org.mifos.application.holiday.persistence.HolidayServiceFacade;
import org.mifos.application.holiday.persistence.HolidayServiceFacadeWebTier;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.customers.business.service.CustomerService;
import org.mifos.customers.business.service.CustomerServiceImpl;
import org.mifos.customers.center.business.service.CenterDetailsServiceFacade;
import org.mifos.customers.center.business.service.WebTierCenterDetailsServiceFacade;
import org.mifos.customers.client.business.service.ClientDetailsServiceFacade;
import org.mifos.customers.client.business.service.WebTierClientDetailsServiceFacade;
import org.mifos.customers.group.business.service.GroupDetailsServiceFacade;
import org.mifos.customers.group.business.service.WebTierGroupDetailsServiceFacade;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.office.persistence.OfficeDaoHibernate;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.persistence.CustomerDaoHibernate;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.personnel.persistence.PersonnelDaoHibernate;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelperForStaticHibernateUtil;
import org.mifos.security.authentication.AuthenticationDao;
import org.mifos.security.authentication.AuthenticationDaoHibernate;

/**
 * I contain static factory methods for locating/creating application services.
 *
 * NOTE: Use of DI frameworks method would make this redundant. e.g. spring/juice
 */
public class DependencyInjectedServiceLocator {

    // service facade
    private static CollectionSheetServiceFacade collectionSheetServiceFacade;
    private static LoanServiceFacade loanServiceFacade;
    private static CustomerServiceFacade customerServiceFacade;
    private static CenterDetailsServiceFacade centerDetailsServiceFacade;
    private static GroupDetailsServiceFacade groupDetailsServiceFacade;
    private static ClientDetailsServiceFacade clientDetailsServiceFacade;
    private static LoginServiceFacade loginServiceFacade;
    private static MeetingServiceFacade meetingServiceFacade;
    private static HolidayServiceFacade holidayServiceFacade;

    // services
    private static CollectionSheetService collectionSheetService;
    private static CustomerService customerService;

    // DAOs
    private static OfficePersistence officePersistence = new OfficePersistence();
    private static MasterPersistence masterPersistence = new MasterPersistence();
    private static PersonnelPersistence personnelPersistence = new PersonnelPersistence();
    private static CustomerPersistence customerPersistence = new CustomerPersistence();
    private static SavingsPersistence savingsPersistence = new SavingsPersistence();
    private static LoanPersistence loanPersistence = new LoanPersistence();
    private static AccountPersistence accountPersistence = new AccountPersistence();
    private static ClientAttendanceDao clientAttendanceDao = new StandardClientAttendanceDao(masterPersistence);

    private static GenericDao genericDao = new GenericDaoHibernate();
    private static OfficeDao officeDao = new OfficeDaoHibernate(genericDao);
    private static PersonnelDao personnelDao = new PersonnelDaoHibernate(genericDao);
    private static HolidayDao holidayDao = new HolidayDaoHibernate(genericDao);
    private static CustomerDao customerDao = new CustomerDaoHibernate(genericDao);
    private static LoanProductDao loanProductDao = new LoanProductDaoHibernate(genericDao);
    private static SavingsDao savingsDao = new SavingsDaoHibernate(genericDao);
    private static AuthenticationDao authenticationDao = new AuthenticationDaoHibernate();
    private static CollectionSheetDao collectionSheetDao = new CollectionSheetDaoHibernate(savingsDao);

    // translators
    private static CollectionSheetDtoTranslator collectionSheetTranslator = new CollectionSheetDtoTranslatorImpl();

    // helpers
    private static HibernateTransactionHelper hibernateTransactionHelper = new HibernateTransactionHelperForStaticHibernateUtil();

    public static CollectionSheetService locateCollectionSheetService() {

        if (collectionSheetService == null) {
            collectionSheetService = new CollectionSheetServiceImpl(clientAttendanceDao, loanPersistence,
                    accountPersistence, savingsPersistence, collectionSheetDao);
        }
        return collectionSheetService;
    }

    public static CustomerService locateCustomerService() {

        if (customerService == null) {
            customerService = new CustomerServiceImpl(customerDao, personnelDao, officeDao, holidayDao,
                    hibernateTransactionHelper);
        }
        return customerService;
    }

    public static CollectionSheetServiceFacade locateCollectionSheetServiceFacade() {

        if (collectionSheetServiceFacade == null) {
            collectionSheetService = DependencyInjectedServiceLocator.locateCollectionSheetService();

            collectionSheetServiceFacade = new CollectionSheetServiceFacadeWebTier(officePersistence,
                    masterPersistence, personnelPersistence, customerPersistence, collectionSheetService,
                    collectionSheetTranslator);
        }
        return collectionSheetServiceFacade;
    }

    public static CustomerServiceFacade locateCustomerServiceFacade() {
        if (customerServiceFacade == null) {

            customerService = DependencyInjectedServiceLocator.locateCustomerService();

            customerServiceFacade = new CustomerServiceFacadeWebTier(customerService, officeDao, personnelDao,
                    customerDao);
        }
        return customerServiceFacade;
    }

    public static ClientDetailsServiceFacade locateClientDetailsServiceFacade() {
        if (clientDetailsServiceFacade == null) {
            clientDetailsServiceFacade = new WebTierClientDetailsServiceFacade(customerDao);
        }
        return clientDetailsServiceFacade;
    }

    public static GroupDetailsServiceFacade locateGroupDetailsServiceFacade() {
        if (groupDetailsServiceFacade == null) {
            groupDetailsServiceFacade = new WebTierGroupDetailsServiceFacade(customerDao);
        }
        return groupDetailsServiceFacade;
    }

    public static CenterDetailsServiceFacade locateCenterDetailsServiceFacade() {
        if (centerDetailsServiceFacade == null) {
            centerDetailsServiceFacade = new WebTierCenterDetailsServiceFacade(customerDao);
        }
        return centerDetailsServiceFacade;
    }

    public static LoanServiceFacade locateLoanServiceFacade() {
        if (loanServiceFacade == null) {
            loanServiceFacade = new LoanServiceFacadeWebTier(loanProductDao, customerDao);
        }
        return loanServiceFacade;
    }

    public static LoginServiceFacade locationLoginServiceFacade() {
        if (loginServiceFacade == null) {
            loginServiceFacade = new LoginServiceFacadeWebTier(authenticationDao);
        }
        return loginServiceFacade;
    }

    public static MeetingServiceFacade locateMeetingServiceFacade() {
        if (meetingServiceFacade == null) {
            customerService = locateCustomerService();
            meetingServiceFacade = new MeetingServiceFacadeWebTier(customerService);
        }
        return meetingServiceFacade;
    }

    public static HolidayServiceFacade locateHolidayServiceFacade() {
        if (holidayServiceFacade == null) {
            holidayServiceFacade = new HolidayServiceFacadeWebTier(officeDao, holidayDao);
        }
        return holidayServiceFacade;
    }

    public static CustomerDao locateCustomerDao() {
        return customerDao;
    }

    public static HolidayDao locateHolidayDao() {
        return holidayDao;
    }

    public static GenericDao locateGenericDao() {
        return genericDao;
    }
}