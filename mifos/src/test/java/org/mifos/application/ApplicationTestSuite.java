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

package org.mifos.application;

import org.junit.internal.runners.InitializationError;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.mifos.application.acceptedpaymenttype.ApplicationAcceptedPaymentTypeTestSuite;
import org.mifos.application.accounts.AccountTestSuite;
import org.mifos.application.accounts.financial.FinancialTestSuite;
import org.mifos.application.accounts.loan.LoanTestSuite;
import org.mifos.application.accounts.savings.SavingsTestSuite;
import org.mifos.application.admin.AdminTestSuite;
import org.mifos.application.branchreport.BranchReportTestSuite;
import org.mifos.application.cashconfirmationreport.BranchCashConfirmationReportTestSuite;
import org.mifos.application.checklist.CheckListTestSuite;
import org.mifos.application.collectionsheet.CollectionSheetTestSuite;
import org.mifos.application.collectionsheet.business.CollectionSheetReportTestSuite;
import org.mifos.application.configuration.ApplicationConfigurationTestSuite;
import org.mifos.application.customer.CustomerTestSuite;
import org.mifos.application.fees.FeeTestSuite;
import org.mifos.application.fund.FundTestSuite;
import org.mifos.application.holiday.HolidayTestSuite;
import org.mifos.application.login.LoginTestSuite;
import org.mifos.application.master.MasterTestSuite;
import org.mifos.application.meeting.MeetingTestSuite;
import org.mifos.application.office.OfficeTestSuite;
import org.mifos.application.personnel.PersonnelTestSuite;
import org.mifos.application.ppi.PPITestSuite;
import org.mifos.application.productdefinition.ProductDefinitionTestSuite;
import org.mifos.application.productsmix.ProductMixTestSuite;
import org.mifos.application.reports.ReportsTestSuite;
import org.mifos.application.rolesandpermission.RolesAndPermissionTestSuite;
import org.mifos.application.surveys.SurveysTestSuite;
import org.mifos.framework.components.ComponentsTestSuite;
import org.mifos.framework.components.audit.AuditLogTestSuite;
import org.mifos.framework.components.batchjobs.BatchJobTestSuite;
import org.mifos.framework.components.configuration.ConfigurationTestSuite;
import org.mifos.framework.components.fieldConfiguration.FieldConfigurationTestSuite;
import org.mifos.framework.components.mifosmenu.MenuParserIntegrationTest;
import org.mifos.framework.hibernate.HibernateIntegrationTest;
import org.mifos.framework.hibernate.helper.HibernateHelperIntegrationTest;
import org.mifos.framework.persistence.LatestTestAfterCheckpointBaseTest;
import org.mifos.framework.persistence.PersistenceIntegrationTest;
import org.mifos.framework.security.SecurityTestSuite;
import org.mifos.framework.struts.StrutsTestSuite;
import org.mifos.framework.util.helpers.FrameworkUtilsSuite;
import org.mifos.framework.util.helpers.StringToMoneyConverterIntegrationTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { //ConfigTestSuite.class, 
                       ProductMixTestSuite.class,
                       FastTests.class, 
                       SecurityTestSuite.class, 
                       CollectionSheetTestSuite.class, 
                       CustomerTestSuite.class,
                       ApplicationConfigurationTestSuite.class, 
                       MasterTestSuite.class, 
                       AccountTestSuite.class,
                       FinancialTestSuite.class, 
                       StringToMoneyConverterIntegrationTest.class, 
                       ConfigurationTestSuite.class,
                       BatchJobTestSuite.class, 
                       LoanTestSuite.class, 
                       SavingsTestSuite.class, 
                       ProductDefinitionTestSuite.class,
                       ReportsTestSuite.class, 
                       FeeTestSuite.class, 
                       FieldConfigurationTestSuite.class, 
                       OfficeTestSuite.class,
                       ComponentsTestSuite.class, 
                       PersonnelTestSuite.class, 
                       RolesAndPermissionTestSuite.class, 
                       MeetingTestSuite.class,
                       LoginTestSuite.class, 
                       FundTestSuite.class, 
                       AuditLogTestSuite.class, 
                       CheckListTestSuite.class,
                       AdminTestSuite.class, 
                       StrutsTestSuite.class, 
                       HibernateIntegrationTest.class,
                       LatestTestAfterCheckpointBaseTest.class, 
                       MayflyMiscTest.class, 
                       MenuParserIntegrationTest.class,
                       HibernateHelperIntegrationTest.class, 
                       PersistenceIntegrationTest.class, 
                       FrameworkUtilsSuite.class,
                       HolidayTestSuite.class, 
                       SurveysTestSuite.class, 
                       PPITestSuite.class,
                       ApplicationAcceptedPaymentTypeTestSuite.class, 
                       CollectionSheetTestSuite.class,
                       CollectionSheetReportTestSuite.class, 
                       BranchReportTestSuite.class, 
                       BranchCashConfirmationReportTestSuite.class,
                       IntegrationTests.class })
public class ApplicationTestSuite extends Suite {
    // placeholder class for above annotations
    public ApplicationTestSuite(Class<?> klass) throws InitializationError {
        super(klass);
    }
}
