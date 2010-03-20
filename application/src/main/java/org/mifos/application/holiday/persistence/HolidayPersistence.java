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

package org.mifos.application.holiday.persistence;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.business.RepaymentRuleEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.customers.business.CustomerScheduleEntity;
import org.mifos.framework.exceptions.PersistenceException;

/**
 * @deprecated - please use {@link HolidayDao} instead.
 *
 * FIXME - move holiday dao functionality from here to {@link HolidayDaoHibernate}.
 */
@Deprecated
public class HolidayPersistence extends MasterPersistence {

    public HolidayPersistence() {
    }

    public HolidayBO getHoliday(final Short holidayId) throws PersistenceException {
        return (HolidayBO) getPersistentObject(HolidayBO.class, holidayId);
    }

    /*
     * we need a way to make this worx because our PK is the HolidayPK public
     * HolidayBO getHoliday(HolidayPK holidayPK) throws PersistenceException {
     * return (HolidayBO) getPersistentObject(HolidayBO.class, holidayPK); }
     *
     * Force a locale that works with pattern parsing.
     */

    @Deprecated
    public List<HolidayBO> getHolidays(final int year) throws PersistenceException {
        SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd",new Locale("en","GB"));
        isoDateFormat.setLenient(false);
        Map<String, Object> parameters = new HashMap<String, Object>();
        try {
            parameters.put("START_OF_YEAR", isoDateFormat.parse(year + "-01-01"));
            parameters.put("END_OF_YEAR", isoDateFormat.parse(year + "-12-31"));
        } catch (ParseException e) {
            throw new PersistenceException(e);
        }
        return executeNamedQuery(NamedQueryConstants.GET_HOLIDAYS, parameters);
    }

    public List<RepaymentRuleEntity> getRepaymentRuleTypes() throws PersistenceException {

        Map<String, Object> parameters = new HashMap<String, Object>();
        return executeNamedQuery(NamedQueryConstants.GET_REPAYMENT_RULE_TYPES, parameters);
    }

    public RepaymentRuleEntity getRepaymentRule(final short repaymentRuleId) throws PersistenceException {

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("repaymentRuleId", repaymentRuleId);
        return (RepaymentRuleEntity) execUniqueResultNamedQuery(NamedQueryConstants.GET_REPAYMENT_RULE, parameters);
    }

    public List<LoanScheduleEntity> getAllLoanSchedules(final HolidayBO holiday) throws PersistenceException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("FROM_DATE", holiday.getHolidayFromDate());
        parameters.put("THRU_DATE", holiday.getHolidayThruDate());

        return executeNamedQuery(NamedQueryConstants.ALL_LOAN_SCHEDULE, parameters);
    }

    public List<HolidayBO> getUnAppliedHolidays() throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("FLAG", YesNoFlag.NO.getValue());
        List<HolidayBO> queryResult = executeNamedQuery(NamedQueryConstants.GET_HOLIDAYS_BY_FLAG, queryParameters);
        return queryResult;
    }

    public List<SavingsScheduleEntity> getAllSavingSchedules(final HolidayBO holiday) throws PersistenceException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("FROM_DATE", holiday.getHolidayFromDate());
        parameters.put("THRU_DATE", holiday.getHolidayThruDate());

        return executeNamedQuery(NamedQueryConstants.ALL_SAVING_SCHEDULE, parameters);
    }

    public List<CustomerScheduleEntity> getAllCustomerSchedules(final HolidayBO holiday) throws PersistenceException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("FROM_DATE", holiday.getHolidayFromDate());
        parameters.put("THRU_DATE", holiday.getHolidayThruDate());

        return executeNamedQuery(NamedQueryConstants.ALL_CUSTOMER_SCHEDULE, parameters);
    }

    public int isValidHolidayState(final Short levelId, final Short stateId, final boolean isCustomer) throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("levelId", levelId);
        queryParameters.put("stateId", stateId);
        Integer count;
        if (isCustomer) {
            count = (Integer) execUniqueResultNamedQuery(NamedQueryConstants.CUSTOMER_VALIDATESTATE, queryParameters);
        } else {
            count = (Integer) execUniqueResultNamedQuery(NamedQueryConstants.PRODUCT_VALIDATESTATE, queryParameters);
        }
        return count;
    }

    public List<HolidayBO> getDistinctYears() throws PersistenceException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        return executeNamedQuery("holiday.getDistinctYears", parameters);
    }

}
