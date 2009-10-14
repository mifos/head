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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.mifos.framework.components.mifosmenu.MenuParserIntegrationTest;
import org.mifos.framework.hibernate.HibernateIntegrationTest;
import org.mifos.framework.hibernate.helper.HibernateHelperIntegrationTest;
import org.mifos.framework.persistence.LatestTestAfterCheckpointBaseTest;
import org.mifos.framework.persistence.PersistenceIntegrationTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( {
    HibernateIntegrationTest.class,
    LatestTestAfterCheckpointBaseTest.class,
    MayflyMiscTest.class,
    MenuParserIntegrationTest.class,
    HibernateHelperIntegrationTest.class,
    PersistenceIntegrationTest.class
    })
public class MiscTestsSuite {
    // the class remains completely empty,
    // being used only as a holder for the above annotations
}
