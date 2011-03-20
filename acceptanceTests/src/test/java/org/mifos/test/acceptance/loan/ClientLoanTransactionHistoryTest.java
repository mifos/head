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

package org.mifos.test.acceptance.loan;

import org.dbunit.DatabaseUnitException;
import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountStatusParameters;
import org.mifos.test.acceptance.framework.loan.PaymentParameters;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"acceptance", "ui", "loan", "no_db_unit"})
public class ClientLoanTransactionHistoryTest extends UiTestCaseBase {
    private LoanTestHelper loanTestHelper;

    private DateTime systemDateTime;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();

        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        systemDateTime = new DateTime(2011, 3, 4, 12, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(systemDateTime);

        loanTestHelper = new LoanTestHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    //http://mifosforge.jira.com/browse/MIFOSTEST-361
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyTransactionHistoryWithDoubleEntryAccounting() throws DatabaseUnitException, SQLException, IOException, URISyntaxException {

        DisburseLoanParameters disburseParameters = new DisburseLoanParameters();
        disburseParameters.setDisbursalDateDD(Integer.toString(systemDateTime.getDayOfMonth()));
        disburseParameters.setDisbursalDateMM(Integer.toString(systemDateTime.getMonthOfYear()));
        disburseParameters.setDisbursalDateYYYY(Integer.toString(systemDateTime.getYear()));
        disburseParameters.setPaymentType(PaymentParameters.CASH);

        PaymentParameters paymentParameters = new PaymentParameters();
        paymentParameters.setAmount("200.0");
        DateTime paymentDate = systemDateTime.plusDays(10);
        paymentParameters.setTransactionDateDD(Integer.toString(paymentDate.getDayOfMonth()));
        paymentParameters.setTransactionDateMM(Integer.toString(paymentDate.getMonthOfYear()));
        paymentParameters.setTransactionDateYYYY(Integer.toString(paymentDate.getYear()));
        paymentParameters.setPaymentType(PaymentParameters.CASH);

        //Then
        String loanId = "000100000000050";
        loanTestHelper.verifyLastEntryInStatusHistory(loanId, EditLoanAccountStatusParameters.PENDING_APPROVAL, EditLoanAccountStatusParameters.APPROVED);
        //When
        loanTestHelper.disburseLoan(loanId, disburseParameters);
        loanTestHelper.setApplicationTime(paymentDate);
        loanTestHelper.applyPayment(loanId, paymentParameters);
        //Then
        loanTestHelper.verifyTransactionHistory(loanId, Double.valueOf(paymentParameters.getAmount()));
    }
}