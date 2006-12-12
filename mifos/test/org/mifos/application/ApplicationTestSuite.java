/**

 * ApplicationTestSuite.java    version: xxx



 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.



 * Apache License
 * Copyright (c) 2005-2006 Grameen Foundation USA
 *

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the

 * License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license

 * and how it is applied.

 *

 */

package org.mifos.application;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.mifos.application.accounts.AccountTestSuite;
import org.mifos.application.accounts.financial.FinancialTestSuite;
import org.mifos.application.accounts.loan.LoanTestSuite;
import org.mifos.application.accounts.savings.SavingsTestSuite;
import org.mifos.application.admin.AdminTestSuite;
import org.mifos.application.bulkentry.BulkEntryTestSuite;
import org.mifos.application.checklist.CheckListTestSuite;
import org.mifos.application.collectionsheet.CollectionSheetTestSuite;
import org.mifos.application.configuration.LabelConfigurationTestSuite;
import org.mifos.application.customer.CustomerTestSuite;
import org.mifos.application.fees.FeeTestSuite;
import org.mifos.application.fund.FundTestSuite;
import org.mifos.application.login.LoginTestSuite;
import org.mifos.application.master.MasterTestSuite;
import org.mifos.application.meeting.MeetingTestSuite;
import org.mifos.application.office.OfficeTestSuite;
import org.mifos.application.office.struts.tag.OfficeListTagTest;
import org.mifos.application.personnel.PersonnelTestSuite;
import org.mifos.application.productdefinition.ProductDefinitionTestSuite;
import org.mifos.application.reports.ReportsTestSuite;
import org.mifos.application.rolesandpermission.RolesAndPermissionTestSuite;
import org.mifos.framework.components.ComponentsTestSuite;
import org.mifos.framework.components.audit.TestAuditLogSuite;
import org.mifos.framework.components.configuration.ConfigurationTestSuite;
import org.mifos.framework.components.cronjob.CronjobTestSuite;
import org.mifos.framework.components.fieldConfiguration.FieldConfigurationTestSuite;
import org.mifos.framework.components.mifosmenu.TestMenuParser;
import org.mifos.framework.exceptions.FrameworkExceptionTest;
import org.mifos.framework.hibernate.HibernateTest;
import org.mifos.framework.hibernate.helper.TestHibernateHelper;
import org.mifos.framework.persistence.DatabaseVersionPersistenceTest;
import org.mifos.framework.persistence.TestPersistence;
import org.mifos.framework.security.SecurityTestSuite;
import org.mifos.framework.struts.StrutsTestSuite;
import org.mifos.framework.util.helpers.FrameworkUtilsSuite;
import org.mifos.framework.util.helpers.MethodInvokerTest;
import org.mifos.framework.util.helpers.StringToMoneyConverterTest;

public class ApplicationTestSuite extends TestSuite {

	public ApplicationTestSuite() throws Exception {
	}

	public static void main(String[] args) throws Exception {
		Test testSuite = suite();
		TestRunner.run(testSuite);
	}

	public static Test suite() throws Exception {
		TestSuite suite = new ApplicationTestSuite();
		suite.addTest(SecurityTestSuite.suite());
		suite.addTest(CollectionSheetTestSuite.suite());
		suite.addTest(CustomerTestSuite.suite());
		suite.addTest(BulkEntryTestSuite.suite());
		suite.addTest(MasterTestSuite.suite());
		suite.addTest(AccountTestSuite.suite());
		suite.addTest(FinancialTestSuite.suite());
		suite.addTest(FastTests.suite());
		suite.addTestSuite(OfficeListTagTest.class);
		suite.addTestSuite(StringToMoneyConverterTest.class);
		suite.addTest(ConfigurationTestSuite.suite());
		suite.addTest(CronjobTestSuite.suite());
		suite.addTest(LabelConfigurationTestSuite.suite());
		suite.addTest(LoanTestSuite.suite());
		suite.addTest(SavingsTestSuite.suite());
		suite.addTest(ProductDefinitionTestSuite.suite());
		suite.addTest(ReportsTestSuite.suite());
		suite.addTest(FeeTestSuite.suite());
		suite.addTest(FieldConfigurationTestSuite.suite());
		suite.addTest(OfficeTestSuite.suite());
		suite.addTest(ComponentsTestSuite.suite());
		suite.addTest(PersonnelTestSuite.suite());
		suite.addTest(RolesAndPermissionTestSuite.suite());
		suite.addTest(MeetingTestSuite.suite());
		suite.addTest(LoginTestSuite.suite());
		suite.addTest(FundTestSuite.suite());
		suite.addTest(TestAuditLogSuite.suite());
		suite.addTest(CheckListTestSuite.suite());
		suite.addTest(AdminTestSuite.suite());
		suite.addTest(StrutsTestSuite.suite());

		suite.addTestSuite(HibernateTest.class);
		suite.addTestSuite(DatabaseVersionPersistenceTest.class);

		suite.addTestSuite(TestMenuParser.class);
		suite.addTestSuite(TestHibernateHelper.class);
		suite.addTestSuite(TestPersistence.class);
		suite.addTestSuite(FrameworkExceptionTest.class);
		suite.addTestSuite(MethodInvokerTest.class);
		suite.addTest(FrameworkUtilsSuite.suite());
		return suite;
	}
}

