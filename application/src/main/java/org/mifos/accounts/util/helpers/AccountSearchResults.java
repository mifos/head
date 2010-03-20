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

package org.mifos.accounts.util.helpers;

import org.mifos.framework.business.View;

public class AccountSearchResults extends View {

    private String officeName;

    private short levelId;

    private String centerName;

    private String groupName;

    private String clientName;

    private int clientId;

    private String globelNo;

    public String getCenterName() {
        return this.centerName;
    }

    public void setCenterName(String centerName) {
        if (centerName == null) {
            centerName = "";
        }
        this.centerName = centerName;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public short getLevelId() {
        return levelId;
    }

    public void setLevelId(short levelId) {
        this.levelId = levelId;
    }

    public String getClientName() {

        return clientName;
    }

    public void setClientName(String clientName) {
        if (null == clientName) {
            clientName = "";
        }
        this.clientName = clientName;
    }

    public String getGroupName() {

        return groupName;

    }

    public void setGroupName(String groupName) {
        if (null == groupName) {
            groupName = "";
        }
        this.groupName = groupName;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public String getGroupId() {
        if (null == this.centerName) {
            return String.valueOf(this.clientId);
        } else {
            return "";
        }
    }

    public String getGroupLink() {
        if (null == this.centerName) {
            return "true";
        } else {
            return "false";
        }
    }

    public String getClientLink() {
        if (null != this.centerName) {
            return "true";
        } else {
            return "false";
        }

    }

    public String getIdClient() {
        if (null == this.centerName) {
            return "";
        } else {
            return String.valueOf(this.clientId);
        }

    }

    public String getGlobelNo() {
        return globelNo;
    }

    public void setGlobelNo(String globelNo) {
        this.globelNo = globelNo;
    }
}
