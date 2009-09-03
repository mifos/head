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

package org.mifos.framework.components.logger;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.log4j.Level;
import org.mifos.framework.exceptions.ResourceBundleNotFoundException;

public class LoggingTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MifosLogManager.configureLogging();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testLogging() throws Exception {
        MifosLogger logger = MifosLogManager.getLogger("org.mifos.logger");
        logger.debug("test debug log", false, null);
        logger.info("test info log", false, null);
        logger.warn("test warn log");
        logger.warn("test warn log", false, null, new Exception());
        logger.warn("test warn log", false, null);
        logger.error("test error log", false, null);
        logger.warn("test warn log", false, null, new Exception());
        logger.fatal("test fatal log", false, null);
        logger.fatal("test fatal log");
        logger.fatal("test fatal log", false, null, new Exception());
    }

    public void testInvalidResourceBundle() throws Exception {
        try {
            MifosLogManager.getResourceBundle("unavailableResourceBundle");
            Assert.fail();
        } catch (ResourceBundleNotFoundException ex) {
        }
    }

    public void testNonKey() throws Exception {
        TestLogger logger = new TestLogger();
        logger.debug("test debug message", false, null);
       Assert.assertEquals(1, logger.nonKeyCount());
       Assert.assertEquals(Level.DEBUG, logger.nonKeyLevel(0));
       Assert.assertEquals("test debug message", logger.nonKeyMessage(0));
    }

}

