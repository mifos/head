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

package org.mifos.config;

import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.loan.persistance.LoanPersistence;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.servicefacade.DependencyInjectedServiceLocator;
import org.mifos.config.exceptions.ConfigurationException;
import org.mifos.customers.business.CustomerStatusEntity;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

/**
 * Clients, groups, and accounts have some optional states, which can be hidden
 * and excluded from the state flows. If any of the following states are set to
 * "false", these optional states will not be visible in the UI. Once an
 * optional state is enabled and corresponding loans, savings accounts, clients,
 * etc. have been created, it should not be disabled as records could be in an
 * optional state and unmodifiable from the Mifos user interface.
 * <p>
 * Overriding to "false" in the custom configuration file must be performed
 * before any loans, savings accounts, or clients (depending on the setting)
 * have been created.
 * <p>
 * Values in the database are <em>not used during runtime.</em> Rather, they are
 * a weak attempt at tracking state to try to avoid an inappropriate flag
 * override in the config file.
 */
public class ProcessFlowRules {
    private static final String SAVINGS_PENDING_APPROVAL = "ProcessFlow.SavingsPendingApprovalStateEnabled";
    private static final String LOAN_PENDING_APPROVAL = "ProcessFlow.LoanPendingApprovalStateEnabled";
    private static final String LOAN_DISBURSED_TO_LOAN_OFFICER = "ProcessFlow.LoanDisbursedToLoanOfficerStateEnabled";
    private static final String GROUP_PENDING_APPROVAL = "ProcessFlow.GroupPendingApprovalStateEnabled";
    private static final String CLIENT_PENDING_APPROVAL = "ProcessFlow.ClientPendingApprovalStateEnabled";

    /**
     * Performs startup sanity checks. While not a requirement, it is considered
     * good practice to call this method prior to any other methods in this
     * class. This method is only intended to be used during startup, ie: in a
     * single thread.
     */
    public static void init() throws PersistenceException, ConfigurationException {
        try {
            initClientPendingApprovalState();
            initGroupPendingApprovalState();
            initLoanDisbursedToLoanOfficerState();
            initLoanPendingApprovalState();
            initSavingsPendingApprovalState();

            StaticHibernateUtil.commitTransaction();
        } catch (PersistenceException pe) {
            StaticHibernateUtil.rollbackTransaction();
            throw pe;
        } catch (ConfigurationException ce) {
            StaticHibernateUtil.rollbackTransaction();
            throw ce;
        }
    }

    private static String getBadOverrideMsg(String key, String detailMsg) {
        return "The value for key " + key + " in the file " + ConfigurationManager.CUSTOM_CONFIG_PROPS_FILENAME
                + " must to be set to 1 because it was set to 1"
                + " in the database, hence can't be set to to 0 in the custom"
                + " configuration file as this might invalidate existing data. " + detailMsg + " Also, "
                + ConfigurationManager.DEFAULT_CONFIG_PROPS_FILENAME + " must never be changed--make sure this"
                + " file is untouched.";
    }

    /**
     * The only disallowed combination of database to config file override of a
     * process flow optional state flag is from true to false.
     */
    // "protected" visibility so it can be unit tested; it would otherwise
    // be private
    protected static boolean isValidOverride(boolean fromDb, boolean fromCfg) {
        if (fromDb && !fromCfg) {
            return false;
        }
        return true;
    }

    /**
     * Given the setting of a process flow optional state flag in the database
     * and in the config file, determine if it is necessary to override the
     * value stored in the database. Uses logic similar to ClientRules: override
     * is necessary when flag in db is false and flag in config file is true.
     *
     * @throws ConfigurationException
     *             if an invalid override is specified. Will not be thrown if
     *             arguments pass {@link #isValidOverride(boolean, boolean)}.
     */
    // "protected" visibility so it can be unit tested; it would otherwise
    // be private
    protected static boolean needsOverride(boolean fromDb, boolean fromCfg) throws ConfigurationException {
        if (fromDb && fromCfg) {
            return false;
        } else if (!fromDb && !fromCfg) {
            return false;
        } else if (!fromDb && fromCfg) {
            return true;
        }

        throw new ConfigurationException("unexpected override specified [" + fromDb + "], [" + fromCfg + "]");
    }

    private static void initClientPendingApprovalState() throws ConfigurationException {

        CustomerDao customerDao = DependencyInjectedServiceLocator.locateCustomerDao();
        CustomerStatusEntity cse = customerDao.findClientPendingStatus();

        boolean fromDb = isClientPendingApprovalStateEnabledOnDatabaseConfiguration(cse);
        boolean fromCfg = isClientPendingApprovalStateEnabled();

        if (databaseAndCustomConfigurationAreNotTheSame(fromDb, fromCfg)) {
            int count = customerDao.countOfClients();
            if (count > 0) {
                final String errMsg = getBadOverrideMsg(CLIENT_PENDING_APPROVAL,
                        "Records for clients in the 'pending approval' state" + " may already exist.");
                throw new ConfigurationException(errMsg);
            }

            makeDatabaseConfigurationMatchPropertiesFileConfiguration(customerDao, cse, fromCfg);
        }
    }

    private static boolean isClientPendingApprovalStateEnabledOnDatabaseConfiguration(CustomerStatusEntity cse) {
        return cse.getIsOptional();
    }

    private static void makeDatabaseConfigurationMatchPropertiesFileConfiguration(CustomerDao customerDao,
            CustomerStatusEntity cse, boolean fromCfg) {
        cse.setIsOptional(fromCfg);
        StaticHibernateUtil.startTransaction();
        customerDao.save(cse);
        StaticHibernateUtil.commitTransaction();
    }

    private static boolean databaseAndCustomConfigurationAreNotTheSame(boolean fromDb, boolean fromCfg) {
        return !Boolean.valueOf(fromDb).equals(Boolean.valueOf(fromCfg));
    }

    public static boolean isClientPendingApprovalStateEnabled() {
        ConfigurationManager cm = ConfigurationManager.getInstance();
        return cm.getBoolean(CLIENT_PENDING_APPROVAL);
    }

    private static void initGroupPendingApprovalState() throws ConfigurationException {

        CustomerDao customerDao = DependencyInjectedServiceLocator.locateCustomerDao();
        CustomerStatusEntity cse = customerDao.findGroupPendingStatus();

        boolean fromDb = isGroupPendingApprovalStateEnabledOnDatabaseConfiguration(cse);
        boolean fromCfg = isGroupPendingApprovalStateEnabled();

        if (databaseAndCustomConfigurationAreNotTheSame(fromDb, fromCfg)) {
            int count = customerDao.countOfGroups();
            if (count > 0) {
                final String errMsg = getBadOverrideMsg(GROUP_PENDING_APPROVAL, "Records for groups in the 'pending approval' state"
                        + " may already exist.");
                throw new ConfigurationException(errMsg);
            }

            makeDatabaseConfigurationMatchPropertiesFileConfiguration(customerDao, cse, fromCfg);
        }
    }

    private static boolean isGroupPendingApprovalStateEnabledOnDatabaseConfiguration(CustomerStatusEntity cse) {
        return cse.getIsOptional();
    }

    public static boolean isGroupPendingApprovalStateEnabled() {
        ConfigurationManager cm = ConfigurationManager.getInstance();
        return cm.getBoolean(GROUP_PENDING_APPROVAL);
    }

    private static void initLoanDisbursedToLoanOfficerState() throws PersistenceException, ConfigurationException {
        AccountPersistence ap = new AccountPersistence();
        AccountStateEntity ase = (AccountStateEntity) ap.loadPersistentObject(AccountStateEntity.class,
                AccountState.LOAN_DISBURSED_TO_LOAN_OFFICER.getValue());

        boolean fromDb = isLoanPendingApprovalStateEnabledOnDatabaseConfig(ase);
        boolean fromCfg = isLoanDisbursedToLoanOfficerStateEnabled();
        String errMsg = getBadOverrideMsg(LOAN_DISBURSED_TO_LOAN_OFFICER,
                "Records for loans in the 'disbursed to loan officer' state" + " may already exist.");

        if (!isValidOverride(fromDb, fromCfg)) {
            throw new ConfigurationException(errMsg);
        }

        if (needsOverride(fromDb, fromCfg)) {
            ase.setIsOptional(fromCfg);
            ap.createOrUpdate(ase);
        }
    }

    public static boolean isLoanDisbursedToLoanOfficerStateEnabled() {
        ConfigurationManager cm = ConfigurationManager.getInstance();
        return cm.getBoolean(LOAN_DISBURSED_TO_LOAN_OFFICER);
    }

    private static void initLoanPendingApprovalState() throws ConfigurationException {

        AccountPersistence ap = new AccountPersistence();
        AccountStateEntity ase = (AccountStateEntity) ap.loadPersistentObject(AccountStateEntity.class, AccountState.LOAN_PENDING_APPROVAL.getValue());

        boolean fromDb = isLoanPendingApprovalStateEnabledOnDatabaseConfig(ase);
        boolean fromCfg = isLoanPendingApprovalStateEnabled();

        if (databaseAndCustomConfigurationAreNotTheSame(fromDb, fromCfg)) {
            int count = new LoanPersistence().countOfLoanAccounts();
            if (count > 0) {
                String errMsg = getBadOverrideMsg(LOAN_PENDING_APPROVAL, "Records for loans in the 'pending approval' state"
                        + " may already exist.");
                throw new ConfigurationException(errMsg);
            }

            makeDatabaseConfigurationMatchPropertiesFileConfiguration(ap, ase, fromCfg);
        }
    }

    private static void makeDatabaseConfigurationMatchPropertiesFileConfiguration(AccountPersistence persistence,
            AccountStateEntity entity, boolean fromCfg) {
        entity.setIsOptional(fromCfg);
        try {
            persistence.createOrUpdate(entity);
            StaticHibernateUtil.commitTransaction();
        } catch (PersistenceException e) {
            StaticHibernateUtil.rollbackTransaction();
        } finally {
            StaticHibernateUtil.closeSession();
        }

    }

    private static boolean isSavingPendingApprovalStateEnabledOnDatabaseConfig(AccountStateEntity ase) {
        return ase.getIsOptional();
    }

    private static boolean isLoanPendingApprovalStateEnabledOnDatabaseConfig(AccountStateEntity ase) {
        return ase.getIsOptional();
    }

    public static boolean isLoanPendingApprovalStateEnabled() {
        ConfigurationManager cm = ConfigurationManager.getInstance();
        return cm.getBoolean(LOAN_PENDING_APPROVAL);
    }

    private static void initSavingsPendingApprovalState() throws ConfigurationException {

        AccountPersistence ap = new AccountPersistence();
        AccountStateEntity ase = (AccountStateEntity) ap.loadPersistentObject(AccountStateEntity.class, AccountState.SAVINGS_PENDING_APPROVAL.getValue());

        boolean fromDb = isSavingPendingApprovalStateEnabledOnDatabaseConfig(ase);
        boolean fromCfg = isSavingsPendingApprovalStateEnabled();

        if (databaseAndCustomConfigurationAreNotTheSame(fromDb, fromCfg)) {
            int count = new LoanPersistence().countOfSavingsAccounts();
            if (count > 0) {
                String errMsg = getBadOverrideMsg(SAVINGS_PENDING_APPROVAL,
                        "Records for savings accounts in the 'pending approval' state" + " may already exist.");
                throw new ConfigurationException(errMsg);
            }

            makeDatabaseConfigurationMatchPropertiesFileConfiguration(ap, ase, fromCfg);
        }
    }

    public static boolean isSavingsPendingApprovalStateEnabled() {
        ConfigurationManager cm = ConfigurationManager.getInstance();
        return cm.getBoolean(SAVINGS_PENDING_APPROVAL);
    }
}
