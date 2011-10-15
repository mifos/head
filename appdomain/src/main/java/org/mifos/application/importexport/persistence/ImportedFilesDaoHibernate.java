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

package org.mifos.application.importexport.persistence;

import java.util.HashMap;
import java.util.Map;

import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.application.importexport.business.ImportedFilesEntity;
import org.springframework.beans.factory.annotation.Autowired;

public class ImportedFilesDaoHibernate implements ImportedFilesDao {

    private final GenericDao genericDao;

    @Autowired
    public ImportedFilesDaoHibernate(final GenericDao genericDao) {
        this.genericDao = genericDao;
    }

    @Override
    public void saveImportedFile(ImportedFilesEntity importedFile) {
        this.genericDao.createOrUpdate(importedFile);
    }

    @Override
    public ImportedFilesEntity findImportedFileByName(String fileName) {
        Map<String, String> queryParameters = new HashMap<String, String>();
        queryParameters.put("importedFileName", fileName);
        Object queryResult = this.genericDao.executeUniqueResultNamedQuery("importfiles.getImportedFileByName", queryParameters);
        return queryResult == null ? null : (ImportedFilesEntity) queryResult;
    }
}