/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.framework.components.batchjobs;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.framework.MifosIntegrationTestCase;

public class TaskHelperIntegrationTest extends MifosIntegrationTestCase {

    public TaskHelperIntegrationTest() throws Exception {
        super();
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testIncompleteTaskHandling() {
        // TODO QUARTZ: Integration test testing whether the catch-up mechanism executes the missed job after
        // finding out that it failed on last scheduled last time
    }

    @Test
    public void testIncompleteTaskDelay() {
        // TODO QUARTZ: Integration test showing that when a job failed to execute several times,
        // the scheduler would launch each failed run with a correct (previously scheduled) date
    }

}
