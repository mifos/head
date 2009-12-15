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

package org.mifos.application.accounts.loan.business.service;

import static java.util.Arrays.asList;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanActivityView;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.persistance.LoanPersistence;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.configuration.business.service.ConfigurationBusinessService;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LoanBusinessServiceIntegrationTest extends MifosIntegrationTestCase {

    public LoanBusinessServiceIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    protected AccountBO accountBO = null;

    protected CustomerBO center = null;

    protected CustomerBO group = null;

    protected AccountPersistence accountPersistence;

    protected LoanBusinessService loanBusinessService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        loanBusinessService = new LoanBusinessService();
        accountPersistence = new AccountPersistence();
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            accountBO = (AccountBO) StaticHibernateUtil.getSessionTL().get(AccountBO.class, accountBO.getAccountId());
            group = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, group.getCustomerId());
            center = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, center.getCustomerId());
            TestObjectFactory.cleanUp(accountBO);
            TestObjectFactory.cleanUp(group);
            TestObjectFactory.cleanUp(center);
        } catch (Exception e) {
            // TODO Whoops, cleanup didnt work, reset db
            TestDatabase.resetMySQLDatabase();
        }

        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testFindBySystemId() throws Exception {
        accountBO = getLoanAccount();
        loanBusinessService = new LoanBusinessService();
        LoanBO loanBO = loanBusinessService.findBySystemId(accountBO.getGlobalAccountNum());
       Assert.assertEquals(loanBO.getGlobalAccountNum(), accountBO.getGlobalAccountNum());
       Assert.assertEquals(loanBO.getAccountId(), accountBO.getAccountId());
    }

    public void testFindIndividualLoans() throws Exception {
        accountBO = getLoanAccount();
        loanBusinessService = new LoanBusinessService();
        List<LoanBO> listLoanBO = loanBusinessService.findIndividualLoans(accountBO.getAccountId().toString());
       Assert.assertEquals(0, listLoanBO.size());
    }

    public void testGetLoanAccountsActiveInGoodBadStanding() throws Exception {
        accountBO = getLoanAccount();
        loanBusinessService = new LoanBusinessService();
        List<LoanBO> loanBO = loanBusinessService.getLoanAccountsActiveInGoodBadStanding(accountBO.getCustomer()
                .getCustomerId());
       Assert.assertEquals(Short.valueOf("1"), loanBO.get(0).getAccountType().getAccountTypeId());
        Assert.assertNotNull(loanBO.size());

    }

    public void testGetRecentActivityView() throws SystemException, NumberFormatException, ApplicationException {
        Date startDate = new Date(System.currentTimeMillis());
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter(this.getClass().getSimpleName() + "_Center_Active", meeting);
        group = TestObjectFactory.createGroupUnderCenter(this.getClass().getSimpleName() + "_Group", CustomerStatus.GROUP_ACTIVE, center);
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        accountBO = TestObjectFactory.createLoanAccount("42423142341", group,
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, startDate, loanOffering);
        StaticHibernateUtil.closeSession();

        applyPayments();

        accountBO = accountPersistence.getAccount(accountBO.getAccountId());
        List<LoanActivityView> loanRecentActivityView = loanBusinessService.getRecentActivityView(accountBO
                .getGlobalAccountNum());

       Assert.assertEquals(3, loanRecentActivityView.size());
        Assert.assertNotNull(loanRecentActivityView);
    }

    private void applyPayments() throws PersistenceException, AccountException {
        Set<AccountActionDateEntity> actionDates = accountBO.getAccountActionDates();
        // Is this always true or does it depend on System.currentTimeMillis?
        //Assert.assertEquals(6, actionDates.size());
        for (AccountActionDateEntity actionDate : actionDates) {
            Assert.assertNotNull(actionDate);
            accountBO = accountPersistence.getAccount(accountBO.getAccountId());
            PaymentData paymentData = createPaymentViewObject(accountBO);
            accountBO.applyPaymentWithPersist(paymentData);
            TestObjectFactory.updateObject(accountBO);
        }
    }

    public void testGetAllActivityView() throws SystemException, NumberFormatException, ApplicationException {
        Date startDate = new Date(System.currentTimeMillis());
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter(this.getClass().getSimpleName() + "_Center_Active", meeting);
        group = TestObjectFactory.createGroupUnderCenter(this.getClass().getSimpleName() + "_Group", CustomerStatus.GROUP_ACTIVE, center);
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        accountBO = TestObjectFactory.createLoanAccount("42423142341", group,
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, startDate, loanOffering);
        StaticHibernateUtil.closeSession();

        applyPayments();

        accountBO = accountPersistence.getAccount(accountBO.getAccountId());
        List<LoanActivityView> loanAllActivityView = loanBusinessService.getAllActivityView(accountBO
                .getGlobalAccountNum());

        Assert.assertNotNull(loanAllActivityView);
       Assert.assertEquals(6, loanAllActivityView.size());
        for (LoanActivityView view : loanAllActivityView) {
            Assert.assertNotNull(view.getActivity());
            Assert.assertNotNull(view.getUserPrefferedDate());
            Assert.assertNotNull(view.getActionDate().getTime());
           Assert.assertEquals(new Money(getCurrency(), "100.0"), view.getFees());
            Assert.assertNotNull(view.getId());
           Assert.assertEquals(new Money(getCurrency(), "12.0"), view.getInterest());
            Assert.assertNull(view.getLocale());
           Assert.assertEquals(new Money(getCurrency(), "0.0"), view.getPenalty());
           Assert.assertEquals(new Money(getCurrency(), "100.0"), view.getPrincipal());
           Assert.assertEquals(new Money(getCurrency(), "212.0"), view.getTotal());
            Assert.assertNotNull(view.getTimeStamp());
           Assert.assertEquals(new Money(getCurrency(), "-100.0"), view.getRunningBalanceFees());
           Assert.assertEquals(new Money(getCurrency(), "24.0"), view.getRunningBalanceInterest());
           Assert.assertEquals(new Money(getCurrency(), "0.0"), view.getRunningBalancePenalty());
           Assert.assertEquals(new Money(getCurrency(), "200.0"), view.getRunningBalancePrinciple());
            break;
        }
    }

    public void testGetAllLoanAccounts() throws Exception {
        accountBO = getLoanAccount();
        loanBusinessService = new LoanBusinessService();
        List<LoanBO> loanAccounts = loanBusinessService.getAllLoanAccounts();
        Assert.assertNotNull(loanAccounts);
       Assert.assertEquals(1, loanAccounts.size());
    }

    private AccountBO getLoanAccount() {
        Date startDate = new Date(System.currentTimeMillis());
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter(this.getClass().getSimpleName() + "_Center", meeting);
        group = TestObjectFactory.createGroupUnderCenter(this.getClass().getSimpleName() + "_Group", CustomerStatus.GROUP_ACTIVE, center);
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        return TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);
    }

    private PaymentData createPaymentViewObject(final AccountBO accountBO) {
        PaymentData paymentData = PaymentData.createPaymentData(new Money(TestObjectFactory.getMFICurrency(), "212.0"),
                accountBO.getPersonnel(), Short.valueOf("1"), new Date(System.currentTimeMillis()));
        paymentData.setReceiptDate(new Date(System.currentTimeMillis()));
        paymentData.setReceiptNum("423423");
        return paymentData;
    }

    public void testgetActiveLoansForAllClientsUnderGroup() throws Exception {
        int groupLoanAccountId = 1;
        GroupBO groupMock = createMock(GroupBO.class);
        CustomerBO clientMock = createMock(ClientBO.class);
        LoanBO groupLoanMock = createMock(LoanBO.class);
        ConfigurationBusinessService configServiceMock = createMock(ConfigurationBusinessService.class);
        LoanBO loanMock1 = createMock(LoanBO.class);
        LoanBO loanMock2 = createMock(LoanBO.class);
        AccountBusinessService accountBusinessServiceMock = createMock(AccountBusinessService.class);

        expect(accountBusinessServiceMock.getCoSigningClientsForGlim(groupLoanAccountId)).andReturn(
                Arrays.asList(clientMock));
        expect(configServiceMock.isGlimEnabled()).andReturn(true);
        expect(loanMock1.isActiveLoanAccount()).andReturn(true);
        expect(loanMock2.isActiveLoanAccount()).andReturn(false);
        expect(groupLoanMock.getAccountId()).andReturn(groupLoanAccountId);

        expect(clientMock.getAccounts()).andReturn(new HashSet<AccountBO>(asList(loanMock1, loanMock2)));

        replay(groupMock, clientMock, loanMock1, loanMock2, groupLoanMock, configServiceMock,
                accountBusinessServiceMock);

       Assert.assertEquals(asList(loanMock1), new LoanBusinessService(new LoanPersistence(), configServiceMock,
                accountBusinessServiceMock).getActiveLoansForAllClientsAssociatedWithGroupLoan(groupLoanMock));

        verify(groupMock, clientMock, loanMock1, loanMock2, groupLoanMock, configServiceMock,
                accountBusinessServiceMock);

    }

}
