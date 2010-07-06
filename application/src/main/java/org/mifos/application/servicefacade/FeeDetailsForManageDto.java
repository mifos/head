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

package org.mifos.application.servicefacade;

import java.util.List;

import org.mifos.accounts.fees.business.FeeStatusEntity;
import org.mifos.accounts.fees.servicefacade.FeeDto;

public class FeeDetailsForManageDto {

    private final FeeDto fee;
    private final List<FeeStatusEntity> feeStatuses;

    public FeeDetailsForManageDto(FeeDto fee, List<FeeStatusEntity> feeStatuses) {
        this.fee = fee;
        this.feeStatuses = feeStatuses;
    }

    public FeeDto getFee() {
        return this.fee;
    }

    public List<FeeStatusEntity> getFeeStatuses() {
        return this.feeStatuses;
    }
}