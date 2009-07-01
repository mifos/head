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

package org.mifos.application.office.business;

import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.office.exceptions.OfficeException;
import org.mifos.application.office.util.helpers.OfficeStatus;
import org.mifos.framework.exceptions.PropertyNotFoundException;

public class OfficeStatusEntity extends MasterDataEntity {

    public OfficeStatusEntity(OfficeStatus status) {
        super(status.getValue());
    }

    protected OfficeStatusEntity() {
        super();
    }

    public OfficeStatus getStatus() throws OfficeException {
        try {
            return OfficeStatus.getOfficeStatus(this.getId());
        } catch (PropertyNotFoundException e) {
            throw new OfficeException(e);
        }

    }
}
