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

package org.mifos.application.fees.business;

import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.master.business.MasterDataEntity;

/**
 * Is the fee periodic or one-time? See {@link FeeFrequencyType}.
 */
public class FeeFrequencyTypeEntity extends MasterDataEntity {

    public FeeFrequencyTypeEntity(FeeFrequencyType feeFrequency) {
        super(feeFrequency.getValue());
    }

    protected FeeFrequencyTypeEntity() {
    }

    public boolean isPeriodic() {
        return getId().equals(FeeFrequencyType.PERIODIC.getValue());
    }

    public boolean isOneTime() {
        return getId().equals(FeeFrequencyType.ONETIME.getValue());
    }
}
