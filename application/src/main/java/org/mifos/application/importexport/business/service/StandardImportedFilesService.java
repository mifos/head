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

package org.mifos.application.importexport.business.service;

import java.sql.Timestamp;

import org.mifos.application.importexport.business.ImportedFilesEntity;
import org.mifos.application.importexport.persistence.HibernateImportedFilesDao;
import org.mifos.application.importexport.persistence.ImportedFilesDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.DateTimeService;

public class StandardImportedFilesService implements ImportedFilesService {

    private ImportedFilesDao importedFileDao;

    public ImportedFilesDao getImportedFileDao() {
        //TODO use Spring context
        if(importedFileDao == null){
            importedFileDao = new HibernateImportedFilesDao();
        }
        return importedFileDao;
    }

    public void setImportedFileDao(ImportedFilesDao importedFileDao) {
        this.importedFileDao = importedFileDao;
    }

    @Override
    public void saveImportedFileName(String fileName, PersonnelBO submittedBy) throws Exception {
        Timestamp submittedOn = new Timestamp(new DateTimeService().getCurrentDateTime().getMillis());
        ImportedFilesEntity importedFile = new ImportedFilesEntity(fileName, submittedOn, submittedBy);
        StaticHibernateUtil.startTransaction();
        getImportedFileDao().saveImportedFile(importedFile);
        StaticHibernateUtil.commitTransaction();
    }

    @Override
    public ImportedFilesEntity getImportedFileByName(String fileName) throws Exception {
        return getImportedFileDao().findImportedFileByName(fileName);
    }

}
