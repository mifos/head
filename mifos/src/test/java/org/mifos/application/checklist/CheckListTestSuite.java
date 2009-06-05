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

package org.mifos.application.checklist;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.mifos.application.checklist.business.CheckListBOIntegrationTest;
import org.mifos.application.checklist.business.service.CheckListBusinessServiceIntegrationTest;
import org.mifos.application.checklist.persistence.CheckListPersistenceIntegrationTest;
import org.mifos.application.checklist.struts.action.CheckListActionTest;

public class CheckListTestSuite extends TestSuite {

    public static void main(String[] args) {
        Test testSuite = suite();
        TestRunner.run(testSuite);
    }

    public static Test suite() {
        TestSuite testSuite = new CheckListTestSuite();
        testSuite.addTestSuite(CheckListBOIntegrationTest.class);
        testSuite.addTestSuite(CheckListBusinessServiceIntegrationTest.class);
        testSuite.addTestSuite(CheckListPersistenceIntegrationTest.class);
        testSuite.addTestSuite(CheckListActionTest.class);
        return testSuite;
    }
}
