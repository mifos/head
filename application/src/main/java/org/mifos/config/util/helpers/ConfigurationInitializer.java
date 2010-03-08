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

package org.mifos.config.util.helpers;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.accounts.productdefinition.persistence.LoanPrdPersistence;
import org.mifos.accounts.productdefinition.persistence.SavingsPrdPersistence;
import org.mifos.config.AccountingRules;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.config.business.SystemConfiguration;
import org.mifos.config.cache.CacheRepository;
import org.mifos.config.cache.Key;
import org.mifos.config.cache.OfficeCache;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ConstantsNotLoadedException;
import org.mifos.framework.exceptions.StartUpException;
import org.mifos.framework.exceptions.SystemException;

/**
 * This class is a remnant of per-office configuration, which <a
 * href="http://article.gmane.org/gmane.comp.finance.mifos.devel/3498">is
 * deprecated and may be removed</a> (-Adam 22-JAN-2008).
 */
public class ConfigurationInitializer {
    private OfficeBO headOffice;

    private OfficeBO getHeadOffice() throws ApplicationException {
        if (headOffice == null) {
            headOffice = new OfficePersistence().getHeadOffice();
        }
        return headOffice;
    }

    protected SystemConfiguration createSystemConfiguration() throws SystemException {

        MifosCurrency defaultCurrency = null;
        try {
            defaultCurrency = AccountingRules.getMifosCurrency(new ConfigurationPersistence());
        } catch (RuntimeException re) {
            throw new SystemException("cannot fetch default currency", re);
        }

        // TODO: pick timezone offset from database
        int timeZone = 19800000;

        return new SystemConfiguration(defaultCurrency, timeZone);
    }

    protected OfficeCache createOfficeCache() throws SystemException, ApplicationException {
        Map<Key, Object> officeConfigMap = new HashMap<Key, Object>();

        setFiscalStartOfWeek(officeConfigMap);
        setWeekOffList(officeConfigMap);
        setLateNessAndDormancyDaysForAccount(officeConfigMap);

        return new OfficeCache(officeConfigMap);
    }

    private void setFiscalStartOfWeek(Map<Key, Object> officeConfigMap) throws SystemException, ApplicationException {

        Short id = new FiscalCalendarRules().getStartOfWeek();
        officeConfigMap.put(new Key(getHeadOffice().getOfficeId(), ConfigConstants.FISCAL_START_OF_WEEK), id);
    }

    private void setWeekOffList(Map<Key, Object> officeConfigMap) throws SystemException, ApplicationException {

        // get weekday off (not working day)
        List<Short> weekOffList = new FiscalCalendarRules().getWeekDayOffList();
        if (weekOffList != null) {
            officeConfigMap.put(new Key(getHeadOffice().getOfficeId(), ConfigConstants.WEEK_OFF_LIST), weekOffList);
        }
    }

    private void setLateNessAndDormancyDaysForAccount(Map<Key, Object> officeConfigMap) throws SystemException,
            ApplicationException {
        Short latenessDays = new LoanPrdPersistence().retrieveLatenessForPrd();
        Short dormancyDays = new SavingsPrdPersistence().retrieveDormancyDays();
        officeConfigMap.put(new Key(getHeadOffice().getOfficeId(), ConfigConstants.LATENESS_DAYS), latenessDays);
        officeConfigMap.put(new Key(getHeadOffice().getOfficeId(), ConfigConstants.DORMANCY_DAYS), dormancyDays);
    }

    public void initialize() {
        try {
            CacheRepository cacheRepository = CacheRepository.getInstance();
            cacheRepository.setSystemConfiguration(createSystemConfiguration());
            cacheRepository.setOfficeCache(createOfficeCache());
        } catch (SystemException se) {
            throw new StartUpException(se);
        } catch (ApplicationException e) {
            throw new StartUpException(e);
        }
    }

    static void checkModifiers(Field field) throws ConstantsNotLoadedException {
        if (!Modifier.isFinal(field.getModifiers())) {
            throw new ConstantsNotLoadedException("field: " + field.getName() + " is not declared as final");
        }
        if (!Modifier.isStatic(field.getModifiers())) {
            throw new ConstantsNotLoadedException("field: " + field.getName() + " is not declared as static");
        }
        if (!Modifier.isPublic(field.getModifiers())) {
            throw new ConstantsNotLoadedException("field: " + field.getName() + " is not declared as public");
        }
    }
}
