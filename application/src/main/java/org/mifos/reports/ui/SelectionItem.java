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

package org.mifos.reports.ui;

import static org.mifos.reports.util.helpers.ReportsConstants.ALL_DISPLAY_NAME;
import static org.mifos.reports.util.helpers.ReportsConstants.ALL_ID;
import static org.mifos.reports.util.helpers.ReportsConstants.NOT_APPLICABLE_DISPLAY_NAME;
import static org.mifos.reports.util.helpers.ReportsConstants.NOT_APPLICABLE_ID;
import static org.mifos.reports.util.helpers.ReportsConstants.SELECT_DISPLAY_NAME;
import static org.mifos.reports.util.helpers.ReportsConstants.SELECT_ID;

import java.io.Serializable;

public class SelectionItem implements Serializable {

    public static final SelectionItem ALL_LOAN_OFFICER_SELECTION_ITEM = new SelectionItem(ALL_ID, ALL_DISPLAY_NAME);
    public static final SelectionItem SELECT_LOAN_OFFICER_SELECTION_ITEM = new SelectionItem(SELECT_ID,
            SELECT_DISPLAY_NAME);
    public static final SelectionItem NA_LOAN_OFFICER_SELECTION_ITEM = new SelectionItem(NOT_APPLICABLE_ID,
            NOT_APPLICABLE_DISPLAY_NAME);

    public static final SelectionItem SELECT_BRANCH_OFFICE_SELECTION_ITEM = new SelectionItem(SELECT_ID,
            SELECT_DISPLAY_NAME);
    public static final SelectionItem NA_BRANCH_OFFICE_SELECTION_ITEM = new SelectionItem(NOT_APPLICABLE_ID,
            NOT_APPLICABLE_DISPLAY_NAME);
    public static final SelectionItem ALL_CENTER_SELECTION_ITEM = new SelectionItem(ALL_ID, ALL_DISPLAY_NAME);
    public static final SelectionItem NA_CENTER_SELECTION_ITEM = new SelectionItem(NOT_APPLICABLE_ID,
            NOT_APPLICABLE_DISPLAY_NAME);
    public static final SelectionItem SELECT_CENTER_SELECTION_ITEM = new SelectionItem(SELECT_ID, SELECT_DISPLAY_NAME);

    private String displayName;
    private Integer id;

    public SelectionItem(Integer id, String displayName) {
        super();
        this.displayName = displayName;
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((displayName == null) ? 0 : displayName.hashCode());
        result = PRIME * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SelectionItem other = (SelectionItem) obj;
        if (displayName == null) {
            if (other.displayName != null) {
                return false;
            }
        } else if (!displayName.equals(other.displayName)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

    public boolean sameAs(Integer id) {
        return this.id.equals(id);
    }
}