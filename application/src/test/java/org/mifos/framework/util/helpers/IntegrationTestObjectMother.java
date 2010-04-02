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

package org.mifos.framework.util.helpers;

import java.util.ArrayList;
import java.util.List;

import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.servicefacade.DependencyInjectedServiceLocator;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.service.CustomerService;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.security.util.UserContext;

/**
 *
 */
public class IntegrationTestObjectMother {

    private static final String INTEGRATION_TEST_DATA_MISSING_MESSAGE = "This should exist within database for integration tests but does not.";

    // user
    private static final Short DEFAULT_INTEGRATION_TEST_USER = PersonnelConstants.TEST_USER;
    private static PersonnelBO testUser = null;

    // office
    private static final Short SAMPLE_BRANCH_OFFICE = 3;
    private static OfficeBO sampleBranchOffice = null;

    // DAO's for fetching existing data within database
    private static final OfficePersistence officePersistence = new OfficePersistence();
    private static final PersonnelPersistence personnelPersistence = new PersonnelPersistence();
    private static final CustomerPersistence customerPersistence = new CustomerPersistence();

    private static final CustomerService customerService = DependencyInjectedServiceLocator.locateCustomerService();

    public static OfficeBO sampleBranchOffice() {
        if (sampleBranchOffice == null) {
            try {
                sampleBranchOffice = officePersistence.getOffice(SAMPLE_BRANCH_OFFICE);
            } catch (PersistenceException e) {
                throw new IllegalStateException("Office with id [" + SAMPLE_BRANCH_OFFICE + "]. "
                        + INTEGRATION_TEST_DATA_MISSING_MESSAGE);
            }
        }
        return sampleBranchOffice;
    }

    public static PersonnelBO testUser() {
        if (testUser == null) {
            try {
                testUser = personnelPersistence.getPersonnel(DEFAULT_INTEGRATION_TEST_USER);
            } catch (PersistenceException e) {
                throw new IllegalStateException("PersonnelBO with id [" + DEFAULT_INTEGRATION_TEST_USER + "]. "
                        + INTEGRATION_TEST_DATA_MISSING_MESSAGE);
            }
        }

        return testUser;
    }

    public static void saveMeeting(final MeetingBO meeting) {
        try {
            StaticHibernateUtil.startTransaction();
            customerPersistence.createOrUpdate(meeting);
            StaticHibernateUtil.commitTransaction();
            StaticHibernateUtil.flushAndClearSession();
        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new RuntimeException(e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    public static void saveCustomer(final CustomerBO center) {
        try {
            StaticHibernateUtil.startTransaction();
            customerPersistence.saveCustomer(center);
            StaticHibernateUtil.commitTransaction();
            StaticHibernateUtil.flushAndClearSession();
        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new RuntimeException(e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    public static void cleanMeeting(MeetingBO weeklyMeeting) {
        try {
            TestObjectFactory.cleanUp(weeklyMeeting);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    public static void cleanCustomerHierarchyWithMultipleClientsMeetingsAndFees(final ClientBO activeClient,
            final ClientBO inActiveClient, final GroupBO group, final CustomerBO center, final MeetingBO weeklyMeeting) {
        try {
            TestObjectFactory.cleanUp(activeClient);
            TestObjectFactory.cleanUp(inActiveClient);
            TestObjectFactory.cleanUp(group);
            TestObjectFactory.cleanUp(center);
            TestObjectFactory.cleanUp(weeklyMeeting);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    public static void cleanCustomerHierarchyWithMeetingAndFees(final ClientBO client, final GroupBO group,
            final CustomerBO center, final MeetingBO weeklyMeeting) {
        try {
            TestObjectFactory.cleanUp(client);
            TestObjectFactory.cleanUp(group);
            TestObjectFactory.cleanUp(center);
            TestObjectFactory.cleanUp(weeklyMeeting);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    public static void saveSavingsProductAndAssociatedSavingsAccounts(final SavingsOfferingBO savingsProduct,
            final SavingsBO... relatedSavingsAccounts) {
        try {
            StaticHibernateUtil.startTransaction();
            customerPersistence.createOrUpdate(savingsProduct);
            for (SavingsBO savingAccount : relatedSavingsAccounts) {
                customerPersistence.createOrUpdate(savingAccount);
            }
            StaticHibernateUtil.commitTransaction();
        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new RuntimeException(e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    public static void cleanSavingsProductAndAssociatedSavingsAccounts(final SavingsBO... savingsAccount) {

        try {
            for (SavingsBO savingsBO : savingsAccount) {
                TestObjectFactory.cleanUp(savingsBO);
            }
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    public static void saveLoanProducts(final LoanOfferingBO... loanProducts) {
        try {
            StaticHibernateUtil.startTransaction();
            for (LoanOfferingBO loanProduct : loanProducts) {
                customerPersistence.createOrUpdate(loanProduct);
            }
            StaticHibernateUtil.commitTransaction();
        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new RuntimeException(e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    public static void createCenter(CenterBO center, MeetingBO meeting) {
        UserContext userContext = TestUtils.makeUser();
        center.setUserContext(userContext);

        List<AccountFeesEntity> accountFees = new ArrayList<AccountFeesEntity>();

        customerService.createCenter(center, meeting, accountFees);
    }

    public static void createCenter(CenterBO center, MeetingBO meeting,
            AmountFeeBO fee) {

        UserContext userContext = TestUtils.makeUser();
        center.setUserContext(userContext);

        AccountFeesEntity accountFee = new AccountFeesEntity(null, fee, fee.getFeeAmount().getAmountDoubleValue());

        List<AccountFeesEntity> accountFees = new ArrayList<AccountFeesEntity>();
        accountFees.add(accountFee);

        customerService.createCenter(center, meeting, accountFees);
    }

    public static void createGroup(GroupBO group, MeetingBO meeting) throws CustomerException {

        UserContext userContext = TestUtils.makeUser();
        group.setUserContext(userContext);

        List<AccountFeesEntity> accountFees = new ArrayList<AccountFeesEntity>();

        customerService.createGroup(group, meeting, accountFees);
    }

    public static void createGroup(GroupBO group, MeetingBO meeting, AmountFeeBO fee) throws CustomerException {

        UserContext userContext = TestUtils.makeUser();
        group.setUserContext(userContext);

        AccountFeesEntity accountFee = new AccountFeesEntity(null, fee, fee.getFeeAmount().getAmountDoubleValue());

        List<AccountFeesEntity> accountFees = new ArrayList<AccountFeesEntity>();
        accountFees.add(accountFee);

        customerService.createGroup(group, meeting, accountFees);
    }

    public static void createClient(ClientBO client, MeetingBO meeting) throws CustomerException {
        UserContext userContext = TestUtils.makeUser();
        client.setUserContext(userContext);

        List<AccountFeesEntity> accountFees = new ArrayList<AccountFeesEntity>();
        List<SavingsOfferingBO> selectedOfferings = new ArrayList<SavingsOfferingBO>();

        customerService.createClient(client, meeting, accountFees, selectedOfferings);
    }

    public static void createClient(ClientBO client, MeetingBO meeting, AmountFeeBO fee) throws CustomerException {

        UserContext userContext = TestUtils.makeUser();
        client.setUserContext(userContext);

        AccountFeesEntity accountFee = new AccountFeesEntity(null, fee, fee.getFeeAmount().getAmountDoubleValue());

        List<AccountFeesEntity> accountFees = new ArrayList<AccountFeesEntity>();
        accountFees.add(accountFee);

        List<SavingsOfferingBO> selectedOfferings = new ArrayList<SavingsOfferingBO>();

        customerService.createClient(client, meeting, accountFees, selectedOfferings);
    }

    public static void saveFee(AmountFeeBO fee) {
        try {
            StaticHibernateUtil.startTransaction();
            customerPersistence.createOrUpdate(fee);
            StaticHibernateUtil.commitTransaction();
        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new RuntimeException(e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }
}