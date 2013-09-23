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

import java.io.Serializable;

public class CustomerStatusDetailDto implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private final String id;
    private final String statusName;
    private final String flagName;

    public CustomerStatusDetailDto(String statusName, String flagName) {
        this.id = "";
        this.statusName = statusName;
        this.flagName = flagName;
    }
    
    public CustomerStatusDetailDto(String id, String statusName, String flagName) {
        this.id = id;
        this.statusName = statusName;
        this.flagName = flagName;
    }

    public String getId() {
        return id;
    }

    public String getStatusName() {
        return this.statusName;
    }

    public String getFlagName() {
        return this.flagName;
    }
}