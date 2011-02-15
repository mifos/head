/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.dto.screen;

import java.sql.Date;

public class CustomerHistoricalDataDto {

    private final boolean client;
    private final boolean group;
    private final Date mfiJoiningDate;

    public CustomerHistoricalDataDto(boolean client, boolean group, Date mfiJoiningDate) {
        this.client = client;
        this.group = group;
        this.mfiJoiningDate = mfiJoiningDate;
    }

    public boolean isClient() {
        return this.client;
    }

    public boolean isGroup() {
        return this.group;
    }

    public Date getMfiJoiningDate() {
        return this.mfiJoiningDate;
    }
}