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

package org.mifos.reports.admindocuments.persistence;

import java.util.HashMap;
import java.util.List;

import org.mifos.application.NamedQueryConstants;
import org.mifos.reports.admindocuments.business.AdminDocAccStateMixBO;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;

public class AdminDocAccStateMixPersistence extends Persistence {

    public AdminDocAccStateMixPersistence() {
        super();

    }

    public List<AdminDocAccStateMixBO> getMixByAdminDocuments(Short admindocId) throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("admindocId", admindocId);
        return executeNamedQuery(NamedQueryConstants.GET_MIX_BY_ADMINISTRATIVE_DOCUMENT, queryParameters);

    }

    public List<AdminDocAccStateMixBO> getAllMixedAdminDocuments() throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        return executeNamedQuery(NamedQueryConstants.GET_ALL_MIXED_ADMINISTRATIVE_DOCUMENT, queryParameters);

    }

}