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

package org.mifos.accounts.business;

import org.mifos.application.master.business.MasterDataEntity;

public class AccountStateFlagEntity extends MasterDataEntity {
    private Short statusId;

    private String flagDescription;

    private Short retained;

    protected AccountStateFlagEntity() {
    }

    public String getFlagDescription() {
        return flagDescription;
    }

    public void setFlagDescription(String flagDescription) {
        this.flagDescription = flagDescription;
    }

    public Short getStatusId() {
        return statusId;
    }

    public void setStatusId(Short statusId) {
        this.statusId = statusId;
    }

    void setRetained(Short retained) {
        this.retained = retained;
    }

    Short getRetained() {
        return this.retained;
    }

    public boolean isFlagRetained() {
        return this.getRetained() == 1;
    }

}
