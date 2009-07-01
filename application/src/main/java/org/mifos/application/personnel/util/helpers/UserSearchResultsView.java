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

package org.mifos.application.personnel.util.helpers;

import org.mifos.framework.business.View;

/**
 * This obect denotes instance of search result, that will be obtained after
 * user search.
 */
public class UserSearchResultsView extends View {

    short officeId;

    String officeName;

    short personnelId;

    String globalPersonnelNum;

    String personnelName;

    public String getGlobalPersonnelNum() {
        return globalPersonnelNum;
    }

    public void setGlobalPersonnelNum(String globalPersonnelNum) {
        this.globalPersonnelNum = globalPersonnelNum;
    }

    public short getOfficeId() {
        return officeId;
    }

    public void setOfficeId(short officeId) {
        this.officeId = officeId;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public short getPersonnelId() {
        return personnelId;
    }

    public void setPersonnelId(short personnelId) {
        this.personnelId = personnelId;
    }

    public String getPersonnelName() {
        return personnelName;
    }

    public void setPersonnelName(String personnelName) {
        this.personnelName = personnelName;
    }
}
