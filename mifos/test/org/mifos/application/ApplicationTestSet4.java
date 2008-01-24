/*
 * Copyright (c) 2005-2007 Grameen Foundation USA
 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
package org.mifos.application;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.mifos.application.acceptedpaymenttype.ApplicationAcceptedPaymentTypeTestSuite;
import org.mifos.application.accounts.AccountTestSuite;
import org.mifos.application.admin.AdminTestSuite;
import org.mifos.application.checklist.CheckListTestSuite;
import org.mifos.application.fund.FundTestSuite;
import org.mifos.application.holiday.HolidayTestSuite;
import org.mifos.application.login.LoginTestSuite;
import org.mifos.application.personnel.PersonnelTestSuite;
import org.mifos.application.ppi.PPITestSuite;
import org.mifos.application.surveys.SurveysTestSuite;
import org.mifos.config.TestProcessFlowRules;
import org.mifos.framework.components.audit.TestAuditLogSuite;
import org.mifos.framework.components.mifosmenu.TestMenuParser;
import org.mifos.framework.hibernate.HibernateTest;
import org.mifos.framework.hibernate.helper.TestHibernateHelper;
import org.mifos.framework.persistence.LatestTest;
import org.mifos.framework.persistence.TestPersistence;
import org.mifos.framework.struts.StrutsTestSuite;
import org.mifos.framework.util.helpers.FrameworkUtilsSuite;

public class ApplicationTestSet4 extends TestSuite {

	public ApplicationTestSet4() throws Exception {
	}

	public static void main(String[] args) throws Exception {
		Test testSuite = suite();
		TestRunner.run(testSuite);
	}

	public static Test suite() throws Exception {
		TestSuite suite = new ApplicationTestSet4();
		
		suite.addTest(AccountTestSuite.suite());
		
		suite.addTest(PersonnelTestSuite.suite());
		
		suite.addTest(LoginTestSuite.suite());
		suite.addTest(FundTestSuite.suite());
		suite.addTest(TestAuditLogSuite.suite());
		suite.addTest(CheckListTestSuite.suite());
		suite.addTest(AdminTestSuite.suite());
		suite.addTest(StrutsTestSuite.suite());

		suite.addTest(LatestTest.suite());		
		suite.addTestSuite(HibernateTest.class);
		suite.addTestSuite(MayflyMiscTest.class);

		suite.addTestSuite(TestMenuParser.class);
		suite.addTestSuite(TestHibernateHelper.class);
		suite.addTestSuite(TestPersistence.class);
		suite.addTest(FrameworkUtilsSuite.suite());
		suite.addTest(HolidayTestSuite.suite());
		suite.addTest(SurveysTestSuite.suite());
		suite.addTest(PPITestSuite.suite());
		suite.addTest(ApplicationAcceptedPaymentTypeTestSuite.suite());
		suite.addTest(TestProcessFlowRules.suite());
		
		return suite;
	}
}

