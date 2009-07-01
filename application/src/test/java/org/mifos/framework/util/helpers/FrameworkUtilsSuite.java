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

package org.mifos.framework.util.helpers;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.framework.util.helpers.FlowManagerHelperTest;

import org.mifos.framework.util.helpers.FlowManagerTest;
import org.mifos.framework.util.helpers.FlowTest;
import org.mifos.framework.util.helpers.MifosDoubleConverterTest;
import org.mifos.framework.util.helpers.ConvertionUtilTest;
import org.mifos.framework.util.helpers.MifosNodeTest;
import org.mifos.framework.util.helpers.CacheTest;
import org.mifos.framework.util.helpers.BundleKeyTest;
import org.mifos.framework.util.helpers.MifosSelectHelperTest;
import org.mifos.framework.util.helpers.StringToMoneyConverterTest;


public class FrameworkUtilsSuite extends TestSuite {

    public FrameworkUtilsSuite() {
        super();
    }

    public static Test suite() throws Exception {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(FlowManagerHelperTest.class);
        suite.addTestSuite(FlowManagerTest.class);
        suite.addTestSuite(FlowTest.class);
        suite.addTestSuite(MifosDoubleConverterTest.class);
        suite.addTestSuite(ConvertionUtilTest.class);
        suite.addTestSuite(MifosNodeTest.class);
        suite.addTestSuite(CacheTest.class);
        suite.addTestSuite(BundleKeyTest.class);
        suite.addTestSuite(MifosSelectHelperTest.class);
        suite.addTestSuite(StringToMoneyConverterTest.class);
        return suite;
    }

}
