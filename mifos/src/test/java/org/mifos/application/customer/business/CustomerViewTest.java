/**

 * CustomerViewTest.java version: 1.0



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

package org.mifos.application.customer.business;

import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;

public class CustomerViewTest extends MifosIntegrationTest {

	public CustomerViewTest() throws SystemException, ApplicationException {
        super();
    }

    public void testCustomerView() throws Exception {
		CustomerView customerView = new CustomerView(Integer.valueOf("1"),
				"Customer", "001global", Short.valueOf("2"));

		assertEquals(1, customerView.getCustomerId().intValue());
		assertEquals("Customer", customerView.getDisplayName());
		assertEquals("001global", customerView.getGlobalCustNum());
		assertEquals(2, customerView.getStatusId().shortValue());

		CustomerView customerView1 = new CustomerView(Integer.valueOf("1"),
				"Customer", "001global", Short.valueOf("2"),
				Short.valueOf("2"), Integer.valueOf("1"), Short.valueOf("2"),
				Short.valueOf("3"));
		assertEquals(2, customerView1.getCustomerLevelId().shortValue());
		assertEquals(2, customerView1.getOfficeId().shortValue());
		assertEquals(3, customerView1.getPersonnelId().shortValue());
		assertEquals(1, customerView1.getVersionNo().intValue());

	}

}
