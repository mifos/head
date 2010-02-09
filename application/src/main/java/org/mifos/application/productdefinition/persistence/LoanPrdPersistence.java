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

package org.mifos.application.productdefinition.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.NamedQueryConstants;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.application.customer.business.CustomerLevelEntity;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.LoanOfferingFeesEntity;
import org.mifos.application.productdefinition.business.LoanOfferingFundEntity;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;

public class LoanPrdPersistence extends Persistence {

    private static ThreadLocal<Map> reportsCacheTL = new ThreadLocal<Map>();

    public Short retrieveLatenessForPrd() throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("productTypeId", AccountTypes.LOAN_ACCOUNT.getValue());

        /*
         * Is the intention here to clear this cache every time we close the
         * session? How do we invalidate/update the cache if the database is
         * updated?
         */
        if (isCacheEnabledForReports()) {
            Map cache = reportsCacheTL.get();
            Short cachedValue = (Short) cache.get(AccountTypes.LOAN_ACCOUNT.getValue());
            if (cachedValue != null) {
                return cachedValue;
            }
        }

        List<Short> queryResult = executeNamedQuery(NamedQueryConstants.GET_LATENESS_FOR_LOANS, queryParameters);

        if (null != queryResult && null != queryResult.get(0)) {
            if (isCacheEnabledForReports()) {
                Map cache = reportsCacheTL.get();
                cache.put(AccountTypes.LOAN_ACCOUNT.getValue(), queryResult.get(0));
            }
            return queryResult.get(0);
        }
        return Short.valueOf("10");
    }

    public static void enableThreadCacheForReports() {
        reportsCacheTL.set(new HashMap());
    }

    public static void disableThreadCacheForReports() {
        reportsCacheTL.set(null);
    }

    public static boolean isCacheEnabledForReports() {
        return reportsCacheTL.get() != null;
    }

    public LoanOfferingBO getLoanOffering(final Short prdofferingId) throws PersistenceException {
        return (LoanOfferingBO) getPersistentObject(LoanOfferingBO.class, prdofferingId);
    }

    public LoanOfferingBO getLoanOffering(final Short loanOfferingId, final Short localeId) throws PersistenceException {
        LoanOfferingBO loanOffering = (LoanOfferingBO) getPersistentObject(LoanOfferingBO.class, loanOfferingId);
        initialize(loanOffering);
        loanOffering.getPrdCategory().getProductCategoryName();
        loanOffering.getPrdApplicableMaster().setLocaleId(localeId);
        loanOffering.getPrdStatus().getPrdState().setLocaleId(localeId);
        loanOffering.getInterestTypes().setLocaleId(localeId);
        loanOffering.getGracePeriodType().setLocaleId(localeId);
        loanOffering.getPrincipalGLcode().getGlcode();
        loanOffering.getInterestGLcode().getGlcode();
        if (loanOffering.getLoanOfferingFunds() != null && loanOffering.getLoanOfferingFunds().size() > 0)
            for (LoanOfferingFundEntity loanOfferingFund : loanOffering.getLoanOfferingFunds())
                loanOfferingFund.getFund().getFundName();
        if (loanOffering.getLoanOfferingFees() != null && loanOffering.getLoanOfferingFees().size() > 0)
            for (LoanOfferingFeesEntity prdOfferingFees : loanOffering.getLoanOfferingFees())
                prdOfferingFees.getFees().getFeeName();

        return loanOffering;
    }

    public List<LoanOfferingBO> getAllLoanOfferings(final Short localeId) throws PersistenceException {
        List<LoanOfferingBO> loanOfferings = executeNamedQuery(NamedQueryConstants.PRODUCT_ALL_LOAN_PRODUCTS, null);
        if (null != loanOfferings && loanOfferings.size() > 0) {
            for (LoanOfferingBO loanOffering : loanOfferings) {
                loanOffering.getPrdStatus().getPrdState().setLocaleId(localeId);
            }
        }
        return loanOfferings;
    }

    public List<LoanOfferingBO> getAllActiveLoanOfferings(final Short localeId) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(AccountConstants.PRDSTATUS, PrdStatus.LOAN_ACTIVE.getValue());

        List<LoanOfferingBO> loanOfferings = executeNamedQuery(NamedQueryConstants.PRODUCT_ALL_ACTIVE_LOAN_PRODUCTS,
                queryParameters);
        if (null != loanOfferings && loanOfferings.size() > 0) {
            for (LoanOfferingBO loanOffering : loanOfferings) {
                loanOffering.getPrdStatus().getPrdState().setLocaleId(localeId);
            }
        }
        return loanOfferings;
    }

    public List<LoanOfferingBO> getLoanOfferingsNotMixed(final Short localeId) throws PersistenceException {

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(AccountConstants.PRDSTATUS, PrdStatus.LOAN_ACTIVE.getValue());

        List<LoanOfferingBO> loanOfferings = executeNamedQuery(NamedQueryConstants.PRODUCT_NOTMIXED_LOAN_PRODUCTS,
                queryParameters);
        if (null != loanOfferings && loanOfferings.size() > 0) {
            for (LoanOfferingBO loanOffering : loanOfferings) {
                loanOffering.getPrdStatus().getPrdState().setLocaleId(localeId);
            }
        }
        return loanOfferings;
    }

    /**
     * @deprecated use
     *             {@link LoanProductDao#findActiveLoanProductsApplicableToCustomerLevel(CustomerLevelEntity)}
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public List<LoanOfferingBO> getApplicablePrdOfferings(final CustomerLevelEntity customerLevel)
            throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(AccountConstants.PRDSTATUS, PrdStatus.LOAN_ACTIVE.getValue());
        queryParameters.put(AccountConstants.PRODUCT_APPLICABLE_TO, customerLevel.getProductApplicableType());
        return executeNamedQuery(NamedQueryConstants.APPLICABLE_LOAN_PRODUCTS, queryParameters);
    }
}
