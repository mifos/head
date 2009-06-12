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

package org.mifos.application.admindocuments.business;

import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.security.util.UserContext;

public class AdminDocAccStateMixBO extends BusinessObject {

    private Short adminDocAccStateMixId;

    private AdminDocumentBO adminDocumentID;

    private AccountStateEntity accountStateID;

    public AdminDocAccStateMixBO(UserContext userContext) {
        super(userContext);
    }

    public AdminDocAccStateMixBO() {
        super();
    }

    public AccountStateEntity getAccountStateID() {
        return accountStateID;
    }

    public void setAccountStateID(AccountStateEntity accountStateID) {
        this.accountStateID = accountStateID;
    }

    public Short getAdminDocAccStateMixId() {
        return adminDocAccStateMixId;
    }

    public AdminDocumentBO getAdminDocumentID() {
        return adminDocumentID;
    }

    public void setAdminDocumentID(AdminDocumentBO adminDocumentID) {
        this.adminDocumentID = adminDocumentID;
    }

}
