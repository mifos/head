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

package org.mifos.test.acceptance.framework.admin;

import java.util.ArrayList;
import java.util.List;

public class DefineChecklistParameters {

    public static final String TYPE_CENTER = "Center";
    public static final String TYPE_GROUP = "Group";
    public static final String TYPE_CLIENT = "Client";
    public static final String TYPE_LOAN = "Loan";
    public static final String TYPE_SAVINGS = "Savings";

    public static final String STATUS_ACTIVE = "Active";
    public static final String STATUS_INACTIVE = "Inactive";

    private String name;
    private String type;
    private String displayedWhenMovingIntoStatus;
    private String status;
    private final List<String> items = new ArrayList<String>();

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDisplayedWhenMovingIntoStatus() {
        return this.displayedWhenMovingIntoStatus;
    }

    public void setDisplayedWhenMovingIntoStatus(String displayedWhenMovingIntoStatus) {
        this.displayedWhenMovingIntoStatus = displayedWhenMovingIntoStatus;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void addItem(String item) {
        this.items.add(item);
    }

    public String[] getItemsArray() {
        return this.items.toArray(new String[0]);
    }
}
