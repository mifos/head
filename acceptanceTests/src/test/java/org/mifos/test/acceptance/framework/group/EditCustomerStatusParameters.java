/*
 * Copyright Grameen Foundation USA
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

package org.mifos.test.acceptance.framework.group;

import org.mifos.test.acceptance.framework.client.ClientStatus;

public class EditCustomerStatusParameters {

    private String note;
    private GroupCancelReason cancelReason;
    private GroupCloseReason closeReason;
    private GroupStatus groupStatus;
    private ClientStatus clientStatus;

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public GroupCancelReason getCancelReason() {
        return this.cancelReason;
    }

    public void setCancelReason(GroupCancelReason cancelReason) {
        this.cancelReason = cancelReason;
    }

    public GroupCloseReason getCloseReason() {
        return this.closeReason;
    }

    public void setCloseReason(GroupCloseReason closeReason) {
        this.closeReason = closeReason;
    }

    public GroupStatus getGroupStatus() {
        return this.groupStatus;
    }

    public void setGroupStatus(GroupStatus groupStatus) {
        this.groupStatus = groupStatus;
    }

    public ClientStatus getClientStatus() {
        return this.clientStatus;
    }

    public void setClientStatus(ClientStatus clientStatus) {
        this.clientStatus = clientStatus;
    }


}
