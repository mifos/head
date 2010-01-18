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

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import junit.framework.Assert;

import org.mifos.application.accounts.savings.struts.action.SavingsAction;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.EnumsNotLoadedException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class EnumPluginStrutsTest extends MifosMockStrutsTestCase {

    public EnumPluginStrutsTest() throws Exception {
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

    public void testCustomFieldType() throws Exception {
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("recordOfficeId", "0");
        addRequestParameter("recordLoanOfficerId", "0");
        performNoErrors();
        Map constantMap = (Map) context.getAttribute("CustomFieldType");
        Assert.assertNotNull(constantMap);
       Assert.assertEquals("NUMERIC", constantMap.get("NUMERIC").toString());
    }

    public void testIfAllConstantFilesAreLoaded() {
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("recordOfficeId", "0");
        addRequestParameter("recordLoanOfficerId", "0");
        performNoErrors();
        Assert.assertNotNull(context.getAttribute("FeeFrequencyType"));
        Assert.assertNotNull(context.getAttribute("FeeCategory"));
        Assert.assertNotNull(context.getAttribute("FeeLevel"));
        Assert.assertNotNull(context.getAttribute("FeePayment"));
        Assert.assertNotNull(context.getAttribute("FeeStatus"));
        Assert.assertNotNull(context.getAttribute("RateAmountFlag"));
        Assert.assertNotNull(context.getAttribute("RecurrenceType"));
        Assert.assertNotNull(context.getAttribute("AccountTypes"));
        Assert.assertNotNull(context.getAttribute("OfficeStatus"));
        Assert.assertNotNull(context.getAttribute("OfficeLevel"));
        Assert.assertNotNull(context.getAttribute("CustomerStatus"));
        Assert.assertNotNull(context.getAttribute("CustomerLevel"));
        Assert.assertNotNull(context.getAttribute("PrdStatus"));
        Assert.assertNotNull(context.getAttribute("AccountState"));
        Assert.assertNotNull(context.getAttribute("RecommendedAmountUnit"));
    }

    public void testEnumPluginException() throws Exception {
        EnumPlugin enumPlugin = new EnumPlugin();
        ArrayList<String> enumPluginClasses = new ArrayList<String>();
        enumPluginClasses.add("org.mifos.doesNotExist");
        try {
            enumPlugin.buildClasses(enumPluginClasses);
            Assert.fail();
        } catch (EnumsNotLoadedException expected) {
        }
    }

}
