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

import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;

public class MifosSelectHelperIntegrationTest extends MifosIntegrationTest {

    public MifosSelectHelperIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    public void testGetInstance() {
        MifosSelectHelper mifosSelectHelper = MifosSelectHelper.getInstance();
        assertNotNull(mifosSelectHelper);
    }

    public void testGetValue() {
        MifosSelectHelper mifosSelectHelper = MifosSelectHelper.getInstance();
        String[] prdCarStatusIds = mifosSelectHelper.getValue(ProductDefinitionConstants.PRODUCTCATEGORYSTATUSID);
        assertEquals("org.mifos.application.productdefinition.business.PrdStatusEntity", prdCarStatusIds[0]);
        assertEquals(ProductDefinitionConstants.PRODUCTCATEGORYSTATUSID, prdCarStatusIds[1]);
    }

}
