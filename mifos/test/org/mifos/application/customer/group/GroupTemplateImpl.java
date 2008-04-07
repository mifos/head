package org.mifos.application.customer.group;

import java.util.Date;

import org.mifos.application.customer.CustomerTemplateImpl;
import org.mifos.application.customer.util.helpers.CustomerStatus;

/**
 * Copyright (c) 2005-2006 Grameen Foundation USA
 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
 * All rights reserved.
 * Apache License
 * Copyright (c) 2005-2006 Grameen Foundation USA
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the
 * License.
 * <p/>
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license
 * and how it is applied.
 */
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
     * Use this in transactions that you don't plan on committing to the database.  If
     * you commit more than one of these to the database you'll run into uniqueness
     * constraints.  Plan on always rolling back the transaction.
     * @param parentCenterId
     * @return
     */
    public static GroupTemplateImpl createNonUniqueGroupTemplate(Integer parentCenterId) {
        return new GroupTemplateImpl(parentCenterId);
    }
}
