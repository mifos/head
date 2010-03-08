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

package org.mifos.application.importexport.servicefacade;

import org.mifos.application.importexport.business.ImportedFilesEntity;
import org.mifos.application.importexport.business.service.ImportedFilesService;
import org.mifos.application.importexport.business.service.StandardImportedFilesService;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.security.util.UserContext;

public class WebTierImportTransactionsServiceFacade implements ImportTransactionsServiceFacade {

    private static final MifosLogger logger = MifosLogManager.getLogger(WebTierImportTransactionsServiceFacade.class.getName());

    private ImportedFilesService importedFilesService;

    private PersonnelPersistence personnelPersistence;

    public void setImportedFilesService(ImportedFilesService importedFilesService) {
        this.importedFilesService = importedFilesService;
    }

    public ImportedFilesService getImportedFilesService() {
        if (importedFilesService == null) {
            importedFilesService = new StandardImportedFilesService();
        }
        return importedFilesService;
    }

    public void setPersonnelPersistence(PersonnelPersistence personnelPersistence) {
        this.personnelPersistence = personnelPersistence;
    }

    public PersonnelPersistence getPersonnelPersistence() {
        if(personnelPersistence == null){
            personnelPersistence = new PersonnelPersistence();
        }
        return personnelPersistence;
    }

    @Override
    public boolean isAlreadyImported(String importTransactionsFileName) throws Exception {
        ImportedFilesEntity importedFile = getImportedFilesService().getImportedFileByName(importTransactionsFileName);
        if (importedFile != null) {
            logger.debug(importTransactionsFileName + " has already been submitted");
            logger.debug("Submitted by" + importedFile.getSubmittedBy());
            logger.debug("Submitted on" + importedFile.getSubmittedOn());
            return true;
        }
        return false;
    }

    @Override
    public void saveImportedFileName(UserContext userContext, String importTransactionsFileName) throws Exception {
        PersonnelBO submittedBy = getPersonnelPersistence().findPersonnelById(userContext.getId());
        getImportedFilesService().saveImportedFileName(importTransactionsFileName, submittedBy);
    }

}
