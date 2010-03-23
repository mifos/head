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

package org.mifos.reports.admindocuments.business;

import org.mifos.framework.business.BusinessObject;
import org.mifos.security.util.UserContext;

public class AdminDocumentBO extends BusinessObject {

    private Short admindocId;
    private String adminDocumentName;
    private String adminDocumentIdentifier;
    private Short isActive;

    public AdminDocumentBO(UserContext userContext) {
        super(userContext);
    }

    public AdminDocumentBO() {
        super();
    }

    public Short getAdmindocId() {
        return admindocId;
    }

    public void setAdmindocId(Short admindocId) {
        this.admindocId = admindocId;
    }

    public String getAdminDocumentIdentifier() {
        return adminDocumentIdentifier;
    }

    public void setAdminDocumentIdentifier(String adminDocumentIdentifier) {
        this.adminDocumentIdentifier = adminDocumentIdentifier;
    }

    public String getAdminDocumentName() {
        return adminDocumentName;
    }

    public void setAdminDocumentName(String adminDocumentName) {
        this.adminDocumentName = adminDocumentName;
    }

    public Short getIsActive() {
        return isActive;
    }

    public void setIsActive(Short isActive) {
        this.isActive = isActive;
    }

}
