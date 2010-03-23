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

package org.mifos.framework.struts.plugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import junit.framework.Assert;

import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.SavingsType;
import org.mifos.accounts.savings.struts.action.SavingsAction;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ConstantsNotLoadedException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class ConstPluginStrutsTest extends MifosMockStrutsTestCase {

    public ConstPluginStrutsTest() throws Exception {
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

    /**
     *This method performs an action to load Plugins defined in
     * struts-config.xml.
     */
    public void testMasterConstants() throws Exception {
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("recordOfficeId", "0");
        addRequestParameter("recordLoanOfficerId", "0");
        performNoErrors();
        Map constantMap = (Map) context.getAttribute("MasterConstants");
        Assert.assertNotNull(constantMap);
       Assert.assertEquals("PaymentType", constantMap.get("PAYMENT_TYPE"));
       Assert.assertEquals(Short.valueOf("1"), constantMap.get("CUSTOMFIELD_NUMBER"));
    }

    public void testIfAllConstantFilesAreLoaded() {
        setRequestPathInfo("/savingsAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("recordOfficeId", "0");
        addRequestParameter("recordLoanOfficerId", "0");
        performNoErrors();
        Assert.assertNotNull(context.getAttribute("Constants"));
        Assert.assertNotNull(context.getAttribute("MasterConstants"));
        Assert.assertNotNull(context.getAttribute("CustomerConstants"));
        Assert.assertNotNull(context.getAttribute("AccountStates"));
        Assert.assertNotNull(context.getAttribute("SavingsConstants"));
    }

    public void testConstantsPluginException() throws Exception {
        ConstPlugin constPlugin = new ConstPlugin();
        ArrayList<String> constPluginClasses = new ArrayList<String>();
        constPluginClasses.add("org.mifos.doesNotExist");
        try {
            Class doesNotExistClass = ConstPlugin.class;
            Field[] fields = doesNotExistClass.getDeclaredFields();
            Field field = fields[0];
            ConstPlugin.checkModifiers(field);
        } catch (ConstantsNotLoadedException expected) {
        }

        try {
            constPlugin.buildClasses(constPluginClasses);
            Assert.fail();
        } catch (ConstantsNotLoadedException expected) {
        }
    }

}
