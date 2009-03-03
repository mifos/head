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
package org.mifos.application.reports.business.service;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.application.productdefinition.business.service.SavingsPrdBusinessService;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ConfigServiceInitializationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.FilePaths;
import org.springframework.core.io.ClassPathResource;


public class ReportProductOfferingServiceTest extends MifosIntegrationTest {

	public ReportProductOfferingServiceTest() throws SystemException, ApplicationException {
        super();
    }

    private static final Short SAVINGS_OFFERING_2_ID = Short.valueOf("2");
	private static final Short SAVINGS_OFFERING_1_ID = Short.valueOf("1");
	private static final Short LOAN_OFFERING_2_ID = Short.valueOf("2");
	private static final Short LOAN_OFFERING_1_ID = Short.valueOf("1");
	private ReportProductOfferingService reportProductOfferingService;
	private LoanPrdBusinessService loanPrdBusinessServiceMock;
	private SavingsPrdBusinessService savingsProductBusinessServiceMock;
	private ClassPathResource reportProductOfferingConfig;

	public void testGetLoanOffering1ReturnsLoanOffering() throws Exception {
		LoanOfferingBO loanOffering1 = LoanOfferingBO
				.createInstanceForTest(LOAN_OFFERING_1_ID);
		expect(loanPrdBusinessServiceMock.getLoanOffering(LOAN_OFFERING_1_ID))
				.andReturn(loanOffering1);
		replay(loanPrdBusinessServiceMock);
		LoanOfferingBO retrievedLoanOffering = reportProductOfferingService
				.getLoanOffering1();
		verify(loanPrdBusinessServiceMock);
		assertEquals(loanOffering1, retrievedLoanOffering);
	}

	public void testGetLoanOffering2ReturnsLoanOffering() throws Exception {
		LoanOfferingBO loanOffering2 = LoanOfferingBO
				.createInstanceForTest(LOAN_OFFERING_2_ID);
		expect(loanPrdBusinessServiceMock.getLoanOffering(LOAN_OFFERING_2_ID))
				.andReturn(loanOffering2);
		replay(loanPrdBusinessServiceMock);
		LoanOfferingBO retrievedLoanOffering = reportProductOfferingService
				.getLoanOffering2();
		verify(loanPrdBusinessServiceMock);
		assertEquals(loanOffering2, retrievedLoanOffering);
	}

	public void testGetSavingsOffering1ReturnsSavingsOffering()
			throws Exception {
		SavingsOfferingBO savingsOffering1 = SavingsOfferingBO
				.createInstanceForTest(SAVINGS_OFFERING_1_ID);
		expect(
				savingsProductBusinessServiceMock
						.getSavingsProduct(SAVINGS_OFFERING_1_ID)).andReturn(
				savingsOffering1);
		replay(savingsProductBusinessServiceMock);
		SavingsOfferingBO retrievedSavingsOffering = reportProductOfferingService
				.getSavingsOffering1();
		verify(savingsProductBusinessServiceMock);
		assertEquals(savingsOffering1, retrievedSavingsOffering);
	}

	public void testGetSavingsOffering2ReturnsSavingsOffering()
			throws Exception {
		SavingsOfferingBO savingsOffering2 = SavingsOfferingBO
				.createInstanceForTest(SAVINGS_OFFERING_2_ID);
		expect(
				savingsProductBusinessServiceMock
						.getSavingsProduct(SAVINGS_OFFERING_2_ID)).andReturn(
				savingsOffering2);
		replay(savingsProductBusinessServiceMock);
		SavingsOfferingBO retrievedSavingsOffering = reportProductOfferingService
				.getSavingsOffering2();
		verify(savingsProductBusinessServiceMock);
		assertEquals(savingsOffering2, retrievedSavingsOffering);
	}

	public void testFindsReportProductOfferingConfigFile() throws Exception {
		try {
			reportProductOfferingService = new ReportProductOfferingService(
					null, null, reportProductOfferingConfig);
		}
		catch (ConfigServiceInitializationException e) {
			fail("Was not able to find reportproductoffering config file" + e);
		}
	}
	
	public void testFindsSignatureConfigProperties() throws Exception {
		retrieveAndAssertSignatureColumn("1");
		retrieveAndAssertSignatureColumn("2");
		retrieveAndAssertSignatureColumn("3");
		retrieveAndAssertSignatureColumn("4");
		retrieveAndAssertSignatureColumn("5");
		retrieveAndAssertSignatureColumn("6");
	}

	private void retrieveAndAssertSignatureColumn(String signatureColumnNumber) {
		assertTrue("could not find signature column "+signatureColumnNumber, reportProductOfferingService
				.isPropertyPresent(ReportConfigServiceConstants.DISPLAY_SIGNATURE_COLUMN+"."
						+ signatureColumnNumber));
	}

	public void testThrowsExceptionIfConfigFileNotFound() throws Exception {
		try {
			reportProductOfferingService = new ReportProductOfferingService(
					null, null, new ClassPathResource(
							"/abcd/filenotexist.properties"));
			fail("Was not able to find reportproductoffering config file");
		}
		catch (ConfigServiceInitializationException e) {
		}
	}

	@Override
	protected void setUp() throws Exception {
		loanPrdBusinessServiceMock = createMock(LoanPrdBusinessService.class);
		savingsProductBusinessServiceMock = createMock(SavingsPrdBusinessService.class);
		reportProductOfferingConfig = new ClassPathResource(
				FilePaths.REPORT_PRODUCT_OFFERING_CONFIG);
		reportProductOfferingService = new ReportProductOfferingService(
				loanPrdBusinessServiceMock, savingsProductBusinessServiceMock,
				reportProductOfferingConfig) {
			@Override
			protected Short getLoanProduct1() {
				return LOAN_OFFERING_1_ID;
			}

			@Override
			protected Short getLoanProduct2() {
				return LOAN_OFFERING_2_ID;
			}

			@Override
			protected Short getSavingsProduct1() {
				return SAVINGS_OFFERING_1_ID;
			}

			@Override
			protected Short getSavingsProduct2() {
				return SAVINGS_OFFERING_2_ID;
			}


		};
	}
}
