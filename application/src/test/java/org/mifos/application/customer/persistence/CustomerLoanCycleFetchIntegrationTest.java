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

package org.mifos.application.customer.persistence;

import junit.framework.Assert;

import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;

public class CustomerLoanCycleFetchIntegrationTest extends MifosIntegrationTestCase {

    public CustomerLoanCycleFetchIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    public void testFetchLoanCountersForGroupQueryIsValid() throws Exception {
        try {
            new CustomerPersistence().fetchLoanCycleCounter(new GroupBO() {
                @Override
                public Integer getCustomerId() {
                    return 1;
                }

                @Override
                public boolean isGroup() {
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Exception fetching customer loan counters");
        }
    }

    public void testFetchLoanCountersForClientQueryIsValid() throws Exception {
        try {
            new CustomerPersistence().fetchLoanCycleCounter(new ClientBO() {
                @Override
                public Integer getCustomerId() {
                    return 1;
                }

                @Override
                public boolean isGroup() {
                    return false;
                }

                @Override
                public boolean isClient() {
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Exception fetching customer loan counters");
        }
    }
}
