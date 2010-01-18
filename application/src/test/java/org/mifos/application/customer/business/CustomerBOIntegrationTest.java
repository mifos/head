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

package org.mifos.application.customer.business;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanBOTestUtils;
import org.mifos.application.accounts.savings.business.SavingBOTestUtils;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.ClientDetailView;
import org.mifos.application.customer.client.business.ClientPerformanceHistoryEntity;
import org.mifos.application.customer.client.business.NameType;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.business.GroupPerformanceHistoryEntity;
import org.mifos.application.customer.group.business.GroupTestUtils;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.customer.util.helpers.CustomerStatusFlag;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.application.personnel.util.helpers.PersonnelStatus;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBOFixture;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CustomerBOIntegrationTest extends MifosIntegrationTestCase {
    public CustomerBOIntegrationTest() throws Exception {
        super();
    }

    private AccountBO accountBO;

    private CenterBO center;

    private GroupBO group;

    private ClientBO client;

    private CustomerPersistence customerPersistence = new CustomerPersistence();

    private MeetingBO meeting;

    private SavingsTestHelper helper = new SavingsTestHelper();

    private SavingsOfferingBO savingsOffering;

    PersonnelBO loanOfficer;

    private OfficeBO createdBranchOffice;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            TestObjectFactory.cleanUp(accountBO);
            TestObjectFactory.cleanUp(client);
            TestObjectFactory.cleanUp(group);
            TestObjectFactory.cleanUp(center);

            TestObjectFactory.cleanUp(loanOfficer);
            TestObjectFactory.cleanUp(createdBranchOffice);
        } catch (Exception e) {
            // TODO Whoops, cleanup didnt work, reset db
            TestDatabase.resetMySQLDatabase();
        }
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testRemoveGroupMemberShip() throws Exception {
        createInitialObjects();
        client.setUserContext(TestUtils.makeUser());
        StaticHibernateUtil.getInterceptor().createInitialValueMap(client);
        createPersonnel(PersonnelLevel.LOAN_OFFICER);
        client.removeGroupMemberShip(loanOfficer, "comment");
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        loanOfficer = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(PersonnelBO.class,
                loanOfficer.getPersonnelId());
       Assert.assertEquals(loanOfficer.getPersonnelId(), client.getPersonnel().getPersonnelId());
        group = TestObjectFactory.getGroup(group.getCustomerId());

        TestObjectFactory.cleanUpChangeLog();
    }

    public void testHasActiveLoanAccounts() throws Exception {
        createInitialObjects();
        client.setUserContext(TestUtils.makeUser());
        StaticHibernateUtil.getInterceptor().createInitialValueMap(client);
        boolean res = client.hasActiveLoanAccounts();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
       Assert.assertEquals(res, false);
        TestObjectFactory.cleanUpChangeLog();
    }

    public void testHasActiveLoanAccountsForProductReturnsTrueIfCustomerHasSuchAccounts() throws Exception {
        client = createClientToTestActiveLoanProducts();
        LoanOfferingBO loanProduct1 = LoanOfferingBOFixture.createLoanOfferingBO("test loan product", "TLP");
        LoanBO accountMock = createMock(LoanBO.class);
        client.addAccount(accountMock);
        expect(accountMock.isActiveLoanAccount()).andReturn(true);
        expect(accountMock.getLoanOffering()).andReturn(loanProduct1);
        replay(accountMock);
       Assert.assertTrue(client.hasActiveLoanAccountsForProduct(loanProduct1));
        verify(accountMock);
    }

    public void testHasActiveLoanAccountsForProductReturnsFalseIfCustomerHasNoSuchAccountsForThatProduct()
            throws Exception {
        client = createClientToTestActiveLoanProducts();
        LoanOfferingBO loanProduct1 = LoanOfferingBOFixture.createLoanOfferingBO("test loan product", "TLP");
        LoanOfferingBO loanProduct2 = LoanOfferingBOFixture.createLoanOfferingBO("test loan product2", "TLP2");
        LoanBO accountMock = createMock(LoanBO.class);
        client.addAccount(accountMock);
        expect(accountMock.isActiveLoanAccount()).andReturn(true);
        expect(accountMock.getLoanOffering()).andReturn(loanProduct2);
        replay(accountMock);
        Assert.assertFalse(client.hasActiveLoanAccountsForProduct(loanProduct1));
        verify(accountMock);
    }

    public void testHasActiveLoanAccountsForProductDoesNotFetchLoanOfferingIfNoActiveLoanAccounts() throws Exception {
        client = createClientToTestActiveLoanProducts();
        LoanOfferingBO loanProduct1 = LoanOfferingBOFixture.createLoanOfferingBO("test loan product", "TLP");
        LoanBO accountMock = createMock(LoanBO.class);
        client.addAccount(accountMock);
        expect(accountMock.isActiveLoanAccount()).andReturn(false);
        replay(accountMock);
        Assert.assertFalse(client.hasActiveLoanAccountsForProduct(loanProduct1));
        verify(accountMock);
    }

    private ClientBO createClientToTestActiveLoanProducts() throws CustomerException {
        return new ClientBO(TestUtils.makeUserWithLocales(), "customerName", CustomerStatus.CLIENT_ACTIVE, null, null,
                null, null, new ArrayList<FeeView>(), null, getSystemUser(), getHeadOffice(), null, null, null, null,
                null, YesNoFlag.YES.getValue(), TestObjectFactory.clientNameView(NameType.CLIENT, "customerName"),
                TestObjectFactory.clientNameView(NameType.SPOUSE, "customerName"), new ClientDetailView(1, 1, 1, 1, 1,
                        1, Short.valueOf("1"), Short.valueOf("1"), Short.valueOf("41")), null);
    }

    public void testCheckIfClientIsATitleHolder() throws Exception {
        createInitialObjects();
        client.setUserContext(TestUtils.makeUser());
        StaticHibernateUtil.getInterceptor().createInitialValueMap(client);

        try {
            client.checkIfClientIsATitleHolder();

        } catch (CustomerException expected) {
           Assert.assertEquals(CustomerConstants.CLIENT_IS_A_TITLE_HOLDER_EXCEPTION, expected.getKey());
        }

        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        TestObjectFactory.cleanUpChangeLog();
    }

    //

    public void testStatusChangeForCenterForLogging() throws Exception {
        OfficeBO office = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
        createdBranchOffice = TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, office, "Office_BRanch1", "OFB");
        StaticHibernateUtil.closeSession();
        createdBranchOffice = (OfficeBO) StaticHibernateUtil.getSessionTL().get(OfficeBO.class,
                createdBranchOffice.getOfficeId());
        createPersonnel(PersonnelLevel.LOAN_OFFICER);

        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());

        center = TestObjectFactory.createCenter(this.getClass().getSimpleName() +" Center", meeting, getBranchOffice().getOfficeId(), loanOfficer
                .getPersonnelId());
        center.setUserContext(TestUtils.makeUserWithLocales());
        StaticHibernateUtil.getInterceptor().createInitialValueMap(center);
        center.changeStatus(CustomerStatus.CENTER_INACTIVE, null, "comment");
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        center = (CenterBO) StaticHibernateUtil.getSessionTL().get(CenterBO.class, center.getCustomerId());
        loanOfficer = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(PersonnelBO.class,
                loanOfficer.getPersonnelId());

        List<AuditLog> auditLogList = TestObjectFactory.getChangeLog(EntityType.CENTER, center.getCustomerId());
       Assert.assertEquals(1, auditLogList.size());
       Assert.assertEquals(EntityType.CENTER, auditLogList.get(0).getEntityTypeAsEnum());
        Set<AuditLogRecord> records = auditLogList.get(0).getAuditLogRecords();
       Assert.assertEquals(1, records.size());
        AuditLogRecord record = records.iterator().next();
       Assert.assertEquals("Status", record.getFieldName());
       Assert.assertEquals("Active", record.getOldValue());
       Assert.assertEquals("Inactive", record.getNewValue());
        TestObjectFactory.cleanUpChangeLog();
    }

    public void testStatusChangeForGroupForLogging() throws Exception {
        createGroup();

        group.setUserContext(TestUtils.makeUserWithLocales());

        StaticHibernateUtil.getInterceptor().createInitialValueMap(group);
        group.changeStatus(CustomerStatus.GROUP_CANCELLED, CustomerStatusFlag.GROUP_CANCEL_DUPLICATE, "comment");
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        center = (CenterBO) StaticHibernateUtil.getSessionTL().get(CenterBO.class, center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        List<AuditLog> auditLogList = TestObjectFactory.getChangeLog(EntityType.GROUP, group.getCustomerId());
       Assert.assertEquals(1, auditLogList.size());
       Assert.assertEquals(EntityType.GROUP.getValue(), auditLogList.get(0).getEntityType());
       Assert.assertEquals(2, auditLogList.get(0).getAuditLogRecords().size());
        for (AuditLogRecord auditLogRecord : auditLogList.get(0).getAuditLogRecords()) {
            if (auditLogRecord.getFieldName().equalsIgnoreCase("Status")) {
               Assert.assertEquals("Active", auditLogRecord.getOldValue());
               Assert.assertEquals("Cancelled", auditLogRecord.getNewValue());
            } else if (auditLogRecord.getFieldName().equalsIgnoreCase("Status Change Explanation")) {
               Assert.assertEquals("-", auditLogRecord.getOldValue());
               Assert.assertEquals("Duplicate", auditLogRecord.getNewValue());
            } else {
                Assert.fail("unexpected record " + auditLogRecord.getFieldName());
            }
        }
        TestObjectFactory.cleanUpChangeLog();
    }

    public void testGroupPerfObject() throws PersistenceException {
        createInitialObjects();
        GroupPerformanceHistoryEntity groupPerformanceHistory = group.getGroupPerformanceHistory();
        GroupTestUtils.setLastGroupLoanAmount(groupPerformanceHistory, new Money(getCurrency(), "100"));
        TestObjectFactory.updateObject(group);
        StaticHibernateUtil.closeSession();
        group = (GroupBO) customerPersistence
                .findBySystemId(group.getGlobalCustNum(), group.getCustomerLevel().getId());
       Assert.assertEquals(group.getCustomerId(), group.getGroupPerformanceHistory().getGroup().getCustomerId());
       Assert.assertEquals(new Money(getCurrency(), "100"), group.getGroupPerformanceHistory().getLastGroupLoanAmount());
        StaticHibernateUtil.closeSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
    }

    public void testGroupPerformanceObject() throws Exception {
        GroupPerformanceHistoryEntity groupPerformanceHistory = new GroupPerformanceHistoryEntity(Integer.valueOf("1"),
                new Money(getCurrency(), "23"), new Money(getCurrency(), "24"), new Money(getCurrency(), "26"),
                new Money(getCurrency(), "25"), new Money(getCurrency(), "27"));
        Assert.assertEquals(new Money(getCurrency(), "23"), groupPerformanceHistory.getLastGroupLoanAmount());
       Assert.assertEquals(new Money(getCurrency(), "27"), groupPerformanceHistory.getPortfolioAtRisk());
       Assert.assertEquals(1, groupPerformanceHistory.getClientCount().intValue());

    }

    public void testClientPerfObject() throws PersistenceException {
        createInitialObjects();
        ClientPerformanceHistoryEntity clientPerformanceHistory = client.getClientPerformanceHistory();
        clientPerformanceHistory.setNoOfActiveLoans(Integer.valueOf("1"));
        clientPerformanceHistory.setLastLoanAmount(new Money(getCurrency(), "100"));
        TestObjectFactory.updateObject(client);
        client = (ClientBO) customerPersistence.findBySystemId(client.getGlobalCustNum(), client.getCustomerLevel()
                .getId());
       Assert.assertEquals(client.getCustomerId(), client.getClientPerformanceHistory().getClient().getCustomerId());
       Assert.assertEquals(new Money(getCurrency(), "100"), client.getClientPerformanceHistory().getLastLoanAmount());
       Assert.assertEquals(new Money(getCurrency(), "0"), client.getClientPerformanceHistory().getDelinquentPortfolioAmount());
    }

    public void testGetBalanceForAccountsAtRisk() throws PersistenceException {
        createInitialObjects();
        accountBO = getLoanAccount(group, meeting);
        TestObjectFactory.flushandCloseSession();
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        accountBO = TestObjectFactory.getObject(AccountBO.class, accountBO.getAccountId());
       Assert.assertEquals(new Money(getCurrency()), getBalanceForAccountsAtRisk());
        changeFirstInstallmentDate(accountBO, 31);
       Assert.assertEquals(new Money(getCurrency(), "300"), getBalanceForAccountsAtRisk());
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        accountBO = TestObjectFactory.getObject(AccountBO.class, accountBO.getAccountId());
    }

    private Money getBalanceForAccountsAtRisk() {
        Money amount = new Money(getCurrency());
        for (AccountBO account : group.getAccounts()) {
            if (account.getType() == AccountTypes.LOAN_ACCOUNT && ((LoanBO) account).isAccountActive()) {
                LoanBO loan = (LoanBO) account;
                if (loan.hasPortfolioAtRisk()) {
                    amount = amount.add(loan.getRemainingPrincipalAmount());
                }
            }
        }
        return amount;
    }
    /*
     * Removing this test for now since this test case is the only place the
     * method getDelinquentPortfolioAmount() is called
     * 
     * public void testGetDelinquentPortfolioAmount() { createInitialObjects();
     * accountBO = getLoanAccount(client, meeting);
     * TestObjectFactory.flushandCloseSession(); accountBO =
     * TestObjectFactory.getObject(AccountBO.class, accountBO.getAccountId());
     * setActionDateToPastDate(); TestObjectFactory.updateObject(accountBO);
     * TestObjectFactory.flushandCloseSession(); client =
     * TestObjectFactory.getClient(ClientBO.class, client.getCustomerId());
     *Assert.assertEquals(new Money(getCurrency(), "0.7"), client.getDelinquentPortfolioAmount());
     * TestObjectFactory.flushandCloseSession(); center =
     * TestObjectFactory.getCenter(CenterBO.class, center .getCustomerId());
     * group = TestObjectFactory.getGroup(GroupBO.class, group
     * .getCustomerId()); client = TestObjectFactory.getClient(ClientBO.class,
     * client .getCustomerId()); accountBO =
     * TestObjectFactory.getObject(AccountBO.class, accountBO.getAccountId()); }
     */

    public void testGetOutstandingLoanAmount() throws PersistenceException {
        createInitialObjects();
        accountBO = getLoanAccount(group, meeting);
        TestObjectFactory.flushandCloseSession();
        group = TestObjectFactory.getGroup(group.getCustomerId());
       Assert.assertEquals(new Money(getCurrency(), "300.0"), group.getOutstandingLoanAmount(getCurrency()));
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        accountBO = TestObjectFactory.getObject(AccountBO.class, accountBO.getAccountId());
    }

    public void testGetActiveLoanCounts() throws PersistenceException {
        createInitialObjects();
        accountBO = getLoanAccount(group, meeting);
        TestObjectFactory.flushandCloseSession();
        group = TestObjectFactory.getGroup(group.getCustomerId());
       Assert.assertEquals(1, group.getActiveLoanCounts().intValue());
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        accountBO = TestObjectFactory.getObject(AccountBO.class, accountBO.getAccountId());
    }

    public void testGetOpenIndividualLoanAccounts() throws PersistenceException {
        createInitialObjects();
        accountBO = getIndividualLoanAccount(group, meeting);
        TestObjectFactory.flushandCloseSession();
        group = TestObjectFactory.getGroup(group.getCustomerId());
        List<LoanBO> loans = group.getOpenIndividualLoanAccounts();
       Assert.assertEquals(1, loans.size());

        TestObjectFactory.flushandCloseSession();

        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        accountBO = TestObjectFactory.getObject(AccountBO.class, accountBO.getAccountId());
    }

    public void testGetLoanAccountInUse() throws PersistenceException {
        createInitialObjects();
        accountBO = getLoanAccount(group, meeting);
        TestObjectFactory.flushandCloseSession();
        group = TestObjectFactory.getGroup(group.getCustomerId());
        List<LoanBO> loans = group.getOpenLoanAccounts();
       Assert.assertEquals(1, loans.size());
       Assert.assertEquals(accountBO.getAccountId(), loans.get(0).getAccountId());
        TestObjectFactory.flushandCloseSession();

        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        accountBO = TestObjectFactory.getObject(AccountBO.class, accountBO.getAccountId());
    }

    public void testGetSavingsAccountInUse() throws Exception {
        accountBO = getSavingsAccount("fsaf6", "ads6");
        TestObjectFactory.flushandCloseSession();
        client = TestObjectFactory.getClient(client.getCustomerId());
        List<SavingsBO> savings = client.getOpenSavingAccounts();
       Assert.assertEquals(1, savings.size());
       Assert.assertEquals(accountBO.getAccountId(), savings.get(0).getAccountId());
        TestObjectFactory.flushandCloseSession();

        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        accountBO = TestObjectFactory.getObject(AccountBO.class, accountBO.getAccountId());
    }

    public void testHasAnyLoanAccountInUse() throws PersistenceException {
        createInitialObjects();
        accountBO = getLoanAccount(group, meeting);
        TestObjectFactory.flushandCloseSession();
        group = TestObjectFactory.getGroup(group.getCustomerId());
       Assert.assertTrue(group.isAnyLoanAccountOpen());
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        accountBO = TestObjectFactory.getObject(AccountBO.class, accountBO.getAccountId());
    }

    public void testHasAnySavingsAccountInUse() throws Exception {
        accountBO = getSavingsAccount("fsaf5", "ads5");
        TestObjectFactory.flushandCloseSession();
        client = TestObjectFactory.getClient(client.getCustomerId());
       Assert.assertTrue(client.isAnySavingsAccountOpen());
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        accountBO = TestObjectFactory.getObject(AccountBO.class, accountBO.getAccountId());
    }

    public void testgetSavingsBalance() throws Exception {
        SavingsBO savings = getSavingsAccount("fsaf4", "ads4");
        SavingBOTestUtils.setBalance(savings, new Money(getCurrency(), "1000"));
        savings.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savings = TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
        client = TestObjectFactory.getClient(client.getCustomerId());
       Assert.assertEquals(new Money(getCurrency(), "1000.0"), savings.getSavingsBalance());
       Assert.assertEquals(new Money(getCurrency(), "1000.0"), client.getSavingsBalance(getCurrency()));
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        savings = TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
        TestObjectFactory.cleanUp(savings);
    }

    public void testValidateStatusWithActiveGroups() throws CustomerException, AccountException {
        createInitialObjects();
        try {
            center.changeStatus(CustomerStatus.CENTER_INACTIVE, null, "Test");
            Assert.fail();
        } catch (CustomerException expected) {
           Assert.assertEquals(CustomerConstants.ERROR_STATE_CHANGE_EXCEPTION, expected.getKey());
           Assert.assertEquals(CustomerStatus.CENTER_ACTIVE, center.getStatus());
        }
    }

    public void testValidateStatusForClientWithCancelledGroups() throws Exception {
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter(this.getClass().getSimpleName() +" Center", meeting);
        group = TestObjectFactory.createGroupUnderCenter(this.getClass().getSimpleName() +" Group", CustomerStatus.GROUP_CANCELLED, center);
        client = TestObjectFactory.createClient(this.getClass().getSimpleName() +" Client", CustomerStatus.CLIENT_PARTIAL, group);
        try {
            client.changeStatus(CustomerStatus.CLIENT_ACTIVE, null, "Test");
            Assert.fail();
        } catch (CustomerException expected) {
           Assert.assertEquals(ClientConstants.ERRORS_GROUP_CANCELLED, expected.getKey());
           Assert.assertEquals(CustomerStatus.CLIENT_PARTIAL, client.getStatus());
        }
    }

    public void testValidateStatusForClientWithPartialGroups() throws Exception {
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter(this.getClass().getSimpleName() +" Center", meeting);
        group = TestObjectFactory.createGroupUnderCenter(this.getClass().getSimpleName() +" Group", CustomerStatus.GROUP_PARTIAL, center);
        client = TestObjectFactory.createClient(this.getClass().getSimpleName() +" Client", CustomerStatus.CLIENT_PARTIAL, group);
        try {
            client.changeStatus(CustomerStatus.CLIENT_ACTIVE, null, "Test");
            Assert.fail();
        } catch (CustomerException sce) {
           Assert.assertEquals(ClientConstants.INVALID_CLIENT_STATUS_EXCEPTION, sce.getKey());
           Assert.assertEquals(CustomerStatus.CLIENT_PARTIAL, client.getStatus());
        }
    }

    public void testValidateStatusForClientWithActiveAccounts() throws Exception {
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter(this.getClass().getSimpleName() +" Center", meeting);
        group = TestObjectFactory.createGroupUnderCenter(this.getClass().getSimpleName() +"  Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient(this.getClass().getSimpleName() +"  Client", CustomerStatus.CLIENT_ACTIVE, group);
        accountBO = getLoanAccount(client, meeting);
        StaticHibernateUtil.closeSession();
        client = (ClientBO) StaticHibernateUtil.getSessionTL().get(ClientBO.class, client.getCustomerId());
        try {
            client.changeStatus(CustomerStatus.CLIENT_CLOSED, null, "Test");
            Assert.fail();
        } catch (CustomerException expected) {
           Assert.assertEquals(CustomerConstants.CUSTOMER_HAS_ACTIVE_ACCOUNTS_EXCEPTION, expected.getKey());
           Assert.assertEquals(CustomerStatus.CLIENT_ACTIVE, client.getStatus());
        }
        StaticHibernateUtil.closeSession();
        client = (ClientBO) StaticHibernateUtil.getSessionTL().get(ClientBO.class, client.getCustomerId());
        group = (GroupBO) StaticHibernateUtil.getSessionTL().get(GroupBO.class, group.getCustomerId());
        center = (CenterBO) StaticHibernateUtil.getSessionTL().get(CenterBO.class, center.getCustomerId());
        accountBO = (LoanBO) StaticHibernateUtil.getSessionTL().get(LoanBO.class, accountBO.getAccountId());
    }

    public void testValidateStatusChangeForCustomerWithInactiveLoanofficerAssigned() throws Exception {
        createPersonnel(PersonnelLevel.LOAN_OFFICER);
        createCenter(getBranchOffice().getOfficeId(), loanOfficer.getPersonnelId());
        center.changeStatus(CustomerStatus.CENTER_INACTIVE, null, "comment");
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        loanOfficer = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(PersonnelBO.class,
                loanOfficer.getPersonnelId());
        center = (CenterBO) StaticHibernateUtil.getSessionTL().get(CenterBO.class, center.getCustomerId());
        updatePersonnel(PersonnelLevel.LOAN_OFFICER, PersonnelStatus.INACTIVE, getBranchOffice());
        try {
            center.changeStatus(CustomerStatus.CENTER_ACTIVE, null, "comment");
            Assert.fail();
        } catch (CustomerException expected) {
           Assert.assertEquals(CustomerConstants.CUSTOMER_LOAN_OFFICER_INACTIVE_EXCEPTION, expected.getKey());
           Assert.assertEquals(CustomerStatus.CENTER_INACTIVE, center.getStatus());
        }
        center = (CenterBO) StaticHibernateUtil.getSessionTL().get(CenterBO.class, center.getCustomerId());
        loanOfficer = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(PersonnelBO.class,
                loanOfficer.getPersonnelId());
    }

    public void testValidateStatusChangeForCustomerWithLoanofficerAssignedToDifferentBranch() throws Exception {
        OfficeBO office = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
        createdBranchOffice = TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, office, "Office_BRanch1", "OFB");
        StaticHibernateUtil.closeSession();
        createdBranchOffice = (OfficeBO) StaticHibernateUtil.getSessionTL().get(OfficeBO.class,
                createdBranchOffice.getOfficeId());
        createPersonnel(PersonnelLevel.LOAN_OFFICER);
        createCenter(getBranchOffice().getOfficeId(), loanOfficer.getPersonnelId());
        center.changeStatus(CustomerStatus.CENTER_INACTIVE, null, "comment");
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        loanOfficer = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(PersonnelBO.class,
                loanOfficer.getPersonnelId());
        center = (CenterBO) StaticHibernateUtil.getSessionTL().get(CenterBO.class, center.getCustomerId());
        updatePersonnel(PersonnelLevel.LOAN_OFFICER, PersonnelStatus.ACTIVE, createdBranchOffice);
        try {
            center.changeStatus(CustomerStatus.CENTER_ACTIVE, null, "comment");
            Assert.assertFalse(true);
        } catch (CustomerException ce) {
           Assert.assertTrue(true);
           Assert.assertEquals(ce.getKey(), CustomerConstants.CUSTOMER_LOAN_OFFICER_INACTIVE_EXCEPTION);
           Assert.assertEquals(CustomerStatus.CENTER_INACTIVE.getValue(), center.getCustomerStatus().getId());
        }
        center = (CenterBO) StaticHibernateUtil.getSessionTL().get(CenterBO.class, center.getCustomerId());
        loanOfficer = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(PersonnelBO.class,
                loanOfficer.getPersonnelId());
    }

    public void testValidateStatusForClientSavingsAccountInactive() throws Exception {
        accountBO = getSavingsAccount("fsaf6", "ads6");
        accountBO.changeStatus(AccountState.SAVINGS_INACTIVE.getValue(), null, "changed status");
        accountBO.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        client = (ClientBO) StaticHibernateUtil.getSessionTL().get(ClientBO.class, client.getCustomerId());
        try {
            client.changeStatus(CustomerStatus.CLIENT_CLOSED, null, "Test");
            Assert.fail();
        } catch (CustomerException expected) {
           Assert.assertEquals(CustomerConstants.CUSTOMER_HAS_ACTIVE_ACCOUNTS_EXCEPTION, expected.getKey());
           Assert.assertEquals(CustomerStatus.CLIENT_ACTIVE, client.getStatus());
        }
        StaticHibernateUtil.closeSession();
        client = (ClientBO) StaticHibernateUtil.getSessionTL().get(ClientBO.class, client.getCustomerId());
        group = (GroupBO) StaticHibernateUtil.getSessionTL().get(GroupBO.class, group.getCustomerId());
        center = (CenterBO) StaticHibernateUtil.getSessionTL().get(CenterBO.class, center.getCustomerId());
        accountBO = (SavingsBO) StaticHibernateUtil.getSessionTL().get(SavingsBO.class, accountBO.getAccountId());
    }

    public void testApplicablePrdforCustomLevel() throws Exception {
        createInitialObjects();
       Assert.assertEquals(Short.valueOf("1"), client.getCustomerLevel().getProductApplicableType());
       Assert.assertEquals(Short.valueOf("3"), center.getCustomerLevel().getProductApplicableType());
    }

    public void testCustomerPerformanceView() throws Exception {
        CustomerPerformanceHistoryView customerPerformanceView = new CustomerPerformanceHistoryView(Integer
                .valueOf("1"), Integer.valueOf("1"), "10");

       Assert.assertEquals(1, customerPerformanceView.getMeetingsAttended().intValue());
       Assert.assertEquals(1, customerPerformanceView.getMeetingsMissed().intValue());
       Assert.assertEquals("10", customerPerformanceView.getLastLoanAmount());

    }

    public void testCustomerPositionView() throws Exception {
        CustomerPositionView customerPositionView = new CustomerPositionView(Integer.valueOf("1"), Short.valueOf("2"));

       Assert.assertEquals(1, customerPositionView.getCustomerId().intValue());
       Assert.assertEquals(2, customerPositionView.getPositionId().shortValue());

    }

    public void testCustomerStatusFlagEntity() throws Exception {
        CustomerStatusFlagEntity customerStatusFlag = (CustomerStatusFlagEntity) TestObjectFactory.getObject(
                CustomerStatusFlagEntity.class, Short.valueOf("1"));
       Assert.assertEquals("Withdraw", customerStatusFlag.getFlagDescription());
        customerStatusFlag.setFlagDescription("Other");
       Assert.assertEquals("Other", customerStatusFlag.getFlagDescription());

    }

    private void changeFirstInstallmentDate(AccountBO accountBO, int numberOfDays) {
        Calendar currentDateCalendar = new GregorianCalendar();
        int year = currentDateCalendar.get(Calendar.YEAR);
        int month = currentDateCalendar.get(Calendar.MONTH);
        int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
        currentDateCalendar = new GregorianCalendar(year, month, day - numberOfDays);
        for (AccountActionDateEntity accountActionDateEntity : accountBO.getAccountActionDates()) {
            LoanBOTestUtils.setActionDate(accountActionDateEntity, new java.sql.Date(currentDateCalendar
                    .getTimeInMillis()));
            break;
        }
    }

    private void setActionDateToPastDate() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(DateUtils.getCurrentDateWithoutTimeStamp());
        calendar.add(calendar.WEEK_OF_MONTH, -1);
        java.sql.Date lastWeekDate = new java.sql.Date(calendar.getTimeInMillis());

        Calendar date = new GregorianCalendar();
        date.setTime(DateUtils.getCurrentDateWithoutTimeStamp());
        date.add(date.WEEK_OF_MONTH, -2);
        java.sql.Date twoWeeksBeforeDate = new java.sql.Date(date.getTimeInMillis());

        for (AccountActionDateEntity installment : accountBO.getAccountActionDates()) {
            if (installment.getInstallmentId().intValue() == 1) {
                LoanBOTestUtils.setActionDate(installment, lastWeekDate);
            } else if (installment.getInstallmentId().intValue() == 2) {
                LoanBOTestUtils.setActionDate(installment, twoWeeksBeforeDate);
            }
        }
    }

    private SavingsBO getSavingsAccount(String offeringName, String shortName) throws Exception {
        createInitialObjects();
        savingsOffering = helper.createSavingsOffering(offeringName, shortName);
        return TestObjectFactory.createSavingsAccount("000100000000017", client, AccountStates.SAVINGS_ACC_APPROVED,
                new Date(System.currentTimeMillis()), savingsOffering);
    }

    private void createInitialObjects() {
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter(this.getClass().getSimpleName() +" Center", meeting);
        group = TestObjectFactory.createGroupUnderCenter(this.getClass().getSimpleName() +" Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient(this.getClass().getSimpleName() +" Client", CustomerStatus.CLIENT_ACTIVE, group);
    }

    private AccountBO getLoanAccount(CustomerBO customer, MeetingBO meeting) {
        Date startDate = new Date(System.currentTimeMillis());
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        return TestObjectFactory.createLoanAccount("42423142341", customer, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);

    }

    private AccountBO getIndividualLoanAccount(CustomerBO customer, MeetingBO meeting) {
        Date startDate = new Date(System.currentTimeMillis());
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        return TestObjectFactory.createIndividualLoanAccount("42423142341", customer,
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, startDate, loanOffering);

    }

    private void createPersonnel(PersonnelLevel personnelLevel) throws Exception {
        List<CustomFieldView> customFieldView = new ArrayList<CustomFieldView>();
        customFieldView.add(new CustomFieldView(Short.valueOf("9"), "123456", CustomFieldType.NUMERIC));
        Address address = new Address("abcd", "abcd", "abcd", "abcd", "abcd", "abcd", "abcd", "abcd");
        Name name = new Name("XYZ", null, null, "Last Name");
        java.util.Date date = new java.util.Date();
        loanOfficer = new PersonnelBO(personnelLevel, getBranchOffice(), Integer.valueOf("1"), Short.valueOf("1"),
                "ABCD", "XYZ", "xyz@yahoo.com", null, customFieldView, name, "111111", date, Integer.valueOf("1"),
                Integer.valueOf("1"), date, date, address, Short.valueOf("1"));
        loanOfficer.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        loanOfficer = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(PersonnelBO.class,
                loanOfficer.getPersonnelId());

    }

    private void updatePersonnel(PersonnelLevel personnelLevel, PersonnelStatus newStatus, OfficeBO office)
            throws Exception {
        Address address = new Address("abcd", "abcd", "abcd", "abcd", "abcd", "abcd", "abcd", "abcd");
        Name name = new Name("XYZ", null, null, "Last Name");
        loanOfficer.update(newStatus, personnelLevel, office, Integer.valueOf("1"), Short.valueOf("1"), "ABCD",
                "rajendersaini@yahoo.com", null, null, name, Integer.valueOf("1"), Integer.valueOf("1"), address, Short
                        .valueOf("1"));
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        loanOfficer = (PersonnelBO) StaticHibernateUtil.getSessionTL().get(PersonnelBO.class,
                loanOfficer.getPersonnelId());

    }

    private void createCenter(Short officeId, Short personnelId) {
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());

        center = TestObjectFactory.createCenter(this.getClass().getSimpleName() +" Center", meeting, officeId, personnelId);
    }

    private void createGroup() {
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter(this.getClass().getSimpleName() +" Center", meeting);
        group = TestObjectFactory.createGroupUnderCenter(this.getClass().getSimpleName() +" Group", CustomerStatus.GROUP_ACTIVE, center);
    }

    @Override
    protected OfficeBO getBranchOffice() {
        return TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);

    }
}
