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

package org.mifos.reports.admindocuments.persistence;

import java.util.HashMap;
import java.util.List;

import org.mifos.application.NamedQueryConstants;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.LegacyGenericDao;
import org.mifos.reports.admindocuments.business.AdminDocumentBO;

public class LegacyAdminDocumentDao extends LegacyGenericDao {

    private LegacyAdminDocumentDao() {
    }

    public AdminDocumentBO getAdminDocumentById(Short adminDocumentId) throws PersistenceException {
        return getPersistentObject(AdminDocumentBO.class, adminDocumentId);
    }

    public List<AdminDocumentBO> getAllActiveAdminDocuments() throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        return executeNamedQuery(NamedQueryConstants.GET_ALL_ACTIVE_ADMINISTRATIVE_DOCUMENT, queryParameters);

    }

    public List<AdminDocumentBO> getAllAdminDocuments() throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        return executeNamedQuery(NamedQueryConstants.GET_ALL_ADMINISTRATIVE_DOCUMENT, queryParameters);
    }
    
    public List<AdminDocumentBO> getActiveAdminDocumentsByAccountActionId(Short accountActionId) throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("accountActionId", accountActionId);
        return executeNamedQuery(NamedQueryConstants.GET_ACTIVE_ADMIN_DOCUMENTS_BY_ACCOUNT_ACTION_ID, queryParameters);
    }

}