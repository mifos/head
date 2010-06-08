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

package org.mifos.application.holiday.business.service;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.holiday.persistence.HolidayDetails;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ValidationException;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;

public class HolidayServiceImpl implements HolidayService {

    private final OfficeDao officeDao;
    private final HolidayDao holidayDao;
    private final HibernateTransactionHelper hibernateTransactionHelper;

    public HolidayServiceImpl(OfficeDao officeDao, HolidayDao holidayDao, HibernateTransactionHelper hibernateTransactionHelper) {
        this.officeDao = officeDao;
        this.holidayDao = holidayDao;
        this.hibernateTransactionHelper = hibernateTransactionHelper;
    }

    @Override
    public void create(HolidayDetails holidayDetails, List<Short> officeIds) throws ApplicationException {
        HolidayBO holiday = HolidayBO.fromDto(holidayDetails);
        holiday.validate();

        List<OfficeBO> offices = new ArrayList<OfficeBO>();
        for (Short officeId : officeIds) {
            OfficeBO office = officeDao.findOfficeById(officeId);
            offices.add(office);
            if (office.hasChildWithAnyOf(officeIds)) {
                throw new ValidationException("Holidays can only be associated with one level of office in an office hierarchy.");
            }
        }

        try {
            hibernateTransactionHelper.startTransaction();

            this.holidayDao.save(holiday);
            for (OfficeBO office : offices) {
                office.addHoliday(holiday);
            }

            hibernateTransactionHelper.commitTransaction();
        } catch (Exception e) {
            hibernateTransactionHelper.rollbackTransaction();
        } finally {
            hibernateTransactionHelper.closeSession();
        }
    }
}