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

import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.persistence.ClientPersistence;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.group.persistence.GroupPersistence;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

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
    private static final GroupPersistence groupPersistence = new GroupPersistence();
    private static final ClientPersistence clientPersistence = new ClientPersistence();

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
        } catch (PersistenceException e) {
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
        } catch (CustomerException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new RuntimeException(e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    public static void saveCustomerHierarchyWithMeetingAndFees(final CustomerBO center, final GroupBO group,
            final ClientBO client, final ClientBO client2, final MeetingBO meeting,
            final AmountFeeBO weeklyPeriodicFeeForCenterOnly, final AmountFeeBO weeklyPeriodicFeeForGroupOnly,
            final AmountFeeBO weeklyPeriodicFeeForClientsOnly,
            final AmountFeeBO secondClientWeeklyPeriodicFeeForClientsOnly) {
        try {
            StaticHibernateUtil.startTransaction();
            customerPersistence.createOrUpdate(meeting);
            customerPersistence.createOrUpdate(weeklyPeriodicFeeForCenterOnly);
            customerPersistence.createOrUpdate(weeklyPeriodicFeeForGroupOnly);
            customerPersistence.createOrUpdate(weeklyPeriodicFeeForClientsOnly);
            customerPersistence.createOrUpdate(secondClientWeeklyPeriodicFeeForClientsOnly);
            customerPersistence.saveCustomer(center);
            groupPersistence.saveGroup(group);
            clientPersistence.saveClient(client);
            clientPersistence.saveClient(client2);
            StaticHibernateUtil.commitTransaction();
        } catch (CustomerException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new RuntimeException(e);
        } catch (PersistenceException e) {
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

    public static void saveCustomerHierarchyWithMeetingAndFees(final CustomerBO center, final GroupBO group,
            final ClientBO client, final MeetingBO meeting, final AmountFeeBO weeklyPeriodicFeeForCenterOnly,
            final AmountFeeBO weeklyPeriodicFeeForGroupOnly, final AmountFeeBO weeklyPeriodicFeeForClientsOnly) {
        try {
            StaticHibernateUtil.startTransaction();
            customerPersistence.createOrUpdate(meeting);
            customerPersistence.createOrUpdate(weeklyPeriodicFeeForCenterOnly);
            customerPersistence.createOrUpdate(weeklyPeriodicFeeForGroupOnly);
            customerPersistence.createOrUpdate(weeklyPeriodicFeeForClientsOnly);
            customerPersistence.saveCustomer(center);
            groupPersistence.saveGroup(group);
            clientPersistence.saveClient(client);
            StaticHibernateUtil.commitTransaction();
        } catch (CustomerException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new RuntimeException(e);
        } catch (PersistenceException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new RuntimeException(e);
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
        } catch (PersistenceException e) {
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
        } catch (PersistenceException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new RuntimeException(e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }
}
