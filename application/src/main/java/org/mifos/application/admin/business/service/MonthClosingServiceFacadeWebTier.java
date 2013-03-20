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

package org.mifos.application.admin.business.service;

import org.mifos.application.admin.servicefacade.MonthClosingServiceFacade;
import org.mifos.config.business.ConfigurationKeyValue;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.service.BusinessRuleException;

import java.util.Date;

public class MonthClosingServiceFacadeWebTier implements MonthClosingServiceFacade {

    private final static String MONTH_CLOSING_DAY_CONFIG_KEY = "MonthClosingDay";
    private ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();

    @Override
    public void setMonthClosingDate(Date day) {
        try {
            StaticHibernateUtil.startTransaction();
            if (day != null) {
                configurationPersistence.createOrUpdateConfigurationKeyValueString(MONTH_CLOSING_DAY_CONFIG_KEY,
                        DateUtils.toDatabaseFormat(day));
            }
            else {
                configurationPersistence.deleteConfigurationKeyValue(MONTH_CLOSING_DAY_CONFIG_KEY);
            }
            StaticHibernateUtil.commitTransaction();
        }
        catch (PersistenceException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException("Unable to set Month Closing Date", e);
        }
        finally {
            StaticHibernateUtil.closeSession();
        }
    }

    @Override
    public Date getMonthClosingDate() {
        ConfigurationKeyValue configurationKeyValue = configurationPersistence.getConfigurationKeyValue(MONTH_CLOSING_DAY_CONFIG_KEY);
        Date monthClosingDay = null;

        if (configurationKeyValue != null) {
            monthClosingDay = DateUtils.getDateAsRetrievedFromDb(configurationKeyValue.getValue());
        }

        return monthClosingDay;
    }

    @Override
    public void validateTransactionDate(Date trxnDate) {
        if (getMonthClosingDate() != null &&
                getMonthClosingDate().compareTo(DateUtils.getDateWithoutTimeStamp(trxnDate)) >= 0) {

            throw new BusinessRuleException("errors.invalidTxndateMonthAlreadyClosed");
        }
    }
}
