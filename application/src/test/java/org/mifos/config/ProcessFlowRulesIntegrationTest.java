/*
 * Copyright Grameen Foundation USA
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

package org.mifos.config;

import junit.framework.Assert;

import org.junit.Test;
import org.mifos.config.exceptions.ConfigurationException;
import org.mifos.customers.business.CustomerStatusEntity;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

/**
 * Validate configuration override logic for optional process flow states.
 */

public class ProcessFlowRulesIntegrationTest extends MifosIntegrationTestCase {


    @Test
    public void testOverrideNeeded() throws Exception {
       Assert.assertTrue(ProcessFlowRules.needsOverride(false, true));
    }

    @Test
    public void testOverrideNotNecessary() throws Exception {
        Assert.assertFalse(ProcessFlowRules.needsOverride(false, false));
        Assert.assertFalse(ProcessFlowRules.needsOverride(true, true));
    }

    @Test
    public void testOverrideValidation() throws Exception {
       Assert.assertTrue(ProcessFlowRules.isValidOverride(true, true));
       Assert.assertTrue(ProcessFlowRules.isValidOverride(false, true));
       Assert.assertTrue(ProcessFlowRules.isValidOverride(false, false));
        Assert.assertFalse(ProcessFlowRules.isValidOverride(true, false));
    }

    @Test
    public void testInvalidOverride() throws Exception {
        try {
            ProcessFlowRules.needsOverride(true, false);
            Assert.fail("Expected ConfigurationException");
        } catch (ConfigurationException e){
            // expected
        }
    }

    @Test
    public void testValidOverrideAgainstDb() throws Exception {
        CustomerPersistence cp = new CustomerPersistence();
        CustomerStatusEntity cse = (CustomerStatusEntity) cp.loadPersistentObject(CustomerStatusEntity.class,
                CustomerStatus.CLIENT_PENDING.getValue());
       Assert.assertTrue(cse.getIsOptional());
        cse.setIsOptional(false);
        StaticHibernateUtil.flushSession();
        Assert.assertFalse(cse.getIsOptional());
       Assert.assertTrue(ProcessFlowRules.isClientPendingApprovalStateEnabled());
        ProcessFlowRules.init();
       Assert.assertTrue(cse.getIsOptional());
    }

    @Test
    public void testInvalidOverrideAgainstDb() throws Exception {
        try {

            ProcessFlowRules.init();

        } catch (ConfigurationException e){
            // expected
        }
    }
}
