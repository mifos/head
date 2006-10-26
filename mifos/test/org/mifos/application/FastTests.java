/**

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

import org.mifos.application.customer.business.CustomFieldViewTest;
import org.mifos.application.master.business.MifosCurrencyTest;
import org.mifos.framework.MifosTestSuite;
import org.mifos.framework.components.tabletag.TableTagTest;
import org.mifos.framework.struts.tags.DateHelperTest;
import org.mifos.framework.struts.tags.MifosTagUtilsTest;
import org.mifos.framework.struts.tags.XmlBuilderTest;
import org.mifos.framework.util.helpers.ConvertionUtilTest;
import org.mifos.framework.util.helpers.MethodInvokerTest;
import org.mifos.framework.util.helpers.MoneyTest;

/**
 * Tests which run quickly (say, <10ms per test, or some such,
 * so that the whole run can be done in seconds or at most a
 * minute or two).
 */
public class FastTests extends MifosTestSuite {

	public static Test suite() throws Exception {
		TestSuite suite = new FastTests();
		suite.addTestSuite(MoneyTest.class);
		suite.addTestSuite(MifosCurrencyTest.class);
		suite.addTestSuite(DateHelperTest.class);
		suite.addTestSuite(CustomFieldViewTest.class);
		suite.addTestSuite(MifosTagUtilsTest.class);

		//Currently this one is slow (extends MifosTestCase).
		//suite.addTestSuite(OfficeListTagTest.class);

		suite.addTestSuite(TableTagTest.class);
		suite.addTestSuite(XmlBuilderTest.class);
		suite.addTestSuite(MethodInvokerTest.class);
		suite.addTestSuite(ConvertionUtilTest.class);
		return suite;
	}

}
