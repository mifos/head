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

package org.mifos.application.personnel.business;

import org.mifos.application.customer.business.CustomerLevelEntity;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.util.helpers.CustomerLevel;

public class CustomerFixture {

    public static CenterBO createCenterBO(Integer customerId) {
        return createCenterBO(customerId, null);
    }

    public static CenterBO createCenterBO(Integer customerId, PersonnelBO loanOfficer) {
        CenterBO center = CenterBO.createInstanceForTest(customerId, new CustomerLevelEntity(CustomerLevel.CENTER),
                null, loanOfficer, customerId.toString());
        return center;
    }
}
