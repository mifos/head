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

package org.mifos.framework.struts.plugin;

import java.util.Date;

import org.mifos.application.accounts.savings.struts.action.SavingsAction;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class InitializerPluginStrutsTest extends MifosMockStrutsTestCase {

    public InitializerPluginStrutsTest() throws SystemException, ApplicationException {
        super();
    }

    private SavingsOfferingBO product;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        request.getSession(true);
        createFlowAndAddToRequest(SavingsAction.class);
        request.getSession().setAttribute(Constants.USERCONTEXT, TestUtils.makeUser());

        product = TestObjectFactory.createSavingsProduct("Offering1", "s1", SavingsType.MANDATORY,
                ApplicableTo.CLIENTS, new Date(System.currentTimeMillis()));
        addRequestParameter("selectedPrdOfferingId", product.getPrdOfferingId().toString());
    }

    @Override
    protected void tearDown() throws Exception {
        TestObjectFactory.removeObject(product);
        super.tearDown();
    }

    public void testLabelConstants() throws Exception {
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("recordOfficeId", "0");
        addRequestParameter("recordLoanOfficerId", "0");
        performNoErrors();
        assertEquals(ConfigurationConstants.BRANCHOFFICE, (String) context.getAttribute("LABEL_"
                + ConfigurationConstants.BRANCHOFFICE.toUpperCase()));
    }

}
