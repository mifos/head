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

package org.mifos.application.importexport.servicefacade;

import org.mifos.accounts.servicefacade.UserContextFactory;
import org.mifos.application.importexport.business.ImportedFilesEntity;
import org.mifos.application.importexport.business.service.ImportedFilesService;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.security.MifosUser;
import org.mifos.security.util.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

public class ImportTransactionsServiceFacadeWebTier implements ImportTransactionsServiceFacade {

    private static final Logger logger = LoggerFactory.getLogger(ImportTransactionsServiceFacadeWebTier.class);

    private ImportedFilesService importedFilesService;
    private PersonnelDao personnelDao;

    public ImportTransactionsServiceFacadeWebTier(ImportedFilesService importedFilesService, PersonnelDao personnelDao) {
        this.importedFilesService = importedFilesService;
        this.personnelDao = personnelDao;
    }

    @Override
    public boolean isAlreadyImported(String importTransactionsFileName) {
        ImportedFilesEntity importedFile = importedFilesService.getImportedFileByName(importTransactionsFileName);
        if (importedFile != null) {
            logger.debug(importTransactionsFileName + " has already been submitted");
            logger.debug("Submitted by" + importedFile.getSubmittedBy());
            logger.debug("Submitted on" + importedFile.getSubmittedOn());
            return true;
        }
        return false;
    }

    @Override
    public void saveImportedFileName(String importTransactionsFileName) {

        MifosUser mifosUser = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(mifosUser);

        PersonnelBO submittedBy = this.personnelDao.findPersonnelById(userContext.getId());
        importedFilesService.saveImportedFileName(importTransactionsFileName, submittedBy);
    }
}