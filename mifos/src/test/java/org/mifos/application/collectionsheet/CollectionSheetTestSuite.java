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

package org.mifos.application.collectionsheet;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.mifos.application.collectionsheet.business.TestBulkEntryView;
import org.mifos.application.collectionsheet.business.TestCollSheetBO;
import org.mifos.application.collectionsheet.business.TestCollSheetCustBO;
import org.mifos.application.collectionsheet.business.TestCollSheetSavingsDetailsEntity;
import org.mifos.application.collectionsheet.business.service.TestBulkEntryBusinessService;
import org.mifos.application.collectionsheet.persistence.TestBulkEntryPersistence;
import org.mifos.application.collectionsheet.persistence.service.TestBulkEntryPersistenceService;
import org.mifos.application.collectionsheet.struts.action.TestBulkEntryAction;
import org.mifos.application.collectionsheet.struts.uihelpers.BulkEntryDisplayHelperTest;

public class CollectionSheetTestSuite extends TestSuite {

    public static void main(String[] args) {
        Test testSuite = suite();

        TestRunner.run(testSuite);
    }

    public static Test suite() {
        TestSuite testSuite = new CollectionSheetTestSuite();
        testSuite.addTestSuite(TestCollSheetCustBO.class);
        testSuite.addTestSuite(TestCollSheetBO.class);
        testSuite.addTestSuite(TestCollSheetSavingsDetailsEntity.class);

        testSuite.addTestSuite(TestBulkEntryPersistence.class);
        testSuite.addTestSuite(TestBulkEntryPersistenceService.class);
        testSuite.addTestSuite(TestBulkEntryBusinessService.class);
        testSuite.addTestSuite(TestBulkEntryAction.class);
        testSuite.addTestSuite(TestBulkEntryView.class);
        testSuite.addTestSuite(BulkEntryDisplayHelperTest.class);

        return testSuite;

    }
}
