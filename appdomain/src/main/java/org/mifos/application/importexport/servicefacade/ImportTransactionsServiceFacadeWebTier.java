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

package org.mifos.application.importexport.servicefacade;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.hibernate.Query;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.mifos.accounts.api.TransactionImport;
import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.servicefacade.AccountServiceFacade;
import org.mifos.accounts.servicefacade.UserContextFactory;
import org.mifos.application.admin.servicefacade.MonthClosingServiceFacade;
import org.mifos.application.importexport.business.ImportedFilesEntity;
import org.mifos.application.importexport.business.service.ImportedFilesService;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.application.servicefacade.ListItem;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.dto.domain.AccountTrxDto;
import org.mifos.dto.domain.ParseResultDto;
import org.mifos.dto.domain.UserReferenceDto;
import org.mifos.dto.screen.ImportedFileDto;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.plugin.PluginManager;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.MifosUser;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

public class ImportTransactionsServiceFacadeWebTier implements ImportTransactionsServiceFacade {

    private static final Logger logger = LoggerFactory.getLogger(ImportTransactionsServiceFacadeWebTier.class);
    private static final String IMPORT_UNDONE = "Import file has been phased out";
    private static final String INVALID_TRXN =  "INVALID_TRXN";
    private static final String VALID_TRXN =  "VALID_TRXN";
    
    private ImportedFilesService importedFilesService;
    private PersonnelDao personnelDao;
    private AccountServiceFacade accountServiceFacade;
    private MonthClosingServiceFacade monthClosingServiceFacade;

    @Autowired
    public ImportTransactionsServiceFacadeWebTier(ImportedFilesService importedFilesService, PersonnelDao personnelDao, AccountServiceFacade accountServiceFacade, MonthClosingServiceFacade monthClosingServiceFacade) {
        this.importedFilesService = importedFilesService;
        this.personnelDao = personnelDao;
        this.accountServiceFacade = accountServiceFacade;
        this.monthClosingServiceFacade = monthClosingServiceFacade;
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
    public void saveImportedFileName(String importTransactionsFileName, String importPluginClassname, List<AccountTrxDto> idsToUndoImport) {

        MifosUser mifosUser = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(mifosUser);

        PersonnelBO submittedBy = this.personnelDao.findPersonnelById(userContext.getId());
        Boolean undoable = Boolean.FALSE;
        if (importPluginClassname.equalsIgnoreCase("org.almajmoua.AudiBankXlsImporter")){
            undoable = Boolean.TRUE;
        }
        importedFilesService.saveImportedFileName(importTransactionsFileName, submittedBy, idsToUndoImport, Boolean.FALSE, undoable);
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
        "Total amount of disbursements imported: %s\n" +
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
                new Money(Money.getDefaultCurrency(), result.getTotalAmountOfDisbursementsImported().toString()),
                new Money(Money.getDefaultCurrency(), result.getTotalAmountOfTransactionsWithError()).toString(),
                rowErrors);
    }

    @Override
    public ParseResultDto confirmImport(String importPluginClassname, String tempFileName) {

        MifosUser mifosUser = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(mifosUser);

        FileInputStream fileInput = null;
        try {
            final TransactionImport transactionImport = getInitializedImportPlugin(importPluginClassname, userContext.getId());
            fileInput = new FileInputStream(tempFileName);
            final ParseResultDto importResult = transactionImport.parse(fileInput);
            fileInput.close();

            fileInput = new FileInputStream(tempFileName);
            if (importPluginClassname.equalsIgnoreCase("org.almajmoua.AudiBankXlsImporter")) {
                importResult.setTrxIdsToUndo(transactionImport.storeForUndoImport(fileInput));
            }
            else {
                transactionImport.store(fileInput);
            }
            return importResult;
        } catch (Exception e) {
            throw new MifosRuntimeException(e);
        } finally {
            if (fileInput != null) {
                try {
                    fileInput.close();
                } catch (Exception e2) {
                    throw new MifosRuntimeException(e2);
                }
            }
        }
    }

    @Override
    public void undoFullImport(String importTransactionsFileName) {
        MifosUser mifosUser = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(mifosUser);
        TreeSet<String> accountsWithAdjustedPayments = new TreeSet<String>();
        try {
            ImportedFilesEntity filesEntity = this.importedFilesService.getImportedFileByName(importTransactionsFileName);
            List<AccountTrxnEntity> trxUndo = new ArrayList<AccountTrxnEntity>(filesEntity.getImportedTrxn());
            List<ImportedAccPaymentDto> accPaymentList = new ArrayList<ImportedAccPaymentDto>();
            for (AccountTrxnEntity trxn : trxUndo) {
                try {
                    validateForAdjustedPayments(trxn, accountsWithAdjustedPayments);
                    accPaymentList.add(new ImportedAccPaymentDto(trxn.getAccount().getGlobalAccountNum(), 
                            trxn.getAccountPayment().getPaymentId()));
                } catch (BusinessRuleException e) {
                   //nothing to do   
                }
                
            }
            for (ImportedAccPaymentDto accDto : accPaymentList) {
                try {
                    this.accountServiceFacade.applyHistoricalAdjustment(accDto.getGlobalNum(), 
                            accDto.getPaymentId(), IMPORT_UNDONE, userContext.getId(), null);
                } catch (MifosRuntimeException e) {
                    // TODO: validation will be added with MIFOS-5779
                }
            }
            this.importedFilesService.saveImportedFileName(filesEntity.getFileName(), filesEntity.getSubmittedBy(), null, Boolean.TRUE, filesEntity.getUndoable());
        } catch (Exception e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public List<ImportedFileDto> getImportedFiles() {
        List<ImportedFileDto> importedFilesDto = new ArrayList<ImportedFileDto>();
        List<ImportedFilesEntity> importedFiles = this.importedFilesService.getImportedFiles();
        DateTime date;
        
        for (ImportedFilesEntity fileEntity : importedFiles) {
            date = new DateTime(fileEntity.getSubmittedOn().getTime());
            importedFilesDto.add(new ImportedFileDto(fileEntity.getFileName(), date, fileEntity.getPhaseOut(), fileEntity.getUndoable()));
        }
        return importedFilesDto;
    }
    
    private class ImportedAccPaymentDto {
        private String globalNum;
        private Integer paymentId;
        
        public String getGlobalNum() {
            return globalNum;
        }
        public Integer getPaymentId() {
            return paymentId;
        }
        
        public ImportedAccPaymentDto(String globalNum, Integer paymentId) {
            super();
            this.globalNum = globalNum;
            this.paymentId = paymentId;
        }
        
        
    }

    
    /**
     * Method currently returns last problem found for each account.
     */
    @Override
    public Map<String, Map<String, String>> getUndoImportDateToValidate(String importTransactionsFileName) {
        Map<String, Map<String, String>> validationResults = new HashMap<String, Map<String, String>>();
        Map<String, String> accountStatusValidationResults = new HashMap<String, String>();;
        Map<String, String> trxnToUndo = new HashMap<String, String>();;
        List<AccountTrxnEntity> trxnData = new ArrayList<AccountTrxnEntity>(this.importedFilesService.getImportedFileByName(importTransactionsFileName).getImportedTrxn());
        TreeSet<String> accountsWithAdjustedPayments = new TreeSet<String>();
        Integer iterator = 0;
        Boolean error_flag = Boolean.FALSE;
        
        for (AccountTrxnEntity trxn : trxnData) {
            error_flag = Boolean.FALSE;
            if(trxn.getAccount().getAccountState().isLoanCanceled() ||
                    trxn.getAccount().getAccountState().isLoanClosedWrittenOff()) {
                    accountStatusValidationResults.put(trxn.getAccount().getGlobalAccountNum(), ApplicationContextProvider.getBean(MessageLookup.class).lookup("ftlDefinedLabels.undoImport.invalidAccountState")+trxn.getAccount().getAccountState().getName());
                    error_flag = Boolean.TRUE;
            }
            else {
                try {
                    validateForAdjustedPayments(trxn, accountsWithAdjustedPayments);
                    monthClosingServiceFacade.validateTransactionDate(trxn.getAccountPayment().getPaymentDate());
                } catch (BusinessRuleException e) {
                    accountStatusValidationResults.put(trxn.getAccount().getGlobalAccountNum(), ApplicationContextProvider.getBean(MessageLookup.class).lookup(e.getMessageKey()));
                    error_flag = Boolean.TRUE;
                }
            }
            if(error_flag) {
                iterator +=1;
            }
            
        }
        Integer valid_trxn = trxnData.size() - iterator;
        validationResults.put(INVALID_TRXN, accountStatusValidationResults);
        trxnToUndo.put(valid_trxn.toString(), ((Integer)trxnData.size()).toString());
        validationResults.put(VALID_TRXN, trxnToUndo);
        return validationResults;
    }
    
    private void validateForAdjustedPayments(AccountTrxnEntity trxn, TreeSet<String> accountsWithAdjustedPayments) {
        if(accountsWithAdjustedPayments.contains(trxn.getAccount().getGlobalAccountNum())) {
            throw new BusinessRuleException("errors.paymentsWereAdjusted");
        }
        Query query = StaticHibernateUtil.getSessionTL().getNamedQuery("countRelatedTransactions");
        query.setParameter("trxn_id", trxn.getAccountTrxnId().intValue());
        if(((BigInteger)query.uniqueResult()).intValue()>0) {
            accountsWithAdjustedPayments.add(trxn.getAccount().getGlobalAccountNum());
            throw new BusinessRuleException("errors.paymentsWereAdjusted");
        }
    }
}