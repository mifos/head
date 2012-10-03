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

package org.mifos.accounts.loan.struts.action;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.struts.actionforms.LoanAccountActionForm;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.config.business.service.ConfigurationBusinessService;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.dto.domain.LoanAccountDetailsDto;
import org.mifos.framework.MifosIntegrationTestCase;

public class LoanAccountActionIndividualLoansIntegrationTest extends MifosIntegrationTestCase {

    @Test
    public void testShouldCallUpdateMethodIfExistingMembersChanged() throws Exception {
        GlimLoanUpdater glimLoanUpdaterMock = createMock(GlimLoanUpdater.class);
        LoanAccountAction loanAccountAction = new LoanAccountAction(ApplicationContextProvider.getBean(LoanBusinessService.class),
                new ConfigurationBusinessService(), glimLoanUpdaterMock);
        LoanBO loanMock = createMock(LoanBO.class);
        Locale locale = Locale.ENGLISH;
        expect(loanMock.getAccountId()).andReturn(2).anyTimes();
        ClientBO customerMock = createMock(ClientBO.class);
        expect(loanMock.getCustomer()).andReturn(customerMock).anyTimes();
        expect(customerMock.getCustomerId()).andReturn(3).anyTimes();
        LoanAccountActionForm loanAccountActionForm = new LoanAccountActionForm();
        List<LoanAccountDetailsDto> clientDetails = new ArrayList<LoanAccountDetailsDto>();
        LoanAccountDetailsDto LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_300 = LoanAccountDetailsDto
                .createInstanceForTest("3", "2", "300.0", "2");
        clientDetails.add(LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_300);
        List<LoanBO> loans = new ArrayList<LoanBO>();
        loans.add(loanMock);
        glimLoanUpdaterMock.updateIndividualLoan(null, null, LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_300, loanMock);
        expectLastCall().atLeastOnce();
        replay(glimLoanUpdaterMock, loanMock, customerMock);
        loanAccountAction.handleIndividualLoans(loanMock, loanAccountActionForm, true, clientDetails, loans, locale);
        verify(glimLoanUpdaterMock, loanMock, customerMock);
    }

    @Test
    public void testShouldCallDeleteMethodIfExistingMembersRemoved() throws Exception {
        GlimLoanUpdater glimLoanUpdaterMock = createMock(GlimLoanUpdater.class);
        LoanAccountAction loanAccountAction = new LoanAccountAction(ApplicationContextProvider.getBean(LoanBusinessService.class),
                new ConfigurationBusinessService(), glimLoanUpdaterMock);
        LoanBO loanMock = createMock(LoanBO.class);
        Locale locale = Locale.ENGLISH;
        expect(loanMock.getAccountId()).andReturn(2).anyTimes();
        ClientBO customerMock = createMock(ClientBO.class);
        expect(loanMock.getCustomer()).andReturn(customerMock).anyTimes();
        expect(customerMock.getGlobalCustNum()).andReturn("3").anyTimes();
        LoanAccountActionForm loanAccountActionForm = new LoanAccountActionForm();
        List<LoanAccountDetailsDto> clientDetails = new ArrayList<LoanAccountDetailsDto>();
        List<LoanBO> loans = new ArrayList<LoanBO>();
        loans.add(loanMock);
        glimLoanUpdaterMock.delete(loanMock);
        expectLastCall().atLeastOnce();
        replay(glimLoanUpdaterMock, loanMock, customerMock);
        loanAccountAction.handleIndividualLoans(loanMock, loanAccountActionForm, true, clientDetails, loans, locale);
        verify(glimLoanUpdaterMock, loanMock, customerMock);

    }
}
