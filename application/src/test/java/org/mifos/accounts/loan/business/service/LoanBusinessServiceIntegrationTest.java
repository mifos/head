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

package org.mifos.accounts.loan.business.service;

import static java.util.Arrays.asList;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.ScheduleCalculatorAdaptor;
import org.mifos.accounts.loan.persistance.LegacyLoanDao;
import org.mifos.accounts.persistence.LegacyAccountDao;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.application.holiday.business.service.HolidayService;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.config.business.service.ConfigurationBusinessService;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class LoanBusinessServiceIntegrationTest extends MifosIntegrationTestCase {

    protected AccountBO accountBO = null;

    protected CustomerBO center = null;

    protected CustomerBO group = null;

    @Autowired
    protected LegacyAccountDao legacyAccountDao;

    @Autowired
    protected LoanBusinessService loanBusinessService;

    @Autowired
    private LegacyLoanDao legacyLoanDao;

    @Test
    public void testFindBySystemId() throws Exception {
        accountBO = getLoanAccount();
        LoanBO loanBO = loanBusinessService.findBySystemId(accountBO.getGlobalAccountNum());
        Assert.assertEquals(loanBO.getGlobalAccountNum(), accountBO.getGlobalAccountNum());
        Assert.assertEquals(loanBO.getAccountId(), accountBO.getAccountId());
    }

    @Test
    public void testFindIndividualLoans() throws Exception {
        accountBO = getLoanAccount();
        List<LoanBO> listLoanBO = loanBusinessService.findIndividualLoans(accountBO.getAccountId().toString());
        Assert.assertEquals(0, listLoanBO.size());
    }

    @Test
    public void testGetLoanAccountsActiveInGoodBadStanding() throws Exception {
        accountBO = getLoanAccount();
        List<LoanBO> loanBO = loanBusinessService.getLoanAccountsActiveInGoodBadStanding(accountBO.getCustomer()
                .getCustomerId());
        Assert.assertEquals(Short.valueOf("1"), loanBO.get(0).getAccountType().getAccountTypeId());
        Assert.assertNotNull(loanBO.size());

    }

    @Test
    public void testGetAllLoanAccounts() throws Exception {
        int initialLoanAccountsSize = loanBusinessService.getAllLoanAccounts().size();
        accountBO = getLoanAccount();
        List<LoanBO> loanAccounts = loanBusinessService.getAllLoanAccounts();
        Assert.assertNotNull(loanAccounts);
        Assert.assertEquals(initialLoanAccountsSize+1, loanAccounts.size());
    }

    private AccountBO getLoanAccount() {
        Date startDate = new Date(System.currentTimeMillis());
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter(this.getClass().getSimpleName() + "_Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter(this.getClass().getSimpleName() + "_Group",
                CustomerStatus.GROUP_ACTIVE, center);
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        return TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);
    }

    private PaymentData createPaymentViewObject(final AccountBO accountBO) {
        PaymentData paymentData = PaymentData.createPaymentData(TestUtils.createMoney("212.0"),
                accountBO.getPersonnel(), Short.valueOf("1"), new Date(System.currentTimeMillis()));
        paymentData.setReceiptDate(new Date(System.currentTimeMillis()));
        paymentData.setReceiptNum("423423");
        return paymentData;
    }

    @Test
    public void testGetActiveLoansForAllClientsUnderGroup() throws Exception {
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

        Assert.assertEquals(asList(loanMock1), new LoanBusinessService(legacyLoanDao, configServiceMock,
                accountBusinessServiceMock, createMock(HolidayService.class), createMock(ScheduleCalculatorAdaptor.class)).getActiveLoansForAllClientsAssociatedWithGroupLoan(groupLoanMock));

        verify(groupMock, clientMock, loanMock1, loanMock2, groupLoanMock, configServiceMock,
                accountBusinessServiceMock);
    }
}