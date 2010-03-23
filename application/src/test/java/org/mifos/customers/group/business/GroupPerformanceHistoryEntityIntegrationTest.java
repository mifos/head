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

package org.mifos.customers.group.business;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.mifos.framework.util.helpers.MoneyUtils.zero;

import java.util.Arrays;

import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.config.business.service.ConfigurationBusinessService;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.client.business.ClientPerformanceHistoryEntity;
import org.mifos.framework.MifosIntegrationTestCase;


public class GroupPerformanceHistoryEntityIntegrationTest extends MifosIntegrationTestCase {

    public GroupPerformanceHistoryEntityIntegrationTest() throws Exception {
        super();
    }

    private LoanBO loan;
    private ConfigurationBusinessService configServiceMock;
    private AccountBusinessService accountBusinessServiceMock;
    private LoanOfferingBO loanOffering;
    private CustomerBO customerMock;
    private ClientPerformanceHistoryEntity clientPerfHistoryMock;

    public void testUpdateOnDisbursementGetsCoSigningClientsForGlim() throws Exception {
        expect(configServiceMock.isGlimEnabled()).andReturn(true);
        clientPerfHistoryMock.updateOnDisbursement(loanOffering);

        expect(customerMock.getPerformanceHistory()).andReturn(clientPerfHistoryMock);
        expectLastCall().atLeastOnce();

        expect(accountBusinessServiceMock.getCoSigningClientsForGlim(loan.getAccountId())).andReturn(
                Arrays.asList(customerMock));
        replay(configServiceMock, accountBusinessServiceMock, customerMock, clientPerfHistoryMock);

        new GroupPerformanceHistoryEntity(configServiceMock, accountBusinessServiceMock).updateOnDisbursement(loan,
                zero());
        verify(configServiceMock, accountBusinessServiceMock, customerMock, clientPerfHistoryMock);
    }

    public void testUpdateOnDisbursementDoesNotGetCoSigningClientsIfNotGlim() throws Exception {
        expect(configServiceMock.isGlimEnabled()).andReturn(false);
        replay(configServiceMock, accountBusinessServiceMock);
        new GroupPerformanceHistoryEntity(configServiceMock, accountBusinessServiceMock).updateOnDisbursement(loan,
                zero());
        verify(configServiceMock, accountBusinessServiceMock);
    }

    public void testUpdateOnWriteOffDoesNotGetCoSigningClientsIfNotGlim() throws Exception {
        expect(configServiceMock.isGlimEnabled()).andReturn(false);
        replay(configServiceMock, accountBusinessServiceMock);
        new GroupPerformanceHistoryEntity(configServiceMock, accountBusinessServiceMock).updateOnWriteOff(loan);
        verify(configServiceMock, accountBusinessServiceMock);
    }

    public void testUpdateOnWriteOffGetsCoSigningClientsForGlim() throws Exception {
        expect(configServiceMock.isGlimEnabled()).andReturn(true);
        expect(customerMock.getPerformanceHistory()).andReturn(clientPerfHistoryMock);
        expectLastCall().atLeastOnce();
        clientPerfHistoryMock.updateOnWriteOff(loanOffering);
        expect(accountBusinessServiceMock.getCoSigningClientsForGlim(loan.getAccountId())).andReturn(
                Arrays.asList(customerMock));
        replay(configServiceMock, accountBusinessServiceMock, customerMock, clientPerfHistoryMock);
        new GroupPerformanceHistoryEntity(configServiceMock, accountBusinessServiceMock).updateOnWriteOff(loan);
        verify(configServiceMock, accountBusinessServiceMock, customerMock, clientPerfHistoryMock);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        loanOffering = LoanOfferingBO.createInstanceForTest((short) 1);
        loan = LoanBO.createInstanceForTest(loanOffering);
        configServiceMock = createMock(ConfigurationBusinessService.class);
        accountBusinessServiceMock = createMock(AccountBusinessService.class);
        clientPerfHistoryMock = createMock(ClientPerformanceHistoryEntity.class);
        customerMock = createMock(CustomerBO.class);
    }
}
