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

package org.mifos.customers.group;

import java.util.Date;

import org.mifos.customers.CustomerTemplateImpl;
import org.mifos.customers.util.helpers.CustomerStatus;

public class GroupTemplateImpl extends CustomerTemplateImpl implements GroupTemplate {
    private boolean isTrained;
    private Date trainedDate;
    private Integer parentCustomerId;

    private GroupTemplateImpl(Integer parentCenterId) {
        super("TestGroup", CustomerStatus.GROUP_ACTIVE);
        this.parentCustomerId = parentCenterId;
    }

    public boolean isTrained() {
        return this.isTrained;
    }

    public Date getTrainedDate() {
        return this.trainedDate;
    }

    public Integer getParentCenterId() {
        return this.parentCustomerId;
    }

    /**
     * Use this in transactions that you don't plan on committing to the
     * database. If you commit more than one of these to the database you'll run
     * into uniqueness constraints. Plan on always rolling back the transaction.
     *
     * @param parentCenterId
     * @return groupTemplateImpl
     */
    public static GroupTemplateImpl createNonUniqueGroupTemplate(Integer parentCenterId) {
        return new GroupTemplateImpl(parentCenterId);
    }
}
