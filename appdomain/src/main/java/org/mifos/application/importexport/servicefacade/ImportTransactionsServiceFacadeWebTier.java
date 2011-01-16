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

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mifos.accounts.api.TransactionImport;
import org.mifos.accounts.servicefacade.UserContextFactory;
import org.mifos.application.importexport.business.ImportedFilesEntity;
import org.mifos.application.importexport.business.service.ImportedFilesService;
import org.mifos.application.servicefacade.ListItem;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.dto.domain.ParseResultDto;
import org.mifos.dto.domain.UserReferenceDto;
import org.mifos.framework.plugin.PluginManager;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.MifosUser;
import org.mifos.security.util.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

public class ImportTransactionsServiceFacadeWebTier implements ImportTransactionsServiceFacade {

    private static final Logger logger = LoggerFactory.getLogger(ImportTransactionsServiceFacadeWebTier.class);

    private ImportedFilesService importedFilesService;
    private PersonnelDao personnelDao;

    @Autowired
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

    @Override
    public List<ListItem<String>> retrieveImportPlugins() {
        List<ListItem<String>> importPlugins = new ArrayList<ListItem<String>>();
        for (TransactionImport ti : new PluginManager().loadImportPlugins()) {
            importPlugins.add(new ListItem<String>(ti.getClass().getName(), ti.getDisplayName()));
        }
        return importPlugins;
    }

    @Override
    public ParseResultDto parseImportTransactions(String importPluginClassname, InputStream inputStream) {

        MifosUser mifosUser = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(mifosUser);

        final TransactionImport ti = getInitializedImportPlugin(importPluginClassname, userContext.getId());
        final ParseResultDto importResult = ti.parse(inputStream);

        int numberRowSuccessfullyParsed = ti.getSuccessfullyParsedRows();
        importResult.setNumberRowSuccessfullyParsed(numberRowSuccessfullyParsed);

        String statusLogFile = generateStatusLogfile(importResult, ti);
        importResult.setStatusLogFile(statusLogFile);

        return importResult;
    }

    private TransactionImport getInitializedImportPlugin(String importPluginClassname, Short userId) {
        final TransactionImport ti = new PluginManager().getImportPlugin(importPluginClassname);
        final UserReferenceDto userReferenceDTO = new UserReferenceDto(userId);
        ti.setUserReferenceDto(userReferenceDTO);
        return ti;
    }

    private static final String LOG_TEMPLATE =
        "%d rows were read.\n" +
        "\n" +
        "%d rows contained no errors and will be imported\n" +
        "%d rows will be ignored\n" +
        "%d rows contained errors and were not imported\n" +
        "\n" +
        "Total amount of transactions imported: %s\n" +
        "Total amount of transactions with error: %s\n" +
        "\n" +
        "%s";

    private String generateStatusLogfile(ParseResultDto result, TransactionImport transactionImport) {
        String rowErrors = "";
        if (!result.getParseErrors().isEmpty()) {
            rowErrors = StringUtils.join(result.getParseErrors(), System.getProperty("line.separator"));
        }
        return String.format(LOG_TEMPLATE,
                result.getNumberOfReadRows(),
                transactionImport.getSuccessfullyParsedRows(),
                result.getNumberOfIgnoredRows(),
                result.getNumberOfErrorRows(),
                new Money(Money.getDefaultCurrency(), result.getTotalAmountOfTransactionsImported()).toString(),
                new Money(Money.getDefaultCurrency(), result.getTotalAmountOfTransactionsWithError()).toString(),
                rowErrors);
    }

    @Override
    public ParseResultDto confirmImport(String importPluginClassname, FileInputStream transactionsTempFile) {

        MifosUser mifosUser = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(mifosUser);

        try {
            final TransactionImport transactionImport = getInitializedImportPlugin(importPluginClassname, userContext.getId());
            final ParseResultDto importResult = transactionImport.parse(transactionsTempFile);

            transactionImport.store(transactionsTempFile);
            return importResult;
        } catch (Exception e) {
            throw new MifosRuntimeException(e);
        }
    }
}