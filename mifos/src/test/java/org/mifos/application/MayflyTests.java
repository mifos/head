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

import org.mifos.application.accounts.savings.business.SavingsBOIntegrationTest;
import org.mifos.application.accounts.struts.action.ApplyAdjustmentActionTest;
import org.mifos.application.fees.persistence.FeePersistenceIntegrationTest;
import org.mifos.application.fees.struts.action.FeeActionTest;
import org.mifos.application.holiday.util.helpers.HolidayUtilsIntegrationTest;
import org.mifos.framework.persistence.DatabaseVersionPersistenceTest;
import org.mifos.framework.persistence.LatestTestAfterCheckpointBaseTest;
import org.mifos.framework.util.helpers.DatabaseSetup;

/**
 * Tests that are known to pass with Mayfly, if you enable Mayfly in
 * {@link DatabaseSetup}.
 * 
 * Does not include tests which are hardcoded to always use Mayfly, like
 * {@link LatestTestAfterCheckpointBaseTest} or
 * {@link DatabaseVersionPersistenceTest}.
 */
public class MayflyTests extends TestSuite {

    public static Test suite() throws Exception {
        TestSuite suite = new MayflyTests();
        suite.addTestSuite(FeePersistenceIntegrationTest.class);
        // suite.addTestSuite(CenterBOIntegrationTest.class);

        // Hung up on SELECT DISTINCT vs ORDER BY
        // Also has other failures - apparently unrelated
        // CustomerPersistenceIntegrationTest

        /*
         * Failing in getMaxOfficeId. Perhaps Integer vs. Long as return from
         * getObject (but that's unconfirmed)?
         */
        // suite.addTestSuite(OfficePersistenceIntegrationTest.class);
        suite.addTestSuite(SavingsBOIntegrationTest.class);
        suite.addTestSuite(ApplyAdjustmentActionTest.class);
        suite.addTestSuite(FeeActionTest.class);
        suite.addTestSuite(HolidayUtilsIntegrationTest.class);

        suite.addTestSuite(MayflyMiscTest.class);
        return suite;
    }

}
