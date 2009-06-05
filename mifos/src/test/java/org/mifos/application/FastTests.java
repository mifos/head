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

import junit.framework.Test;
import junit.framework.TestSuite;
import org.mifos.application.accounts.business.AccountBOIntegrationTest;
import org.mifos.application.collectionsheet.business.BulkEntryAccountFeeActionViewTest;
import org.mifos.application.customer.business.CustomFieldViewTest;
import org.mifos.application.customer.client.struts.actionforms.ClientCustActionFormTest;
import org.mifos.application.customer.struts.actionforms.CustomerActionFormTest;
import org.mifos.application.customer.util.helpers.CustomerStatusFlagTest;
import org.mifos.application.customer.util.helpers.LoanCycleCounterTest;
import org.mifos.application.master.business.MifosCurrencyTest;
import org.mifos.application.meeting.util.helpers.WeekDayTest;
import org.mifos.application.office.struts.tag.OfficeListTagTest;
import org.mifos.application.ppi.helpers.XmlPPIParserTest;
import org.mifos.application.productdefinition.business.PrdOfferingBOIntegrationTest;
import org.mifos.application.productdefinition.struts.actionforms.LoanPrdActionFormTest;
import org.mifos.application.rolesandpermission.business.RoleActivityEntityTest;
import org.mifos.application.surveys.business.QuestionTest;
import org.mifos.application.ui.DispatcherTest;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.ProperlyAdaptedJUnit4Test;
import org.mifos.framework.components.batchjobs.business.TaskTest;
import org.mifos.framework.components.configuration.cache.KeyTest;
import org.mifos.framework.components.customTableTag.TableTagParserTest;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.TestLogger;
import org.mifos.framework.components.tabletag.TableTagTest;
import org.mifos.framework.exceptions.FrameworkExceptionTest;
import org.mifos.framework.formulaic.ValidatorsTest;
import org.mifos.framework.persistence.CompositeUpgradeTest;
import org.mifos.framework.persistence.DatabaseInitFilterTest;
import org.mifos.framework.persistence.DatabaseVersionPersistenceTest;
import org.mifos.framework.persistence.UpgradeTest;
import org.mifos.framework.struts.tags.MifosSelectTest;
import org.mifos.framework.struts.tags.MifosTagUtilsTest;
import org.mifos.framework.struts.tags.XmlBuilderTest;
import org.mifos.framework.util.LocalizationConverterTest;
import org.mifos.framework.util.TestingServiceTest;
import org.mifos.framework.util.helpers.ChapterNumSortTest;
import org.mifos.framework.util.helpers.ConvertionUtilTest;
import org.mifos.framework.util.helpers.DateUtilsTest;
import org.mifos.framework.util.helpers.MethodInvokerTest;
import org.mifos.framework.util.helpers.MoneyTest;
import org.mifos.framework.util.helpers.StringUtilsTest;

/**
 * Tests which run quickly (say, <10ms per test, or some such, so that the whole
 * run can be done in seconds or at most a minute or two).
 * 
 * Test setup should also be fast (say, <1-2 seconds). This currently means that
 * a fast test cannot inherit from {@link MifosIntegrationTest}, call Hibernate,
 * or other things which take many seconds to start up.
 * 
 * It is also true that tests here do not depend on the MySQL test database. We
 * want to keep it that way (partly for speed, partly for isolation from other
 * test runs and the like).
 * 
 * If your only reason for wanting {@link MifosIntegrationTest} is logging, you
 * can call {@link MifosLogManager#configureLogging()} (this seems fast enough).
 * Another choice is to pass around a {@link TestLogger} (see
 * {@link RoleActivityEntityTest} for an example) - this does a better job of
 * avoiding side effects, strange dependencies on configuration files, etc, and
 * is what we'll need to do if we want to assert on log messages, for example.
 */
public class FastTests extends TestSuite {

    public static Test suite() throws Exception {
        TestSuite suite = new FastTests();
        suite.addTest(MoneyTest.suite());
        suite.addTestSuite(MifosCurrencyTest.class);
        suite.addTestSuite(DateUtilsTest.class);
        suite.addTestSuite(CustomFieldViewTest.class);
        suite.addTestSuite(MifosTagUtilsTest.class);
        suite.addTestSuite(FrameworkExceptionTest.class);
        suite.addTestSuite(WeekDayTest.class);

        suite.addTestSuite(CustomerActionFormTest.class);
        suite.addTestSuite(LoanPrdActionFormTest.class);
        suite.addTestSuite(ClientCustActionFormTest.class);

        suite.addTestSuite(OfficeListTagTest.class);

        suite.addTestSuite(TableTagTest.class);
        suite.addTest(MifosSelectTest.suite());
        suite.addTestSuite(XmlBuilderTest.class);
        suite.addTestSuite(MethodInvokerTest.class);
        suite.addTestSuite(ConvertionUtilTest.class);
        suite.addTestSuite(TableTagParserTest.class);
        suite.addTestSuite(DispatcherTest.class);
        suite.addTestSuite(CreateReportTest.class);

        suite.addTestSuite(DatabaseInitFilterTest.class);
        suite.addTest(DatabaseVersionPersistenceTest.suite());
        suite.addTest(UpgradeTest.suite());
        suite.addTest(CompositeUpgradeTest.suite());

        suite.addTestSuite(RoleActivityEntityTest.class);
        suite.addTestSuite(TaskTest.class);
        suite.addTestSuite(KeyTest.class);
        suite.addTestSuite(BulkEntryAccountFeeActionViewTest.class);
        suite.addTestSuite(LoanCycleCounterTest.class);
        suite.addTestSuite(AccountBOIntegrationTest.class);
        suite.addTestSuite(PrdOfferingBOIntegrationTest.class);

        suite.addTest(QuestionTest.suite());

        suite.addTestSuite(CustomerStatusFlagTest.class);

        suite.addTestSuite(ValidatorsTest.class);

        suite.addTest(XmlPPIParserTest.suite());

        suite.addTest(StringUtilsTest.suite());
        suite.addTest(ChapterNumSortTest.suite());
        suite.addTest(ProperlyAdaptedJUnit4Test.suite());
        suite.addTest(LocalizationConverterTest.suite());
        suite.addTest(TestingServiceTest.suite());
        return suite;
    }

}
