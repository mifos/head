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

package org.mifos.application.collectionsheet.business;

import java.sql.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.application.collectionsheet.persistence.CollectionSheetPersistence;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetConstants;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.servicefacade.CollectionSheetService;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;

/**
 * @deprecated collection sheets not used as a domain concept.
 * 
 * @see CollectionSheetService#retrieveCollectionSheet(Integer, java.util.Date)
 */
@Deprecated
public class CollectionSheetBO extends BusinessObject {

    private static final int CUSTOMER_ID_MAP_INITIAL_CAPACITY = 10000;

    public CollectionSheetBO() {
        super();
    }

    private Integer collSheetID;

    private Date collSheetDate;

    private Short statusFlag;

    private Date runDate;

    private Set<CollSheetCustBO> collectionSheetCustomers;

    private CollectionSheetPersistence collectionSheetPersistence;

    public CollectionSheetPersistence getCollectionSheetPersistence() {

        if (collectionSheetPersistence == null) {
            collectionSheetPersistence = new CollectionSheetPersistence();
        }
        return collectionSheetPersistence;
    }

    public void setCollectionSheetPersistence(final CollectionSheetPersistence collectionSheetPersistence) {
        this.collectionSheetPersistence = collectionSheetPersistence;
    }

    private final Map<Integer, CollSheetCustBO> collectionSheetCustomerLookup = new HashMap<Integer, CollSheetCustBO>(
            CUSTOMER_ID_MAP_INITIAL_CAPACITY);

    public void populateTestInstance(final Date collSheetDate, final Date runDate, final Set<CollSheetCustBO> collectionSheetCustomers,
            final Short statusFlag) {
        this.collSheetDate = collSheetDate;
        this.runDate = runDate;
        this.collectionSheetCustomers = collectionSheetCustomers;
        this.statusFlag = statusFlag;
        for (CollSheetCustBO customer : collectionSheetCustomers) {
            customer.setCollectionSheet(this);
        }
    }

    public Date getCollSheetDate() {
        return collSheetDate;
    }

    public void setCollSheetDate(final Date collSheetDate) {
        this.collSheetDate = collSheetDate;
    }

    public Integer getCollSheetID() {
        return collSheetID;
    }

    public void setCollSheetID(final Integer collSheetID) {
        this.collSheetID = collSheetID;
    }

    public Date getRunDate() {
        return runDate;
    }

    public void setRunDate(final Date runDate) {
        this.runDate = runDate;
    }

    public Short getStatusFlag() {
        return statusFlag;
    }

    public void setStatusFlag(final Short statusFlag) {
        this.statusFlag = statusFlag;
    }

    public Set<CollSheetCustBO> getCollectionSheetCustomers() {
        return collectionSheetCustomers;
    }

    public void setCollectionSheetCustomers(final Set<CollSheetCustBO> collectionSheetCustomerSet) {
        this.collectionSheetCustomers = collectionSheetCustomerSet;
    }

    /**
     * It adds the passed collectionSheetCustomer object to the set of
     * collectionsheet customers. If the set is null it creates a new hash set
     * and adds the collectionsheetCustomer object to it setting the
     * bidirectional relation ship.
     */
    public void addCollectionSheetCustomer(final CollSheetCustBO collectionSheetCustomer) {
        collectionSheetCustomer.setCollectionSheet(this);
        if (null == collectionSheetCustomers) {
            collectionSheetCustomers = new HashSet<CollSheetCustBO>();
        }
        collectionSheetCustomers.add(collectionSheetCustomer);
        collectionSheetCustomerLookup.put(collectionSheetCustomer.getCustId(), collectionSheetCustomer);
    }

    /**
     * @return - It returns the collectionSheetCustomer object reference from
     *         the set if it finds a collection sheet customer in the
     *         collectionSheetCustomers with the same customerId else returns
     *         null.
     */
    public CollSheetCustBO getCollectionSheetCustomerForCustomerId(final Integer customerId) {
        return collectionSheetCustomerLookup.get(customerId);
    }

    /**
     * This method takes a list of loan accounts which are due for disbursal and
     * adds them to the collection sheet loan details set associated with the
     * corresponding customer id.It first checks if the customer record already
     * exists in the collectionSheetCustomer set,if it does not exist it adds
     * the customer record first and then adds the collectionSheetloandetails
     * object to the customer record.
     */
    public void addLoanDetailsForDisbursal(final List<LoanBO> loanWithDisbursalDate) {
        if (null != loanWithDisbursalDate && loanWithDisbursalDate.size() > 0) {
            for (LoanBO loan : loanWithDisbursalDate) {
                CollSheetLnDetailsEntity collSheetLnDetail = null;
                CollSheetCustBO collectionSheetCustomer = getCollectionSheetCustomerForCustomerId(loan.getCustomer()
                        .getCustomerId());
                if (null == collectionSheetCustomer) {
                    collectionSheetCustomer = new CollSheetCustBO(loan.getCurrency());
                    collectionSheetCustomer.populateCustomerDetails(loan.getCustomer());
                    MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug(
                            "after addng customer detals");
                    addCollectionSheetCustomer(collectionSheetCustomer);
                }
                collSheetLnDetail = new CollSheetLnDetailsEntity();
                collSheetLnDetail.addDisbursalDetails(loan);
                MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug(
                        "after addng disbursal details of loan detals");
                collectionSheetCustomer.addCollectionSheetLoanDetail(collSheetLnDetail);
                MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug(
                        "after addng loan details to customer record");
            }
        }
    }

    /**
     * This method updates the collective totals for the collectionsheetCustomer
     * records in the set. It iterates over the set and updates the totals for
     * each record which the sum of self and the group or client under it.Hence
     * if the record is that of center it adds the totals of self and groups and
     * clients under that center or if it is that of a group it sums the totals
     * of self and clients under it.
     */
    public void updateCollectiveTotals() {
        for (CollSheetCustBO collSheetCustomer : collectionSheetCustomers) {
            if (collSheetCustomer.getCustLevel() == CustomerLevel.CLIENT.getValue()) {
                int parentCustomerId = collSheetCustomer.getParentCustomerId();
                CollSheetCustBO collSheetParentCust = getCollectionSheetCustomerForCustomerId(parentCustomerId);
                // it would be null in case a client belongs directly to a
                // branch and does not belong to a group.
                if (null != collSheetParentCust) {
                    collSheetParentCust.addCollectiveTotalsForChild(collSheetCustomer);
                }
            }
        }
        for (CollSheetCustBO collSheetCustomer : collectionSheetCustomers) {
            if (collSheetCustomer.getCustLevel() == CustomerLevel.GROUP.getValue()) {
                int parentCustomerId = collSheetCustomer.getParentCustomerId();
                CollSheetCustBO collSheetParentCust = getCollectionSheetCustomerForCustomerId(parentCustomerId);
                // it would be null in case center hierarchy does not exist.
                if (null != collSheetParentCust) {
                    collSheetParentCust.addCollectiveTotalsForChild(collSheetCustomer);
                }
            }
        }
    }

    /**
     * This creates a collection sheet object in the database, before persisting
     * the object it implicitly sets the state to started.
     */
    public void create() throws PersistenceException {
        this.statusFlag = CollectionSheetConstants.COLLECTION_SHEET_GENERATION_STARTED;
        getCollectionSheetPersistence().createOrUpdate(this);
    }

    /**
     * Populates customer, customer account, loan and savings details
     * 
     * This is achieved by retrieving customer objects from the list
     * accountActionDates passed as parameter, also if the account associated
     * with that item of accountActionDates is a CustomerAccount it populates
     * the relevant details from that object like fees , misc penalty etc.
     * 
     * @throws ApplicationException
     * @throws SystemException
     */
    void populateCustomerLoanAndSavingsDetails(final List<AccountActionDateEntity> accountActionDates)
            throws SystemException, ApplicationException {
        long cumulative = 0, count = 0;
        for (AccountActionDateEntity accountActionDate : accountActionDates) {
            // it might be present in the set if that customer was already added
            // because it has got more than one loan/savings account.
            MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug(
                    "checkin if the customer already exists in the list");
            CollSheetCustBO collectionSheetCustomer = getCollectionSheetCustomerForCustomerId(accountActionDate
                    .getCustomer().getCustomerId());
            if (null == collectionSheetCustomer) {
                collectionSheetCustomer = new CollSheetCustBO(accountActionDate.getAccount().getCurrency());
                CustomerBO customer = accountActionDate.getCustomer();

                // add customer details to the fields in collectionSheetCustomer
                // object.
                collectionSheetCustomer.populateCustomerDetails(customer);
                MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug("after adding customer details");
            }
            // here we need not check if it was already in the list because
            // there can be only one customer account with a customer so it
            // would definitely would not have been added to the list.
            MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug(
                    "account type id is " + accountActionDate.getAccount().getType());
            addCollectionSheetCustomer(collectionSheetCustomer);
            if (accountActionDate.getAccount().getType() == AccountTypes.CUSTOMER_ACCOUNT) {
                collectionSheetCustomer.populateAccountDetails(accountActionDate);
                MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug("after adding account details");
            } else if (accountActionDate.getAccount().getType() == AccountTypes.LOAN_ACCOUNT) {
                MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug(
                        "Loan accoutns size: " + accountActionDate);
                CollSheetLnDetailsEntity collectionSheetLoanDetail = new CollSheetLnDetailsEntity();
                collectionSheetLoanDetail.addAccountDetails(accountActionDate);
                collectionSheetCustomer.addCollectionSheetLoanDetail(collectionSheetLoanDetail);
            } else if (accountActionDate.getAccount().getType() == AccountTypes.SAVINGS_ACCOUNT) {
                CollSheetSavingsDetailsEntity collSheetSavingsDetail = new CollSheetSavingsDetailsEntity();
                collSheetSavingsDetail.addAccountDetails(accountActionDate);
                collectionSheetCustomer.addCollectionSheetSavingsDetail(collSheetSavingsDetail);
            }
        }
    }

    /**
     * This sets the collection sheet record with the status id passed and then
     * updates the record in the database.
     */
    public void update(final Short statusId) throws PersistenceException {
        this.statusFlag = statusId;
        getCollectionSheetPersistence().createOrUpdate(this);
    }

    public void populateAccountActionDates(final List<AccountActionDateEntity> accountActionDates) throws SystemException,
            ApplicationException {
        populateCustomerLoanAndSavingsDetails(accountActionDates);
        MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug("after populate customers");
    }
}
