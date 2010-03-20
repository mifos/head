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

package org.mifos.accounts.financial.util.helpers;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.accounts.financial.business.COABO;
import org.mifos.accounts.financial.business.COAHierarchyEntity;
import org.mifos.accounts.financial.business.FinancialActionBO;
import org.mifos.accounts.financial.exceptions.FinancialException;
import org.mifos.accounts.financial.exceptions.FinancialExceptionConstants;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.application.NamedQueryConstants;
import org.mifos.config.ChartOfAccountsConfig;
import org.mifos.config.GLAccount;
import org.mifos.config.exceptions.ConfigurationException;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

public class FinancialInitializer {
    private static MifosLogger logger;

    public static void initialize() throws FinancialException {
        logger = MifosLogManager.getLogger(LoggerConstants.CONFIGURATION_LOGGER);
        try {
            StaticHibernateUtil.getSessionTL();
            StaticHibernateUtil.startTransaction();
            initalizeFinancialAction();
            loadCOA();
            StaticHibernateUtil.commitTransaction();

            // necessary or cacheCOA() doesn't work correctly. Is that because
            // the commitTransaction() isn't flushing the session?
            StaticHibernateUtil.closeSession();

            cacheCOA();
        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new FinancialException(FinancialExceptionConstants.ACTIONNOTFOUND, e);
        }
    }

    /**
     * Reads chart of accounts from a configuration file and inserts into the
     * database. Deleting accounts is not currently supported, but if the user
     * tries to do this (by leaving it out of the custom chart of accounts
     * config file), there is currently no error reported.
     * <p>
     * QUESION: what if custom config was missing entries from default config,
     * but then is moved _out_ of the app server classpath?
     * <p>
     * ANSWER: once ChartOfAccountsConfig.isLoaded() returns true, <em>only
     * the custom CoA config file will be considered</em>. Using the custom
     * config will be the only way to add new accounts after the initial CoA
     * data is loaded in the database.
     */
    public static void loadCOA() throws FinancialException {
        Session session = StaticHibernateUtil.getSessionTL();

        if (!ChartOfAccountsConfig.canLoadCoa(session)) {
            logger.info("Chart of accounts data will not be modified since "
                    + "the custom chart of accounts configuration file was " + "not found on the classpath.");
            return;
        }

        final String coaLocation = ChartOfAccountsConfig.getCoaUri(session);
        logger.info("going to load or modify chart of accounts " + "configuration from " + coaLocation);

        ChartOfAccountsConfig coa;
        try {
            coa = ChartOfAccountsConfig.load(coaLocation);
        } catch (ConfigurationException e) {
            throw new FinancialException(e);
        }

        AccountPersistence ap = new AccountPersistence();
        for (GLAccount glAccount : coa.getGLAccounts()) {
            Short accountId = ap.getAccountIdFromGlCode(glAccount.glCode);
            if (null == accountId) {
                logger.info("Adding new general ledger account: " + glAccount);
                ap.addGeneralLedgerAccount(glAccount.name, glAccount.glCode, glAccount.parentGlCode,
                        glAccount.categoryType);
            } else {
                COABO account = (COABO) session.load(COABO.class, accountId);

                if (account.getCategoryType() != glAccount.categoryType) {
                    throw new FinancialException("category type change not supported");
                }

                if (!accountHierarchyMatch(account, glAccount)) {
                    throw new FinancialException("chart of accounts hierarchy change not supported");
                }

                if (!account.getAccountName().equals(glAccount.name)) {
                    logger.info("updating general ledger account name. code=" + account.getGlCode() + ". old name="
                            + account.getAccountName() + ", new name=" + glAccount.name);
                    ap.updateAccountName(account, glAccount.name);
                }
            }
        }
    }

    /**
     * Compares hierarchy (parent GL codes) of a general ledger account in the
     * database to an unpersisted general ledger account.
     */
    private static boolean accountHierarchyMatch(COABO account1, GLAccount account2) {

        COAHierarchyEntity account1hierarchy = account1.getCoaHierarchy().getParentAccount();
        if (null == account1hierarchy) {
            if (null == account2.parentGlCode) {
                return true;
            } else {
                logger.error("persisted account has no parent, but new account does");
                return false;
            }
        }

        COABO account1parent = account1hierarchy.getCoa();
        String account1parentGlCode = account1parent.getGlCode();
        if (!account1parentGlCode.equals(account2.parentGlCode)) {
            logger.error("persistent account parent gl code was " + account1parentGlCode
                    + ", but new account parent gl code was " + account2.parentGlCode);
            return false;
        }

        return true;
    }

    /**
     * Reads chart of accounts from the database and caches in memory.
     */
    public static void cacheCOA() throws FinancialException {
        if (ChartOfAccountsCache.isInitialized()) {
            return;
        }
        Session session = StaticHibernateUtil.getSessionTL();
        Query query = session.getNamedQuery(NamedQueryConstants.GET_ALL_COA);
        List<COABO> coaBoList = query.list();
        for (COABO coabo : coaBoList) {
            ChartOfAccountsCache.add(hibernateInitalize(coabo));
        }
    }

    public static void initalizeFinancialAction() throws FinancialException {
        Session session = StaticHibernateUtil.getSessionTL();
        try {
            Query queryFinancialAction = session.getNamedQuery(FinancialQueryConstants.GET_ALL_FINANCIAL_ACTION);
            List<FinancialActionBO> listFinancialAction = queryFinancialAction.list();
            for (FinancialActionBO fabo : listFinancialAction) {
                FinancialActionCache.addToCache(fabo);
            }
        } catch (Exception e) {
            throw new FinancialException(FinancialExceptionConstants.FINANCIALACTION_INITFAILED, e);
        }

    }

    private static COABO hibernateInitalize(COABO coa) {

        Hibernate.initialize(coa);
        Hibernate.initialize(coa.getCOAHead());
        Hibernate.initialize(coa.getAssociatedGlcode());
        Hibernate.initialize(coa.getSubCategory());

        return coa;
    }

}
